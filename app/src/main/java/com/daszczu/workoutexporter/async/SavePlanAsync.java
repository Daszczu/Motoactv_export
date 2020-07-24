package com.daszczu.workoutexporter.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Button;
import com.daszczu.workoutexporter.retrofit.RetrofitCalls;
import com.daszczu.workoutexporter.retrofit.WorkoutPlan;

public class SavePlanAsync extends AsyncTask<Void, String, String> {

    private RetrofitCalls retrofit;
    private ProgressDialog dialog;
    private WorkoutPlan plan;
    private Button sendBtn;

    public SavePlanAsync(WorkoutPlan plan, Context ctx, Button btn, RetrofitCalls retrofit) {
        this.dialog = new ProgressDialog(ctx);
        this.plan = plan;
        this.sendBtn = btn;
        this.retrofit = retrofit;
    }

    @Override
    protected void onPreExecute() {
        sendBtn.setBackgroundColor(Color.GRAY);
        dialog.setTitle("Saving plan");
        dialog.setMessage("Saving...");
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        return retrofit.saveWorkoutPlan(plan);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            sendBtn.setBackgroundColor(Color.RED);
        } else {
            sendBtn.setBackgroundColor(Color.GREEN);
        }
        this.dialog.hide();
    }
}
