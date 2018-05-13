package com.cibeg.cibreserve;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SlotsActivity extends AppCompatActivity {
    private static String TAG = "SlotsActivity";
    ProgressDialog progressDialog;
    ArrayList<RadioGroup> radiosArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slots);

        // initialize networking
        AndroidNetworking.initialize(getApplicationContext());

        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            Log.d("actionbar", e.getMessage());
        }

        findViewById(R.id.fabSlots).setEnabled(false);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        String currentEmail = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getEmail() : null;
        if (currentEmail == null)
            return;

        progressDialog = new ProgressDialog(SlotsActivity.this,
                R.style.AppTheme_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        final SharedPreferences prefs = getSharedPreferences("MY_OWN_PREFERENCE", MODE_PRIVATE);


        final String branch = prefs.getString("branch", " 10th of Ramadan");
        final String bank = prefs.getString("bank", "CIB EG");
        final String service = prefs.getString("service", "Business referrals");
        final String date = prefs.getString("date", "20180530");
        final String clientId = (FirebaseAuth.getInstance().getCurrentUser() != null) ? FirebaseAuth.getInstance().getCurrentUser().getEmail() : null;

        AndroidNetworking
                .post("https://e7gz.herokuapp.com/returnAvailableSlots")
                .addBodyParameter("branch", branch)
                .addBodyParameter("bank", bank)
                .addBodyParameter("clientId", clientId)
                .addBodyParameter("service", service.trim())
                .addBodyParameter("date", date)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        progressDialog.cancel();
                                        try {
                                            findViewById(R.id.fabSlots).setEnabled(true);
                                            populateSlots(response);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onError(ANError error) {
                                        // handle error
                                        progressDialog.cancel();
                                        Toast.makeText(SlotsActivity.this, "There is no internet connection, try again later", Toast.LENGTH_LONG).show();
                                        onBackPressed();
                                        Log.e(TAG, error.getErrorBody());
                                    }
                                }
                );

        findViewById(R.id.fabSlots).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("haveReservation", true);
                edit.apply();

                String start = "";
                String end = "";

                for(int i=0;i<radiosArray.size();i++){
                    if(radiosArray.get(i).getCheckedRadioButtonId() != -1){
                       int id = radiosArray.get(i).getCheckedRadioButtonId();

                        String value = ((RadioButton)findViewById(id)).getText().toString();

                        String[] startEnd = value.split("-");

                        start = startEnd[0];
                        end = startEnd[1];
                    }
                }

                if(start.isEmpty()){
                    Toast.makeText(SlotsActivity.this, "Please select a slot first...", Toast.LENGTH_LONG).show();
                }

                AndroidNetworking
                        .post("https://e7gz.herokuapp.com/reserveTimeSlot")
                        .addBodyParameter("branch", branch)
                        .addBodyParameter("bank", bank)
                        .addBodyParameter("clientId", clientId)
                        .addBodyParameter("service", service.trim())
                        .addBodyParameter("start", start)
                        .addBodyParameter("end", end)
                        .setTag(this)
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsString(new StringRequestListener() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    startActivity(new Intent(SlotsActivity.this, SummaryActivity.class));
                                                } catch (Exception e) {
                                                    Log.e(TAG, e.getMessage());
                                                }
                                            }

                                            @Override
                                            public void onError(ANError error) {
                                                // handle error
                                                Toast.makeText(SlotsActivity.this, "There is no internet connection, try again later", Toast.LENGTH_LONG).show();
                                                onBackPressed();
                                                Log.e(TAG, error.getErrorBody());
                                            }
                                        }
                        );
            }
        });
    }


    private void populateSlots(JSONArray slots) throws JSONException {

        int top = 0;
        ViewGroup insertSlotsHere = findViewById(R.id.insert_slots);
        for (int i = 0; i < slots.length(); i++) {
            LayoutInflater inflater = getLayoutInflater();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            lp.height = 450;
            lp.topMargin = top;
            top += 450;

            JSONObject counter = slots.getJSONObject(i);
            JSONArray timeSlots = counter.getJSONArray("timeSlots");

            View view = inflater.inflate(R.layout.card_main, null);
            // fill in each card's data
            view.setId(i + 1);
            ((TextView) view.findViewById(R.id.card_main_title)).setText("Counter " + counter.getInt("counterId"));

            RadioGroup rdg = view.findViewById(R.id.slot_time_insert);
            rdg.setId(View.generateViewId());
            radiosArray.add(rdg);



            rdg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    for (int l = 0; l < radiosArray.size(); l++) {
                        if(group.getId() == radiosArray.get(l).getId())
                            continue;

                        if(radiosArray.get(l).getCheckedRadioButtonId() == -1)
                            continue;

                        radiosArray.get(l).clearCheck();
                    }
                }
            });
            for (int j = 0; j < timeSlots.length(); j++) {
                RadioButton rdb = (RadioButton) getLayoutInflater().inflate(R.layout.radio_button_item, null);
                JSONObject aSlot = timeSlots.getJSONObject(j);
                rdb.setId(j);
                rdb.setText(aSlot.getString("start") + " - " + aSlot.getString("end"));
                rdg.addView(rdb, j);
            }


            view.setLayoutParams(lp);
            insertSlotsHere.addView(view);
        }


        progressDialog.dismiss();
    }


}
