package com.example.devolucionmaterial.activitys.codigoqr.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.devolucionmaterial.R;


/**
 * Created by EDGAR ARANA on 11/14/2018.
 */

public class CustomListCompoSigns   extends ArrayAdapter<String> {


    private final Activity context;
    private String[] serie, codigo,clave,refaccion;


    public CustomListCompoSigns (Activity context, String[]serie,String[]codigo,String[] clave,String[] refaccion){
        super(context, R.layout.custom_item_signs,serie);
        this.context=context;
        this.codigo=codigo;
        this.serie=serie;
        this.clave=clave;
        this.refaccion=refaccion;


    }



    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.custom_item_signs, null,true);
        //
        // Log.i("info-----------------","customListCompoSigns codigo:"+codigo[position]);
        TextView tvSerie = (TextView) rowView.findViewById(R.id.tv_cis_serie);
        TextView tvCode =  (TextView) rowView.findViewById(R.id.tv_cis_codigo);
        TextView tvClave = (TextView) rowView.findViewById(R.id.tv_cis_clave);
        TextView tvRefaccion = (TextView) rowView.findViewById(R.id.tv_cis_refaccion);

        tvCode.setText((position+1)+") QR:"+codigo[position]);
        tvSerie.setText(serie[position]);
        tvClave.setText(clave[position]);
        tvRefaccion.setText(refaccion[position]);

        return rowView;
    }









}
