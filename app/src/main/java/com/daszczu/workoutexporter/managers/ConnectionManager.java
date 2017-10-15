package com.daszczu.workoutexporter.managers;

import com.daszczu.workoutexporter.dto.WorkoutToUpload;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

public class ConnectionManager {

	public static Response post(String url, Map<String, String> data, Map<String, String> headers, Map<String, String> cookies, String referer, WorkoutToUpload wtu) throws IOException {
		Connection conn = Jsoup.connect(url)
							.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0")
							.ignoreHttpErrors(true)
							.ignoreContentType(true)
							.timeout(0)
							.maxBodySize(0);
		
		if (referer != null && !referer.isEmpty())
			conn.referrer(referer);
		
		if (data != null && !data.isEmpty()) {
			conn.data(data);
			conn.method(Method.POST);
		} 
		else
			conn.method(Method.GET);
		
		if (cookies != null && !cookies.isEmpty())
			conn.cookies(cookies);
		
		if (headers != null && !headers.isEmpty())	{	
			for (String key : headers.keySet())
				conn.header(key, headers.get(key));
			conn.header("Content-Type", "multipart/form-data");
		}
		if (wtu != null)
			conn.data("file", wtu.getFilename(), wtu.getInputStream());
//		conn.data("files[import-file]", wtu.getFilename(), wtu.getInputStream());
		
		Response res = null;
		try {
			res = conn.execute();
			if (cookies != null)
				cookies.putAll(res.cookies());
		} catch (IOException e) {
			throw e;
		}
		return res;
	}

	public static Response post(String url, Map<String, String> data, Map<String, String> headers, Map<String, String> cookies, String referer) throws IOException {
		return post(url, data, headers, cookies, referer, null);
	}
	

	public static Response get(String url, Map<String, String> headers, Map<String, String> cookies, String referer) throws IOException {
		Response res = post(url, null, headers, cookies, referer);
		return res;
	}

	private static String castInputToString(InputStream is) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while(result != -1) {
			buf.write((byte) result);
			result = bis.read();
		}
// StandardCharsets.UTF_8.name() > JDK 7
		return buf.toString("UTF-8");
	}
}
