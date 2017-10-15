package com.daszczu.workoutexporter.managers;

import android.util.Log;

import com.daszczu.workoutexporter.ConnectionException;
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
	private final static int APP_ID = 10733;
	private final static String APP_SECRET = "4813c1e6da1db95ffb03bfe54ebafbd16b69a73d";
	private final static String CLIENT_SECRET = "83aaf0063a057943d835e10e2604ea51e4842915";
	private final static String FILE_TYPE = "tcx";
    private final static String AUTH_URL = "https://www.strava.com/oauth/token";
	private Gson gson = new Gson();
    private String token;

	public StravaManager() throws IOException {
        StravaExchangeTokenResponse response = getToken();
        this.token = response.getAccessToken();
	}

	private StravaExchangeTokenResponse getToken() throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("client_id", String.valueOf(APP_ID));
        data.put("client_secret", APP_SECRET);
        data.put("code", CLIENT_SECRET);

        Connection.Response res = ConnectionManager.post(AUTH_URL, data, null, new HashMap<String, String>(), null);

        return gson.fromJson(res.body(), StravaExchangeTokenResponse.class);

    }

	public StravaUploadResponse uploadActivity2(File file) throws ConnectionException {
        try {
            Map<String, String> headers = new HashMap<>();
            Map<String, String> data = new HashMap<>();
            WorkoutToUpload wtu = new WorkoutToUpload(file.getName(), new FileInputStream(file));

            String uploadURL = "https://www.strava.com/api/v3/uploads";
            headers.put("Authorization", "Bearer " + token);
            data.put("data_type", FILE_TYPE);

            Connection.Response res2 = ConnectionManager.post(uploadURL, data, headers, null, null, wtu);
            if (res2 == null) {
                Log.d("UploadActivity", token);
                throw new ConnectionException(0, "Response is null");
            }

            int statusCode = res2.statusCode();
            String body = res2.body();

            if (statusCode != 200 && statusCode != 400)
                throw new ConnectionException(statusCode, res2.parse().text());

            Log.d("TAG", body);
            return gson.fromJson(body, StravaUploadResponse.class);
        } catch (IOException e) {
            throw new ConnectionException(0, e.getLocalizedMessage());
        }
	}
}
