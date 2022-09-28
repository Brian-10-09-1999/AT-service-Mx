package com.example.devolucionmaterial.activitys.foliosPendientesSeccion;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import android.icu.util.Calendar;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.api.PostGet;
import com.example.devolucionmaterial.chat.model.Contact;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.libraryvideo.subtitle.CaptionsView;
import com.google.android.gms.maps.model.ButtCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import retrofit2.http.POST;

//import static com.example.devolucionmaterial.R2.id.date;
//import static com.example.devolucionmaterial.R2.id.email;

public class RevisionJugada extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener{

    private LinearLayout lyClickProcedencia,lyProcedencia,
                         lyClickInfoJugada,lyInfoJugada,
                         lyClickPremiosRev,lyPremiosRev,
                         lyClickProblematicaSala,lyProblematicaSala,
                         lyClickPruebasTec,lyPruebasTec,
                         lyClickObservacionesTec,lyObservacionesTec, lyJuegos;

    private boolean desProcedencia=true, desInfoJugada=false,desPremiosRev=false,desProblematicaSala=false,desPruebasTec=false,desObservacionesTec=false,selectDate=false,selectTime=false;
    private EditText etQuienReporta,etEmailCliente,etSistemaCaja,etApuesta,etProporcionalRec,etCreditosReg,
                      etCreditosRec,etProblReportada,etSerieCpu,etObservacionesComen;

    private String listaJuegos[];
    private String quienReporta, emailCliente,juego,sendJuego, sistemaDeCaja, horaFecha, denominacion, apuesta, cartonesActivos,
            jugadaTouch, jugadaBotonera, airCash, propReclamado, credRegistrados, credReclamados, ProbReportada,
            statusMaquina, noSeriePlaca, seRelizoPago, seRealizoRetiro, ultimasJugadas, pantallaJugada,
            contaEstadistica, histEventos, histPagos, histEventosSeg, contEstadSegmento, calibracionTouch, botones,
            red, fuentePoder, statusMaquina2, cherryMaquina, obserComentarios,nombretecnico;
    private Spinner spDenominacion,spCartonesActivos,spListaJuegos;

    private Switch swTouch,swBotonera,swBotin,swStatusMaquina,swSeRealizoPago,swSeRealizoRetiro,swCalibracionTouch,swBotones,swRed,swFuentePoder,
                    swStatusMaquina2,swCherryMaquina;
    private TextView etHoraFecha,etJuego;

    private CheckBox cbUltimJugadas,cbPantJugada,cbContEsta,cbHistEventos,cbHistPagos,cbHistEventosSeg,cbContEstSegmento;
    private Context context;
    private String folio,getFecha="",getHora="";

    private String baseUrl="";
    private String url="revisionJugada.php";
    private String url2="ListaJuegos.php";

