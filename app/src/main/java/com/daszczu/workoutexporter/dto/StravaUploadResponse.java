package com.daszczu.workoutexporter.dto;

import com.google.gson.annotations.SerializedName;

public class StravaUploadResponse {
    private Long id;
    @SerializedName("activity_id")
    private String activityId;
    @SerializedName("external_id")
    private String externalId;
    private String status;
    private String error;

    public StravaUploadResponse() {
    }


    public StravaUploadResponse(String status, String error) {
        this.status = status;
        this.error = error;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
