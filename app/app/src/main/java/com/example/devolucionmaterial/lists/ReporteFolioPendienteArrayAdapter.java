package com.example.devolucionmaterial.lists;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.devolucionmaterial.lists.Lista_item;

public class ReporteFolioPendienteArrayAdapter extends ArrayAdapter<Lista_item>{
    String metodo;
    Context context;
    public ReporteFolioPendienteArrayAdapter(Context context, List<Lista_item> objects){
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
            listItemView = inflater.inflate(android.R.layout.simple_list_item_1,parent,false);
        }
        
      //Obteniendo instancias de los text views
        TextView idFolio = (TextView)listItemView.findViewById(android.R.id.text1);
        TextView nombredeSala = (TextView)listItemView.findViewById(android.R.id.text2);
        
      //Obteniendo instancia de la Tarea en la posicion actual
        Lista_item item = (Lista_item)getItem(position);

        
        //Devolver al ListView la fila creada
		return listItemView;		
	}
}
