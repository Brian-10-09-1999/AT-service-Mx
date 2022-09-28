package com.example.devolucionmaterial.data_base;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.devolucionmaterial.api.PostGet;
import com.example.devolucionmaterial.internet.JSON;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JONHGab on 30/04/2018.
 */

public class ActualizaBDCreaFolios extends IntentService {

    BDmanager BD;
    String metodo="data_base.ActualizaDBCrearFolios";
    Context context;
    private static String funcion = "funcion=Control_Tabla_Fallas";
    private String Respuesta="";



    public ActualizaBDCreaFolios() {
        super("ActualizaBDCreaFolio");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        context = this;
        BD = new BDmanager(context);
        String url= formaURL(this);

        try {
            PostGet servicio=new PostGet(context);
            Log.i("---------"+metodo + "ConectaServer GET url",url);
            Respuesta=servicio.ConexionGet(url);
            Log.i("---------"+metodo + "Respuesta=",Respuesta);

            JSONObject json=new JSONObject(Respuesta);

            JSONArray arrJsonFallas = json.getJSONArray("fallas");
            JSONArray arrJsonSubFallas = json.getJSONArray("subfallas");
            JSONArray arrJsonSalaJuegoLic = json.getJSONArray("salaJuegoLic");





            for (int i = 0; i < arrJsonFallas.length(); i++) {
                JSONObject object1 = arrJsonFallas.getJSONObject(i);
                //  if ("SSS".equals(BD.consulta("SELECT officeID FROM cjuegos WHERE o=" + object1.getString("OfficeID") + "", "SSS"))) {
                BD.actualiza("INSERT INTO cfallas VALUES ('" +(i+1)+ "', '" +object1.getString("fallaid") + "', '" + object1.getString("nombre") + "')" );
               // Log.i(metodo ,"id insertado -> "+object1.getString("fallaid")+"       falla="+object1.getString("nombre"));
                // }
            }

            for (int i = 0; i < arrJsonSubFallas.length(); i++) {
                JSONObject object1 = arrJsonSubFallas.getJSONObject(i);

                //  if ("SSS".equals(BD.consulta("SELECT officeID FROM cjuegos WHERE o=" + object1.getString("OfficeID") + "", "SSS"))) {
                BD.actualiza("INSERT INTO csubfallas VALUES ('" +(i+1)+ "', '" +object1.getString("subfallaid") + "', '" + object1.getString("nombre") +"', '"+ object1.getString("idfalla")+"')" );
               // Log.i(metodo ,"id insertado -> "+object1.getString("subfallaid")+"       subfalla="+object1.getString("nombre")+ "id falla="+object1.getString("idfalla"));
                // }
            }



            for (int i = 0; i < arrJsonSalaJuegoLic.length(); i++) {
                JSONObject object1 = arrJsonSalaJuegoLic.getJSONObject(i);
                //  if ("SSS".equals(BD.consulta("SELECT officeID FROM cjuegos WHERE o=" + object1.getString("OfficeID") + "", "SSS"))) {
                BD.actualiza("INSERT INTO csalajuegolic VALUES ('" +(i+1)+ "', '" +object1.getString("maquinaid") + "', '" + object1.getString("salaid") +"', '"+ object1.getString("licencia")+"', '"+ object1.getString("juegoid") +"', '"+ object1.getString("nombreJuego")+ "')" );
               // Log.i(metodo ,"id insertado -> "+object1.getString("maquinaid")+"       salaid="+object1.getString("salaid")+ " licencia="+object1.getString("licencia"));
                // }
            }







        } catch (JSONException e) {
            MensajeEnConsola.log(context, metodo, "JSONException = " + e.getMessage());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception = " + e.getMessage());
        }

    }



    //develop jonh gab
    private static  String formaURL(Context context) {
        String dominio = "";
        String directorio = "Android/android.php?";

        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION")) {
            dominio = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRODUCCION");
            directorio += "ejecuta=PRODUCCION&";
        }
        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")) {
            dominio = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRUEBAS");
            directorio += "ejecuta=PRUEBAS&";
        }
        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION-LOCAL")) {
            dominio = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRODUCCION_LOCAL");
            directorio += "ejecuta=PRODUCCION&";
        }
        // TODO: 31/05/2017 se agraga por get el tecnico id poara saber las salas
        return dominio + directorio + funcion +"&tecnicoid="+BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID");
    }


}
