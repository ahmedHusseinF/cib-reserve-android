package com.cibeg.cibreserve;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;

public class SummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        if (getActionBar() != null) {
            getActionBar().hide();
        }


        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getBaseContext())
                        .setTitle("Cancel You Reservation")
                        .setMessage("")
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final SharedPreferences prefs = getSharedPreferences("MY_OWN_PREFERENCE", MODE_PRIVATE);


                                String branch = prefs.getString("branch", " 10th of Ramadan");
                                String bank = prefs.getString("bank", "CIB EG");
                                String clientId = "";
                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                    clientId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                }

                                AndroidNetworking
                                        .post("https://e7gz.herokuapp.com/deleteTimeSlot")
                                        .addBodyParameter("branch", branch)
                                        .addBodyParameter("bank", bank)
                                        .addBodyParameter("clientId", clientId)
                                        .setTag(this)
                                        .setPriority(Priority.HIGH)
                                        .build()
                                        .getAsString(new StringRequestListener() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    Toast.makeText(SummaryActivity.this, "Reservation Deleted Successfully", Toast.LENGTH_LONG).show();
                                                                    SharedPreferences.Editor edit = prefs.edit();
                                                                    edit.putBoolean("haveReservation", false);
                                                                    edit.apply();
                                                                    startActivity(new Intent(SummaryActivity.this, BranchesActivity.class));
                                                                } catch (Exception e) {
                                                                    //

                                                                }
                                                            }

                                                            @Override
                                                            public void onError(ANError error) {
                                                                // handle error
                                                            }
                                                        }
                                        );
                            }
                        })
                        .setNegativeButton("Stay", null)
                        .show();
            }
        });

    }
}
