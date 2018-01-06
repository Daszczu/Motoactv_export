package com.daszczu.workoutexporter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

import com.daszczu.workoutexporter.constants.Template;
import com.daszczu.workoutexporter.dto.LapDetails;
import com.daszczu.workoutexporter.dto.Trackpoint;
import com.daszczu.workoutexporter.dto.WorkoutActivity;
import com.daszczu.workoutexporter.managers.DatabaseManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SyncTools {
    private Context ctx;
    private DatabaseManager dbClient;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
    private String tcxFolder = "tcx";

    public SyncTools(Context ctx) {
        this.ctx = ctx;
        this.df.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.dbClient = new DatabaseManager(ctx.getContentResolver());
    }

    public SyncTools(Context ctx, String tcxFolder) {
        this(ctx);
        this.tcxFolder = tcxFolder;
    }

    public WorkoutActivity getWorkout(int id) {
        return dbClient.getFullWorkout(id);
    }

    public File saveWorkoutToFile(WorkoutActivity woa, List<LapDetails> laps) {
        String sWorkoutDate = df.format(woa.getStartTime());

        File exportDir = new File(Environment.getExternalStorageDirectory(), tcxFolder);
        if (!exportDir.exists())
            exportDir.mkdir();

        String filename = String.format(Locale.getDefault(), "kd_workout_%s.%s", woa.getId(), "TCX");
        File file = new File(exportDir, filename);

        try {
            OutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            osw
                .append(Template.getBegining())
                .append(StringUtils.prepareActivityType(woa.getTypeId(), 1))
                .append(StringUtils.prepareId(sWorkoutDate, 2));

            int lapNumber = 0;
            LapDetails lapDetails = laps.get(lapNumber);
            lapDetails.setTime(woa.getStartTime());

            writeLapDetails(lapDetails, osw);
            long lapTime = lapDetails.getTime();
            long lapDuration = lapDetails.getDuration();

            for (Trackpoint s : woa.getTrackpoints()) {
                if (s.getTime() - lapTime > lapDuration) {
                    lapNumber++;
                    if (lapNumber != laps.size()) {
                        lapDetails = laps.get(lapNumber);
                        lapDuration = lapDetails.getDuration();

                        lapTime = s.getTime();
                        lapDetails.setTime(lapTime);

                        osw
                            .append("</Track>")
                            .append("</Lap>");
                        writeLapDetails(lapDetails, osw);
                    }
                }
                osw
                    .append("<Trackpoint>")
                    .append(StringUtils.prepareTime(df.format(new Date(s.getTime()))))
                    .append(StringUtils.preparePosition(s.getLat(), s.getLon()))
                    .append(StringUtils.prepareAltitude(s.getAltitude()))
                    .append(StringUtils.prepareDistance(s.getDistance()))
                    .append(StringUtils.prepareHeart(s.getHeartRate()))
                    .append(StringUtils.prepareSpeed(s.getSpeed()))
                    .append(StringUtils.prepareCadence(s.getCadence()))
                    .append("</Trackpoint>");
            }
            osw
                .append("</Track>")
                .append("</Lap>");

            osw
                .append("</Activity>")
                .append("</Activities>")
                .append("</TrainingCenterDatabase>");
            osw.flush();
            osw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("saveWorkoutToFile", "Workout id: " + woa.getId());
        }
        return file;
    }

    public void saveWorkoutToByteArrayOutputStream() {

    }
    private void writeLapDetails(LapDetails lapDetails, OutputStreamWriter osw) throws IOException {
        osw
            .append(StringUtils.prepareLapTime(df.format(lapDetails.getTime())))
            .append(StringUtils.prepareTotalTime(lapDetails.getDuration() / 1000))
            .append(StringUtils.prepareDistance(lapDetails.getDistance()))

            .append(StringUtils.prepareMaxSpeed(lapDetails.getMaxSpeed()))
            .append(StringUtils.prepareCalories(lapDetails.getCalories()))
            .append(StringUtils.prepareAvgHeart(lapDetails.getAvgHeartRate()))
            .append(StringUtils.prepareCadence(lapDetails.getCadence()))

            .append("<Intensity>Active</Intensity>")
            .append("<TriggerMethod>Manual</TriggerMethod>")
            .append("<Track>");
    }

    public File fileExists(int id) {
        File exportDir = new File(Environment.getExternalStorageDirectory(), tcxFolder);
        String filename = String.format(Locale.getDefault(), "kd_workout_%s.%s", id, "TCX");
        File file = new File(exportDir, filename);

        return file.exists() ? file : null;
    }

    public boolean checkOrEnableInternet() {
        boolean isOnline = isOnline();
        if (isOnline)
            return true;
        else {
            enableWifi();

            try {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, 30);
                while (!isOnline && cal.getTimeInMillis() > new Date().getTime()) {
                    isOnline = isOnline();
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return isOnline;
        }

    }

    private void enableWifi() {
        WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public List<LapDetails> getLaps(int workoutId) {
        return dbClient.getLaps(workoutId);
    }
}
