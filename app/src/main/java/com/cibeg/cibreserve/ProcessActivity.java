package com.cibeg.cibreserve;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.time.Year;
import java.util.Calendar;

public class ProcessActivity extends AppCompatActivity {

    private static final String TAG = "Process";
    public static TextView date_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            Log.d(TAG, "Can't hide the bar");
        }

        Spinner s_process = findViewById(R.id.process_spinner);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        s_process.setAdapter(adapter);


        date_text = findViewById(R.id.txtDate);

        findViewById(R.id.SetDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // next up to next activity
                DatePickerFragment f = new DatePickerFragment();
                f.show(getFragmentManager(), "tag");

            }
        });
    }


        public static class DatePickerFragment extends DialogFragment
                implements DatePickerDialog.OnDateSetListener {

            private int field;

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {

                // Use the current date as the default date in the date picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);


                // return DatePickerDialog new instance
               // return new DatePickerDialog(getActivity(), this, year, month, day);
                return new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,this, year, month, day);

            }


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Set the date to textview
                // Month value start with zero, we have to add by one

                date_text.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }




    }
}
