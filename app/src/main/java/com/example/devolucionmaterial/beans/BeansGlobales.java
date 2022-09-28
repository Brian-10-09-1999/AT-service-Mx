package com.example.devolucionmaterial.beans;

import android.content.Context;

import com.example.devolucionmaterial.data_base.BDVarGlo;

/**
 * Created by Usuario on 08/11/2016.
 */

public class BeansGlobales {
    static String url;
    static Context context;

    public BeansGlobales(Context context) {
        this.context = context;
    }

    public static String getUrl() {

        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION")) {
            url = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRODUCCION") + "Android/";
        }
        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")) {
            url = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRUEBAS") + "Android/";
        }
        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION-LOCAL")) {
            url = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRODUCCION_LOCAL") + "Android/";
        }
        return url;
    }


    public static String SERVIDOR_PRUEBAS_URL = "http://pruebasisco.ddns.net:8082/Android/";
    public static String SERVIDOR_PRODUCCION_URL = "http://189.254.111.195:8082/Android/";
    public static String SERVIDOR_PRODUCCION_LOCAL_URL = "http://10.10.0.11:8082/Android/";

    // TODO: 08/11/2016 seleccion de la funcion en el servidor que se envia al php
    // TODO: 08/11/2016 funciones que estan en el php
    public static final String FUNCIION_LISTA_MATERIALES = "lista_solicitud_material";
    public static final String FUNCIION_LISTA_MATERIALES_DETALLES = "lista_solicitud_material_detalles";
    public static final String FUNCIION_LISTA_MATERIALES_ACTUALIZAR_ESTATUS = "actualizar_lista_sdm";


}
