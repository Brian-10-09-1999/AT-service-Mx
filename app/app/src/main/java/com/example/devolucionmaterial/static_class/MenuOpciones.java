package com.example.devolucionmaterial.static_class;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.sharedpreferences.PreferencesVar;

public class MenuOpciones {
    String metodo;
    Context context;

    public MenuOpciones() {
    }

    public void llamarContactCenter(Context context) {

        String prefijo = "";
        try {
            if (PreferencesVar.getPaisValor() == 1)
                prefijo = PreferencesVar.prefijo();
        } catch (Exception e) {
        }

        try {
            Uri numero = Uri.parse("tel:" + prefijo + "5553626292");
            Intent intent = new Intent(Intent.ACTION_CALL, numero);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            context.startActivity(intent);
        } catch (ActivityNotFoundException activityException) {
            Toast.makeText(context, "No se pudo realizar la llamada.", Toast.LENGTH_SHORT).show();
        }
    }

    public void enviarCorreo(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Atención de Incidencia");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contactcenter@operacionesdelnorte.com"});
        context.startActivity(intent);
    }

    public void mostrarInfoApp(Context context) {
        String versionName = null;
        PackageManager manager = context.getPackageManager();
        {
            try {
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                //String packageName = info.packageName;
                //int versionCode = info.versionCode;
                versionName = info.versionName;
            } catch (NameNotFoundException e) {
                Toast.makeText(context, "Excepción de paquete ", Toast.LENGTH_LONG).show();
            }
        }


        final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(context);
        alertaSimple.setTitle("Acerca de...");
        alertaSimple.setMessage("Versión : " + versionName + "\n" +
                "Fecha de última Actualización: 05/Octubre/2016\n" +
                "Esta aplicación forma parte de SisCo Mobile");
        alertaSimple.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertaSimple.setIcon(R.drawable.logozitro);
        alertaSimple.create();
        alertaSimple.show();
    }
}
