package com.example.devolucionmaterial.firebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devolucionmaterial.Adapters.AdapaterListaSolicitudMaterial;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.MenuInicial;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.beans.BeanSMDetalles;
import com.example.devolucionmaterial.beans.BeansGlobales;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.fragments.FragmentSolicitudMaterial;
import com.example.devolucionmaterial.services.CheckService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolicitudMaterialDetallesNotificacion extends Activity implements View.OnClickListener {

    private static Context context;
    private static String solicitud_refaccionid;
    private List<BeanSMDetalles> listaSolicitudMaterialesDetalles = new ArrayList<>();
    private ListView listView;
    private AdapaterListaSolicitudMaterial adaptador;
    private Button btnAcpetar, btnRechazar, btnMenu;
    private TextView txtFolio;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_material_detalles_notificacion);
        solicitud_refaccionid = getIntent().getExtras().getString("solicitud_refaccionidfk");
        context = this;
        setUpSolicitudMaterialDetalles();
        if (CheckService.internet(context))
            conexion();
        else
            Alert.ActivaInternet(context);


    }

    void setUpSolicitudMaterialDetalles() {
        listView = (ListView) findViewById(R.id.lvSolicitudMaterialDetallesNotificaciones);
        btnAcpetar = (Button) findViewById(R.id.btn_asmn_aceptar);
        btnRechazar = (Button) findViewById(R.id.btn_asmn_rechazar);
        btnMenu = (Button) findViewById(R.id.btn_asmn_menu);
        btnAcpetar.setOnClickListener(this);
        btnRechazar.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        txtFolio = (TextView) findViewById(R.id.txt_asmn_folio);
        txtFolio.setText(solicitud_refaccionid);
    }

    void conexion() {

        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<List<BeanSMDetalles>> call =
                serviceApi.SOLICITUD_MATERIAL_CALL_DETALLES( BeansGlobales.FUNCIION_LISTA_MATERIALES_DETALLES, solicitud_refaccionid);
        call.enqueue(new Callback<List<BeanSMDetalles>>() {
            @Override
            public void onResponse(Call<List<BeanSMDetalles>> call, Response<List<BeanSMDetalles>> response) {
                pDialog.dismiss();

                try {
                    if (!(null == response.body())) {
                        respuestaServidor(response.body());
                    }
                } catch (Exception e) {
                    Toast.makeText(context, getString(R.string.vuelve_a_intertarlo), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<List<BeanSMDetalles>> call, Throwable t) {
                pDialog.dismiss();
            }
        });
    }

    private void respuestaServidor(List<BeanSMDetalles> listaDatos) {
        for (int i = 0; i < listaDatos.size(); i++) {
            BeanSMDetalles item = listaDatos.get(i);
            listaSolicitudMaterialesDetalles.add(new BeanSMDetalles(item.getClave(), item.getNombre(), item.getCantidad()));
            if (!listaDatos.isEmpty() || listaDatos != null)
                inflarListView();
        }
    }

    void inflarListView() {
        listView.setAdapter(adaptador =
                new AdapaterListaSolicitudMaterial(
                        context, R.layout.item_solicitud_material_detalles, listaSolicitudMaterialesDetalles) {

                    @Override
                    public void onEntrada(Object entrada, View view) {
                        TextView tvCodigo = (TextView) view.findViewById(R.id.tvCodigo);
                        TextView tvNombre = (TextView) view.findViewById(R.id.tvNombre);
                        TextView tvCantidad = (TextView) view.findViewById(R.id.tvCantidad);

                        tvCodigo.setText(((BeanSMDetalles) entrada).getClave());
                        tvNombre.setText(((BeanSMDetalles) entrada).getNombre());
                        tvCantidad.setText(((BeanSMDetalles) entrada).getCantidad());


                    }
                });
    }

    public static void conexionActulizar(int estatusidfk) {

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanResponse> call =
                serviceApi.ACTUALIZAR_SM_CALL(BeansGlobales.FUNCIION_LISTA_MATERIALES_ACTUALIZAR_ESTATUS, estatusidfk, solicitud_refaccionid);
        call.enqueue(new Callback<BeanResponse>() {
            @Override
            public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {

                try {

                    Toast.makeText(context, response.body().getMessege(), Toast.LENGTH_SHORT).show();
                    if (response.body().getValue() == 1) {

                        Toast.makeText(context, response.body().getMessege(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, context.getString(R.string.vuelve_a_intertarlo), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<BeanResponse> call, Throwable t) {
                Toast.makeText(context, "Vuelve a intertarlo", Toast.LENGTH_SHORT).show();
                Log.e("error", String.valueOf(t));
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_asmn_aceptar:
                Alert.confirmacionSM(context,
                        context.getResources().getString(R.string.titulo_dialog),
                        context.getResources().getString(R.string.desc_dialog_aceptar), 87);
                break;
            case R.id.btn_asmn_rechazar:
                Alert.confirmacionSM(context,
                        context.getResources().getString(R.string.titulo_dialog),
                        context.getResources().getString(R.string.desc_dialog_rechazar), 31);
                break;
            case R.id.btn_asmn_menu:
                // intent para abrir el menu
                Intent menuIntent = new Intent(SolicitudMaterialDetallesNotificacion.this, MenuInicial.class);
                startActivity(menuIntent);
                break;
        }
    }


}
