package com.daszczu.workoutexporter.dto;

public class Routine {
    private int id;
    private int routeId;
    private int gpsPoints;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGpsPoints() {
        return gpsPoints;
    }

    public void setGpsPoints(int gpsPoints) {
        this.gpsPoints = gpsPoints;
    }

    @Override
    public String toString() {
        return name + " (" + gpsPoints + ")";
    }
}
