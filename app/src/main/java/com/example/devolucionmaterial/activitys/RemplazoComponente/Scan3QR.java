package com.example.devolucionmaterial.activitys.RemplazoComponente;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.ComponentActivity;
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

public class Scan3QR extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener, Callback<ModelRC> {
    private ZXingScannerView mScannerView;
    private String metodo="(RemplazoComponente) --------- > Scan3QR.";
    private int contador;

    private String maquina,componente, componenteNew,idtecnico,OrdenId;
    private Toolbar toolbar;
    private TextView tvTitle, tvDesc;
    private Button btnRestar;

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
            OrdenId=getIntent().getExtras().getString("ordenid");

        }


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
        tvTitle.setText("Escanear Maquina ");
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
                Intent retsarActivity = new Intent(this, Scan3QR.class);
                retsarActivity.putExtra("tecnicoid",idtecnico);
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
            new MaterialDialog.Builder(Scan3QR.this)
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
                            Scan3QR.super.onBackPressed();
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

        metodo=metodo+"handleResult()";

        mScannerView.stopCamera();
        String data = result.getText();
        switch (contador) {
            case 1:
                maquina = data;
                contador++;
                tvTitle.setText("Escanear Componente ");
                tvDesc.setText("Maquina: " + data);
                animate(tvTitle);
                animate(tvDesc);
                restarCamera();
                break;
            case 2:
                componente = data;
                contador++;
                tvTitle.setText("Escanear Remplazo de Componente");
                tvDesc.setText(tvDesc.getText().toString() + "\nComponente: " + data);
                animate(tvTitle);
                animate(tvDesc);
                restarCamera();
                break;
            case 3:
                componenteNew = data;
                tvDesc.setText(tvDesc.getText().toString() + "\nRemplazo: " + data);
                animate(tvDesc);
                break;
        }

        Log.e(metodo, String.valueOf(contador));

        if (maquina != null && componente != null && componenteNew !=null) {

            sendRemplazo(maquina, componente, componenteNew, idtecnico);
        }


    }

    void restarCamera() {
        mScannerView = new ZXingScannerView(this);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();


    }



    void returnScarner() {
        tvTitle.setText("Escanear Maquina");
        contador = 1;
        tvDesc.setText("");
        maquina = null;
        componente = null;
        componenteNew=null;
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

    void sendRemplazo(String maquina, String componente, String componenteNew, String idtecnico) {
        metodo=metodo+"sendRemplazo()";

        Log.i(metodo,"  maquina:"+ maquina);
        Log.i(metodo,"  componente:" +componente);
        Log.i(metodo,"  nuevo Componente:"+ componenteNew);
        Log.i(metodo,"  idTecnico:" +idtecnico);

        pDialog = new ProgressDialog(this);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);

        Call<ModelRC> call = serviceApi.RemplazoComponente(maquina, componente, componenteNew, idtecnico,OrdenId);//el orden id solo se usa para enviar folio desde "OS asiganadas"
        call.enqueue(this);
    }







    @Override
    public void onResponse(Call<ModelRC> call, Response<ModelRC> response) {
        pDialog.dismiss();
        try {
             ModelRC pq = response.body();

            if (pq.getResult().equals("0")) {
                new MaterialDialog.Builder(Scan3QR.this)
                        .title("Error Codigos Incorrectos")
                        .content(pq.getAlerta())
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
            if (pq.getResult().equals("1")) {


                toastAlert("Se Realizó El Remplazo Correctamente");



                new MaterialDialog.Builder(Scan3QR.this)
                        .title("Escanear Otro Remplazo")
                        .content("Desea realizar nuevo reemplazo sobre la misma OS?")
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(false)
                        .negativeText("Si")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                returnScarner();
                                restarCamera();

                            }
                        })
                        .positiveText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                                finish();

                            }
                        })
                        .show();




            }
        } catch (Exception e) {
            new MaterialDialog.Builder(Scan3QR.this)
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
                            contador=1;
                        }
                    })
                    .positiveText("Reintentar")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            sendRemplazo(maquina, componente, componenteNew, idtecnico);
                        }
                    })
                    .show();

        }


    }







    @Override
    public void onFailure(Call<ModelRC> call, Throwable t) {
        pDialog.dismiss();



        Log.e(metodo+"onFailure()", "  Error:"+t);
        new MaterialDialog.Builder(Scan3QR.this)
                .title("Error al Conectar Con Servidor ")
                .iconRes(R.drawable.icon_error_sever)
                .cancelable(false)
                .negativeText("Volver escanear")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        returnScarner();
                        restarCamera();
                        contador=1;
                    }
                })
                .positiveText("Reintentar")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        sendRemplazo(maquina, componente, componenteNew, idtecnico);
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

    private void toastAlert(String ms){
        Toast.makeText(getApplicationContext(),ms, Toast.LENGTH_LONG).show();}




}
