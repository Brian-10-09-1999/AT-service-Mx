package com.example.devolucionmaterial.static_class;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

public class MensajeEnConsola {
    private static String TAG = "ZITRO";

    public static void log(Context context, String metodo, String mensaje){


        try {boolean showLogs = Boolean.valueOf(com.example.devolucionmaterial.Util.getProperty("ShowLogs", context));

             if(showLogs){
                 String[] clase;
                 clase = String.valueOf(context).split("@");
                 String claseMetodo = clase[0]+"."+metodo;


                 Log.d(TAG, ".\n"+claseMetodo+"\n"+mensaje);

             }

        }
        catch (IOException e){ e.printStackTrace();}


    }



    public static void logInfo(Context context, String metodo, String mensaje){

        try {boolean showLogs = Boolean.valueOf(com.example.devolucionmaterial.Util.getProperty("ShowLogs", context));

            if(showLogs){
                String[] clase;
                clase = String.valueOf(context).split("@");
                String claseMetodo = clase[0]+"."+metodo;


                Log.i(TAG, ".\n"+claseMetodo+"\n"+mensaje);

            }

        }
        catch (IOException e){ e.printStackTrace();}


    }

    public static void logError(Context context, String metodo, String mensaje){

        try {boolean showLogs = Boolean.valueOf(com.example.devolucionmaterial.Util.getProperty("ShowLogs", context));

            if(showLogs){
                String[] clase;
                clase = String.valueOf(context).split("@");
                String claseMetodo = clase[0]+"."+metodo;


                Log.e(TAG, ".\n"+claseMetodo+"\n"+mensaje);

            }

        }
        catch (IOException e){ e.printStackTrace();}


    }





}
