package com.daszczu.workoutexporter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.DatePickerDialogFragment;
import com.avast.android.dialogs.fragment.TimePickerDialogFragment;
import com.avast.android.dialogs.iface.IDateDialogListener;
import com.crashlytics.android.Crashlytics;
import com.daszczu.workoutexporter.retrofit.RetrofitCalls;
import com.daszczu.workoutexporter.retrofit.WorkoutPlan;
import io.fabric.sdk.android.Fabric;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewWorkoutPlannerActivity extends AppCompatActivity implements IDateDialogListener {
    private static final int REQUEST_START_DATE_PICKER = 11;
    private static final int REQUEST_TIME_PICKER = 13;
    private static final String TAG = "NWPActivity";
    private RetrofitCalls retrofit;
    private Context context;
    private Calendar startDate;


    private Map<String, String> activityTypes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_workout_planner);

        context = NewWorkoutPlannerActivity.this;
        retrofit = new RetrofitCalls(context);

        this.startDate = Calendar.getInstance();

        findViewById(R.id.start_date_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment
                        .createBuilder(NewWorkoutPlannerActivity.this, getSupportFragmentManager())
                        .setDate(new Date())
                        .setPositiveButtonText(android.R.string.ok)
                        .setNegativeButtonText(android.R.string.cancel)
                        .setRequestCode(REQUEST_START_DATE_PICKER)
                        .show();
            }
        });

        findViewById(R.id.time_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialogFragment
                        .createBuilder(NewWorkoutPlannerActivity.this, getSupportFragmentManager())
                        .setDate(new Date())
                        .setPositiveButtonText(android.R.string.ok)
                        .setNegativeButtonText(android.R.string.cancel)
                        .setRequestCode(REQUEST_TIME_PICKER)
                        .show();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.activityTypesSpinner);
        activityTypes.put("Running", "1");
        activityTypes.put("Riding", "4");
        spinner.setSelection(1);

        List<String> spinnerActivityTypes = new ArrayList<>();
        spinnerActivityTypes.addAll(activityTypes.keySet());


        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerActivityTypes);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        try {
            retrofit.login();

          Fabric.with(this, new Crashlytics());
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public void send(View v) throws IOException, ParseException {
        EditText titleText = (EditText) findViewById(R.id.title_text);
        EditText descText = (EditText) findViewById(R.id.desc_text);
        Spinner spinner = (Spinner) findViewById(R.id.activityTypesSpinner);

        String selectedKey = (String) spinner.getSelectedItem();
        String activityType = activityTypes.get(selectedKey);

        WorkoutPlan workoutPlan = new WorkoutPlan(activityType);
        workoutPlan.setWorkoutPlanName(titleText.getText().toString());
        workoutPlan.setDescription(descText.getText().toString());
        workoutPlan.setStartDate(startDate.getTimeInMillis());

        DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        workoutPlan.setStartTime(df.format(startDate.getTime()));

        startDate.add(Calendar.HOUR, 1);
        workoutPlan.setEndDate(startDate.getTimeInMillis());
        workoutPlan.setRouteId("51b83dde-0c3e-42c4-9299-73d03c44fb0");

        retrofit.saveWorkoutPlan(workoutPlan);
    }

    @Override
    public void onPositiveButtonClicked(int resultCode, Date date) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        DateFormat onlyDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        DateFormat onlyTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(date);

        switch (resultCode) {
            case REQUEST_START_DATE_PICKER:
                this.startDate.set(Calendar.DATE, tempCal.get(Calendar.DATE));
                this.startDate.set(Calendar.MONTH, tempCal.get(Calendar.MONTH));
                this.startDate.set(Calendar.YEAR, tempCal.get(Calendar.YEAR));

                Button dateBtn = (Button) findViewById(R.id.start_date_picker);
                dateBtn.setText(onlyDateFormat.format(startDate.getTime()));
                break;
            case REQUEST_TIME_PICKER:
                this.startDate.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
                this.startDate.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));

                Button timeBtn = (Button) findViewById(R.id.time_picker);
                timeBtn.setText(onlyTimeFormat.format(startDate.getTime()));
                break;
            default:
                break;
        }
        Toast.makeText(this, "Success! " + dateFormat.format(startDate.getTime()), Toast.LENGTH_SHORT).show();
   }

    @Override
    public void onNegativeButtonClicked(int resultCode, Date date) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
        Toast.makeText(this, "Cancelled " + dateFormat.format(date), Toast.LENGTH_SHORT).show();
    }
}
