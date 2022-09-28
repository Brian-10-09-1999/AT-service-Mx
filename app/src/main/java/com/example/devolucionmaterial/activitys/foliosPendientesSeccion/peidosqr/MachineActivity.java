package com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model.Datos;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model.PedidoQr;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.chat.utils.MyBounceInterpolator;
import com.example.devolucionmaterial.data_base.dbPedidosQr.DBPedidoQrManger;
import com.google.zxing.Result;


import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MachineActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener, Callback<PedidoQr> {
    private ZXingScannerView mScannerView;
    private int contador;
    private String maquina;
    private String componente;
    private Toolbar toolbar;
    private TextView tvTitle, tvDesc;
    private Button btnRestar;
    String idtecnico, folio, sala;

    private ProgressDialog pDialog;
    private DBPedidoQrManger dbPedidoQrManger;

    private boolean onOffFlash=false;
    protected Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine);

        if (getIntent().getExtras() != null) {
            idtecnico = getIntent().getExtras().getString("tecnicoid");
            folio = getIntent().getExtras().getString("folio");
            sala = getIntent().getExtras().getString("sala");
        }
        dbPedidoQrManger = new DBPedidoQrManger(this).open();
        //dbPedidoQrManger.borrartodo();
        initToolbar();
        contador = 1;
        mScannerView = new ZXingScannerView(this);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
        // mScannerView.setFlash(true);


        tvTitle = (TextView) findViewById(R.id.am_tv_title);
        tvDesc = (TextView) findViewById(R.id.am_tv_desc);
        btnRestar = (Button) findViewById(R.id.am_btn_restar);
        tvTitle.setText("Escanear el codigo de la maquina ");
        tvTitle.setSelected(true);
        tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvTitle.setSingleLine(true);

        btnRestar.setOnClickListener(this);

    }

    void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_machine, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MenuItem item2 = mMenu.findItem(R.id.menu_machine_lamparita);

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;
            case R.id.restar_camera:
                Intent retsarActivity = new Intent(this, MachineActivity.class);
                retsarActivity.putExtra("tecnicoid",idtecnico);
                retsarActivity.putExtra("folio",folio);
                retsarActivity.putExtra("sala",sala);
                startActivity( retsarActivity);
                finish();
                return true;

            case R.id.menu_machine_lamparita:

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

    @Override
    public void onBackPressed() {
        if (contador == 1) {
            new MaterialDialog.Builder(MachineActivity.this)
                    .title("Salir del escaner ")
                    .content("¿Estas seguro de salir?")
                    .iconRes(R.drawable.ic_alert_material)
                    .cancelable(false)
                    .negativeText("Cancelar")
                    .positiveText(R.string.aceptar)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_CANCELED, returnIntent);
                            finish();
                            MachineActivity.super.onBackPressed();
                        }
                    })
                    .show();
        }

        if (contador == 2) {
            returnScarner();
        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        mScannerView = new ZXingScannerView(this);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cleanup in onPause()
        mScannerView.stopCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();
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
        switch (contador) {
            case 1:
                maquina = data;
                contador++;
                tvTitle.setText("Escanear el código del componente ");
                tvDesc.setText("Maquina: " + data);
                animate(tvTitle);
                animate(tvDesc);
                restarCamera();
                break;
            case 2:
                componente = data;
                //tvDesc.setText(tvDesc.getText().toString() + "\nAnterior: " + data);
                tvDesc.setText(tvDesc.getText().toString() + "\nComponente: " + data);
                animate(tvDesc);
                break;
        }

       // Log.e("contador", String.valueOf(contador));

        if (maquina != null && componente != null) {

            sentPedido(maquina, componente, folio, idtecnico);

        }


    }

    void restarCamera() {
        mScannerView = new ZXingScannerView(this);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();


    }

    void returnScarner() {
        contador = 1;
        tvDesc.setText("");
        maquina = null;
        componente = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.am_btn_restar:
                mScannerView.stopCamera();
                restarCamera();
                break;
        }
    }

    void sentPedido(String maquina, String componente, String folio, String idtecnico) {
        Log.e("maquina", maquina);
        Log.e("componente", componente);
        Log.e("folio", folio);
        Log.e("idtecnico", idtecnico);

        pDialog = new ProgressDialog(this);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);

        Call<PedidoQr> call = serviceApi.setVerificardatosPedido(maquina, componente, folio, idtecnico);
        call.enqueue(this);
    }






    @Override
    public void onResponse(Call<PedidoQr> call, Response<PedidoQr> response) {
        pDialog.dismiss();
        try {
             PedidoQr pq = response.body();

            if (pq.getRespuesta().equals("0")) {
                new MaterialDialog.Builder(MachineActivity.this)
                        .title("Códigos incorrectos")
                        .content("No se encontro máquina o componente")
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(false)
                        .positiveText(R.string.aceptar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .show();
            }
            if (pq.getRespuesta().equals("1")) {

                //va a otra vista para escanear el componente nuevo

                Datos datos = pq.getDatos();
                dbPedidoQrManger.insert(Integer.parseInt(datos.getPedido()), datos.getComponenteNuevo(),
                        datos.getComponenteAnterior(), datos.getCodMaquina(), folio, Integer.parseInt(idtecnico), sala, 0, -1);

                Intent i = new Intent(this, ComponentActivity.class);
                i.putExtra("pedido",  Integer.parseInt(datos.getPedido()));
                i.putExtra("anterior",datos.getComponenteAnterior());
                i.putExtra("nuevo",   datos.getComponenteNuevo());
                startActivity(i);
                finish();


            } else if (pq.getRespuesta().equals("2")) {
                new MaterialDialog.Builder(MachineActivity.this)
                        .title("Pedido de material")
                        .content("Se creo el pedido de material correctamente :" + pq.getPedido())
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(false)
                        .positiveText(R.string.aceptar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .show();
            } else if (pq.getRespuesta().equals("3")) {
                new MaterialDialog.Builder(MachineActivity.this)
                        .title("Se asocio correcetamente la pieza")
                        .content("Pedido :" + pq.getPedido())
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(false)
                        .positiveText(R.string.aceptar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .show();
            } else if (pq.getRespuesta().equals("4")) {
                new MaterialDialog.Builder(MachineActivity.this)
                        .title("Esta máquina no corresponde a esta incidencia")
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(false)
                        .positiveText(R.string.aceptar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .show();
            } else if (pq.getRespuesta().equals("5")) {
                new MaterialDialog.Builder(MachineActivity.this)
                        .title("Ya exite un pedido para esta refacción")
                        .content("Pedido :" + pq.getPedido())
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(false)
                        .positiveText(R.string.aceptar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .show();
            }
        } catch (Exception e) {
            new MaterialDialog.Builder(MachineActivity.this)
                    .title("Error")
                    .content("Ocurrio un error en la solicitud")
                    .iconRes(R.drawable.ic_alert_material)
                    .cancelable(false)
                    .negativeText("Volver escanear")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            returnScarner();
                            restarCamera();
                        }
                    })
                    .positiveText("Reintentar")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            sentPedido(maquina, componente, folio, idtecnico);
                        }
                    })
                    .show();

        }


    }







    @Override
    public void onFailure(Call<PedidoQr> call, Throwable t) {
        pDialog.dismiss();

        Log.e("error", t + "");
        new MaterialDialog.Builder(MachineActivity.this)
                .title("Error al conectar")
                .iconRes(R.drawable.ic_alert_material)
                .cancelable(false)
                .negativeText("Volver escanear")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        returnScarner();
                        restarCamera();
                    }
                })
                .positiveText("Reintentar")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        sentPedido(maquina, componente, folio, idtecnico);
                    }
                })
                .show();

    }





    void animate(View view) {
        // Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        double animationDuration = 2.0 * 1000;
        myAnim.setDuration((long) animationDuration);

        // Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.20, 20.0);

        myAnim.setInterpolator(interpolator);

        // Animate the button
        view.startAnimation(myAnim);
        //playSound();

        // Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //animateButton();
            }
        });
    }
}
