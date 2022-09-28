package com.example.devolucionmaterial;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.lists.FolioPendienteArrayAdapter;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;

public class FoliosPendientes extends Activity implements OnClickListener {
    String metodo;
    Context context;
    private ListView lvfoliosPendientes;
    private ArrayList<FolioPendiente> listaFoliospendientes = new ArrayList<FolioPendiente>();
    ;
    private ArrayAdapter<FolioPendiente> adaptador;
    private EditText etNoFolio;
    private ProgressDialog pDialog;
    private Menu mMenu;
    private BDmanager manager;
    private static String docPHPgetFoliosPendientes = "getFoliosPend.php?";
    private static String docPHPasignaFolio = "asignaFolio.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folios_pend);
        context = this;
        setupFoliosPendientes();
    }

    private void setupFoliosPendientes() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        manager = new BDmanager(this);
        manager.actualizarContadorNotif(0);
        Cursor cursorContador = manager.cargarCursorEstatusTec();
        cursorContador.moveToFirst();
        ShortcutBadger.applyCount(context, cursorContador.getInt(9));
        TextView tvNomUsuario = (TextView) findViewById(R.id.tvNombreusuario2);
        lvfoliosPendientes = (ListView) findViewById(R.id.lvFoliosPendientes);
        Button btnFoliosPorSala = (Button) findViewById(R.id.btnFoliosporSala);
        Button btnAsignar = (Button) findViewById(R.id.btnAsignar);
        etNoFolio = (EditText) findViewById(R.id.etNoFolio);
        lvfoliosPendientes.setOnItemClickListener(selectedItem);

        btnAsignar.setOnClickListener(this);
        btnFoliosPorSala.setOnClickListener(this);
        tvNomUsuario.setText(BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
        barraProgresoPedirFoliosPendientes();

    }

    public OnItemClickListener selectedItem = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
            FolioPendiente item = listaFoliospendientes.get(posicion);
            BDVarGlo.setVarGlo(context, "INFO_USUARIO_FOLIO_ID", item.getSolServId());
            startActivity(new Intent(context, ReporteFolioPendiente.class));
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAsignar:
                if (etNoFolio.getText().toString().equals("") || etNoFolio.length() == 0)
                    Alert.Alerta(context, metodo, 1, getString(R.string.campoVacio));
                else
                    barraProgresoPostAsignarFolio();
                break;
            case R.id.btnFoliosporSala:
                startActivity(new Intent(context, SalasPorRegion.class));
                break;
        }
    }

    private void barraProgresoPedirFoliosPendientes() {
        if (CheckService.internet(context)) {
            new Progreso().execute(MainActivity.url + docPHPgetFoliosPendientes + "aliasx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ALIAS") + "&passwordx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_PASSWORD"));
        } else {
            Alert.ActivaInternet(context);
        }
    }

    private class Progreso extends AsyncTask<String, Void, ArrayList<FolioPendiente>> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        @Override
        protected ArrayList<FolioPendiente> doInBackground(String... params) {
            metodo = "Progreso<AsyncTask>.doInBackground()";
            listaFoliospendientes.clear();

            try {
                JSONObject object = JSONparse.consultaURL(context, metodo, params[0]);
                assert object != null;
                JSONArray jsonArray = object.optJSONArray("salasPen");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    listaFoliospendientes.add(new FolioPendiente(jsonArrayChild.getString("idFolio"),
                            jsonArrayChild.getString("nomSala"),
                            jsonArrayChild.getString("solSerId"),
                            jsonArrayChild.getString("estatusx"),
                            jsonArrayChild.getString("falla")));
                }
            } catch (JSONException e) {
                MensajeEnConsola.log(context, metodo, "JSONException e = " + e.getMessage());
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
            }
            return listaFoliospendientes;
        }

        @Override
        protected void onPostExecute(ArrayList<FolioPendiente> result) {
            super.onPostExecute(result);
            metodo = "Progreso<AsyncTask>.onPostExecute()";
            pDialog.dismiss();
            if (result != null) {
                try {
                    adaptador = new FolioPendienteArrayAdapter(context, result);
                    lvfoliosPendientes.setAdapter(adaptador);
                    FolioPendiente item = listaFoliospendientes.get(0);
                    if (item.getIdFolio().equals("0")) {
                        Alert.Error(context, metodo, getString(R.string.sinRegistros), getString(R.string.sinIncidencias));
                        lvfoliosPendientes.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    MensajeEnConsola.log(context, metodo, "Exception valor e = " + e.getMessage());
                    Alert.Error(context, metodo, getString(R.string.sinRegistros), getString(R.string.sinIncidencias));
                }


            } else {
                Alert.Error(context, metodo, getString(R.string.sinRegistros), getString(R.string.sinIncidencias));
            }
        }
    }

    private void barraProgresoPostAsignarFolio() {
        if (CheckService.internet(context)) {
            new ProgresoAsignarFolio().execute(MainActivity.url + docPHPasignaFolio + "foliox=" + etNoFolio.getText().toString() + "&tecnicoidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
        } else {
            Alert.ActivaInternet(context);
        }
    }

    private class ProgresoAsignarFolio extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        @Override
        protected String[] doInBackground(String... params) {
            metodo = "ProgresoAsignarFolio<AsyncTask>.doInBackground()";
            String[] regresa = new String[2];
            try {
                JSONObject object = JSONparse.consultaURL(context, metodo, params[0]);
                assert object != null;
                regresa = object.getString("res").split("!");
            } catch (JSONException e) {
                MensajeEnConsola.log(context, metodo, "JSONException e = " + e.getMessage());
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
            }
            return regresa;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            metodo = "ProgresoAsignarFolio<AsyncTask>.onPostExecute()";
            listaFoliospendientes = new ArrayList<FolioPendiente>();
            if (result[0].equals("0")) {
                if (result[1].equals("2")) {
                    Alert.Alerta(context, metodo, 1, getString(R.string.infoAsigFol));
                } else {
                    Alert.Alerta(context, metodo, 0, getString(R.string.infoAsignado) + etNoFolio.getText().toString());
                    lvfoliosPendientes.setVisibility(View.VISIBLE);
                }
            }
            if (result[0].equals("1")) {
                Alert.Alerta(context, metodo, 2, getString(R.string.infoFolioInex));
            }

            if (result[0].equals("2") || result[0].equals("12")) {
                Alert.Alerta(context, metodo, 1, getString(R.string.incidencia) + etNoFolio.getText().toString() + getString(R.string.atendida));
            }
            etNoFolio.getText().clear();
            pDialog.dismiss();
            barraProgresoPedirFoliosPendientes();

            lvfoliosPendientes.setAdapter(adaptador);
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        actualizaEstatusTecnico();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_est_tec);
        Cursor cursorEstatusTec = manager.cargarCursorEstatusTec();
        cursorEstatusTec.moveToFirst();
        try{

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

        }catch (Exception e){
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

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        actualizaEstatusTecnico();
    }

    public void onRestart() {
        super.onRestart();
        setupFoliosPendientes();
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
//364