package com.example.devolucionmaterial.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devolucionmaterial.Adapters.AdapaterListaSolicitudMaterial;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanSolicitudMaterial;
import com.example.devolucionmaterial.beans.BeansGlobales;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.fragments.FragmentSolicitudMaterial;
import com.example.devolucionmaterial.services.CheckService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolicitudMaterialActivity extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener {
    private Context context;
    private List<BeanSolicitudMaterial> listSoliciudMateriales = new ArrayList<BeanSolicitudMaterial>();
    private ListView listview;
    private AdapaterListaSolicitudMaterial adaptador;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_material);
        context = this;
        setUpSolicitudMaterial();

        if (CheckService.internet(context)) {
            conexion();
        } else {
            Alert.ActivaInternet(context);
        }


    }

    void setUpSolicitudMaterial() {
        listview = (ListView) findViewById(R.id.lvSolicitudMaterial);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    void conexion() {

        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);

        // TODO: 08/11/2016 aqui se hace la instacia de retrofit
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);

        final Call<List<BeanSolicitudMaterial>> call = serviceApi.SOLICITUD_MATERIAL_CALL(
                BeansGlobales.FUNCIION_LISTA_MATERIALES, BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
        call.enqueue(new Callback<List<BeanSolicitudMaterial>>() {
            @Override
            public void onResponse(Call<List<BeanSolicitudMaterial>> call, Response<List<BeanSolicitudMaterial>> response) {

                try{
                    if (!(null == response.body() || response.body().isEmpty())) {
                        respuestaServidor(response.body());
                        pDialog.dismiss();
                    } else {
                        Toast.makeText(context, "Lista vacia", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                        finish();
                    }
                }catch (Exception e){
                    Toast.makeText(context, getString(R.string.vuelve_a_intertarlo), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<List<BeanSolicitudMaterial>> call, Throwable t) {
                Log.e("error ", String.valueOf(t));
            }
        });

    }

    private void respuestaServidor(List<BeanSolicitudMaterial> listaDatos) {
        listSoliciudMateriales.clear();
        for (int i = 0; i < listaDatos.size(); i++) {
            BeanSolicitudMaterial item = listaDatos.get(i);
            listSoliciudMateriales.add(new BeanSolicitudMaterial(
                    item.getSolicitud_refaccionid()
                    , item.getFolio_solicitudidfk()
                    , item.getFecha_creacion()
                    , item.getSalaidfk()));
        }
        if (!listaDatos.isEmpty() || listaDatos != null)
            inflarListView();
    }

    void inflarListView() {
        listview.setAdapter(adaptador =
                new AdapaterListaSolicitudMaterial(
                        context, R.layout.item_solicitud_material, listSoliciudMateriales) {

                    @Override
                    public void onEntrada(Object entrada, View view) {
                        TextView tvFolio = (TextView) view.findViewById(R.id.tvFolofioSM);
                        TextView tvSala = (TextView) view.findViewById(R.id.tvSalaSM);
                        TextView tvInsidencia = (TextView) view.findViewById(R.id.tvInsidenciaSM);
                        TextView tvFecha = (TextView) view.findViewById(R.id.tvFechaCreacionSM);

                        tvFolio.setText(String.valueOf(((BeanSolicitudMaterial) entrada).getSolicitud_refaccionid()).toString());
                        tvSala.setText(((BeanSolicitudMaterial) entrada).getSalaidfk());
                        tvInsidencia.setText(((BeanSolicitudMaterial) entrada).getFolio_solicitudidfk());
                        tvFecha.setText(((BeanSolicitudMaterial) entrada).getFecha_creacion());


                    }
                });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BeanSolicitudMaterial item = listSoliciudMateriales.get(i);
                fragmentDMDetalles(item.getSolicitud_refaccionid());
            }
        });
    }


    // TODO: 09/11/2016 se infla el fragmento al darle click a una de los item de la lista
    public void fragmentDMDetalles(int solicitud_refaccionid) {

        FragmentSolicitudMaterial fullReadFragment = new FragmentSolicitudMaterial();
        Bundle args = new Bundle();
        args.putString("solicitud_refaccionidfk", String.valueOf(solicitud_refaccionid));
        fullReadFragment.setArguments(args);

        SolicitudMaterialActivity.this.getSupportFragmentManager().beginTransaction().addToBackStack("SMfragment")
                .replace(R.id.container, fullReadFragment, "DESC").commit();

    }

    public void actualizarLista() {
        if (CheckService.internet(context))
            conexion();
        else
            Alert.ActivaInternet(context);

        adaptador.notifyDataSetChanged();

    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Update data in ListView
                if (CheckService.internet(context)) {
                    conexion();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Alert.ActivaInternet(context);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, 1000);
    }
}
