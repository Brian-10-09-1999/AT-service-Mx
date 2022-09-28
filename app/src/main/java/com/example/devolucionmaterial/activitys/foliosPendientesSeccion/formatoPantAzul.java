package com.example.devolucionmaterial.activitys.foliosPendientesSeccion;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.api.PostGet;
import com.example.devolucionmaterial.data_base.BDVarGlo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;

public class formatoPantAzul extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private LinearLayout  lyJuegos;

    private String metodo = "(foliosPendientesSeccion) ---------- RevisionJugada.",baseUrl="",getFecha="",getHora="";
    private Button butEnviar;
    protected Toolbar toolbar;
    private DateFormat dateFormat;
    private Context context;
    private boolean selectDate=false,selectTime=false;

    private EditText etQuienReporta,etSistemaCaja,etCreditosRec,etProblReportada;
    private TextView etHoraFecha,etJuego;

    private CheckBox cbPantallaAzul,cbPantallaCont;
    private String folio="",juego="",nombretecnico,quienReporta, sistemaDeCaja,creditosReclamados,checkPantallaazul,checkPantallaConta,
            problematicaRepo,fechaHora,sendJuego;


    private String url="formatoPantallaAzul.php ";
    private String url2="ListaJuegos.php";

    private Spinner spListaJuegos;
    private String listaJuegos[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_formato_pant_azul);

        context=this;
        initToolbar("Formato Pantalla Azul", true, true);

        if (getIntent().getExtras() != null) {
            folio = getIntent().getExtras().getString("folio");
            juego=getIntent().getExtras().getString("juego");
        }

        nombretecnico= BDVarGlo.getVarGlo(this, "INFO_USUARIO_NOMBRE_COMPLETO");

        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION"))
        {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION") + "Android/";
        }

        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS"))
        {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRUEBAS") + "Android/";
        }

        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION-LOCAL"))
        {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION_LOCAL") + "Android/";
        }


        etQuienReporta = (EditText) findViewById(R.id.et_quien_reporta);
        etSistemaCaja = (EditText) findViewById(R.id.et_sistema_de_caja);
        etHoraFecha = (TextView) findViewById(R.id.et_horayfecha);
        etCreditosRec=(EditText) findViewById(R.id.et_creditos_reclamados);
        etProblReportada=(EditText) findViewById(R.id.et_quien_como);
        cbPantallaAzul=(CheckBox) findViewById(R.id.cb_pantalla_azul);
        cbPantallaCont=(CheckBox) findViewById(R.id.cb_pantalla_contabilidad);
        spListaJuegos=(Spinner) findViewById(R.id.sp_juegos);
        etJuego=(TextView) findViewById(R.id.tv_juego);etJuego.setText(juego);
        lyJuegos=(LinearLayout) findViewById(R.id.ly_juegos);



        if( juego.equals("NO DISPONIBLE") ){
            new formatoPantAzul.SolicitaListaJuegos().execute();
        }

        else{
            etJuego.setVisibility(View.VISIBLE);
            spListaJuegos.setVisibility(View.GONE);
        }



    }







    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //java.util.Calendar cal = new GregorianCalendar(year, month, day);
        //horaFecha=dateFormat.format(cal.getTime());

        getFecha=""+day+"/"+(month+1)+"/"+year+"  ";

        etHoraFecha.setText(getFecha+getHora);

        selectDate=true;

    }



    public void datePicker(View view){
        DatePickerFragment fragment = new DatePickerFragment();
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_edit_name");
        fragment.show(manager,"fragment_edit_name" );
    }





    public void timePickerClick(View view ){
        // Get Current time
        int hour = Calendar.HOUR_OF_DAY;
        int minute =Calendar.MINUTE;
        TimePickerDialog timePickerDialog = new TimePickerDialog(formatoPantAzul.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        getHora=hourOfDay + ":" + minute;
                        etHoraFecha.setText(getFecha+getHora);
                        selectTime=true;
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }




    public void clickButonEnviarFormato(View view) {

        boolean isok=true;

        quienReporta=etQuienReporta.getText().toString();
        sistemaDeCaja=etSistemaCaja.getText().toString();
        creditosReclamados=etCreditosRec.getText().toString();
        problematicaRepo=etProblReportada.getText().toString();
        fechaHora=etHoraFecha.getText().toString();

        if(cbPantallaAzul.isChecked()){   checkPantallaazul="1";   } else{checkPantallaazul="0";}
        if(cbPantallaCont.isChecked()){  checkPantallaConta="1"; }   else{checkPantallaConta="0";}

        if(quienReporta.equals("") || quienReporta.equals(" ")){isok=false;etQuienReporta.setBackgroundResource(R.drawable.custom_item_empty);}
        if(creditosReclamados.equals("") || creditosReclamados.equals(" ")){isok=false;etCreditosRec.setBackgroundResource(R.drawable.custom_item_empty);}

        if(problematicaRepo.equals("") || problematicaRepo.equals(" ")){ isok=false;etProblReportada.setBackgroundResource(R.drawable.custom_item_empty);  }


        if(juego.equals("NO DISPONIBLE")){
            sendJuego = spListaJuegos.getSelectedItem().toString();
        }
        else{sendJuego=etJuego.getText().toString();}
        if(sendJuego.equals("NO DISPONIBLE")){isok=false;lyJuegos.setBackgroundResource(R.drawable.custom_item_empty);}else{}


        if(isok & selectDate & selectTime){ new formatoPantAzul.EnviaFormatoServer().execute();}
        else{
            toastAlert("Llena todos los campos resaltados en Rojo, fecha y hora.");

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





    class EnviaFormatoServer extends AsyncTask<Void, Void, Void> {
        //   ProgressDialog loading;
        String exito="0",respuesta="";
        private ProgressDialog pDialog;
        protected void onPreExecute() {
            //     loading = ProgressDialog.show(Activity_Gastos.this, "Conectando...", null,true,false);
            pDialog = new ProgressDialog(formatoPantAzul.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        protected Void doInBackground(Void... JSONurl) {
            PostGet servicios=new PostGet(context);

            final JSONObject jsonParam = new JSONObject();
            try {

                jsonParam.put("folio",folio);
                jsonParam.put("nameTecnico",nombretecnico);
                jsonParam.put("quienReporta",quienReporta);
                jsonParam.put("sistemaDeCaja",sistemaDeCaja);

                jsonParam.put("creditosReclamados",creditosReclamados);
                jsonParam.put("pantallaAzul",checkPantallaazul);
                jsonParam.put("pantallaContabilidad",checkPantallaConta);
                jsonParam.put("problematicaRep",problematicaRepo);
                jsonParam.put("horaFecha",fechaHora);
                jsonParam.put("juego",sendJuego);

                Log.i(metodo+"IngresaRevJugada","Peticion Post Url "+ baseUrl+url);
                Log.i(metodo+"IngresaRevJugada ","Cuerpo="+jsonParam.toString());

                respuesta=servicios.ConexionPost(baseUrl+url,jsonParam);

                Log.i(metodo+"IngresaRevJugada ","Respuesta= "+respuesta);
                JSONObject json=new JSONObject(respuesta);
                exito=json.getString("responder");


            } catch (Exception e) {
                Log.e(metodo+"IngresaRevJugada", "Peticion Post  Error" + e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            if(exito.equals("1")){
                toastAlert("Formato Enviado Exitosamente!!");
                finish();
            }
            else{
                toastAlert("Error Al Ingresar Revision de jugada");
            }
            // loading.dismiss();
            pDialog.dismiss();
        }
    }


    class SolicitaListaJuegos extends AsyncTask<Void, Void, Void> {
        //ProgressDialog loading;
        private String respuesta = "";
        private ProgressDialog pDialog;
        private String exito="0",error="";


        protected void onPreExecute() {
            //  loading = ProgressDialog.show(Activity_Lista.this, "Conectando...", null,true,true);
            pDialog = new ProgressDialog(formatoPantAzul.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);

            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);

        }

        protected Void doInBackground(Void... JSONurl) {
            PostGet servicios = new PostGet(context);

            try {
                Log.i(metodo + "SolicitaListaJuegos GET url",baseUrl+url2);
                respuesta = servicios.ConexionGet(baseUrl+url2);
                Log.i(metodo + "SolicitaListaJuegos RESPUESTA", respuesta);

                JSONObject json = new JSONObject(respuesta);
                JSONArray jsonArray = json.getJSONArray("juegos");
                listaJuegos = new String[jsonArray.length()+1];
                listaJuegos[0]="NO DISPONIBLE";

                for (int i = 1; i <jsonArray.length()+1; i++) {
                    listaJuegos[i]= jsonArray.get(i-1).toString();

                }

                JSONObject json2 = new JSONObject(json.getString("responder"));
                exito=json2.getString("exito");


            } catch (Exception e) {
                Log.e(metodo + "SolicitaListaJuegos", "ERROR En doInBackground: Exception :" + e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            if (exito.equals("1")) {
                ArrayAdapter<String> spArrayAdapter2 = new ArrayAdapter<String>(formatoPantAzul.this, android.R.layout.simple_spinner_item, listaJuegos);
                spListaJuegos.setAdapter(spArrayAdapter2);
            }


            else{finish();}

            pDialog.dismiss();

        }

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



    private void toastAlert(String ms) {
        Toast.makeText(getApplicationContext(), ms, Toast.LENGTH_LONG).show();

    }



}
