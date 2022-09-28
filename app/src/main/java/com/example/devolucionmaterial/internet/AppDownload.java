package com.example.devolucionmaterial.internet;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.checks.Device;
import com.example.devolucionmaterial.data_base.BDVarGlo;

import org.json.JSONObject;


public class AppDownload {

    private static String metodo;
    private static Context context;
    private static DownloadManager.Request request;
    private static final String carpetaGuardado = "ZITRO";
    private static final String SD_CARD = "file:/sdcard/";
    private static String nombre = "";

    public static void descargar(Context context1, String nombre1) {


        context = context1;
        nombre = nombre1;
        metodo = "AppDownload.descargar()";
        MensajeEnConsola.log(context, metodo, "APK = "+formaURL(context, nombre));

     //   MainActivity.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        request = new DownloadManager.Request(Uri.parse(formaURL(context, nombre)));

        request.setTitle("Actualización "+nombre1+".apk");
        request.setDescription("Espera un momento...");
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager. Request . VISIBILITY_VISIBLE );

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            request.setDestinationInExternalPublicDir(carpetaGuardado, nombre+".apk");
            //Intent intent = null;
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            //    intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            //}
            //intent.setData(Uri.parse(SD_CARD + carpetaGuardado + File.separator + nombre+".apk"));
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(intent);
            new DescargaApkOK().execute(BDVarGlo.getVarGlo(context, "FUNCTION_INSERTA_DESCARGA") + "&usuarioID=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID") + "&version=" + Device.versionName(context) + "&tipo=Devolucion_De_Material");
        }

        //iniciamos la descarga //
        MainActivity.id = MainActivity.downloadManager.enqueue(request);


    }

    private static String formaURL(Context context, String nombre) {
        String dominio = "";
        String directorio = "Android/";

        if(BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION")){
            dominio = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRODUCCION");
        }
        if(BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")){
            dominio = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRUEBAS");
        }
        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION-LOCAL")) {
            dominio = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRODUCCION_LOCAL");
        }
        return dominio+directorio+nombre+".apk";
    }




    private static class DescargaApkOK extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            metodo = "DescargaApkOK.doInBackground()";
            JSONObject ignored = JSON.load(context, metodo, params[0]);
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            verDescargaRealizada();
        }


    }


    private static void verDescargaRealizada() {
        Intent intent = new Intent();
        intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        context.startActivity(intent);
    }





}
