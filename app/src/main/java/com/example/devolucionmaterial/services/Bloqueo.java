package com.example.devolucionmaterial.services;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.viaticos.Activity_Lista;

public class Bloqueo {
    private static Context context;
    private static  String metodo;

    public static void BDUpdates(Context context1){
        context = context1;
        new BDUpdatesAsy().execute();
    }
    private static class BDUpdatesAsy extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);

            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            metodo = "Bloqueo<>doInBackground()";
            //revisando servicios
            int numero_servicios = 4;
            String OK1 = "NO", OK2 = "NO", OK3 = "NO", OK4 = "NO";
            do{
                if(!CheckService.isRunningService(context, "ControlTotalLocalxRemoto") && "NO".equals(OK1)){
                    numero_servicios--;
                    OK1 = "SI";
                }
                if(!CheckService.isRunningService(context, "ActualizaBDcestatus") && "NO".equals(OK2)){
                    numero_servicios--;
                    OK2 = "SI";
                }
                if(!CheckService.isRunningService(context, "ActualizaBDcrefacciones") && "NO".equals(OK3)){
                    numero_servicios--;
                    OK3 = "SI";
                }
                if(!CheckService.isRunningService(context, "ActualizaBDcsala") && "NO".equals(OK4)){
                    numero_servicios--;
                    OK4 = "SI";
                }
                //MensajeEnConsola.log(context, metodo, "numero_servicios -> "+numero_servicios);
            }while(numero_servicios != 0);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
        }
    }
}
