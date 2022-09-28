package com.example.devolucionmaterial.data_base;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.internet.JSON;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.Notificacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActualizaBDcsala extends IntentService {
    BDmanager BD;
    String metodo;
    Context context;
    String funcionControl_Tabla_Sala = "funcion=Control_Tabla_Sala";

    public ActualizaBDcsala() {
        super("ActualizaBDcsala");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int nuevos = 0;
        context = this;
        BD = new BDmanager(context);
        int viejos = BD.consulta("SELECT count(nombre) FROM csala", 0);
        metodo = "onHandleIntent()";
        try {
            JSONObject object = JSON.loadSalas(context, metodo, funcionControl_Tabla_Sala);
            assert object != null;
            JSONArray array = object.getJSONArray("registros");
            nuevos = array.length();
            for (int i = 0; i < nuevos; i++) {
                JSONObject object1 = array.getJSONObject(i);
                if ("SSS".equals(BD.consulta("SELECT officeID FROM csala WHERE officeID=" + object1.getString("OfficeID") + "", "SSS"))) {
                    BD.actualiza("INSERT INTO csala VALUES ('" + object1.getString("OfficeID") + "', '" + object1.getString("nombre") + "', '" + object1.getString("cliente") + "', " +
                            "'" + object1.getString("region") + "', '" + object1.getString("localizacion") + "', '" + object1.getString("regionidfk") + "', '" + object1.getString("salaid") + "')");
                    //MensajeEnConsola.log(context, metodo, "Office id insertado -> "+object1.getString("OfficeID"));
                } else {
                    BD.actualiza("UPDATE csala SET nombre='" + object1.getString("nombre") + "', cliente='" + object1.getString("cliente") + "', region='" + object1.getString("region") + "', " +
                            "localizacion='" + object1.getString("localizacion") + "', regionidfk=" + object1.getString("regionidfk") + ", salaID=" + object1.getString("salaid") + " WHERE officeID=" + object1.getString("OfficeID") + "");
                    //MensajeEnConsola.log(context, metodo, "Office id actualizado -> "+object1.getString("OfficeID"));
                }
            }
        } catch (JSONException e) {
            MensajeEnConsola.log(context, metodo, "JSONException = " + e.getMessage());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception = " + e.getMessage());
        }

        //Enviamos una notificacion para mostrar al usuario que se a actualizado su base de datos con nuevos registros
        MensajeEnConsola.log(context, metodo, "CSALA\nnuevos -> " + nuevos + "\nviejos -> " + viejos);
        if (nuevos > viejos) {
            int notificaNumero = nuevos - viejos;
            Notificacion.notifica(context, R.drawable.devolucion, R.drawable.devolucion, "BD Sala ha sido actualizada.",
                    "Se han agregado " + notificaNumero + " nuevas salas.",
                    "Entra para revizar las nuevas salas.");
        } else {
            Toast.makeText(context, "Tabla sala sincronizada", Toast.LENGTH_SHORT).show();
            //MensajeEnConsola.log(context, metodo, "Tabla salas ha sido actualizada");
            BD.actualiza("UPDATE control_total SET control = '" + BDVarGlo.getVarGlo(context, "VAR_BD_CONTROL_CSALA") + "' WHERE nombre = 'csala'");
        }
    }
}
/*
$arreglo["OfficeID"] = $fila["OfficeID"];
	$arreglo["nombre"] = $fila["nombre"];
	$arreglo["cliente"] = $fila["cliente"];
	$arreglo["region"] = $fila["region"];
	$arreglo["localizacion,"] = $fila["localizacion,"];
	$arreglo["regionidfk"] = $fila["regionidfk"];
	$arreglo["salaid"] = $fila["salaid"];
	"create table if not exists csala(
	officeID integer, nombre text, cliente text, region text, localizacion text, regionidfk integer, salaID integer)");

* */