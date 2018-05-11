package com.cibeg.cibreserve;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_address) EditText _addressText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    //FireBase Authentication Field
    private FirebaseAuth LoginAuth;

    //DataBase Field
    private FirebaseFirestore DataBase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);


        try {
            getSupportActionBar().hide();
        }catch (Exception e){
            Log.d("actionbar", e.getMessage());
        }

        LoginAuth = FirebaseAuth.getInstance();
        DataBase = FirebaseFirestore.getInstance();



        _signupButton = findViewById(R.id.btn_signup);
        _passwordText = findViewById(R.id.input_password);
        _emailText = findViewById(R.id.input_email);
        _nameText = findViewById(R.id.input_name);
        _addressText = findViewById(R.id.input_address);
        _mobileText = findViewById(R.id.input_mobile);
        _reEnterPasswordText = findViewById(R.id.input_reEnterPassword);
        _signupButton = findViewById(R.id.btn_signup);
        _loginLink = findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String address = _addressText.getText().toString();
        final String email = _emailText.getText().toString();
        final String mobile = _mobileText.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

            LoginAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(getBaseContext(), "Error: "+task.getException(), Toast.LENGTH_LONG).show();
                                _signupButton.setEnabled(true);
                            }
                            else
                            {
                                Toast.makeText(getBaseContext(), "You have been registered successfully", Toast.LENGTH_LONG).show();

                                // TODO: Store clients data in the FireBase Database
                               Map<String, Object> NewClient =new HashMap<>();
                               NewClient.put("name", name);
                               NewClient.put("address", address);
                               NewClient.put("email", email);
                               NewClient.put("mobile", mobile);
                               DataBase.collection("Users").document(email)
                                       .set(NewClient)
                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               Toast.makeText(getBaseContext(), "You have been registered successfully", Toast.LENGTH_LONG).show();
                                               overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                               startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                               finish();
                                           }
                                       })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getBaseContext(), "Error: "+e, Toast.LENGTH_LONG).show();
                                                _signupButton.setEnabled(true);
                                            }
                                        });

                                onSignupSuccess(email, password);

                            }

                        }
                    });

    }


    public void onSignupSuccess(String email, String password) {
        _signupButton.setEnabled(true);
        Intent data = new Intent();
        data.addCategory(email);
        data.addCategory(password);
        setResult(RESULT_OK, data);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Wrong sign up data", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 5) {
            _nameText.setError("at least 5 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (address.isEmpty()) {
            _addressText.setError("Enter Valid Address");
            valid = false;
        } else {
            _addressText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()!=11) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 16) {
            _passwordText.setError("must be between 8 and 16 alphanumeric characters");
            valid = false;
        } else{
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() ||  !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password don't match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}
