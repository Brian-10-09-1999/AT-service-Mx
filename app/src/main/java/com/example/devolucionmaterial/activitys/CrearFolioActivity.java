package com.example.devolucionmaterial.activitys;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.api.ServiceApi;

import com.example.devolucionmaterial.beans.FallaCrearFolio;
import com.example.devolucionmaterial.beans.LicenciaPorSala;
import com.example.devolucionmaterial.beans.Licencium;

import com.example.devolucionmaterial.beans.createFolio;
import com.example.devolucionmaterial.customview.AutoscaleEditText;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.lists.ListViewItem;
import com.example.devolucionmaterial.sharedpreferences.PreferencesVar;
import com.jaredrummler.materialspinner.MaterialSpinner;


import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

import static com.example.devolucionmaterial.sharedpreferences.PreferencesVar.context;

public class CrearFolioActivity extends AppCompatActivity implements
        MaterialSpinner.OnItemSelectedListener, Callback<List<LicenciaPorSala>>, View.OnClickListener {

    private MaterialDialog materialDialog;

    private Button btnCreate;

    private MaterialSpinner spOrigenLlamada;
    private MaterialSpinner spMedioContacto;
    private MaterialSpinner spMotivo;
    private MaterialSpinner spRegion;
    private MaterialSpinner spSala;
    private MaterialSpinner spLicencia;

    private final String[] arrayOrigien = {"Cliente", "Comercial", "técnico", "gerente de cuenta", "AnálisisZLab"};
    private final String[] arrayMedioContacto = {"Teléfono", "email", "chat", " Portal Bos", "Otro"};
    private final String[] arrayMotivo = {"Solicitud Servicio", "solicitud Información", "Supervisión"};

    List<String> arrSalas;
    List<Integer> arrSalasID;

    List<Licencium> licenciumList;
    List<String> licencias;

    private int RegionId;
    private BDmanager manager;

    /****
     * varaibles datos de solicitud
     *
     *
     * */


    private String llamada;
    private String medioContacto;
    private String motivo;
    private String region;
    private String sala;
    private String nombreSala;
    private int salaID;


    private String licencia;
    private String ip;
    private String juego;
    private String version;
    private String serie;
    private String gabinete;
    private String modelo;


    private String falla;
    private String subfalla;
    private String noMaquina;
    private String reclamdo;
    private String validado;
    private String maquinaInoperativa;
    private String comentario;
    /****
     * datos maquina
     * */
    private AutoscaleEditText etIp;
    private AutoscaleEditText etJuego;
    private AutoscaleEditText etVersion;
    private AutoscaleEditText etGabinete;
    private AutoscaleEditText etModelo;
    private AutoscaleEditText etSerie;


    private AutoscaleEditText etNoMaquina;
    private AutoscaleEditText etReclamdo;
    private AutoscaleEditText etValio;
    private AutoscaleEditText etComentario;

    private CheckBox ckMaquinaIno;

    /***
     *
     *
     * */
    private MaterialSpinner spFalla;
    private MaterialSpinner spSubFalla;

    ArrayList<String> listFallasNombres;
    List<FallaCrearFolio> listFallasId;

    List<String> subFallaListaNombre;
    List<FallaCrearFolio> subFallaLista;

    // TODO: 31/05/2017 es para saber si tiene licencia o no
    private boolean sinLicenia = false;

    private int fallaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_folio);
        initToolbar();
        initSetUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Crear  Folio");
        }
    }

    void initSetUp() {
        manager = new BDmanager(this);
        RegionId = PreferencesVar.getIdRegion();


        spOrigenLlamada = (MaterialSpinner) findViewById(R.id.acf_ms_origen_llamada);
        spMedioContacto = (MaterialSpinner) findViewById(R.id.acf_ms_medio_contacto);
        spMotivo = (MaterialSpinner) findViewById(R.id.acf_ms_motivo);
        spRegion = (MaterialSpinner) findViewById(R.id.acf_ms_region);
        spSala = (MaterialSpinner) findViewById(R.id.acf_ms_sala);
        spLicencia = (MaterialSpinner) findViewById(R.id.acf_ms_licencia);


        etIp = (AutoscaleEditText) findViewById(R.id.acf_ed_ip);
        etJuego = (AutoscaleEditText) findViewById(R.id.acf_ed_juego);
        etVersion = (AutoscaleEditText) findViewById(R.id.acf_ed_version);
        etGabinete = (AutoscaleEditText) findViewById(R.id.acf_ed_gabinete);
        etModelo = (AutoscaleEditText) findViewById(R.id.acf_ed_modelo);
        etSerie = (AutoscaleEditText) findViewById(R.id.acf_ed_serie);


        etNoMaquina = (AutoscaleEditText) findViewById(R.id.acf_ed_nom_maquina);
        etReclamdo = (AutoscaleEditText) findViewById(R.id.acf_ed_reclamado);
        etValio = (AutoscaleEditText) findViewById(R.id.acf_ed_validado);


        etComentario = (AutoscaleEditText) findViewById(R.id.acf_ae_comentarios);

        spFalla = (MaterialSpinner) findViewById(R.id.acf_ms_fallas);
        spSubFalla = (MaterialSpinner) findViewById(R.id.acf_ms_subfalla);

        ckMaquinaIno = (CheckBox) findViewById(R.id.acf_ck_no_maquina);


        btnCreate = (Button) findViewById(R.id.acf_btn_create);


        spOrigenLlamada.setItems(arrayOrigien);
        spMedioContacto.setItems(arrayMedioContacto);
        spMotivo.setItems(arrayMotivo);
        spRegion.setItems(listRegionesNombres());


        spRegion.setOnItemSelectedListener(this);
        spSala.setOnItemSelectedListener(this);
        spLicencia.setOnItemSelectedListener(this);
        btnCreate.setOnClickListener(this);


        llamada = arrayOrigien[spOrigenLlamada.getSelectedIndex()];
        medioContacto = arrayMedioContacto[spMedioContacto.getSelectedIndex()];
        motivo = arrayMotivo[spMotivo.getSelectedIndex()];


        llenaComboSala(listRegiones().get(spRegion.getSelectedIndex()).getId());
        // TODO: 04/05/2017 se inicia el spiner de salas en 0
        nombreSala = arrSalas.get(spSala.getSelectedIndex());
        salaID = arrSalasID.get(spSala.getSelectedIndex());


        materialDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.Conectando_con_servidor_remoto))
                .content("Cargando...")
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(false)
                .show();

        // TODO: 05/05/2017 se obtiene las fallas y sus ids
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<List<FallaCrearFolio>> call = serviceApi.getFallasMaquinas();
        call.enqueue(new Callback<List<FallaCrearFolio>>() {
            @Override
            public void onResponse(Call<List<FallaCrearFolio>> call, Response<List<FallaCrearFolio>> response) {
                materialDialog.dismiss();
                listFallasId = new ArrayList<>();
                listFallasNombres = new ArrayList<String>();

                //listFallasNombres.add("Seleccione una falla");
                //listFallasId.add(new FallaCrearFolio("", ""));

                try {
                    listFallasId.addAll(response.body());
                    for (int i = 0; i < listFallasId.size(); i++) {
                        FallaCrearFolio fcf = listFallasId.get(i);
                        listFallasNombres.add(fcf.getFalla());
                    }
                    spFalla.setItems(listFallasNombres);
                    falla = listFallasId.get(0).getFallaid();
                    Log.e("listFallasNombres", String.valueOf(listFallasNombres.size()));
                    Log.e("listFallasId", String.valueOf(listFallasId.size()));

                } catch (Exception e) {
                    errorConectar();
                    Log.e("error", String.valueOf(e));
                }

            }

            @Override
            public void onFailure(Call<List<FallaCrearFolio>> call, Throwable t) {
                materialDialog.dismiss();
                Log.e("error al conectar ", String.valueOf(t));
                errorConectar();
            }
        });
        spinnerDatosSolicitud();
        spinnerDatosIncidencia();
    }

    private void errorConectar() {
        new MaterialDialog.Builder(this)
                .content("Ocurrio un error al conectar")
                .iconRes(R.drawable.warning)
                .positiveText(R.string.aceptar)
                //.negativeText(R.string.disagree)
                .show();
    }

    private void spinnerDatosSolicitud() {
        // TODO: 04/05/2017 spinser de llamada
        spOrigenLlamada.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                llamada = item;
                Log.e("llamada", llamada);

            }
        });
        spOrigenLlamada.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override
            public void onNothingSelected(MaterialSpinner spinner) {

            }
        });
        // TODO: 04/05/2017 spinner de mediso de contactos
        spMedioContacto.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                medioContacto = item;
                Log.e("medioContacto", medioContacto);
            }
        });
        spMedioContacto.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override
            public void onNothingSelected(MaterialSpinner spinner) {

            }
        });
        // TODO: 04/05/2017 spinner de motivo
        spMotivo.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                motivo = item;
                Log.e("motivo", motivo);
            }
        });
        spMotivo.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override
            public void onNothingSelected(MaterialSpinner spinner) {

            }
        });
    }

    private void spinnerDatosIncidencia() {
        // TODO: 04/05/2017 spinser de falla


        spFalla.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                falla = String.valueOf(listFallasId.get(position).getFallaid());
                subfalla = null;

                ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
                Log.e("id", listFallasId.get(position).getFallaid() + "");
                if (position != 0) {
                    materialDialog = new MaterialDialog.Builder(CrearFolioActivity.this)
                            .title(getString(R.string.Conectando_con_servidor_remoto))
                            .content("Cargando...")
                            .progress(true, 0)
                            .cancelable(false)
                            .progressIndeterminateStyle(false)
                            .show();
                    Call<List<FallaCrearFolio>> call = serviceApi.setFallaId(listFallasId.get(position).getFallaid());
                    call.enqueue(new Callback<List<FallaCrearFolio>>() {
                        @Override
                        public void onResponse(Call<List<FallaCrearFolio>> call, Response<List<FallaCrearFolio>> response) {
                            materialDialog.dismiss();
                            subFallaLista = new ArrayList<FallaCrearFolio>();
                            subFallaListaNombre = new ArrayList<String>();
                            try {
                                Log.e("tamaño", String.valueOf(response.body().size()));
                                subFallaLista.addAll(response.body());
                                if (response.body().isEmpty()) {
                                    spSubFalla.setItems("");
                                }

                                for (int i = 0; i < response.body().size(); i++) {
                                    // subFallaLista.add(new FallaCrearFolio(response.body().get(i).getFallaid()+"",response.body().get(i).getFalla()) );
                                    subFallaListaNombre.add(response.body().get(i).getFalla());
                                }

                                spSubFalla.setItems(subFallaListaNombre);

                                subfalla = subFallaLista.get(0).getFallaid();

                            } catch (Exception e) {
                                Log.e("error", e + "");
                                subfalla = null;
                                spSubFalla.setItems("");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<FallaCrearFolio>> call, Throwable t) {
                            materialDialog.dismiss();
                            errorConectar();
                        }
                    });
                } else {
                    spSubFalla.setItems("");
                }


            }
        });
        // TODO: 04/05/2017 spinser de subfalla
        spSubFalla.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if (subFallaLista != null) {
                    subfalla = subFallaLista.get(position).getFallaid();
                } else {
                    subfalla = null;
                }

                Log.e("subfalla", subFallaLista.get(position).getFallaid());
                Log.e("subfalla", subFallaLista.get(position).getFalla());

            }
        });
        spSubFalla.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override
            public void onNothingSelected(MaterialSpinner spinner) {

            }
        });
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

        switch (view.getId()) {
            case R.id.acf_ms_region:
                llenaComboSala(listRegiones().get(position).getId());
                break;

            case R.id.acf_ms_sala:
                nombreSala = arrSalas.get(position);
                Log.e("nombreSala", nombreSala);
                salaID = arrSalasID.get(spSala.getSelectedIndex());
                Log.e("idSala", String.valueOf(arrSalasID.get(spSala.getSelectedIndex())));

                if (salaID != 0) {
                    materialDialog = new MaterialDialog.Builder(this)
                            .title(getString(R.string.Conectando_con_servidor_remoto))
                            .content("Cargando...")
                            .progress(true, 0)
                            .cancelable(false)
                            .progressIndeterminateStyle(false)
                            .show();
                    RequestBody salaId = RequestBody.create(MediaType.parse("text/plain"), salaID + "");
                    ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
                    Call<List<LicenciaPorSala>> call = serviceApi.getLicenciasPorSala(salaId);
                    call.enqueue(this);
                } else {
                    spLicencia.setItems("");
                    sinLicenia = false;
                    licencia = null;
                    clearEditTex();
                }
                break;

            case R.id.acf_ms_licencia:
                if (licenciumList != null) {

                    licencia = licencias.get(position);

                    Licencium l = licenciumList.get(position);
                    etIp.setText(l.getIp());
                    etJuego.setText(l.getJuego());
                    etVersion.setText(l.getVersion());
                    etGabinete.setText(l.getMueble());
                    etModelo.setText(l.getModelo());
                    etSerie.setText(l.getSerie());
                }
                break;
        }

    }

    // TODO: 04/05/2017 llena el spinner de las dependiendo del spiner de regiones
    private void llenaComboSala(int regionID) {
        Log.e("region", String.valueOf(regionID));
        arrSalas = new ArrayList<>();
        arrSalasID = new ArrayList<>();
        arrSalas.add("Seleccione una sala");
        arrSalasID.add(0);
        Cursor fila = manager.consulta("select salaid , nombre from csala where regionidfk= '" + regionID + "' ORDER BY nombre");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
            arrSalasID.add(fila.getInt(0));
            arrSalas.add(fila.getString(1));

        }
        fila.close();
        spSala.setItems(arrSalas);


    }


    private List<ListViewItem> listRegiones() {
        List<ListViewItem> data = new ArrayList<>();
        Cursor fila = manager.consulta("select id, nombre from cregion ORDER BY nombre");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
            data.add(new ListViewItem(fila.getInt(0), fila.getString(1)));
        }
        return data;
    }

    // TODO: 04/05/2017 regresa solo el nombre las regiones
    private List<String> listRegionesNombres() {
        List<String> data = new ArrayList<>();
        Cursor fila = manager.consulta("select id, nombre from cregion ORDER BY nombre");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
            data.add(fila.getString(1));
        }
        return data;
    }

    // TODO: 05/05/2017 limpia las cajas de testo de datos maquina
    private void clearEditTex() {
        etIp.setText("");
        etJuego.setText("");
        etVersion.setText("");
        etGabinete.setText("");
        etModelo.setText("");
        etSerie.setText("");

        ip = null;
        juego = null;
        version = null;
        gabinete = null;
        modelo = null;
        serie = null;
    }

    @Override
    public void onResponse(Call<List<LicenciaPorSala>> call, Response<List<LicenciaPorSala>> response) {
        clearEditTex();
        materialDialog.dismiss();
        licenciumList = new ArrayList<>();
        licencias = new ArrayList<>();
        try {
            for (int i = 0; i < response.body().size(); i++) {
                licenciumList.addAll(response.body().get(i).getLicencia());
            }
            for (int i = 0; i < licenciumList.size(); i++) {
                licencias.add(licenciumList.get(i).getLicencia());
            }
            spLicencia.setItems(licencias);

            Licencium l = licenciumList.get(spSala.getSelectedIndex());
            etIp.setText(l.getIp());
            etJuego.setText(l.getJuego());
            etVersion.setText(l.getVersion());
            etGabinete.setText(l.getMueble());
            etModelo.setText(l.getModelo());
            etSerie.setText(l.getSerie());


            licencia = licencias.get(0);
        } catch (Exception e) {
            Log.e("error", String.valueOf(e));
            sinLicenia = true;
            licencia = null;
            spLicencia.setItems("");
            new MaterialDialog.Builder(this)
                    .content("No tiene licencias registradas")
                    .iconRes(R.drawable.warning)
                    .positiveText(R.string.aceptar)
                    //.negativeText(R.string.disagree)
                    .show();
        }

    }

    @Override
    public void onFailure(Call<List<LicenciaPorSala>> call, Throwable t) {
        materialDialog.dismiss();
        Log.e("error al conectar", String.valueOf(t));
        errorConectar();
    }

    @Override
    public void onClick(View v) {
        // TODO: 29/05/2017 validacion de datos de la solicitud
        if (validationSala()) {
            // TODO: 29/05/2017  de datod de maquina

            if (validationLicencia()) {
                ip = etIp.getText().toString();
                juego = etJuego.getText().toString();
                version = etVersion.getText().toString();
                gabinete = etGabinete.toString();
                modelo = etModelo.getText().toString();
                serie = etSerie.getText().toString();

                if (validationFalla()) {

                    validationComentario();

                } else {
                    // datos de incidencia
                    new MaterialDialog.Builder(this)
                            .content("Faltan datos en el apartado Datos de incidencia ")
                            .iconRes(R.drawable.warning)
                            .positiveText(R.string.aceptar)
                            //.negativeText(R.string.disagree)
                            .show();

                }
            } else {
                // TODO: 31/05/2017 si la sala no tiene licencuas puede saltar el filtro
                if (sinLicenia) {
                    validationComentario();
                } else {
                    new MaterialDialog.Builder(this)
                            .content("Faltan datos en el apartado Datos de maquinas ")
                            .iconRes(R.drawable.warning)
                            .positiveText(R.string.aceptar)
                            //.negativeText(R.string.disagree)
                            .show();
                }

            }

        } else {
            //datos de la maquina
            new MaterialDialog.Builder(this)
                    .content("Faltan datos en el apartado Datos de la solicitud")
                    .iconRes(R.drawable.warning)
                    .positiveText(R.string.aceptar)
                    //.negativeText(R.string.disagree)
                    .show();
        }


    }

    public void validationComentario() {
        if (!etComentario.getText().toString().trim().isEmpty()) {
            sendFolio();
        } else {
            //comenatarui
            new MaterialDialog.Builder(this)
                    .content("Faltan los comentarios ")
                    .iconRes(R.drawable.warning)
                    .positiveText(R.string.aceptar)
                    //.negativeText(R.string.disagree)
                    .show();
        }
    }

    private Boolean validationSala() {
        if (salaID != 0) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean validationLicencia() {
        if (licencia != null) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean validationFalla() {
        if (spFalla.getSelectedIndex() != 0) {
            Log.e("licencia", " no cero");
            return true;
        } else {
            Log.e("licencia", " si cero");
            return false;
        }
    }


    void sendFolio() {

        materialDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.Conectando_con_servidor_remoto))
                .content("Cargando...")
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(false)
                .show();

        if (ckMaquinaIno.isChecked()) {
            maquinaInoperativa = "1";
        } else {
            maquinaInoperativa = "0";
        }

        if (subfalla == null) {
            subfalla = "NULL";
        }


        noMaquina = etNoMaquina.getText().toString();
        reclamdo = etReclamdo.getText().toString();
        validado = etValio.getText().toString();
        comentario = etComentario.getText().toString();

        if (noMaquina.trim().isEmpty()) {
            noMaquina = "NULL";
        }

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<createFolio> call = serviceApi.createFolio(
                llamada,
                medioContacto,
                motivo,
                region,
                salaID + "",
                licencia,
                ip,
                juego,
                version,
                serie,
                gabinete,
                modelo,
                falla,
                subfalla,
                noMaquina,
                reclamdo,
                validado,
                maquinaInoperativa,
                comentario,
                BDVarGlo.getVarGlo(CrearFolioActivity.this, "INFO_USUARIO_ID")

        );

        call.enqueue(new Callback<createFolio>() {
            @Override
            public void onResponse(Call<createFolio> call, Response<createFolio> response) {
                materialDialog.dismiss();
                try {
                    if (response.body().getEstatus() == 1) {
                        Log.e("foloio", response.body().getFolio());
                        String folio = "Se a creado el folio " + response.body().getFolio();
                        new MaterialDialog.Builder(CrearFolioActivity.this)
                                .content(folio)
                                .iconRes(R.drawable.warning)
                                .positiveText(R.string.aceptar)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent i = new Intent(CrearFolioActivity.this, CrearFolioActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                })
                                //.negativeText(R.string.disagree)
                                .show();
                    } else {
                        new MaterialDialog.Builder(CrearFolioActivity.this)
                                .content("Ocurrio un error al procesar solicitud")
                                .iconRes(R.drawable.warning)
                                .positiveText(R.string.aceptar)
                                //.negativeText(R.string.disagree)
                                .show();
                    }

                } catch (Exception e) {
                    Log.e("error", String.valueOf(e));
                    new MaterialDialog.Builder(CrearFolioActivity.this)
                            .content("Ocurrio un error al procesar solicitud")
                            .iconRes(R.drawable.warning)
                            .positiveText(R.string.aceptar)
                            //.negativeText(R.string.disagree)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<createFolio> call, Throwable t) {
                materialDialog.dismiss();
                Log.e("error", String.valueOf(t));
                new MaterialDialog.Builder(CrearFolioActivity.this)
                        .content("Ocurrio un error al conectar")
                        .iconRes(R.drawable.warning)
                        .positiveText(R.string.aceptar)
                        //.negativeText(R.string.disagree)
                        .show();
            }
        });
    }


}
