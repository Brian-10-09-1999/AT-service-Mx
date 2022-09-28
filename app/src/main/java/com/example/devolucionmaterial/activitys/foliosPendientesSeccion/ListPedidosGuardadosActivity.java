package com.example.devolucionmaterial.activitys.foliosPendientesSeccion;

import android.Manifest;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.adapter.CrearPedidoAdpter;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.CrearPedido;
import com.example.devolucionmaterial.beans.PedidosGuardados;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.DBCrearPedido;
import com.thanosfisherman.mayi.PermissionBean;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListPedidosGuardadosActivity extends BaseActivity implements CrearPedidoAdpter.ClickListener {
    private DBCrearPedido dbCrearPedido;

    private List<PedidosGuardados> pedidosGuardadosList;
    private RecyclerView rvMedios;
    private TextView tvNoData;
    private CrearPedidoAdpter mAdapter;

    private MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pedidos_guardados);
        initToolbar();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        } else {
            initSetUp();
        }



    }

    @Override
    protected void onRestart() {
        super.onRestart();
      /*  if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }*/

    }

    @Override
    public String[] permissions() {
        return new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CALL_PHONE,
                Manifest.permission.GET_ACCOUNTS
        };
    }

    @Override
    protected void permissionResultMulti(PermissionBean[] permissions) {
        Boolean val = true;
        //Toast.makeText(PermissionActivity.this, "MULTI PERMISSION RESULT " + Arrays.deepToString(permissions), Toast.LENGTH_LONG).show();
        for (int i = 0; i < permissions.length; i++) {
            if (!permissions[i].isGranted()) {
                val = false;
            }
        }
        if (val) {
            initSetUp();
        } else {
            showSnackBarPermission();
        }

    }

    private void initSetUp(){
        dbCrearPedido = new DBCrearPedido(this);
        dbCrearPedido.open();
        pedidosGuardadosList = new ArrayList<>();
        pedidosGuardadosList = dbCrearPedido.getListPedidosGuardados();

        rvMedios = (RecyclerView) findViewById(R.id.alpg_rv_lista_guardados);
        tvNoData = (TextView) findViewById(R.id.alpg_tv_no_hay_datos);

        mAdapter = new CrearPedidoAdpter(this, pedidosGuardadosList);
        mAdapter.setClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvMedios.setLayoutManager(mLayoutManager);
        rvMedios.setItemAnimator(new DefaultItemAnimator());
        rvMedios.setAdapter(mAdapter);
        //dbCrearPedido.insert(1, "30238;07/350051;black;black", "601042 004743", 0,"pedido" ,"folio" );
        if (pedidosGuardadosList.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
          //g   dbCrearPedido.insert(1, "30238;07/350051;black;black", "601042 004743", 0,"pedido" ,"folio" );

        }
    }

    void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pedidos guardados");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void itemClicked(final int position, final CrearPedidoAdpter.ViewHolderCustom holder, int status) {
        final PedidosGuardados pg = pedidosGuardadosList.get(position);

        if(status==1){

            if (dbCrearPedido.getStatus(pg.getId()) == 0) {
                new MaterialDialog.Builder(ListPedidosGuardadosActivity.this)
                        .title("¿Enviar pedido?")
                        //.content(pedido)
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(false)
                        .negativeText("Cancelar")
                        .positiveText(R.string.aceptar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                conexion(position, pg.getId(), pg.getSala(), pg.getMaquina(), pg.getComponente(),
                                        BDVarGlo.getVarGlo(ListPedidosGuardadosActivity.this, "INFO_USUARIO_ID"), pg.getFolio(), holder);
                            }
                        })
                        .show();

            }
        }else if(status==2){
            new MaterialDialog.Builder(ListPedidosGuardadosActivity.this)
                    .title("¿Cancelar pedido?")
                    //.content(pedido)
                    .iconRes(R.drawable.ic_alert_material)
                    .cancelable(false)
                    .negativeText("Cancelar")
                    .positiveText(R.string.aceptar)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            holder.tvEstastatus.setText("Cancelado");
                            int upadet = dbCrearPedido.update(pg.getId(), 2);
                            mAdapter.notifyDataSetChanged();
                        }
                    })
                    .show();
        }


    }

    void conexion(final int pos, final int id, int idSala, String maquina, String componente, String idTecnico, String folio, final CrearPedidoAdpter.ViewHolderCustom viewHolder) {
        materialDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.Conectando_con_servidor_remoto))
                .content("Cargando...")
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(false)
                .show();
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<CrearPedido> call = serviceApi.sendCrearPedido(idSala, maquina, componente, idTecnico, folio);
        call.enqueue(new Callback<CrearPedido>() {
            @Override
            public void onResponse(Call<CrearPedido> call, Response<CrearPedido> response) {
                materialDialog.dismiss();

                // TODO: 08/06/2017 cuando respuesta es 11 es correcto
                try {
                    if (response.body().getRespuesta() == 11) {

                        String pedido = "Número de pedido" + " " + response.body().getPedido() + "";
                        new MaterialDialog.Builder(ListPedidosGuardadosActivity.this)
                                .title("Pedido exitoso!!")
                                .content(pedido)
                                .iconRes(R.drawable.delivery_ok)
                                .cancelable(false)
                                .positiveText(R.string.aceptar)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        viewHolder.tvEstastatus.setText("Enviado");
                                        int upadet = dbCrearPedido.update(id, 1);
                                        mAdapter.notifyDataSetChanged();
                                        Log.e("upadte", upadet + "");
                                    }
                                })
                                .show();
                    } else {
                        errorPedido(response.body().getRespuesta());
                    }


                } catch (Exception e) {
                    Log.e("error", String.valueOf(e));
                    new MaterialDialog.Builder(ListPedidosGuardadosActivity.this)
                            .content("Ocurrio un error al procesar solicitud")
                            .iconRes(R.drawable.warning)
                            .positiveText(R.string.aceptar)
                            .cancelable(false)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<CrearPedido> call, Throwable t) {
                viewHolder.tvEstastatus.setText("Enviado");
                Log.e("error", t + "");
                materialDialog.dismiss();
                new MaterialDialog.Builder(ListPedidosGuardadosActivity.this)
                        .content("Ocurrio un error al conectar")
                        .iconRes(R.drawable.warning)
                        .positiveText(R.string.aceptar)
                        .cancelable(false)
                        .show();
            }
        });
    }


    void errorPedido(int codigo) {
        String mensaje = "";
        switch (codigo) {
            case 1:
                mensaje = "Código de la máquina incorrecto";
                break;
            case 2:
                mensaje = "Código de la máquina incorrecto";
                break;
            case 3:
                mensaje = "Código del componente incorrecto";
                break;
            case 4:
                mensaje = "Código del componente incorrecto";
                break;
            case 5:
                mensaje = "Código del componente incorrecto";
                break;
            case 6:
                mensaje = "El componente ya está asignado";
                break;

        }
        String codigosss = "Código de error 0" + codigo;

        new MaterialDialog.Builder(ListPedidosGuardadosActivity.this)
                .title(mensaje)
                .content(codigosss)
                .iconRes(R.drawable.warning)
                .positiveText(R.string.aceptar)
                .show();
    }
}
