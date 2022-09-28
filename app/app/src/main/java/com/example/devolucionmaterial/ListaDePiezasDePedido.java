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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

public class ListaDePiezasDePedido extends Activity{
	String metodo;
	Context context;
	private ListView lvPiezasPedido;
    private ArrayAdapter<PiezaPedido> adaptador;
	private ProgressDialog  pDialog;
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
		setupListaDePiezasDePedido();
	}
	
	private void setupListaDePiezasDePedido(){
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
			  pDialog = new ProgressDialog(ListaDePiezasDePedido.this);
			  pDialog.setCancelable(false);
			  pDialog.show();
			  pDialog.setContentView(R.layout.custom_progressdialog);
		        

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
            MenuInicialTecnico.iniciaMenuEstatusTec(ListaDePiezasDePedido.this);
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
//414