package com.example.devolucionmaterial.activitys.viaticos;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.devolucionmaterial.R;

import java.io.File;

/**
 * Created by EDGAR ARANA on 23/02/2018.
 */

public class CustomListAdapterExplore extends ArrayAdapter<String> {

    private final Activity context;
    private  String[] names;
    private  String[] tipe;


    public CustomListAdapterExplore(Activity context, String[] nameFiles,String[] tipe) {
        super(context, R.layout.text_view_lista_archivos, nameFiles);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.tipe=tipe;
        names=nameFiles;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.text_view_lista_archivos, null,true);
        TextView conc = (TextView) rowView.findViewById(R.id.tv_name_file);
        ImageView imf=(ImageView) rowView.findViewById(R.id.im_type_file);

      Log.i("------------------   -----view","tipe="+tipe[position]);

       if(tipe.length!=0 & tipe[position] != null)
        {
            Log.i("------------------   -----view","tipe="+tipe[position]);
             if(tipe[position].equals("f")){

                 File file= new File(names[position]);
                 String ext=getFileExtension(file);
                 if(ext.equals("xml") || ext.equals("Xml") || ext.equals("XML")){
                     imf.setImageResource(R.drawable.ico_ext_xml);
                 }
                 else if(ext.equals("pdf") || ext.equals("Pdf") || ext.equals("PDF")){
                     imf.setImageResource(R.drawable.ico_ext_pdf);
                 }
                 else if(!ext.equals("xml") || !ext.equals("pdf" ) || !ext.equals("pdf") || !ext.equals("Pdf") || !ext.equals("PDF")){

                     imf.setImageResource(R.drawable.ico_file);
                 }


             }
            if(tipe[position].equals("d")){
            imf.setImageResource(R.drawable.ico_folder);
             }
        }
        if(names[position].equals("...")){

            imf.setImageResource(R.drawable.ico_back_explore);

        }




        conc.setText(names[position]);


        return rowView;

    }


    private String getFileExtension(File file) {
        String name = file.getName();
        try {return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {return "";
        }
    }



}
