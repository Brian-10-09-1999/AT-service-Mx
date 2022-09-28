package com.example.devolucionmaterial.lists;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.devolucionmaterial.R;

import java.util.ArrayList;

public class ListViewAdapterSpinerRegiones extends BaseAdapter {
    private ArrayList<ListViewItem> data = new ArrayList<ListViewItem>();
    private Context context;

    public ListViewAdapterSpinerRegiones(Context context, ArrayList<ListViewItem> data) {
        this.context = context;
        this.data = data;
        Log.e("set", String.valueOf(data.size()));
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /* static class ViewHolder{
            TextView id;
            TextView nombre;
            int position;
        }*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.layout_regiones_id_nombre, null);
        Log.e("position", String.valueOf(position));
            ListViewItem item = data.get(position);
            TextView id;
            TextView nombre;
            id = (TextView) convertView.findViewById(R.id.id_spiner_id);
            nombre = (TextView) convertView.findViewById(R.id.id_spiner_nombre);
            id.setText(String.valueOf(item.getId()));
            nombre.setText(item.getNombre());


       /* convertView.setTag(holder);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_regiones_id_nombre, null);
            holder = new ViewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.id_spiner_id);
            holder.nombre = (TextView) convertView.findViewById(R.id.id_spiner_nombre);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.position = position;
        holder.id.setText(String.valueOf(item.getId()));
        holder.nombre.setText(item.getNombre());*/
        return convertView;
    }
}
