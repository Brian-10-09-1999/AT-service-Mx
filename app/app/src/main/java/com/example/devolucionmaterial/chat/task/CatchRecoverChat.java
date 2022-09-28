package com.example.devolucionmaterial.chat.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

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
 * Created by EDGAR ARANA on 17/04/2017.
 */

public class CatchRecoverChat extends AsyncTask<String, String, String> {

    private Context context;
    private String json;
    private DBChatManager dbManager;
    private PrefrerenceChat prefrerenceChat;

    public CatchRecoverChat(String json, Context context) {
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
        PrefrerenceChat prefrerenceChat= new PrefrerenceChat(context);
        JSONObject jsonInsertgroup;
        JSONArray jsonArrayMembers;
        try {
            jsonInsertgroup = new JSONObject(json);
            jsonArrayMembers= jsonInsertgroup.getJSONArray("members");
            for(int i=0;i<jsonArrayMembers.length(); i++){

                int id_alias= Integer.valueOf((String) jsonArrayMembers.get(i));
                List<ContactList> stList =dbManager.getInfoContact(id_alias);
                ContactList singleContact = stList.get(0);
                if(id_alias!=prefrerenceChat.getTokenChat())
                    dbManager.insertMembersGroup(
                            jsonInsertgroup.getInt("id_chat"),
                            singleContact.getId_alias(),
                            singleContact.getName(),
                            singleContact.getAlias());
            }


            dbManager.insertChat(
                    jsonInsertgroup.getInt("id_chat"),
                    jsonInsertgroup.getString("name"),
                    jsonInsertgroup.getString("descripcion"),
                    R.drawable.ic_chat_group,
                    jsonInsertgroup.getString("manager"),
                    DatabaseChatHelper.chatNoRead,
                    Long.valueOf(String.valueOf(Calendar.getInstance().getTime().getTime())),
                    DatabaseChatHelper.TYPE_CHAT_GROUP);
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
    }



}
