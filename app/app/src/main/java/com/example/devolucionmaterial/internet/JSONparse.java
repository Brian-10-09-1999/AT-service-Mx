package com.example.devolucionmaterial.internet;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.NetworkOnMainThreadException;

import com.example.devolucionmaterial.static_class.MensajeEnConsola;

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

public class JSONparse{
    static String metodo;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static JSONObject consultaURL(Context context, String metodo1, String url){
        metodo = metodo1+".Alerta()";
        try {
            InputStream is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText =  readAll(rd);
            is.close();
            MensajeEnConsola.log(context, metodo, "ENVIA \n url = "+url+"\nRECIVE \n datos = "+jsonText);
            return new JSONObject(jsonText);
        }catch(NetworkOnMainThreadException e){
            MensajeEnConsola.log(context, metodo, "ERROR.NetworkOnMainThreadException = "+e.getMessage());
        } catch (MalformedURLException e){
            MensajeEnConsola.log(context, metodo, "ERROR.MalformedURLException = "+e.getMessage());
        } catch(JSONException e) {
            MensajeEnConsola.log(context, metodo, "ERROR.JSONException = "+e.getMessage());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "ERROR.Exception = "+e.getMessage());
        }
        return null;
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