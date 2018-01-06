package com.daszczu.workoutexporter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daszczu.workoutexporter.async.GPXInsert;
import com.daszczu.workoutexporter.dto.Instance;
import com.daszczu.workoutexporter.dto.Routine;

import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MainMenu extends Activity {
    private File[] gpxFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_menu5);
        getGPXFilenames();
    }

    private void getGPXFilenames() {
        File gpxDir = new File(Environment.getExternalStorageDirectory(), "gpx");
        if (gpxDir.isFile()) {
            Toast.makeText(MainMenu.this, "Brak folderu gpx", Toast.LENGTH_LONG).show();
            return;
        }
        gpxFiles = gpxDir.listFiles();
    }
    public void exportWorkout(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void changePlannedRoute(View v) {
        Intent intent = new Intent(this, PlannedWorkoutsActivity.class);
        startActivity(intent);
    }

    public void addRouteFromFile(View v) {
        registerForContextMenu(v);
        openContextMenu(v);

    }

    public void planNewWorkout(View v) {
        registerForContextMenu(v);
        openContextMenu(v);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        int id = v.getId();
        int btnId = R.id.image4;
        if (v.getId() == R.id.image4) {
            for (int i = 0; i < gpxFiles.length; i++) {
                File file = gpxFiles[i];
                menu.add(0, i, 0, file.getName());
            }

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo lastMenuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int itemId = item.getItemId();
        int groupId = item.getGroupId();
        return true;
    }

    private void addRouteFromFile(int itemId) {
        try {
            String filename = gpxFiles[itemId].getCanonicalPath();
            String routineName = filename;
            String routeName = filename;

            //1. insert routine

            //2. insert route

            //3. insert routine_activities

            //4. last step
//            new GPXInsert(MainMenu.this, routeId).execute(gpxFiles[itemId]);
        } catch (IOException e) {}

    }
}
