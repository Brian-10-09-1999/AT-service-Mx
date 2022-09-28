package com.example.devolucionmaterial;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.lists.FolioPendienteArrayAdapter;
import com.example.devolucionmaterial.activitys.pedidosSeccion.ListaDePiezasDePedido;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.ServerErrors;
import com.thanosfisherman.mayi.PermissionBean;

public class PedidosDeIncidencia extends BaseActivity {
    String metodo;
    private LinearLayout llMenu;
    private ListView lvPedidosDeIncidencia;
    private Bundle bndReceptor;
    private Intent intReceptor;
    private String alias, password, usuarioid, resp, solSerId, jsonResult;
    private List<FolioPendiente> listaPedidosDeIncidencia;
    private ArrayAdapter<FolioPendiente> adaptador;
    private Bundle bndMyBundle;

    private Menu mMenu;
    private BDmanager manager;
    private int codigoHTTP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folios_pend);
        initToolbar("Pedidos de Incidencia", true, true);
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
                android.Manifest.permission.GET_ACCOUNTS
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

    @Override
    protected void onRestart() {
        super.onRestart();
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }*/

    }

    private void initSetUp() {
        manager = new BDmanager(this);
        lvPedidosDeIncidencia = (ListView) findViewById(R.id.lvFoliosPendientes);
        llMenu = (LinearLayout) findViewById(R.id.llmenu);
        lvPedidosDeIncidencia.setOnItemClickListener(selectedItem);
        intReceptor = this.getIntent();
        bndReceptor = intReceptor.getExtras();
        alias = bndReceptor.getString("aliasx");
        password = bndReceptor.getString("passwordx");
        usuarioid = bndReceptor.getString("usuarioidx");
        solSerId = bndReceptor.getString("solSerIdx");

        llMenu.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lvPedidosDeIncidencia.setLayoutParams(params);
        barraProgresoPedirPedidos();
    }

    public OnItemClickListener selectedItem = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
            metodo = "selectedItem.OnItemClickListener()";
            FolioPendiente item = listaPedidosDeIncidencia.get(posicion);
            Intent intentMenuDevolucion = new Intent(PedidosDeIncidencia.this, ListaDePiezasDePedido.class);
            bndMyBundle = new Bundle();
            try {
                bndMyBundle.putString("pedidox", (item.getIdFolio()));
                MensajeEnConsola.log(context, metodo, "pedido= " + item.getIdFolio());
            } catch (Throwable ex) {
                bndMyBundle.putInt("usuario", 0);
            }
            intentMenuDevolucion.putExtras(bndMyBundle);
            startActivity(intentMenuDevolucion);
        }
    };

    private void pedirFoliosPendientes() {
        metodo = "pedirFoliosPendientes()";
//        HttpParams params = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(params, 20000);
//        HttpConnectionParams.setSoTimeout(params, 20000);
//        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//        HttpProtocolParams.setUseExpectContinue(params, false);
//
//		HttpClient httpClient = new DefaultHttpClient(params);
//	    HttpPost httpPost = new HttpPost(MainActivity.url+"getPedidosPorIncidencia.php?tecnicoidx="+usuarioid+"&solSerIdx="+solSerId);
        listaPedidosDeIncidencia = new ArrayList<FolioPendiente>();
        MensajeEnConsola.log(context, metodo, MainActivity.url + "getPedidosPorIncidencia.php?tecnicoidx=" + usuarioid + "&solSerIdx=" + solSerId);
        try {
//	    	HttpResponse response= httpClient.execute(httpPost);
//			codigoHTTP = response.getStatusLine().getStatusCode();
//			jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
//	    	JSONObject object = new JSONObject(jsonResult);

            JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url + "getPedidosPorIncidencia.php?tecnicoidx=" + usuarioid + "&solSerIdx=" + solSerId);
            //assert object != null;
            JSONArray jsonArray = object.optJSONArray("pedidosxIncidencia");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                listaPedidosDeIncidencia.add(new FolioPendiente(jsonArrayChild.optString("pedido"), jsonArrayChild.optString("nomSala"), jsonArrayChild.optString("solSerId"), jsonArrayChild.optString("estatusx"),""));
            }
            adaptador = new FolioPendienteArrayAdapter(PedidosDeIncidencia.this, listaPedidosDeIncidencia);
            FolioPendiente item = listaPedidosDeIncidencia.get(0);
            if (item.getNombreSala().toString().equals("0")) {
                Alert.Error(context, metodo, "Sin Registros", "No hay pedidos registrados para esta incidencia");
                lvPedidosDeIncidencia.setVisibility(View.GONE);

//				new Thread(
//	    	            new Runnable() {
//	    	                @Override
//	    	                public void run() {
//	    	                    runOnUiThread(new Runnable() {
//	    	                        @Override
//	    	                        public void run() {
//	    	            	    		lvPedidosDeIncidencia.setVisibility(View.GONE);
//	    	                        }
//	    	                    });
//	    	                }
//	    	            }
//	    	    ).start();
            }
        } catch (JSONException e) {
            Alert.Error(context, metodo, getString(R.string.titJsonException), new ServerErrors(codigoHTTP, jsonResult).getErrorServer());
        } catch (Exception e) {
            Alert.Error(context, metodo, "Error MA001", e.getMessage());
        }
    }

    private void barraProgresoPedirPedidos() {
        Progreso progreso = new Progreso();
        progreso.execute();
    }

    private class Progreso extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            pDialog = new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.Conectando_con_servidor_remoto))
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();
        }

        protected Void doInBackground(Void... params) {
            pedirFoliosPendientes();
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            lvPedidosDeIncidencia.setAdapter(adaptador);
            pDialog.dismiss();
        }
    }


    public void onResume() {
        super.onResume();
        actualizaEstatusTecnico();
    }


}
//275