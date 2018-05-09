package com.cibeg.cibreserve;

import android.app.ProgressDialog;
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

public class SlotsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slots);

        try {
            getSupportActionBar().hide();
        }catch (Exception e){
            Log.d("actionbar", e.getMessage());
        }


        final ProgressDialog progressDialog = new ProgressDialog(SlotsActivity.this,
                R.style.AppTheme_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // This is a thread running in parallel to make connection
        (new Thread(new Runnable() {
            @Override
            public void run() {
                // make connection to backend to get the slots array
            }
        })).start();

        int top = 0;
        ViewGroup insertSlotsHere =  findViewById(R.id.insert_slots);
        for(int i=0 ; i<2 ; i++){
            LayoutInflater inflater = getLayoutInflater();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            lp.height = 975;
            lp.topMargin = top;
            top+=975;

            View view = inflater.inflate(R.layout.card_main, null);
            // fill in each card's data
            view.setId(i+1);
            ((TextView)view.findViewById(R.id.card_main_title)).setText("Counter "+ i);

            RadioGroup rdg = view.findViewById(R.id.slot_time_insert);
            for(int j=0; j<4; j++){
                RadioButton rdb = (RadioButton) getLayoutInflater().inflate(R.layout.radio_button_item, null);//new RadioButton(SlotsActivity.this);//, null, R.style.Widget_AppCompat_Button_Borderless);
                rdb.setId(j);
                rdb.setText("Radio Button " + j);
                rdg.addView(rdb, j);
            }


            view.setLayoutParams(lp);
            insertSlotsHere.addView(view);
        }

        progressDialog.dismiss();

    }


}
