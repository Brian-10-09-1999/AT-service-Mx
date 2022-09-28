package com.example.devolucionmaterial.lists;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.devolucionmaterial.FolioPendiente;
import com.example.devolucionmaterial.R;

public class SalaPorRegionArrayAdapter extends ArrayAdapter<FolioPendiente> {
    String metodo;
    Context context;

    public SalaPorRegionArrayAdapter(Context context, List<FolioPendiente> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con two_line_list_item.xml
            listItemView = inflater.inflate(R.layout.item_salas_folios, parent, false);
        }

        //Obteniendo instancias de los text views
        TextView nomSala = (TextView) listItemView.findViewById(R.id.isf_sala);
        TextView numsFolios = (TextView) listItemView.findViewById(R.id.isf_nums_folios);

        //Obteniendo instancia de la Tarea en la posicion actual
        FolioPendiente item = (FolioPendiente) getItem(position);

        nomSala.setText(item.getNombreSala());
        numsFolios.setText(item.getNumsFolio());

        //Devolver al ListView la fila creada
        return listItemView;
    }

}
