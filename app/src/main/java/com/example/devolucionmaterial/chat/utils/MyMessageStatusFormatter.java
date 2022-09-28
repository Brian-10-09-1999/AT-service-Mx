package com.example.devolucionmaterial.chat.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.example.devolucionmaterial.R;

/**
 * Created by Administrador on 02/03/2017.
 */

public class MyMessageStatusFormatter {
    public static final int STATUS_DELIVERING = 0;
    public static final int STATUS_DELIVERED = 1;
    public static final int STATUS_SEEN = 2;
    public static final int STATUS_ERROR = 3;
    public static final int STATUS_RECIBE= 4;
    private Drawable mDeliveringIcon;
    private Drawable mDeliveredIcon;
    private Drawable mSeenIcon;
    private Drawable mErrorIcon;

    public MyMessageStatusFormatter(Context context) {
        //Init icons
        mDeliveringIcon = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.ic_mail_outline));
        mDeliveredIcon = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.ic_done));
        mSeenIcon = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.ic_done_all));
        mErrorIcon = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.ic_report));

    }


    public Drawable getStatusIcon(int status) {
        switch (status) {
            case STATUS_DELIVERING:
                return mDeliveringIcon;
            case STATUS_DELIVERED:
                return mDeliveredIcon;
            case STATUS_SEEN:
                return mSeenIcon;
            case STATUS_ERROR:
                return mErrorIcon;
        }
        return null;
    }
}
