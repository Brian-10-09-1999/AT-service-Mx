package com.example.devolucionmaterial.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devolucionmaterial.Adapters.AdapaterListaSolicitudMaterial;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.SolicitudMaterialActivity;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.beans.BeanSMDetalles;
import com.example.devolucionmaterial.beans.BeansGlobales;
import com.example.devolucionmaterial.dialogs.Alert;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Usuario on 08/11/2016.
 */

public class FragmentSolicitudMaterial extends Fragment implements View.OnClickListener {

    private View view;
    private static Context context;
    private static String solicitud_refaccionid;
    private List<BeanSMDetalles> listaSolicitudMaterialesDetalles = new ArrayList<>();
    private ListView listView;
    private AdapaterListaSolicitudMaterial adaptador;
    private Button btnAcpetar, btnRechazar, btnAtras;
    private TextView txtFolio;
    private ProgressDialog pDialog;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_solicitud_material, container, false);
        context = getActivity();
        solicitud_refaccionid = getArguments().getString("solicitud_refaccionidfk", "");
        Log.e("solicitud_refaccionidfk", solicitud_refaccionid);
        setUpSolicitudMaterialDetalles();
        conexion();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    void setUpSolicitudMaterialDetalles() {
        listView = (ListView) view.findViewById(R.id.lvSolicitudMaterialDetalles);
        btnAcpetar = (Button) view.findViewById(R.id.btn_fsm_aceptar);
        btnRechazar = (Button) view.findViewById(R.id.btn_fsm_rechazar);
        btnAtras = (Button) view.findViewById(R.id.btn_fsm_atras);
        btnAcpetar.setOnClickListener(this);
        btnRechazar.setOnClickListener(this);
        btnAtras.setOnClickListener(this);
        txtFolio = (TextView) view.findViewById(R.id.txt_fsm_folio);
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
                try{
                    if (!(null == response.body())) {
                        respuestaServidor(response.body());
                    }
                }catch (Exception e){
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
                try{
                    Toast.makeText(context, response.body().getMessege(), Toast.LENGTH_SHORT).show();
                    if (response.body().getValue() == 1) {
                        cerrarFragmento();
                        ((SolicitudMaterialActivity) context).actualizarLista();
                        Toast.makeText(context, response.body().getMessege(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
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

            case R.id.btn_fsm_aceptar:
                Alert.confirmacionSM(context,
                        context.getResources().getString(R.string.titulo_dialog),
                        context.getResources().getString(R.string.desc_dialog_aceptar), 87);
                break;
            case R.id.btn_fsm_rechazar:
                Alert.confirmacionSM(context,
                        context.getResources().getString(R.string.titulo_dialog),
                        context.getResources().getString(R.string.desc_dialog_rechazar), 31);
                break;
            case R.id.btn_fsm_atras:
                cerrarFragmento();
                break;
        }
    }

    // TODO: 09/11/2016 este metodo cierra el fragemnto en un clase static
    static void cerrarFragmento() {
        ((SolicitudMaterialActivity) context).getSupportFragmentManager().popBackStack("SMfragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


}
