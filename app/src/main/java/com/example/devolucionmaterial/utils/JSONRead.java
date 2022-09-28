package com.example.devolucionmaterial.utils;

import android.util.Log;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by EDGAR ARANA on 14/06/2017.
 */

public class JSONRead {

    public String loadJSONFromAsset(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jObject = null;
        try {
            // Parse the data into jsonobject to get original data in form of json.
            jObject = new JSONArray(byteArrayOutputStream.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return jObject.toString();
    }

}
