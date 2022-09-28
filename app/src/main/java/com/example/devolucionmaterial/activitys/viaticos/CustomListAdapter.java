package com.example.devolucionmaterial.activitys.viaticos;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.devolucionmaterial.R;

/**
 * Created by EDGAR ARANA on 16/02/2018.
 */

public class CustomListAdapter  extends ArrayAdapter<String> {
    private final Activity context;
    private  int[] idviaje;
    private  String[] actividad;
    private  String[] motivo;
    private  String[] origen;
    private  String[] destino;
    private  String[] inicio;
    private  String[] termina;
    private  int[] status;
    private int verde=Color.argb(255,28,244,111);
    private int rojo=Color.argb(255,255,85,85);

    public CustomListAdapter(Activity context,int[] idviaje, String[] actividad, String[] motivo, String[] origen, String[] destino, String[] inicio, String[] termina,int[] status) {
        super(context, R.layout.layout_viaticos_viajes_list, actividad);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.idviaje=idviaje;
        this.actividad=actividad;
        this.motivo=motivo;
        this.origen=origen;
        this.destino=destino;
        this.inicio=inicio;
        this.termina=termina;
        this.status=status;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.layout_viaticos_viajes_list, null,true);


        TextView numSol = (TextView) rowView.findViewById(R.id.num_sol);
        TextView act = (TextView) rowView.findViewById(R.id.actividad);
        TextView mot = (TextView) rowView.findViewById(R.id.motivo);
        TextView ori = (TextView) rowView.findViewById(R.id.origen);
        TextView des = (TextView) rowView.findViewById(R.id.destino);
        TextView ini = (TextView) rowView.findViewById(R.id.inicio);
        TextView ter = (TextView) rowView.findViewById(R.id.termina);


        if(status[position]==1){
            rowView.setBackgroundResource(R.drawable.custom_item_list_viaticos2);
            numSol.setBackgroundColor(verde);
        }



        else if(status[position]==2){
            rowView.setBackgroundResource(R.drawable.custom_item_list_viaticos);
            numSol.setBackgroundColor(rojo);
        }

        numSol.setText(" #"+idviaje[position]+" ");
        act.setText(actividad[position]);
        mot.setText(motivo[position]);
        ori.setText(origen[position]);
        des.setText(destino[position]);
        ini.setText(inicio[position]);
        ter.setText(termina[position]);

        Animation animation = null;
        animation = AnimationUtils.loadAnimation(context, R.anim.wave);
        animation.setDuration(500);
        rowView.startAnimation(animation);
        animation = null;

        return rowView;

    }

}
