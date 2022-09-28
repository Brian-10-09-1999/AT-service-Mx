package com.example.devolucionmaterial.file;

import android.content.Context;
import android.os.Environment;

import com.example.devolucionmaterial.static_class.MensajeEnConsola;

import java.io.File;

public class FileDrop {
    private static final String DIRECTORIO = "ZITRO/";
    private static Context context;
    private static String metodo;
    private static String SD_CARD = "file:/sdcard/";
    public static void remove(Context context1){
        context = context1;
        metodo = "FileDrop.remove()";
        File dir = new File(Environment.getExternalStorageDirectory(), DIRECTORIO);
        File[] files = dir.listFiles();
        if(files.length > 0) {
            for (File file : files) {
                if (file.getName().substring(file.getName().lastIndexOf(".")).equals(".apk")) {
                    //Aqui borramos el archivo
                    MensajeEnConsola.log(context, metodo, "ELIMINANDO FILE : " + file.getName());
                    try {
                        context.deleteFile(SD_CARD + DIRECTORIO + file.getName());
                    } catch (Exception e) {
                        MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
                    }
                }
            }
        }
    }
}
