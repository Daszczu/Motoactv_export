package com.daszczu.workoutexporter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.daszczu.workoutexporter.async.GPXInsert;
import com.daszczu.workoutexporter.dto.Instance;
import com.daszczu.workoutexporter.dto.Routine;
import com.daszczu.workoutexporter.managers.DatabaseManager;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class PlannedWorkoutsActivity extends Activity {
    private DatabaseManager dbClient;
    private ListView lv;
    private List<Instance> instances;
    private ProgressDialog dialog;
    private File[] gpxFiles;
    private List<Routine> routines;
    private AdapterView.AdapterContextMenuInfo lastMenuInfo;
    private ArrayAdapter<Instance> arrayAdapter;

    private static final int MENU_INDEX_CHANGE = 1;
    private static final int MENU_INDEX_CHANGE_BY_FILE = 2;
    private static final int MENU_INDEX_DELETE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planned_workouts);

        dbClient = new DatabaseManager(this.getContentResolver());
        getGPXFilenames();
        populateListView();
        routines = dbClient.getRoutines();
    }

    private void getGPXFilenames() {
        File gpxDir = new File(Environment.getExternalStorageDirectory(), "gpx");
        if (gpxDir.isFile()) {
            Toast.makeText(PlannedWorkoutsActivity.this, "Brak folderu gpx", Toast.LENGTH_LONG).show();
            return;
        }
        gpxFiles = gpxDir.listFiles();
    }

    private void populateListView() {
        instances = dbClient.getInstances();
        lv = (ListView) findViewById(R.id.planned_list_view);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, instances);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(onItemClickListener);
        registerForContextMenu(lv);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Instance item = (Instance) parent.getItemAtPosition(position);
            Log.d("ad", item.toString());

            openContextMenu(view);
        }
    };


    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.planned_list_view) {

            SubMenu subMenu = menu.addSubMenu("Podmień z bazy");

            for (int i = 0; i< routines.size(); i++) {
                Routine routine = routines.get(i);
                subMenu.add(MENU_INDEX_CHANGE, i, 0, routine.toString());
            }

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            SubMenu subMenu2 = menu.addSubMenu("Podmień z pliku");
            for (int i = 0; i < gpxFiles.length; i++) {
                File file = gpxFiles[i];
                subMenu2.add(MENU_INDEX_CHANGE_BY_FILE, i, 0, file.getName());
            }
            menu.add(MENU_INDEX_DELETE, Menu.NONE, 2, "Usuń");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (lastMenuInfo == null)
            lastMenuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        int itemId = item.getItemId();
        int groupId = item.getGroupId();

        Instance inst = instances.get(lastMenuInfo.position);
        int routeId = inst.getRoutine().getRouteId();

        switch (groupId) {
            case MENU_INDEX_CHANGE:
                int eventId = inst.getEventId();
                dbClient.updateWorkoutRoutine(eventId, routines.get(itemId).getId());
//                try {
////                    Log.d("MENU", gpxFiles[itemId].getCanonicalPath());
//                } catch (IOException e) {}
//                dbClient.removeTracks(routeId);
//                new GPXInsert(PlannedWorkoutsActivity.this, routeId).execute(gpxFiles[itemId]);
                break;

            case MENU_INDEX_CHANGE_BY_FILE:
//                dbClient.removeTracks(routeId);
//                updateInstances();

                try {
                    Log.d("MENU", gpxFiles[itemId].getCanonicalPath());
                } catch (IOException e) {}
                dbClient.removeTracks(routeId);
                new GPXInsert(PlannedWorkoutsActivity.this, routeId, null).execute(gpxFiles[itemId]);
                break;

            case MENU_INDEX_DELETE:
                break;

            default:
                break;
        }

        return true;
    }

    private void updateInstances() {
        PlannedWorkoutsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arrayAdapter.clear();
                List<Instance> instances = dbClient.getInstances();
                for (Instance instance : instances)
                    arrayAdapter.add(instance);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        lastMenuInfo = null;
    }
}
