package com.example.devolucionmaterial.fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.FolioPendiente;
import com.example.devolucionmaterial.lists.FolioPendienteArrayAdapter;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.ReporteFolioPendiente;
import com.example.devolucionmaterial.SalasPorRegion;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragFoliosPendientes extends Fragment{
    String metodo;
    Context context;
    View view;

    private ListView lvfoliosPendientes;
    private List<FolioPendiente> listaFoliospendientes;
    private ArrayAdapter<FolioPendiente> adaptador;
    private Bundle bndMyBundle;
    private EditText etNoFolio;
    private ProgressDialog  pDialog;
    private BDmanager manager;
    private static String docPHPgetFoliosPendientes="getFoliosPend.php?";
    private static String docPHPasignaFolio="asignaFolio.php?";
    public FragFoliosPendientes(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_folios_pend, container, false);
        context = view.getContext();
        setupFoliosPendientes();
        return view;
    }

    void setupFoliosPendientes(){
        //context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        manager = new BDmanager(context);
        manager.actualizarContadorNotif(0);
        Cursor cursorContador = manager.cargarCursorEstatusTec();
        cursorContador.moveToFirst();
        ShortcutBadger.applyCount(context, cursorContador.getInt(9));
        TextView tvNomUsuario = (TextView) view.findViewById(R.id.tvNombreusuario2);
        lvfoliosPendientes = (ListView) view.findViewById(R.id.lvFoliosPendientes);
        Button btnFoliosPorSala = (Button) view.findViewById(R.id.btnFoliosporSala);
        Button btnAsignar = (Button) view.findViewById(R.id.btnAsignar);
        etNoFolio = (EditText) view.findViewById(R.id.etNoFolio);
        lvfoliosPendientes.setOnItemClickListener(selectedItem);
        btnAsignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etNoFolio.getText().toString().equals("") || etNoFolio.length()==0)
                    Alert.Alerta(context, metodo, 1,getString(R.string.campoVacio));
                else
                    barraProgresoPostAsignarFolio();
            }
        });
        btnFoliosPorSala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SalasPorRegion.class));
            }
        });
        tvNomUsuario.setText(BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
        barraProgresoPedirFoliosPendientes();
    }
    public AdapterView.OnItemClickListener selectedItem = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
            FolioPendiente item = listaFoliospendientes.get(posicion);
            BDVarGlo.setVarGlo(context, "INFO_USUARIO_FOLIO_ID", item.getSolServId());
            startActivity(new Intent(context, ReporteFolioPendiente.class));
        }
    };
    private void barraProgresoPedirFoliosPendientes(){
        if(CheckService.internet(context)) {
            new Progreso().execute(MainActivity.url+docPHPgetFoliosPendientes+"aliasx="+BDVarGlo.getVarGlo(context, "INFO_USUARIO_ALIAS")+"&passwordx="+BDVarGlo.getVarGlo(context, "INFO_USUARIO_PASSWORD"));
        }else{
            Alert.ActivaInternet(context);
        }
    }
    private class Progreso extends AsyncTask<String, Void, String[][]> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }
        @Override
        protected String[][] doInBackground(String... params) {
            metodo = "Progreso<AsyncTask>.doInBackground()";
            String[][] regresa = null;
            try {
                JSONObject object = JSONparse.consultaURL(context, metodo, params[0]);
                assert object != null;
                JSONArray jsonArray = object.optJSONArray("salasPen");
                regresa = new String[jsonArray.length()][5];
                regresa[0][0] = String.valueOf(jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    regresa[i][1] = jsonArrayChild.optString("idFolio");
                    regresa[i][2] = jsonArrayChild.optString("nomSala");
                    regresa[i][3] = jsonArrayChild.optString("solSerId");
                    regresa[i][4] = jsonArrayChild.optString("estatusx");
                }
            } catch(JSONException e) {
                MensajeEnConsola.log(context, metodo, "JSONException e = "+e.getMessage());
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Exception e = "+e.getMessage());
            }
            return regresa;
        }
        @Override
        protected void onPostExecute(String[][] result) {
            super.onPostExecute(result);
            metodo = "Progreso<AsyncTask>.onPostExecute()";
            pDialog.dismiss();
            if(result != null) {
                listaFoliospendientes = new ArrayList<FolioPendiente>();
                try {
                    for (int i = 0; i < Integer.valueOf(result[0][0]); i++) {
                        listaFoliospendientes.add(new FolioPendiente(result[i][1], result[i][2], result[i][3], result[i][4]));
                    }
                } catch (Exception e) {
                    MensajeEnConsola.log(context, metodo, "Exception valor e = " + e.getMessage());
                }
                adaptador = new FolioPendienteArrayAdapter(context, listaFoliospendientes);
                lvfoliosPendientes.setAdapter(adaptador);
                pDialog.dismiss();
                FolioPendiente item = listaFoliospendientes.get(0);
                if (item.getIdFolio().equals("0")) {
                    Alert.Error(context, metodo, getString(R.string.sinRegistros), getString(R.string.sinIncidencias));
                    lvfoliosPendientes.setVisibility(View.GONE);
                }
            }else{
                Alert.Error(context, metodo, getString(R.string.sinRegistros), getString(R.string.sinIncidencias));
            }
        }
    }
    void barraProgresoPostAsignarFolio(){
        if(CheckService.internet(context)) {
            new ProgresoAsignarFolio().execute(MainActivity.url+docPHPasignaFolio+"foliox="+etNoFolio.getText().toString()+"&tecnicoidx="+BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
        }else{
            Alert.ActivaInternet(context);
        }
    }
    class ProgresoAsignarFolio extends AsyncTask<String, Void, String[]> {
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
            } catch(JSONException e) {
                MensajeEnConsola.log(context, metodo, "JSONException e = "+e.getMessage());
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Excetion e = "+e.getMessage());
            }
            return regresa;
        }
        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            metodo = "ProgresoAsignarFolio<AsyncTask>.onPostExecute()";
            listaFoliospendientes = new ArrayList<FolioPendiente>();
            if(result[0].equals("0")){
                if(result[1].equals("2")){
                    Alert.Alerta(context, metodo, 1, getString(R.string.infoAsigFol));
                }else{
                    Alert.Alerta(context, metodo, 0, getString(R.string.infoAsignado)+etNoFolio.getText().toString());
                    lvfoliosPendientes.setVisibility(View.VISIBLE);
                }
            }
            if(result[0].equals("1")){
                Alert.Alerta(context, metodo, 2, getString(R.string.infoFolioInex));
            }

            if(result[0].equals("2")){
                Alert.Alerta(context, metodo, 1, getString(R.string.incidencia)+etNoFolio.getText().toString()+getString(R.string.atendida));
            }

            if(result[0].equals("12")){
                Alert.Alerta(context, metodo, 1, getString(R.string.incidencia)+etNoFolio.getText().toString()+getString(R.string.atendida));
            }
            etNoFolio.getText().clear();
            pDialog.dismiss();
            barraProgresoPedirFoliosPendientes();

            lvfoliosPendientes.setAdapter(adaptador);
        }
    }
}
//350