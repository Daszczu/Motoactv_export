package com.daszczu.workoutexporter;

public class NoActivityDataException extends Exception {

    private final int workoutActivityId;

    public NoActivityDataException(int workoutActivityId) {
        super();
        this.workoutActivityId = workoutActivityId;
    }

    public int getWorkoutActivityId() {
        return workoutActivityId;
    }
}
