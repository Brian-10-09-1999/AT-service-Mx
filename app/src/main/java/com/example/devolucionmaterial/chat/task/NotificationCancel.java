package com.example.devolucionmaterial.chat.task;

import android.app.NotificationManager;
import android.content.Context;

/**
 * Created by EDGAR ARANA on 11/04/2017.
 */

public class NotificationCancel {

    public  static  void cancelNotification(Context context, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}
