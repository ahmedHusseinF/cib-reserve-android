package com.cibeg.cibreserve;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BranchesActivity extends AppCompatActivity {
    private static String  TAG = "BranchesActivity";
    //DataBase Field
    private FirebaseFirestore DataBase;
    ArrayList<String> BankBranches=new ArrayList<>();
    Spinner s_branches;
    Spinner s_banks;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);

        try{
            getSupportActionBar().hide();
        }catch (NullPointerException e){
            Log.d(TAG, "Can't hide the bar");
        }

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


        s_branches.setEnabled(false); // disable the branches list until we get the data


        final String[] BankSelected=new String[1];
        s_banks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

               BankSelected[0]= adapterView.getItemAtPosition(pos).toString();
                DataBase.collection(BankSelected[0])
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        //Log.d(TAG, document.getId() + " => " + document.getData());
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



        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // next up to next activity
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                startActivity(new Intent(BranchesActivity.this, ProcessActivity.class));
            }
        });
    }
    public final void getbranch(List<String> BankBranches)
    {
        ArrayAdapter Adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                BankBranches);
        s_branches.setEnabled(true);
        s_branches.setAdapter(Adapter);


    }


}
