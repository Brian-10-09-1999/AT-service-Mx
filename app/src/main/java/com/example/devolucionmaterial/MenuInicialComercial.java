package com.example.devolucionmaterial;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.dialogs.Estatus;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.MenuOpciones;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuInicialComercial extends Activity {
    private static String metodo;
    private static BDmanager manager;
    static Context context;
    private MenuOpciones mo;
    private ProgressDialog pDialog;
    private static Menu mMenu;
    private static String docPHPFirmaCom="MenuProFirma.php?";
    private TextView tvNombreCom;
    String url;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenu = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        metodo ="onOptionsItemSelected()";
        int id = item.getItemId();
        if (id == R.id.action_est_tec) {
            iniciaMenuEstatusTec(context);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public static void iniciaMenuEstatusTec(Context context){
        BDVarGlo.setVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO", "comercial");
        if(CheckService.internet(context)) {
            //context.startActivity(new Intent(context, EstatusPromotora.class));
            context.startActivity(new Intent(context, Estatus.class));
        }else{
            Alert.ActivaInternet(context);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial_comercial);
        context = this;
        manager = new BDmanager(context);
        setupMenuInicialComercial();
        AnunciosPrimeroLogin();
    }
    void AnunciosPrimeroLogin(){
        //muestrta un anuncio cada dia al usuario
        Alert.Anuncio(context);
    }
    private void setupMenuInicialComercial(){
        metodo = "setupMenuInicialComercial()";
        ImageView imgCall = (ImageView) findViewById(R.id.imgCallCom);
        ImageView imgCorreo = (ImageView) findViewById(R.id.imgCorreoCom);
        ImageView imgInfo = (ImageView) findViewById(R.id.imgInfoCom);
        tvNombreCom = (TextView)findViewById(R.id.tvUsuCom);
        mo = new MenuOpciones();
        tvNombreCom.setText(getString(R.string.bienvenido) + BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE") + "!");
        if(CheckService.internet(context)) {
            new Progreso().execute(MainActivity.url + docPHPFirmaCom + "promotoraidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
        }else{
            Alert.ActivaInternet(context);
        }
        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {mo.llamarContactCenter(context);}
        });

        imgCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {mo.enviarCorreo(context);
            }
        });

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {mo.mostrarInfoApp(context);
            }
        });
    }
    public void onResume(){
        metodo="onResume()";
        super.onResume();
        actualizaEstatusComercial();
    }
    private class Progreso extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            metodo ="Progreso<AsyncTask>.onPreExecute()";
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }
        @Override
        protected Void doInBackground(String... params) {
            metodo ="Progreso<AsyncTask>.doInBackground()";
            try{
                JSONObject object = JSONparse.consultaURL(context, metodo, params[0]);
                assert object != null;
                JSONArray jsonArray = object.optJSONArray("tipoMenu");
                JSONObject jsonArrayChild1 = jsonArray.getJSONObject(0);
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", jsonArrayChild1.optString("estatusidx"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", jsonArrayChild1.optString("estEnServx"));
                BDVarGlo.setDatosUsuario(context);
            }catch(JSONException e) {
                MensajeEnConsola.log(context, metodo, "JSONException e = "+e.getMessage());
            }catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Error MA001"+"\n"+e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            metodo ="Progreso<AsyncTask>.onPostExecute()";
            actualizaEstatusComercial();
        }
    }
    public static void actualizaEstatusComercial(){
        metodo = "actualizaEstatusComercial()";
        if(mMenu!=null){
            MenuItem item = mMenu.findItem(R.id.action_est_tec);
            Cursor cursorEstatusCom = manager.cargarCursorEstatusTec();
            cursorEstatusCom.moveToFirst();

            if(cursorEstatusCom.getString(1).equals("39")){
                if(cursorEstatusCom.getString(4).equals("1")){
                    item.setIcon(R.drawable.est_activo);
                }else if(cursorEstatusCom.getString(4).equals("2")){
                    item.setIcon(R.drawable.est_traslado_sala);
                }else if(cursorEstatusCom.getString(4).equals("3")){
                    item.setIcon(R.drawable.est_asignado);
                }

                if(cursorEstatusCom.getString(4).equals("4")){
                    item.setIcon(R.drawable.est_en_comida);
                }
            }

            if(cursorEstatusCom.getString(1).equals("40")){
                item.setIcon(R.drawable.est_inactivo);
            }
            if(cursorEstatusCom.getString(1).equals("109")){
                item.setIcon(R.drawable.est_incidencia);
            }
            if(cursorEstatusCom.getString(1).equals("79")){
                item.setIcon(R.drawable.est_dia_descanso);
            }
            if(cursorEstatusCom.getString(1).equals("80")){
                item.setIcon(R.drawable.mix_peq);
            }
            if(cursorEstatusCom.getString(1).equals("81")){
                item.setIcon(R.drawable.est_vacaciones);
            }
            if(cursorEstatusCom.getString(1).equals("89")){
                item.setIcon(R.drawable.est_incapacidad);
            }
        }
    }
}
//204