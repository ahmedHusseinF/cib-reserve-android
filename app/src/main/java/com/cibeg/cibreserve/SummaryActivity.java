package com.cibeg.cibreserve;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SummaryActivity extends AppCompatActivity { // 5od screenshoot wngz w8

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        if (getActionBar() != null) {
            getActionBar().hide();
        }


        SharedPreferences prefs = getSharedPreferences("MY_OWN_PREFERENCE", MODE_PRIVATE);


        final String branch = prefs.getString("branch", " 10th of Ramadan");
        final String bank = prefs.getString("bank", "CIB EG");
        final String service = prefs.getString("service", "Business referrals");
        final String date = prefs.getString("date", "20180530");

        final String start=prefs.getString("start","");
        final String end=prefs.getString("end","");

        TextView bank_name =findViewById(R.id.sum_bank_name);
        bank_name.setText(bank);

        TextView branch_name =findViewById(R.id.sum_branch_name);
        branch_name.setText(branch);


        TextView date_name =findViewById(R.id.sum_date_name);
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        String newDate = "";
        try {
            newDate = fromUser.parse(date).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date_name.setText(newDate);

        TextView process_name =findViewById(R.id.sum_process_name);
        process_name.setText(service);

        TextView start_name =findViewById(R.id.sum_start_name);
        start_name.setText(start);

        TextView end_name =findViewById(R.id.sum_end_name);
        end_name.setText(end);








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
