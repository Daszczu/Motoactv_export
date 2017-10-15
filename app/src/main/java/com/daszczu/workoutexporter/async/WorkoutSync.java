package com.daszczu.workoutexporter.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daszczu.workoutexporter.ConnectionException;
import com.daszczu.workoutexporter.R;
import com.daszczu.workoutexporter.dto.LapDetails;
import com.daszczu.workoutexporter.managers.StravaManager;
import com.daszczu.workoutexporter.SyncTools;
import com.daszczu.workoutexporter.dto.StravaUploadResponse;
import com.daszczu.workoutexporter.dto.WorkoutActivity;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WorkoutSync extends AsyncTask<Integer, String, StravaUploadResponse> {
    private ProgressDialog dialog;
    private SyncTools syncTools;
    private WorkoutActivity workout;

    private WorkoutSync() {
    }

    public WorkoutSync(Context ctx, WorkoutActivity workout) {
        this.dialog = new ProgressDialog(ctx);
        this.syncTools = new SyncTools(ctx);
        this.workout = workout;
    }

    @Override
    protected void onPreExecute() {
        String title = workout.toString();
        title = title.split(" ")[1] + " " + title.split(" ")[2];
        dialog.setTitle(title);
        dialog.setMessage("20% - Checking workout...");
        dialog.show();
    }

    @Override
    protected StravaUploadResponse doInBackground(Integer... params) {
        int workoutId = params[0];
        File file = syncTools.fileExists(workoutId);
        if (file == null)
            file = createWorkoutFile(workoutId);

        publishProgress("75", "Checking Internet connection");

        boolean isOnline = syncTools.checkOrEnableInternet();

        if (isOnline) {
            publishProgress("85", "Getting auth token");
            StravaManager sm;
            try {
                sm = new StravaManager();
            } catch (IOException e) {
                return new StravaUploadResponse("Strava failure", "Cannot obtain auth token");
            }
            return uploadActivity(sm, file, "95", "Sending workout");
        }
        else
            return new StravaUploadResponse("Internet failure", "Cannot connect to the Internet");
    }

    private File createWorkoutFile(int workoutId) {
        publishProgress("40", "Reading workout");
        WorkoutActivity woa = syncTools.getWorkout(workoutId);
        List<LapDetails> laps = syncTools.getLaps(workoutId);
        publishProgress("60", "Saving workout");
        return syncTools.saveWorkoutToFile(woa, laps);
    }

    private StravaUploadResponse uploadActivity(StravaManager sm, File file, String... progressInfo) {
        try {
            if (progressInfo.length == 2)
                publishProgress(progressInfo[0], progressInfo[1]);
            else
                publishProgress(progressInfo[0] + " " + progressInfo[2], progressInfo[1]);
            return sm.uploadActivity2(file);
        }
        catch (ConnectionException e) {
            String iteration = progressInfo.length != 2 ? String.valueOf(Integer.valueOf(progressInfo[2]) + 1) : "2";
            Log.e("UPLOAD", e.getLocalizedMessage());
            return uploadActivity(sm, file, progressInfo[0], progressInfo[1], iteration);
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        dialog.setMessage(String.format("%s%% - %s...", values[0], values[1]));
        dialog.show();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(StravaUploadResponse res) {
        if (dialog.isShowing())
            dialog.dismiss();

        dialog.setContentView(R.layout.my_layout);

        TextView title = (TextView) dialog.findViewById(R.id.status);
        title.setText(res.getStatus());

        TextView text = (TextView) dialog.findViewById(R.id.error);
        text.setText(res.getError());

        Button btn = (Button) dialog.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}