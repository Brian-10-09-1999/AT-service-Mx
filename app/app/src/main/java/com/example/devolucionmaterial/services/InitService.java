package com.example.devolucionmaterial.services;

import android.content.Context;
import android.content.Intent;


public class InitService {
    public static void start(Context context){
        if(!CheckService.isRunningService(context, "FolioAsignadoService")){
            //context.startService(new Intent(context, FolioAsignadoService.class));
        }
    }
}
