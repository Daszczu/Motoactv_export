package com.daszczu.workoutexporter.retrofit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.daszczu.workoutexporter.R;
import com.daszczu.workoutexporter.dto.MotoResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Response;

public class RetrofitCalls {
    private RetrofitConnection retrofitConnection;
    private Context ctx;
    private Headers headers;
    private String motoLogin;
    private String motoPassword;

    public RetrofitCalls(Context ctx) {
        this.ctx = ctx;
        this.motoLogin = ctx.getResources().getString(R.string.moto_login);
        this.motoPassword = ctx.getResources().getString(R.string.moto_pass);
        retrofitConnection = APIClient.getClient(ctx).create(RetrofitConnection.class);
    }

    public void login() throws IOException {
        if (!checkOrEnableInternet())
            return;

        MotoLoginRequest request = new MotoLoginRequest(motoLogin, motoPassword, "1");

        Call<MotoResponse> call = retrofitConnection.login(request.getmLogin(), request.getmPassword(), request.getmRememberMe());
        Response<MotoResponse> response = call.execute();
        headers = response.headers();
    }

    public void saveWorkoutPlan(WorkoutPlan workoutPlan) throws ParseException {
        if (!checkOrEnableInternet())
            return;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        WorkoutPlan workoutPlan = getExampleWorkoutPlan();

        String cookies = headers.get("Set-Cookie");//.split(";")[0];
        Log.d("RetrofitCall", gson.toJson(workoutPlan));
        Call<MotoResponse> call = retrofitConnection.saveWorkoutPlan(gson.toJson(workoutPlan), cookies);

        try {
            Response<MotoResponse> response = call.execute();
            Toast.makeText(ctx, response.toString(), Toast.LENGTH_LONG).show();
            MotoResponse motoResponse = response.body();
            String asd = "";
        } catch (IOException e) {
            Toast.makeText(ctx, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            Log.e("RetrofitCalls", e.getLocalizedMessage(), e);
        }

//        call.enqueue(new Callback<MotoResponse>() {
//            @Override
//            public void onResponse(Call<MotoResponse> call, Response<MotoResponse> response) {
//
//                Log.d("RetrofitCalls", "success");
//            }
//
//            @Override
//            public void onFailure(Call<MotoResponse> call, Throwable t) {
//
//                Log.d("RetrofitCalls", "failed");
//            }
//        });
    }

    private WorkoutPlan getExampleWorkoutPlan() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date dateStart = df.parse("2018-04-25 12:34:56");
        Date dateEnd = df.parse("2018-04-25 13:45:43");

        Stage stage = new Stage();
        stage.setIntensity("GENERAL_RUN");
        stage.setTrigger("DOUBLE_PRESS");
        stage.setRepeatCount(1);
        stage.setIsSet(false);

        List<Stage> stages = new ArrayList<>();
        stages.add(stage);

        Activity activity = new Activity();
        activity.setId("4");
        activity.setStages(stages);

        List<Activity> activities = new ArrayList<>();
        activities.add(activity);

        WorkoutPlan plan = new WorkoutPlan();
        plan.setWorkoutPlanName("Nazwa workoutu");
        plan.setDescription("Opis jakis");
        plan.setStartDate(dateStart.getTime());
        plan.setStartTime("12:34");
        plan.setEndDate(dateEnd.getTime());
        plan.setRouteId("51b83dde-0c3e-42c4-9299-73d03c44fb0");
        plan.setAddToLibrary(false);
        plan.setEditAsLibraryItem(false);
        plan.setActivities(activities);

        return plan;
    }

    private boolean checkOrEnableInternet() {
        boolean isOnline = isOnline();
        if (isOnline)
            return true;
        else {
            enableWifi();
            try {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, 30);
                while (!isOnline && cal.getTimeInMillis() > new Date().getTime()) {
                    isOnline = isOnline();
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Log.w("RetrofitCalls", e);
            }
            return isOnline;
        }
    }

    private void enableWifi() {
        WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
