package com.example.devolucionmaterial.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;

import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.Notificacion;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.internet.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FolioAsignadoService extends Service{
    String metodo;
    Context context;
    private String folioEnvia="1";
    int bandera=0;
	public FolioAsignadoService(){}
	@Override
	public IBinder onBind(Intent intent) {return null;}

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //startService(new Intent(context, FolioAsignadoService.class));
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TiempoInfinito();
        return START_STICKY;
    }
    void TiempoInfinito(){
        metodo = "TiempoInfinito()";
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ProcesoParaSincronizacion();
                TiempoInfinito();
            }
        }, 60000);
    }
    void ProcesoParaSincronizacion(){
        metodo = "ProcesoParaSincronizacion()";
        //MensajeEnConsola.log(context, metodo, "T actual -> "+BDVarGlo.getVarGlo(context, "TIEMPO_ACTUAL_FOLIOS_ASIGNADOS")+ " && T sincroniza -> "+BDVarGlo.getVarGlo(context, "TIEMPO_SINCRONIZACIONES"));
        if (Integer.valueOf(BDVarGlo.getVarGlo(context, "TIEMPO_ACTUAL_FOLIOS_ASIGNADOS")) >= Integer.valueOf(BDVarGlo.getVarGlo(context, "TIEMPO_SINCRONIZACIONES")) && CheckService.isOnline(context)) {
            BDVarGlo.setVarGlo(context, "TIEMPO_ACTUAL_FOLIOS_ASIGNADOS", "0");
            new SolicitudDatosURL().execute();
        } else {
            int _int = Integer.parseInt(BDVarGlo.getVarGlo(context, "TIEMPO_ACTUAL_FOLIOS_ASIGNADOS"));
            _int++;
            BDVarGlo.setVarGlo(context, "TIEMPO_ACTUAL_FOLIOS_ASIGNADOS", String.valueOf(_int));
        }
    }
    class SolicitudDatosURL extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            metodo = "SolicitudDatosURL<>.doInBackground()";
            //MensajeEnConsola.log(context, metodo, "");
            if(bandera==1){
                folioEnvia="1";
                bandera=0;
            }
            try{
                JSONObject object = JSON.load(context, metodo,
                    BDVarGlo.getVarGlo(context, "FUNCTION_NUEVOS_FOLIOS_ASIGNADOS")+
                    "&tecnicoid="+BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID")+
                    "&folios="+folioEnvia);
                assert object != null;
                if(!"0".equals(object.getString("cantidad_folios"))) {
                    JSONArray jsonArray = object.optJSONArray("folios_nuevos");
                    folioEnvia = "";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                        folioEnvia = folioEnvia + jsonArrayChild.optString("folio") + "!";
                    }
                    //MensajeEnConsola.log(context, metodo, "folioEnvia = " + folioEnvia);
                    Notificacion.notifica(context, android.R.drawable.ic_dialog_info, R.drawable.foliopend, "Atención de Incidencia", "¡Nuevo Folio Asignado!", "Entra para ver la incidencia.");
                    int _int = Integer.parseInt(BDVarGlo.getVarGlo(context, "INFO_USUARIO_NOTIFICACION_NUMERO_TOTAL"));
                    _int++;
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_NOTIFICACION_NUMERO_TOTAL", String.valueOf(_int));
                    Notificacion.shortcut_Badger(context, Integer.parseInt(BDVarGlo.getVarGlo(context, "INFO_USUARIO_NOTIFICACION_NUMERO_TOTAL")));
                    bandera = 1;
                }
            }catch(JSONException e) {
                MensajeEnConsola.log(context, metodo, "JSONException valor e = "+e.getMessage());
            }catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Exception valor e = "+e.getMessage());
            }
            return null;
        }
    }
}
//160