package com.daszczu.workoutexporter;

public class NoActivityDataException extends Exception {

    public NoActivityDataException() {
        super();
    }

    public NoActivityDataException(int workoutActivityId) {
        super();
        this.workoutActivityId = workoutActivityId;
    }

    private int workoutActivityId;

    public int getWorkoutActivityId() {
        return workoutActivityId;
    }
}
