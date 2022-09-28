package com.example.devolucionmaterial.activitys.foliosPendientesSeccion;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.FolioPendiente;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.activitys.viaticos.Activity_Lista;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.lists.SalaPorRegionArrayAdapter;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.thanosfisherman.mayi.PermissionBean;

public class SalasPorRegion extends BaseActivity {
    String metodo;
    Context context;
    private ListView lvSalasPorRegion;
    private List<FolioPendiente> listaSalasPorRegion;
    private ArrayAdapter<FolioPendiente> adaptador;
    private Menu mMenu;
    private BDmanager manager;
    private static String docPHPsalasRegionTec = "salasregionTec.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folios_pend);
        initToolbar("Salas", true, true);
        context = this;
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        } else {
            initSetUp();
        }

    }

    @Override
    public String[] permissions() {
        return new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CALL_PHONE,
                Manifest.permission.GET_ACCOUNTS
        };
    }

    @Override
    protected void permissionResultMulti(PermissionBean[] permissions) {
        Boolean val = true;
        //Toast.makeText(PermissionActivity.this, "MULTI PERMISSION RESULT " + Arrays.deepToString(permissions), Toast.LENGTH_LONG).show();
        for (int i = 0; i < permissions.length; i++) {
            if (!permissions[i].isGranted()) {
                val = false;
            }
        }
        if (val) {
            initSetUp();
        } else {
            showSnackBarPermission();
        }

    }

    private void initSetUp() {
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
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
           /* pDialog= new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.Conectando_con_servidor_remoto))
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();*/
            pDialog = new ProgressDialog(SalasPorRegion.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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




    public void onResume() {
        super.onResume();
        actualizaEstatusTecnico();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
     /*   if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }*/

        actualizaEstatusTecnico();
    }
}
//225