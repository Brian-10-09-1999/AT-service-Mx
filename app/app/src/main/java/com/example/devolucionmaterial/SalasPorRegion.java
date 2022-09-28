package com.example.devolucionmaterial;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.lists.SalaPorRegionArrayAdapter;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

public class SalasPorRegion extends Activity {
    String metodo;
    Context context;
    private ListView lvSalasPorRegion;
    private List<FolioPendiente> listaSalasPorRegion;
    private ArrayAdapter<FolioPendiente> adaptador;
    private ProgressDialog pDialog;
    private Menu mMenu;
    private BDmanager manager;
    private static String docPHPsalasRegionTec = "salasregionTec.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folios_pend);
        context = this;
        setupSalasPorRegion();
    }

    private void setupSalasPorRegion() {
        metodo = "setupSalasPorRegion()";
        manager = new BDmanager(this);
        TextView tvNomUsuario = (TextView) findViewById(R.id.tvNombreusuario2);
        lvSalasPorRegion = (ListView) findViewById(R.id.lvFoliosPendientes);
        LinearLayout llMenu = (LinearLayout) findViewById(R.id.llmenu);

        llMenu.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lvSalasPorRegion.setLayoutParams(params);
        lvSalasPorRegion.setOnItemClickListener(selectedItem);
        tvNomUsuario.setText(BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
        new Progreso().execute();
    }

    public OnItemClickListener selectedItem = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
            FolioPendiente item = listaSalasPorRegion.get(posicion);
            Intent intentMenuDevolucion = new Intent(context, FoliosPorSala.class);
            Bundle bndMyBundle = new Bundle();
            bndMyBundle.putString("tecnicoidx", (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID")));
            bndMyBundle.putString("salaIdx", (item.getSalaId()));
            bndMyBundle.putString("nombrex", BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
            intentMenuDevolucion.putExtras(bndMyBundle);
            startActivity(intentMenuDevolucion);
        }
    };

    private void pedirSalasPorRegion() {
        metodo = "pedirSalasPorRegion()";
        listaSalasPorRegion = new ArrayList<FolioPendiente>();
        try {
            JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPsalasRegionTec + "tecnicoidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
            assert object != null;
            JSONArray jsonArray = object.optJSONArray("salasRegion");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                listaSalasPorRegion.add(new FolioPendiente(
                        jsonArrayChild.optString("salaId"),
                        jsonArrayChild.optString("nomSala"),
                        jsonArrayChild.optString("numsFolio")));
            }
            adaptador = new SalaPorRegionArrayAdapter(this, listaSalasPorRegion);
            FolioPendiente item = listaSalasPorRegion.get(0);
            if (item.getNombreSala().equals("0")) {
                Alert.Error(context, metodo, getString(R.string.sinRegistros), getString(R.string.infoNotSalas));
                lvSalasPorRegion.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            MensajeEnConsola.log(context, metodo, "JSONException e = " + e.getMessage());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
        }
    }

    private class Progreso extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(SalasPorRegion.this);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        protected Void doInBackground(Void... params) {
            pedirSalasPorRegion();
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            lvSalasPorRegion.setAdapter(adaptador);
            pDialog.dismiss();
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
            Log.e("error menu", String.valueOf(e));
        }
        mMenu = menu;
        actualizaEstatusTecnico();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_est_tec) {
            MenuInicialTecnico.iniciaMenuEstatusTec(SalasPorRegion.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        String claseMetodo = "onResume()";
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
//225