package com.example.devolucionmaterial;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.devolucionmaterial.data_base.BDhelper;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

import org.json.JSONException;
import org.json.JSONObject;

public class EdicionRegistroMaterial extends Activity{
	String metodo;
	Context context;
	private EditText etcodigo, etcantidad, etserie;
	Button btnEditar,btnCancelar;
	Bundle bndMyBundle;
	Bundle bndReceptor;
	Intent intReceptor;
	int usuarioID;
	private BDmanager manager;
	private Cursor cursorGeneral;
	private TextView tvDescripcion;
	private int existeSerie, idSustituto;
	private boolean verificarSerie=false;
	private String cantidad;
	private static String docPHPVerificaRefaccion="verificaRefaccion.php?";
	private ProgressDialog  pDialog;

	//private Cursor sala;
	private int clave;
	private String nombre;
	private int codigo, estatus, suplente, refaccionid;
	private String nombrePieza;
	private int existePieza, status;

	private int i;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_edicion_piezas_mat);
		context = this;
		setupEdicionRegistroDeMaterial();
	}

	private void setupEdicionRegistroDeMaterial(){
		metodo = "setupEdicionRegistroDeMaterial()";
		manager = new BDmanager(this);
		btnEditar = (Button)findViewById(R.id.btEditar);
		btnCancelar = (Button)findViewById(R.id.btnCancelarEdit);
		etcodigo = (EditText)findViewById(R.id.etcodigoEdit);
		etcantidad = (EditText)findViewById(R.id.etcantidadEdit);
		etserie = (EditText)findViewById(R.id.etserieEdit);
		tvDescripcion = (TextView)findViewById(R.id.tvDescripcionEdit);

		Intent intent = getIntent();
		bndReceptor = intent.getExtras();

		final int idPiezaRecibido = Integer.valueOf(bndReceptor.getString(Lista_Devolucion.EXTRA_ID_PIEZA));


		etcodigo.setText(bndReceptor.getString(Lista_Devolucion.EXTRA_CODIGO));
		etcantidad.setText(bndReceptor.getString(Lista_Devolucion.EXTRA_CANTIDAD));
		etserie.setText(bndReceptor.getString(Lista_Devolucion.EXTRA_SERIE));

		btnCancelar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnEditar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String codigo=etcodigo.getText().toString();
				cantidad=etcantidad.getText().toString();
				String serie=etserie.getText().toString();
				String descripcion = tvDescripcion.getText().toString();
				String seriePartida[] = serie.split(" ");
				String serieValidada="";

				if(verificarSerie){
					if(serie.length()==0 || cantidad.length()==0 || codigo.length()==0)
						Alert.Alerta(context, metodo, 2, "Hay campos vacíos");
					else {
						int cantidadInt=Integer.valueOf(cantidad);
						if(cantidadInt==0){
							Alert.Alerta(context, metodo, 2, "La cantidad no puede ser 0");
							etcantidad.requestFocus();
							getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
						}else if(cantidadInt>99){
							Alert.Alerta(context, metodo, 2, "No puedes pedir 100 piezas o más");
							etcantidad.requestFocus();
							getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
						}else if(etserie.getText().length()<6)
						{
							Alert.Alerta(context, metodo, 1,"El número de serie es incorrecto");
							etserie.setText("");
						}else{
							//for(int i=0; i<seriePartida.length;i++)
							//	serieValidada = serieValidada+seriePartida[i];
							for (String aSeriePartida : seriePartida)
								serieValidada = serieValidada+seriePartida[i];
							manager.actualizarRegistroPieza(idPiezaRecibido, Integer.valueOf(codigo), descripcion, Integer.valueOf(cantidad), serieValidada, "");
							Intent databack = new Intent();
							databack.putExtra("result","ok");
							setResult(RESULT_OK, databack);
							Alert.Alerta(context, metodo, 0, "Registro modificado correctamente");
							finish();
							//manager.insertarDescripcionSalida(salidaid, Integer.valueOf(codigo), descripcion, Integer.valueOf(cantidad), serieValidada);
						}
					}
				}else{
					if(cantidad.length()==0 || codigo.length()==0)
						Alert.Alerta(context, metodo, 2, "Falta la cantidad");
					else {
						int cantidadInt=Integer.valueOf(cantidad);
						if(cantidadInt==0){
							Alert.Alerta(context, metodo, 2, "La cantidad no puede ser 0");
							etcantidad.requestFocus();
							getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
						}else if(cantidadInt>99){
							Alert.Alerta(context, metodo, 2, "No puedes pedir 100 piezas o más");
							etcantidad.requestFocus();
							getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
						}else  {
							manager.actualizarRegistroPieza(idPiezaRecibido, Integer.valueOf(codigo), descripcion, Integer.valueOf(cantidad), serieValidada, "");
							Intent databack = new Intent();
							databack.putExtra("result","ok");
							setResult(RESULT_OK, databack);
							Alert.Alerta(context, metodo, 0, "Registro modificado correctamente");
							finish();
						}
					}
				}
			}
		});

		etcodigo.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				metodo = "etcodigo.addTextChangedListener()";
				if(s.length()==6){
					clave = Integer.valueOf(etcodigo.getText().toString());
					cursorGeneral = manager.buscarClave(clave);
					cursorGeneral.moveToFirst();
					existePieza = cursorGeneral.getCount();
					if(existePieza>0){
						nombre = cursorGeneral.getString(3);
						existeSerie = cursorGeneral.getInt(6);
						status = cursorGeneral.getInt(4);
						if(cursorGeneral.getString(5)==null)
							idSustituto = 0;
						else
							idSustituto = cursorGeneral.getInt(5);
						if(status==124){
							if(idSustituto!=0){
								Cursor cursorBuscarSustituto = manager.buscarId(idSustituto);
								cursorBuscarSustituto.moveToFirst();
								final int claveSustituto = cursorBuscarSustituto.getInt(2);
								final String nombreSustituto = cursorBuscarSustituto.getString(3);
								final int existeSerieEnSustituto = cursorBuscarSustituto.getInt(6);

								AlertDialog.Builder builder = new AlertDialog.Builder(context);
								builder.setMessage(getString(R.string.infoRef1)+clave+getString(R.string.infoRef2)+
										claveSustituto+getString(R.string.infoRef3)).setTitle(getString(R.string.refSusUb))
										.setPositiveButton(getString(R.string.aceptar),
												new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int id) {
														if(existeSerieEnSustituto==1){
															etcantidad.setText("1");
															etcantidad.setEnabled(false);
															etserie.setEnabled(true);
															etserie.setHint(getString(R.string.ingresaSerie));
															etserie.requestFocus();
															getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
															verificarSerie = true;
														}else{
															etserie.setEnabled(false);
															etcantidad.setEnabled(true);
															etcantidad.setText("");
															etserie.setHint(getString(R.string.campoCompleto));
															etcantidad.requestFocus();
															getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
															verificarSerie = false;
														}
														etcodigo.setText(String.valueOf(claveSustituto));
														tvDescripcion.setText(nombreSustituto);
														btnEditar.setEnabled(true);
													}
												})
										.setNegativeButton("Cancelar",
												new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int id) {
														tvDescripcion.setText(R.string.tvDescripcion);
														etcodigo.setHint(R.string.hintSeisDigitos);
														etserie.setText("");
														etcantidad.setText("");
														btnEditar.setEnabled(false);
														etcantidad.setEnabled(false);
														etserie.setEnabled(false);
														etcodigo.requestFocus();
														getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
														Alert.Alerta(context, metodo, 2, getString(R.string.errorPieDes));
													}
												});
								builder.show();

							}else{
								if (existeSerie == 1) {
									etserie.setEnabled(true);
									etcantidad.setText("1");
									etcantidad.setEnabled(false);
									etserie.setHint(getString(R.string.ingresaSerie));
									etserie.requestFocus();
									getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
									verificarSerie = true;
								} else {
									etserie.setEnabled(false);
									etserie.setHint(getString(R.string.campoCompleto));
									etcantidad.setEnabled(true);
									etcantidad.setText("");
									etcantidad.requestFocus();
									getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
									verificarSerie = false;
								}
								tvDescripcion.setText(nombre);
								btnEditar.setEnabled(true);
								getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
								Alert.Alerta(context, metodo, 1, getString(R.string.pieDesc));
								///////////////////
							}
						}else {
							tvDescripcion.setText(nombre);
							etcantidad.setEnabled(true);
							btnEditar.setEnabled(true);
							if (existeSerie == 1) {
								etserie.setEnabled(true);
								etcantidad.setText("1");
								etcantidad.setEnabled(false);
								etserie.setHint(getString(R.string.ingresaSerie));
								etserie.requestFocus();
								getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
								verificarSerie = true;
							} else {
								etserie.setEnabled(false);
								etserie.setHint(getString(R.string.campoCompleto));
								etcantidad.setEnabled(true);
								etcantidad.setText("");
								etcantidad.requestFocus();
								getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
								verificarSerie = false;
							}
						}
					}else{
						barraProgresoRefaccion();
						Alert.Alerta(context, metodo, 1, "La pieza solicitada no existe en la base de datos del celular.\nVerificando existencia de pieza en SisCo...");
					}
				}
			}
			@Override
			public void afterTextChanged(Editable s) {}
		});
	}
	public void barraProgresoRefaccion(){
		if(CheckService.internet(context)) {
			new ProgresoRefaccion().execute();
		}else{
			Alert.ActivaInternet(context);
		}
	}

	private class ProgresoRefaccion extends AsyncTask<Void, Integer, Void> {
		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(context);
			pDialog.setCancelable(false);
			pDialog.show();
			pDialog.setContentView(R.layout.custom_progressdialog);
		}
		protected Void doInBackground(Void... params) {
			metodo = "ProgresoRefaccion<>.doInBackground()";
			try {
				JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url+docPHPVerificaRefaccion+"codigox="+clave);
				assert object != null;
				refaccionid = object.getInt("refaccionidx");
				if(refaccionid!=0){
					codigo = object.getInt("clavex");
					nombrePieza = object.getString("nombrex");
					estatus = object.getInt("estatusidfkx");
					suplente = object.getInt("suplentex");
				}else{
					refaccionid=0;
				}
			} catch(JSONException e) {
				MensajeEnConsola.log(context, metodo, "JSONException e = "+e.getMessage());
			} catch (Exception e) {
				MensajeEnConsola.log(context, metodo, "Exception e = "+e.getMessage());
			}
			return null;
		}
		@SuppressWarnings("deprecation")
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			metodo = "ProgresoRefaccion<AsyncTask>.onPostExecute()";
			pDialog.dismiss();
			if(refaccionid!=0){
				BDhelper admin = new BDhelper(context);
				SQLiteDatabase bd = admin.getWritableDatabase();
				ContentValues cv = new ContentValues(6);
				cv.put(BDmanager.CN_ID_REFACCIONES, refaccionid);
				cv.put(BDmanager.CN_CLAVE, codigo);
				cv.put(BDmanager.CN_NOMBRE, nombrePieza);
				cv.put(BDmanager.CN_STATUS, estatus);
				cv.put(BDmanager.CN_NO_SERIE, suplente);
				bd.insert(BDmanager.TABLE_REFACCIONES, null, cv);
				Alert.Alerta(context, metodo, 0, "Existencia validada");
				etcodigo.setText("");
				etcodigo.setText(String.valueOf(clave));
			}else{
				AlertDialog alertDialog = new AlertDialog.Builder(context).create();
				alertDialog.setTitle(getString(R.string.errorPie));
				alertDialog.setMessage(getString(R.string.infoErrPie1)+clave+getString(R.string.infoErrPie2));
				alertDialog.setButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						tvDescripcion.setText(R.string.tvDescripcion);
						etserie.setHint("");
						etserie.setText("");
						etcantidad.setText("");
						etcantidad.setEnabled(false);
						etserie.setEnabled(false);
						btnEditar.setEnabled(false);
						etcodigo.requestFocus();
						getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
					}
				});
				alertDialog.setIcon(R.drawable.ic_launcher);
				alertDialog.show();
			}
		}

	}
}
//423