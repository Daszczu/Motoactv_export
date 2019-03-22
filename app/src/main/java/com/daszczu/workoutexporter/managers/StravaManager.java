package com.daszczu.workoutexporter.managers;

import com.daszczu.workoutexporter.App;
import com.daszczu.workoutexporter.R;
import com.daszczu.workoutexporter.dto.StravaExchangeTokenResponse;
import com.daszczu.workoutexporter.dto.StravaUploadResponse;
import com.daszczu.workoutexporter.dto.WorkoutToUpload;
import com.google.gson.Gson;
import org.jsoup.Connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StravaManager {
    private static final String LOGIN_URL = "https://strava-proxy.herokuapp.com/strava/login?client_secret=%s&code=%s;";
    private static final String UPLOAD_URL = "https://strava-proxy.herokuapp.com/strava/upload?activity_type=%s";
    private static final String FILE_TYPE = "tcx";
    private Gson gson = new Gson();
    private String appSecret;
    private String clientSecret;
    private ConnectionManager connectionManager;

    public StravaManager() {
        appSecret = App.getContext().getString(R.string.strava_app_secret);
        clientSecret = App.getContext().getString(R.string.strava_client_secret);
        connectionManager = new ConnectionManager();
    }

    public StravaExchangeTokenResponse getToken() throws IOException {
        Connection.Response res = connectionManager.get(
                String.format(LOGIN_URL, appSecret, clientSecret),
                null,
                null,
                null);

        return gson.fromJson(res.body(), StravaExchangeTokenResponse.class);
    }

    public StravaUploadResponse uploadActivity(File file, String token) throws IOException {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> data = new HashMap<>();
        WorkoutToUpload wtu = new WorkoutToUpload(file.getName(), new FileInputStream(file));

        headers.put("Token", token);
        data.put("data_type", FILE_TYPE);

        Connection.Response response = connectionManager.post(UPLOAD_URL, data, headers, null, null, wtu);
        return gson.fromJson(response.body(), StravaUploadResponse.class);
    }
}
