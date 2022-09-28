package com.example.devolucionmaterial.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.MenuInicial;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.chat.task.CatchAddMemberChatGroup;
import com.example.devolucionmaterial.chat.task.CatchDeleteMmember;
import com.example.devolucionmaterial.chat.task.CatchFinishGroup;
import com.example.devolucionmaterial.chat.task.CatchMessageChat;
import com.example.devolucionmaterial.chat.task.CatchMessageChatGroup;
import com.example.devolucionmaterial.chat.task.CatchNewGroup;
import com.example.devolucionmaterial.chat.task.CatchRecoverChat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Usuario on 10/11/2016.
 * <p>
 * se declaran variables finales ya que este el el unico canal donde cae
 * toda la infromacion de firebase
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String tituloNotificacion;
    private final static int CHAT = 1;
    private final static int CHAT_GROUP = 2;
    private final static int CHAT_GROUP_CREATE = 3;
    private final static int ADD_CHAT_GROUP = 4;
    private final static int DELETE_MEMBER_CHAT = 5;
    private final static int FINISH_GROUP = 6;
    private final static int RECOVER_CHATS = 7;
    private final static int NOTIFICATION = 8;

    /**
     * typeNotificatiion
     * <p>
     * 1= para los chats
     **/

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log.e("FIREBASE", remoteMessage.getNotification().getBody());
        if (remoteMessage.getData().size() > 0) {
            // TODO: 27/03/2017  se le da un nombre a la radiofusion para que la actividad pueda recicbir el mensaje
            /****
             * se decalara dentro de la activdad que va cachar el mensaje y actualice el chat
             * y en el manifiesto dentro de la etiqueta de la  actividad se declara el filtrado cpn el nombre de el string INTENT_FILTER
             * */
            try {

                Log.e("remoteMessage", remoteMessage.getData().toString());
                // TODO: 04/04/2017 data encapsula el cuerpo de json
                JSONObject jsonData = new JSONObject(remoteMessage.getData().toString());
                JSONObject json = jsonData.getJSONObject("data");
                // TODO: 04/04/2017   // typeNotificatiion 1


                switch (json.getInt("typeNotificatiion")) {
                    // TODO: 06/04/2017 1
                    case CHAT:
                        CatchMessageChat catchMessageChat =
                                new CatchMessageChat(json.toString(), getApplicationContext());
                        catchMessageChat.execute();
                        break;
                    // TODO: 06/04/2017 2
                    case CHAT_GROUP:
                        CatchMessageChatGroup catchMessageChatGroup =
                                new CatchMessageChatGroup(json.toString(), getApplicationContext());
                        catchMessageChatGroup.execute();
                        break;
                    // TODO: 06/04/2017 3
                    case CHAT_GROUP_CREATE:
                        CatchNewGroup catchNewGroup =
                                new CatchNewGroup(json.toString(), getApplicationContext());
                        catchNewGroup.execute();
                        break;

                    case ADD_CHAT_GROUP:
                        CatchAddMemberChatGroup catchAddMemberChatGroup =
                                new CatchAddMemberChatGroup(json.toString(), getApplicationContext());
                        catchAddMemberChatGroup.execute();
                        break;
                    case DELETE_MEMBER_CHAT:
                        CatchDeleteMmember deleteMmember =
                                new CatchDeleteMmember(json.toString(), getApplicationContext());
                        deleteMmember.execute();
                        break;

                    case FINISH_GROUP:
                        CatchFinishGroup catchFinishGroup =
                                new CatchFinishGroup(json.toString(), getApplicationContext());
                        catchFinishGroup.execute();
                        break;

                    case RECOVER_CHATS:
                        CatchRecoverChat catchRecoverChat = new CatchRecoverChat(json.toString(), getApplicationContext());
                        catchRecoverChat.execute();
                        break;

                    case NOTIFICATION:
                        tituloNotificacion = json.getJSONObject("data").getString("title");
                        Log.e("MyFirebaseInstanceIdService", String.valueOf(json));
                        Log.e("titulo", tituloNotificacion);
                        tipoNotificacion(json, Integer.valueOf(json.getJSONObject("data").getString("tipo")));
                        break;

                }

            } catch (Exception e) {
                Log.e("error json parseo", "Exception: " + e.getMessage());
            }
        }
    }


    private void tipoNotificacion(JSONObject jsonObject, int tipo) {
        String desc = "";
        String solicitud_refaccionidfk = "";
        String sala = "";
        switch (tipo) {
            case 1:
                try {
                    tituloNotificacion = jsonObject.getJSONObject("data").getString("title");
                    desc = jsonObject.getJSONObject("data").getString("solicitud_refaccionidfk");
                    solicitud_refaccionidfk = jsonObject.getJSONObject("data").getString("solicitud_refaccionidfk");
                    sala = jsonObject.getJSONObject("data").getString("sala");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                notificaionTipoUno(tituloNotificacion, desc, solicitud_refaccionidfk, sala);
                break;

            case 2:
                startService(new Intent(this, RastreoService.class));
                break;
        }
    }


    void notificaionTipoUno(String titutlo, String desc, String solicitud_refaccionidfk, String sala) {
        int notificationId = 1;

        String descripcion = "" + solicitud_refaccionidfk + "," + sala;

        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.devolucion)
                .setContentTitle(titutlo)
                .setContentText(descripcion);

        notificationCompat.setVibrate(new long[]{500, 1000});

        //LED
        notificationCompat.setLights(Color.YELLOW, 3000, 3000);

        //Ton
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationCompat.setSound(alarmSound);

        Intent intent = new Intent(getBaseContext(), SolicitudMaterialDetallesNotificacion.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
        intent.putExtra("solicitud_refaccionidfk", solicitud_refaccionidfk);
        taskStackBuilder.addParentStack(MenuInicial.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationCompat.setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationCompat.setAutoCancel(true);
        notificationManager.notify(notificationId, notificationCompat.build());
    }
}
