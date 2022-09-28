package com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.materialesSeccion.Registro;
import com.example.devolucionmaterial.activitys.LectorActivity;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.adapter.PedidosQrApapter;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model.Datos;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.data_base.dbPedidosQr.DBPedidoQrManger;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidosListQrActivity extends AppCompatActivity implements PedidosQrApapter.ClickListener {

    private List<Datos> pedidosGuardadosList;
    private RecyclerView rvMedios;
    private TextView tvNoData;
    private PedidosQrApapter mAdapter;
    private DBPedidoQrManger dbPedidoQrManger;

    private ProgressDialog pDialog;
    BDmanager manager;

    private final static int RESULT_LECTOR = 100;

    private PedidosQrApapter.ViewHolderCustom viewHolderCustom;
    private Datos datos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_list_qr);
        initToolbar();


        rvMedios = (RecyclerView) findViewById(R.id.aplq_rv_lista_guardados);
        tvNoData = (TextView) findViewById(R.id.aplq_tv_no_hay_datos);

        dbPedidoQrManger = new DBPedidoQrManger(this).open();

        //dbPedidoQrManger.borrartodo();
        manager = new BDmanager(this);
        pedidosGuardadosList = new ArrayList<>();
        pedidosGuardadosList = dbPedidoQrManger.getListPedidosGuardados();

        mAdapter = new PedidosQrApapter(this, pedidosGuardadosList);
        mAdapter.setClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvMedios.setLayoutManager(mLayoutManager);
        rvMedios.setItemAnimator(new DefaultItemAnimator());
        rvMedios.setAdapter(mAdapter);




        if (pedidosGuardadosList.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
             //toastAlert("No tienes ningun Pedido");
            /*
            dbPedidoQrManger.insert(5000, "20002238", "350025 002237", "9363", " MEX-297048-2017", 162, "BIG BOLA BOCA DEL RIO", 0,-1);
            for (int i = 0; i < 50; i++) {
                if (i % 2 != 0) {
                    dbPedidoQrManger.insert(5000 + i, "20002238", "350025 002237", "9363", " MEX-297048-2017", 162, "BIG BOLA BOCA DEL RIO", 2,-1);
                } else {
                    dbPedidoQrManger.insert(5000 + 1, "20002238", "350025 002237", "9363", " MEX-297048-2017", 162, "BIG BOLA BOCA DEL RIO", 3,-1);

                }

            }

            */

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            final String guia = data.getStringExtra("result");
            if (requestCode == RESULT_LECTOR) {
                new MaterialDialog.Builder(this)
                        .title("¿Enviar?")
                        .content("Guía " + guia)
                        .cancelable(false)
                        .negativeText("Cancelar")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .positiveText(R.string.aceptar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                sendGuia(datos, viewHolderCustom, guia);
                            }
                        })
                        .show();

            }

        }

        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }


    }




    @Override
    public void itemClicked(int position, PedidosQrApapter.ViewHolderCustom viewHolderCustom, int status) {
        Datos datos = pedidosGuardadosList.get(position);


        if (status == 3) {
            Intent iLector = new Intent(this, LectorActivity.class);
            startActivityForResult(iLector, RESULT_LECTOR);
            this.viewHolderCustom = viewHolderCustom;
            this.datos = datos;

        }
        if (status == 2) {
            devolution(datos, viewHolderCustom);

        }

        if (status == 1) {
            send(Integer.parseInt(datos.getPedido()), datos.getComponenteNuevo(), datos.getComponenteAnterior(), datos.getCodMaquina(), viewHolderCustom);

        }
        if (status == 0) {
            Intent i = new Intent(this, ComponentActivity.class);
            i.putExtra("pedido", Integer.parseInt(datos.getPedido()));
            startActivity(i);
            finish();

        }
    }



    /**
     * se crea este metodo para recibir el codigo de barras de la guia
     *
     * @param datos            es el objeto de la lista con lo informacion del pedido
     * @param viewHolderCustom es las vista del Recyclearview para modificarla
     * @param guia             es el el dato que viene del escanner
     */




    void sendGuia(Datos datos, PedidosQrApapter.ViewHolderCustom viewHolderCustom, String guia) {

        pDialog = new ProgressDialog(this);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);

        /**
         * se usa de @link{@BeanResponse} solo result y comment
         *
         * */
        ServiceApi serviceApi= ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanResponse> call= serviceApi.enviarGuia(datos.getDevolucionId(),guia );

        call.enqueue(new Callback<BeanResponse>() {
            @Override
            public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                pDialog.dismiss();
                if(response.body().getResult() == 1){

                    updateGuia();

                }else if(response.body().getResult() == 0){

                    new MaterialDialog.Builder(PedidosListQrActivity.this)
                            .title("Error")
                            .content(response.body().getMessege())
                            .iconRes(R.drawable.ic_alert_material)
                            .cancelable(false)
                            .positiveText("Atras")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })
                            .show();

                }

                else{
                    new MaterialDialog.Builder(PedidosListQrActivity.this)
                            .title("Error")
                            .content("Ocurrio un error en la solicitud")
                            .iconRes(R.drawable.ic_alert_material)
                            .cancelable(false)
                            .positiveText("Atras")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })
                            .show();

                }

            }

            @Override
            public void onFailure(Call<BeanResponse> call, Throwable t) {
                pDialog.dismiss();
                new MaterialDialog.Builder(PedidosListQrActivity.this)
                        .title("Error")
                        .content("Ocurrio un error en la solicitud")
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(false)
                        .positiveText("Atras")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();
            }
        });


    }


    /**
     * se accede a este metodo cuando  la giua fue actualizada correctamente
     * y se actuiliza la base de telefono
     * */
    public void updateGuia(){

        if (dbPedidoQrManger.updatePedido(Integer.parseInt(datos.getPedido()), 4) > 0) {

            this.viewHolderCustom.btnGuia.setVisibility(View.GONE);
            this.datos = null;
            this.viewHolderCustom = null;

        }
    }

    /**
     * se crea este metodo para recibir el codigo de barras de la guia
     *
     * @param pedido                  es el numero del pedido
     * @param componenteAnterior      es el numero del pedido
     * @param viewHolderCustom        es las vista del Recyclearview para modificarla
     */
    void send(final int pedido, String componenteAnterior, String componenteNuevo, String codMaquina, final PedidosQrApapter.ViewHolderCustom viewHolderCustom) {

        pDialog = new ProgressDialog(this);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);


        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanResponse> call = serviceApi.sendValidationPedido(
                progressData(codMaquina)[0], componenteAnterior, componenteNuevo, BDVarGlo.getVarGlo(this, "INFO_USUARIO_ID"), pedido);
        call.enqueue(new Callback<BeanResponse>() {
            @Override
            public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                pDialog.dismiss();
                // TODO: 28/08/2017 solo se usa result
                if (response.body().getResult() == 1) {

                    if (dbPedidoQrManger.updatePedido(pedido, 2) > 0) {
                        viewHolderCustom.tvEstastatus.setText("Falta devolución");
                        viewHolderCustom.tvEstastatus.setTextColor(getResources().getColor(R.color.red));
                        viewHolderCustom.btnStatus.setVisibility(View.VISIBLE);


                    }

                    new MaterialDialog.Builder(PedidosListQrActivity.this)
                            .title("Correcto")
                            .content("Remplazo completo")
                            .iconRes(R.drawable.os_ok)
                            .cancelable(false)
                            .negativeText("Devolución")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })
                            .positiveText(R.string.aceptar)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                                }
                            })
                            .show();
                } else {
                    new MaterialDialog.Builder(PedidosListQrActivity.this)
                            .title("Error")
                            .content("Ocurrio un error en la solicitud")
                            .iconRes(R.drawable.ic_alert_material)
                            .cancelable(false)
                            .positiveText("Atras")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })
                            .show();
                }

            }

            @Override
            public void onFailure(final Call<BeanResponse> call, Throwable t) {
                pDialog.dismiss();
                new MaterialDialog.Builder(PedidosListQrActivity.this)
                        .title("Error al conectar")
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(true)
                        .positiveText("Atras")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();
            }
        });

    }


    void devolution(Datos datos, final PedidosQrApapter.ViewHolderCustom viewHolderCustom) {

        final String componenteNuevo, componenteAnterior, codMaquina, folio, sala;
        final int pedido, idtecnico;

        pedido = Integer.parseInt(datos.getPedido());
        componenteNuevo = datos.getComponenteNuevo();
        componenteAnterior = datos.getComponenteAnterior();
        codMaquina = datos.getCodMaquina();
        folio = datos.getFolio();
        idtecnico = datos.getId_tecnico();
        sala = datos.getSala();


        pDialog = new ProgressDialog(this);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);


        String valSala = sala;
        manager.actualiza("insert into csalida(usuarioidfk, officeID, fecha, estatus)" +
                "values(" + BDVarGlo.getVarGlo(this, "INFO_USUARIO_ID") + ",(select officeID from csala where nombre='" + valSala + "'),date('now'),'1')");
        Cursor fila = manager.consulta("SELECT salidaid,officeID FROM csalida ORDER BY salidaid desc LIMIT 1");
        fila.moveToFirst();
        final int salidaid = fila.getInt(0);
        final int officeID = fila.getInt(1);
        fila.close();
        manager.insertarSala(valSala);
        Cursor cursorSala = manager.cargarCursorSala();
        cursorSala.moveToLast();
        final int idSala = cursorSala.getInt(0);
        cursorSala.close();
        String tipo = "m";
        tipo = "ORDINARIA";



        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanResponse> call = serviceApi.devolutionQR(pedido, componenteAnterior, folio, idtecnico, salidaid);
        final String finalTipo = tipo;
        call.enqueue(new Callback<BeanResponse>() {
            @Override
            public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                pDialog.dismiss();
                if (response.body().getResult() == 1) {
                    // TODO: 30/08/2017 salida id es el consecutivo
                    int insert = manager.insertarSalida(
                            idSala, salidaid, Integer.parseInt(BDVarGlo.getVarGlo(PedidosListQrActivity.this, "INFO_USUARIO_ID"))
                            , Registro.obtenerHora(), "2", response.body().getDevolucionid(), officeID, "pendiente", "pendiente", finalTipo, "0");

                    int clave = Integer.valueOf(componenteAnterior.substring(0, 6).trim());
                    Cursor cursorGeneral = manager.buscarClave(clave);
                    cursorGeneral.moveToFirst();
                    int existePieza = cursorGeneral.getCount();
                    int idSustituto = cursorGeneral.getInt(5);
                    String descripcion = cursorGeneral.getString(3);
                    int vlaor = manager.insertarDescripcionSalida(salidaid, componenteAnterior, descripcion, Integer.valueOf(1), "serie", "1", "maquina");

                    Log.e("response.body().getDevolucionid()",response.body().getDevolucionid() );
                    dbPedidoQrManger.updatePedidoDevolucionId(pedido, 3, Integer.parseInt(response.body().getDevolucionid()));
                    viewHolderCustom.btnStatus.setVisibility(View.GONE);
                    viewHolderCustom.tvEstastatus.setText("Completo");
                    viewHolderCustom.tvEstastatus.setTextColor(getResources().getColor(R.color.accentColor));

                    new MaterialDialog.Builder(PedidosListQrActivity.this)
                            .title("Delovución completa")
                            .content("Delovución " + response.body().getDevolucionid())
                            .iconRes(R.drawable.ic_alert_material)
                            .cancelable(false)
                            .positiveText("Aceptar")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                }
                            })
                            .show();

                } else {
                    // borarr lo el id qu eingre crear esa delete
                    manager.borarCsalida(salidaid);
                    new MaterialDialog.Builder(PedidosListQrActivity.this)
                            .title("Error")
                            .content("Ocurrio un error en la delovución")
                            .iconRes(R.drawable.ic_alert_material)
                            .cancelable(false)
                            .positiveText("Aceptar")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })
                            .show();


                }


            }

            @Override
            public void onFailure(Call<BeanResponse> call, Throwable t) {
                // borarr lo el id qu eingre crear esa delete
                pDialog.dismiss();
                manager.borarCsalida(salidaid);
                new MaterialDialog.Builder(PedidosListQrActivity.this)
                        .title("Error al conectar")
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(false)
                        .positiveText("Aceptar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();
            }
        });
    }



    // TODO: 03/02/2017 corta la cadena en un array
    String[] progressData(String dataQr) {
        String temp[] = null;
        try {
            String delimiter = ";";
            temp = dataQr.split(delimiter);
        } catch (Exception e) {
            Log.e("error progresdata ", "");
        }
        return temp;

    }


    private void toastAlert(String ms){
        Toast.makeText(getApplicationContext(),ms, Toast.LENGTH_LONG).show();}


}
