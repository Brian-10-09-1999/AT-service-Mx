package com.example.devolucionmaterial;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.mediosfolios.ListMediosFolios;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.mediosfolios.SelectMediaActivity;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.beans.ContactosSala;
import com.example.devolucionmaterial.beans.DatosFoliopend;
import com.example.devolucionmaterial.customview.AutoscaleEditText;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReporteFolioPendiente extends Activity implements OnClickListener {
    String metodo;
    Context context;
    private TextView tvFolio, tvSala, tvFalla, tvSubFalla, tvLicencia,
            tvJuego, tvAceptarFolio, tvRechazarFolio, tvTraslado, TvAcceso, tvAtiende, tvEstatus, tvRepara;
    private String[] arregloOpciones;
    private LinearLayout ll_actividades;
    private String resp1, estatus, respuesta, estatusTecnico, estEnServ, comentarios;
    private ProgressDialog pDialog, pDialog2;
    private JSONObject jsonArrayChildInicial, jsonArrayChild;
    private Menu mMenu;
    private BDmanager manager;
    private int CN_TECNICO_ID = 2;
    private int CN_REGION_ID = 3;
    private static String docPHPcambiaEstatusFirmaTec = "cambiaEstatusFirmaTec.php?";
    private static String docPHPverFolioPend = "verFolioPend.php?";
    private static String docPHPactividadesTec = "actividadesTecnicoHTML.php?";
    private Button btn_fragment_coment, btn_fragment_sala, btn_medios;

    private AlertDialog.Builder builder;
    public DialogComment dialogComment;
    public DialogDatoSala dialogDatoSala;
    private int fallaNum;
    public static int salaid;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_folio_pendiente);
        context = this;
        manager = new BDmanager(context);
        setupReporteFolioPendiente();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setupReporteFolioPendiente() {
        ll_actividades = (LinearLayout) findViewById(R.id.arfp_ll_actividades);
        tvFolio = (TextView) findViewById(R.id.lblFolio);
        tvSala = (TextView) findViewById(R.id.lblSala);
        tvFalla = (TextView) findViewById(R.id.lblFalla);
        tvSubFalla = (TextView) findViewById(R.id.lblSubFalla);
        tvLicencia = (TextView) findViewById(R.id.lblLicencia);
        tvJuego = (TextView) findViewById(R.id.lblJuego);

        tvTraslado = (TextView) findViewById(R.id.lblTraslado);
        TvAcceso = (TextView) findViewById(R.id.lblAcceso);
        tvAtiende = (TextView) findViewById(R.id.lblAtiende);
        tvEstatus = (TextView) findViewById(R.id.lblEstatus);
        tvRepara = (TextView) findViewById(R.id.lblRepara);
        tvAceptarFolio = (TextView) findViewById(R.id.lblAceptarFolio);
        tvRechazarFolio = (TextView) findViewById(R.id.lblRechazarFolio);

        btn_fragment_coment = (Button) findViewById(R.id.arfp_fragment_comment);
        btn_fragment_sala = (Button) findViewById(R.id.arfp_fragment_sala);
        btn_medios = (Button) findViewById(R.id.arfp_btn_medios);

        btn_fragment_coment.setOnClickListener(this);
        btn_fragment_sala.setOnClickListener(this);
        btn_medios.setOnClickListener(this);

        if (getIntent().getExtras() != null) {
            if (validarAccesoPorSala(getIntent().getExtras().getString("folioSala"))) {
                ll_actividades.setVisibility(View.GONE);
            }

        }

        barraProgresoPedirDatosReporteFolioPendiente();
        tvTraslado.setOnClickListener(this);
        TvAcceso.setOnClickListener(this);
        tvAtiende.setOnClickListener(this);
        tvEstatus.setOnClickListener(this);
        tvRepara.setOnClickListener(this);
        tvAceptarFolio.setOnClickListener(this);
        tvRechazarFolio.setOnClickListener(this);

    }

    Boolean validarAccesoPorSala(String valor) {
        if (valor.equals("0")) {
            return true;
        }
        return false;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("ValidFragment")
    public class DialogComment extends DialogFragment {
        public TextView tvComent;
        public AutoscaleEditText etComment;
        public ScrollView scrollview;


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.upload_coment, null);
            builder.setView(view);
            tvComent = (TextView) view.findViewById(R.id.ul_txt_historial);
            etComment = (AutoscaleEditText) view.findViewById(R.id.ul_et_comennt);
            scrollview = (ScrollView) view.findViewById(R.id.ul_crollcomentario);


            tvComent.setText(comentarios);
            scrollview.post(new Runnable() {
                @Override
                public void run() {
                    scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });

            ImageView btn = (ImageView) view.findViewById(R.id.ul_btn_enviar);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String comment = etComment.getText().toString();
                    if (!comment.equals("") && comment != null) {

                        String commentComplet = BDVarGlo.getVarGlo(context, "INFO_USUARIO_NOMBRE_COMPLETO") + " : "
                                + getDate() + " " + comment;

                        if (CheckService.internet(context))
                            sendComment(commentComplet);
                        else
                            Alert.ActivaInternet(context);

                        Log.e("commnet", commentComplet);
                    } else {
                        Toast.makeText(context, "Comentario vacio", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            return builder.create();


        }

        public String getDate() {

            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            String deta = (DateFormat.format("dd MMM yyyy", new java.util.Date()).toString());
            today.setToNow();
            String sTime = today.format("%H:%M");

            String copleteDate = deta + " " + sTime;

            return copleteDate;
        }


        public void setComentarios(final String comentario) {
            dialogComment.tvComent.setText(comentario);
            dialogComment.etComment.setText("");
            dialogComment.scrollview.post(new Runnable() {
                @Override
                public void run() {
                    scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });

        }

        // TODO: 19/01/2017 envia el comentario al servidor
        private void sendComment(String comentario) {
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);

            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<BeanResponse> call = serviceApi.CARGAR_COMENTARIIO(tvFolio.getText().toString(), comentario);
            call.enqueue(new Callback<BeanResponse>() {
                @Override
                public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                    pDialog.dismiss();
                    try {
                        if (response.body().getComment() != null) {
                            comentarios = response.body().getComment();
                            setComentarios(comentarios);

                        }
                    } catch (Exception e) {
                        Log.e("error", String.valueOf(e));

                    }
                }

                @Override
                public void onFailure(Call<BeanResponse> call, Throwable t) {
                    Log.e("error de respuesat", String.valueOf(t));
                    pDialog.dismiss();
                    Toast.makeText(context, getString(R.string.vuelve_a_intertarlo), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("ValidFragment")
    public class DialogDatoSala extends DialogFragment {
        Spinner spinnerContac;
        EditText etNombre;
        EditText etCargo;
        Button btnEnviar;
        String nombre = "", cargo = "";
        List<ContactosSala> contactosSalaList;
        int response;

        public DialogDatoSala(int response, List<ContactosSala> contactosSalaList) {
            this.contactosSalaList = contactosSalaList;
            this.response = response;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_data_sala, null);
            builder.setView(view);

            etNombre = (EditText) view.findViewById(R.id.fds_txt_nombre);
            etCargo = (EditText) view.findViewById(R.id.fds_txt_cargo);
            btnEnviar = (Button) view.findViewById(R.id.fds_btn_send);

            spinnerContac = (Spinner) view.findViewById(R.id.fds_sp_contacto);

            try {
                if (!contactosSalaList.isEmpty()) {

                    List<String> nombreContacto = new ArrayList<>();
                    nombreContacto.add("Seleccionar contacto");
                    for (int i = 0; i < contactosSalaList.size(); i++) {

                        ContactosSala it = contactosSalaList.get(i);
                        nombreContacto.add(it.getNombre());
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ReporteFolioPendiente.this,
                            android.R.layout.simple_spinner_item, nombreContacto);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerContac.setAdapter(dataAdapter);
                    spinnerContac.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                etNombre.setText("");
                                etCargo.setText("");
                            } else {
                                ContactosSala item = contactosSalaList.get(position - 1);
                                etNombre.setText(item.getNombre());
                                etCargo.setText(item.getCargo());
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else {
                    spinnerContac.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.e("error", String.valueOf(e));
            }

            btnEnviar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!etNombre.getText().toString().isEmpty() && !etCargo.getText().toString().isEmpty()) {

                        if (CheckService.internet(context))
                            sendDataSupervision(etNombre.getText().toString(), etCargo.getText().toString());
                        else
                            Alert.ActivaInternet(context);

                    } else {
                        Toast.makeText(context, "Faltan campos por llenar", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            return builder.create();


        }

        public void sendDataSupervision(final String nombre, final String cargo) {

            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<BeanResponse> call = serviceApi.DATOS_SUPERVISION_SALA(tvFolio.getText().toString(), nombre, cargo, salaid);
            call.enqueue(new Callback<BeanResponse>() {
                @Override
                public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                    pDialog.dismiss();

                    call.request().url();
                    try {
                        if (response.body() != null) {
                            if (response.body().getValue() == 1)
                                Toast.makeText(context, "Datos enviados", Toast.LENGTH_SHORT).show();
                            dialogDatoSala.dismiss();

                        }

                    } catch (Exception e) {
                        Toast.makeText(context, "Vuelve a intertarlo", Toast.LENGTH_SHORT).show();

                        Log.e("error", String.valueOf(e));
                    }


                }

                @Override
                public void onFailure(Call<BeanResponse> call, Throwable t) {
                    Toast.makeText(context, "Vuelve a intertarlo", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    Log.e("error", String.valueOf(t));
                }
            });
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void onClick(View v) {
        if (CheckService.internet(context)) {
            switch (v.getId()) {
                case R.id.lblTraslado:

                    estatus = "5";
                    respuesta = "1";
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", "39");
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", "2");
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_OFFICE_ID", "0");
                    // TODO: 07/12/2016 validacion si es que la falla es 31 para cambiar de conexion
                    if (tipofalla(fallaNum))
                        setActivdadTecnico();
                    else
                        barraProgresoSetActividadesTecnico();

                    break;
                case R.id.lblAcceso:
                    estatus = "5";
                    respuesta = "2";
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", "39");
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", "3");
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_OFFICE_ID", "0");
                    if (tipofalla(fallaNum))
                        setActivdadTecnico();
                    else
                        barraProgresoSetActividadesTecnico();
                    break;
                case R.id.lblAtiende:
                    estatus = "5";
                    respuesta = "3";
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", "39");
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", "3");
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_OFFICE_ID", "0");
                    if (tipofalla(fallaNum))
                        setActivdadTecnico();
                    else
                        barraProgresoSetActividadesTecnico();
                    break;
                case R.id.lblEstatus:
                    startActivity(new Intent(context, EstatusIncidencia.class));
                    break;
                case R.id.lblRepara:
                    estatus = "5";
                    respuesta = "4";
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", "39");
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", "1");
                    BDVarGlo.setVarGlo(context, "INFO_USUARIO_OFFICE_ID", "0");
                    if (tipofalla(fallaNum)) {
                        // TODO: 14/12/2016 validacion de terminar supervision
                        if (arregloOpciones[3].equals("0")) {
                            alertFinalizarSuper();
                        } else {
                            setActivdadTecnico();
                        }
                    } else
                        barraProgresoSetActividadesTecnico();
                    break;
                case R.id.lblAceptarFolio:
                    estatus = "4";
                    respuesta = "24";
                    if (tipofalla(fallaNum))
                        setActivdadTecnico();
                    else
                        barraProgresoSetActividadesTecnico();
                    break;
                case R.id.lblRechazarFolio:
                    estatus = "4";
                    respuesta = "25";
                    if (tipofalla(fallaNum))
                        setActivdadTecnico();
                    else
                        barraProgresoSetActividadesTecnico();
                    break;
                case R.id.arfp_fragment_comment:
                    dialogComment = new DialogComment();
                    dialogComment.show(getFragmentManager(), "miDialog");
                    dialogComment.setCancelable(true);
                    break;
                case R.id.arfp_fragment_sala:
                    if (CheckService.internet(context))
                        revisarDatosSupervisionSala();
                    else
                        Alert.ActivaInternet(context);
                    break;
                case R.id.arfp_btn_medios:
                    Intent intentMedios = new Intent(ReporteFolioPendiente.this, ListMediosFolios.class);
                    intentMedios.putExtra("folio",tvFolio.getText().toString());
                    startActivity(intentMedios);
                    break;
            }
        } else {
            Alert.ActivaInternet(context);
        }

    }

    private void alertFinalizarSuper() {

        AlertDialog.Builder alertTerminarSupervition = new AlertDialog.Builder(context);
        alertTerminarSupervition.setTitle("Termimar supervisión");
        alertTerminarSupervition.setMessage("¿Seguro que quieres  finalizar a la supervisión ?");
        alertTerminarSupervition.setPositiveButton(context.getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setActivdadTecnico();
            }
        });
        alertTerminarSupervition.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertTerminarSupervition.setIcon(R.drawable.warning);
        alertTerminarSupervition.create();
        alertTerminarSupervition.show();


    }

    // TODO: 13/12/2016  esta metodo hace la conexcion para abrir el fagmento de los datos de la sala
    public void revisarDatosSupervisionSala() {
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);
        final int[] value = new int[1];
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanResponse> call = serviceApi.REVISAR_DATOS_SUPERVISION_SALA(tvFolio.getText().toString(), salaid);
        call.enqueue(new Callback<BeanResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                pDialog.dismiss();
                try {
                    if (response.body().getValue() == 0) {
                        //no hay datos
                        List<ContactosSala> listaVacia = new ArrayList<ContactosSala>();
                        fragmentDatos(response.body().getValue(), listaVacia);
                    } else if (response.body().getValue() == 1) {

                        fragmentDatos(response.body().getValue(), response.body().getContactosSala());
                    }
                } catch (Exception e) {
                    Toast.makeText(context, getString(R.string.vuelve_a_intertarlo), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<BeanResponse> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(context, getString(R.string.vuelve_a_intertarlo), Toast.LENGTH_SHORT).show();
                Log.e("errorrrr", String.valueOf(t));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void fragmentDatos(int response, List<ContactosSala> contactosSalaList) {


        switch (response) {
            case 0:

                dialogDatoSala = new DialogDatoSala(response, contactosSalaList);
                dialogDatoSala.show(getFragmentManager(), "fragmentsala");
                dialogDatoSala.setCancelable(true);
                break;
            case 1:

                dialogDatoSala = new DialogDatoSala(response, contactosSalaList);
                dialogDatoSala.show(getFragmentManager(), "fragmentsala");
                dialogDatoSala.setCancelable(true);

                break;

        }

    }

    private void actualizaEstatus(final String estatus, final String estEnServ, final String officeID, final String folio) {
        metodo = "actualizaEstatus()";
        Cursor cursorEstTec = manager.cargarCursorEstatusTec();
        cursorEstTec.moveToFirst();
        String usuarioID = cursorEstTec.getString(CN_TECNICO_ID);
        try {
            JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPcambiaEstatusFirmaTec + "tecnicoidx=" + usuarioID + "&estatusx=" + estatus + "&estEnServx=" + estEnServ + "&officeidx=" + officeID + "&foliox=" + folio);
            assert object != null;
            JSONArray jsonArray = object.optJSONArray("estatus");
            jsonArrayChild = jsonArray.getJSONObject(0);
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (jsonArrayChild.optString("res").equals("1")) {
                                        Cursor cursorEstTec = manager.cargarCursorEstatusTec();
                                        cursorEstTec.moveToFirst();
                                        manager.actualizarEstatusTec(estatus, cursorEstTec.getString(CN_TECNICO_ID), cursorEstTec.getString(CN_REGION_ID), estEnServ, cursorEstTec.getString(6), cursorEstTec.getString(7), BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
                                        //Alert.Alerta(context, metodo, 0, getString(R.string.estActualizado));
                                        actualizaEstatusTecnico();
                                    }
                                }
                            });
                        }
                    }
            ).start();

        } catch (JSONException e) {
            MensajeEnConsola.log(context, metodo, "JSONException e = " + e.getMessage());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
        }
    }

    private void barraProgresoActualizaEst() {
        if (CheckService.internet(context)) {
            new ProgresoActualizaEst().execute();
        } else {
            Alert.ActivaInternet(context);
        }
    }

    private class ProgresoActualizaEst extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            pDialog2 = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        protected Void doInBackground(Void... params) {
            actualizaEstatus(estatusTecnico, estEnServ, BDVarGlo.getVarGlo(context, "INFO_USUARIO_OFFICE_ID"), BDVarGlo.getVarGlo(context, "INFO_USUARIO_FOLIO_ID"));
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog2.dismiss();
        }

    }

    boolean tipofalla(int tipo) {
        if (tipo == 131) {
            return true;
        }
        return false;
    }

    private void terminarFolio() {

        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanResponse> call = serviceApi.CERRAR_FOLIO_PENDIENTES(
                tvFolio.getText().toString(),
                BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
        call.enqueue(new Callback<BeanResponse>() {
            @Override
            public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                Log.e("url de cerrar folio", String.valueOf(call.request().url()));
                pDialog.dismiss();
                try {
                    if (response.body().getValue() == 1) {
                        Alert.Alerta(context, metodo, 0, response.body().getMessege());
                        finish();
                    } else {
                        Alert.Alerta(context, metodo, 0, response.body().getMessege());

                    }
                } catch (Exception e) {

                    Log.e("eeror", String.valueOf(e));
                }

            }

            @Override
            public void onFailure(Call<BeanResponse> call, Throwable t) {
                pDialog.dismiss();
            }
        });
    }


    private void pedirDatosReporteFolioPendiente() {
        metodo = "pedirDatosReporteFolioPendiente()";

        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanResponse> call = serviceApi.OBTENER_ACTIVIDADES(
                BDVarGlo.getVarGlo(context, "INFO_USUARIO_ALIAS"),
                BDVarGlo.getVarGlo(context, "INFO_USUARIO_PASSWORD"),
                BDVarGlo.getVarGlo(context, "INFO_USUARIO_FOLIO_ID"));
        call.enqueue(new Callback<BeanResponse>() {
            @Override
            public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                pDialog.dismiss();
                try {
                    Log.e("url de restrofit", String.valueOf(call.request().url()));
                    DatosFoliopend i = response.body().datosFoliopend.get(0);
                    tvFolio.setText(i.getFolio());
                    tvSala.setText(i.getSala());
                    tvFalla.setText(i.getFalla());
                    tvSubFalla.setText(i.getSubFalla());
                    tvLicencia.setText(i.getLicencia());
                    tvJuego.setText(i.getJuego());
                    arregloOpciones = i.getOpciones().split(",");
                    salaid = i.getSalaid();

                    try {
                        comentarios = i.getComentarios();
                        if (Integer.valueOf(i.getFallaid()) == 131) {
                            tvAtiende.setText("Inicio supervisión");
                            tvRepara.setText("Fin de supervisión");
                            fallaNum = Integer.valueOf(i.getFallaid());
                            btn_fragment_sala.setVisibility(View.VISIBLE);
                            tipofalla(131);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (Exception e) {

                }

                try {
                    if (arregloOpciones[0].equals("1"))
                        setEstatus23();
                    else
                        setEstatus23False();
                    if (arregloOpciones[1].equals("1")) {
                        tvTraslado.setEnabled(true);
                    } else {
                        tvTraslado.setTextColor(Color.GRAY);
                        tvTraslado.setEnabled(false);
                    }


                    if (arregloOpciones[2].equals("1")) {
                        TvAcceso.setEnabled(true);
                    } else {
                        TvAcceso.setTextColor(Color.GRAY);
                        TvAcceso.setEnabled(false);
                    }

                    if (arregloOpciones[3].equals("1")) {
                        tvAtiende.setEnabled(true);
                    } else {
                        tvAtiende.setTextColor(Color.GRAY);
                        tvAtiende.setEnabled(false);
                    }

                    if (arregloOpciones[4].equals("1")) {
                        tvEstatus.setEnabled(true);
                    } else {
                        tvEstatus.setTextColor(Color.GRAY);
                        tvEstatus.setEnabled(false);
                    }

                    if (arregloOpciones[5].equals("1")) {
                        tvRepara.setEnabled(true);
                    } else {
                        tvRepara.setTextColor(Color.GRAY);
                        tvRepara.setEnabled(false);
                    }
                    if (arregloOpciones[0].equals("0") &&
                            arregloOpciones[1].equals("0") &&
                            arregloOpciones[2].equals("0") &&
                            arregloOpciones[3].equals("0") &&
                            arregloOpciones[5].equals("0")) {
                        if (tipofalla(fallaNum)) {
                            terminarFolio();
                        }


                    }
                } catch (Exception e) {
                    MensajeEnConsola.log(context, metodo, "Exception e -> " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<BeanResponse> call, Throwable t) {
                Log.e("error", String.valueOf(t));
                pDialog.dismiss();
            }
        });

    }


    private void setActividadesTecnico(final String estatus, String respuesta, String solicitud, String usuarioID, final String estatusTec, final String estEnServ) {
        metodo = "setActividadesTecnico()";
        try {
            JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPactividadesTec +
                    "estatusx=" + estatus + "&respuestax=" + respuesta + "&solicitudx=" + solicitud +
                    "&tecnicoidx=" + usuarioID + "&estatusTecx=" + estatusTec + "&estEnServx=" + estEnServ);
            assert object != null;
            resp1 = object.getString("res");
            String officeid = object.getString("officeidx");
            manager.actualizarOfficeid(officeid);

            if (!officeid.equals("no")) {
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Cursor cursorEstTec = manager.cargarCursorEstatusTec();
                                        cursorEstTec.moveToFirst();
                                        manager.actualizarEstatusTec(estatusTec, cursorEstTec.getString(CN_TECNICO_ID), cursorEstTec.getString(CN_REGION_ID), estEnServ, cursorEstTec.getString(6), cursorEstTec.getString(7), BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
                                        //Alert.Alerta(context, metodo, 0, getString(R.string.estActualizado));
                                        actualizaEstatusTecnico();
                                        pedirDatosReporteFolioPendiente();
                                    }
                                });
                            }
                        }
                ).start();

            } else {
                Alert.Alerta(context, metodo, 1, getString(R.string.infoCambiaEstatus));
            }
        } catch (JSONException e) {
            MensajeEnConsola.log(context, metodo, "JSONException e = " + e.getMessage());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
        }
    }

    private void setEstatus23() {
        tvAceptarFolio.setVisibility(View.VISIBLE);
        tvRechazarFolio.setVisibility(View.VISIBLE);
        tvAceptarFolio.setTextColor(Color.MAGENTA);
        tvRechazarFolio.setTextColor(Color.MAGENTA);
        tvTraslado.setVisibility(View.GONE);
        TvAcceso.setVisibility(View.GONE);
        tvAtiende.setVisibility(View.GONE);
        tvEstatus.setVisibility(View.GONE);
        tvRepara.setVisibility(View.GONE);
    }

    private void setEstatus23False() {
        tvAceptarFolio.setVisibility(View.GONE);
        tvRechazarFolio.setVisibility(View.GONE);
        tvTraslado.setVisibility(View.VISIBLE);
        TvAcceso.setVisibility(View.VISIBLE);
        tvAtiende.setVisibility(View.VISIBLE);
        tvEstatus.setVisibility(View.VISIBLE);
        tvRepara.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onRestart() {
        super.onRestart();
        setupReporteFolioPendiente();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        actualizaEstatusTecnico();
    }


    private void barraProgresoSetActividadesTecnico() {
        Progreso2 progreso = new Progreso2();
        progreso.execute();
    }

    private void barraProgresoPedirDatosReporteFolioPendiente() {
        pedirDatosReporteFolioPendiente();
    }


    private class Progreso2 extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        @Override
        protected Void doInBackground(Void... params) {
            setActividadesTecnico(estatus, respuesta, BDVarGlo.getVarGlo(context, "INFO_USUARIO_FOLIO_ID"), BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"), estatusTecnico, estEnServ);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Alert.Alerta(context, metodo, 0, resp1);
            actualizaEstatusTecnico();
            pDialog.dismiss();
        }

    }

    private void setActivdadTecnico() {
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanResponse> call = serviceApi.ACTIVIDADES_TECNICO_FOLIO(
                estatus, respuesta, BDVarGlo.getVarGlo(context, "INFO_USUARIO_FOLIO_ID"), BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"), estatusTecnico, estEnServ);
        call.enqueue(new Callback<BeanResponse>() {
            @Override
            public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                pDialog.dismiss();
                pedirDatosReporteFolioPendiente();
                try {
                    manager.actualizarOfficeid(response.body().getOfficeidx());
                    Log.e("res", response.body().getRes());
                    Log.e("Officeidx", response.body().getOfficeidx());
                    if (!response.body().getOfficeidx().equals("no")) {
                        new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Cursor cursorEstTec = manager.cargarCursorEstatusTec();
                                                cursorEstTec.moveToFirst();
                                                manager.actualizarEstatusTec(estatusTecnico, cursorEstTec.getString(CN_TECNICO_ID), cursorEstTec.getString(CN_REGION_ID), estEnServ, cursorEstTec.getString(6), cursorEstTec.getString(7), BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
                                                //Alert.Alerta(context, metodo, 0, getString(R.string.estActualizado));
                                                actualizaEstatusTecnico();
                                            }
                                        });
                                    }
                                }
                        ).start();
                    } else {
                        Alert.Alerta(context, metodo, 1, getString(R.string.infoCambiaEstatus));
                    }

                    Alert.Alerta(context, metodo, 0, response.body().getRes());
                    actualizaEstatusTecnico();
                } catch (Exception e) {

                }


            }

            @Override
            public void onFailure(Call<BeanResponse> call, Throwable t) {
                Log.e("error", String.valueOf(t));
                pDialog.dismiss();

            }
        });
    }

    public void actualizaEstatusTecnico() {
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_est_tec);
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("39")) {
                if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("1")) {
                    item.setIcon(R.drawable.est_activo);
                } else if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("2")) {
                    item.setIcon(R.drawable.est_traslado_sala);
                } else if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("3")) {
                    item.setIcon(R.drawable.est_asignado);
                }
                if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("4")) {
                    item.setIcon(R.drawable.est_en_comida);
                }
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("40")) {
                item.setIcon(R.drawable.est_inactivo);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("109")) {
                item.setIcon(R.drawable.est_incidencia);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("79")) {
                item.setIcon(R.drawable.est_dia_descanso);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("80")) {
                item.setIcon(R.drawable.mix_peq);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("81")) {
                item.setIcon(R.drawable.est_vacaciones);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("89")) {
                item.setIcon(R.drawable.est_incapacidad);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reporte_folios_pendientes, menu);
        MenuItem item = menu.findItem(R.id.action_est_tec);
        Cursor cursorEstatusTec = manager.cargarCursorEstatusTec();
        cursorEstatusTec.moveToFirst();
        try {

            if (cursorEstatusTec.getString(1).equals("39")) {
                if (cursorEstatusTec.getString(4).equals("1")) {
                    item.setIcon(R.drawable.est_activo);
                } else if (cursorEstatusTec.getString(4).equals("2")) {
                    item.setIcon(R.drawable.est_traslado_sala);
                } else if (cursorEstatusTec.getString(4).equals("3")) {
                    item.setIcon(R.drawable.est_asignado);
                }
                if (cursorEstatusTec.getString(4).equals("4")) {
                    item.setIcon(R.drawable.est_en_comida);
                }
            }
            if (cursorEstatusTec.getString(1).equals("40")) {
                item.setIcon(R.drawable.est_inactivo);
            }
            if (cursorEstatusTec.getString(1).equals("109")) {
                item.setIcon(R.drawable.est_incidencia);
            }
            if (cursorEstatusTec.getString(1).equals("79")) {
                item.setIcon(R.drawable.est_dia_descanso);
            }
            if (cursorEstatusTec.getString(1).equals("80")) {
                item.setIcon(R.drawable.mix_peq);
            }
            if (cursorEstatusTec.getString(1).equals("81")) {
                item.setIcon(R.drawable.est_vacaciones);
            }
            if (cursorEstatusTec.getString(1).equals("89")) {
                item.setIcon(R.drawable.est_incapacidad);
            }

        } catch (Exception e) {
            Log.e("error inflar menu", String.valueOf(e));
        }

        mMenu = menu;
        actualizaEstatusTecnico();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_est_tec) {
            MenuInicialTecnico.iniciaMenuEstatusTec(context);
            return true;
        }
        if (id == R.id.action_medios) {

            Intent intentMedio = new Intent(ReporteFolioPendiente.this, SelectMediaActivity.class);
            intentMedio.putExtra("folio", tvFolio.getText().toString());
            intentMedio.putExtra("idusuario", BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
            intentMedio.putExtra("pais", Integer.valueOf(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION")));
            startActivityForResult(intentMedio, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


}
//635