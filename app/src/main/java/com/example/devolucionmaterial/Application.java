package com.example.devolucionmaterial;

/**
 * Created by EDGAR ARANA on 09/08/2017.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;
import android.util.Log;


import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class Application extends android.app.Application {
    private static final String TAG = Application.class.getSimpleName();


    public static OkHttpClient.Builder httpClient;


    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {

        } else {
            Fabric.with(this, new Crashlytics());
        }


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder();

        httpClient.connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES);

        httpClient.addInterceptor(logging);


    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("termino", "app");
    }
}
