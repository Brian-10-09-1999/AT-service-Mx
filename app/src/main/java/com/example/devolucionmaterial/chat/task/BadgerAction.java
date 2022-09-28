package com.example.devolucionmaterial.chat.task;

import android.content.Context;
import android.util.Log;

import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.model.ChatList;

import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by EDGAR ARANA on 05/07/2017.
 */

public class BadgerAction {
    private Context context;
    private DBChatManager dbManager;

    public BadgerAction(Context context) {
        dbManager = new DBChatManager(context);
        dbManager.open();
        this.context = context;
    }

    public void update() {
        // TODO: 05/07/2017 badger es la notificacion en el icono
        /***
         * se saca el numero de se muestra en el badget
         * del lista de chats
         * */
        List<ChatList> chatLists = dbManager.getListChat();

        if (chatLists != null) {
            int badgeCount = 0;
            for (int i = 0; i < chatLists.size(); i++) {
                ChatList cl = chatLists.get(i);
                if (cl.getStatus() == 0)
                    badgeCount++;
            }
            Log.e("numero de bagder", String.valueOf(badgeCount));
            boolean success = ShortcutBadger.applyCount(context, badgeCount);

            Log.e("numero de bagder", "Set count=" + badgeCount + ", success=" + success);
        } else {
            boolean success = ShortcutBadger.applyCount(context, 0);
        }
        dbManager.close();
    }
}