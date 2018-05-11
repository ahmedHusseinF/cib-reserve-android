package com.cibeg.cibreserve;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePassword extends AppCompatActivity {
    private static final String TAG = "ChangePassword";

    @BindView(R.id.new_password) EditText _newPassword;
    @BindView(R.id.new_reEnterPassword) EditText _newreEnterPassword;
    @BindView(R.id.old_password) EditText _oldPassword;
    @BindView(R.id.savePassword) ImageButton  _savePasswordButton;
    @BindView(R.id.backToSettings) ImageButton _backToSettingButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);


        try {
            getSupportActionBar().hide();
        }catch (Exception e){
            Log.d("actionbar", e.getMessage());
        }


        _newPassword = findViewById(R.id.new_password);
        _newreEnterPassword=findViewById(R.id.new_reEnterPassword);
        _oldPassword=findViewById(R.id.old_password);
        _savePasswordButton = findViewById(R.id.savePassword);
        _backToSettingButton = findViewById(R.id.backToSettings);


        _savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               boolean s=changePassword();


            }
        });


        _backToSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePassword.this, SettingsActivity.class));

            }
        });


    }

    public boolean changePassword() {
        Log.d(TAG, "Change Password");

        if (!validate()) {
            onChangeInfoFailed();
            return false;
        }


        String oldpass = _oldPassword.getText().toString();
        final String newpass = _newPassword.getText().toString();
        final FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email,oldpass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    user.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getBaseContext(), "Something went wrong. Please try again later", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getBaseContext(), "Password Successfully Modified", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ChangePassword.this, SettingsActivity.class));
                            }
                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(),  "Wrong password", Toast.LENGTH_LONG).show();
                }
            }
        });

     return true;

    }



    public void onChangeInfoFailed() {
        Toast.makeText(getBaseContext(), "Wrong data", Toast.LENGTH_LONG).show();
        _savePasswordButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        final String oldpass = _oldPassword.getText().toString();
        final String newpass = _newPassword.getText().toString();
       final String newreEnterPass = _newreEnterPassword.getText().toString();

        if (oldpass.isEmpty() || oldpass.length() < 8 || oldpass.length() > 16) {
            _newPassword.setError("must be between 8 and 16 alphanumeric characters");
            valid = false;
        } else{
            _oldPassword.setError(null);
        }


        if (newpass.isEmpty() || newpass.length() < 8 || newpass.length() > 16) {
            _newPassword.setError("must be between 8 and 16 alphanumeric characters");
            valid = false;
        } else{
            _newPassword.setError(null);
        }

        if (newreEnterPass.isEmpty() ||  !(newreEnterPass.equals(newpass))) {
            _newreEnterPassword.setError("Password don't match");
            valid = false;
        } else {
            _newreEnterPassword.setError(null);

        }

        return valid;
    }
}
