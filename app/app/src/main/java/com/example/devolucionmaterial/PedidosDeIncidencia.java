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
import android.widget.AdapterView.OnItemClickListener;

import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.lists.FolioPendienteArrayAdapter;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.ServerErrors;

public class PedidosDeIncidencia extends Activity{
	String metodo;
	Context context;
	private LinearLayout llMenu;
	private ListView lvPedidosDeIncidencia;
	private Bundle bndReceptor;
	private Intent intReceptor;
	private String alias, password, usuarioid, resp, solSerId,jsonResult;
	private List<FolioPendiente> listaPedidosDeIncidencia;
	private ArrayAdapter<FolioPendiente> adaptador;
	private Bundle bndMyBundle;
	private ProgressDialog  pDialog;	
	private Menu mMenu;
	private BDmanager manager;
	private int codigoHTTP=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folios_pend);
		context = this;
		setupPedidosDeIncidencia();
	}
	
	private void setupPedidosDeIncidencia(){
		manager = new BDmanager(this);
		lvPedidosDeIncidencia = (ListView)findViewById(R.id.lvFoliosPendientes);
		llMenu = (LinearLayout)findViewById(R.id.llmenu);
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
	
	public OnItemClickListener selectedItem = new OnItemClickListener(){
	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
	        metodo = "selectedItem.OnItemClickListener()";
			FolioPendiente item = listaPedidosDeIncidencia.get(posicion);
	    	Intent intentMenuDevolucion = new Intent(PedidosDeIncidencia.this, ListaDePiezasDePedido.class);
			bndMyBundle = new Bundle();
			try{
				bndMyBundle.putString("pedidox", (item.getIdFolio()));
				MensajeEnConsola.log(context, metodo, "pedido= "+item.getIdFolio());
			}catch (Throwable ex){
				bndMyBundle.putInt("usuario", 0);
			}
			intentMenuDevolucion.putExtras(bndMyBundle);
			startActivity(intentMenuDevolucion);
	    }
	};
	
	private void pedirFoliosPendientes(){
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
	    MensajeEnConsola.log(context, metodo, MainActivity.url+"getPedidosPorIncidencia.php?tecnicoidx="+usuarioid+"&solSerIdx="+solSerId);
	    try {
//	    	HttpResponse response= httpClient.execute(httpPost);
//			codigoHTTP = response.getStatusLine().getStatusCode();
//			jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
//	    	JSONObject object = new JSONObject(jsonResult);

			JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url+"getPedidosPorIncidencia.php?tecnicoidx="+usuarioid+"&solSerIdx="+solSerId);
	    	//assert object != null;
	    	JSONArray jsonArray = object.optJSONArray("pedidosxIncidencia");
	    	for (int i = 0; i < jsonArray.length(); i++) {
	    		JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
    	        listaPedidosDeIncidencia.add(new FolioPendiente(jsonArrayChild.optString("pedido"), jsonArrayChild.optString("nomSala"), jsonArrayChild.optString("solSerId"), jsonArrayChild.optString("estatusx")));
	    	}
	    	adaptador = new FolioPendienteArrayAdapter(PedidosDeIncidencia.this, listaPedidosDeIncidencia);
	    	FolioPendiente item = listaPedidosDeIncidencia.get(0);
	    	if(item.getNombreSala().toString().equals("0")){
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
	    }
		catch(JSONException e) {
			Alert.Error(context, metodo, getString(R.string.titJsonException), new ServerErrors(codigoHTTP, jsonResult).getErrorServer());
		} catch (Exception e) {
			Alert.Error(context, metodo, "Error MA001",e.getMessage());
		}
	}private void barraProgresoPedirPedidos(){
		 Progreso progreso = new Progreso();
	        progreso.execute();  
	}
	
	private class Progreso extends AsyncTask<Void, Integer, Void> {
		  @Override
		  protected void onPreExecute() {
			  pDialog = new ProgressDialog(PedidosDeIncidencia.this);
			  pDialog.setCancelable(false);
			  pDialog.show();
			  pDialog.setContentView(R.layout.custom_progressdialog);
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
			Log.e("error menu", String.valueOf(e));
		}
        mMenu = menu;
		actualizaEstatusTecnico();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_est_tec) {
            MenuInicialTecnico.iniciaMenuEstatusTec(PedidosDeIncidencia.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


	public void onResume(){
		super.onResume();
		actualizaEstatusTecnico();
	}

	public void actualizaEstatusTecnico(){
		if(mMenu!=null){
			MenuItem item = mMenu.findItem(R.id.action_est_tec);
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("39")){
				if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("1")){
					item.setIcon(R.drawable.est_activo);
				}
				else if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("2")){
					item.setIcon(R.drawable.est_traslado_sala);
				}
				else if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("3")){
					item.setIcon(R.drawable.est_asignado);
				}
				if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("4")){
					item.setIcon(R.drawable.est_en_comida);
				}
			}
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("40")){
				item.setIcon(R.drawable.est_inactivo);
			}
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("109")){
				item.setIcon(R.drawable.est_incidencia);
			}
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("79")){
				item.setIcon(R.drawable.est_dia_descanso);
			}
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("80")){
				item.setIcon(R.drawable.mix_peq);
			}
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("81")){
				item.setIcon(R.drawable.est_vacaciones);
			}
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("89")){
				item.setIcon(R.drawable.est_incapacidad);
			}
		}
	}
}
//275