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

import com.daszczu.workoutexporter.async.WorkoutSync;
import com.daszczu.workoutexporter.dto.WorkoutActivity;
import com.daszczu.workoutexporter.managers.DatabaseManager;

public class MainActivity extends Activity {
    private String TAG = "EXPORTER";
    private DatabaseManager dbClient;
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
        dbClient = new DatabaseManager(getContentResolver());
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
}
