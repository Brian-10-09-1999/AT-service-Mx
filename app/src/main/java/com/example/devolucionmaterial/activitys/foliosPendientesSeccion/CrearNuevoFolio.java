package com.example.devolucionmaterial.activitys.foliosPendientesSeccion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.viaticos.Activity_Gastos;
import com.example.devolucionmaterial.api.PostGet;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class CrearNuevoFolio extends AppCompatActivity {
    private static final String TAG="activitys.foliosPendietesSeccion.CreaNuevoFolio ---------- ";
    private Button btn_CreaFolio,btn_irFolio;
    private TextView tvNombreTecnico,tvNameGame,tvFolio;
    private EditText etComentario;


    private Spinner spSala,spFalla,spSubFalla,spLicencia;
    //se recaban de lña base de datos del telefono
    private ArrayList<String> listSala = new ArrayList<String>();
    private ArrayList<String> idSala = new ArrayList<String>();

    private ArrayList<String> listFallas = new ArrayList<String>();
    private ArrayList<String> idFalla= new ArrayList<String>();

    private ArrayList<String> listSubFallas = new ArrayList<String>();
    private ArrayList<String> idSubFalla= new ArrayList<String>();
    private ArrayList<String> idSubFallaFalla= new ArrayList<String>();


    private ArrayList<String> listIdSalaJuegoLic = new ArrayList<String>();
    private ArrayList<String> listMaquinaid = new ArrayList<String>();
    private ArrayList<String> listSalaid = new ArrayList<String>();
    private ArrayList<String> listLicencias = new ArrayList<String>();
    private ArrayList<String> listJuegoId = new ArrayList<String>();
    private ArrayList<String> listNombreJuedo = new ArrayList<String>();
    protected Toolbar toolbar;

    private int regionidx;
    private Context context;
    private BDmanager manager;
    private String url1="creaFolio.php";//servicio post
    private String baseUrl="";

    private String metodo="(foliosPendientesSeccion) ---------- CrearNuevoFolio.";
    private int posSala,posFalla,posSubFalla,poslicencia,posJuego;
    private String licenciaSelection="",idSubfalla="",comentario="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_nuevo_folio);

        context=this;
        manager = new BDmanager(context);
        regionidx= Integer.parseInt((BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION")));
        initToolbar(" Crear Folio", true, true);

        spSala=(Spinner)findViewById(R.id.sp_creafolio_sala);
        spFalla=(Spinner) findViewById(R.id.sp_creafolio_falla);
        spSubFalla=(Spinner) findViewById(R.id.sp_creafolio_subfalla);
       // spJuego=(Spinner) findViewById(R.id.sp_creafolio_juego);
        spLicencia=(Spinner) findViewById(R.id.sp_creafolio_licencia);

        tvNombreTecnico=(TextView) findViewById(R.id.tv_name_tecnico);
        tvNombreTecnico.setText(BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
        tvFolio=(TextView) findViewById(R.id.tv_folio_creado);

        tvNameGame=(TextView) findViewById(R.id.tv_name_game);tvNameGame.setText("");
        etComentario=(EditText) findViewById(R.id.et_comentariosx);

        btn_CreaFolio=(Button) findViewById(R.id.but_crea_folio);
        btn_irFolio=(Button) findViewById(R.id.but_ira_folio); btn_irFolio.setEnabled(false);btn_irFolio.setClickable(false);btn_irFolio.setTextColor(Color.LTGRAY);

       setSpSala(regionidx);
       setSpFalla();



       spFalla.setOnItemSelectedListener(
               new AdapterView.OnItemSelectedListener() {
                   public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                       posFalla=posicion;
                       Log.i("-- juan ---","id falla="+idFalla.get(posicion));
                       idSubfalla="";
                    setSpSubFalla(idFalla.get(posicion));

                   }
                   public void onNothingSelected(AdapterView<?> spn) {}
               });

       spSubFalla.setOnItemSelectedListener(
               new AdapterView.OnItemSelectedListener() {
                   public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                       posSubFalla=posicion;
                       idSubfalla=idSubFalla.get(posicion);
                       Log.i("-- juan ---","id Subfalla="+idSubFalla.get(posicion));
                   }
                   public void onNothingSelected(AdapterView<?> spn) {}
               });

       spSala.setOnItemSelectedListener(
               new AdapterView.OnItemSelectedListener() {
                   public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                       posSala=posicion;
                       Log.i("--- juan ---"," id sala ="+idSala.get(posicion));

                       tvNameGame.setText("");
                       licenciaSelection="";

                       setSpLicenciaJuego(idSala.get(posicion));


                   }
                   public void onNothingSelected(AdapterView<?> spn) {}
               });

        spLicencia.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        Log.i("--- juan ---"," nombre licencia ="+listLicencias.get(posicion));
                        licenciaSelection=listLicencias.get(posicion);

                       if(licenciaSelection.equals("Vacio")){ licenciaSelection="";}
                       else{ setJuego(spLicencia.getSelectedItem().toString());}

                    }
                    public void onNothingSelected(AdapterView<?> spn) {}
                });


        btn_CreaFolio.setOnClickListener( new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(etComentario.getText().toString().equals("") ){  toastAlert("El campo comentarios no puede estar vacio"); }

            else {

                if (isOnline()) {
                    new CrearNuevoFolio.EnviaFolio().execute();
                } else {
                    toastAlert("No tienes conexion a Internet");
                }
            }

        }
       });

        btn_irFolio.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context, FoliosPendientes.class));

            }
        });




        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION")) {
            baseUrl = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRODUCCION") + "Android/";
        }
        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")) {
            baseUrl = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRUEBAS") + "Android/";
        }
        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION-LOCAL")) {
            baseUrl = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRODUCCION_LOCAL") + "Android/";
        }


    }


    private void setSpSala(int regionID){

        listSala.clear();idSala.clear();

        Cursor fila=manager.consulta("SELECT nombre,salaID FROM csala WHERE regionidfk= '" + regionID + "' ORDER BY nombre");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {

            listSala.add(fila.getString(0));
            idSala.add(fila.getString(1));
            Log.i(TAG+".setSpSala",fila.getString(0)+ "  "+fila.getString(1));
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listSala);
        spSala.setAdapter(adapter2);
    }

    private void setSpFalla(){

        listFallas.clear();idFalla.clear();


        Cursor fila=manager.consulta("SELECT idFalla,nombreFalla FROM cfallas ORDER BY nombreFalla ");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {

            listFallas.add(fila.getString(1));

            idFalla.add(fila.getString(0));

            Log.i(TAG+".setSpSala",fila.getString(0)+ "  "+fila.getString(1));
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listFallas);
        spFalla.setAdapter(adapter2);

    }
    private void setSpSubFalla(String falla){

        listSubFallas.clear();idSubFalla.clear();idSubFallaFalla.clear();
        Cursor fila=manager.consulta("SELECT idSubFalla,nombreSubFalla,idSubFallaFalla FROM csubfallas WHERE idSubFallaFalla= '"+ falla + "' ");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
            listSubFallas.add(fila.getString(1));
            idSubFalla.add(fila.getString(0));
            idSubFallaFalla.add(fila.getString(2));
            Log.i(TAG+".setSpSala",fila.getString(0)+ "  "+fila.getString(1));
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listSubFallas);
        spSubFalla.setAdapter(adapter2);
    }
    private void setSpLicenciaJuego(String salaid){

        listLicencias.clear();


        listLicencias.add("Vacio");

        Cursor fila=manager.consulta("SELECT licencia FROM csalajuegolic WHERE salaid= '"+ salaid + "' ");

        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {

            listLicencias.add(fila.getString(0));
          //  listNombreJuedo.add(fila.getString(1));
         //   Log.i(TAG+"  sala id= ",fila.getString(0));
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listLicencias);
        spLicencia.setAdapter(adapter2);





    }


    private void setJuego(String licencia){

        Cursor fila=manager.consulta("SELECT nombreJuego FROM csalajuegolic WHERE licencia= '"+ licencia + "' ");
        fila.moveToFirst();
        tvNameGame.setText( fila.getString(0));
        Log.i(TAG+"  nombre juego= ",fila.getString(0));

    }



    class EnviaFolio extends AsyncTask<Void, Void, Void> {
        // ProgressDialog loading;
        private ProgressDialog pDialog;
        private String respuesta="";
        private String folionum="",exito="0";

        protected void onPreExecute() {
            //   loading = ProgressDialog.show(Activity_Gastos.this, "Conectando...", null,true,false);
            pDialog = new ProgressDialog(CrearNuevoFolio.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);

        }
        protected Void doInBackground(Void... JSONurl) {
            PostGet servicios=new PostGet(context);
            try {

                final JSONObject jsonParam = new JSONObject();

                jsonParam.put("salaidx",idSala.get(posSala));
                jsonParam.put("fallaidx",idFalla.get(posFalla));
                jsonParam.put("subFallaidx",idSubfalla);
                jsonParam.put("licencia",licenciaSelection);
                jsonParam.put("juegoidx","");
                jsonParam.put("comentariosx",etComentario.getText().toString());
                jsonParam.put("tecnicoidx", BDVarGlo.getVarGlo(context,"INFO_USUARIO_ID"));

                Log.i(metodo+"EditarGasto","peticion Post Url "+ baseUrl+url1);
                respuesta=servicios.ConexionPost(baseUrl+url1,jsonParam);
                Log.i(metodo+"json","json param"+jsonParam.toString());
                Log.i(metodo+"json","respuesta"+respuesta);
                JSONObject jsonresponse=new JSONObject(respuesta);
                folionum=jsonresponse.getString("folio");
                exito=jsonresponse.getString("respuesta");



            } catch (Exception e) {
                Log.e(metodo+"EnviaFolio ", "Error en peticion Get " + e.getMessage());
            }
            return null;
        }
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(exito.equals("1"))
            {  btn_irFolio.setEnabled(true);btn_irFolio.setTextColor(Color.BLACK);
               tvFolio.setText("Folio creado con Exito: "+folionum);
               btn_CreaFolio.setEnabled(false);btn_CreaFolio.setClickable(false);btn_CreaFolio.setTextColor(Color.LTGRAY);

            }


            pDialog.dismiss();
        }

    }






    protected void initToolbar(String title, Boolean homeAsUpEnabled, Boolean enabledMenu) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  toolbar.setLogo(R.drawable.icon_mis_viajes);
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


    private void toastAlert(String ms) {
        Toast.makeText(getApplicationContext(), ms, Toast.LENGTH_LONG).show();

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
