package com.example.devolucionmaterial.chat.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.model.ContactList;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by EDGAR ARANA on 10/04/2017.
 */

public class CatchFinishGroup extends AsyncTask<String, String, String> {

    private Context context;
    private String json;
    private DBChatManager dbManager;
    private PrefrerenceChat prefrerenceChat;
    private long idChat;

    public CatchFinishGroup(String json, Context context) {
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
        prefrerenceChat = new PrefrerenceChat(context);
        JSONObject jsonDeleteMember;

        try {
            jsonDeleteMember = new JSONObject(json);
            dbManager.deleteChatComplete(jsonDeleteMember.getLong("idchat"));
            idChat = jsonDeleteMember.getLong("idchat");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return json;
    }

    @Override
    protected void onPostExecute(String json) {
        // TODO: 30/03/2017  este sendBroadcast actuliza la lista que se encuentra en el fragmento del chat
        Intent intentUpdateListChat = new Intent(BrodcastBean.INTENT_UPDATE_LIST_CHAT);
        intentUpdateListChat.putExtra("json", json);
        context.sendBroadcast(intentUpdateListChat);


        Intent intentFinishGroup = new Intent(BrodcastBean.INTENT_FINISH_GROUP_CHAT);
        intentFinishGroup.putExtra("idchat", idChat);
        context.sendBroadcast(intentFinishGroup);


    }


}
