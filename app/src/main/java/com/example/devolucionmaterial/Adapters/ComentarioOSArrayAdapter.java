package com.example.devolucionmaterial.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.osSeccion.ReporteActividadOS;
import com.example.devolucionmaterial.beans.ComentarioOS;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by EDGAR ARANA on 12/09/2017.
 */

public class ComentarioOSArrayAdapter extends ArrayAdapter<ComentarioOS> {
    Context context;

    public ComentarioOSArrayAdapter(Context context, List<ComentarioOS> objects) {
        super(context, 0, objects);
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;

        //Obteniendo una instancia del inflater
        //LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Contexto.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;


        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con two_line_list_item.xml
            LayoutInflater inflater = (LayoutInflater) ((Activity) getContext()).getLayoutInflater();
            listItemView = inflater.inflate(R.layout.item_lista_comentarios_os, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.hora = (TextView) listItemView.findViewById(R.id.tvHoraComOS);
            viewHolder.comentarios = (TextView) listItemView.findViewById(R.id.tvComOS);

            viewHolder.imgComentarios = (ImageView) listItemView.findViewById(R.id.imgComOS);
            viewHolder.llComentario = (LinearLayout) listItemView.findViewById(R.id.llComentarioOS);
            //viewHolder.rlInfoComentario = (RelativeLayout) listItemView.findViewById(R.id.rlComentariosOS1);
            //viewHolder.rlbtnGaleria = (RelativeLayout) listItemView.findViewById(R.id.rlComentariosOS2);

            viewHolder.llBackground = (RelativeLayout) listItemView.findViewById(R.id.llItemListaComent);
            viewHolder.entregado = (TextView) listItemView.findViewById(R.id.tvEntregado);
            viewHolder.falla = (TextView) listItemView.findViewById(R.id.tvFallaEntregado);
            viewHolder.imgEntregado = (ImageView) listItemView.findViewById(R.id.imgOk);
            viewHolder.imgFalla = (ImageView) listItemView.findViewById(R.id.imgError);

            // store the holder with the view.
            listItemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }


        //Obteniendo instancia de la Tarea en la posicion actual
        ComentarioOS item = (ComentarioOS) getItem(position);
        if (item != null) {
            //Verga leria
                /*if("VG".equals(item.getHora())){
                    viewHolder.llBackground.setBackgroundColor(Color.argb(0,0,0,0));
					viewHolder.llComentario.setVisibility(View.GONE);
					viewHolder.rlInfoComentario.setVisibility(View.GONE);
					viewHolder.rlbtnGaleria.setVisibility(View.VISIBLE);
				}else{
					viewHolder.llComentario.setVisibility(View.VISIBLE);
					viewHolder.rlInfoComentario.setVisibility(View.VISIBLE);
					viewHolder.rlbtnGaleria.setVisibility(View.GONE);
				}*/
            //Ver imagenes
            if ("".equals(item.getComentario_url())) {
                viewHolder.imgComentarios.setVisibility(View.GONE);
                viewHolder.comentarios.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imgComentarios.setVisibility(View.VISIBLE);
                viewHolder.comentarios.setVisibility(View.GONE);

                Picasso.with(context).load(item.getComentario_url()).placeholder(android.R.drawable.stat_sys_download).resize(90, 90).memoryPolicy(MemoryPolicy.NO_CACHE).into(viewHolder.imgComentarios);
            }
            //Ver solo el titulo en negro
            if (item.getHora().equals("0")) {
                viewHolder.llBackground.setBackgroundColor(Color.parseColor("#000000"));
                viewHolder.falla.setVisibility(View.GONE);
                viewHolder.imgFalla.setVisibility(View.GONE);
                viewHolder.entregado.setVisibility(View.GONE);
                viewHolder.imgEntregado.setVisibility(View.GONE);
                viewHolder.hora.setText("");
                viewHolder.comentarios.setTextColor(Color.parseColor("#ffffff"));
                viewHolder.comentarios.setText(item.getComentario());
            } else {
                viewHolder.hora.setText(item.getHora());
                viewHolder.comentarios.setText(item.getComentario());
                viewHolder.comentarios.setTextColor(Color.parseColor("#000000"));

                if (item.getEstatusDeEntrega().equals(ReporteActividadOS.ESTATUS_PENDIENTE)) {
                    viewHolder.falla.setVisibility(View.VISIBLE);
                    viewHolder.imgFalla.setVisibility(View.VISIBLE);
                    viewHolder.entregado.setVisibility(View.GONE);
                    viewHolder.imgEntregado.setVisibility(View.GONE);
                    viewHolder.llBackground.setBackgroundColor(Color.parseColor("#FA8072"));
                } else {
                    viewHolder.falla.setVisibility(View.GONE);
                    viewHolder.imgFalla.setVisibility(View.GONE);
                    viewHolder.entregado.setVisibility(View.VISIBLE);
                    viewHolder.imgEntregado.setVisibility(View.VISIBLE);
                    viewHolder.llBackground.setBackgroundColor(Color.parseColor("#00FA9A"));
                }
            }
        }

        //Devolver al ListView la fila creada
        return listItemView;
    }

   public static class ViewHolderItem {
        TextView hora;
        TextView comentarios;
        RelativeLayout llBackground;
        TextView entregado;
        TextView falla;
        ImageView imgEntregado;
        ImageView imgFalla;

        ImageView imgComentarios;
        LinearLayout llComentario;
        RelativeLayout rlInfoComentario, rlbtnGaleria;
    }
}