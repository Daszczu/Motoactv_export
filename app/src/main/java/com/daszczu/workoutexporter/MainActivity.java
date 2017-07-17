package com.daszczu.workoutexporter;

import android.content.Context;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.daszczu.workoutexporter.async.WorkoutSync;
import com.daszczu.workoutexporter.managers.DatabaseManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.daszczu.workoutexporter.constants.Metrics.METRIC_DISTANCE;

public class MainActivity extends AppCompatActivity {
    private DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
    private String TAG = "EXPORTER";
    private DatabaseManager dbClient;

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
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        List<String> workoutLabels = getListLabels();
        setListLabels(workoutLabels);
    }

    private List<String> getListLabels() {
        List<String> options = new ArrayList<>();
        Cursor workoutCursor = getContentResolver().query(DatabaseManager.CONTENT_URI_WORKOUT_ACTIVITY, null, "", null, null);
        if (workoutCursor == null)
            return options;

        while (workoutCursor.moveToNext()) {
            int id = workoutCursor.getInt(workoutCursor.getColumnIndex("_id"));
            double dDistance = dbClient.getMetric(id, METRIC_DISTANCE)/1000;
            String sDistance = String.format(Locale.getDefault(), "%.2fkm", dDistance);

            options.add(id + " " + df.format(workoutCursor.getLong(workoutCursor.getColumnIndex("start_time"))) + " " + sDistance);
        }
        workoutCursor.close();
        Collections.reverse(options);
        return options;
    }

    private void setListLabels(List<String> values) {
        final ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, values));
        listView.setOnItemClickListener(onItemClickListener);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final String item = (String) parent.getItemAtPosition(position);

            String[] sID = item.split(" ");
            int workoutId = Integer.parseInt(sID[0]);

            Log.d(TAG, "Let's go");
            new WorkoutSync(MainActivity.this, item).execute(workoutId);
        }
    };

}
