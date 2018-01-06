package com.daszczu.workoutexporter.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.daszczu.workoutexporter.MainActivity;
import com.daszczu.workoutexporter.SyncTools;
import com.daszczu.workoutexporter.dto.LapDetails;
import com.daszczu.workoutexporter.dto.WorkoutActivity;

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

public class SaveAllWorkouts extends AsyncTask<Integer, String, Void> {
    private SyncTools syncTools;
    private ProgressDialog dialog;

    private SaveAllWorkouts() {
    }

    public SaveAllWorkouts(Context ctx) {
        this.syncTools = new SyncTools(ctx, "backup");
        this.dialog = new ProgressDialog(ctx);
    }

    @Override
    protected Void doInBackground(Integer... params) {
        for (int i = 0; i < params.length; i++) {
            int activityId = params[i];
            File file = syncTools.fileExists(activityId);
            if (file == null) {
                WorkoutActivity woa = syncTools.getWorkout(activityId);
                List<LapDetails> laps = syncTools.getLaps(activityId);
                syncTools.saveWorkoutToFile(woa, laps);
            }
            publishProgress(i + " / " + params.length);
            Log.d("SAVE", "i: " + i + ", length: " + params.length);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        dialog.setMessage(values[0]);
        dialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        try {
            if (dialog.isShowing())
                dialog.dismiss();
            saveToZip();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPostExecute(aVoid);
    }

    private Map<String, Long> saveToZip() throws IOException {
        Map<String, Long> files = new TreeMap<>();

        File folder = new File(Environment.getExternalStorageDirectory(), "/backup/");
        if (!folder.exists())
            folder.mkdir();

        for (File file : folder.listFiles()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
            ZipOutputStream out = new ZipOutputStream(bufferedOutputStream);
            out.setLevel(9);
            String filename = file.getName();
            ZipEntry ze = new ZipEntry(filename);
            out.putNextEntry(ze);
            byte[] content = FileUtils.readFileToByteArray(file);

            out.write(content, 0, content.length);
            out.closeEntry();
            out.finish();
            out.flush();
            out.close();

            bufferedOutputStream.close();
            byteArrayOutputStream.close();
            int size = byteArrayOutputStream.toByteArray().length;
            files.put(filename, (long) size);
        }
        return files;
    }
}