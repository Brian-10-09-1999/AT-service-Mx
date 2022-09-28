package com.example.devolucionmaterial.sharedpreferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.devolucionmaterial.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Usuario on 22/11/2016.
 */

public class PreferencesVar {

    public static String PAIS = "pais";
    public static Context context;
    public static SharedPreferences guardarPais;
    public static String regionId = "regionid";
    public static SharedPreferences spRegion;

    public PreferencesVar(Context context) {
        this.context = context;
    }

    public static void setPais(String pais, int valor) {

        guardarPais = context.getSharedPreferences(PAIS, MODE_PRIVATE);
        SharedPreferences.Editor editor = guardarPais.edit();
        editor.putString("pais", pais);
        editor.putInt("valor", valor);
        editor.commit();
    }

    public static int getPaisValor() {
        SharedPreferences recuperarValor = context.getSharedPreferences(PAIS, MODE_PRIVATE);
        int valorRecuperado = recuperarValor.getInt("valor", 0);

        return valorRecuperado;
    }

    public static String prefijo() {
        String[] lValor = context.getResources().getStringArray(R.array.prefijo);
        Log.e("prefijo",lValor[getPaisValor()]);
        return lValor[getPaisValor()];
    }

    public static void setRegionId(int region) {
        spRegion = context.getSharedPreferences(regionId, MODE_PRIVATE);
        SharedPreferences.Editor editor = spRegion.edit();
        editor.putInt("regionid", region);
        editor.apply();
    }

    // TODO: 14/03/2017  ontienes id de la region que regresa el login
    public static int getIdRegion() {
        SharedPreferences recuperarValor = context.getSharedPreferences(regionId, MODE_PRIVATE);
        int valorRecuperado = recuperarValor.getInt("regionid", 0);
        return valorRecuperado;
    }


}
