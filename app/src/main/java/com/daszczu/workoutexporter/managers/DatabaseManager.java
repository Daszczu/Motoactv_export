package com.daszczu.workoutexporter.managers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;

import com.daszczu.workoutexporter.NoActivityDataException;
import com.daszczu.workoutexporter.StringUtils;
import com.daszczu.workoutexporter.dto.Instance;
import com.daszczu.workoutexporter.dto.LapDetails;
import com.daszczu.workoutexporter.dto.Routine;
import com.daszczu.workoutexporter.dto.Trackpoint;
import com.daszczu.workoutexporter.dto.WorkoutActivity;
import com.daszczu.workoutexporter.mapsimporter.MapTrackPoint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.daszczu.workoutexporter.constants.Metrics.*;

public class DatabaseManager {
    private static final Uri CONTENT_URI_LAP_DETAILS = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/lap_details");
    private static final Uri CONTENT_URI_WORKOUT_DATA = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/workout_data");
    private static final Uri CONTENT_URI_LAST_WORKOUT = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/workout_last_details");
    private static final Uri CONTENT_URI_APGX = Uri.parse("content://com.motorola.gault.activity.providers.workoutrawcontentprovider/workout_activity_apgx");
    //public static final Uri CONTENT_URI_WORKOUT_ACTIVITY = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/view_workout_activity");
    public static final Uri CONTENT_URI_WORKOUT_ACTIVITY_SUMMARY_VIEW = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/view_workout_activity_summary");
    public static final Uri CONTENT_URI_WORKOUT_ACTIVITY = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/workout_activity");
    private static final Uri CONTENT_URI_WORKOUT_ACTIVITY_SUMMARY = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/workout_activity_summary");
    private static final Uri CONTENT_URI_WORKOUT_SUB_ACTIVITY = Uri.parse("content://com.motorola.gault.activity.providers.summarycontentprovider/workout_sub_activity");
    private static final Uri CONTENT_URI_ROUTES = Uri.parse("content://com.motorola.gault.activity.providers.routecontentprovider/routes");
    private static final Uri CONTENT_URI_CALENDAR_EVENTS = Uri.parse("content://com.android.calendar/events");

    private static final Uri CONTENT_URI_ROUTES_GPX = Uri.parse("content://com.motorola.gault.activity.providers.routecontentprovider/routes_gpx");
    private static final Uri CONTENT_URI_ROUTINE_ACTIVITIES = Uri.parse("content://com.motorola.gault.activity.providers.routinecontentprovider/routine_activities");
    private static final Uri CONTENT_URI_ROUTINES_VIEW = Uri.parse("content://com.motorola.gault.activity.providers.routinecontentprovider/view_rouitne_activity");
    private static final Uri CONTENT_URI_PLAN_WORKOUT_ENTRY = Uri.parse("content://com.motorola.gault.activity.providers.plancontentprovider/plan_workout_entry");

    private DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private ContentResolver CR;

    public DatabaseManager(ContentResolver CR) {
        this.CR = CR;
    }

    private DatabaseManager() {
    }

    private WorkoutActivity getWorkoutBaseInfo(int id) {
        Cursor cursor;
        int count;

        if (id == 0)
            cursor = this.CR.query(CONTENT_URI_WORKOUT_ACTIVITY, null, "", null, "_id desc");
        else
            cursor = this.CR.query(CONTENT_URI_WORKOUT_ACTIVITY, null, "_id = " + id, null, null);

        if (cursor != null && (count = cursor.getCount()) > 0)
            cursor.moveToNext();
        else
            return new WorkoutActivity();

        id = getInt(cursor, "_id");
        long startTime = getLong(cursor, "start_time");
        long endTime = getLong(cursor, "end_time");
        int activityTypeId = getInt(cursor, "activity_type_id");
        String activityType = StringUtils.getActivityType(activityTypeId);

        for (int i = 1; i < count; i++) {
            cursor.moveToNext();
            endTime = getLong(cursor, "end_time");
        }

        cursor.close();

        WorkoutActivity woa = new WorkoutActivity(id, startTime, endTime, activityTypeId, activityType);
        woa.setDistance(getMetric(id, METRIC_DISTANCE));
        woa.setCalories(getMetric(id, METRIC_CALORIES));
        woa.setAvgHeartRate(getMetric(id, METRIC_AVG_HR));
        woa.setMaxHeartRate(getMetric(id, METRIC_MAX_HR));
        woa.setDuration(getMetric(id, METRIC_DURATION));
        return woa;
    }

