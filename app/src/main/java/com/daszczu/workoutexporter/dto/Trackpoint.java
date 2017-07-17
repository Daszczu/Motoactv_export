package com.daszczu.workoutexporter.dto;

public class Trackpoint {
	private long id;
	private long time;
	private double lon;
	private double lat;
	private double altitude;
	private double distance;
	private double speed;
	private double heartRate;
	private double cadence;

	public Trackpoint(long id, long time, double lon, double lat, double altitude, double distance, double speed, double heartRate, double cadence) {
		this.id = id;
		this.time = time;
		this.lon = lon;
		this.lat = lat;
		this.altitude = altitude;
		this.distance = distance;
		this.speed = speed;
		this.heartRate = heartRate;
		this.cadence = cadence;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long vTime) {
		this.time = vTime;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double vLong) {
		this.lon = vLong;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double vLat) {
		this.lat = vLat;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double vAltitude) {
		this.altitude = vAltitude;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double vDistance) {
		this.distance = vDistance;
	}

	public double getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(double vHeartRate) {
		this.heartRate = vHeartRate;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double vSpeed) {
		this.speed = vSpeed;
	}

	public double getCadence() {
		return cadence;
	}

	public void setCadence(double vCadence) {
		this.cadence = vCadence;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
