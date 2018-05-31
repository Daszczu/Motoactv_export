package com.daszczu.workoutexporter.retrofit;

import com.daszczu.workoutexporter.dto.MotoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RetrofitConnection {
    @FormUrlEncoded
    @POST("session/login.json")
    Call<MotoResponse> login(@Field("screen_name") String login, @Field("password") String password, @Field("remember_me") String rememberMe);

    @FormUrlEncoded
    @POST("planning/saveWorkoutPlan.json")
    Call<MotoResponse> saveWorkoutPlan(@Field("workoutPlan") String workoutPlan, @Header("Cookie") String cookies);
}
