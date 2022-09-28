package com.example.devolucionmaterial.services;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.devolucionmaterial.data_base.ActualizaBDcestatus;
import com.example.devolucionmaterial.data_base.ActualizaBDcrefacciones;
import com.example.devolucionmaterial.data_base.ActualizaBDcsala;
import com.example.devolucionmaterial.data_base.ControlTotalLocalxRemoto;

public class CheckService {
    private static String metodo;
    public static boolean isOnline(Context context){
        metodo = "CheckService.isOnline()";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
    @SuppressWarnings("deprecation")
    private static String tipoRed(Context context){
        metodo = "CheckService.tipoRed()";

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return "wifi";
                }}}

        ConnectivityManager connectivity2 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity2 != null) {
            NetworkInfo info = connectivity2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return "datos";
                }}}

        return "sin internet";
    }
    public static boolean internet(Context context){
        metodo = "CheckService.internet()";
        if(isOnline(context)) {
            if (tipoRed(context).equals("datos")) {
                return true;
            } else if (tipoRed(context).equals("wifi")) {
                return true;
            } else if (tipoRed(context).equals("sin internet")) {
                return false;
            } else {
                return false;
            }
        }
        return false;
    }
    public static void startService(Context context){
    /*    if (!isRunningService("ServicePruebaEstresToServer")) {
            context.startService(new Intent(context, ServicePruebaEstresToServer.class));
        }
    */
    }
    public static boolean isRunningService(Context context, String servicio) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if("FolioAsignadoService".equals(servicio)) {
                if (FolioAsignadoService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }

            if("ControlTotalLocalxRemoto".equals(servicio)) {
                if (ControlTotalLocalxRemoto.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            if("ActualizaBDcestatus".equals(servicio)) {
                if (ActualizaBDcestatus.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            if("ActualizaBDcrefacciones".equals(servicio)) {
                if (ActualizaBDcrefacciones.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            if("ActualizaBDcsala".equals(servicio)) {
                if (ActualizaBDcsala.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
