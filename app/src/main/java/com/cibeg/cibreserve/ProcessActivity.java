package com.cibeg.cibreserve;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProcessActivity extends AppCompatActivity {
    private FirebaseFirestore DataBase;
    private DatabaseReference mDatabase;
    ArrayList<String> process = new ArrayList<>();
    Spinner s_process;
    Services pp=new Services();

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

        DataBase = FirebaseFirestore.getInstance();
        s_process = findViewById(R.id.process_spinner);

        DataBase.collection("Services").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        process.add(document.getId());




                    }
                    getprocess(process);
                    //s_process.setEnabled(true);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }


            }
        });
       s_process.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {


                Spinner spinner = findViewById(R.id.process_spinner);
                String selectedprocess = spinner.getSelectedItem().toString();


// ..
         DataBase=FirebaseFirestore.getInstance();
                DocumentReference docRef = DataBase.collection("Services").document(selectedprocess);


                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                Services name =new Services();
                                name.Information=(String)document.get("Information");
                                TextView tv = findViewById(R.id.text_process);
                                tv.setText(name.Information);

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

    public final void getprocess(List<String> processes)
    {
        ArrayAdapter Adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, processes);
        s_process.setAdapter(Adapter);

    }



    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            /*
                We should use THEME_HOLO_LIGHT, THEME_HOLO_DARK or THEME_TRADITIONAL only.

                The THEME_DEVICE_DEFAULT_LIGHT and THEME_DEVICE_DEFAULT_DARK does not work
                perfectly. This two theme set disable color of disabled dates but users can
                select the disabled dates also.

                Other three themes act perfectly after defined enabled date range of date picker.
                Those theme completely hide the disable dates from date picker object.
             */
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_DARK, this, year, month, day);

            /*
                add(int field, int value)
                    Adds the given amount to a Calendar field.
             */
            // Add 3 days to Calendar
            calendar.add(Calendar.DATE, 6);

            /*
                getTimeInMillis()
                    Returns the time represented by this Calendar,
                    recomputing the time from its fields if necessary.

                getDatePicker()
                Gets the DatePicker contained in this dialog.

                setMinDate(long minDate)
                    Sets the minimal date supported by this NumberPicker
                    in milliseconds since January 1, 1970 00:00:00 in getDefault() time zone.

                setMaxDate(long maxDate)
                    Sets the maximal date supported by this DatePicker in milliseconds
                    since January 1, 1970 00:00:00 in getDefault() time zone.
             */

            // Set the Calendar new date as maximum date of date picker
            dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());

            // Subtract 6 days from Calendar updated date
            calendar.add(Calendar.DATE, -6);

            // Set the Calendar new date as minimum date of date picker
            dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());

            // So, now date picker selectable date range is 7 days only

            // Return the DatePickerDialog
            return dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the chosen date
            TextView tv = (TextView) getActivity().findViewById(R.id.txtDate);
            int actualMonth = month+1; // Because month index start from zero
            // Display the unformatted date to TextView


            // Create a Date variable/object with user chosen date
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, day, 0, 0, 0);
            Date chosenDate = cal.getTime();

           // SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
           // Date date = new Date(year, month, day-1);
           // String dayOfWeek = simpledateformat.format(date);



            // Format the date using style full
            DateFormat df_full = DateFormat.getDateInstance(DateFormat.FULL);
            String df_full_str = df_full.format(chosenDate);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date d_name = chosenDate;
            String dayOfTheWeek = sdf.format(d_name);
            if(dayOfTheWeek.equals("Friday") || dayOfTheWeek.equals("Saturday"))
            {
                tv.setText("Choose any date except(Friday,Saturday)");
                tv.setTextColor(Color.parseColor("#D50000"));
                tv.setTextSize(12);
            }

            else
            {
                // Display the formatted date
                tv.setTextColor(Color.parseColor("#3F51B5"));
                tv.setTextSize(20);
                tv.setText("Choosen date:"  + "\n");
                tv.setText(tv.getText() + df_full_str);
            }
        }
    }
}

