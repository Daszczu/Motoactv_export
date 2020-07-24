package com.daszczu.workoutexporter.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.daszczu.workoutexporter.retrofit.RetrofitCalls;

import java.io.IOException;


public class NewWorkoutPlannerAsync extends AsyncTask<Void, String, Void> {

    private static final String TAG = "NewPlanAsync";
    private AsyncResponse asyncResponse;
    private ProgressDialog dialog;
    private Context ctx;
    private RetrofitCalls retrofit;

    public NewWorkoutPlannerAsync(Context ctx, AsyncResponse asyncResponse) {
        this.dialog = new ProgressDialog(ctx);
        this.asyncResponse = asyncResponse;
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        dialog.setTitle("Connecting to MOTOACTV");
        dialog.setMessage("Establishing connection...");
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... plans) {
        try {
            retrofit = new RetrofitCalls(ctx);
            publishProgress("Logging into account...");
            retrofit.login();
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        dialog.setMessage(values[0]);
        dialog.show();
    }

    @Override
    protected void onPostExecute(Void result) {
        this.dialog.hide();
        this.asyncResponse.processFinish(retrofit);
    }
}
