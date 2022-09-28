package com.example.devolucionmaterial.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;


import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model.Datos;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.dbPedidosQr.DBPedidoQrManger;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by EDGAR ARANA on 17/08/2017.
 */

public class SendPedidosQrSave extends Service {

    AsyncTaskHora asyncTaskHora;
    private DBPedidoQrManger dbPedidoQrManger;
    private String idTecnico;

    private String componenteNuevo, componenteAnterior, codMaquina;
    private int pedido;


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbPedidoQrManger = new DBPedidoQrManger(getApplicationContext()).open();
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
            List<Datos> list = dbPedidoQrManger.getListPedidosGuardados();

            try {
                if (list.isEmpty()) {
                    return true;
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        Datos datos = list.get(i);
                        if (datos.getStatus() == 1) {
                            //se neceita enviar
                            sendData(datos);
                        }
                    }

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
            stopService(new Intent(getApplicationContext(), SendPedidosQrSave.class));
            Log.e("finish pedido qr", String.valueOf(s));
            super.onPostExecute(s);
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();

        }
    }

    void sendData(Datos datos) {
        pedido = Integer.parseInt(datos.getPedido());
        componenteNuevo = datos.getComponenteNuevo();
        componenteAnterior = datos.getComponenteAnterior();
        codMaquina = datos.getCodMaquina();

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanResponse> call = serviceApi.sendValidationPedido(
                progressData(codMaquina)[0], componenteAnterior, componenteNuevo, BDVarGlo.getVarGlo(this, "INFO_USUARIO_ID"), pedido);
        call.enqueue(new Callback<BeanResponse>() {
            @Override
            public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                // TODO: 28/08/2017 solo se usa result de este bean
                if (response.body().getResult() == 1) {
                    dbPedidoQrManger.updatePedido(pedido, 2);
                }

            }

            @Override
            public void onFailure(Call<BeanResponse> call, Throwable t) {

            }
        });
    }

    // TODO: 03/02/2017 corta la cadena en un array
    String[] progressData(String dataQr) {
        String temp[] = null;
        try {
            String delimiter = ";";
            temp = dataQr.split(delimiter);
        } catch (Exception e) {
            Log.e("error progresdata ", "");
        }
        return temp;

    }

}
