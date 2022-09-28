package com.example.devolucionmaterial.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.R;

public class ToastManager {
    static String metodo;
    public static final int INFORMATION = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;
    public static final int EXITO = 3;

    public static void show(Context context, String text, int toastType) {
        metodo = "show()";
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.activity_toast_layout, null);

            TextView tv = (TextView) layout.findViewById(R.id.tvTexto);
            tv.setText(text);

            LinearLayout llRoot = (LinearLayout) layout.findViewById(R.id.llRoot);
            CardView cardView = (CardView) layout.findViewById(R.id.toast_layout_root);

            Drawable img;
            int bg;
            int color;
            switch (toastType) {
                case WARNING:
                    img = context.getResources().getDrawable(R.drawable.warning);
                    bg = R.drawable.toast_background_yellow;
                    color = context.getResources().getColor(R.color.toast_background_yellow);
                    break;
                case ERROR:
                    img = context.getResources().getDrawable(R.drawable.error);
                    bg = R.drawable.toast_background_red;
                    color = context.getResources().getColor(R.color.toast_background_yellow);
                    break;
                case EXITO:
                    img = context.getResources().getDrawable(R.drawable.icon_successs);
                    bg = R.drawable.toast_background_green;
                    color = context.getResources().getColor(R.color.toast_background_yellow);
                    break;
                default:
                    img = context.getResources().getDrawable(R.drawable.information);
                    bg = R.drawable.toast_background_blue;
                    color = context.getResources().getColor(R.color.toast_background_blue);
                    break;
            }
            tv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            llRoot.setBackgroundResource(bg);
            cardView.setCardBackgroundColor(color);

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Excepcio = " + e.getMessage());
        }
    }
}
