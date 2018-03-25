package com.cibeg.cibreserve;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ProcessActivity extends AppCompatActivity {

    private static final String TAG = "Process";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            Log.d(TAG, "Can't hide the bar");
        }
    }
}
