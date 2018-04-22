package com.daszczu.workoutexporter.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.daszczu.workoutexporter.NoActivityDataException;
import com.daszczu.workoutexporter.SyncTools;
import com.daszczu.workoutexporter.dto.Asd;
import com.daszczu.workoutexporter.dto.FileForZip;
import com.daszczu.workoutexporter.dto.LapDetails;
import com.daszczu.workoutexporter.dto.WorkoutActivity;

import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
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
            WorkoutActivity woa;
            if (file == null) {
                try {
                    woa = syncTools.getWorkout(activityId);
                }
                catch (NoActivityDataException e) {
                    Log.d("SaveAllWorkouts", "Missing data for workoutActivityId: " + e.getWorkoutActivityId());
                    continue;
                }
                List<LapDetails> laps = syncTools.getLaps(activityId);
                syncTools.saveWorkoutToFile(woa, laps);
            }
            publishProgress(i + " / " + params.length);
//            Log.d("SAVE", "i: " + i + ", length: " + params.length);
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
            Map<String, Long> files = saveToZip();
            saveZipsToPackages(files);

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPostExecute(aVoid);
    }

    private void saveZipsToPackages(Map<String, Long> files) throws IOException {
        List<Map.Entry<String, Long>> list = new LinkedList<>(files.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Iterator entries = files.entrySet().iterator();
        List<FileForZip> packages = new ArrayList<>();
        FileForZip zipFile = new FileForZip();

//        while (entries.hasNext()) {
        for (Map.Entry<String, Long> thisEntry : list) {
//            Map.Entry<String, Long> thisEntry = (Map.Entry) entries.next();
            Long size = zipFile.getSize();

            if (size + thisEntry.getValue() > 2621440) {
                packages.add(zipFile);
                zipFile = new FileForZip();
            }
            zipFile.getFilenames().add(thisEntry.getKey());
            zipFile.setSize(zipFile.getSize() + thisEntry.getValue());

        }

        packages.add(zipFile);
        Asd asd = new Asd();
        asd.size = 0;
        int index = 0;
        for (FileForZip zipFile1 : packages) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
            ZipOutputStream out = new ZipOutputStream(bufferedOutputStream);
            out.setLevel(9);

            for (String filename : zipFile1.getFilenames()) {
                File exportDir = new File(Environment.getExternalStorageDirectory(), "backup");
                File file = new File(exportDir, filename);

                ZipEntry ze = new ZipEntry(filename);
                out.putNextEntry(ze);
                byte[] content = FileUtils.readFileToByteArray(file);

                out.write(content, 0, content.length);
                out.closeEntry();

            }
            out.finish();
            out.flush();
            out.close();
            bufferedOutputStream.close();
            byteArrayOutputStream.close();
            //int size = byteArrayOutputStream.toByteArray().length;

            File exportDir = new File(Environment.getExternalStorageDirectory(), "backup");
            String name = "moto" + index + ".zip";
            File file = new File(exportDir, name);
            OutputStream fos2 = null;
            try {
                fos2 = new FileOutputStream(file);
                fos2.write(byteArrayOutputStream.toByteArray());
            }
            finally {
                fos2.close();
            }
            index++;
            asd.size = index;

        }
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
            if (filename.contains(".zip"))
                continue;
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