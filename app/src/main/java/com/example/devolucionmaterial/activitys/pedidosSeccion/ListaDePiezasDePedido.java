package com.example.devolucionmaterial.activitys.pedidosSeccion;

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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.thanosfisherman.mayi.PermissionBean;

public class ListaDePiezasDePedido extends BaseActivity{
	String metodo;
	private ListView lvPiezasPedido;
    private ArrayAdapter<PiezaPedido> adaptador;
	private static String TAG = "DebugListaDePiezasDePedido";
	private String pedido,jsonResult;
	private Menu mMenu;
	private BDmanager manager;
	private static String docPHPgetPiezasPedido= "getPiezasPedido.php?";
	private int codigoHTTP=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_piezas_pedido);
		initToolbar("Piezas del Pedido", true , true);

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
        manager = new BDmanager(this);
		lvPiezasPedido = (ListView)findViewById(R.id.lvPiezasPedido);
        Intent intReceptor = this.getIntent();
        Bundle bndReceptor = intReceptor.getExtras();
		pedido = bndReceptor.getString("pedidox");
		barraProgresoFoliosPorSala();
	} 
	
	private void pedirPiezasPedido(){
		metodo = "pedirPiezasPedido()";
        List<PiezaPedido> listaPiezasPedido = new ArrayList<PiezaPedido>();
	    try {
	    	JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url+docPHPgetPiezasPedido+"pedidox="+pedido);
			assert object != null;
	    	JSONArray jsonArray = object.optJSONArray("pedidosxIncidencia");
	    	for (int i = 0; i < jsonArray.length(); i++) {
	    		JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
    	        listaPiezasPedido.add(new PiezaPedido(jsonArrayChild.optString("codigo"),
                        jsonArrayChild.optString("descripcion"),
                        jsonArrayChild.optString("estatusid"),
                        jsonArrayChild.optString("cantidad"),
                        jsonArrayChild.optString("estatusSim")));
	    	}
	    	adaptador = new PiezaPedidoArrayAdapter(this, listaPiezasPedido);
	    	PiezaPedido item =  listaPiezasPedido.get(0);
	    	if(item.getCodigo().equals("0")){
	    		iniciarHiloExcepcion(getString(R.string.sinRegistros), getString(R.string.notRef));
	    		new Thread(
	    	            new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                    runOnUiThread(new Runnable() {
	    	                        @Override
	    	                        public void run() {
	    	            	    		lvPiezasPedido.setVisibility(View.GONE);
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
			final AlertDialog alertDialog = new AlertDialog.Builder(ListaDePiezasDePedido.this).create();
			alertDialog.setTitle(tituloError);
			alertDialog.setMessage(cuerpoError);
			alertDialog.setButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//startActivity(enviarCorreo(MainActivity.this, tituloError, cuerpoError));
				alertDialog.dismiss();
				ListaDePiezasDePedido.this.finish();
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
				pedirPiezasPedido();
		      return null;
		  }

		  protected void onPostExecute(Void result) {
		      super.onPostExecute(result);	
		    	 lvPiezasPedido.setAdapter(adaptador);		
		      pDialog.dismiss();
		  }

		}
	
	private class PiezaPedido{
		private String codigo, descripcion, estatus, cantidad, estatusSimilar;
		
		public PiezaPedido(String codigo, String descripcion, String estatus, String cantidad, 
				String estatusSimilar){
			this.codigo = codigo;
			this.descripcion = descripcion;
			this.estatus = estatus;
			this.cantidad = cantidad;
			this.estatusSimilar = estatusSimilar;
		}
		
		public String getCodigo(){return codigo;}
		public String getDescripcion(){return descripcion;}
		public String getEstatus(){return estatus;}
		public String getCantidad(){return cantidad;}
		public String getEstatusSimilar(){return estatusSimilar;}
	}
	
	public class PiezaPedidoArrayAdapter extends ArrayAdapter<PiezaPedido>{
		public PiezaPedidoArrayAdapter(Context context, List<PiezaPedido> objects){
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
	            listItemView = inflater.inflate(R.layout.item_lista_piezas_pedido,parent,false);
	        }
	        
	      //Obteniendo instancias de los text views
	        TextView codigo = (TextView)listItemView.findViewById(R.id.tvcodPiePed);
	        TextView descripcion = (TextView)listItemView.findViewById(R.id.tvdescPiePed);
	        TextView cantidad = (TextView)listItemView.findViewById(R.id.tvcantPiePed);
	        LinearLayout llPiezaPedido = (LinearLayout)listItemView.findViewById(R.id.llitemPiezasPedido);

	        
	      //Obteniendo instancia de la Tarea en la posicion actual
	        PiezaPedido item = getItem(position);
	        
	        codigo.setText(item.getCodigo());
	        descripcion.setText(item.getDescripcion());
	        cantidad.setText(item.getCantidad());
	        
	        switch (Integer.parseInt(item.getEstatus())) {
			case 29:
				llPiezaPedido.setBackgroundColor(Color.parseColor("#FFFF00"));
				break;
			case 31:
				llPiezaPedido.setBackgroundColor(Color.parseColor("#FF3366"));
				break;
			case 33:
				llPiezaPedido.setBackgroundColor(Color.parseColor("#FF9900"));
				break;
			case 43:
				llPiezaPedido.setBackgroundColor(Color.parseColor("#FFFFFF"));
				break;
			case 47:
				llPiezaPedido.setBackgroundColor(Color.parseColor("#00FFFF"));
				break;
			case 51:
				if(item.getEstatusSimilar().equals("1"))
					llPiezaPedido.setBackgroundColor(Color.parseColor("#00FFFF"));
				else if(item.getEstatusSimilar().equals("0"))
					llPiezaPedido.setBackgroundColor(Color.parseColor("#FFFF00"));
				break;

			default:
				break;
			}
	        
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
//414