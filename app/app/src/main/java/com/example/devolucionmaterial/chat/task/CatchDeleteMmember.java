package com.example.devolucionmaterial.chat.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.db.DatabaseChatHelper;
import com.example.devolucionmaterial.chat.model.ContactList;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

/**
 * Created by EDGAR ARANA on 10/04/2017.
 */

public class CatchDeleteMmember extends AsyncTask<String, String, String> {

    private Context context;
    private String json;
    private DBChatManager dbManager;
    private PrefrerenceChat prefrerenceChat;
    private long id_alias;

    public CatchDeleteMmember(String json, Context context) {
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
        JSONArray jsonArrayMembers;
        try {
            jsonDeleteMember = new JSONObject(json);

            jsonArrayMembers = jsonDeleteMember.getJSONArray("members");
            for (int i = 0; i < jsonArrayMembers.length(); i++) {
                String id = jsonArrayMembers.get(i).toString().trim();
                 id_alias = Long.valueOf(id);
                List<ContactList> stList = dbManager.getInfoContact(id_alias);
                ContactList singleContact = stList.get(0);

                if (id_alias != prefrerenceChat.getTokenChat()){

                    dbManager.deleteMemberId(singleContact.getId_alias());
                }

                // TODO: 10/04/2017 si el id es el que se elimino se debe quitar  de la lista d echat
                if (id_alias == prefrerenceChat.getTokenChat()) {
                    dbManager.deleteChatComplete(jsonDeleteMember.getLong("idchat"));
                }
            }

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


        Intent intentDeleteMember = new Intent(BrodcastBean.INTENT_DELETE_MEMBER_GROUP_CHAT);
        Log.e("id_alias", String.valueOf(id_alias));
        intentDeleteMember.putExtra("id_alias", id_alias);
        context.sendBroadcast(intentDeleteMember);

    }


}
