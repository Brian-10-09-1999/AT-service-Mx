package com.example.devolucionmaterial.data_base;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.example.devolucionmaterial.internet.JSON;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActualizaBDestatusDevMaterial extends IntentService {
    BDmanager BD;
    String metodo;
    Context context;
    String funcionControl_Tabla_Sala = "funcion=Actualiza_Estatus_Dev_Mat&tecnicoid=";

    public ActualizaBDestatusDevMaterial() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //http://pruebasisco.ddns.net:8082/Android/android.php?ejecuta=PRUEBAS&funcion=Actualiza_Estatus_Dev_Mat&tecnicoid=
        metodo = "onHandleIntent()";
        context = this;
        BD = new BDmanager(context);
        try {
            JSONObject object = JSON.load(context, metodo, funcionControl_Tabla_Sala+BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
            assert object != null;
            //para las salidas == cdevoculionAlmacen
            JSONArray array = object.getJSONArray("registros");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object1 = array.getJSONObject(i);
                BD.actualiza("UPDATE salidas SET statusSalida = '" + object1.getString("estatusc") + "' WHERE _id=" + object1.getString("idc") + "");
                BD.actualiza("UPDATE descripcionDeSalida SET estatus = '" + object1.getString("estatusd") + "' WHERE _id=" + object1.getString("idd") + "");
            }
        } catch (JSONException e) {
            MensajeEnConsola.log(context, metodo, "JSONException = " + e.getMessage());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception = " + e.getMessage());
        }
    }
}