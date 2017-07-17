package com.daszczu.workoutexporter.managers;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.daszczu.workoutexporter.StringUtils;
import com.daszczu.workoutexporter.dto.Trackpoint;
import com.daszczu.workoutexporter.dto.WorkoutActivity;

import java.util.ArrayList;
import java.util.List;
import static com.daszczu.workoutexporter.constants.Metrics.*;

public class DatabaseManager {
    public static final Uri CONTENT_URI_LAP_DETAILS = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/lap_details");
    public static final Uri CONTENT_URI_WORKOUT_DATA = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/workout_data");
    public static final Uri CONTENT_URI_LAST_WORKOUT = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/workout_last_details");
    public static final Uri CONTENT_URI_APGX = Uri.parse("content://com.motorola.gault.activity.providers.workoutrawcontentprovider/workout_activity_apgx");
    //public static final Uri CONTENT_URI_WORKOUT_ACTIVITY = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/view_workout_activity");
    public static final Uri CONTENT_URI_WORKOUT_ACTIVITY = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/workout_activity");
    public static final Uri CONTENT_URI_WORKOUT_ACTIVITY_SUMMARY = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/workout_activity_summary");
    public static final Uri CONTENT_URI_WORKOUT_SUB_ACTIVITY = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/workout_sub_activity");


    private ContentResolver CR;

    public DatabaseManager(ContentResolver CR) {
        this.CR = CR;
    }

    public WorkoutActivity getWorkoutBaseInfo(int id) {
        Cursor cursor;
        if (id == 0)
            cursor = this.CR.query(CONTENT_URI_WORKOUT_ACTIVITY, null, "", null, "_id desc");
        else
            cursor = this.CR.query(CONTENT_URI_WORKOUT_ACTIVITY, null, "_id = " + id, null, null);

        cursor.moveToNext();

        id = getInt(cursor, "_id");
        long startTime = getLong(cursor, "start_time");
        long endTime = getLong(cursor, "end_time");
        int activityTypeId = getInt(cursor, "activity_type_id");
        String activityType = StringUtils.getActivityType(activityTypeId);
        cursor.close();

        WorkoutActivity woa = new WorkoutActivity(id, startTime, endTime, activityTypeId, activityType);
        woa.setDistance(getMetric(id, METRIC_DISTANCE));
        woa.setCalories(getMetric(id, METRIC_CALORIES));
        woa.setAvgHeartRate(getMetric(id, METRIC_AVG_HR));
        woa.setMaxHeartRate(getMetric(id, METRIC_MAX_HR));
        woa.setDuration(getMetric(id, METRIC_DURATION));
        return woa;
    }

    public double getMetric(int workoutId, int metricId) {
        Cursor csum = CR.query(CONTENT_URI_WORKOUT_ACTIVITY_SUMMARY, null,
                "workout_activity_id = " + workoutId + " and metric_id = " + metricId, null, null);
        csum.moveToNext();
        double dMetric = getDouble(csum, "summary_value");
        csum.close();
        return dMetric;
    }

    public WorkoutActivity getFullWorkout(int workoutId) {
        WorkoutActivity woa = getWorkoutBaseInfo(workoutId);
        long startTime = woa.getStartTime();
        long endTime = woa.getEndTime();

        Double avgHeart = 0D, maxHeart = 0D, maxSpeed = 0D, avgCadence = 0D;
        int cadenceSize = 0, heartRateSize = 0;
        long stepsPrev = 0;
        long timePrev = 0;
        List<Trackpoint> trackPoints = new ArrayList<>();
        Cursor cursor = this.CR.query(CONTENT_URI_APGX, null, "time_of_day >= " + startTime + " and time_of_day <= " + endTime, null, null);
        while (cursor.moveToNext()) {
            long id = getLong(cursor, "_id");
            long time = getLong(cursor, "time_of_day");
            double lat = getDouble(cursor, "latitude");
            double lon = getDouble(cursor, "longitude");
            double alt = getDouble(cursor, "elevation");
            double distance = getDouble(cursor, "distance");
            double speed = getDouble(cursor, "speed");
            double heartRate = getDouble(cursor, "heart_rate");
            long steps = getLong(cursor, "steps");
            Double cadence = getDouble(cursor, "cadence");
            if (cadence.equals(0.0D) && steps != 0) {
                if (stepsPrev != 0 && timePrev != 0) {
                    long stepsDiff = steps - stepsPrev;
                    long timeDiff = time - timePrev;
                    double secTimeDiff = timeDiff / 1000D;

                    cadence = stepsDiff / secTimeDiff * 60;
                }
//                cadence = steps / (time - startTime) * 1000D * 60;
                timePrev = time;
                stepsPrev = steps;
            }

//            maxHeart = maxHeart > heartRate ? maxHeart : heartRate;
            maxSpeed = maxSpeed > speed ? maxSpeed : speed;


//            if (heartRate != 0) {
//                avgHeart += heartRate;
//                heartRateSize++;
//            }
            if (cadence != 0) {
                avgCadence += cadence;
                cadenceSize++;
            }

            //set avgs and maxes and calories
            Trackpoint tp = new Trackpoint(id, time, lon, lat, alt, distance, speed, heartRate, cadence);
            trackPoints.add(tp);
            if (woa.getTypeId() == 0)
                woa.setTypeId(getInt(cursor, "activity_type"));
        }

//        if (heartRateSize != 0)
//            avgHeart /= heartRateSize;
        if (cadenceSize != 0)
            avgCadence /= cadenceSize;

        woa.setAvgCadence(avgCadence);
//        woa.setAvgHeartRate(avgHeart);
//        woa.setMaxHeartRate(maxHeart);
        woa.setMaxSpeed(maxSpeed);
        woa.setTrackpoints(trackPoints);
        return woa;
    }


    private long getLong(Cursor cur, String columnName) {
        return cur.getLong(cur.getColumnIndex(columnName));
    }

    private int getInt(Cursor cur, String name) {
        return cur.getInt(cur.getColumnIndex(name));
    }

    private double getDouble(Cursor cur, String name) {
        return cur.getDouble(cur.getColumnIndex(name));
    }

    private String getString(Cursor cur, String name) {
        return cur.getString(cur.getColumnIndex(name));
    }
}