package com.cibeg.cibreserve;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    @BindView(R.id.new_name) EditText new_nameText;
    @BindView(R.id.new_address) EditText new_addressText;
    @BindView(R.id.button_backToBranches) ImageButton _backButton;
    @BindView(R.id.new_mobile) EditText new_mobileText;
    @BindView(R.id.button_changePassword) ImageButton  _changePasswordButton;
    @BindView(R.id.button_saveInformation) ImageButton _saveInformation;



    private FirebaseAuth LoginAuth;
    private FirebaseFirestore DataBase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.bind(this);


        try {
            getSupportActionBar().hide();
        }catch (Exception e){
            Log.d("actionbar", e.getMessage());
        }

        LoginAuth = FirebaseAuth.getInstance();
        DataBase = FirebaseFirestore.getInstance();



        _changePasswordButton = findViewById(R.id.button_changePassword);
        _saveInformation=findViewById(R.id.button_saveInformation);
        _backButton=findViewById(R.id.button_backToBranches);
        new_nameText = findViewById(R.id.new_name);
        new_addressText = findViewById(R.id.new_address);
        new_mobileText = findViewById(R.id.new_mobile);


        _changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               boolean s= changeinfo();
               if(s){ startActivity(new Intent(SettingsActivity.this, ChangePassword.class));}
            }
        });

        _saveInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               boolean d =changeinfo();


            }
        });


        _backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, BranchesActivity.class));
            }
        });





        DataBase = FirebaseFirestore.getInstance();
        final String currentEmail= LoginAuth.getCurrentUser().getEmail();
        DocumentReference docRef = DataBase.collection("Users").document(currentEmail);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        String name= (String)document.get("name");
                        String address=(String)document.get("address");
                        String number= (String)document.get("mobile");
                        EditText NName = findViewById(R.id.new_name);
                        EditText NAddress = findViewById(R.id.new_address);
                        EditText NNom = findViewById(R.id.new_mobile);
                        NName.setText(name);
                        NAddress.setText(address);
                        NNom.setText(number);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    public boolean changeinfo() {
        Log.d(TAG, "Change Password");

        if (!validate()) {
            onChangeInfoFailed();
            return false;
        }

       // _saveInformation.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this,
                R.style.AppTheme_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Changing Information...");
        progressDialog.show();

        final String email= LoginAuth.getCurrentUser().getEmail();
        final String name = new_nameText.getText().toString();
        final String address = new_addressText.getText().toString();
        final String mobile = new_mobileText.getText().toString();

                                // Store clients data in the FireBase Database
                               Map<String, Object> Client =new HashMap<>();
                               Client.put("name", name);
                               Client.put("address", address);
                               Client.put("mobile", mobile);
                               DataBase.collection("Users").document(email)
                                       .set(Client)
                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {

                                               Toast.makeText(getBaseContext(), "Your information have been changed successfuly ", Toast.LENGTH_LONG).show();
                                               overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                               progressDialog.dismiss();

                                           }
                                       })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getBaseContext(), "Error: "+e, Toast.LENGTH_LONG).show();
                                                _saveInformation.setEnabled(true);
                                            }
                                        });

return true;

    }



    public void onChangeInfoFailed() {
        Toast.makeText(getBaseContext(), "Wrong data", Toast.LENGTH_LONG).show();
        _saveInformation.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = new_nameText.getText().toString();
        String address = new_addressText.getText().toString();
        String mobile = new_mobileText.getText().toString();


        if (name.isEmpty() || name.length() < 5) {
            new_nameText.setError("at least 5 characters");
            valid = false;
        } else {
            new_nameText.setError(null);
        }

        if (address.isEmpty()) {
            new_addressText.setError("Enter Valid Address");
            valid = false;
        } else {
            new_addressText.setError(null);
        }


        if (mobile.isEmpty() || mobile.length()!=11) {
            new_mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            new_mobileText.setError(null);
        }


        return valid;
    }
}
