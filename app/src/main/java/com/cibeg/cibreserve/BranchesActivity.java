package com.cibeg.cibreserve;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class BranchesActivity extends AppCompatActivity {
    private static String TAG = "BranchesActivity";
    //DataBase Field
    private FirebaseFirestore DataBase;
    ArrayList<String> BankBranches = new ArrayList<>();
    Spinner s_branches;
    Spinner s_banks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);


        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            Log.d(TAG, "Can't hide the bar");
        }

        // Go to Setting when the user clicks on the edit button
        ImageButton B = findViewById(R.id.EditButton);
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                startActivity(new Intent(BranchesActivity.this, SettingsActivity.class));
                finish();

            }
        });

        DataBase = FirebaseFirestore.getInstance();

        s_banks = findViewById(R.id.banks_spinner);
        s_branches = findViewById(R.id.branches_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Banks, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        s_banks.setAdapter(adapter);

        findViewById(R.id.fab).setEnabled(false);


        s_branches.setEnabled(false); // disable the branches list until we get the data
        s_banks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {


                Spinner spinner = findViewById(R.id.banks_spinner);
                String bankselected = spinner.getSelectedItem().toString();
                DataBase.collection(bankselected).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                BankBranches.add(document.getId());
                            }
                            getbranch(BankBranches);
                            s_branches.setEnabled(true);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }


                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // have to be implemented to create an OnItemSelectedListener
            }
        });

        s_branches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {


                Spinner spinner = findViewById(R.id.branches_spinner);
                String selectedBranch = spinner.getSelectedItem().toString();


                DataBase = FirebaseFirestore.getInstance();
                DocumentReference docRef = DataBase.collection("CIB EG").document(selectedBranch);


                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                BranchParameters BParam = new BranchParameters();
                                BParam.Address = (String) document.get("Address");
                                BParam.Area = (String) document.get("Area");
                                BParam.City = (String) document.get("City");
                                BParam.Working_Days = (String) document.get("Working Days");
                                BParam.Working_Hours = (String) document.get("Working Hours");
                                TextView tv = findViewById(R.id.text_branchesparameters);
                                tv.setText("Address:" + BParam.Address + "\n" + "Area:" + BParam.Area + "\n" + "City:" + BParam.City +
                                        "\n" + "Working Days:" + BParam.Working_Days + "\n" + "Working Hours:" + BParam.Working_Hours);


                                findViewById(R.id.fab).setEnabled(true);

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // have to be implemented to create an OnItemSelectedListener
            }
        });


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // next up to next activity
                SharedPreferences prefs = getSharedPreferences("MY_OWN_PREFERENCE", MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                Spinner spinner = findViewById(R.id.branches_spinner);
                String selectedBranch = spinner.getSelectedItem().toString();


                edit.putString("branch", selectedBranch);

                if(edit.commit()) {
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    startActivity(new Intent(BranchesActivity.this, ProcessActivity.class));
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

    public final void getbranch(List<String> BankBranches) {
        ArrayAdapter Adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                BankBranches);
        s_branches.setEnabled(true);
        s_branches.setAdapter(Adapter);
    }
}
