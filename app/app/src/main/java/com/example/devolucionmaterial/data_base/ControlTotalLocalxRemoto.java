package com.example.devolucionmaterial.data_base;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.internet.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ControlTotalLocalxRemoto extends IntentService{
    BDmanager BD;
    String metodo;
    Context context;
    String funcionControl_Total = "funcion=Control_Total";

    public ControlTotalLocalxRemoto() {
        super("ControlTotalLocalxRemoto");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        metodo = "onHandleIntent()";
        context = this;
        BD = new BDmanager(context);
        try {
            JSONObject object = JSON.load(context, metodo, funcionControl_Total);
            assert object != null;
            //Empezamos a actualizar nuestra base de datos aqui e ir revizando si hay cambios para mandar a llamar otros servicios que actualicen otras bases de datos
            JSONArray array = object.getJSONArray("registros");
            for(int i = 0; i < array.length(); i++){
                JSONObject object1 = array.getJSONObject(i);
                if(!BD.consulta("SELECT control FROM control_total WHERE nombre = '" + object1.getString("nombre") + "'",
                        object1.getString("control")).equals(object1.getString("control"))){
                    //Aqui mandaremos a actualizar nuestra base dependiendo cual sea
                    IniciaServiciosEspeciales(object1.getString("nombre"), object1.getString("control"));
                }
            }
        }catch(JSONException e){
            MensajeEnConsola.log(context, metodo, "JSONException = "+e.getMessage());
        }catch(Exception e){
            MensajeEnConsola.log(context, metodo, "Exception = "+e.getMessage());
        }
    }
    void IniciaServiciosEspeciales(String servicio, String fecha_ult_act) {
        metodo = "IniciaServiciosEspeciales()";
        /*MensajeEnConsola.log(context, metodo, "nombre : "+servicio+
                "\nAQUI : "+BD.consulta("SELECT control FROM control_total WHERE nombre = '" + servicio + "'","")+
                "\nAlla : "+fecha_ult_act
        );*/
        if (servicio.equals("crefacciones")) {
            //para la tabla de refacciones
            //BD.EliminarTabla("crefacciones");
            startService(new Intent(context, ActualizaBDcrefacciones.class));
            BDVarGlo.setVarGlo(context, "VAR_BD_CONTROL_CREFACCION", fecha_ult_act);
            //MensajeEnConsola.log(context, metodo, "Entro a pedir intent REFACCIONES");
        }
        if (servicio.equals("csala")) {
            //para la tabla de salas
            //BD.EliminarTabla("csala");
            startService(new Intent(context, ActualizaBDcsala.class));
            BDVarGlo.setVarGlo(context, "VAR_BD_CONTROL_CSALA", fecha_ult_act);
            //MensajeEnConsola.log(context, metodo, "Entro a pedir intent SALA");
        }
        if (servicio.equals("cestatus")) {
            //para la tabla de salas
            //BD.EliminarTabla("cestatus");
            startService(new Intent(context, ActualizaBDcestatus.class));
            BDVarGlo.setVarGlo(context, "VAR_BD_CONTROL_CESTATUS", fecha_ult_act);
            //MensajeEnConsola.log(context, metodo, "Entro a pedir intent ESTATUS");
        }
    }
}
