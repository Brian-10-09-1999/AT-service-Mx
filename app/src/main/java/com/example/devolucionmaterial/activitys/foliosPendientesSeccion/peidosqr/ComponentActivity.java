package com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.materialesSeccion.Registro;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model.Datos;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.data_base.dbPedidosQr.DBPedidoQrManger;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComponentActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener {
    private ZXingScannerView mScannerView;
    private int contador;
    private DBPedidoQrManger dbPedidoQrManger;

    private Datos datos;
    private String componenteNuevo, componenteAnterior, codMaquina, folio, sala;
    private int pedido, idtecnico;
    private Toolbar toolbar;
    private TextView tvTitle, tvDesc;
    private Button btnRestar;
    private ProgressDialog pDialog;
    BDmanager manager;

    private boolean onOffFlash=false;
    protected Menu mMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component);
        dbPedidoQrManger = new DBPedidoQrManger(this).open();
        manager = new BDmanager(this);
        if (getIntent().getExtras() != null) {
            datos = dbPedidoQrManger.getPeido(getIntent().getExtras().getInt("pedido"));

            initSetUp();
        }
        //devolution();

        initToolbar();

        mScannerView = new ZXingScannerView(this);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(com.odn.qr_manager.R.id.ZXingScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();


        tvTitle = (TextView) findViewById(R.id.ac_tv_title);
        tvDesc = (TextView) findViewById(R.id.ac_tv_desc);
        btnRestar = (Button) findViewById(R.id.ac_btn_resta);

        tvDesc.setText("Maquina: "+datos.getCodMaquina()+ "\nAnterior: "+datos.getComponenteAnterior());


        btnRestar.setOnClickListener(this);
        tvTitle.setText("Escanear Componente nuevo");

    }

    void initSetUp() {

        pedido = Integer.parseInt(datos.getPedido());
        componenteNuevo = datos.getComponenteNuevo();
        componenteAnterior = datos.getComponenteAnterior();
        codMaquina = datos.getCodMaquina();
        folio = datos.getFolio();
        idtecnico = datos.getId_tecnico();
        sala = datos.getSala();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        mScannerView.stopCamera();
        String data = result.getText();
        Log.e("data", data);
        Log.e("componenteNuevo", componenteNuevo);
        Log.e("folio", componenteNuevo);
        Log.e("idtecnico", String.valueOf(idtecnico));

        tvDesc.setText(tvDesc.getText().toString()+"\nNuevo: "+data);


        if (data.equals(componenteNuevo)) {
            dbPedidoQrManger.updatePedido(pedido, 1);

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
                        dbPedidoQrManger.updatePedido(pedido, 2);

                        // TODO: 23/08/2017 aqui falta el servico para cabiar el estatus a 2
                        new MaterialDialog.Builder(ComponentActivity.this)
                                .title("Correcto")
                                .content("Remplazo completo")
                                .iconRes(R.drawable.os_ok)
                                .cancelable(false)
                                .negativeText("Devolución")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        devolution();
                                    }
                                })
                                .positiveText(R.string.aceptar)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        finish();

                                    }
                                })
                                .show();
                    } else {
                        new MaterialDialog.Builder(ComponentActivity.this)
                                .title("Error")
                                .content("Ocurrio un error en la solicitud")
                                .iconRes(R.drawable.ic_alert_material)
                                .cancelable(false)
                                .negativeText("Salir")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        finish();
                                    }
                                })
                                .positiveText("Volver escanear")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        mScannerView.stopCamera();
                                        restarCamera();
                                    }
                                })
                                .show();
                    }

                }

                @Override
                public void onFailure(Call<BeanResponse> call, Throwable t) {
                    pDialog.dismiss();
                    new MaterialDialog.Builder(ComponentActivity.this)
                            .title("Error al conectar")
                            .iconRes(R.drawable.ic_alert_material)
                            .cancelable(false)
                            .negativeText("Salir")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    finish();
                                }
                            })
                            .positiveText("Volver escanear")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    mScannerView.stopCamera();
                                    restarCamera();
                                }
                            })
                            .show();
                }
            });

        } else {
            new MaterialDialog.Builder(ComponentActivity.this)
                    .title("Error")
                    .content("No es el mismo componente")
                    .iconRes(R.drawable.ic_alert_material)
                    .cancelable(false)
                    .negativeText("Salir")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish();
                        }
                    })
                    .positiveText("Volver escanear")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            mScannerView.stopCamera();
                            restarCamera();
                        }
                    })
                    .show();
        }


    }


    void devolution() {
        // manager.actualizarRegistroReporte(result[1], Registro.obtenerHora(), salidaid);
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
                            idSala, salidaid, Integer.parseInt(BDVarGlo.getVarGlo(ComponentActivity.this, "INFO_USUARIO_ID"))
                            , Registro.obtenerHora(), "2", response.body().getDevolucionid(), officeID, "pendiente", "pendiente", finalTipo, "0");

                    int clave = Integer.valueOf(componenteAnterior.substring(0, 6).trim());
                    Cursor cursorGeneral = manager.buscarClave(clave);
                    cursorGeneral.moveToFirst();
                    int existePieza = cursorGeneral.getCount();
                    int idSustituto = cursorGeneral.getInt(5);
                    String descripcion = cursorGeneral.getString(3);
                    int vlaor = manager.insertarDescripcionSalida(salidaid, componenteAnterior, descripcion, Integer.valueOf(1), "serie", "1", "maquina");


                    dbPedidoQrManger.updatePedidoDevolucionId(pedido, 3, Integer.parseInt(response.body().getDevolucionid()));
                    new MaterialDialog.Builder(ComponentActivity.this)
                            .title("Devolución completa")
                            .content("Devolución " + response.body().getDevolucionid())
                            .iconRes(R.drawable.ic_alert_material)
                            .cancelable(false)
                            .positiveText("Aceptar")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    finish();
                                }
                            })
                            .show();

                } else {
                    // borarr lo el id qu eingre crear esa delete
                    manager.borarCsalida(salidaid);
                    new MaterialDialog.Builder(ComponentActivity.this)
                            .title("Error")
                            .content("Ocurrio un error en la delovución")
                            .iconRes(R.drawable.ic_alert_material)
                            .cancelable(false)
                            .negativeText("Salir")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    finish();
                                }
                            })
                            .positiveText("Volver escanear")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    mScannerView.stopCamera();
                                    restarCamera();
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
                new MaterialDialog.Builder(ComponentActivity.this)
                        .title("Error al conectar")
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(false)
                        .negativeText("Salir")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .positiveText("Volver escanear")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mScannerView.stopCamera();
                                restarCamera();
                            }
                        })
                        .show();
            }
        });
    }



    /*public void ingresaLista(String codigo, String cantidad, String serie) {
        manager.actualiza("insert into rpiezas(salidaidfk, codigo, cantidad, serie, fecha,estatus)" +
                "values(" + salidaid + ",'" + codigo + "'," + cantidad + ",'" + serie + "',date('now'),'1')");


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }*/


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_btn_resta:
                mScannerView.stopCamera();
                restarCamera();
                break;
        }
    }

    void restarCamera() {
        mScannerView = new ZXingScannerView(this);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
        tvDesc.setText("Maquina: "+datos.getCodMaquina()+ "\nAnterior: "+datos.getComponenteAnterior());

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




    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lampara, menu);


        mMenu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MenuItem item2 = mMenu.findItem(R.id.lamparita);


        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;

            case R.id.lamparita:

                if(onOffFlash)
                { mScannerView.setFlash(false); onOffFlash=false;
                    item2.setIcon(R.drawable.ico_lampara1);
                }//do not forget to invalidate}}
                else { mScannerView.setFlash(true); onOffFlash=true;
                    item2.setIcon(R.drawable.ico_lampara2);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("escan compo");
        }
    }



}
