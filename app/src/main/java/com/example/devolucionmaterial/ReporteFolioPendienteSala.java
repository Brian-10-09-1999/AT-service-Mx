package com.example.devolucionmaterial;

import org.json.JSONException;
import org.json.JSONObject;

import android.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.thanosfisherman.mayi.PermissionBean;

public class ReporteFolioPendienteSala extends BaseActivity{
	String metodo;


	private String tecnicoid, numFolio;
	private TextView tvFolio, tvSala, tvFalla, tvSubFalla, tvLicencia,
			tvJuego, tvAceptarFolio, tvRechazarFolio, tvTraslado, TvAcceso, tvAtiende, tvEstatus, tvRepara;
	private RelativeLayout rlOpciones;
	private ProgressDialog  pDialog;
	private Menu mMenu;
	private BDmanager manager;
	private static  String docPHPverFolioPenSala = "verFolioPenSala.php?";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reporte_folio_pendiente);
		initToolbar("Reporte folio pendiente sala",true,true);

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
	/*	if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			permissionMultiple();
		}*/

	}


	private void initSetUp(){
		manager = new BDmanager(this);
		Intent intReceptor = this.getIntent();
		Bundle bndReceptor = intReceptor.getExtras();
		tecnicoid = bndReceptor.getString("tecnicoidx");
		numFolio = bndReceptor.getString("numFolio");

		tvFolio = (TextView)findViewById(R.id.lblFolio);
		tvSala = (TextView)findViewById(R.id.lblSala);
		tvFalla = (TextView)findViewById(R.id.lblFalla);
		tvSubFalla = (TextView)findViewById(R.id.lblSubFalla);
		tvLicencia = (TextView)findViewById(R.id.lblLicencia);
		tvJuego = (TextView)findViewById(R.id.lblJuego);

		tvTraslado = (TextView)findViewById(R.id.lblTraslado);
		TvAcceso = (TextView)findViewById(R.id.lblAcceso);
		tvAtiende = (TextView)findViewById(R.id.lblAtiende);
		tvEstatus = (TextView)findViewById(R.id.lblEstatus);
		tvRepara = (TextView)findViewById(R.id.lblRepara);
		tvAceptarFolio = (TextView)findViewById(R.id.lblAceptarFolio);
		tvRechazarFolio = (TextView)findViewById(R.id.lblRechazarFolio);
		rlOpciones = (RelativeLayout)findViewById(R.id.rlOpciones);
		inhabilitarCampos();
		barraProgresoReporteFolioPendienteSala();
	}

	private void inhabilitarCampos(){
		tvTraslado.setVisibility(View.GONE);
		TvAcceso.setVisibility(View.GONE);
		tvAtiende.setVisibility(View.GONE);
		tvEstatus.setVisibility(View.GONE);
		tvRepara.setVisibility(View.GONE);
		tvAceptarFolio.setVisibility(View.GONE);
		tvRechazarFolio.setVisibility(View.GONE);
		rlOpciones.setVisibility(View.GONE);
	}

	private void pedirDatosReporteFolioPendiente(String numFolio, String tecnicoid){
		metodo = "pedirDatosReporteFolioPendiente()";
		try{
			final JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url+docPHPverFolioPenSala+"numFoliox="+numFolio+"&tecnicoidx="+tecnicoid);
			assert object != null;
			new Thread(
					new Runnable() {
						@Override
						public void run() {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try{
										tvFolio.setText(object.getString("folio"));
										tvSala.setText(object.getString("sala"));
										tvFalla.setText(object.getString("falla"));
										tvSubFalla.setText(object.getString("subFalla"));
										tvLicencia.setText(object.getString("licencia"));
										tvJuego.setText(object.getString("juego"));
									}catch(JSONException e){
										e.printStackTrace();
										iniciarHiloExcepcion(getString(R.string.titJsonException), e.getMessage()
												+getString(R.string.cuerpoJsonException));
										pDialog.dismiss();
									}
								}
							});
						}
					}
			).start();
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
		final AlertDialog alertDialog = new AlertDialog.Builder(ReporteFolioPendienteSala.this).create();
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


	private void barraProgresoReporteFolioPendienteSala(){
		Progreso progreso = new Progreso();
		progreso.execute();
	}

	private class Progreso extends AsyncTask<Void, Integer, Void> {
		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(ReporteFolioPendienteSala.this);
			pDialog.setCancelable(false);
			pDialog.show();
			pDialog.setContentView(R.layout.custom_progressdialog);
		}
		protected Void doInBackground(Void... params) {
			pedirDatosReporteFolioPendiente(numFolio, tecnicoid);
			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();
		}

	}


}

//345