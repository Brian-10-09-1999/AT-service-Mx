package com.example.devolucionmaterial.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.CrearPedido;
import com.example.devolucionmaterial.beans.PedidosGuardados;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.DBCrearPedido;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by EDGAR ARANA on 09/06/2017.
 */

public class SendPeddidosGuardados extends Service {
    AsyncTaskHora asyncTaskHora;
    private DBCrearPedido dbCrearPedido;
    private String idTecnico;


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbCrearPedido = new DBCrearPedido(getApplicationContext());
        dbCrearPedido.open();
        idTecnico = BDVarGlo.getVarGlo(getApplicationContext(), "INFO_USUARIO_ID");
        asyncTaskHora = new AsyncTaskHora();
        asyncTaskHora.execute();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // inicia el servico
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //termin el servicio
        asyncTaskHora.cancel(true);
    }

    private class AsyncTaskHora extends AsyncTask<String, String, Boolean> {


        @Override
        protected Boolean doInBackground(String... params) {


            try {

                for (int i = 0; i < dbCrearPedido.getListBackGround().size(); i++) {

                    final PedidosGuardados pg = dbCrearPedido.getListBackGround().get(i);

                    ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
                    Call<CrearPedido> call = serviceApi.sendCrearPedido(pg.getSala(), pg.getMaquina(), pg.getComponente(), idTecnico, pg.getFolio());
                    call.enqueue(new Callback<CrearPedido>() {
                        @Override
                        public void onResponse(Call<CrearPedido> call, Response<CrearPedido> response) {
                            if (response.body().getRespuesta() == 11) {

                                int upadet = dbCrearPedido.update(pg.getId(), 1);
                                Log.e("back update", upadet + "");

                            } else {
                                Log.e("error ", String.valueOf(response.body().getRespuesta()));
                            }


                        }

                        @Override
                        public void onFailure(Call<CrearPedido> call, Throwable t) {

                            Log.e("error", t + "");

                        }
                    });
                }
            } catch (Exception e) {
                Log.e("error procesos", String.valueOf(e));
            }


            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Boolean s) {
            stopService(new Intent(getApplicationContext(), SendPeddidosGuardados.class));
            Log.e("finish", String.valueOf(s));
            super.onPostExecute(s);
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();

        }
    }
}
