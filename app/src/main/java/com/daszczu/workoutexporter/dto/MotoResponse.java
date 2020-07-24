package com.daszczu.workoutexporter.dto;

public class MotoResponse {
    private String routineId;
    private String workoutPlanId;
    private String return_url;
    private String code;

    public String getRoutineId() {
        return routineId;
    }

    public void setRoutineId(String routineId) {
        this.routineId = routineId;
    }

    public String getWorkoutPlanId() {
        return workoutPlanId;
    }

    public void setWorkoutPlanId(String workoutPlanId) {
        this.workoutPlanId = workoutPlanId;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "MotoResponse{" +
                "routineId='" + routineId + '\'' +
                ", workoutPlanId='" + workoutPlanId + '\'' +
                ", return_url='" + return_url + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
