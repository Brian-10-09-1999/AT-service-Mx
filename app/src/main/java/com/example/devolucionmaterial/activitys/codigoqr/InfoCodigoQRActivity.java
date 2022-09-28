package com.example.devolucionmaterial.activitys.codigoqr;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.annca.internal.configuration.AnncaConfiguration;
import com.example.devolucionmaterial.MenuInicialTecnico;
import com.example.devolucionmaterial.PermissionActivity;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.activitys.codigoqr.adapter.ComponentAdapter;
import com.example.devolucionmaterial.activitys.codigoqr.adapter.customListCompoAdapter;
import com.example.devolucionmaterial.activitys.codigoqr.model.Componente;
import com.example.devolucionmaterial.activitys.codigoqr.model.InfoQR;
import com.example.devolucionmaterial.activitys.viaticos.Activity_Lista;
import com.example.devolucionmaterial.api.PostGet;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.chat.activitys.NewGroupActivity;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.dialogs.ToastManager;
import com.google.zxing.Result;
import com.thanosfisherman.mayi.PermissionBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoCodigoQRActivity extends BaseActivity implements ComponentAdapter.ClickListener, ZXingScannerView.ResultHandler {

    private RecyclerView rvPiezas;
    private TextView tvSerie, tvQR, tvNoHayElemnto,tvSala;
    private String codigo;
    private ComponentAdapter componentAdapter;
    private ArrayList<Componente> lcomponent;
    private ZXingScannerView mScannerView;
    private String resItem[];

    private Button  btnReiniciarCamara,btnHideCamara,btnLamparita;
    private boolean onOffFlash=false;

    private ProgressDialog pDialog;
    private String metodo="activitys.codigoqr.InfoCodigoQRActivity.";
    private String url1="codigoQR/creaFolioQRInc.php?";
    private String baseUrl="";
    private RelativeLayout lyHideCamara;
    private boolean showcamera=true;
    public boolean isScanMaquina=false;


    private ListView list;//para mostrar la lista de componentes de la maquina
    customListCompoAdapter adapter = null;
    private final int SELECCIONA_QR = 201;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_codigo_qr);
        context=this;
        if (getIntent().getExtras() != null) {
            codigo = getIntent().getExtras().getString("codigo");
            //codigo = "3768;10/07178;D2BLUEW1;BLUEWAVE";
            //codigo = "601036 000134";
        }
        initToolbar("Escanea Máquina", true, false);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION") + "Android/";
        }
        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRUEBAS") + "Android/";
        }
        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION-LOCAL")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION_LOCAL") + "Android/";
        }


        lyHideCamara=(RelativeLayout) findViewById(R.id.ly_hide_camara);
        btnHideCamara=(Button) findViewById(R.id.btn_hide);
        list=(ListView) findViewById(R.id.list_componentes);
        btnLamparita=(Button) findViewById(R.id.btn_iqr_lamparita2);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        } else {
            initSetUp();
        }


        btnReiniciarCamara=(Button) findViewById(R.id.btn_reiniciar_cam);


        btnReiniciarCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky is clicked
              restarCamera();
              //  iniciaScanQr();
            }
        });


        btnHideCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky is clicked
                if(showcamera){

                    lyHideCamara.setVisibility(View.GONE);

                    btnHideCamara.setBackgroundResource(R.drawable.icon_camara_show);
                    showcamera=false;
                }
                else{

                    btnHideCamara.setBackgroundResource(R.drawable.icon_camara_hide);
                    lyHideCamara.setVisibility(View.VISIBLE);
                    showcamera=true;
                }



            }
        });

    }



    public void btnLampara(View v){
        if(onOffFlash)
        { mScannerView.setFlash(false); onOffFlash=false;
            btnLamparita.setBackgroundResource(R.drawable.ico_lampara1);
        }//do not forget to invalidate}}
        else { mScannerView.setFlash(true); onOffFlash=true;
            //item2.setIcon(R.drawable.ico_lampara2);
            btnLamparita.setBackgroundResource(R.drawable.ico_lampara2);
        }
    }



    void restarCamera() {

        mScannerView = new ZXingScannerView(this);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();


    }


    public void btnClickQrMaquina(View view){
        isScanMaquina=false; list.setAdapter(null);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Escanea Máquina");
        }
        //muestraInfo("error 37","4567","1234","4356maq");
    }

    void initSetUp() {
        //rvPiezas = (RecyclerView) findViewById(R.id.aicq_rv_piezas);
        tvQR = (TextView) findViewById(R.id.aicq_tv_qr);
        tvSerie = (TextView) findViewById(R.id.aicq_tv_serie);
        tvSala=(TextView)findViewById(R.id.aicq_tv_sala);

        tvNoHayElemnto = (TextView) findViewById(R.id.aicq_tv_no_hay_elementos);
        View view_toolbar_shadow_pre_lollipop = findViewById(R.id.view_toolbar_shadow_pre_lollipop);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            view_toolbar_shadow_pre_lollipop.setVisibility(View.GONE);

        }


    }

    private void scanMaquina(){

        lcomponent = new ArrayList<>();
        if (codigo != null)
        {
            tvQR.setText(codigo);


            pDialog = new ProgressDialog(InfoCodigoQRActivity.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);


            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<InfoQR> call = serviceApi.sendValidationCodeQR(codigo);
            call.enqueue(new Callback<InfoQR>() {
                @Override
                public void onResponse(Call<InfoQR> call, Response<InfoQR> response) {
                    try {
                        InfoQR infoQR = response.body();
                        assert infoQR != null;

                        if (infoQR.tipo == 1 || infoQR.tipo == 2) {



                            if (infoQR.tipo == 1) {
                                tvNoHayElemnto.setVisibility(View.GONE);
                                isScanMaquina=true;
                                tvQR.setText("Máquina " + codigo); //isScanMaquina=true;
                                tvSala.setText(infoQR.Sala);


                                if (getSupportActionBar() != null) {
                                    getSupportActionBar().setTitle("Escanea Componente");
                                }

                            } else if (infoQR.tipo == 2) {
                                tvQR.setText("Componente " + codigo);

                            }
                            if (infoQR.serieCompleta != null) {
                                tvSerie.setText(infoQR.serieCompleta);
                            } else {
                                tvSerie.setText("Sin serie");
                            }

                            if (infoQR.componentes != null) {
                                lcomponent = infoQR.componentes;
                                resItem= new String[lcomponent.size()];

                                for(int i=0;i<lcomponent.size();i++){resItem[i]="0";}


                                initRv(lcomponent,resItem);
                            } else {
                                tvNoHayElemnto.setVisibility(View.VISIBLE);
                            }
                        } else if (infoQR.tipo == 3) {
                            new MaterialDialog.Builder(context).
                                    title(R.string.error_codigo)
                                    .content("No se encontro el Código")
                                    .iconRes(R.drawable.warning)
                                    .positiveText(R.string.aceptar)
                                    //.negativeText(R.string.disagree)
                                    .show();
                        } else if (infoQR.tipo == 4) {
                            new MaterialDialog.Builder(context).
                                    title(R.string.codigo_incorrecto)
                                    .content("El código no es valido")
                                    .iconRes(R.drawable.warning)
                                    .positiveText(R.string.aceptar)
                                    //.negativeText(R.string.disagree)
                                    .show();
                        }



                        if (infoQR.tipo != 1) {  toastAlert("Escanea código de MAQUINA valido",1); list.setAdapter(null);  }


                    } catch (Exception e) {
                        new MaterialDialog.Builder(context).
                                title(R.string.error_conect)
                                .content("Ocurrio un error al procesar la información")
                                .iconRes(R.drawable.warning)
                                .positiveText(R.string.aceptar)
                                //.negativeText(R.string.disagree)
                                .show();
                    }
                    pDialog.dismiss();
                }

                @Override
                public void onFailure(Call<InfoQR> call, Throwable t) {
                    Log.e("erorrrrr", t + "");
                    pDialog.dismiss();

                    new MaterialDialog.Builder(context)
                            .content(R.string.error_conect)
                            .iconRes(R.drawable.warning)
                            .positiveText(R.string.aceptar)
                            //.negativeText(R.string.disagree)
                            .show();
                }
            });
        }
    }


    void initRv(ArrayList<Componente> lcomponent,String[] itemres) {

        adapter = new customListCompoAdapter(InfoCodigoQRActivity.this, lcomponent,itemres);
        list.setAdapter(adapter);


    }


    public void buscarCodigo(String code){


    boolean existe=false;

        for (int i=0;i< lcomponent.size();i++){
            Componente c = lcomponent.get(i);
            if(c.codigo.equals(code)){
                resItem[i]="1";
                existe=true;
            }

           Log.i("------------","codigo:"+c.codigo);
        }



        initRv(lcomponent,resItem);


        if(existe){
            toastAlert("Codigo Correcto",0);
        }
        else {
            //toastAlert(" no existe");
            new SolicitaInfoComponente(codigo, code).execute();
        }

    }



    @Override
    public void handleResult(Result result) {
        mScannerView.stopCamera();
        Log.i("info-----------------","handlerResult");
        String data = result.getText();


        if(!isScanMaquina){
            if(data.indexOf(";")!=-1){
                codigo=data;
                scanMaquina();
            }

           else{
                toastAlert("El código escaneado no pertenece a una Máquina",1);
            }
        }



        else{
            if(data.indexOf(" ")!=-1){
                buscarCodigo(data);
            }
            else{
                toastAlert("El código escaneado no pertenece a un Componente",1);
            }
        }

        restarCamera();

    }


     // ciclo de vida de la app---------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
         mScannerView = new ZXingScannerView(context);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(InfoCodigoQRActivity.this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();


        Log.i("info-----------------","onResume");
    }
    @Override
    protected void onRestart() {
        super.onRestart();


        mScannerView = new ZXingScannerView(context);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(InfoCodigoQRActivity.this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();


        Log.i("info-----------------","onRestart");

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cleanup in onPause()
        mScannerView.stopCamera();
        Log.i("info-----------------","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();
        Log.i("info-----------------","onStop");
    }




    //----------------------------------------------------------------------------------------------








    @Override
    public void onClick(int position) {

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



    private void toastAlert(String ms,int tipo) {
        //Toast.makeText(getApplicationContext(), ms, Toast.LENGTH_LONG).show();
        ToastManager.show(context, ms, tipo);

    }





    class SolicitaInfoComponente extends AsyncTask<Void, Void, Void> {
        //   ProgressDialog loading;
        String exito="0",gastoId="",respuesta="",maquina="",componente="",codigo="",refaccion="",serie="";
        private ProgressDialog pDialog;


        public SolicitaInfoComponente(String ma,String compo){
            maquina=ma;componente=compo;

        }


        protected void onPreExecute() {
            //     loading = ProgressDialog.show(Activity_Gastos.this, "Conectando...", null,true,false);
            pDialog = new ProgressDialog(InfoCodigoQRActivity.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        protected Void doInBackground(Void... JSONurl) {
            PostGet servicios=new PostGet(context);
            final JSONObject jsonParam = new JSONObject();

            try {

                jsonParam.put("tecnicoidx", BDVarGlo.getVarGlo(context,"INFO_USUARIO_ID"));
                jsonParam.put("maquinaidx",maquina);
                jsonParam.put("componentex",componente);


                Log.i(metodo+"SolicitaInfoComponente","Peticion Get Url="+ baseUrl+url1);
                Log.i(metodo+"SolicitaInfoComponente","Cuerpo="+ jsonParam.toString());

                respuesta=servicios.ConexionPost(baseUrl+url1,jsonParam);
                Log.i(metodo+"SolicitaInfoComponente","Respuesta="+respuesta);

                JSONObject json=new JSONObject(respuesta);
                exito=json.getString("respuesta");

                codigo=json.getString("codigo");
                refaccion=json.getString("refaccion");
                serie=json.getString("serie");


            } catch (Exception e) {
                Log.e(metodo+"SolicitaInfoComponente doInBackground", "Peticion Get  Error:" + e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(exito.equals("0")){
                toastAlert("No existe Máquina",1);

            }

            else if(exito.equals("1")){

                toastAlert("No existe el componente",1);
            }


            else if(exito.equals("2")){

                toastAlert("Ya existe un folio con esa información",0);
            }

            //muestra informacion en pantalla
            else if(exito.equals("3")) {
                muestraInfo("El código Escaneado no pertenece a esta máquina ",codigo,refaccion,serie);
            }

            else if(exito.equals("4")){
                toastAlert("Error al crear el folio",2);
            }


            else{
                toastAlert("Error (Server Response) ",2);
            }

            // loading.dismiss();
            pDialog.dismiss();
        }
    }



    public void muestraInfo(String error,String cod,String ref, String maqr){


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.info_codigo_qr, null);

        //EditText editText = (EditText) dialogView.findViewById(R.id.label_field);
        TextView tvCode=(TextView) dialogView.findViewById(R.id.tv_code);
        TextView tvrefaccion=(TextView) dialogView.findViewById(R.id.tv_refaccion);
        TextView tvmaqrel=(TextView) dialogView.findViewById(R.id.tv_maqrel);

         tvCode.setText(cod);
         tvrefaccion.setText(ref);
         tvmaqrel.setText(maqr);

        alertDialogBuilder.setView(dialogView);

        alertDialogBuilder.setTitle(error);


        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with discard

            }
        });

        alertDialogBuilder.show();

    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

        Log.i("qr-----------------","presback");
        mScannerView.stopCamera();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            Log.i("qr-----------------","home azul");
            mScannerView.stopCamera();
        }


        return super.onOptionsItemSelected(item);
    }







}
