package com.example.devolucionmaterial;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.*;
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
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.activitys.pedidosSeccion.ListaDePiezasDePedido;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.thanosfisherman.mayi.PermissionBean;

public class PedidosDeSala extends BaseActivity{
	String metodo;
	Context context;
	private ListView lvPedidosDeSala;
	private List<Pedidos> listaPedidosDeSala;
	private ArrayAdapter<Pedidos> adaptador;
	private String salaID;
	private Menu mMenu;
	private BDmanager manager;
	private static String docPHPgetPedidosDeSala = "getPedidosDeSala.php?";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folios_pend);
		initToolbar("Pedido de sala", true , true);
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
		TextView tvNomUsuario = (TextView) findViewById(R.id.tvNombreusuario2);
		lvPedidosDeSala = (ListView)findViewById(R.id.lvFoliosPendientes);
		LinearLayout llMenu = (LinearLayout) findViewById(R.id.llmenu);
		lvPedidosDeSala.setOnItemClickListener(selectedItem);
		Intent intReceptor = this.getIntent();
		Bundle bndReceptor = intReceptor.getExtras();
		salaID= bndReceptor.getString("salaIDx");
		String nombre = bndReceptor.getString("nombrex");
		tvNomUsuario.setText(nombre);
		
		llMenu.setVisibility(View.GONE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lvPedidosDeSala.setLayoutParams(params);
		new Progreso().execute();
	}
	
	public OnItemClickListener selectedItem = new OnItemClickListener(){
	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
	    	Pedidos item = listaPedidosDeSala.get(posicion);
	    	Intent intentMenuDevolucion = new Intent(context, ListaDePiezasDePedido.class);
			Bundle bndMyBundle = new Bundle();
			try{
				bndMyBundle.putString("pedidox", (item.getPedido()));
			}catch (Throwable ex){
				bndMyBundle.putInt("usuario", 0);
			}
			intentMenuDevolucion.putExtras(bndMyBundle);
			startActivity(intentMenuDevolucion);
	    }
	};
	
	private void pedirPedidosDeSala(){
        metodo = "pedirPedidosDeSala()";
		listaPedidosDeSala = new ArrayList<Pedidos>();
	    MensajeEnConsola.log(context, metodo, MainActivity.url+docPHPgetPedidosDeSala+"salaidx="+salaID);
	    try{
	    	JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url+docPHPgetPedidosDeSala+"salaidx="+salaID);
			assert object != null;
			JSONArray jsonArray = object.optJSONArray("pedidos");
			for (int i = 0; i < jsonArray.length(); i++) {
	    		JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
    	        listaPedidosDeSala.add(new Pedidos(jsonArrayChild.optString("pedido"),jsonArrayChild.optString("origen"),
    	        		jsonArrayChild.optString("tipoPedido")));
				MensajeEnConsola.log(context, metodo, "pedido="+jsonArrayChild.optString("pedido"));
	    	}
	    	adaptador = new PedidoArrayAdapter(context, listaPedidosDeSala);
	    	Pedidos item = listaPedidosDeSala.get(0);
	    	if(item.getPedido().equals("0")){
				Alert.Error(context, metodo, getString(R.string.sinRegistros), getString(R.string.notPedReg));
				lvPedidosDeSala.setVisibility(View.GONE);
			}
	    }catch(JSONException e) {
			MensajeEnConsola.log(context, metodo, "JSONException e = "+e.getMessage());
		}catch (Exception e) {
			MensajeEnConsola.log(context, metodo, "Exception e = "+e.getMessage());
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
		protected Void doInBackground(Void... params) {
			pedirPedidosDeSala();
			return null;
		}
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			lvPedidosDeSala.setAdapter(adaptador);
			pDialog.dismiss();
		}
	}
	
	private class Pedidos{
		private String pedido, origen, tipoPedido;
		
		public Pedidos(String pedido, String origen, String tipoPedido){
			this.pedido = pedido;
			this.origen = origen;
			this.tipoPedido = tipoPedido;
		}
		
		public String getPedido(){return pedido;}
		public String getOrigen(){return origen;}
		public String getTipoPedido(){return tipoPedido;}
	}
	
	public class PedidoArrayAdapter extends ArrayAdapter<Pedidos>{
		public PedidoArrayAdapter(Context context, List<Pedidos> objects){
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
	        TextView pedido = (TextView)listItemView.findViewById(android.R.id.text1);
	        TextView origen = (TextView)listItemView.findViewById(android.R.id.text2);

	        
	      //Obteniendo instancia de la Tarea en la posici�n actual
	        Pedidos item = getItem(position);
	        
	        pedido.setText(item.getPedido());
	        
	        
	        if(item.getOrigen().equals("0")){
	        	origen.setText(getString(R.string.preventivo));
	        }else if(item.getTipoPedido().equals("OS"))
	        	origen.setText(getString(R.string.titOS)+item.getOrigen());
	        else if(item.getTipoPedido().equals("0"))
	        	origen.setText(getString(R.string.titFolio)+item.getOrigen());
	        
	        //Devolver al ListView la fila creada
			return listItemView;	
	}
}
	

	public void onResume(){
		super.onResume();
		actualizaEstatusTecnico();
	}

}
//294