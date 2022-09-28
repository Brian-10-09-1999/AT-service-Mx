package com.example.devolucionmaterial.activitys.scrollDynamic.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiCallDragon {

    private static final String TAG = "DebugApiCall";
    //GET network request
    public static String GET(OkHttpClient client, HttpUrl url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.d(TAG, "REQUEST = "+request.toString());
        Response response = client.newCall(request).execute();
        return response.body().string();


    }



}
