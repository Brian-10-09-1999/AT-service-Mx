package com.example.devolucionmaterial.api;

import android.content.Context;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by EDGAR ARANA on 13/04/2018.
 */

public class PostGet {



    private Context context;
    private String metodo="api.PostGet";





    public PostGet(Context context){
        this.context=context;
    }


    public String ConexionPost(String urlpath, JSONObject json) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlpath);
            connection = (HttpURLConnection) url.openConnection();
            //configuracion parametros iniciales
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(json.toString());
            streamWriter.flush();
            streamWriter.close();

            StringBuilder stringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String response = null;
                while ((response = bufferedReader.readLine()) != null) {
                    stringBuilder.append(response);
                }
                bufferedReader.close();
                Log.d(metodo+" Post ok ","");
                return stringBuilder.toString();
            } else {
                Log.e(metodo+ " HTTP no ok: ", connection.getResponseMessage());
                return null;
            }


        } catch (Exception exception) {
            Log.e(metodo+" Post Fail", exception.toString());
            return "La Conexion Fallo!!!";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }




    public String ConexionGet(String urlpath) {


        try {
            InputStream is = new URL(urlpath).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String respuesta = readAll(rd);
            is.close();

            return respuesta;
        } catch (NetworkOnMainThreadException e) {
            Log.e(metodo+"ConexionGet ","ERROR.NetworkOnMainThreadException = "+e.getMessage().toString());

        } catch (MalformedURLException e) {
            Log.e(metodo+"ConexionGet ","ERROR.MalformedURLException = "+e.getMessage().toString());

        }  catch (Exception e) {
            Log.e(metodo+"ConexionGet ","ERROR.Exception = "+e.getMessage().toString());

        }
        return "Error Server";
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
