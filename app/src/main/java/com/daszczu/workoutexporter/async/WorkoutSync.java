package com.daszczu.workoutexporter.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.daszczu.workoutexporter.NoActivityDataException;
import com.daszczu.workoutexporter.R;
import com.daszczu.workoutexporter.SyncTools;
import com.daszczu.workoutexporter.dto.LapDetails;
import com.daszczu.workoutexporter.dto.StravaExchangeTokenResponse;
import com.daszczu.workoutexporter.dto.StravaUploadResponse;
import com.daszczu.workoutexporter.dto.WorkoutActivity;
import com.daszczu.workoutexporter.managers.StravaManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
            try {
                file = createWorkoutFile(workoutId);
            } catch (NoActivityDataException e) {
                Log.d("SaveAllWorkouts", "Missing data for workoutActivityId: " + e.getWorkoutActivityId());
                return null;
            }

        publishProgress("75", "Checking Internet connection");

        boolean isOnline = syncTools.checkOrEnableInternet();

        if (isOnline) {
            publishProgress("85", "Getting auth token");
            StravaManager stravaManager = new StravaManager();
            StravaExchangeTokenResponse token;
            try {
                token = stravaManager.getToken();
                publishProgress("95", "Sending workout");
                return stravaManager.uploadActivity(file, token.getAccessToken());
            } catch (IOException e) {
                Log.e("WorkoutSync", e.getLocalizedMessage(), e);
            }
        }
        return new StravaUploadResponse("Internet failure", "Cannot connect to the Internet");
    }

    private File createWorkoutFile(int workoutId) throws NoActivityDataException {
        publishProgress("40", "Reading workout");
        WorkoutActivity woa = syncTools.getWorkout(workoutId);
        List<LapDetails> laps = syncTools.getLaps(workoutId);
        publishProgress("60", "Saving workout");
        return syncTools.saveWorkoutToFile(woa, laps);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        dialog.setMessage(String.format("%s%% - %s...", values[0], values[1]));
        dialog.show();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Log.e("WorkoutSync", e.getLocalizedMessage(), e);
        }
    }

    @Override
    protected void onPostExecute(StravaUploadResponse res) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        dialog.setContentView(R.layout.my_layout);

        TextView title = (TextView) dialog.findViewById(R.id.status);
        TextView text = (TextView) dialog.findViewById(R.id.error);

        String sTitle = res != null ? res.getStatus() : "No activity data";
        String sError = res != null ? res.getError() : "Data for this activity is corrupted. It cannot be exported";

        title.setText(sTitle);
        text.setText(sError);

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