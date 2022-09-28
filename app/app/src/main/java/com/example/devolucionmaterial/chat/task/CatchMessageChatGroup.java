package com.example.devolucionmaterial.chat.task;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.activitys.ChatActivity;
import com.example.devolucionmaterial.chat.adapter.ChatAdapter;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.db.DatabaseChatHelper;
import com.example.devolucionmaterial.chat.model.ContactList;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

/**
 * Created by EDGAR ARANA on 04/04/2017.
 */

public class CatchMessageChatGroup extends AsyncTask<String, String, String> {
    private Context context;
    private String json;
    private DBChatManager dbManager;
    private PrefrerenceChat prefrerenceChat;

    public CatchMessageChatGroup(String json, Context context) {
        this.context = context;
        this.json = json;
    }

    protected void onPreExecute() {
        this.prefrerenceChat = new PrefrerenceChat(context);
        this.dbManager = new DBChatManager(context);
        this.dbManager.open();
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            JSONObject jsonMsg = new JSONObject(json);

            dbManager.insertMessageRecibe(
                    jsonMsg.getInt("receptor"),
                    assembleMessage(jsonMsg.getString("message")),
                    Long.valueOf(String.valueOf(Calendar.getInstance().getTime().getTime()))
                    , DatabaseChatHelper.ChatRecibe);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onPostExecute(String json) {
        // TODO: 31/03/2017  notificationID recibe del json userModel que es el id alias y que en las tablas se relaciona como id chat
        int notificationID = 0;
        String msj = "";
        JSONObject jsonRecibe = null;
        String userAlias = "";
        int type = 0;
        try {
            jsonRecibe = new JSONObject(json);
            notificationID = jsonRecibe.getInt("receptor");
            userAlias = dbManager.getContactNameChat(notificationID);
            msj = jsonRecibe.getString("message");
            type = jsonRecibe.getInt("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (prefrerenceChat.getStatusChat() == 1 && prefrerenceChat.getIdChatStatus() == notificationID) {
            Intent intentF = new Intent(BrodcastBean.INTENT_FILTER);
            intentF.putExtra("json", json);
            context.sendBroadcast(intentF);
            dbManager.updateStatusChatList(notificationID, DatabaseChatHelper.chatRead, Calendar.getInstance().getTime().getTime());
        } else {
            dbManager.updateStatusChatList(notificationID, DatabaseChatHelper.chatNoRead, Calendar.getInstance().getTime().getTime());
            catchMessage(notificationID, msj, userAlias, type);

        }
        // TODO: 30/03/2017  este sendBroadcast actuliza la lista que se encuentra en el fragmento del chat
        Intent intentUpdateListChat = new Intent(BrodcastBean.INTENT_UPDATE_LIST_CHAT);
        intentUpdateListChat.putExtra("json", json);
        context.sendBroadcast(intentUpdateListChat);


    }

    void catchMessage(int notificationID, String msj, String userAlias, int type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("id", notificationID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //sonido
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //vibracion
        long[] vibracion = new long[]{1000, 500, 100};

// TODO: 18/04/2017 se ve que tipo de notificacione llega
        if (ChatAdapter.LEFT_MSG_IMG == type) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            builder.setSmallIcon(R.drawable.devolucion);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.devolucion));
            builder.setContentTitle(userAlias);

            builder.setContentText("Imagen........");
            //builder.setSubText(msj);
            builder.setSound(sonido);
            builder.setVibrate(vibracion);

            builder.setLights(Color.BLUE, 1, 0);

            NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(getImage(msj));
            s.setSummaryText(msj);
            builder.setStyle(s);
            //Enviar la notificacion
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationID, builder.build());


        } else {


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            builder.setSmallIcon(R.drawable.devolucion);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            //builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.devolucion));
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.devolucion));
            builder.setContentTitle(userAlias);

            builder.setContentText(msj);
            builder.setSubText(msj);
            builder.setSound(sonido);
            builder.setVibrate(vibracion);
            //usar led de notificacion
            builder.setLights(Color.BLUE, 1, 0);

            //Enviar la notificacion
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationID, builder.build());


        }
    }


    // TODO: 01/02/2017  se crea el json con la informacion del menasje
    private JSONObject assembleMessage(String msj) {
        JSONObject jsonRecibe = null;
        String userAlias = null;
        try {
            jsonRecibe = new JSONObject(json);
            userAlias = dbManager.getContactAlias(jsonRecibe.getInt("emisor"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonMessage = new JSONObject();
        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser.put(DatabaseChatHelper.user, userAlias);
            jsonUser.put(DatabaseChatHelper.id_emisor, jsonRecibe.getInt("emisor"));
            jsonUser.put(DatabaseChatHelper.id_resceptor, jsonRecibe.getInt("receptor"));
            jsonMessage.put(DatabaseChatHelper.message, msj);
            jsonMessage.put(DatabaseChatHelper.type, jsonRecibe.getInt("type"));
            jsonMessage.put(DatabaseChatHelper.userModel, jsonUser);
            jsonMessage.put(DatabaseChatHelper.timeStamp, Calendar.getInstance().getTime().getTime() + "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("json message recibe", jsonMessage.toString());
        return jsonMessage;
    }

    Bitmap getImage(String url) {
        Bitmap bm = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            URL _url = new URL(url);
            URLConnection con = _url.openConnection();
            con.connect();
            InputStream is = con.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {

        }
        return bm;
    }


}
