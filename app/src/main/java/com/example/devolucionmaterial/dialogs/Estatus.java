package com.example.devolucionmaterial.dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.MenuInicialComercial;
import com.example.devolucionmaterial.MenuInicialTecnico;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.materialesSeccion.Registro;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Estatus extends Activity implements OnItemSelectedListener {
    String metodo;
    Context context;
    private TextView tvEstatus, tvHorasServ, tvSalaInicial;
    private ListView lvListaOpcionesEstatus;
    private LinearLayout llEstatus;
    private ProgressDialog pDialog;
    private List<OpcionEstatus> listaOpcionesEstatus;
    private ArrayAdapter<OpcionEstatus> adaptador;
    private static String funcionMenuProFirma = "funcion=Menu_Pro_Firma";
    private static String funcionCambiaEstatusFirmaPro = "funcion=Cambia_Estatus_Firma_Pro";
    private static String docPHPMenuProFirma = "MenuProFirma.php?";
    private static String docPHPCambiaEstatusFirmaPro = "CambiaEstatusFirmaPro.php?";
    private static String funcionMenuTecFirma = "funcion=MenuTecFirma.php";
    private static String funcionCambiaEstatusFirmaTec = "funcion=Cambiar_Estatus_Firma_Tec";
    private static String docPHPMenuTecFirma = "MenuTecFirma.php?";
    private static String docPHPCambiaEstatusFirmaTec = "CambiaEstatusFirmaTec.php?";
    private BDmanager manager;
    private JSONObject jsonArrayChild;
    private String folio = "0";
    private static String ESTATUS_EN_SERVICIO = "39",
            ESTATUS_POR_INCIDENCIA = "109",
            ESTATUS_DIA_DE_DESCANSO = "79",
            ESTATUS_MIX = "80",
            ESTATUS_VACACIONES = "81",
            ESTATUS_INCAPACIDAD = "89",
            ESTATUS_FUERA_DE_SERVICIO = "40";
    private static int REQUEST_EST_EN_SERV = 1, REQUEST_EST_OUT_SERV = 2;
    private int ESTATUS_ACTIVIDAD = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_estatus_tecnico);
        context = this;
        manager = new BDmanager(context);
        BDVarGlo.setVarGlo(context, "VAR_ESTATUS_ESTATUS_ID_ANTERIOR", BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS"));
        BDVarGlo.setVarGlo(context, "VAR_ESTATUS_ESTATUS_EN_SERVICIO_ANTERIOR", BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO"));
        setupEstatus();
    }

    private void setupEstatus() {
        metodo = "setupEstatusPromotora()";
        tvEstatus = (TextView) findViewById(R.id.tvEstatus);
        tvHorasServ = (TextView) findViewById(R.id.tvHoras);
        tvSalaInicial = (TextView) findViewById(R.id.id_tvSalaInicial);
        llEstatus = (LinearLayout) findViewById(R.id.llEstatus);
        lvListaOpcionesEstatus = (ListView) findViewById(R.id.lvEstatus);
        lvListaOpcionesEstatus.setOnItemClickListener(selectedItem);
        barraProgresoEstatus();
    }

    public OnItemClickListener selectedItem = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
            metodo = "OnItemClickListener()";
            OpcionEstatus item = listaOpcionesEstatus.get(posicion);
            BDVarGlo.setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", item.getEstEnServ());
            BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", item.getEstatus());
            MensajeEnConsola.log(context, metodo, "APRETASTE\n" + item.getTituloEstatus() + " -> OfficeID -> " + BDVarGlo.getVarGlo(context, "INFO_USUARIO_OFFICE_ID") + " -> estatus -> " + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS") + " -> estEnServ -> " + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO"));
            /*
            EN SERVICIO 				-> estatus -> 39 	-> estEnServ -> 1
			Fuera de Servicio 			-> estatus -> 40 	-> estEnServ -> 0
			Traslado a Sala 			-> estatus -> 39 	-> estEnServ -> 2
			Has llegado a Sala 			-> estatus -> 39 	-> estEnServ -> 1
			Hora de Comida 				-> estatus -> 39 	-> estEnServ -> 4
			Fin de Hora de Comida 		-> estatus -> 39 	-> estEnServ -> 1
			POR INCIDENCIA 				-> estatus -> 109 	-> estEnServ -> 0
			Fin de S. por Incidencia 	-> estatus -> 40 	-> estEnServ -> 0
			DÍA DE DESCANSO 			-> estatus -> 79 	-> estEnServ -> 0
			Fin de Día de Descanso 		-> estatus -> 40 	-> estEnServ -> 0
			MIX 						-> estatus -> 80 	-> estEnServ -> 0
			Fin de Mix 					-> estatus -> 40 	-> estEnServ -> 0
			VACACIONES 					-> estatus -> 81 	-> estEnServ -> 0
			Fin de Vacaciones 			-> estatus -> 40 	-> estEnServ -> 0
			INCAPACIDAD 				-> estatus -> 89 	-> estEnServ -> 0
			Fin de Incapacidad 			-> estatus -> 40 	-> estEnServ -> 0
			Fin de asignado				-> estatus -> 39 	-> estEnServ -> 1
			*/


			/*if("Fin de S. por Incidencia".equals(item.getTituloEstatus()) ||
                "Fin de Día de Descanso".equals(item.getTituloEstatus()) ||
				"Fin de Mix".equals(item.getTituloEstatus()) ||
				"Fin de Vacaciones".equals(item.getTituloEstatus()) ||
				"Fin de Incapacidad".equals(item.getTituloEstatus()) ||
				"Fin de asignado".equals(item.getTituloEstatus())) {
				String mandaURL = "";
				if("comercial".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
					mandaURL = MainActivity.url + docPHPCambiaEstatusFirmaPro +"promotoraidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID") +
							"&estatusx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS") +
							"&estEnServx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO") +
							"&officeidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_OFFICE_ID") +
							"&foliox=" + folio;
				}
				if("tecnico".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
					mandaURL = MainActivity.url + docPHPCambiaEstatusFirmaTec + "tecnicoidx="+BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID")+
							"&estatusx="+BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS")+
							"&estEnServx="+BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO")+
							"&officeidx="+BDVarGlo.getVarGlo(context, "INFO_USUARIO_OFFICE_ID")+
							"&foliox="+folio;
				}
				new ProgresoActualizaEst().execute(mandaURL);
			}else */
            if ("EN SERVICIO".equals(item.getTituloEstatus()) ||
                    "Fuera de Servicio".equals(item.getTituloEstatus()) ||
                    "Traslado a Sala".equals(item.getTituloEstatus())) {
                llamaDialogoOpcionesEstEnServ();
            } else {
                barraProgresoActualizaEst();
            }
        }
    };

    void MuestraLista(String menu, String boton, String estatus1, String horas, String minutos) {
        if (menu.equals("1")) {
            llEstatus.setVisibility(View.GONE);
            listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titEstEnSer), getString(R.string.infoEstEnSer), ESTATUS_EN_SERVICIO, "1", "5"));
            listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titEstPorInc), getString(R.string.infoEstPorInc), ESTATUS_POR_INCIDENCIA, "0", "8"));
            listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titEstDiaD), getString(R.string.infoEstDiaDesc), ESTATUS_DIA_DE_DESCANSO, "0", "3"));
            listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titEstMix), getString(R.string.infoEstMix), ESTATUS_MIX, "0", "9"));
            listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titEstVac), getString(R.string.infoEstVacaciones), ESTATUS_VACACIONES, "0", "11"));
            listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titEstInc), getString(R.string.infoEstPorInc), ESTATUS_INCAPACIDAD, "0", "7"));
            ESTATUS_ACTIVIDAD = 1;
        } else {
            llEstatus.setVisibility(View.VISIBLE);
            tvEstatus.setText(estatus1);
            // TODO: 03/04/2017 se comenta las operaciones de fechas en el telefono
            //tvHorasServ.setText(Registro.diferenciaFechas(context, BDVarGlo.getVarGlo(context, "INFO_USUARIO_HORA_INICIA"), Registro.obtenerDiaHora()));
            tvHorasServ.setText(horas+" H "+minutos+" m" );
            Log.e("dia y hora", Registro.obtenerDiaHora());
            Log.e("INFO_USUARIO_HORA_INICIA", BDVarGlo.getVarGlo(context, "INFO_USUARIO_HORA_INICIA"));
            tvSalaInicial.setText(BDVarGlo.getVarGlo(context, "INFO_USUARIO_SALA_INICIA"));
            if (boton.equals("1")) {
                listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titEstFuera), getString(R.string.infoEstFuera), ESTATUS_FUERA_DE_SERVICIO, "0", "6"));
                listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titTraslado), getString(R.string.infoTraslado), ESTATUS_EN_SERVICIO, "2", "10"));
                listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titHoraComida), getString(R.string.infoHoraComida), ESTATUS_EN_SERVICIO, "4", "4"));
                ESTATUS_ACTIVIDAD = 2;
            }
            if (boton.equals("79")) {
                listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titFinDiaDesc), getString(R.string.infoFinDiaDesc), ESTATUS_FUERA_DE_SERVICIO, "0", "3"));
                ESTATUS_ACTIVIDAD = 2;
            }
            if (boton.equals("80")) {
                listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titFinMix), getString(R.string.infoFinMix), ESTATUS_FUERA_DE_SERVICIO, "0", "9"));
                ESTATUS_ACTIVIDAD = 2;
            }
            if (boton.equals("81")) {
                listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titFinVacaciones), getString(R.string.infoFinVacaciones), ESTATUS_FUERA_DE_SERVICIO, "0", "11"));
                ESTATUS_ACTIVIDAD = 2;
            }
            if (boton.equals("89")) {
                listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titFinIncap), getString(R.string.infoFinIncap), ESTATUS_FUERA_DE_SERVICIO, "0", "7"));
                ESTATUS_ACTIVIDAD = 2;
            }
            if (boton.equals("109")) {
                listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titFinInc), getString(R.string.infoFinInc), ESTATUS_FUERA_DE_SERVICIO, "0", "8"));
                ESTATUS_ACTIVIDAD = 2;
            }

            if (boton.equals("2")) {
                listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titFinTras), getString(R.string.infoFinTras), ESTATUS_EN_SERVICIO, "1", "10"));
                ESTATUS_ACTIVIDAD = 2;
            }

            if (boton.equals("3")) {
                listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titFinAsig), getString(R.string.infoFinAsig), ESTATUS_EN_SERVICIO, "1", "2"));
                ESTATUS_ACTIVIDAD = 2;
            }

            if (boton.equals("4")) {
                listaOpcionesEstatus.add(new OpcionEstatus(getString(R.string.titFinHcom), getString(R.string.infoFinHcom), ESTATUS_EN_SERVICIO, "1", "4"));
                ESTATUS_ACTIVIDAD = 2;
            }
        }
        adaptador = new OpcionEstatusArrayAdapter(context, listaOpcionesEstatus);
        lvListaOpcionesEstatus.setAdapter(adaptador);
    }

    public void barraProgresoEstatus() {
        if (CheckService.internet(context)) new Progreso().execute();
        else Alert.ActivaInternet(context);
    }

    private class Progreso extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            //pDialog.setContentView(R.layout.custom_progressdialog);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            metodo = "Progreso<AsyncTask>.doInBackground()";
            String regresa = "";
            listaOpcionesEstatus = new ArrayList<OpcionEstatus>();

            try {

                JSONObject object = null;
                if ("comercial".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
                    object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPMenuProFirma + "promotoraidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
                }
                if ("tecnico".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
                    object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPMenuTecFirma + "tecnicoidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
                }
                //JSONObject object = JSON.load(context, metodo, funcionMenuProFirma+"&promotoraid="+BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
                assert object != null;
                JSONArray jsonArray = object.optJSONArray("tipoMenu");
                jsonArrayChild = jsonArray.getJSONObject(0);
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", jsonArrayChild.optString("estatusidx"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", jsonArrayChild.optString("estEnServx"));

                BDVarGlo.setVarGlo(context, "INFO_USUARIO_HORAS_ACTIVIDAD", jsonArrayChild.optString("horasx"));
                if ("".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_SALA_INICIA")))
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_SALA_INICIA", jsonArrayChild.optString("estatusx"));
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MuestraLista(jsonArrayChild.optString("menux"), jsonArrayChild.optString("botonx"), jsonArrayChild.optString("estatusx"), jsonArrayChild.optString("horasx"),jsonArrayChild.optString("minutosx"));
                                    }
                                });
                            }
                        }
                ).start();
            } catch (JSONException e) {
                MensajeEnConsola.log(context, metodo, "JSONException e = " + e.getMessage());
                regresa = "ERROR";
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
                regresa = "ERROR";
            }
            return regresa;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            if (result.equals("ERROR")) {
                Estatus.this.finish();
            }
        }
    }

    private void barraProgresoActualizaEst() {
        if (CheckService.internet(context)) {
            String mandaURL = "";
            if ("comercial".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
                mandaURL = MainActivity.url + docPHPCambiaEstatusFirmaPro + "promotoraidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID") +
                        "&estatusx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS") +
                        "&estEnServx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO") +
                        "&officeidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_OFFICE_ID") +
                        "&foliox=" + folio;
            }
            if ("tecnico".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
                mandaURL = MainActivity.url + docPHPCambiaEstatusFirmaTec + "tecnicoidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID") +
                        "&estatusx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS") +
                        "&estEnServx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO") +
                        "&officeidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_OFFICE_ID") +
                        "&foliox=" + folio;
            }
            Log.e("url ProgresoActualizaEst ", mandaURL);
            new ProgresoActualizaEst().execute(mandaURL);
        } else {
            Alert.ActivaInternet(context);
        }
    }

    void validaOpenCloseEstatus() {
        metodo = "validaOpenCloseEstatus()";
        if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals(ESTATUS_EN_SERVICIO) &&
                BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("1")) {
            String salaIni = manager.consulta("SELECT nombre FROM csala WHERE officeID=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_OFFICE_ID") + "", "");
            BDVarGlo.setVarGlo(context, "INFO_USUARIO_SALA_INICIA", salaIni);
            BDVarGlo.setVarGlo(context, "INFO_USUARIO_HORA_INICIA", Registro.obtenerDiaHora());
        }
        if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals(ESTATUS_FUERA_DE_SERVICIO) && BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("0")) {
            BDVarGlo.setVarGlo(context, "INFO_USUARIO_SALA_INICIA", "");
            BDVarGlo.setVarGlo(context, "INFO_USUARIO_HORA_INICIA", Registro.obtenerDiaHora());
        }
    }

    void llamaDialogoOpcionesEstEnServ() {
        Intent intent = new Intent(context, DialogoOpcionesEstEnServ.class);
        startActivityForResult(intent, REQUEST_EST_OUT_SERV);
    }

    private class ProgresoActualizaEst extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        @Override
        protected String doInBackground(String... params) {
            metodo = "ProgresoActualizaEst<AsyncTask>.doInBackground()";
            String regresa = "";
            try {
                JSONObject object = JSONparse.consultaURL(context, metodo, params[0]);
                assert object != null;
                JSONArray jsonArray = object.optJSONArray("estatus");
                jsonArrayChild = jsonArray.getJSONObject(0);
                regresa = jsonArrayChild.optString("res");
            } catch (JSONException e) {
                MensajeEnConsola.log(context, metodo, "JSONException e = " + e.getMessage());
                regresa = "ERROR";
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Error MA001 " + e.getMessage());
                regresa = "ERROR";
            }
            return regresa;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            metodo = "ProgresoActualizaEst<>.onPostExecute()";
            if (result.equals("ERROR")) {
                Estatus.this.finish();
            } else {
                if (result.equals("1")) {
                    validaOpenCloseEstatus();
                    //FragmentHead.ActualizaImagenEstatus();
                    //lo siguiente solo estara disponible hasta que se cambien todas las variables globales
                    BDVarGlo.setDatosUsuario(context);
                    if ("comercial".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
                        MenuInicialComercial.actualizaEstatusComercial();
                    } else if ("tecnico".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
                        MenuInicialTecnico.actualizaEstatusTecnico();
                    }
                    Alert.Alerta(context, metodo, 0, getString(R.string.estActualizado));
                    Estatus.this.finish();
                } else {
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", BDVarGlo.getVarGlo(context, "VAR_ESTATUS_ESTATUS_ID_ANTERIOR"));
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", BDVarGlo.getVarGlo(context, "VAR_ESTATUS_ESTATUS_EN_SERVICIO_ANTERIOR"));
                    Alert.Alerta(context, metodo, 0, getString(R.string.estNoActualizado));
                }
            }
        }
    }

    private class OpcionEstatus {
        private String tituloEstatus, descEstatus, estatus, estatusEnServ, imgid;

        public OpcionEstatus(String tituloEstatus, String descEstatus, String estatus, String estEnServ, String imgid) {
            this.tituloEstatus = tituloEstatus;
            this.descEstatus = descEstatus;
            this.estatus = estatus;
            this.estatusEnServ = estEnServ;
            this.imgid = imgid;
        }

        public String getTituloEstatus() {
            return tituloEstatus;
        }

        public String getDescEstatus() {
            return descEstatus;
        }

        public String getEstatus() {
            return estatus;
        }

        public String getEstEnServ() {
            return estatusEnServ;
        }

        public String getImgid() {
            return imgid;
        }
    }

    public class OpcionEstatusArrayAdapter extends ArrayAdapter<OpcionEstatus> {
        public OpcionEstatusArrayAdapter(Context context, List<OpcionEstatus> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Obteniendo una instancia del inflater
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Salvando la referencia del View de la fila
            View listItemView = convertView;

            //Comprobando si el View no existe
            if (null == convertView) {
                //Si no existe, entonces inflarlo con two_line_list_item.xml
                listItemView = inflater.inflate(R.layout.item_lista_menu_est_tec, parent, false);
            }

            //Obteniendo instancias de los text views
            TextView titulo = (TextView) listItemView.findViewById(R.id.tvTitEst);
            TextView descripcion = (TextView) listItemView.findViewById(R.id.tvDesEst);
            ImageView imgEst = (ImageView) listItemView.findViewById(R.id.imgEstatus);


            //Obteniendo instancia de la Tarea en la posici�n actual
            OpcionEstatus item = getItem(position);

            if (item.imgid.equals("1")) {
                imgEst.setBackgroundResource(R.drawable.est_activo);
            }
            if (item.imgid.equals("2")) {
                imgEst.setBackgroundResource(R.drawable.est_asignado);
            }
            if (item.imgid.equals("3")) {
                imgEst.setBackgroundResource(R.drawable.est_dia_descanso);
            }
            if (item.imgid.equals("4")) {
                imgEst.setBackgroundResource(R.drawable.est_en_comida);
            }
            if (item.imgid.equals("5")) {
                imgEst.setBackgroundResource(R.drawable.est_en_servicio);
            }
            if (item.imgid.equals("6")) {
                imgEst.setBackgroundResource(R.drawable.est_inactivo);
            }
            if (item.imgid.equals("7")) {
                imgEst.setBackgroundResource(R.drawable.est_incapacidad);
            }
            if (item.imgid.equals("8")) {
                imgEst.setBackgroundResource(R.drawable.est_incidencia);
            }
            if (item.imgid.equals("9")) {
                imgEst.setBackgroundResource(R.drawable.est_mix);
            }
            if (item.imgid.equals("10")) {
                imgEst.setBackgroundResource(R.drawable.est_traslado_sala);
            }
            if (item.imgid.equals("11")) {
                imgEst.setBackgroundResource(R.drawable.est_vacaciones);
            }


            titulo.setText(item.getTituloEstatus());
            titulo.setTextColor(Color.BLACK);
            descripcion.setText(item.getDescEstatus());

            //Devolver al ListView la fila creada
            return listItemView;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        metodo = "onActivityResult()";
        if (requestCode == REQUEST_EST_EN_SERV) {
            if (resultCode == Activity.RESULT_OK) {
                barraProgresoActualizaEst();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Alert.Alerta(context, metodo, 2, getString(R.string.sinSalaInic));
            }
        }
        if (requestCode == REQUEST_EST_OUT_SERV) {
            if (resultCode == Activity.RESULT_OK) {
                barraProgresoActualizaEst();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Alert.Alerta(context, metodo, 2, getString(R.string.sinSalaFinalDialog));
            }
        }
    }
}
//470