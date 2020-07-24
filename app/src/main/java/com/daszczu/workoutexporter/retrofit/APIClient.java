package com.daszczu.workoutexporter.retrofit;

import android.content.Context;
import android.util.Log;
import com.daszczu.workoutexporter.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class APIClient {

    private static final String LOG = "MotoAPI";

    private APIClient() {
    }

    public static Retrofit getClient(Context ctx) {

        long startTime = System.nanoTime();
        OkHttpClient client = new OkHttpClient.Builder()
//                .connectionSpecs(Collections.singletonList(ConnectionSpec.CLEARTEXT))
                .connectTimeout(Integer.MAX_VALUE - 1L, TimeUnit.MILLISECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
//                .followRedirects(false)
                .build();

        long clientBuild = System.nanoTime();
        Log.d(LOG, String.format("Created OkHttpClient, it took %f seconds", (clientBuild - startTime)/1000000000d));

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(ctx.getString(R.string.moto_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }
}
