package com.example.devolucionmaterial.dialogs;

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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.activitys.materialesSeccion.Menu_Devolucion;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.lists.ListViewAdapterSpinerRegiones;
import com.example.devolucionmaterial.lists.ListViewItem;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.ServerErrors;

public class DialogoBusquedaTecnicoBaja extends Activity implements OnItemSelectedListener{
	String metodo;
	Context context;
	private ListView lvTecnicosBusqueda;
	private Spinner spnRegion;
	private EditText etBusquedaTecnico;
	private ImageButton imgbtnBuscar;
	ArrayList<ListViewItem> data = new ArrayList<>();
	protected ArrayAdapter<CharSequence> adapter;
	private String region, region2,jsonResult;
	private List<Tecnico> listaDeTecnicos;
	private ArrayAdapter<Tecnico> adaptador;
	private ProgressDialog  pDialog;
	private String palabraBusqueda;
	private int codigoHTTP=0;
	public static String docPHPgetTecnicosBaja = "getTecnicosBaja.php?";
	BDmanager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_buscar_tecnico);
		context = this;
		manager = new BDmanager(context);
		setupDialogoBusqueda();
    }
    
    private void setupDialogoBusqueda(){
		metodo = "setupDialogoBusqueda()";
		lvTecnicosBusqueda = (ListView)findViewById(R.id.lvBusquedaTecnico);
		spnRegion = (Spinner)findViewById(R.id.spnRegionBusqueda);
		etBusquedaTecnico = (EditText)findViewById(R.id.etBusquedaTecnico);
		imgbtnBuscar = (ImageButton)findViewById(R.id.imgbtnBuscar);
		data.clear();
		Cursor fila = manager.consulta("select id, nombre from cregion ORDER BY nombre");
		for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){

			data.add(new ListViewItem(fila.getInt(0),fila.getString(1)));
		}
		fila.close();
		ListViewAdapterSpinerRegiones adapterRegiones = new ListViewAdapterSpinerRegiones(this, data);
		spnRegion.setAdapter(adapterRegiones);

		//Asignas el origen de datos desde los recursos
		//adapter = ArrayAdapter.createFromResource(this, R.array.Regiones, android.R.layout.simple_spinner_item);
		//Asignas el layout a inflar para cada elemento al momento de desplegar la lista
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Seteas el adaptador
		//spnRegion.setAdapter(adapter);
		spnRegion.setOnItemSelectedListener(this);
		lvTecnicosBusqueda.setOnItemClickListener(selectedItem);

    	Intent intentPost = getIntent();
		region = intentPost.getStringExtra(Menu_Devolucion.EXTRA_REGION);
        MensajeEnConsola.log(context, metodo, "Region = "+region);
		region2 = ""+((Integer.parseInt(region)));
		if(Integer.parseInt(region2)>7) {
			spnRegion.setSelection(66-Integer.parseInt(region2));
		}else{
			spnRegion.setSelection(Integer.parseInt(region2) - 1);
		}

		//spnRegion.setSelection(Integer.parseInt(region2));
		
		imgbtnBuscar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				palabraBusqueda = etBusquedaTecnico.getText().toString();
				palabraBusqueda = palabraBusqueda.replace(" ", "!");
				if (palabraBusqueda.length() < 4)
					Alert.Alerta(context, "imgbtnBuscar.setOnClickListener()", 1, "Ingresa al menos 4 caractéres en tu búsqueda");
				else
					barraProgresoObtenerTecnicos();
			}
		});

		etBusquedaTecnico.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					palabraBusqueda = etBusquedaTecnico.getText().toString();
					palabraBusqueda = palabraBusqueda.replace(" ", "!");
					if (palabraBusqueda.length() < 4)
						Alert.Alerta(context, "etBusquedaTecnico.setOnEditorActionListener()",1, "Ingresa al menos 4 caractéres en tu búsqueda");
					else
						barraProgresoObtenerTecnicos();
					return true;
				}
				return false;
			}
		});
    	
    }
    
    private void postObtenerTecnicos(){
		metodo = "postObtenerTecnicos()";
		int seleccion;
		if(spnRegion.getSelectedItemPosition()>6){
			seleccion = 52+spnRegion.getSelectedItemPosition();
		}else{
			seleccion = spnRegion.getSelectedItemPosition()+1;
		}
        region2 = ""+(seleccion);
        
	    listaDeTecnicos = new ArrayList<Tecnico>();
	    try {
	    	JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url+docPHPgetTecnicosBaja+"datox="+palabraBusqueda+"&regionx="+region2);
			assert object != null;
			JSONArray jsonArray = object.optJSONArray("datosTec");
	    	for (int i = 0; i < jsonArray.length(); i++) {
	    		JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
    	        listaDeTecnicos.add(new Tecnico(jsonArrayChild.optString("nombre"), jsonArrayChild.optString("tecnicoid")));
	    	}
	    	
	    	adaptador = new TecnicoArrayAdapter(context, listaDeTecnicos);
	    	
	    	new Thread(
		            new Runnable() {
		                @Override
		                public void run() {
		                    runOnUiThread(new Runnable() {
		                        @Override
		                        public void run() {
									Tecnico item = listaDeTecnicos.get(0);
									//Tecnico item = (Tecnico)listaDeTecnicos.get(0);
		                	    	if(item.getTecnicoid().equals("0")){
										Alert.Error(context, metodo, "Sin Registros", "No hay coincidencias para el técnico que buscas" +
		                	    				" con la palabra "+etBusquedaTecnico.getText().toString());
		                		    	DialogoBusquedaTecnicoBaja.this.setTitle("Buscar Técnico: 0 coincidencias");

		                	    		}else if(listaDeTecnicos.size()==1)
		                	    			DialogoBusquedaTecnicoBaja.this.setTitle("Buscar Técnico: "+listaDeTecnicos.size()+" coincidencia");
		                	    		else
		                	    			DialogoBusquedaTecnicoBaja.this.setTitle("Buscar Técnico: "+listaDeTecnicos.size()+" coincidencias");

		                        }
		                    });
		                }
		            }
		    ).start();
	    }
		catch(JSONException e) {
			Alert.Error(context, metodo, getString(R.string.titJsonException), new ServerErrors(codigoHTTP, jsonResult).getErrorServer());
		} catch (Exception e) {
			Alert.Error(context, metodo, "Error MA001",e.getMessage());
		}
    }
	private void barraProgresoObtenerTecnicos(){
		if(CheckService.internet(context)) {
			new Progreso().execute();
		}else{
			Alert.ActivaInternet(context);
		}
	}
	
	private class Progreso extends AsyncTask<Void, Integer, Void> {
		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(DialogoBusquedaTecnicoBaja.this);
			pDialog.setCancelable(false);
			pDialog.show();
			pDialog.setContentView(R.layout.custom_progressdialog);


			Alert.ActivaInternet(context);



		}
		protected Void doInBackground(Void... params) {
			postObtenerTecnicos();
			return null;
		}
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			lvTecnicosBusqueda.setAdapter(adaptador);
			pDialog.dismiss();
		}
	}
	public OnItemClickListener selectedItem = new OnItemClickListener(){
	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
			Tecnico item = listaDeTecnicos.get(posicion);
			//Tecnico item = (Tecnico)listaDeTecnicos.get(posicion);
	    	//Toast.makeText(DialogoBusquedaTecnicoBaja.this, "id= "+	item.getTecnicoid(), Toast.LENGTH_LONG).show();

			Intent databack = new Intent();
			databack.putExtra("tecnicoidBajax", item.getTecnicoid());
			setResult(RESULT_OK, databack);
			DialogoBusquedaTecnicoBaja.this.finish();
	    }
	};

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id){}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}
	
	private class Tecnico{
		private String nombre, tecnicoid;
		
		public Tecnico(String nombre, String tecnicoid){
			this.tecnicoid = tecnicoid;
			this.nombre = nombre;
		}
		
		public String getNombre(){return nombre;}
		public String getTecnicoid(){return tecnicoid;}
	}
	
	public class TecnicoArrayAdapter extends ArrayAdapter<Tecnico>{
		public TecnicoArrayAdapter(Context context, List<Tecnico> objects){
			super(context,0, objects);
		}
		@Override
	    public View getView(int position, View convertView, ViewGroup parent){
			//Obteniendo una instancia del inflater
	        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        
	      //Salvando la referencia del View de la fila
	        View listItemView = convertView;
	        
	      //Comprobando si el View no existe
	        if (null == convertView) {
	            //Si no existe, entonces inflarlo con two_line_list_item.xml
	            listItemView = inflater.inflate(android.R.layout.simple_list_item_1,parent,false);
	        }
	        
	      //Obteniendo instancias de los text views
	        TextView nombre = (TextView)listItemView.findViewById(android.R.id.text1);
	        nombre.setTextColor(Color.BLACK);
	        nombre.setTextSize(13);

	        
	      //Obteniendo instancia de la Tarea en la posici�n actual
	        //Tecnico item = (Tecnico)getItem(position);
			Tecnico item = getItem(position);

			nombre.setText(item.getNombre());

	        //Devolver al ListView la fila creada
			return listItemView;	
	}
}
}
