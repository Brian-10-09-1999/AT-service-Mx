package com.example.devolucionmaterial.activitys.codigoqr;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.activitys.codigoqr.adapter.ComponentAdapter;
import com.example.devolucionmaterial.activitys.codigoqr.adapter.CustomListCompoSigns;
import com.example.devolucionmaterial.activitys.codigoqr.adapter.customListCompoAdapter;
import com.example.devolucionmaterial.activitys.codigoqr.model.Componente;
import com.example.devolucionmaterial.activitys.viaticos.Activity_Gastos;
import com.example.devolucionmaterial.api.PostGet;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.dialogs.ToastManager;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.google.zxing.Result;
import com.google.zxing.qrcode.decoder.QRCodeDecoderMetaData;
import com.thanosfisherman.mayi.PermissionBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class infoCodigoQrSings extends BaseActivity implements  ZXingScannerView.ResultHandler  {


    private ZXingScannerView mScannerView;
    private String serie[],codigo[],clave[],refaccion[];
    private Button btnReiniciarCamara,btnHideCamara;
    private ProgressDialog pDialog;
    private String metodo="activitys.codigoqr.infoCodigoQRSings.";
    private String url1="codigoQR/validaListaAsociada.php?";//opcionx=1&codigoQRx=653004 00002";
    private String url2="codigoQR/activacionQR.php?";
    private String url3="codigoQR/asociarComponentes.php?";
    private String url4="codigoQR/reemplazoQR.php?";
    private String url5="codigoQR/QRvirtual.php?regionidx=";


    //http://10.10.0.6:8082/Android/codigoQR/activacionQR.php?codigox=653004 00002

    private String baseUrl="",tipoConsulta="lista",QRprincipal="",opcion="",QrDanado="",salaParaRemplazo="";
    private RelativeLayout lyHideCamara;
    private boolean showcamera=true;
    private TextView tvSala,tvQr,tvSerie,tvDescripcion;


    private ListView list;//para mostrar la lista de componentes de la maquina
    private LinearLayout lyQrvirtual;
    CustomListCompoSigns adapter = null;
    private final int SELECCIONA_QR = 201;
    private Switch swQrVirtual;
    private boolean onOffFlash=false;
    private Button onOffLamparita;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_codigo_qr_sings);

        context=this; BDVarGlo.INICIALIZA(context);
        opcion=getIntent().getExtras().getString("opcion");

        lyQrvirtual=(LinearLayout) findViewById(R.id.ly_qrvir);

        if(opcion.equals("1")){
        initToolbar("Signs", true, false);
        lyQrvirtual.setVisibility(View.GONE);
        }
        else{ initToolbar("Materiales", true, false);}


        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION") + "Android/";
        }
        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRUEBAS") + "Android/";
        }
        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION-LOCAL")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION_LOCAL") + "Android/";
        }



        lyHideCamara=(RelativeLayout) findViewById(R.id.ly_hide_camara_signs);
        btnHideCamara=(Button) findViewById(R.id.btn_hide_sings);

        list=(ListView) findViewById(R.id.list_componentes_signs);
        tvSala=(TextView) findViewById(R.id.tv_sala_sign);
        tvQr=(TextView) findViewById(R.id.tv_qr_sign);

        tvSerie=(TextView) findViewById(R.id.tv_no_serie);
        tvDescripcion=(TextView) findViewById(R.id.tv_descripcion);

        swQrVirtual=(Switch) findViewById(R.id.sw_qr_virtual);
        btnReiniciarCamara=(Button) findViewById(R.id.btn_reiniciar_cam_sings);
        onOffLamparita=(Button) findViewById(R.id.btn_lamparita);



        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        } else {
            initSetUp();
        }



        swQrVirtual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    new infoCodigoQrSings.getQrPrincipalVirtual().execute();
                }
                else {  QRprincipal="";
                    tvQr.setText( "");
                    tvSala.setText("");
                    tvSerie.setText("");
                    tvDescripcion.setText("");
                    list.setAdapter(null);
                }
            }
        });



        btnReiniciarCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky is clicked
                restarCamera();
                //  iniciaScanQr();
                onOffLamparita.setEnabled(true);
            }
        });


        btnHideCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(showcamera){
                    lyHideCamara.setVisibility(View.GONE);
                    btnHideCamara.setBackgroundResource(R.drawable.icon_camara_show);
                    mScannerView.stopCamera();
                    showcamera=false;
                    onOffLamparita.setEnabled(false);
                }
                else{

                    btnHideCamara.setBackgroundResource(R.drawable.icon_camara_hide);
                    lyHideCamara.setVisibility(View.VISIBLE);
                    showcamera=true;
                }



            }
        });



    }



    void restarCamera() {

        mScannerView = new ZXingScannerView(this);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();

    }


    void initSetUp() {
        View view_toolbar_shadow_pre_lollipop = findViewById(R.id.view_toolbar_shadow_pre_lollipop);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view_toolbar_shadow_pre_lollipop.setVisibility(View.GONE);
        }
    }


    private void getQrPrincipalVirtual(String Qrvirtual,String Sala){

        String quitaSpace=Qrvirtual.replace(" ","%20");
        QRprincipal=quitaSpace;
        tvQr.setText( "VIRTUAL:"+Qrvirtual);
        tvSala.setText(Sala);
        toastAlert("El Qr Principal es Virtual");
        new infoCodigoQrSings.listaCompoSigns(quitaSpace).execute();
    }



    @Override
    public void handleResult(Result result) {
        mScannerView.stopCamera();

        String codigoqrscan = result.getText();

        String quitaSpace=codigoqrscan.replace(" ","%20");


        if(tipoConsulta.equals("lista")){

            tvQr.setText(codigoqrscan);
            new infoCodigoQrSings.listaCompoSigns(quitaSpace).execute();
        }




        if(tipoConsulta.equals("asociacion")){

            if(QRprincipal.equals("")){
                QRprincipal=quitaSpace;
                tvQr.setText( codigoqrscan);
                toastAlert("Escanea Componente para Asociar");
            }
            else{
                new infoCodigoQrSings.asociacionCodigo(QRprincipal,quitaSpace).execute();
            }

        }




        if(tipoConsulta.equals("activacion")){
            new infoCodigoQrSings.activacionCodigo(quitaSpace).execute();
        }




        if(tipoConsulta.equals("remplazo")){



              if(QRprincipal.equals("") || salaParaRemplazo.equals("") ){

                  QRprincipal=codigoqrscan;

                  if(salaParaRemplazo.equals("")){ new infoCodigoQrSings.getSala(QRprincipal).execute();}

              }
              else{
                  if(QrDanado.equals("")){
                  QrDanado=quitaSpace;
                  toastAlert("Escanea Componente Nuevo");}
                  else{

                      new infoCodigoQrSings.remplazoCodigo(QRprincipal,QrDanado,quitaSpace).execute();

                  }

              }



        }



        Log.i(metodo+"handleResult()","codigoqrscan:"+codigoqrscan+"  sin espacio  "+quitaSpace);
        //ir a consultar lista de componentes
        restarCamera();

    }




    public void llenaListaComponentes (String[] codigo,String[] serie,String[] clave,String[] refaccion ) {
    //    Log.i("info-----------------","llenaListaCompo1");
        adapter = new CustomListCompoSigns(infoCodigoQrSings.this,codigo,serie,clave,refaccion);
        list.setAdapter(adapter);
      //  Log.i("info-----------------","llenaListaCompo2");
    }




    public void btnOnOffLampara(View v){
        if(onOffFlash)
        { mScannerView.setFlash(false); onOffFlash=false;
          onOffLamparita.setBackgroundResource(R.drawable.ico_lampara1);
        }//do not forget to invalidate}}
        else { mScannerView.setFlash(true); onOffFlash=true;
            //item2.setIcon(R.drawable.ico_lampara2);
            onOffLamparita.setBackgroundResource(R.drawable.ico_lampara2);
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        QRprincipal="";
        QrDanado="";
        swQrVirtual.setChecked(false);


        switch(view.getId()) {
            case R.id.radio_sings_lista:
                if (checked)
                    tipoConsulta="lista";
                    salaParaRemplazo="";
                    break;
            case R.id.radio_sings_aso:
                if (checked)
                    tipoConsulta="asociacion";
                    salaParaRemplazo="";
                    toastAlert("Escanea QR Principal");
                    tvQr.setText("");
                    tvSala.setText("");
                    tvSerie.setText("");
                    tvDescripcion.setText("");

                    break;
            case R.id.radio_sings_act:
                if (checked)
                    tipoConsulta="activacion";
                    salaParaRemplazo="";
                    tvQr.setText("");
                    tvSala.setText("");
                    tvSerie.setText("");
                    tvDescripcion.setText("");
                    break;
            case R.id.radio_sings_remp:
                if (checked)
                    tipoConsulta="remplazo";
                    salaParaRemplazo="";
                    tvQr.setText("");
                    tvSala.setText("");
                    tvSerie.setText("");
                    tvDescripcion.setText("");
                    toastAlert("Escanea QR Principal");

                    break;

        }
    }



    // ciclo de vida de la app---------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView = new ZXingScannerView(context);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(infoCodigoQrSings.this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();


        Log.i("info-----------------","onResume");
    }
    @Override
    protected void onRestart() {
        super.onRestart();


        mScannerView = new ZXingScannerView(context);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(infoCodigoQrSings.this); // Register ourselves as a handler for scan results.
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

    private void toastAlert(String ms) {
        Toast.makeText(getApplicationContext(), ms, Toast.LENGTH_LONG).show();
    }


    String clickItemQr,clickItemSala;
    public void showDialogSelectQrVirtual(final String [] salas, final String [] qrVirtual) {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.custom_lv_select_item, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final ListView lv = (ListView) promptsView.findViewById(R.id.lv_itemlist_qrvirtual);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, salas);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Log.i("Click", "click en el elemento :" + salas[position] + " QR virtual:"+qrVirtual[position]);
                clickItemSala=salas[position];
                clickItemQr=qrVirtual[position];
            }
        });

        // customListSelectItem adapter =new customListSelectItem(auditoriaActivity.this,codes);
        lv.setAdapter(adapter2);
        // final EditText ETfx2 = (EditText) promptsView.findViewById(R.id.et_dialog_cantidad2);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getQrPrincipalVirtual(clickItemQr,clickItemSala);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }






    /////////////////////////////  SERVICIOS DE CONEXION SERVER   /////////////////////////////////////////


    class listaCompoSigns extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;
        String Exito="0",respuesta="",codigoQr="",sala="",mensaje="",seriePrin="",descripion="";


        public listaCompoSigns(String cod){
            codigoQr=cod;
        }

        protected void onPreExecute() {

            pDialog = new ProgressDialog(infoCodigoQrSings.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }
        protected Void doInBackground(Void... JSONurl) {
            PostGet servicios=new PostGet(context);

            try {

                MensajeEnConsola.logInfo(context,"listaCompoSigns<>","Peticion GET Url "+baseUrl+url1+"opcionx="+opcion+"&codigoQRx="+codigoQr);
                respuesta=servicios.ConexionGet(baseUrl+url1+"opcionx="+opcion+"&codigoQRx="+codigoQr);
                MensajeEnConsola.logInfo(context,"listaCompoSigns<>","Respuesta: "+respuesta);
                JSONObject json=new JSONObject(respuesta);

                mensaje=json.getString("Mensaje");

                JSONArray jsonArray;

                if(opcion.equals("1"))  jsonArray = json.getJSONArray("ListaSigns");
                else{jsonArray = json.getJSONArray("ListaMaterial");}

                codigo = new String[jsonArray.length()];
                serie  = new String[jsonArray.length()];
                clave  = new String[jsonArray.length()];
                refaccion  = new String[jsonArray.length()];
                sala=json.getString("Sala");
                seriePrin=json.getString("Serie");
                descripion=json.getString("Descripcion");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objetoJson = jsonArray.getJSONObject(i);
                    codigo[i] = objetoJson.getString("serie");
                    serie[i] = objetoJson.getString("codigo");
                    clave[i] = objetoJson.getString("clave");
                    refaccion[i] = objetoJson.getString("refaccion");
                   // Log.i("","-------- codigo"+codigo[i]);
                }

                Exito="1";

            } catch (Exception e) {
                Log.e(metodo+"listaCompoSigns<> ", "Error en peticion Get " + e.getMessage());
                Exito="0";
            }
            return null;
        }
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(Exito.equals("1")){
                tvSala.setText(sala);
                tvSerie.setText(seriePrin);
                tvDescripcion.setText(descripion);
                llenaListaComponentes(codigo,serie,clave,refaccion);
            }

            else{
                toastAlert(mensaje,2);
            }


            pDialog.dismiss();


        }

    }

    //----------------------------------------------------------------------------------------------

    class activacionCodigo extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;
        String Exito="0",respuesta="",codigoQr="",mensaje="";
        public activacionCodigo(String cod){
            codigoQr=cod;
        }

        protected void onPreExecute() {

            pDialog = new ProgressDialog(infoCodigoQrSings.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        protected Void doInBackground(Void... JSONurl) {
            PostGet servicios=new PostGet(context);
            try {

                MensajeEnConsola.logInfo(context,"activacionCodigo<>","Peticion GET Url "+baseUrl+url2+"codigox="+codigoQr);
                respuesta=servicios.ConexionGet(baseUrl+url2+"codigox="+codigoQr);
                MensajeEnConsola.logInfo(context,"activacionCodigo<>","Respuesta: "+respuesta);
                JSONObject json=new JSONObject(respuesta);

                Exito=json.getString("Valor");
                mensaje=json.getString("Mensaje");




            } catch (Exception e) {
                Log.e(metodo+"activacionCodigo<> ", "Error en peticion Get " + e.getMessage());
                Exito="0";

            }
            return null;
        }
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(Exito.equals("1")){

                toastAlert(mensaje,3);
            }
            else{

                toastAlert(mensaje,2);
            }

            pDialog.dismiss();


        }

    }

    //----------------------------------------------------------------------------------------------

    class asociacionCodigo extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;
        String Exito="0",respuesta="",codigoPri="",codigoAso="",mensaje="",Sala="";

        public asociacionCodigo(String codP,String codAso){
            codigoPri=codP;codigoAso=codAso;
        }

        protected void onPreExecute() {

            pDialog = new ProgressDialog(infoCodigoQrSings.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }
        protected Void doInBackground(Void... JSONurl) {
            PostGet servicios=new PostGet(context);

            try {

                MensajeEnConsola.logInfo(context,"asociacionCodigo<>","Peticion GET Url "+baseUrl+url3+"opcionx="+opcion+"&codigoPrinx="+codigoPri+"&codigoDescx="+codigoAso);
                respuesta=servicios.ConexionGet(baseUrl+url3+"opcionx="+opcion+"&codigoPrinx="+codigoPri+"&codigoDescx="+codigoAso);
                MensajeEnConsola.logInfo(context,"asociacionCodigo<>","Respuesta: "+respuesta);
                JSONObject json=new JSONObject(respuesta);
                Exito=json.getString("Valor");
                mensaje=json.getString("Mensaje");
                Sala=json.getString("Sala");

            } catch (Exception e) {
                Log.e(metodo+"asociacionCodigo<> ", "Error en peticion Get " + e.getMessage());
                Exito="0";
            }

            return null;
        }
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(Exito.equals("1")){
                toastAlert(mensaje,3);
                tvSala.setText(Sala);

            }
            else{
                toastAlert(mensaje,2);
            }

            pDialog.dismiss();



        }

    }

    //----------------------------------------------------------------------------------------------

    class remplazoCodigo extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;
        String Exito="0",respuesta="",qrP="",qrD="",qrN="",mensaje="";
        public remplazoCodigo(String QRprinc,String QRanterior, String QRnuevo){
            qrP=QRprinc;
            qrD=QRanterior;
            qrN=QRnuevo;
        }

        protected void onPreExecute() {

            pDialog = new ProgressDialog(infoCodigoQrSings.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        protected Void doInBackground(Void... JSONurl) {
            PostGet servicios=new PostGet(context);
            try {

                MensajeEnConsola.logInfo(context,"remplazoCodigo<>","Peticion GET Url "+baseUrl+url4+"salax=0&"+"opcionx="+opcion+"&QRprincipalx="+qrP+"&QRdanadox="+qrD+"&QRnuevox="+qrN);
                respuesta=servicios.ConexionGet(baseUrl+url4+"salax=0&"+"opcionx="+opcion+"&QRprincipalx="+qrP+"&QRdanadox="+qrD+"&QRnuevox="+qrN);
                MensajeEnConsola.logInfo(context,"remplazoCodigo<>","Respuesta: "+respuesta);
                JSONObject json=new JSONObject(respuesta);

                Exito=json.getString("Valor");
                mensaje=json.getString("Mensaje");

            } catch (Exception e) {
                Log.e(metodo+"remplazoCodigo<> ", "Error en peticion Get " + e.getMessage());
                Exito="0";

            }
            return null;
        }
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(Exito.equals("1")){

                toastAlert(mensaje,3);
            }
            else{
                toastAlert(mensaje,2);
            }

            QrDanado="";
            pDialog.dismiss();


        }

    }

    //----------------------------------------------------------------------------------------------

    class getSala extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;
        String Exito="0",respuesta="",qrP="",mensaje="";

        public getSala(String QRprinc){
            qrP=QRprinc;

        }

        protected void onPreExecute() {

            pDialog = new ProgressDialog(infoCodigoQrSings.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        protected Void doInBackground(Void... JSONurl) {
            PostGet servicios=new PostGet(context);
            try {

                MensajeEnConsola.logInfo(context,"remplazoCodigo<>","Peticion GET Url "+baseUrl+url4+"salax=1&QRprincipalx="+qrP);
                respuesta=servicios.ConexionGet(baseUrl+url4+"salax=1&QRprincipalx="+qrP);
                MensajeEnConsola.logInfo(context,"remplazoCodigo<>","Respuesta: "+respuesta);
                JSONObject json=new JSONObject(respuesta);

                Exito=json.getString("Valor");
                mensaje=json.getString("Mensaje");
                salaParaRemplazo=json.getString("Sala");


            } catch (Exception e) {
                Log.e(metodo+"getSala<> ", "Error en peticion Get " + e.getMessage());
                salaParaRemplazo="";
                Exito="0";

            }
            return null;
        }
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(Exito.equals("1")){

             tvSala.setText(salaParaRemplazo);
             tvQr.setText(qrP);
                toastAlert("Escanea Componente Dañado");

            }
            else{

                toastAlert(mensaje,2);
            }


            pDialog.dismiss();


        }

    }

    //----------------------------------------------------------------------------------------------

    class getQrPrincipalVirtual extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;
        String Exito="0";
        //String respuesta="{\"listaDatos\":[{\"codigo\":\"100173 00002\",\"sala\":\"JACKPOT CHOLULA\"},{\"codigo\":\"100173 00004\",\"sala\":\"CODERE ORIZABA\"},{\"codigo\":\"100173 00003\",\"sala\":\"GOLDEN LIONS XALAPA\"},{\"codigo\":\"100173 00001\",\"sala\":\"CODERE PLAZA DORADA\"}]}",mensaje=""
        String respuesta="", regionid="";
        String salas [];
        String qrVirtual[];


        public getQrPrincipalVirtual(){
            regionid= BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION");
        }

        protected void onPreExecute() {
            pDialog = new ProgressDialog(infoCodigoQrSings.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        protected Void doInBackground(Void... JSONurl) {
            PostGet servicios=new PostGet(context);
            try {
                MensajeEnConsola.logInfo(context,"getQrPrincipalVirtual<>","Peticion GET Url "+baseUrl+url5+regionid);
                respuesta=servicios.ConexionGet(baseUrl+url5+regionid);
                MensajeEnConsola.logInfo(context,"getQrPrincipalVirtual<>","Respuesta:"+respuesta);
                JSONObject json=new JSONObject(respuesta);

                JSONArray jsonArray;
                jsonArray = json.getJSONArray("listaDatos");

                salas = new String[jsonArray.length()];
                qrVirtual  = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objetoJson = jsonArray.getJSONObject(i);
                    salas[i] = objetoJson.getString("sala");
                    qrVirtual[i] = objetoJson.getString("codigo");
                }


            } catch (Exception e) {
                Log.e(metodo+"getQrPrincipalVirtual<>", "Error en peticion Get " + e.getMessage());
            }
            return null;
        }
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);


            pDialog.dismiss();

            showDialogSelectQrVirtual(salas, qrVirtual);

        }

    }





}
