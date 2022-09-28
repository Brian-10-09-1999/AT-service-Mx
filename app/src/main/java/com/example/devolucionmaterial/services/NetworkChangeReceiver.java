package com.example.devolucionmaterial.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by EDGAR ARANA on 09/06/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            //Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            context.startService(new Intent(context, SendPeddidosGuardados.class));
            context.startService(new Intent(context, SendPedidosQrSave.class));



        } else {
            //Toast.makeText(context, "Desconect", Toast.LENGTH_SHORT).show();
        }




       /* if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {


            Toast.makeText(context, "Network Available", Toast.LENGTH_SHORT).show();
        }*/


    }
}