package com.daszczu.workoutexporter.async;

import com.daszczu.workoutexporter.retrofit.RetrofitCalls;

public interface AsyncResponse {
    void processFinish(RetrofitCalls retrofit);
}
