package com.example.devolucionmaterial.chat.task;



import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.model.Contact;




import java.util.List;



/**
 * Created by EDGAR ARANA on 30/03/2017.
 */

public class ContactChat extends AsyncTask<String, String, String> {

    private Context context;
    private DBChatManager dbManager;
    private List<Contact> contactList;

    public ContactChat(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;

    }

    protected void onPreExecute() {
        this.dbManager = new DBChatManager(context);
        this.dbManager.open();

    }

    @Override
    protected String doInBackground(String... params) {
        dbManager.deleteAllContacts();
        try {
            for (int i = 0; i < contactList.size(); i++) {
                Contact ct = contactList.get(i);
                dbManager.insert(
                        ct.getAlias(),
                        ct.getNombre(),
                        ct.getId(),
                        ct.getEmail(),
                        ct.getToken());
            }

        } catch (Exception e) {
            Log.e("execpytion", String.valueOf(e));
        }

        return "";
    }

    @Override
    protected void onPostExecute(String json) {
        Log.e("contactList", "");

    }



}
