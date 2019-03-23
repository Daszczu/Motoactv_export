package com.daszczu.workoutexporter.dto;

import com.daszczu.workoutexporter.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WorkoutActivity {
    private int id;
    private long startTime;
    private long endTime;
    private int typeId;
    private String type;
    private double distance;
    private double avgCadence;
    private double avgHeartRate;
    private double maxHeartRate;
    private double maxSpeed;
    private double calories;
    private double duration;
    private List<Trackpoint> trackpoints;

    private DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.ENGLISH);
    private DateFormat simpleDF = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public WorkoutActivity() {
    }

    public WorkoutActivity(int id, long startTime, long endTime, int typeId, String type) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.typeId = typeId;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
        this.type = StringUtils.getActivityType(typeId);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<Trackpoint> getTrackpoints() {
        return trackpoints;
    }

    public void setTrackpoints(List<Trackpoint> trackpoints) {
        this.trackpoints = trackpoints;
    }

    public double getAvgCadence() {
        return avgCadence;
    }

    public void setAvgCadence(double avgCadence) {
        this.avgCadence = avgCadence;
    }

    public double getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(double avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }

    public double getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(double maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        simpleDF.setTimeZone(TimeZone.getTimeZone("GMT"));
        String timeDuration = prepareTimeDuration(simpleDF.format(endTime - startTime));

        return String.format(Locale.ENGLISH, "%s %n\r%.2fkm \t%s",
                df.format(startTime),
                distance/1000,
                timeDuration);
    }

    private String prepareTimeDuration(String timeDuration) {
        if (timeDuration.startsWith("0") || timeDuration.startsWith(":")) {
            timeDuration = timeDuration.substring(1);
            return prepareTimeDuration(timeDuration);
        } else {
            return timeDuration;
        }
    }
}