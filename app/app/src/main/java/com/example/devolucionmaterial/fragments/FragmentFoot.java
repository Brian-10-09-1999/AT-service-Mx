package com.example.devolucionmaterial.fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.MenuOpciones;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.squareup.picasso.Picasso;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragmentFoot extends Fragment {
    String metodo;
    Context context;
    View view;
    ImageView img_open_close, img_email, img_call, img_info;
    LinearLayout ly_menu_foot;
    MenuOpciones menuOpciones;
    public FragmentFoot(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_foot, container, false);
        context = view.getContext();
        menuOpciones = new MenuOpciones();
        setupFragmentFoot();
        return view;
    }

    void setupFragmentFoot() {
        metodo = "FragmentoFoot.setupFragmentFoot()";
        ly_menu_foot = (LinearLayout) view.findViewById(R.id.frag_foot_id_ly_menu_opc);
        img_open_close = (ImageView) view.findViewById(R.id.frag_foot_id_img_open_close);
        img_email = (ImageView) view.findViewById(R.id.frag_foot_id_img_email);
        img_call = (ImageView) view.findViewById(R.id.frag_foot_id_img_call);
        img_info = (ImageView) view.findViewById(R.id.frag_foot_id_img_info);

        img_open_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MensajeEnConsola.log(context, metodo, "Apretaste el boton para ocultar las opciones -> "+BDVarGlo.getVarGlo(context, "FRAGMENTO_FOOT_IMG_OPEN_CLOSE"));
                if("OPEN".equals(BDVarGlo.getVarGlo(context, "FRAGMENTO_FOOT_IMG_OPEN_CLOSE"))){
                    animClose();
                }else
                if("CLOSE".equals(BDVarGlo.getVarGlo(context, "FRAGMENTO_FOOT_IMG_OPEN_CLOSE"))){
                    animOpen();
                }
            }
        });
        img_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOpciones.enviarCorreo(context);
            }
        });
        img_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOpciones.llamarContactCenter(context);
            }
        });
        img_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOpciones.mostrarInfoApp(context);
            }
        });
    }

    void animClose(){
        metodo = "FragmentoFoot.animClose()";
        MensajeEnConsola.log(context, metodo, "CLOSE las opciones");
        BDVarGlo.setVarGlo(context, "FRAGMENTO_FOOT_IMG_OPEN_CLOSE","CLOSE");
        ly_menu_foot.setVisibility(View.GONE);
        Picasso.with(context).load(R.drawable.ic_open_foot).into(img_open_close);
    }
    void animOpen(){
        closeForInactivity(10);
        metodo = "FragmentoFoot.animOpen()";
        MensajeEnConsola.log(context, metodo, "OPEN las opciones");
        BDVarGlo.setVarGlo(context, "FRAGMENTO_FOOT_IMG_OPEN_CLOSE","OPEN");
        ly_menu_foot.setVisibility(View.VISIBLE);
        Picasso.with(context).load(R.drawable.ic_close_foot).into(img_open_close);
    }

    void closeForInactivity(int segundos) {
        if ("OPEN".equals(BDVarGlo.getVarGlo(context, "FRAGMENTO_FOOT_IMG_OPEN_CLOSE"))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animClose();
                }
            }, segundos * 1000);
        }
    }
}
