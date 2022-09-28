package com.example.devolucionmaterial.lists;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.devolucionmaterial.FolioPendiente;
import com.example.devolucionmaterial.R;

public class FolioPendienteArrayAdapter extends ArrayAdapter<FolioPendiente> {
    String metodo;
    Context context;
    public FolioPendienteArrayAdapter(Context context, List<FolioPendiente> objects){
		super(context, 0, objects);
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
            listItemView = inflater.inflate(R.layout.item_folios_pendientes,parent,false);
        }
        
      //Obteniendo instancias de los text views
        TextView idFolio = (TextView)listItemView.findViewById(R.id.tvEncabezado);
        TextView nombredeSala = (TextView)listItemView.findViewById(R.id.tvTextInf);
        TextView nombreFalla = (TextView)listItemView.findViewById(R.id.tvTextFalla);
        LinearLayout background = (LinearLayout)listItemView.findViewById(R.id.llFolioPend);

        
      //Obteniendo instancia de la Tarea en la posicion actual
        FolioPendiente item = getItem(position);
        if(item.getEstatus().equals("1")){
            background.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        if(item.getEstatus().equals("74")){
            background.setBackgroundColor(Color.parseColor("#ff0000"));
        }
        if(item.getEstatus().equals("75")){
            background.setBackgroundColor(Color.parseColor("#00FA9A"));
        }
        nombreFalla.setText(item.getFalla());
        idFolio.setText(item.getIdFolio());
        nombredeSala.setText(item.getNombreSala());
        
        //Devolver al ListView la fila creada
		return listItemView;		
	}

}