    public double getMetric(int workoutActivityId, int metricId) {
        Cursor cursor = CR.query(CONTENT_URI_WORKOUT_ACTIVITY_SUMMARY, null,
                "workout_activity_id = " + workoutActivityId + " and metric_id = " + metricId, null, null);

        if (cursor != null && cursor.getCount() > 0)
            cursor.moveToNext();
        else
            return 0;

        //cursor.moveToNext();
        double dMetric = getDouble(cursor, "summary_value");
        cursor.close();
        return dMetric;
    }

    public WorkoutActivity getFullWorkout(int workoutId) throws NoActivityDataException {
        WorkoutActivity woa = getWorkoutBaseInfo(workoutId);
        long startTime = woa.getStartTime();
        long endTime = woa.getEndTime();

//        Double avgHeart = 0D, maxHeart = 0D, maxSpeed = 0D, avgCadence = 0D;
//        int cadenceSize = 0, heartRateSize = 0;
        long stepsPrev = 0;
        long timePrev = 0;
        List<Trackpoint> trackPoints = new ArrayList<>();
        Cursor cursor = this.CR.query(CONTENT_URI_APGX, null, "time_of_day >= " + startTime + " and time_of_day <= " + endTime, null, null);

        if (cursor == null || cursor.getCount() == 0)
            throw new NoActivityDataException(workoutId);

        while (cursor.moveToNext()) {
//            long id = getLong(cursor, "_id");
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
                if (stepsPrev != 0 && timePrev != 0 && steps != stepsPrev) {
                    long stepsDiff = steps - stepsPrev;
                    long timeDiff = time - timePrev;
                    double secTimeDiff = timeDiff / 1000D;

                    cadence = stepsDiff / secTimeDiff * 60 / 2;
                }
//                cadence = steps / (time - startTime) * 1000D * 60;
                timePrev = time;
                stepsPrev = steps;
            }

//            maxHeart = maxHeart > heartRate ? maxHeart : heartRate;
//            maxSpeed = maxSpeed > speed ? maxSpeed : speed;


//            if (heartRate != 0) {
//                avgHeart += heartRate;
//                heartRateSize++;
//            }
//            if (cadence != 0) {
//                avgCadence += cadence;
//                cadenceSize++;
//            }

            //set avgs and maxes and calories
            Trackpoint tp = new Trackpoint(0, time, lon, lat, alt, distance, speed, heartRate, cadence);
            trackPoints.add(tp);
            if (woa.getTypeId() == 0)
                woa.setTypeId(getInt(cursor, "activity_type"));
        }

//        if (heartRateSize != 0)
//            avgHeart /= heartRateSize;
//        if (cadenceSize != 0)
//            avgCadence /= cadenceSize;

//        woa.setAvgCadence(avgCadence);
//        woa.setAvgHeartRate(avgHeart);
//        woa.setMaxHeartRate(maxHeart);
//        woa.setMaxSpeed(maxSpeed);
        woa.setTrackpoints(trackPoints);
        cursor.close();
        return woa;
    }

    public List<WorkoutActivity> getLastWorkouts(int size) {
        Cursor cur = this.CR.query(
                CONTENT_URI_WORKOUT_ACTIVITY_SUMMARY_VIEW,
                new String[] {"workout_activity_id", "summary_value", "start_time", "end_time"},
                "metric_id = 4",
                null,
                "workout_id desc limit " + size);

        if (cur == null || cur.getCount() == 0)
            return null;

        List<WorkoutActivity> workouts = new ArrayList<>();
        while(cur.moveToNext()) {
            WorkoutActivity workoutActivity = new WorkoutActivity();
            workoutActivity.setId(getInt(cur, "workout_activity_id"));
            workoutActivity.setDistance(getDouble(cur, "summary_value"));
            workoutActivity.setStartTime(getLong(cur, "start_time"));
            workoutActivity.setEndTime(getLong(cur, "end_time"));
            workouts.add(workoutActivity);
        }
        return workouts;
    }

    public Integer[] getAllWorkoutActivitiesIds() {
        Cursor cur = this.CR.query(
                CONTENT_URI_WORKOUT_ACTIVITY_SUMMARY_VIEW,
                new String[] {"workout_activity_id"},
                null,
                null,
                null);

        if (cur == null || cur.getCount() == 0)
            return null;
        Set<Integer> set = new HashSet<>();

        while(cur.moveToNext()) {
            set.add(getInt(cur, "workout_activity_id"));
        }

        cur.close();
        return set.toArray(new Integer[0]);
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

    public long saveEvent(String name, Date date) {
        ContentValues params = new ContentValues();
        params.put("_SYNC_DIRTY", 1);
        params.put("CALENDAR_ID", 1000);
        params.put("TITLE", name);
        params.put("DESCRIPTION", name);
        params.put("EVENTLOCATION", "");
        params.put("DTSTART", date.getTime());

        return 0;
    }

    public List<String> getCalendarEvents() {
        Cursor cursor = this.CR.query(
                CONTENT_URI_CALENDAR_EVENTS,
                new String[]{"_id", "title", "description", "dtstart", "dtend"},
                null,
                null,
                "dtstart DESC");

        if (cursor == null || cursor.getCount() == 0)
            return null;

        List<String> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = getInt(cursor, "_id");
            String title = getString(cursor, "title");
            String desc = getString(cursor, "description");
            long dtStart = getLong(cursor, "dtstart");
            long dtEnd = getLong(cursor, "dtend");
            Date start = new Date(dtStart);
            Date end = new Date(dtEnd);

            String row = String.format("%s, %s, %s, %s, %s", id, sdf.format(start), sdf.format(end), title, desc);
            data.add(row);
        }

        cursor.close();
        return data;
    }

    public List<String> getRoutes() {
        Cursor cursor = this.CR.query(
                CONTENT_URI_ROUTES,
                new String[]{"_id", "activity_id", "total_distance", "total_elevation_change", "route_name"},
                "activity_id != \"\"",
                null,
                "_id DESC");

        if (cursor == null || cursor.getCount() == 0)
            return null;

        List<String> routes = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = getInt(cursor, "_id");
            int activity_id = getInt(cursor, "activity_id");
            int distance = getInt(cursor, "total_distance");
            int elevation = getInt(cursor, "total_elevation_change");
            String name = getString(cursor, "route_name");

            routes.add(String.format("%s %s %sm %sm %s", id, activity_id, distance, elevation, name));
        }

        cursor.close();
        return routes;
    }

    public List<MapTrackPoint> getGPX(int routeId) {
        Cursor cursor = this.CR.query(
                CONTENT_URI_ROUTES_GPX,
                new String[]{"ALTITUDE", "LONGITUDE", "LATITUDE"},
                "ROUTE_ID = " + routeId,
                null,
                null);

        if (cursor == null || cursor.getCount() == 0)
            return null;

        List<MapTrackPoint> gpxs = new ArrayList<>();

        while (cursor.moveToNext()) {
            double lat = getDouble(cursor, "LATITUDE");
            double lon = getDouble(cursor, "LONGITUDE");
            double alt = getDouble(cursor, "ALTITUDE");
            MapTrackPoint gpx = new MapTrackPoint();
            gpx.setAlt(alt);
            gpx.setLat(lat);
            gpx.setLon(lon);
            gpxs.add(gpx);
        }

        return gpxs;
    }

    public int getGPXCount(int routeId) {
        Cursor cursor = this.CR.query(
                CONTENT_URI_ROUTES_GPX,
                new String[]{"count(*) AS COUNT"},
                "ROUTE_ID = " + routeId,
                null,
                null);

        if (!checkCursor(cursor))
            return 0;

        return getInt(cursor, "count");
    }

    private boolean checkCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0)
            return false;
        cursor.moveToNext();
        return true;
    }

    public int getLastRouteId() {
        Cursor cursor = this.CR.query(
                CONTENT_URI_ROUTES,
                new String[]{"_id"},
                null,
                null,
                "_id DESC");

        if (cursor == null || cursor.getCount() == 0)
            return 0;

        cursor.moveToNext();

        int routeId = getInt(cursor, "_id");

        cursor.close();
        return routeId;
    }

    public void addTracks(int routeId, List<MapTrackPoint> tracks) {
        int deleted = CR.delete(CONTENT_URI_ROUTES_GPX,"route_id = " + routeId, null) ;
        Log.d("Delete", String.format("deleted %s rows", deleted));

        for (MapTrackPoint track : tracks ) {
            ContentValues values = new ContentValues();
            values.put("ROUTE_ID", routeId);
            values.put("LATITUDE", track.getLat());
            values.put("LONGITUDE", track.getLon());
            values.put("ALTITUDE", track.getAlt());

            CR.insert(CONTENT_URI_ROUTES_GPX, values);
        }
    }

    public void addTrack(int routeId, MapTrackPoint track) {
        ContentValues values = new ContentValues();
        values.put("ROUTE_ID", routeId);
        values.put("LATITUDE", track.getLat());
        values.put("LONGITUDE", track.getLon());
        values.put("ALTITUDE", track.getAlt());

        CR.insert(CONTENT_URI_ROUTES_GPX, values);
    }

    public void removeTracks(int routeId) {
        int deleted = CR.delete(CONTENT_URI_ROUTES_GPX,"route_id = " + routeId, null) ;
        Log.d("GPS-Track", String.format("deleted %s rows from route %s", deleted, routeId));
    }

    public List<Instance> getInstances() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1);
        Date monthAgo = cal.getTime();
        cal.add(Calendar.MONTH, 2);
        Date monthInFuture = cal.getTime();

        String sURI = String.format("content://com.android.calendar/instances/when/%s/%s", monthAgo.getTime(), monthInFuture.getTime());
        Cursor cursor = this.CR.query(Uri.parse(sURI),
                new String[]{"_id", "event_id", "begin", "end", "startDay", "endDay"},
                null,
                null,
                "begin DESC limit 5");

        if (cursor == null || cursor.getCount() == 0) {
            return new ArrayList<>();
        }

        List<Instance> instances = new ArrayList<>();
        while (cursor.moveToNext()) {
            Instance instance = new Instance();
            instance.setId(getInt(cursor, "_id"));
            instance.setEventId(getInt(cursor, "event_id"));
            instance.setBegin(getLong(cursor, "begin"));
            instance.setEnd(getLong(cursor, "end"));
            instance.setStartDay(getLong(cursor, "startDay"));
            instance.setEndDay(getLong(cursor, "endDay"));
            setRoutine(instance);
            instances.add(instance);
        }
        cursor.close();
        return instances;
    }

    public void setRoutine(Instance instance) {
        int routineId = getRoutineId(instance.getEventId());
        Cursor cur = this.CR.query(
                CONTENT_URI_ROUTINES_VIEW,
                new String[]{"_id", "routine_name", "route_id"},
                "_id = " + routineId,
                null,
                null);

        if (cur == null || cur.getCount() == 0)
            return;

        cur.moveToNext();

        Routine routine = new Routine();
        routine.setId(getInt(cur, "_id"));
        routine.setRouteId(getInt(cur, "route_id"));
        routine.setName(getString(cur, "routine_name"));
        routine.setGpsPoints(getGPXCount(routine.getRouteId()));

        cur.close();
        instance.setRoutine(routine);
    }

    private int getRoutineId(int eventId) {
        Cursor cur = this.CR.query(
                CONTENT_URI_PLAN_WORKOUT_ENTRY,
                new String[] {"routine_id"},
                "event_id = " + eventId,
                null,
                null);

        if (cur == null || cur.getCount() == 0)
            return 0;

        cur.moveToNext();

        int routineId = getInt(cur, "routine_id");
        cur.close();
        return routineId;
    }

    public void printCursorLog(Cursor cursor) {
        Log.d("DB-Cursor", DatabaseUtils.dumpCursorToString(cursor));
    }
	
	public List<Routine> getRoutines() {
		Cursor cursor = this.CR.query(
			CONTENT_URI_ROUTINES_VIEW,
            new String[]{"_id", "routine_name", "route_id"},
			null,
			null,
			"_id DESC");
			
        if (cursor == null || cursor.getCount() == 0)
            return null;
		
		List<Routine> routines = new ArrayList<>();
		while(cursor.moveToNext()) {
			Routine routine = new Routine();
			routine.setId(getInt(cursor, "_id"));
			routine.setRouteId(getInt(cursor, "route_id"));
			routine.setName(getString(cursor, "routine_name"));
			routine.setGpsPoints(getGPXCount(routine.getRouteId()));
			routines.add(routine);
		}

        cursor.close();
		return routines;
	}

	public void updateWorkoutRoutine(int eventId, int routineId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("routine_id", routineId);
        CR.update(
                CONTENT_URI_PLAN_WORKOUT_ENTRY,
                contentValues,
                "event_id = " + eventId,
                null);
    }

    public List<LapDetails> getLaps(int workoutId) {
        Cursor cursor = CR.query(CONTENT_URI_LAP_DETAILS, null, "workout_activity_id = " + workoutId, null, null);

        if (cursor == null || cursor.getCount() == 0)
            return null;

        List<LapDetails> laps = new ArrayList<>();
        while(cursor.moveToNext()) {
            LapDetails lap = new LapDetails();
            double lapSpeed = cursor.getDouble(cursor.getColumnIndex("speed"));
            double lapDistance = cursor.getDouble(cursor.getColumnIndex("distance"));
            long lapDuration = cursor.getLong(cursor.getColumnIndex("duration"));
            double lapCalorie = cursor.getDouble(cursor.getColumnIndex("calorie"));
            double lapHR = cursor.getDouble(cursor.getColumnIndex("heart_rate"));
            double lapCadence = cursor.getDouble(cursor.getColumnIndex("step_rate"));

            lap.setDistance(lapDistance);
            lap.setAvgHeartRate(lapHR);
            lap.setCadence(lapCadence);
            lap.setMaxSpeed(lapSpeed);
            lap.setDuration(lapDuration);
            lap.setCalories(lapCalorie);
            laps.add(lap);
        }
        cursor.close();
        return laps;
    }
}