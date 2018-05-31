package com.daszczu.workoutexporter.retrofit;

import com.google.gson.annotations.SerializedName;

public class MotoLoginRequest {
    @SerializedName("login")
    private String mLogin;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("remember_me")
    private String mRememberMe;

    public MotoLoginRequest(String mLogin, String mPassword, String mRememberMe) {
        this.mLogin = mLogin;
        this.mPassword = mPassword;
        this.mRememberMe = mRememberMe;
    }

    public String getmLogin() {
        return mLogin;
    }

    public void setmLogin(String mLogin) {
        this.mLogin = mLogin;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmRememberMe() {
        return mRememberMe;
    }

    public void setmRememberMe(String mRememberMe) {
        this.mRememberMe = mRememberMe;
    }
}
