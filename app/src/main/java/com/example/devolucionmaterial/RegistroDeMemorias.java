package com.example.devolucionmaterial;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.activitys.materialesSeccion.Registro;

public class RegistroDeMemorias extends Activity implements OnItemSelectedListener{
	String metodo;
	Context context;
	protected ArrayAdapter<CharSequence> adapter;
	private Spinner spnMemorias;
	private EditText etFolio, etOS, etCantidad;
	private TextView tvSalida;
	private ListView lvDelucionesMemorias;
	private BDmanager manager;
	private Button btnIngresar, btnEnviar; 
	Intent myIntent;
	Intent intReceptor;
	Bundle bndReceptor;
	private int salidaid, actividadOrigenMemorias;
	String url, modeloSeleccionado, modeloSelReemp;
	private int idSalidaMemoria, tecnicoID, idSalidaMemFromDevMem, idSalidaRec;
	String cantidad, folio, OS;
	ArrayList<String> listItems=new ArrayList<String>();
	ArrayList<String> listItemsEnviar=new ArrayList<String>();
	ArrayAdapter<String> adapterMemorias;
	String cadena, cadenaVerificacion, folioGuardado="", OSGuardado="", cantidadGuardado="";
	private int seleccion;
	private static final String KEY_SELECCION_SPINNER = "seleccionSpinner";
	private static final String KEY_FOLIO_GUARDADO = "folioGuardado";
	private static final String KEY_OS_GUARDADO = "OSGuardado";
	private static final String KEY_CANTIDAD_GUARDADO = "cantidadGuardado";
	private static final String KEY_NO_SALIDA = "noSalida";
	private static final String KEY_ACTIVIDAD_ORIGEN= "actividadOrigen";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devolucion_memorias);
		spnMemorias = (Spinner) findViewById(R.id.spnMemorias);
		etFolio = (EditText)findViewById(R.id.etFolioMemorias);
		etOS = (EditText)findViewById(R.id.etOS);
		etCantidad = (EditText)findViewById(R.id.etCantidadMemorias);
		tvSalida = (TextView)findViewById(R.id.tvSalidaMemorias);
		btnEnviar = (Button)findViewById(R.id.btnEnviarMemoria);
		btnIngresar = (Button)findViewById(R.id.btnIngresarMemoria);
		lvDelucionesMemorias = (ListView) findViewById(R.id.lvDevolucionesMemorias);
		setupRegistroDeMemorias();
	}
	
    @Override
    protected void onRestoreInstanceState(Bundle recEstado) {
          super.onRestoreInstanceState(recEstado);
          int savedseleccionSpinner = recEstado.getInt(KEY_SELECCION_SPINNER);	      
          String savedCantidad = recEstado.getString(KEY_CANTIDAD_GUARDADO);
          String savedFolio = recEstado.getString(KEY_FOLIO_GUARDADO);
          String savedOS = recEstado.getString(KEY_OS_GUARDADO);
	      actividadOrigenMemorias= recEstado.getInt(KEY_ACTIVIDAD_ORIGEN);
          spnMemorias.setSelection(savedseleccionSpinner);
          folioGuardado=savedFolio;
          OSGuardado = savedOS;
          cantidadGuardado = savedCantidad;

    }
	
	@Override
	protected void onSaveInstanceState (Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putString(KEY_CANTIDAD_GUARDADO, etCantidad.getText().toString());
	    outState.putString(KEY_FOLIO_GUARDADO, etFolio.getText().toString());
	    outState.putString(KEY_OS_GUARDADO, etOS.getText().toString());
	    outState.putInt(KEY_SELECCION_SPINNER, seleccion);
	    outState.putInt(KEY_NO_SALIDA, idSalidaRec);
	    outState.putInt(KEY_ACTIVIDAD_ORIGEN, actividadOrigenMemorias);

	}
	
	private void setupRegistroDeMemorias(){
	    manager = new BDmanager(this);
		adapterMemorias=new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1,
	            listItems);
		//Asignas el origen de datos desde los recursos
		adapter = ArrayAdapter.createFromResource(this, R.array.Memorias,
		                android.R.layout.simple_spinner_item);
		
		//Asignas el layout a inflar para cada elemento
		//al momento de desplegar la lista
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		intReceptor = this.getIntent();
		bndReceptor = intReceptor.getExtras();
		myIntent = this.getIntent();
		url=bndReceptor.getString("urlx");
		salidaid = bndReceptor.getInt("salidaidx");
		actividadOrigenMemorias = bndReceptor.getInt(MenuInicialTecnico.ACTIVIDAD_ORIGEN_MEMORIAS);
		//Seteas el adaptador 
		spnMemorias.setAdapter(adapter);
			if(actividadOrigenMemorias==0){
				//manager.insertarSalidaMemoria(usuarioID, Registro.obtenerHora(), 0);
				Cursor cursorSalidaMemoria = manager.cargarCursorSalidasMemoria();
				cursorSalidaMemoria.moveToLast();
				idSalidaMemoria = cursorSalidaMemoria.getInt(0);
				idSalidaRec=idSalidaMemoria;
			}else if (actividadOrigenMemorias==1){
				idSalidaMemoria = salidaid;
			idSalidaRec= idSalidaMemFromDevMem;
			}



		tvSalida.setText("Salida: "+idSalidaMemoria);
		spnMemorias.setOnItemSelectedListener(this);
		
		llenalista();		
		
		btnIngresar.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if(etCantidad.length()==0)
					Alert.Alerta(RegistroDeMemorias.this, "RegistroDeMemorias", 1, "Falta llenar campo cantidad");
				else if(Integer.valueOf(etCantidad.getText().toString())>99)
					Alert.Alerta(RegistroDeMemorias.this, "RegistroDeMemorias", 1, "No puedes pedir 100 piezas o mas");
				
				if(etFolio.length()==0 && etOS.length()==0)
					Alert.Alerta(RegistroDeMemorias.this, "RegistroDeMemorias", 1, "Falta No. OS o No. folio");
				else if(etFolio.length()==0 && etOS.length()!=0 && (etCantidad.length()!=0 && etCantidad.length()<100)){
					cantidad = etCantidad.getText().toString();
					OS = etOS.getText().toString();
					modeloSelReemp = modeloSeleccionado.replace(" ", "_");
					cadenaVerificacion = ""+OS+"/"+modeloSelReemp+"/"+"2";

					if(verificarFolio_OS(cadenaVerificacion)){
						manager.insertarPiezaSalidaMemoria(idSalidaMemoria, 0, 
							Integer.valueOf(etCantidad.getText().toString()), Integer.valueOf(etOS.getText().toString()), 0, modeloSeleccionado);						
					}
					else
						Alert.Alerta(RegistroDeMemorias.this, "RegistroDeMemorias", 1, "El OS o Folio no existen");
					}
				else if(etFolio.length()!=0 && etOS.length()==0 && (etCantidad.length()!=0 && etCantidad.length()<100)){
					cantidad = etCantidad.getText().toString();
					folio = etFolio.getText().toString();
					modeloSelReemp = modeloSeleccionado.replace(" ", "_");
					cadenaVerificacion = ""+folio+"/"+modeloSelReemp+"/"+"1";
					
					if(verificarFolio_OS(cadenaVerificacion)){
					manager.insertarPiezaSalidaMemoria(idSalidaMemoria, Integer.valueOf(etFolio.getText().toString()), 
							Integer.valueOf(etCantidad.getText().toString()), 0, 0, modeloSeleccionado);
					}else
						Alert.Alerta(RegistroDeMemorias.this, "RegistroDeMemorias", 1, "El OS o Folio no existen");
					
				}
				etFolio.setText("");
				etOS.setText("");
				etOS.setText("");
				spnMemorias.setSelection(0);
				llenalista();
			}
		});
		
		btnEnviar.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				enviarSalida(cadena);
			}
		});
		
	}
	
	public void onBackPressed() {
		salirReporte();
	}
	
	public void salirReporte(){
		final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(RegistroDeMemorias.this);
        alertaSimple.setTitle("Informacion");
        alertaSimple.setMessage("Deseas salir del Registro de Memorias?");
        alertaSimple.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                		finish();
                    }
                });
        alertaSimple.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        }
                });
        alertaSimple.setIcon(R.drawable.warning);
        alertaSimple.create();
        alertaSimple.show();

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int posicion, long id) {
		seleccion = posicion;
		modeloSeleccionado = parent.getItemAtPosition(posicion).toString();
		switch (seleccion) {
		case 0:
			etFolio.requestFocus();
	        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			etFolio.setText("");
			etCantidad.setText("");
			etOS.setText("");
			etFolio.setHint("Ingresa el No. Folio");
			etFolio.setEnabled(true);
			etOS.setHint("Campo Completo");
			etOS.setEnabled(false);
			etCantidad.setText("1");
			etCantidad.setEnabled(false);
			etFolio.setText(folioGuardado);
			break;
		case 1:
			etFolio.requestFocus();
	        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			etFolio.setText("");
			etCantidad.setText("");
			etOS.setText("");
			etFolio.setHint("Ingresa el No. Folio");
			etOS.setHint("Campo Completo");
			etOS.setEnabled(false);
			etCantidad.setText("1");
			etCantidad.setEnabled(false);
			etFolio.setText(folioGuardado);
			break;
		case 2:
			etFolio.requestFocus();
	        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			etFolio.setText("");
			etCantidad.setText("");
			etOS.setText("");
			etFolio.setHint("Ingresa el No. Folio");
			etOS.setHint("Campo Completo");
			etOS.setEnabled(false);
			etCantidad.setText("1");
			etCantidad.setEnabled(false);
			etFolio.setText(folioGuardado);
			break;
		case 3:
			etFolio.requestFocus();
	        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			etFolio.setText("");
			etCantidad.setText("");
			etOS.setText("");
			etFolio.setHint("Ingresa el No. Folio");
			etOS.setHint("Campo Completo");
			etOS.setEnabled(false);
			etCantidad.setText("1");
			etCantidad.setEnabled(false);
			etFolio.setText(folioGuardado);
			break;
		case 4:
			etOS.requestFocus();
	        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			etFolio.setText("");
			etCantidad.setText("");
			etOS.setText("");
			etFolio.setHint("Campo Completo");
			etFolio.setEnabled(false);
			etOS.setHint("Ingresa el OS");
			etOS.setEnabled(true);
			etCantidad.setText("");
			etCantidad.setEnabled(true);
			etOS.setText(OSGuardado);
			etCantidad.setText(cantidadGuardado);
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}
	
	private void llenalista(){
		listItems.clear();
		Cursor CursorSalidaId = manager.buscarSalidaMemoriaID(idSalidaMemoria);
		CursorSalidaId.moveToLast();
		tecnicoID = CursorSalidaId.getInt(1);
		cadena="";
		Cursor cursorSalidaActual = manager.buscarPiezaSalidaMemoriaID(idSalidaMemoria);
		cursorSalidaActual.moveToFirst();
		try{
			do{
				if(cursorSalidaActual.getInt(2)==0){
					listItems.add(""+cursorSalidaActual.getInt(4));
					cadena=cadena+""+cursorSalidaActual.getInt(2)+"-"+cursorSalidaActual.getInt(3)+"-"+cursorSalidaActual.getInt(4)+"-"+cursorSalidaActual.getString(6)+"-"+tecnicoID+"*";
				}
				else if (cursorSalidaActual.getInt(4)==0){
					listItems.add(""+cursorSalidaActual.getInt(2));
					cadena=cadena+""+cursorSalidaActual.getInt(2)+"-"+cursorSalidaActual.getInt(3)+"-"+cursorSalidaActual.getInt(4)+"-"+cursorSalidaActual.getString(6)+"-"+tecnicoID+"*";
				}
			}while(cursorSalidaActual.moveToNext());
		}catch (IndexOutOfBoundsException ignored){}
		lvDelucionesMemorias.setOnTouchListener(new ListView.OnTouchListener() {
	        @SuppressLint("ClickableViewAccessibility") @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            int action = event.getAction();
	            switch (action) {
	            case MotionEvent.ACTION_DOWN:
	                // Disallow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(true);
	                break;
	            case MotionEvent.ACTION_UP:
	                // Allow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(false);
	                break;
	            }
	            // Handle ListView touch events.
	            v.onTouchEvent(event);
	            return true;
	        }
	    });
		
		lvDelucionesMemorias.setAdapter(adapterMemorias);
		if(listItems.isEmpty())
			btnEnviar.setEnabled(false);
		else
			btnEnviar.setEnabled(true);
	}
	private boolean verificarFolio_OS(String cadenaVerifica) {
		metodo = "verificarFolio_OS()";
		String res, validacion = null;
		try {
			JSONObject object = JSONparse.consultaURL(context, metodo, url+"recibeUSBValida.php?cadenaverx="+cadenaVerifica);
			assert object != null;
	    	//JSONArray jsonArray = object.optJSONArray("validar");
	    	
        	res = object.getString("res");
        	//String area = object.getString("dep");
        	Log.i("hola",res+"--");
        	if(res.equals("1"))
        		validacion="1";
        	else
        		validacion="0";

	    //regresa validacion y usuarioID o desde aqui manda a la otra activity o manda alerta de usuario incorrecto
		} catch(JSONException e) {
			Toast.makeText(context, "Json", Toast.LENGTH_LONG).show();
			//textView.setText("Ocurrio un error 1.." + e.getMessage());
			//e.printStackTrace();
		} catch(Exception e) {
			Toast.makeText(context, "IOException", Toast.LENGTH_LONG).show();
			//textView.setText("Ocurrio un error 3.." + e.getMessage());
			//e.printStackTrace();
		}
		return validacion.equals("1");
	}
	
	private boolean enviarSalida(String cadenaSalida){
		metodo = "enviarSalida()";
		String res, validacion = null;
		try{
			JSONObject object = JSONparse.consultaURL(context, metodo, url+"poneTransitoUSB.php?cadenasalx="+cadena);
			assert object != null;
	    	//JSONArray jsonArray = object.optJSONArray("validar");
	    	
        	res = object.getString("res");
        	//String area = object.getString("dep");
        	Log.i("hola",res+"--");
        	if(res.equals("1")){
				Alert.Alerta(RegistroDeMemorias.this, "RegistroDeMemorias", 0, "Datos enviados y guardados correctamente");
        		manager.actualizarSalidaMemoria(Registro.obtenerHora(), 1, idSalidaMemoria);
        		this.finish();
        	}
        	else if(res.equals("0"))
				Alert.Alerta(RegistroDeMemorias.this, "RegistroDeMemorias", 2, "Ocurrio algun error, vuelve a intentarlo");


	    //regresa validacion y usuarioID o desde aqui manda a la otra activity o manda alerta de usuario incorrecto
		}catch(JSONException e){
			Toast.makeText(context, "Json", Toast.LENGTH_LONG).show();
			//textView.setText("Ocurrio un error 1.." + e.getMessage());
			//e.printStackTrace();
		}catch(Exception e){
			Toast.makeText(context, "IOException", Toast.LENGTH_LONG).show();
			//textView.setText("Ocurrio un error 3.." + e.getMessage());
			//e.printStackTrace();
		}
		return validacion.equals("1");
	}
}
//452