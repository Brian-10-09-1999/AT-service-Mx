package com.example.devolucionmaterial.activitys.pedidosSeccion;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.thanosfisherman.mayi.PermissionBean;

public class PedidosPorIncidencia extends BaseActivity{
	String metodo;
	Context context;
	private ListView lvfoliosPendientes;
    private String alias;
    private String password,jsonResult;
    private List<Incidencia> listaFoliospendientes;
	private ArrayAdapter<Incidencia> adaptador;
    private Menu mMenu;
	private BDmanager manager;
	private static String TAG = "DebugPedidosPorIncidencia";
	private static String docPHPgetIncidenciasConPedido= "getIncidenciasConPedido.php?";
	private int codigoHTTP=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folios_pend);
		initToolbar("Incidencias",true, true);
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


	private void initSetUp(){
		metodo = "setupPedidosPorIncidencia()";
		manager = new BDmanager(this);
        TextView tvNomUsuario = (TextView) findViewById(R.id.tvNombreusuario2);
		lvfoliosPendientes = (ListView)findViewById(R.id.lvFoliosPendientes);
        LinearLayout llMenu = (LinearLayout) findViewById(R.id.llmenu);
		lvfoliosPendientes.setOnItemClickListener(selectedItem);
        Intent intReceptor = this.getIntent();
        Bundle bndReceptor = intReceptor.getExtras();
		alias = bndReceptor.getString("aliasx");
		password = bndReceptor.getString("passwordx");
        String nombre = bndReceptor.getString("nombrex");
		tvNomUsuario.setText(nombre);
		llMenu.setVisibility(View.GONE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lvfoliosPendientes.setLayoutParams(params);
		barraProgresoPedirFoliosPendientes();
	}
	
	public OnItemClickListener selectedItem = new OnItemClickListener(){
	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
	        Incidencia item = listaFoliospendientes.get(posicion);
	    	Intent intentMenuDevolucion = new Intent(PedidosPorIncidencia.this, ListaDePiezasDePedido.class);
            Bundle bndMyBundle = new Bundle();
			try{
				bndMyBundle.putString("pedidox", item.getPedido());
			}catch (Throwable ex){
				bndMyBundle.putInt("usuario", 0);
			}
			intentMenuDevolucion.putExtras(bndMyBundle);
			startActivity(intentMenuDevolucion);
	    }
	};
	
	private void pedirFoliosPendientes(){
    	metodo="pedirFoliosPendientes()";
		listaFoliospendientes = new ArrayList<Incidencia>();
	    try{
	    	JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url+docPHPgetIncidenciasConPedido+"aliasx="+alias+"&passwordx="+password);
			assert object != null;
			//assert  object != null;
			JSONArray jsonArray = object.optJSONArray("incidenciasConPedido");
	    	for (int i = 0; i < jsonArray.length(); i++) {
	    		JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
    	        listaFoliospendientes.add(new Incidencia(jsonArrayChild.optString("idFolio"), jsonArrayChild.optString("nomSala"), jsonArrayChild.optString("pedido")));
	    	}
	    	adaptador = new IncidenciaArrayAdapter(PedidosPorIncidencia.this, listaFoliospendientes);
	    	Incidencia item = listaFoliospendientes.get(0);
	    	if(item.getNomSala().equals("0")){
	    		Alert.Error(context, metodo, getString(R.string.sinRegistros), getString(R.string.notPedInc));
	    		new Thread(
	    	            new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                    runOnUiThread(new Runnable() {
	    	                        @Override
	    	                        public void run() {
	    	            	    		lvfoliosPendientes.setVisibility(View.GONE);
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
	private void barraProgresoPedirFoliosPendientes(){
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
			  pedirFoliosPendientes();
		      return null;
		  }
		  
		  protected void onPostExecute(Void result) {
		      super.onPostExecute(result);	
		      lvfoliosPendientes.setAdapter(adaptador);
		      pDialog.dismiss();
		  }

		}
	
	private class Incidencia{
		private String pedido, idFolio, nomSala;
		
		public Incidencia(String idFolio, String nomSala, String pedido){
			this.idFolio = idFolio;
			this.nomSala = nomSala;
			this.pedido = pedido;
		}
		
		public String getPedido(){return pedido;}
		public String getIDFolio(){return idFolio;}
		public String getNomSala(){return nomSala;}
	}
	
	public class IncidenciaArrayAdapter extends ArrayAdapter<Incidencia>{
		public IncidenciaArrayAdapter(Context context, List<Incidencia> objects){
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
	            listItemView = inflater.inflate(android.R.layout.two_line_list_item,parent,false);
	        }
	        
	      //Obteniendo instancias de los text views
	        TextView encabezado = (TextView)listItemView.findViewById(android.R.id.text1);
	        TextView textoInferior = (TextView)listItemView.findViewById(android.R.id.text2);

	        
	      //Obteniendo instancia de la Tarea en la posicion actual
	        Incidencia item = getItem(position);
	        
	        encabezado.setText(item.getPedido()+" - " +item.getNomSala());
	        textoInferior.setText(item.getIDFolio());
	        
	        //Devolver al ListView la fila creada
			return listItemView;	
	}
}
	



	public void onResume(){
		super.onResume();
		actualizaEstatusTecnico();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		actualizaEstatusTecnico();
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			permissionMultiple();
		}
	}


}
//405