package com.example.devolucionmaterial.data_base;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.internet.JSON;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.Notificacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActualizaBDcrefacciones extends IntentService{
    String metodo;
    Context context;
    BDmanager BD;
    String funcionControl_Tabla_Refacciones = "funcion=Control_Tabla_Refacciones";
    public ActualizaBDcrefacciones() {
        super("ActualizaBDcrefacciones");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        int nuevos = 0;
        BD = new BDmanager(this);
        int viejos = BD.consulta("SELECT count(nombre) FROM crefacciones",0);
        metodo = "onHandleIntent()";
        context = this;
        try {
            JSONObject object = JSON.load(context, metodo, funcionControl_Tabla_Refacciones);
            assert object != null;
            JSONArray array = object.getJSONArray("registros");
            nuevos = array.length();
            for (int i = 0; i < nuevos; i++) {
                JSONObject object1 = array.getJSONObject(i);
                if ("SSS".equals(BD.consulta("SELECT _idRef FROM crefacciones WHERE _idRef=" + object1.getInt("refaccionid") + "", "SSS"))) {
                    BD.actualiza("INSERT INTO crefacciones ( _id,_idRef,clave,nombre,status,sustituto,noSerie) " +
                            "VALUES (null, " + object1.getInt("refaccionid") + ", " + object1.getInt("clave") + ", '" + object1.getString("nombre") + "', " +
                            "" + object1.getInt("estatusidfk") + ", '" + object1.getString("suplente") + "', " + object1.getInt("necesitaSerie") + ")");
                }else{
                    BD.actualiza("UPDATE crefacciones SET clave="+object1.getInt("clave")+", nombre='"+object1.getString("nombre")+"', status="+object1.getInt("estatusidfk")+", sustituto='"+object1.getString("suplente")+"', noSerie="+object1.getInt("necesitaSerie")+" WHERE _idRef="+object1.getInt("refaccionid")+"");
                }
            }
        } catch (JSONException e) {
            MensajeEnConsola.log(context, metodo, "JSONException = " + e.getMessage());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception = " + e.getMessage());
        }

        MensajeEnConsola.log(context, metodo, "CREFACCION\nnuevos -> "+nuevos+"\nviejos -> "+viejos);
        if (nuevos > viejos) {
            int notificaNumero = nuevos - viejos;
            Notificacion.notifica(context, R.drawable.devolucion, R.drawable.devolucion, "BD Refacciones ha sido actualizada.",
                    "Se han agregado " + notificaNumero + " nuevas refaciones.",
                    "Entra para revizar las nuevas refacciones.");
        }else{
            Toast.makeText(context, "Tabla refacciones sincronizada", Toast.LENGTH_SHORT).show();
            //MensajeEnConsola.log(context, metodo, "Tabla refacciones ha sido actualizada");
            BD.actualiza("UPDATE control_total SET control = '" + BDVarGlo.getVarGlo(context, "VAR_BD_CONTROL_CREFACCION") + "' WHERE nombre = 'crefacciones'");
        }
    }
}
/*
$arreglo["refaccionid"] = $fila["refaccionid"];
	$arreglo["clave"] = $fila["clave"];
	$arreglo["nombre"] = $fila["nombre"];
	$arreglo["estatusidfk"] = $fila["estatusidfk"];
	$arreglo["suplente"] = $fila["suplente"];
	$arreglo["necesitaSerie"] = $fila["necesitaSerie"];
	create table if not exists "+TABLE_REFACCIONES+ " ("
    		_id integer primary key autoincrement,"
    		_idRef integer,"
            clave integer,"
            nombre text not null,"
            status integer,"
            sustituto text,"
            noSerie integer
* */
