package com.example.devolucionmaterial.activitys.viaticos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.api.PostGet;
import com.example.devolucionmaterial.beans.BeansGlobales;
import com.example.devolucionmaterial.checks.Device;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSON;
import com.example.devolucionmaterial.sharedpreferences.PreferencesVar;
import com.example.devolucionmaterial.sharedpreferences.PreferencesViaticos;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.odn.qr_manager.activities.QReaderActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Activity_Lista extends AppCompatActivity {

    private int[] idViaje;
    private String[] actividad;
    private String[] motivo;
    private String[] origen;
    private String[] destino;
    private String[] inicio;
    private String[] fechaInicio;
    private String[] LimiteDiario;
    private String[] termina;
    private String[] fechaTermino;
    private int[] status;
    private double[] saldo, anticipo;


    private String urlListaViajes = "Viaticos/busquedaViaticos.php?tecnicoidx=";
    //private String urlListaViajes = "http://189.254.111.195:8082/Android/Viaticos/busquedaViaticos.php?tecnicoidx=";


    private String baseUrl="";


    private ListView list;//para mostrar la lista de vuajes
    private Context context;
    private String metodo = "(Viaticos) ---------- Activity_Lista.";
    private PreferencesViaticos preferencesViaticos;
    CustomListAdapter adapter = null;

    protected Toolbar toolbar;
    private boolean enabledMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaticos_lista);

        initToolbar(" Mis Viajes", true, true);

        list = (ListView) findViewById(R.id.list);
        context = Activity_Lista.this;
        preferencesViaticos = new PreferencesViaticos(Activity_Lista.this);


        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION") + "Android/";
        }
        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRUEBAS") + "Android/";
        }
        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION-LOCAL")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION_LOCAL") + "Android/";
        }



     //   Log.i(metodo + "onCreated  ", "usuario id=" + BDVarGlo.getVarGlo(this, "INFO_USUARIO_ID"));
        MensajeEnConsola.logInfo(context,"onCreated","usuario id=" + BDVarGlo.getVarGlo(this, "INFO_USUARIO_ID"));



        if (isOnline()) {
            //Servicios servicios = new Servicios(this);
            new Activity_Lista.ConectaConServer().execute();
        } else {
            alertas("No Estas Conectado a Internet La\nLista de Viajes Podria no Estar Actualizada.", 2);
            String jsonSaved = preferencesViaticos.getJson("lista_viajes");
           // Log.i(metodo + "onCreated: sin Conexion a Internet ", "jsonSaved=" + jsonSaved);
            MensajeEnConsola.logInfo(context,"onCreated","sin Conexion a Internet"+"jsonSaved=" + jsonSaved);

            boolean parseoExitoso = parseoRespuesta(jsonSaved);

            if (parseoExitoso) setListaViajes();
            if (jsonSaved.equals("{\"viaticos\":[]}")) {
                toastAlert("No tienes Ningun Viaje");
            }
            if (jsonSaved.equals("")) {
                toastAlert("Ningun Viaje Guardado");
            }

        }

    }


    protected void initToolbar(String title, Boolean homeAsUpEnabled, Boolean enabledMenu) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.icon_mis_viajes);
        this.enabledMenu = enabledMenu;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            if (homeAsUpEnabled)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setTitle(title);
        }
    }


    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void alertas(String mensaje, int caso) {
        final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(this);
        alertaSimple.setTitle("Información");
        alertaSimple.setMessage(mensaje);
        alertaSimple.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        if (caso == 1) alertaSimple.setIcon(R.drawable.icon_timeout);
        if (caso == 2) alertaSimple.setIcon(R.drawable.ico_nowifi);
        if (caso == 3) alertaSimple.setIcon(R.drawable.icon_error_sever);


        alertaSimple.create();
        alertaSimple.show();
    }




    private void toastAlert(String ms) {
        Toast.makeText(getApplicationContext(), ms, Toast.LENGTH_LONG).show();
    }


    class ConectaConServer extends AsyncTask<Void, Void, Void> {
        //ProgressDialog loading;
        boolean parseoExitoso = false;
        private String respuesta = "";
        private ProgressDialog pDialog;


        protected void onPreExecute() {
            //  loading = ProgressDialog.show(Activity_Lista.this, "Conectando...", null,true,true);
            pDialog = new ProgressDialog(Activity_Lista.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);

            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);

        }

        protected Void doInBackground(Void... JSONurl) {

            PostGet servicios = new PostGet(context);

            try {
              // Log.i(metodo + "ConectaServer GET url", baseUrl+urlListaViajes + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID") + "&tipoUsuariox=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"));

                MensajeEnConsola.logInfo(context,"ConectaConServer","GET url="+baseUrl+urlListaViajes + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID") + "&tipoUsuariox=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"));

                respuesta = servicios.ConexionGet(baseUrl+urlListaViajes + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID") + "&tipoUsuariox=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"));

                //Log.i(metodo + "ConectaServer RESPUESTA", respuesta);
                MensajeEnConsola.logInfo(context,"ConectaConServer","RESPUESTA:"+respuesta);

                parseoExitoso = parseoRespuesta(respuesta);

            } catch (Exception e) {
                //Log.e(metodo + "ConectaConServer", "ERROR En doInBackground: Exception :" + e.getMessage());
                MensajeEnConsola.logError(context,"ConectaConServer", "ERROR En doInBackground: Exception:"+e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if (parseoExitoso) {
                setListaViajes();
                preferencesViaticos.setJson("lista_viajes", respuesta);//se guarda el json para cuando no exista conexion
            }
            if (respuesta.equals("{\"viaticos\":[]}")) {
                toastAlert("No tienes Ningun Viaje");
            }
            // loading.dismiss();//termina todoo el proceso

            pDialog.dismiss();
        }

    }


    private void setListaViajes() {

        adapter = new CustomListAdapter(Activity_Lista.this,idViaje, actividad, motivo, origen, destino, inicio, termina, status);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent vi = new Intent(getApplicationContext(), Activity_Gastos.class);
                vi.putExtra("idviaje", idViaje[+position]);
                vi.putExtra("status", status[+position]);
                vi.putExtra("saldo", saldo[+position]);
                vi.putExtra("fechaInicial", inicio[+position]);
                vi.putExtra("fechaFinal", termina[+position]);
                vi.putExtra("inicio", fechaInicio[+position]);
                vi.putExtra("termino", fechaTermino[+position]);
                vi.putExtra("limite", LimiteDiario[+position]);//limite de gasto por comida
                vi.putExtra("anticipo", anticipo[+position]);
                startActivity(vi);
            }
        });

    }

    private boolean parseoRespuesta(String stringJson) {

        try {
            JSONObject json = new JSONObject(stringJson);
            JSONArray jsonArray = json.getJSONArray("viaticos");
            idViaje = new int[jsonArray.length()];
            actividad = new String[jsonArray.length()];
            motivo = new String[jsonArray.length()];
            origen = new String[jsonArray.length()];
            destino = new String[jsonArray.length()];
            inicio = new String[jsonArray.length()];
            termina = new String[jsonArray.length()];
            status = new int[jsonArray.length()];
            saldo = new double[jsonArray.length()];
            anticipo = new double[jsonArray.length()];
            fechaInicio = new String[jsonArray.length()];
            fechaTermino = new String[jsonArray.length()];
            LimiteDiario = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objetoJson = jsonArray.getJSONObject(i);
                idViaje[i] = objetoJson.getInt("id");
                actividad[i] = objetoJson.getString("actividad");
                motivo[i] = objetoJson.getString("Motivo");
                origen[i] = objetoJson.getString("Origen");
                destino[i] = objetoJson.getString("Destino");
                inicio[i] = objetoJson.getString("FechaInicial");
                termina[i] = objetoJson.getString("FechaFinal");
                status[i] = objetoJson.getInt("Estatus");
                saldo[i] = objetoJson.getDouble("Saldo");
                anticipo[i] = objetoJson.getDouble("Anticipo");
                fechaInicio[i] = objetoJson.getString("fechaIniFor");
                fechaTermino[i] = objetoJson.getString("fechaFinFor");
                LimiteDiario[i] = objetoJson.getString("limiteDiario");
            }
            return true;
        } catch (Exception e) {
          //  Log.e(metodo + "parseoRespuesta: ", "ERROR al Parsear: Exception :" + e.getMessage());
            MensajeEnConsola.logError(context,"parseoRespuesta","ERROR al Parsear: Exception :" + e.getMessage());
            return false;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();


        if (isOnline()) {
            //Servicios servicios = new Servicios(this);
            new Activity_Lista.ConectaConServer().execute();
        } else {
            alertas("No Estas Conectado a Internet La\nLista de Viajes Podria no Estar Actualizada.", 2);
            String jsonSaved = preferencesViaticos.getJson("lista_viajes");
           // Log.i(metodo + "onCreated: sin Conexion a Internet ", "jsonSaved=" + jsonSaved);
            MensajeEnConsola.logError(context,"onResume","sin Conexion a Internet "+ " jsonSaved=" + jsonSaved);

            boolean parseoExitoso = parseoRespuesta(jsonSaved);

            if (parseoExitoso) setListaViajes();
            if (jsonSaved.equals("{\"viaticos\":[]}")) {
                toastAlert("No tienes Ningun Viaje");
            }
            if (jsonSaved.equals("")) {
                toastAlert("Ningun Viaje Guardado");
            }

        }


    }







    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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
        }


        return super.onOptionsItemSelected(item);
    }

}
