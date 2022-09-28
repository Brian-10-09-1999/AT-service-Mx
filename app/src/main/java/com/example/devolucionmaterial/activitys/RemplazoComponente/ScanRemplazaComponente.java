package com.example.devolucionmaterial.activitys.RemplazoComponente;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.MachineActivity;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model.PedidoQr;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.dbPedidosQr.DBPedidoQrManger;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanRemplazaComponente extends AppCompatActivity implements   Callback<ModelRC>   {

    private String metodo="(RemplazoComponente) --------- > ScanRemplazaComponente.";
    private Button btn_ScanComponente;


    protected Toolbar toolbar;


    private TextView tvShowCode;
    private EditText etScan;
    private Button butNewScan;
   //private String metodo="(RemplazoComponente) --------- > Scan3QR.";
    private int contador;
    private Button btnRestar;
    private ProgressDialog pDialog;
    private String maquina,componente, componenteNew,idtecnico,OrdenId;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_remplaza_componente);

        initToolbar("Remplazo Componente");
        btn_ScanComponente=(Button) findViewById(R.id.btn_scan_remplazo);
        butNewScan=(Button) findViewById(R.id.btn_new_scan);
        tvShowCode=(TextView) findViewById(R.id.tv_show_codes);
        etScan=(EditText) findViewById(R.id.et_read_code);


        tvShowCode.setText("Escanear Maquina ");

        idtecnico= BDVarGlo.getVarGlo(ScanRemplazaComponente.this, "INFO_USUARIO_ID");
        OrdenId="0";

        contador = 1;

        btn_ScanComponente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iLector = new Intent(ScanRemplazaComponente.this, Scan3QR.class);
                iLector.putExtra("tecnicoid", BDVarGlo.getVarGlo(ScanRemplazaComponente.this, "INFO_USUARIO_ID"));
                iLector.putExtra("ordenid", 0);
                startActivity(iLector);

            }
        });


        etScan.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                   Log.i("-------------- Scan ","press Enter");
                   handleResult(etScan.getText().toString());
                    etScan.setText("");



                    return true;
                }

               // btn_ScanComponente.setText(":"+keyCode+":");


                return false;
            }
        });


        butNewScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                returnScarner();

            }
        });


    }




    protected void initToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }



    public void handleResult(String data) {

        metodo=metodo+"handleResult()";



        switch (contador) {
            case 1:
                maquina = data;
                contador++;
               // tvTitle.setText("Escanear Componente ");
                tvShowCode.setText("Maquina: " + data);


                break;
            case 2:
                componente = data;
                contador++;
               // tvTitle.setText("Escanear Remplazo de Componente");
                tvShowCode.setText(tvShowCode.getText().toString() + "\nComponente: " + data);


                break;
            case 3:
                componenteNew = data;
                tvShowCode.setText(tvShowCode.getText().toString() + "\nRemplazo: " + data);

                break;
        }

        Log.e(metodo, String.valueOf(contador));

        if (maquina != null && componente != null && componenteNew !=null) {
            sendRemplazo(maquina, componente, componenteNew, idtecnico);
            Log.i("------------ Scan","maquina:"+maquina+"      componente:"+componente+"     comp new:"+componenteNew+"     contador:"+contador);
        }



    }



    void returnScarner() {
        tvShowCode.setText("Escanear Maquina");
        contador = 1;
      //  tvDesc.setText("");
        maquina = null;
        componente = null;
        componenteNew=null;
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
                new MaterialDialog.Builder(ScanRemplazaComponente.this)
                        .title("Error Codigos Incorrectos")
                        .content(pq.getAlerta())
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(false)
                        .positiveText(R.string.aceptar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                               // finish();
                            }
                        })
                        .show();
            }
            if (pq.getResult().equals("1")) {


                toastAlert("Se Realizó El Remplazo Correctamente");



                new MaterialDialog.Builder(ScanRemplazaComponente.this)
                        .title("Escanear Otro Remplazo")
                        .content("Desea realizar nuevo reemplazo sobre la misma OS?")
                        .iconRes(R.drawable.ic_alert_material)
                        .cancelable(false)
                        .negativeText("Si")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                               returnScarner();
                               // restarCamera();

                            }
                        })
                        .positiveText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                             //   finish();

                            }
                        })
                        .show();




            }
        } catch (Exception e) {
            new MaterialDialog.Builder(ScanRemplazaComponente.this)
                    .title("Error")
                    .content("Ocurrio un error en la solicitud")
                    .iconRes(R.drawable.ic_alert_material)
                    .cancelable(false)
                    .negativeText("Volver escanear")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                           returnScarner();
                           // restarCamera();
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
        new MaterialDialog.Builder(ScanRemplazaComponente.this)
                .title("Error al Conectar Con Servidor ")
                .iconRes(R.drawable.icon_error_sever)
                .cancelable(false)
                .negativeText("Volver escanear")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //returnScarner();
                        //restarCamera();
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






    private void toastAlert(String ms){
        Toast.makeText(getApplicationContext(),ms, Toast.LENGTH_LONG).show();}



}
