package com.example.devolucionmaterial.static_class;

import android.content.Context;
import android.util.Log;

public class MensajeEnConsola {
    private static String TAG = "ZITRO";
    public static void log(Context context, String metodo, String mensaje){
        String[] clase;
        clase = String.valueOf(context).split("@");
        String claseMetodo = clase[0]+"."+metodo;
        Log.d(TAG, ".\n"+claseMetodo+"\n"+mensaje);
    }
}
