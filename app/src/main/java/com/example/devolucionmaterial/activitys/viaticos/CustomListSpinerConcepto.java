package com.example.devolucionmaterial.activitys.viaticos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.devolucionmaterial.R;




/**
 * Created by EDGAR ARANA on 11/21/2018.
 */




public class CustomListSpinerConcepto extends BaseAdapter {


    private Context context;
    private String[] concepto;

    private LayoutInflater inflater;


    public  CustomListSpinerConcepto(Context context, String[] concepto){


        this.concepto=concepto;
        this.context=context;
        inflater=LayoutInflater.from(context);

    }


    public View getView(int position, View view, ViewGroup parent) {

        View rowView=inflater.inflate(R.layout.layout_viaticos_concepto_spinner, null,true);

        ImageView imConcepto=(ImageView) rowView.findViewById(R.id.im_viaticos_spconcepto);
        TextView tvConcepto= (TextView) rowView.findViewById(R.id.tv_viaticos_spconcepto);

        tvConcepto.setText(concepto[position]);

        if(concepto[position].equals("....")){

        }
        if(concepto[position].equals("Desayuno")){
            imConcepto.setImageResource(R.drawable.ico_desayuno_small);
        }

        if(concepto[position].equals("Comida")){
            imConcepto.setImageResource(R.drawable.ico_comida_small);
        }
        if(concepto[position].equals("Cena")){
            imConcepto.setImageResource(R.drawable.ico_cena_small);
        }
        if(concepto[position].equals("Bebidas o Alimentos")){
            imConcepto.setImageResource(R.drawable.ico_bebidas_alimentos_small);
        }

        if(concepto[position].equals("Taxi")){
            imConcepto.setImageResource(R.drawable.ico_taxi_small);
        }
        if(concepto[position].equals("Boleto de Autobus")){
            imConcepto.setImageResource(R.drawable.ico_ticketbus_small);
        }
        if(concepto[position].equals("Deposito")){
            imConcepto.setImageResource(R.drawable.ico_deposito_small);
        }
        if(concepto[position].equals("Otros")){
            imConcepto.setImageResource(R.drawable.ico_otros_small);
        }





        return rowView;

    }

    @Override
    public int getCount() {
        return concepto.length;
    }


    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }



}