    private String metodo = "(foliosPendientesSeccion) ---------- RevisionJugada.";
    private Button butEnviar;
    protected Toolbar toolbar;
    private DateFormat dateFormat;
    FragmentManager manager =getFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision_jugada);
        context=this;

        initToolbar("Revision de Jugada", true, true);

        if (getIntent().getExtras() != null) {
            folio = getIntent().getExtras().getString("folio");
            juego=getIntent().getExtras().getString("juego");
        }

        nombretecnico=BDVarGlo.getVarGlo(this, "INFO_USUARIO_NOMBRE_COMPLETO");

        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION") + "Android/";
        }
        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRUEBAS") + "Android/";
        }
        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION-LOCAL")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION_LOCAL") + "Android/";
        }



        butEnviar=(Button) findViewById(R.id.btn_enviar_rev);

        lyClickProcedencia = (LinearLayout) findViewById(R.id.ly_click_procedencia);
        lyProcedencia = (LinearLayout) findViewById(R.id.ly_procedencia);
        lyProcedencia.setVisibility(View.GONE);

        lyClickInfoJugada = (LinearLayout) findViewById(R.id.ly_click_info_jugada);
        lyInfoJugada = (LinearLayout) findViewById(R.id.ly_info_jugada);
        lyInfoJugada.setVisibility(View.GONE);

        lyClickPremiosRev = (LinearLayout) findViewById(R.id.ly_click_premiosarev);
        lyPremiosRev = (LinearLayout) findViewById(R.id.ly_premiosarev);
        lyPremiosRev.setVisibility(View.GONE);

        lyClickProblematicaSala = (LinearLayout) findViewById(R.id.ly_click_problematica_rep_sala);
        lyProblematicaSala = (LinearLayout) findViewById(R.id.ly_problematica_rep_sala);
        lyProblematicaSala.setVisibility(View.GONE);

        lyClickPruebasTec = (LinearLayout) findViewById(R.id.ly_click_ptremls);
        lyPruebasTec = (LinearLayout) findViewById(R.id.ly_ptremls);
        lyPruebasTec.setVisibility(View.GONE);

        lyClickObservacionesTec = (LinearLayout) findViewById(R.id.ly_click_obs_tec);
        lyObservacionesTec = (LinearLayout) findViewById(R.id.ly_obs_tec);
        lyObservacionesTec.setVisibility(View.GONE);
        lyJuegos=(LinearLayout) findViewById(R.id.ly_juegos);

        etQuienReporta = (EditText) findViewById(R.id.et_quien_reporta);
        etEmailCliente = (EditText) findViewById(R.id.et_email_cliente);
        etJuego=(TextView) findViewById(R.id.tv_juego);etJuego.setText(juego);
        etSistemaCaja = (EditText) findViewById(R.id.et_sistema_de_caja);
        etHoraFecha = (TextView) findViewById(R.id.et_horayfecha);


        spListaJuegos=(Spinner) findViewById(R.id.sp_juegos);
        spDenominacion = (Spinner) findViewById(R.id.sp_denominacion);
        spCartonesActivos = (Spinner) findViewById(R.id.sp_cartones_activos);
        String items1[] = {"5c", "10c", "15c", "20c", "25c", "50c", "$1.00"};
        ArrayAdapter<String> spArrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items1);
        spDenominacion.setAdapter(spArrayAdapter1);
        String items2[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
        ArrayAdapter<String> spArrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items2);
        spCartonesActivos.setAdapter(spArrayAdapter2);


        etApuesta=(EditText) findViewById(R.id.et_apuesta);

        swTouch=(Switch) findViewById(R.id.sw_touch);
        swBotonera=(Switch) findViewById(R.id.sw_botonera);
        swBotin=(Switch) findViewById(R.id.sw_cash_botin);

        etProporcionalRec=(EditText) findViewById(R.id.et_proporcional_reclamado);
        etCreditosReg=(EditText) findViewById(R.id.et_creditos_registrados);
        etCreditosRec=(EditText) findViewById(R.id.et_creditos_reclamados);

        etProblReportada=(EditText) findViewById(R.id.et_quien_como);
        etSerieCpu=(EditText) findViewById(R.id.et_no_serie_cpu);

        swStatusMaquina=(Switch) findViewById(R.id.sw_estatus_maq);
        swSeRealizoPago=(Switch) findViewById(R.id.sw_se_realizo_pago);
        swSeRealizoRetiro=(Switch) findViewById(R.id.sw_se_realizo_retiros);

        cbUltimJugadas=(CheckBox) findViewById(R.id.cb_ultimas_jugadas);
        cbPantJugada=(CheckBox) findViewById(R.id.cb_pantalla_jugada);
        cbContEsta=(CheckBox) findViewById(R.id.cb_contabilidad_yest);
        cbHistEventos=(CheckBox) findViewById(R.id.cb_historial_even);
        cbHistPagos=(CheckBox) findViewById(R.id.cb_historial_pagos);
        cbHistEventosSeg=(CheckBox) findViewById(R.id.cb_hetmmsr);
        cbContEstSegmento=(CheckBox) findViewById(R.id.cb_cetmmsr);

        swCalibracionTouch=(Switch) findViewById(R.id.sw_calibra_touch);
        swBotones=(Switch) findViewById(R.id.sw_botones);
        swRed=(Switch) findViewById(R.id.sw_red);
        swFuentePoder=(Switch) findViewById(R.id.sw_fuente_poder);
        swStatusMaquina2=(Switch) findViewById(R.id.sw_status_maquina2);
        swCherryMaquina=(Switch) findViewById(R.id.sw_cherry_maquina);

        etObservacionesComen=(EditText) findViewById(R.id.et_observaciones_y_comen);



        if( juego.equals("NO DISPONIBLE") ){

            new RevisionJugada.SolicitaListaJuegos().execute();

        }
        else{
             etJuego.setVisibility(View.VISIBLE);
             spListaJuegos.setVisibility(View.GONE);
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //java.util.Calendar cal = new GregorianCalendar(year, month, day);
        //horaFecha=dateFormat.format(cal.getTime());

          getFecha=""+day+"/"+(month+1)+"/"+year+"  ";

        etHoraFecha.setText(getFecha+getHora);

        selectDate=true;

    }

    public void setLyClickProcedencia(View view){
        if(desProcedencia){
            lyProcedencia.setVisibility(View.VISIBLE);
            lyClickProcedencia.setBackgroundResource(R.drawable.ly_plegar);
            desProcedencia=false;
        }
        else{

            lyClickProcedencia.setBackgroundResource(R.drawable.ly_desplegar);
            lyProcedencia.setVisibility(View.GONE);
            desProcedencia=true;
        }

    }

    public  void setLyClickInfoJugada(View view){
        if(desInfoJugada){

            lyClickInfoJugada.setBackgroundResource(R.drawable.ly_desplegar);
            lyInfoJugada.setVisibility(View.GONE);
            desInfoJugada=false;

        }

        else{

            lyInfoJugada.setVisibility(View.VISIBLE);
            lyClickInfoJugada.setBackgroundResource(R.drawable.ly_plegar);
            desInfoJugada=true;
        }



    }

    public  void setLyClickPremiosRev(View view){
        if( desPremiosRev){

            lyClickPremiosRev.setBackgroundResource(R.drawable.ly_desplegar);
            lyPremiosRev.setVisibility(View.GONE);
            desPremiosRev=false;

        }

        else{

            lyPremiosRev.setVisibility(View.VISIBLE);
            lyClickPremiosRev.setBackgroundResource(R.drawable.ly_plegar);
            desPremiosRev=true;
        }

    }

    public  void setLyClickProblematicaSala(View view){
        if(desProblematicaSala){

            lyClickProblematicaSala.setBackgroundResource(R.drawable.ly_desplegar);
            lyProblematicaSala.setVisibility(View.GONE);
            desProblematicaSala=false;
        }

        else{

            lyClickProblematicaSala.setBackgroundResource(R.drawable.ly_plegar);
            lyProblematicaSala.setVisibility(View.VISIBLE);
            desProblematicaSala=true;
        }

    }

    public  void setLyClickPruebasTec(View view){
        if(desPruebasTec){

            lyClickPruebasTec.setBackgroundResource(R.drawable.ly_desplegar);
            lyPruebasTec.setVisibility(View.GONE);
            desPruebasTec=false;
        }

        else{

            lyClickPruebasTec.setBackgroundResource(R.drawable.ly_plegar);
            lyPruebasTec.setVisibility(View.VISIBLE);
            desPruebasTec=true;
        }

    }

    public  void setLyClickObservacionesTec(View view){
        if(desObservacionesTec){

            lyClickObservacionesTec.setBackgroundResource(R.drawable.ly_desplegar);
            lyObservacionesTec.setVisibility(View.GONE);
            desObservacionesTec=false;
        }

        else{

            lyClickObservacionesTec.setBackgroundResource(R.drawable.ly_plegar);
            lyObservacionesTec.setVisibility(View.VISIBLE);
            desObservacionesTec=true;
        }

    }

    public void clickButonEnviarRevJugada(View view) {

        boolean isok=true;

        quienReporta = etQuienReporta.getText().toString();
        emailCliente = etEmailCliente.getText().toString();


       if(juego.equals("NO DISPONIBLE")){
            sendJuego = spListaJuegos.getSelectedItem().toString();
       }
       else{sendJuego=etJuego.getText().toString();}




        if(quienReporta.equals(""))   {isok=false;etQuienReporta.setBackgroundResource(R.drawable.custom_item_empty);}else{}
        if(emailCliente.equals(""))   {isok=false;etEmailCliente.setBackgroundResource(R.drawable.custom_item_empty);}else{}
        if(sendJuego.equals("NO DISPONIBLE")){isok=false;lyJuegos.setBackgroundResource(R.drawable.custom_item_empty);}else{}

        sistemaDeCaja = etSistemaCaja.getText().toString();

        if(sistemaDeCaja.equals("")){isok=false; etSistemaCaja.setBackgroundResource(R.drawable.custom_item_empty); } else{}
         if(getFecha.equals("") || getHora.equals("")){  isok=false;}{ horaFecha = etHoraFecha.getText().toString();  }

        denominacion = spDenominacion.getSelectedItem().toString();

         if( etApuesta.getText().toString().equals("")){apuesta ="0";}else{apuesta= etApuesta.getText().toString();}
        cartonesActivos = spCartonesActivos.getSelectedItem().toString();

        if (swTouch.isChecked()) {
            jugadaTouch = "si";
        } else {
            jugadaTouch = "no";
        }
        if (swBotonera.isChecked()) {
            jugadaBotonera = "si";
        } else {
            jugadaBotonera = "no";
        }
        if (swBotin.isChecked()) {
            airCash = "si";
        } else {
            airCash = "no";
        }

        propReclamado = etProporcionalRec.getText().toString();

        if( etCreditosReg.getText().toString().equals("")){ credRegistrados="0"; }else{ credRegistrados=etCreditosReg.getText().toString();}

        credReclamados = etCreditosRec.getText().toString();

        if(credReclamados.equals("")){isok=false;  etCreditosRec.setBackgroundResource(R.drawable.custom_item_empty);}else{}

        ProbReportada = etProblReportada.getText().toString();

        if(ProbReportada.equals("")){isok=false; etProblReportada.setBackgroundResource(R.drawable.custom_item_empty);}else{}



        if (swStatusMaquina.isChecked()) {
            statusMaquina = "operativa";
        } else {
            statusMaquina = "inoperativa";
        }
        noSeriePlaca = etSerieCpu.getText().toString();
        if (swSeRealizoPago.isChecked()) {
            seRelizoPago = "si";
        } else {
            seRelizoPago = "no";
        }
        if (swSeRealizoRetiro.isChecked()) {
            seRealizoRetiro = "si";
        } else {
            seRealizoRetiro = "no";
        }


        if (cbUltimJugadas.isChecked()) {
            ultimasJugadas = "1";
        } else {
            ultimasJugadas = "0";
        }
        if (cbPantJugada.isChecked()) {
            pantallaJugada = "1";
        } else {
            pantallaJugada = "0";
        }
        if (cbContEsta.isChecked()) {
            contaEstadistica = "1";
        } else {
            contaEstadistica = "0";
        }
        if (cbHistEventos.isChecked()) {
            histEventos = "1";
        } else {
            histEventos = "0";
        }
        if (cbHistPagos.isChecked()) {
            histPagos = "1";
        } else {
            histPagos = "0";
        }
        if (cbHistEventosSeg.isChecked()) {
            histEventosSeg = "1";
        } else {
            histEventosSeg = "0";
        }
        if (cbContEstSegmento.isChecked()) {
            contEstadSegmento = "1";
        } else {
            contEstadSegmento= "0";
        }

        if(swCalibracionTouch.isChecked()){calibracionTouch="falla"; }else{calibracionTouch="sin problema";}
        if(swBotones.isChecked()){botones="falla";  }else{botones="sin problema";}
        if(swRed.isChecked()){red="falla";}else{red="sin problema";}
        if(swFuentePoder.isChecked()){fuentePoder="falla";}else{fuentePoder="sin problema";}
        if(swStatusMaquina2.isChecked()){statusMaquina2="falla";}else{statusMaquina2="sin problema";}
        if(swCherryMaquina.isChecked()){cherryMaquina="falla";}else{cherryMaquina="sin problema";}

        obserComentarios=etObservacionesComen.getText().toString();

        if(isok & selectDate & selectTime){ new RevisionJugada.IngresaRevJugada().execute();}
        else{
            toastAlert("Llena todos los campos resaltados en Rojo, fecha y hora.");

        }




    }





    private void toastAlert(String ms) {
        Toast.makeText(getApplicationContext(), ms, Toast.LENGTH_LONG).show();
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
         TimePickerDialog timePickerDialog = new TimePickerDialog(RevisionJugada.this,
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


    class IngresaRevJugada extends AsyncTask<Void, Void, Void> {
        //   ProgressDialog loading;
        String exito="0",respuesta="";
        private ProgressDialog pDialog;
        protected void onPreExecute() {
            //     loading = ProgressDialog.show(Activity_Gastos.this, "Conectando...", null,true,false);
            pDialog = new ProgressDialog(RevisionJugada.this);
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
                jsonParam.put("emailCliente",emailCliente);
                jsonParam.put("juego",sendJuego);
                jsonParam.put("sistemaDeCaja",sistemaDeCaja);
                jsonParam.put("horaFecha",horaFecha);
                jsonParam.put("denominacion",denominacion);
                jsonParam.put("apuesta",apuesta);
                jsonParam.put("cartonesActivos",cartonesActivos);
                jsonParam.put("jugadaTouch",jugadaTouch);
                jsonParam.put("jugadaTouch",jugadaTouch);
                jsonParam.put("jugadaBotonera",jugadaBotonera);
                jsonParam.put("airCash",airCash);
                jsonParam.put("propReclamado",propReclamado);
                jsonParam.put("credRegistrados",credRegistrados);
                jsonParam.put("credReclamados",credReclamados);
                jsonParam.put("ProbReportada",ProbReportada);
                jsonParam.put("statusMaquina",statusMaquina);
                jsonParam.put("noSeriePlaca",noSeriePlaca);
                jsonParam.put("seRelizoPago",seRelizoPago);
                jsonParam.put("seRealizoRetiro",seRealizoRetiro);
                jsonParam.put("ultimasJugadas",ultimasJugadas);
                jsonParam.put("pantallaJugada",pantallaJugada);
                jsonParam.put("contaEstadistica",contaEstadistica);
                jsonParam.put("histEventos",histEventos);
                jsonParam.put("histPagos",histPagos);
                jsonParam.put("histEventosSeg",histEventosSeg);
                jsonParam.put("contEstadSegmento",contEstadSegmento);
                jsonParam.put("calibracionTouch",calibracionTouch);
                jsonParam.put("botones",botones);
                jsonParam.put("red",red);
                jsonParam.put("fuentePoder",fuentePoder);
                jsonParam.put("statusMaquina2",statusMaquina2);
                jsonParam.put("cherryMaquina",cherryMaquina);
                jsonParam.put("obserComentarios",obserComentarios);


                Log.i(metodo+"IngresaRevJugada","Peticion Post Url "+ baseUrl+url);
                Log.i(metodo+"IngresaRevJugada ","Cuerpo="+jsonParam.toString());
                respuesta=servicios.ConexionPost(baseUrl+url,jsonParam);
                Log.i(metodo+"IngresaRevJugada ","Respuesta= "+respuesta);

                JSONObject json=new JSONObject(respuesta);
                exito=json.getString("responder");
                //gastoId=json.getString("gastoid");
                //SaldoTotal=json.getDouble("saldo");

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



    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }


    class SolicitaListaJuegos extends AsyncTask<Void, Void, Void> {
        //ProgressDialog loading;
        private String respuesta = "";
        private ProgressDialog pDialog;
        private String exito="0",error="";


        protected void onPreExecute() {
            //  loading = ProgressDialog.show(Activity_Lista.this, "Conectando...", null,true,true);
            pDialog = new ProgressDialog(RevisionJugada.this);
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
                ArrayAdapter<String> spArrayAdapter2 = new ArrayAdapter<String>(RevisionJugada.this, android.R.layout.simple_spinner_item, listaJuegos);
                spListaJuegos.setAdapter(spArrayAdapter2);
            }


           else{finish();}

            pDialog.dismiss();

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
