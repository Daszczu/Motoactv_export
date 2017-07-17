package com.daszczu.workoutexporter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;

import com.daszczu.workoutexporter.constants.Template;
import com.daszczu.workoutexporter.dto.Trackpoint;
import com.daszczu.workoutexporter.dto.WorkoutActivity;
import com.daszczu.workoutexporter.managers.DatabaseManager;
import com.daszczu.workoutexporter.managers.FileManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SyncTools {
    private Context ctx;
    private DatabaseManager dbClient;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());

    public SyncTools (Context ctx) {
        this.ctx = ctx;
        this.df.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.dbClient = new DatabaseManager(ctx.getContentResolver());
    }

    public WorkoutActivity getWorkout(int id) {
        return dbClient.getFullWorkout(id);
    }

    public File saveWorkoutToFile(WorkoutActivity woa) {
        String sWorkoutDate = df.format(woa.getStartTime());

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        String filename = String.format(Locale.getDefault(), "kd_workout_%s.%s", woa.getId(), "TCX");
        File file = new File(exportDir, filename);

        FileManager fm = new FileManager();

        fm.append(Template.getBegining(),
                StringUtils.prepareActivityType(woa.getTypeId(), 1),
                StringUtils.prepareId(sWorkoutDate, 2),
                StringUtils.prepareLapTime(sWorkoutDate),
                StringUtils.prepareTotalTime(woa.getDuration() == 0.0 ? (woa.getEndTime() - woa.getStartTime()) / 1000D : woa.getDuration()),
                StringUtils.prepareDistance(woa.getDistance()),
                "<Intensity>Active</Intensity>",
                "<TriggerMethod>Manual</TriggerMethod>",
                "<Track>");

        for (Trackpoint s : woa.getTrackpoints())
            fm.append(
                    "<Trackpoint>",
                    StringUtils.prepareTime(df.format(new Date(s.getTime()))),
                    StringUtils.preparePosition(s.getLat(), s.getLon()),
                    StringUtils.prepareAltitude(s.getAltitude()),
                    StringUtils.prepareDistance(s.getDistance()),
                    StringUtils.prepareHeart(s.getHeartRate()),
                    StringUtils.prepareSpeed(s.getSpeed()),
                    StringUtils.prepareCadence(s.getCadence()),
                    "</Trackpoint>");

        fm.append(
                "</Track>",
                StringUtils.prepareMaxSpeed(woa.getMaxSpeed()),
                StringUtils.prepareCalories(woa.getCalories()),
                StringUtils.prepareAvgHeart(woa.getAvgHeartRate()),
//                StringUtils.prepareMaxHeart(woa.getMaxHeartRate()),
                StringUtils.prepareCadence(woa.getAvgCadence()),
                "</Lap>",
                "</Activity>",
                "</Activities>",
                "</TrainingCenterDatabase>");
        try {
            fm.save(new FileWriter(file));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public File fileExists(int id) {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        String filename = String.format(Locale.getDefault(), "kd_workout_%s.%s", id, "TCX");
        File file = new File(exportDir, filename);
        if (file.exists())
            return file;
        else
            return null;
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
}
