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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.lists.FolioPendienteArrayAdapter;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.thanosfisherman.mayi.PermissionBean;

public class IncidenciasDeSala extends BaseActivity{
	String metodo;
	private LinearLayout llMenu;
	private ListView lvFoliosDeSala;
	private Bundle bndReceptor, bndMyBundle;
	private Intent intReceptor;
	private String salaID, resp;
	private List<FolioPendiente> listaIncidenciasDeSala;
	private ArrayAdapter<FolioPendiente> adaptador;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folios_pend);
		initToolbar("Incidencia de Sala", true, true);
		context = this;
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
	private void initSetUp(){
		lvFoliosDeSala = (ListView)findViewById(R.id.lvFoliosPendientes);
		llMenu = (LinearLayout)findViewById(R.id.llmenu);
		lvFoliosDeSala.setOnItemClickListener(selectedItem);
		intReceptor = this.getIntent();
		bndReceptor = intReceptor.getExtras();
		salaID= bndReceptor.getString("salaIDx");
		llMenu.setVisibility(View.GONE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lvFoliosDeSala.setLayoutParams(params);
		barraProgresoPedirIncidenciasPendientes();
	}
	
	public OnItemClickListener selectedItem = new OnItemClickListener(){
	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
	        FolioPendiente item = (FolioPendiente)listaIncidenciasDeSala.get(posicion);
	    	Intent intentMenuDevolucion = new Intent(IncidenciasDeSala.this, PedidosDeSala.class);
			bndMyBundle = new Bundle();
			try{
				bndMyBundle.putString("folioIDx", (item.getIdFolio()));
				bndMyBundle.putString("nomSalax", (item.getNombreSala()));
			}catch (Throwable ex){
				bndMyBundle.putInt("usuario", 0);
			}
			intentMenuDevolucion.putExtras(bndMyBundle);
			startActivity(intentMenuDevolucion);
	    }
	};
	
	private void pedirIncidenciasPendientes(){
		metodo = "pedirIncidenciasPendientes()";
        listaIncidenciasDeSala = new ArrayList<FolioPendiente>();
	    try {
	    	JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url+"getIncidenciasporSala.php?salaidx="+salaID);
			assert object != null;
	    	JSONArray jsonArray = object.optJSONArray("foliosDeSala");
	    	for (int i = 0; i < jsonArray.length(); i++) {
	    		JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
	    		listaIncidenciasDeSala.add(new FolioPendiente(jsonArrayChild.optString("folioId"), jsonArrayChild.optString("nombreSala"), jsonArrayChild.optString("solSerId"), jsonArrayChild.optString("estatusx"),""));
	    	}
	    	adaptador = new FolioPendienteArrayAdapter(IncidenciasDeSala.this, listaIncidenciasDeSala);
	    	FolioPendiente item = (FolioPendiente)listaIncidenciasDeSala.get(0);
	    	if(item.getNombreSala().equals("0")){
				Alert.Error(context, metodo, "Sin Registros", "No hay Incidencias pendientes registradas para esta sala");
	    		new Thread(
	    	            new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                    runOnUiThread(new Runnable() {
	    	                        @Override
	    	                        public void run() {
	    	            	    		lvFoliosDeSala.setVisibility(View.GONE);
	    	                        }
	    	                    });
	    	                }
	    	            }
	    	    ).start();
	    		}
	
	    }
		catch(JSONException e) {
			MensajeEnConsola.log(context, metodo, "JSONException e = "+e.getMessage());
		}catch (Exception e) {
			MensajeEnConsola.log(context, metodo, "Exception e = "+e.getMessage());
		}
	}
	private void barraProgresoPedirIncidenciasPendientes(){
		if(CheckService.internet(context)) {
			new Progreso().execute();
		}else{
			Alert.ActivaInternet(context);
		}
	}
	private class Progreso extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog= new MaterialDialog.Builder(context)
					.title(context.getString(R.string.Conectando_con_servidor_remoto))
					.content("Cargando...")
					.progress(true, 0)
					.cancelable(false)
					.progressIndeterminateStyle(false)
					.show();
		}
		@Override
			protected Void doInBackground(Void... params) {
			pedirIncidenciasPendientes();
			return null;
		}
		@Override
			protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			lvFoliosDeSala.setAdapter(adaptador);
			pDialog.dismiss();
		}
	}
}
//202