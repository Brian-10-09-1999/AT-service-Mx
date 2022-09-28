package com.example.devolucionmaterial.static_class;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.devolucionmaterial.MainActivity;

import me.leolin.shortcutbadger.ShortcutBadger;

public class Notificacion {
    public static void notifica(Context context, int iconoSmall, int iconoLarge, String contenidoTitulo, String contenidoContenido, String mensajeTouch){
        //Lanza notificaion de registros de refacciones actualziados
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        //sonido
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //vibracion
        long[] vibracion = new long[]{1000,500,100};

        //Construccion de la notificacion;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //builder.setSmallIcon(R.drawable.devolucion);
        builder.setSmallIcon(iconoSmall);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        //builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.devolucion));
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), iconoLarge));
        builder.setContentTitle(contenidoTitulo);

        builder.setContentText(contenidoContenido);
        builder.setSubText(mensajeTouch);
        builder.setSound(sonido);
        builder.setVibrate(vibracion);
        //usar led de notificacion
        builder.setLights(Color.GREEN, 1, 0);

        //Enviar la notificacion
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
    public static void shortcut_Badger(Context context, int numero){
        //AQui se pone el numero en el icono en el launcher del telefono
        ShortcutBadger.applyCount(context, numero);
    }
}
