package com.example.devolucionmaterial.activitys.viaticos;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.devolucionmaterial.R;

import org.json.JSONArray;

//import static com.example.devolucionmaterial.R2.id.actividad;
//import static com.example.devolucionmaterial.R2.id.ticket;

/**
 * Created by EDGAR ARANA on 19/02/2018.
 */

public class CustomListAdaperResum extends ArrayAdapter<String> {


    private final Activity context;
    private String[] monto;
    private String[] propina;
    private String[] total;
    private String[] concepto;

    private String[] fechaConsumo;
    private String[] comentario;
    private String[] tiket;
    private String[]  pdf;
    private String[]  xml;
    private String[]  isExcedente;
    private String[]  excedenteMonto;
    private String[]  excedentePropina;
   // private JSONArray[] jsonArrayFiles;



    public CustomListAdaperResum(Activity context, String[] monto, String[] propina, String[] total, String[] concepto, String[] comentario,String[] tiket
    ,String[]pdf,String[]xml,String[] fechaConsumo,String[] isexcedente,String[] excedentemonto,String[] excedentepropina) {
        super(context, R.layout.layout_viaticos_resume_list, monto);
        // TODO Auto-generated constructor stub

        this.context=context;

        this.monto=monto;
        this.propina=propina;
        this.total=total;
        this.concepto=concepto;

        this.comentario=comentario;
        this.tiket=tiket;
        this.pdf=pdf;
        this.xml=xml;
        this.fechaConsumo=fechaConsumo;
        this.isExcedente=isexcedente;
        this.excedenteMonto=excedentemonto;
        this.excedentePropina=excedentepropina;
      //  this.jsonArrayFiles= jsonArrayFiles;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.layout_viaticos_resume_list, null,true);

        TextView conc = (TextView) rowView.findViewById(R.id.tv_re_concepto);
        TextView fechConsum = (TextView) rowView.findViewById(R.id.tv_fecha_consumo);
        TextView mont = (TextView) rowView.findViewById(R.id.tv_re_monto);    TextView mont2 = (TextView) rowView.findViewById(R.id.tv_re_monto2);
        TextView propi = (TextView) rowView.findViewById(R.id.tv_re_propina); TextView propi2 = (TextView) rowView.findViewById(R.id.tv_re_propina2);
        TextView tota = (TextView) rowView.findViewById(R.id.tv_re_total);    TextView tota2 = (TextView) rowView.findViewById(R.id.tv_re_total2);

        TextView comen = (TextView) rowView.findViewById(R.id.tv_re_comentario);

        ImageView imConcepto=(ImageView) rowView.findViewById(R.id.im_viaticos_list_concepto);
        ImageView imTicket=(ImageView) rowView.findViewById(R.id.image_tiket);
        ImageView imPdf=(ImageView) rowView.findViewById(R.id.image_pdf);
        ImageView imXml=(ImageView) rowView.findViewById(R.id.image_xml);


        TextView tvfilesfoto = (TextView) rowView.findViewById(R.id.tv_filesfoto);
        TextView tvfilespdf = (TextView) rowView.findViewById(R.id.tv_filespdf);
        TextView tvfilesxml = (TextView) rowView.findViewById(R.id.tv_filesxml);



        /*


        try{

            String filesfoto="",filespdf="",filesxml="";

            for (int i=0;i<jsonArrayFiles[position].length();i++){

                String file=jsonArrayFiles[position].getString(i);
                if(file.contains(".xml") ||file.contains(".XML")||file.contains(".Xml")  ){filesxml=filesxml+ file; };
                if(file.contains(".pdf") ||file.contains(".PDF")||file.contains(".Pdf")  ){filespdf=filespdf+ file; };
                if(file.contains(".JPG") ||file.contains(".JPEG")||file.contains(".jpg")||file.contains(".png")  ){filesfoto=filespdf+ file; };
            }

            tvfilesfoto.setText(filesfoto);
            tvfilespdf.setText(filespdf);
            tvfilesxml.setText(filesxml);

        }

        catch(Exception e){


        }

        */



        if(concepto[position].equals("....")){

        }
        if(concepto[position].equals("Desayuno")){
            imConcepto.setImageResource(R.drawable.ico_desayuno_great);
        }

        if(concepto[position].equals("Comida")){
            imConcepto.setImageResource(R.drawable.ico_comida_great);
        }
        if(concepto[position].equals("Cena")){
            imConcepto.setImageResource(R.drawable.ico_cena_great);
        }
        if(concepto[position].equals("Bebidas o Alimentos")){
            imConcepto.setImageResource(R.drawable.ico_bebidas_alimentos_great);
        }

        if(concepto[position].equals("Taxi")){
            imConcepto.setImageResource(R.drawable.ico_taxi_great);
        }
        if(concepto[position].equals("Boleto Autobus")){
            imConcepto.setImageResource(R.drawable.ico_ticketbus_great);
        }
        if(concepto[position].equals("Deposito")){
            imConcepto.setImageResource(R.drawable.ic0_deposito_great);
        }
        if(concepto[position].contains("Otros")){
            imConcepto.setImageResource(R.drawable.ico_otros_great);
            conc.setText(concepto[position]);
        }

        if(concepto[position].equals("Desayuno - Comida")){
            imConcepto.setImageResource(R.drawable.ico_desayuno_comida);
        }
        if(concepto[position].equals("Comida - Cena")){
            imConcepto.setImageResource(R.drawable.icon_comida_cena);
        }
        if(concepto[position].equals("Cena - Desayuno")){
            imConcepto.setImageResource(R.drawable.icon_cena_desayuno);
        }






        if(tiket[position].equals("1")){
            imTicket.setImageResource(R.drawable.ico_image1);
        }
        if(pdf[position].equals("1")){
            imPdf.setImageResource(R.drawable.ico_pdf1);
        }
        if(xml[position].equals("1")){
            imXml.setImageResource(R.drawable.ico_xml1);
        }

        //conc.setText(concepto[position]);
        conc.setText("");
        fechConsum.setText(fechaConsumo[position]);
        mont.setText("monto=$"+monto[position]);
        propi.setText("propina=$"+propina[position]);
        tota.setText("Total=$"+String.format("%.2f",Double.parseDouble(total[position])));
        comen.setText(comentario[position]);

        if(isExcedente[position].equals("1")){
            mont2.setTextColor(Color.RED);
            propi2.setTextColor(Color.RED);
            tota2.setTextColor(Color.RED);
            mont2.setText("exc.=$"+excedenteMonto[position]);
            propi2.setText("propina=$"+excedentePropina[position]);
            double total=Double.parseDouble(excedenteMonto[position])+Double.parseDouble(excedentePropina[position]);
            tota2.setText("Total=$"+String.format("%.2f",total));
        }
        else{
            mont2.setText("");
            propi2.setText("");
            tota2.setText("");
        }

        //varibles para los archivos

        /*
        if(position==9){
            Animation animation = null;
            animation = AnimationUtils.loadAnimation(context, R.anim.shake);
            animation.setDuration(500);
            rowView.startAnimation(animation);
            animation = null;
        }
        else{ Animation animation = null;
            animation = AnimationUtils.loadAnimation(context, R.anim.left_in);
            animation.setDuration(500);
            rowView.startAnimation(animation);
            animation = null;}
        */



        return rowView;

    }




}
