package com.example.devolucionmaterial;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

public class PedidosPorIncidencia extends Activity{
	String metodo;
	Context context;
	private ListView lvfoliosPendientes;
    private String alias;
    private String password,jsonResult;
    private List<Incidencia> listaFoliospendientes;
	private ArrayAdapter<Incidencia> adaptador;
    private ProgressDialog  pDialog;
    private Menu mMenu;
	private BDmanager manager;
	private static String TAG = "DebugPedidosPorIncidencia";
	private static String docPHPgetIncidenciasConPedido= "getIncidenciasConPedido.php?";
	private int codigoHTTP=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folios_pend);
		context = this;
		setupPedidosPorIncidencia();
	}
	
	private void setupPedidosPorIncidencia(){
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
		        pDialog = new ProgressDialog(PedidosPorIncidencia.this);
			  pDialog.setCancelable(false);
			  pDialog.show();
			  pDialog.setContentView(R.layout.custom_progressdialog);
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
            MenuInicialTecnico.iniciaMenuEstatusTec(PedidosPorIncidencia.this);
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
//405