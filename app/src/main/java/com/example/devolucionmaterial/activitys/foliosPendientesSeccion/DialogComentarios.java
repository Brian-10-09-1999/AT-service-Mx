package com.example.devolucionmaterial.activitys.foliosPendientesSeccion;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.viaticos.Activity_Lista;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.customview.AutoscaleEditText;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.services.CheckService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by jonh on 21/04/18.
 */

public class DialogComentarios extends DialogFragment {

    public TextView tvComent;
    public AutoscaleEditText etComment;
    public ScrollView scrollview;
    private String comentario="",folio="",returnComentarios="";
    private Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        comentario=getArguments().getString("comentarios");
        folio=getArguments().getString("folio");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());


        //builder = new AlertDialog.Builder(getActivity());
        context=getActivity().getApplicationContext();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.upload_coment, null);
        alertDialogBuilder.setView(view);
        tvComent = (TextView) view.findViewById(R.id.ul_txt_historial);
        etComment = (AutoscaleEditText) view.findViewById(R.id.ul_et_comennt);
        scrollview = (ScrollView) view.findViewById(R.id.ul_crollcomentario);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                // Handle the menu item
                return true;
            }
        });
        //toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setTitle("Comentarios");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });


        tvComent.setText(comentario);
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        ImageView btn = (ImageView) view.findViewById(R.id.ul_btn_enviar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etComment.getText().toString();
                if (!comment.equals("") && comment != null) {

                    String commentComplet = BDVarGlo.getVarGlo(context, "INFO_USUARIO_NOMBRE_COMPLETO") + " : "
                            + getDate() + " " + comment;

                    if (CheckService.internet(context))
                        sendComment(commentComplet);
                    else
                        Alert.ActivaInternet(context);



                    Log.e(" ------------   --- commnet", commentComplet);
                } else {
                    Toast.makeText(context, "Comentario vacio", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return alertDialogBuilder.create();

    }





    public String getDate() {

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        String deta = (DateFormat.format("dd MMM yyyy", new java.util.Date()).toString());
        today.setToNow();
        String sTime = today.format("%H:%M");

        String copleteDate = deta + " " + sTime;

        return copleteDate;
    }


    private void sendComment(String comentario) {

     /*  MaterialDialog  pDialog= new MaterialDialog.Builder(getActivity())
                .title(context.getString(R.string.Conectando_con_servidor_remoto))
                .content("Cargando...")
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(false)
                .show();*/

      ProgressDialog  pDialog = new ProgressDialog(getActivity());
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setCancelable(false);

        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);



        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanResponse> call = serviceApi.CARGAR_COMENTARIIO(folio, comentario);
        call.enqueue(new Callback<BeanResponse>() {
            @Override
            public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                pDialog.dismiss();
                try {
                    if (response.body().getComment() != null) {
                        returnComentarios = response.body().getComment();
                        setComentarios(returnComentarios);

                    }
                } catch (Exception e) {
                    Log.e("error", String.valueOf(e));

                }
            }

            @Override
            public void onFailure(Call<BeanResponse> call, Throwable t) {
                Log.e("error de respuesat", String.valueOf(t));
                pDialog.dismiss();
                Toast.makeText(context, getString(R.string.vuelve_a_intertarlo), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setComentarios(final String comentario) {
        tvComent.setText(comentario);
        etComment.setText("");
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }





}
