package com.example.devolucionmaterial.activitys.codigoqr;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.codigoqr.adapter.CustomListCompoSigns;
import com.example.devolucionmaterial.activitys.viaticos.Activity_Gastos;
import com.example.devolucionmaterial.api.PostGet;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class materialStockActivity extends AppCompatActivity {

    private BDmanager manager;
    Context context;
    String metodo;
    private ArrayList<String> arrLisSalas = new ArrayList<String>();
    private ArrayList<String> arrLisSalasId = new ArrayList<String>();
    private Spinner spSala;
    private Button btnConsultar;
    private ListView lvMateriales;
    private String baseUrl="";
    private String url1="codigoQR/listaStocks.php?salaidx=170";

    private String serie[],codigo[],clave[],refaccion[];
    CustomListCompoSigns adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_stock);
        context= this;
        manager = new BDmanager(context);

        spSala=(Spinner) findViewById(R.id.mat_stock_sp_sala);
        btnConsultar=(Button) findViewById(R.id.mat_stock_btn_consultar);
        lvMateriales=(ListView) findViewById(R.id.mat_stock_lv_materiales);



        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION") + "Android/";
        }
        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRUEBAS") + "Android/";
        }
        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION-LOCAL")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION_LOCAL") + "Android/";
        }



       btnConsultar.setOnClickListener( new View.OnClickListener() {

           @Override
           public void onClick(View v) {               // TODO Auto-generated method stub
               new materialStockActivity.stockSala().execute();
           }
       });




        spSala.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        llenaSpinnerSala(Integer.parseInt(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION")));

    }


    public void llenaSpinnerSala(int regionID) {
        metodo = "llenaComboSala()";
  //    arrSalas.clear();
        arrLisSalas.clear();
        arrLisSalasId.clear();


        Cursor fila = manager.consulta("SELECT salaid,nombre FROM csala WHERE regionidfk= '"+regionID+"' ORDER BY nombre");
        for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
        //    arrSalas.add(fila.getString(0));
            Log.i("-----","id:"+fila.getString(0)+"  nom:"+fila.getString(1));

            arrLisSalasId.add(fila.getString(0));
            arrLisSalas.add(fila.getString(1));
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,  arrLisSalas);
        spSala.setAdapter(adapter2);


    }





    class stockSala extends AsyncTask<Void, Void, Void> {
        //ProgressDialog loading;

        String Exito="",respuesta="",stock="";

        private ProgressDialog pDialog;

        protected void onPreExecute() {
            // loading = ProgressDialog.show(Activity_Gastos.this, "Conectando...", null,true,false);
            pDialog = new ProgressDialog(materialStockActivity.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }
        protected Void doInBackground(Void... JSONurl) {

            PostGet servicios=new PostGet(context);
            try {
                metodo="stockSala<>";
                MensajeEnConsola.logInfo(context,metodo,baseUrl+url1);
                respuesta=servicios.ConexionGet(baseUrl+url1);
                // Log.i(metodo+"TerminaViaje Respuesta",respuesta );
                MensajeEnConsola.logInfo(context,metodo,"Respuesta ="+respuesta);

                JSONObject json= new JSONObject(respuesta);
                Exito=json.getString("Mensaje");

                JSONArray jsonArray;
                jsonArray = json.getJSONArray("ListaStock");
                stock=jsonArray.toString();
                Log.i("++++++","stock:"+stock);


                codigo = new String[jsonArray.length()];
                serie  = new String[jsonArray.length()];
                clave  = new String[jsonArray.length()];
                refaccion  = new String[jsonArray.length()];


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objetoJson = jsonArray.getJSONObject(i);
                    codigo[i] = objetoJson.getString("serie");
                    serie[i] = objetoJson.getString("codigo");
                    clave[i] = objetoJson.getString("clave");
                    refaccion[i] = objetoJson.getString("refaccion");
                    // Log.i("","-------- codigo"+codigo[i]);
                }


            } catch (Exception e) {
                // Log.e(metodo+"TerminaViaje ", "Error en peticion Get " + e.getMessage());
                MensajeEnConsola.logError(context,metodo,"Error en Peticion GET " + e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            pDialog.dismiss();// loading.dismiss();

            if(Exito.equals("Sin Stock Disponible")){
              toastAlert("Sin Stock Disponible");
            }

            else{
                llenaListaComponentes(codigo,serie,clave,refaccion);
            }



        }

    }






    public void llenaListaComponentes (String[] codigo,String[] serie,String[] clave,String[] refaccion ) {

        adapter = new CustomListCompoSigns(materialStockActivity.this,codigo,serie,clave,refaccion);
        lvMateriales.setAdapter(adapter);

    }






    private void toastAlert(String ms){
        Toast.makeText(getApplicationContext(),ms, Toast.LENGTH_LONG).show();
    }


}
