package com.example.devolucionmaterial.internet;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.data_base.BDVarGlo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class JSON {
    static String metodo;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static JSONObject load(Context context, String metodo1, String funcion) {
        metodo = metodo1 + ".JSON()";
        String url = formaURL(context, funcion);
        Log.e("url", url);

        //http://pruebasisco.ddns.net:8082/Android/android.php?ejecuta=PRUEBAS&funcion=Control_Total
        try {
            InputStream is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            is.close();
            String[] mensajesSeparados = new String[2];
            mensajesSeparados = jsonText.split("---");

            MensajeEnConsola.log(context, metodo, "" +
                    "ENVIA \n" +
                    "url = " + url + "\n" +
                    "RECIVE \n" +
                    "errores inicial = " + mensajesSeparados[0] + "\n" +
                    "errores finales = " + mensajesSeparados[2] + "\n" +
                    "datos = " + mensajesSeparados[1]);
            return new JSONObject(mensajesSeparados[1]);
        } catch (NetworkOnMainThreadException e) {
            MensajeEnConsola.log(context, metodo, "ERROR.NetworkOnMainThreadException = " + e.getMessage());
        } catch (MalformedURLException e) {
            MensajeEnConsola.log(context, metodo, "ERROR.MalformedURLException = " + e.getMessage());
        } catch (JSONException e) {
            MensajeEnConsola.log(context, metodo, "ERROR.JSONException = " + e.getMessage());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "ERROR.Exception = " + e.getMessage());
        }
        return null;
    }
    public static JSONObject loadSalas(Context context, String metodo1, String funcion) {
        metodo = metodo1 + ".JSON()";
        String url = formaURLSalas(context, funcion);
        Log.e("url", url);
        //http://pruebasisco.ddns.net:8082/Android/android.php?ejecuta=PRUEBAS&funcion=Control_Total
        try {
            InputStream is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            is.close();
            String[] mensajesSeparados = new String[2];
            mensajesSeparados = jsonText.split("---");

            MensajeEnConsola.log(context, metodo, "" +
                    "ENVIA \n" +
                    "url = " + url + "\n" +
                    "RECIVE \n" +
                    "errores inicial = " + mensajesSeparados[0] + "\n" +
                    "errores finales = " + mensajesSeparados[2] + "\n" +
                    "datos = " + mensajesSeparados[1]);
            return new JSONObject(mensajesSeparados[1]);
        } catch (NetworkOnMainThreadException e) {
            MensajeEnConsola.log(context, metodo, "ERROR.NetworkOnMainThreadException = " + e.getMessage());
        } catch (MalformedURLException e) {
            MensajeEnConsola.log(context, metodo, "ERROR.MalformedURLException = " + e.getMessage());
        } catch (JSONException e) {
            MensajeEnConsola.log(context, metodo, "ERROR.JSONException = " + e.getMessage());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "ERROR.Exception = " + e.getMessage());
        }
        return null;
    }
    private static String formaURL(Context context, String funcion) {
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
        return dominio + directorio + funcion;
    }

    private static String formaURLSalas(Context context, String funcion) {
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
        return dominio + directorio + funcion+"&tecnicoid="+BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID");
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}