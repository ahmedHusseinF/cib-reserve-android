package com.cibeg.cibreserve;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Calendar;
import java.util.Locale;

public class SummaryActivity extends Activity {

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
        final String count = prefs.getString("counterId", "1");
        final String start = prefs.getString("start", "");
        final String end = prefs.getString("end", "");


        TextView bank_name = findViewById(R.id.sum_bank_name);
        bank_name.setText(bank);

        TextView branch_name = findViewById(R.id.sum_branch_name);
        branch_name.setText(branch);


        TextView date_name = findViewById(R.id.sum_date_name);
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        String newDate = "";
        try {

            Calendar cal = Calendar.getInstance();
            cal.setTime(fromUser.parse(date));
            newDate = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        date_name.setText(newDate);

        TextView process_name = findViewById(R.id.sum_process_name);
        process_name.setText(service);

        TextView start_name = findViewById(R.id.sum_start_name);
        start_name.setText(start);

        TextView end_name = findViewById(R.id.sum_end_name);
        end_name.setText(end);

        TextView counter_name = findViewById(R.id.sum_counter_name);
        counter_name.setText(count);


        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                             } catch (Exception e) {
                                                 Log.d("Summary", e.getMessage());
                                             }finally {
                                                 startActivity(new Intent(SummaryActivity.this, BranchesActivity.class));
                                             }
                                         }

                                         @Override
                                         public void onError(ANError error) {
                                             // handle error
                                             Toast.makeText(SummaryActivity.this, "Deletion failed, try again later", Toast.LENGTH_SHORT).show();
                                         }
                                     }
                        );
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
}
