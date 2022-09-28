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

public class ActualizaBDcestatus extends IntentService {
    BDmanager BD;
    String metodo;
    Context context;
    String funcionControl_Tabla_Sala = "funcion=Control_Tabla_Estatus";

    public ActualizaBDcestatus() {
        super("ActualizaBDcestatus");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int nuevos = 0;
        BD = new BDmanager(this);
        //http://pruebasisco.ddns.net:8082/Android/android.php?ejecuta=PRUEBAS&funcion=Control_Tabla_Estatus
        int viejos = BD.consulta("SELECT count(nombre) FROM cestatus", 0);
        metodo = "onHandleIntent()";
        context = this;
        try {
            JSONObject object = JSON.load(context, metodo, funcionControl_Tabla_Sala);
            assert object != null;
            JSONArray array = object.getJSONArray("registros");
            nuevos = array.length();
            for (int i = 0; i < nuevos; i++) {
                JSONObject object1 = array.getJSONObject(i);
                if ("SSS".equals(BD.consulta("SELECT id FROM cestatus WHERE id=" + object1.getString("id") + "", "SSS"))) {
                    BD.actualiza("INSERT INTO cestatus VALUES ('" + object1.getString("id") + "', '" + object1.getString("tarea_referente") + "', '" + object1.getString("nombre") + "')");
                }else{
                    BD.actualiza("UPDATE cestatus SET tarea_referente='"+object1.getString("tarea_referente")+"', nombre='"+object1.getString("nombre")+"' WHERE id="+object1.getString("id")+"");
                }
            }
        } catch (JSONException e) {
            MensajeEnConsola.log(context, metodo, "JSONException = " + e.getMessage());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception = " + e.getMessage());
        }

        //Enviamos una notificacion para mostrar al usuario que se a actualizado su base de datos con nuevos registros
        MensajeEnConsola.log(context, metodo, "CESTATUS\nnuevos -> "+nuevos+"\nviejos -> "+viejos);
        if (nuevos > viejos) {
            int notificaNumero = nuevos - viejos;
            Notificacion.notifica(context, R.drawable.devolucion, R.drawable.devolucion, "BD Estatus ha sido actualizada.",
                    "Se han agregado " + notificaNumero + " nuevos estatus.",
                    "Entra para revizar los nuevos estatus.");
        }else{
            Toast.makeText(context, "Tabla estatus sincronizada", Toast.LENGTH_SHORT).show();
            //MensajeEnConsola.log(context, metodo, "Tabla estatus ha sido actualizada");
            BD.actualiza("UPDATE control_total SET control = '" + BDVarGlo.getVarGlo(context, "VAR_BD_CONTROL_CESTATUS") + "' WHERE nombre = 'cestatus'");
        }
    }
}
/*$arreglo["id"] = nullx0($fila["estatusid"]);
$arreglo["tarea_referente"] = nullx0($fila["tarea_referente"]);
$arreglo["nombre"] = nullx0($fila["nombre"]);
"create table if not exists cestatus (" +
            "id integer primary key, " +
            "tarea_referente text, " +
            "nombre text" +
            ");
*/