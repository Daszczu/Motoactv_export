
package com.daszczu.workoutexporter.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stage {

    @SerializedName("intensity")
    @Expose
    private String intensity;
    @SerializedName("trigger")
    @Expose
    private String trigger;
    @SerializedName("repeat_count")
    @Expose
    private Integer repeatCount;
    @SerializedName("is_set")
    @Expose
    private Boolean isSet;

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Boolean getIsSet() {
        return isSet;
    }

    public void setIsSet(Boolean isSet) {
        this.isSet = isSet;
    }

}
