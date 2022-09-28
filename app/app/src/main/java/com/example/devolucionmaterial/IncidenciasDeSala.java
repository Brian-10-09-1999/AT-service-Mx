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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.lists.FolioPendienteArrayAdapter;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

public class IncidenciasDeSala extends Activity{
	String metodo;
	Context context;
	private LinearLayout llMenu;
	private ListView lvFoliosDeSala;
	private Bundle bndReceptor, bndMyBundle;
	private Intent intReceptor;
	private String salaID, resp;
	private List<FolioPendiente> listaIncidenciasDeSala;
	private ArrayAdapter<FolioPendiente> adaptador;
	private ProgressDialog  pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folios_pend);
		context = this;
		setupIncidenciasDeSala();
	}
	private void setupIncidenciasDeSala(){
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
	    		listaIncidenciasDeSala.add(new FolioPendiente(jsonArrayChild.optString("folioId"), jsonArrayChild.optString("nombreSala"), jsonArrayChild.optString("solSerId"), jsonArrayChild.optString("estatusx")));
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
			pDialog = new ProgressDialog(IncidenciasDeSala.this);
			pDialog.setCancelable(false);
			pDialog.show();
			pDialog.setContentView(R.layout.custom_progressdialog);
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