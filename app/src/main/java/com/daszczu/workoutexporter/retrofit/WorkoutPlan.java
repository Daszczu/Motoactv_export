
package com.daszczu.workoutexporter.retrofit;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WorkoutPlan {

    @SerializedName("workoutPlanName")
    @Expose
    private String workoutPlanName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("startDate")
    @Expose
    private Long startDate;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("endDate")
    @Expose
    private Long endDate;
    @SerializedName("routeId")
    @Expose
    private String routeId;
    @SerializedName("addToLibrary")
    @Expose
    private Boolean addToLibrary;
    @SerializedName("editAsLibraryItem")
    @Expose
    private Boolean editAsLibraryItem;
    @SerializedName("activities")
    @Expose
    private List<Activity> activities = null;

    public WorkoutPlan() {
    }

    public WorkoutPlan(String activityId) {
        Stage stage = new Stage();
        stage.setIntensity("GENERAL_RUN");
        stage.setTrigger("DOUBLE_PRESS");
        stage.setRepeatCount(1);
        stage.setIsSet(false);

        List<Stage> stages = new ArrayList<>();
        stages.add(stage);

        Activity activity = new Activity();
        activity.setStages(stages);
        activity.setId(activityId);

        activities = new ArrayList<>();
        activities.add(activity);
        addToLibrary = false;
        editAsLibraryItem = false;
    }

    public String getWorkoutPlanName() {
        return workoutPlanName;
    }

    public void setWorkoutPlanName(String workoutPlanName) {
        this.workoutPlanName = workoutPlanName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public Boolean getAddToLibrary() {
        return addToLibrary;
    }

    public void setAddToLibrary(Boolean addToLibrary) {
        this.addToLibrary = addToLibrary;
    }

    public Boolean getEditAsLibraryItem() {
        return editAsLibraryItem;
    }

    public void setEditAsLibraryItem(Boolean editAsLibraryItem) {
        this.editAsLibraryItem = editAsLibraryItem;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

}
