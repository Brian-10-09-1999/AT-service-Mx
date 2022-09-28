package com.example.devolucionmaterial.fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.devolucionmaterial.dialogs.Estatus;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.internet.JSONparse;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragmentHead extends Fragment {
    String metodo;
    static Context context;
    View view;

    ImageView img_menu, img_actividad;
    static ImageView img_status;
    static TextView txt_actividad;
    BDmanager manager;

    public FragmentHead() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_head, container, false);
        context = view.getContext();
        manager = new BDmanager(context);
        new ConectWithServerForStatus().execute();
        setupFragmentHead();
        return view;
    }

    void setupFragmentHead() {
        img_menu = (ImageView) view.findViewById(R.id.frag_head_id_img_menu);
        img_actividad = (ImageView) view.findViewById(R.id.frag_head_id_img_act);
        img_status = (ImageView) view.findViewById(R.id.frag_head_id_img_status);
        txt_actividad = (TextView) view.findViewById(R.id.frag_head_id_txt_act_name);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenuLateral();
            }
        });
        img_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEstatus();
            }
        });
    }

    void openMenuLateral() {
    }

    void openEstatus() {
        startActivity(new Intent(context, Estatus.class));
    }

    public static void CambiarTitulo(String newTitulo) {
        txt_actividad.setText(newTitulo);
    }

    public static void ActualizaImagenEstatus() {
        if ("39".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS"))) {
            if ("1".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO"))) {
                Picasso.with(context).load(R.drawable.est_activo).into(img_status);
            } else if ("2".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO"))) {
                Picasso.with(context).load(R.drawable.est_traslado_sala).into(img_status);
            } else if ("3".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO"))) {
                Picasso.with(context).load(R.drawable.est_asignado).into(img_status);
            }

            if ("4".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO"))) {
                Picasso.with(context).load(R.drawable.est_en_comida).into(img_status);
            }
        }

        if ("40".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS"))) {
            Picasso.with(context).load(R.drawable.est_inactivo).into(img_status);
        }
        if ("109".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS"))) {
            Picasso.with(context).load(R.drawable.est_incidencia).into(img_status);
        }
        if ("79".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS"))) {
            Picasso.with(context).load(R.drawable.est_dia_descanso).into(img_status);
        }
        if ("80".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS"))) {
            Picasso.with(context).load(R.drawable.mix_peq).into(img_status);
        }
        if ("81".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS"))) {
            Picasso.with(context).load(R.drawable.est_vacaciones).into(img_status);
        }
        if ("89".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS"))) {
            Picasso.with(context).load(R.drawable.est_incapacidad).into(img_status);
        }
    }

    private class ConectWithServerForStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String docPHPMenuProFirma = "MenuProFirma.php?";
            String docPHPMenuTecFirma = "MenuTecFirma.php?";
            try {
                JSONObject object = null;
                if ("comercial".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
                    object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPMenuProFirma + "promotoraidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
                }
                if ("tecnico".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
                    object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPMenuTecFirma + "tecnicoidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
                }
                assert object != null;
                JSONArray jsonArray = object.optJSONArray("tipoMenu");
                JSONObject jsonArrayChild = jsonArray.getJSONObject(0);
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", jsonArrayChild.optString("estatusidx"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", jsonArrayChild.optString("estEnServx"));
            } catch (Exception ignore) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ActualizaImagenEstatus();
        }
    }
}
