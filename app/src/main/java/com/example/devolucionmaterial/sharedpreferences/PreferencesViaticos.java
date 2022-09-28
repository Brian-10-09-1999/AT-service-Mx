package com.example.devolucionmaterial.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jonh on 25/02/18. esta clase guarda solo jsons de llegada de las activitys viaticos
 */

public class PreferencesViaticos {

    private SharedPreferences Viaticos;
    private SharedPreferences.Editor EditorViaticos;
    private Context context;

    private String ListadeViajes;
    private String ListaGastos;

    public PreferencesViaticos(Context context){

        this.context=context;
        Viaticos = context.getSharedPreferences("viaticos", Context.MODE_PRIVATE);
    }

    public String getJson(String jsonName) {

        return Viaticos.getString(jsonName,"");

    }

    public void setJson(String jsonName,String json) {
        EditorViaticos = Viaticos.edit();

        EditorViaticos.putString(jsonName ,json);

        EditorViaticos.commit();

    }
}