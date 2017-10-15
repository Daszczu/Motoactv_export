package com.daszczu.workoutexporter.dto;

/**
 * Created by Daszczu on 2017-09-12.
 */

public class GPX {
    private double lon;
    private double lat;
    private double alt;

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }
}
