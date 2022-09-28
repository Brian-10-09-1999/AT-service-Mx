package com.example.devolucionmaterial.activitys.foliosPendientesSeccion;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.lists.SalaPorRegionArrayAdapter;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.thanosfisherman.mayi.PermissionBean;

public class FoliosPorSala extends BaseActivity{
	String metodo;
	Context context;
	private ListView lvFoliosPorSala;
	private String usuarioidx;
	private String salaId,jsonResult;
	private List<FolioPendiente>  listaFoliosPorSala;
	private ArrayAdapter<FolioPendiente> adaptador;
	private Menu mMenu;
	private BDmanager manager;
	private static String docPHPfoliosPorSala = "foliosPorSala.php?";
	private int codigoHTTP=0;
	private int folioSala;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folios_pend);
		initToolbar("Folio de Sala", true , true);
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

	@Override
	protected void onRestart() {
		super.onRestart();
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			permissionMultiple();
		}
		actualizaEstatusTecnico();

	}
	
	private void initSetUp(){
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
			  pDialog= new MaterialDialog.Builder(context)
					  .title(context.getString(R.string.Conectando_con_servidor_remoto))
					  .content("Cargando...")
					  .progress(true, 0)
					  .cancelable(false)
					  .progressIndeterminateStyle(false)
					  .show();
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
	


	public void onResume(){
		super.onResume();
		actualizaEstatusTecnico();
	}



}
//350