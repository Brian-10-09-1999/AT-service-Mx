package com.example.devolucionmaterial.dialogs;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

public class DialogoOpcionesEstEnServ extends Activity implements OnItemSelectedListener{
	String metodo;
	Context context;
	private ListView lvOpcionesEstEnServ;
	private List<Sala> listaDeSalas;
	private ArrayAdapter<Sala> adaptador;
	private BDmanager manager;
	private static int  CN_REGION_ID = 3;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.dialogo_estatus_en_servicio);
		context = this;
		manager = new BDmanager(context);
		metodo = "Dialogo()";
		//if ("SI".equals(BDVarGlo.getVarGlo(context, "MUESTRA_DIALOGO_LISTA_REGIONES"))){
			setupDialogoOpcionesEst();
		//}else{
		//	DialogoOpcionesEstEnServ.this.finish();
		//}
	}
	private void setupDialogoOpcionesEst(){
		Cursor cursorEstTec = manager.cargarCursorEstatusTec();
		cursorEstTec.moveToFirst();
		Cursor fila = manager.consulta("select officeID,nombre from csala where regionidfk= '"+cursorEstTec.getString(CN_REGION_ID)+"' ORDER BY nombre");
		lvOpcionesEstEnServ = (ListView)findViewById(R.id.lvOpcionesEstEnServ);
		lvOpcionesEstEnServ.setOnItemClickListener(selectedItem);
		listaDeSalas = new ArrayList<Sala>();
		if(cursorEstTec.getInt(CN_REGION_ID) == 59){
			listaDeSalas.add(new Sala(manager.consulta("select nombre from csala where officeID = 807", ""), "807"));//se agrega la sala de officina aqui directamente para los usuarios de region 59 DFZ2
		}
		for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
			listaDeSalas.add(new Sala(fila.getString(1), fila.getString(0)));
	    }
		adaptador = new SalaArrayAdapter(DialogoOpcionesEstEnServ.this, listaDeSalas);
        lvOpcionesEstEnServ.setAdapter(adaptador);
		fila.close();
	}
	
	
	public OnItemClickListener selectedItem = new OnItemClickListener(){
	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
	        metodo = "OnItemClickListener().selectedItem";
			Sala item = (Sala)listaDeSalas.get(posicion);
			MensajeEnConsola.log(context, metodo, "OFFICE ID -> "+item.getOfficeID());
	    	BDVarGlo.setVarGlo(context, "INFO_USUARIO_OFFICE_ID", item.getOfficeID());

			Intent databack = new Intent();
			databack.putExtra("officeidx", item.getOfficeID());
			manager.actualizarOfficeid(item.getOfficeID());
			setResult(RESULT_OK, databack);
			DialogoOpcionesEstEnServ.this.finish();
	    }
	};



	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}
	
	public class Sala{
		private String nombreSala, officeID;
		
		public Sala(String nombreSala, String officeID){
			this.nombreSala = nombreSala;
			this.officeID = officeID;
		}
		
		public String getNombreDeSala(){return nombreSala;}
		public String getOfficeID(){return officeID;}
	}
	
	public class SalaArrayAdapter  extends ArrayAdapter<Sala>{
		public SalaArrayAdapter(Context context, List<Sala> objects){
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
	        TextView nombresala = (TextView)listItemView.findViewById(android.R.id.text1);
	        nombresala.setTextColor(Color.parseColor("#000000"));
	        Sala item = (Sala)getItem(position);

	        nombresala.setText(item.getNombreDeSala());

	        
	        //Devolver al ListView la fila creada
			return listItemView;	
	}
}
	
}
