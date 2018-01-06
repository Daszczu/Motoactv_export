package com.daszczu.workoutexporter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.daszczu.workoutexporter.async.SaveAllWorkouts;
import com.daszczu.workoutexporter.async.WorkoutSync;
import com.daszczu.workoutexporter.dto.LapDetails;
import com.daszczu.workoutexporter.dto.WorkoutActivity;
import com.daszczu.workoutexporter.managers.DatabaseManager;

import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends Activity {
    private String TAG = "EXPORTER";
    private DatabaseManager dbClient;
    private SyncTools syncTools;
    private final static int LAST_WORKOUTS = 5;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_plannedworkoutactivity:
                openPlannedWorkoutsActivity();
                return true;
            case R.id.export_all:
                exportAllActivities();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        Log.d(TAG, "WiFi disabled");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        dbClient = new DatabaseManager(getContentResolver());
        syncTools = new SyncTools(MainActivity.this, "backup");
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main2);

        setListLabels();
    }
    private void setListLabels() {
        final ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, dbClient.getLastWorkouts(LAST_WORKOUTS)));
        listView.setOnItemClickListener(onItemClickListener);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final WorkoutActivity item = (WorkoutActivity) parent.getItemAtPosition(position);

            int workoutId = item.getId();
            new WorkoutSync(MainActivity.this, item).execute(workoutId);
        }
    };

    private void openPlannedWorkoutsActivity() {
        Intent intent = new Intent(MainActivity.this, PlannedWorkoutsActivity.class);
        startActivity(intent);
    }

    private void exportAllActivities() {
        //TODO implement exporting all activities - use ASYNC!!!
        // 1. Get all the ids
        // 2. Check if file workout exists
        // 3. If not create, if exists use it
        // 4. Create maps with names and sizes
        // 5. Sort those file in zips of 2.5MBs
        // 6. Profit $$$
        Integer[] activityIds = dbClient.getAllWorkoutsIds();

        //invoke the async task

        SaveAllWorkouts saveAllWorkoutsTask = new SaveAllWorkouts(MainActivity.this);
        saveAllWorkoutsTask.execute(activityIds);
    }
}
