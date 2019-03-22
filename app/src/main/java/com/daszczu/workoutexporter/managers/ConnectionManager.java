package com.daszczu.workoutexporter.managers;

import com.daszczu.workoutexporter.dto.WorkoutToUpload;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

public class ConnectionManager {

    public Response post(String url, Map<String, String> data, Map<String, String> headers, Map<String, String> cookies, String referer, WorkoutToUpload wtu) throws IOException {
        Connection conn = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0")
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .timeout(0)
                .maxBodySize(0);

        if (referer != null && !referer.isEmpty()) {
            conn.referrer(referer);
        }

        if (data != null && !data.isEmpty()) {
            conn.data(data);
            conn.method(Method.POST);
        } else {
            conn.method(Method.GET);
        }

        if (cookies != null && !cookies.isEmpty()) {
            conn.cookies(cookies);
        }

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.header(entry.getKey(), entry.getValue());
            }
        }
        if (wtu != null) {
            conn.data("file", wtu.getFilename(), wtu.getInputStream());
        }

        Response res = conn.execute();
        if (cookies != null) {
            cookies.putAll(res.cookies());
        }
        return res;
    }

    public Response post(String url, Map<String, String> data, Map<String, String> headers, Map<String, String> cookies, String referer) throws IOException {
        return post(url, data, headers, cookies, referer, null);
    }


    public Response get(String url, Map<String, String> headers, Map<String, String> cookies, String referer) throws IOException {
        return post(url, null, headers, cookies, referer);
    }
}
