package com.daszczu.workoutexporter.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.daszczu.workoutexporter.PlannedWorkoutsActivity;
import com.daszczu.workoutexporter.R;
import com.daszczu.workoutexporter.dto.Instance;
import com.daszczu.workoutexporter.managers.DatabaseManager;
import com.daszczu.workoutexporter.mapsimporter.MapTrackPoint;
import com.daszczu.workoutexporter.mapsimporter.MapsImporter;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class GPXInsert extends AsyncTask<File, String, Void> {
    private Activity mActivity;
    private DatabaseManager dbClient;
    private ProgressDialog dialog;
    private int routeId;
    private List<MapTrackPoint> tracks;

    private GPXInsert(){}

    public GPXInsert(Activity mActivity, int routeId, List<MapTrackPoint> tracks) {
        this.mActivity = mActivity;
        this.tracks = tracks;
        this.dialog = new ProgressDialog(mActivity);
        this.dbClient = new DatabaseManager(mActivity.getContentResolver());
        this.routeId = routeId;
    }

    @Override
    protected Void doInBackground(File... params) {
        if (params != null) {
            File file = params[0];

            try {
                publishProgress("Reading GPX file... " + file.getCanonicalPath());
                tracks = MapsImporter.getMapTracks(file);
            } catch (SAXException | ParserConfigurationException | XmlPullParserException | IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        int size = tracks.size();
        for (int i = 0; i < size; i++) {
            dbClient.addTrack(routeId, tracks.get(i));
            publishProgress(String.format("%s / %s tracks added", i+1, size));
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        dialog.setMessage(values[0]);
        dialog.show();
    }

    @Override
    protected void onPostExecute(Void res) {
        if (dialog.isShowing())
            dialog.dismiss();
        updateInstances();
    }

    private void updateInstances() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView lv = (ListView) mActivity.findViewById(R.id.planned_list_view);

                ArrayAdapter<Instance> arrayAdapter = (ArrayAdapter<Instance>) lv.getAdapter();
                arrayAdapter.clear();
                List<Instance> instances = dbClient.getInstances();
                for (Instance instance : instances)
                    arrayAdapter.add(instance);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }
}