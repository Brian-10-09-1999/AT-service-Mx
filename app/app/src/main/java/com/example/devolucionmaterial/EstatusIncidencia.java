package com.example.devolucionmaterial;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.MenuOpciones;

public class EstatusIncidencia extends Activity implements OnClickListener {
    String metodo;
    Context context;
    private String alias, password, solServId;
    private ProgressDialog pDialog;
    private MenuOpciones mo;
    private Menu mMenu;
    private BDmanager manager;
    private static String docPHPestatusTecnico = "estatusTecnicoHTML2.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estatus_incidencia);
        context = this;
        setupEstatusIncidencia();
    }

    private void setupEstatusIncidencia() {
        metodo = "setupEstatusIncidencia()";
        manager = new BDmanager(this);
        TextView tvNomUsuario = (TextView) findViewById(R.id.tvUsuarioestatus);
        mo = new MenuOpciones();
        ImageView imgEmail = (ImageView) findViewById(R.id.imgCorreoei);
        ImageView imgInfo = (ImageView) findViewById(R.id.imgInfoei);
        ImageView imgCallCC = (ImageView) findViewById(R.id.imgLlamarei);
        Button btnRefaccion = (Button) findViewById(R.id.btnRefaccion);
        Button btnCodigosdeError = (Button) findViewById(R.id.btnCodigosDeError);
        Button btnCodigosdeActivacion = (Button) findViewById(R.id.btnCodigosdeActivacion);
        Button btnSistemaDeCaja = (Button) findViewById(R.id.btnSistemaDeCaja);
        Button btnSolucionDeSala = (Button) findViewById(R.id.btnSoluciondeSala);

        btnRefaccion.setOnClickListener(this);
        btnCodigosdeError.setOnClickListener(this);
        btnCodigosdeActivacion.setOnClickListener(this);
        btnSistemaDeCaja.setOnClickListener(this);
        btnSolucionDeSala.setOnClickListener(this);
        imgCallCC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mo.llamarContactCenter(context);
            }
        });

        imgEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mo.enviarCorreo(context);
            }
        });

        imgInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mo.mostrarInfoApp(context);
            }
        });

        alias = BDVarGlo.getVarGlo(context, "INFO_USUARIO_ALIAS");
        password = BDVarGlo.getVarGlo(context, "INFO_USUARIO_PASSWORD");
        solServId = BDVarGlo.getVarGlo(context, "INFO_USUARIO_FOLIO_ID");
        tvNomUsuario.setText(BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRefaccion:
                barraProgresoPedirFoliosPendientes(1);
                break;
            case R.id.btnCodigosDeError:
                barraProgresoPedirFoliosPendientes(2);
                break;
            case R.id.btnCodigosdeActivacion:
                barraProgresoPedirFoliosPendientes(3);
                break;
            case R.id.btnSistemaDeCaja:
                barraProgresoPedirFoliosPendientes(4);
                break;
            case R.id.btnSoluciondeSala:
                barraProgresoPedirFoliosPendientes(5);
                break;
        }
    }

    private void barraProgresoPedirFoliosPendientes(int peticion) {
        if (CheckService.internet(context)) {
            new Progreso().execute(MainActivity.url + docPHPestatusTecnico +
                    "passx=" + password + "&respuestax=" + peticion + "&solicitudx=" + solServId +
                    "&aliasx=" + alias);
        } else {
            Alert.ActivaInternet(context);
        }
    }

    private class Progreso extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            metodo = "Progreso<AyncTask>.onPreExecute()";
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        @Override
        protected String doInBackground(String... params) {
            metodo = "Progreso<AyncTask>.doInBackground()";
            String regresa = "";
            try {
                JSONObject object = JSONparse.consultaURL(context, metodo, params[0]);
                assert object != null;
                regresa = object.getString("res");
            } catch (JSONException e) {
                MensajeEnConsola.log(context, metodo, "JSONException e = " + e.getMessage());
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
            }
            return regresa;
        }

        @Override
        protected void onPostExecute(String result) {
            metodo = "Progreso<AyncTask>.onPostExecute()";
            super.onPostExecute(result);
            pDialog.dismiss();
            if (result.equals("1"))
                Alert.Alerta(context, metodo, 0, getString(R.string.actRegistrada));
            else if (result.equals("2"))
                Alert.Alerta(context, metodo, 1, getString(R.string.actRegAnt));
            EstatusIncidencia.this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
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
        }

        mMenu = menu;
        actualizaEstatusTecnico();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_est_tec) {
            MenuInicialTecnico.iniciaMenuEstatusTec(EstatusIncidencia.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        actualizaEstatusTecnico();
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
}
//250