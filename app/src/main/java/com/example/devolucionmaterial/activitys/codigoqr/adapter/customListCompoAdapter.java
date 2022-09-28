package com.example.devolucionmaterial.activitys.codigoqr.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.codigoqr.model.Componente;

import java.util.ArrayList;

/**
 * Created by EDGAR ARANA on 19/06/2018.
 */

public class customListCompoAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<Componente> lComponentes;
    private  String[] itemCheck;



    public customListCompoAdapter (Activity context,  ArrayList<Componente> lComponentes,String[] itemCheck ){
        super(context, R.layout.item_component_qr,itemCheck);
        this.context=context;
        this.lComponentes=lComponentes;
        this.itemCheck=itemCheck;
    }


    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.item_component_qr, null,true);

        TextView tvName = (TextView) rowView.findViewById(R.id.icq_tv_name);
        TextView tvCode = (TextView) rowView.findViewById(R.id.icq_tv_code);
        ImageView imChecked=(ImageView) rowView.findViewById(R.id.im_checked_item);

        Componente c = lComponentes.get(position);
        tvName.setText(c.nombre);
        tvCode.setText(c.codigo);


        if(itemCheck[position]!=null & itemCheck[position]=="1"){

           // imChecked.setImageDrawable(null);
           // imChecked.setBackgroundResource(R.drawable.icon_checked);
            imChecked.setImageResource(R.drawable.icon_checked);
        }
        else{imChecked.setImageResource(R.drawable.ic_qrn_icon);}

        return rowView;

    }


}
