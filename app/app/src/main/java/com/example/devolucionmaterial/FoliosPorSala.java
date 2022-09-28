package com.example.devolucionmaterial;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.lists.SalaPorRegionArrayAdapter;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

public class FoliosPorSala extends Activity{
	String metodo;
	Context context;
	private ListView lvFoliosPorSala;
	private String usuarioidx;
	private String salaId,jsonResult;
	private List<FolioPendiente>  listaFoliosPorSala;
	private ArrayAdapter<FolioPendiente> adaptador;
	private ProgressDialog  pDialog;
	private Menu mMenu;
	private BDmanager manager;
	private static String docPHPfoliosPorSala = "foliosPorSala.php?";
	private int codigoHTTP=0;
	private int folioSala;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folios_pend);
		context = this;
		setupFoliosPorSala();
	}
	
	private void setupFoliosPorSala(){
		manager = new BDmanager(this);
		TextView tvNomUsuario = (TextView) findViewById(R.id.tvNombreusuario2);
		lvFoliosPorSala = (ListView)findViewById(R.id.lvFoliosPendientes);
		LinearLayout llMenu = (LinearLayout) findViewById(R.id.llmenu);

		Intent intReceptor = this.getIntent();
		Bundle bndReceptor = intReceptor.getExtras();
		usuarioidx = bndReceptor.getString("tecnicoidx");
		salaId = bndReceptor.getString("salaIdx");
		String nombre = bndReceptor.getString("nombrex");

		llMenu.setVisibility(View.GONE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lvFoliosPorSala.setLayoutParams(params);
		lvFoliosPorSala.setOnItemClickListener(selectedItem);
		tvNomUsuario.setText(nombre);
		barraProgresoFoliosPorSala();

	}
	
	public OnItemClickListener selectedItem = new OnItemClickListener(){
	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
	        FolioPendiente item = listaFoliosPorSala.get(posicion);
	    	Intent intentMenuDevolucion = new Intent(FoliosPorSala.this, ReporteFolioPendiente.class);
	    	//Intent intentMenuDevolucion = new Intent(FoliosPorSala.this, ReporteFolioPendienteSala.class);
			Bundle bndMyBundle = new Bundle();
			//bndMyBundle.putString("tecnicoidx", (usuarioidx));
			//bndMyBundle.putString("numFolio", (item.getSalaId()));
			bndMyBundle.putString("folioSala", "0");
			BDVarGlo.setVarGlo(context, "INFO_USUARIO_FOLIO_ID", item.getSalaId());

			intentMenuDevolucion.putExtras(bndMyBundle);
			startActivity(intentMenuDevolucion);
	    }
	};
	
	private void pedirSalasPorRegion(){
		metodo = "pedirSalasPorRegion()";
        listaFoliosPorSala = new ArrayList<FolioPendiente>();
		try {
	    	JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url+docPHPfoliosPorSala+"tecnicoidx="+usuarioidx +"&salaidx="+salaId);
			assert object != null;
	    	JSONArray jsonArray = object.optJSONArray("foliosPendSala");
	    	for (int i = 0; i < jsonArray.length(); i++) {
	    		JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
    	        listaFoliosPorSala.add(new FolioPendiente(
						jsonArrayChild.optString("numFolio"),
						jsonArrayChild.optString("folioSolId"),""));
	    	}
	    	adaptador = new SalaPorRegionArrayAdapter(this, listaFoliosPorSala);
			FolioPendiente item = listaFoliosPorSala.get(0);
			if(item.getNombreSala().equals("0")){
				iniciarHiloExcepcion(getString(R.string.sinRegistros), getString(R.string.infoNotFoliosDeSala));
				new Thread(
						new Runnable() {
							@Override
							public void run() {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										lvFoliosPorSala.setVisibility(View.GONE);
									}
								});
							}
						}
				).start();
			}
	    }
		catch(JSONException e) {
			MensajeEnConsola.log(context, metodo, "JSONException e = "+e.getMessage());
		} catch (Exception e) {
			MensajeEnConsola.log(context, metodo, "Exception e = "+e.getMessage());
		}
	}
	private void iniciarHiloExcepcion(final String titulo, final String cuerpoMensaje){
		new Thread(
	            new Runnable() {
	                @Override
	                public void run() {
	                    runOnUiThread(new Runnable() {
	                        @Override
	                        public void run() {
                    	alertaErrores(titulo, cuerpoMensaje);
	                        }
	                    });
	                }
	            }
	    ).start();
	}
	
	@SuppressWarnings("deprecation")
	private void alertaErrores(final String tituloError, final String cuerpoError){
			final AlertDialog alertDialog = new AlertDialog.Builder(FoliosPorSala.this).create();
			alertDialog.setTitle(tituloError);
			alertDialog.setMessage(cuerpoError);
			alertDialog.setButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//startActivity(enviarCorreo(MainActivity.this, tituloError, cuerpoError));
				alertDialog.dismiss();
			}
			});
			alertDialog.setIcon(R.drawable.errorinternet);
			alertDialog.show();
	}
	
	private void barraProgresoFoliosPorSala(){
		 Progreso progreso = new Progreso();
	        progreso.execute();  
	}
	
	private class Progreso extends AsyncTask<Void, Integer, Void> {
		  @Override
		  protected void onPreExecute() {
			  pDialog = new ProgressDialog(FoliosPorSala.this);
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
		    	 lvFoliosPorSala.setAdapter(adaptador);		
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_est_tec) {
            MenuInicialTecnico.iniciaMenuEstatusTec(FoliosPorSala.this);
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
//350