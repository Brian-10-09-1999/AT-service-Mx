package com.example.devolucionmaterial;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jonhGab lopez on 11/07/2018.
 */

public class Util {

    public static String getProperty(String key,Context context) throws IOException {
        Properties properties = new Properties();;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("Setup.properties");
        properties.load(inputStream);
        return properties.getProperty(key);

    }

}
