package com.example.devolucionmaterial.activitys.Rack.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.Rack.RackBaseActivity;
import com.example.devolucionmaterial.activitys.Rack.RackFragments.ListaHardware;
import com.example.devolucionmaterial.activitys.Rack.RackFragments.Listenerfragments;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by gonet_cam on 25/08/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private List<itemModel> mData;
    private LayoutInflater mInflater;//este ayuda a inflar cada fila de nuestro listview
    //o en este caso RecyclerView
    private String [] SelectedItemsp1;
    private String [] SelectedItemsp2;
    private Activity ac;//SE USA PARA CREAR LOS LISTENERFRAGMENT


    public RecyclerAdapter(Context context, List<itemModel> data, Activity ac) {
        this.mData=data;
        this.mInflater=LayoutInflater.from(context);
        SelectedItemsp1=new String[mData.size()];
        SelectedItemsp2=new String[mData.size()];
        this.ac=ac;
    }

    //este metodo se llama cuando se crea el RecyclerView con los items a mostrar en pantalla por lo
    //regular las primeras 12 veces o la cantidad de items mostrables en pantalla
    //
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG," onCreateViewHolder");
        View view = mInflater.inflate(R.layout.list_item1_recview_rack, parent, false);
        MyViewHolder holder =new MyViewHolder(view);
        return holder;
    }

    @Override//este metodo es llamado cada vez que se crean las vistas del Recyclerview
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder"+position);
        itemModel currentObject=mData.get(position);
        holder.setData(currentObject,position);
        holder.setListeners();// esta linea es necesaria para el click sobre los items
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    //la siguiente clase debe implementar el onclick listener para
    //para personalizar el click sobre cada item del recycler view
    //por ende se debe sobreescribir el metodo Onclick()

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView subsala;
        ImageView imgStatus;
        int position;
        itemModel current;
        Context context;

        Spinner spinner1,spinner2;

        public MyViewHolder(View itemView) {
            super(itemView);
            context=itemView.getContext();

            subsala =(TextView) itemView.findViewById(R.id.tv_subsala);
            imgStatus=(ImageView) itemView.findViewById(R.id.img_row_status);

            spinner1=(Spinner) itemView.findViewById(R.id.sp_rack_subsala);
            spinner2=(Spinner) itemView.findViewById(R.id.sp_rack_subsala2);

            String itemss2[] = {"zitro","sala","no hay rack","no existe zona"};
            String itemss1[] = {"0","1","2","3"};

            ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(context,   android.R.layout.simple_spinner_item,itemss1);
            spinnerArrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            spinner1.setAdapter(spinnerArrayAdapter1);

            ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(context,   android.R.layout.simple_spinner_item,itemss2);
            spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            spinner2.setAdapter(spinnerArrayAdapter2);

            spinner1.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                            Log.i("--------------","click spinner1 pos="+posicion+ "   item="+position);
                            SelectedItemsp1[position]=""+posicion;
                        }
                        public void onNothingSelected(AdapterView<?> spn) {    }
                    });


            spinner2.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                            SelectedItemsp2[position]=""+spinner2.getSelectedItem().toString();
                            Log.i("--------------","click spinner2 pos="+posicion+ "   item="+spinner2.getSelectedItem().toString());
                        }
                        public void onNothingSelected(AdapterView<?> spn) {     }
                    });

        }




        public void setData(itemModel current,int position) {
            this.subsala.setText(current.getSubsala());

            switch(current.getRack()){
                case "0":spinner1.setSelection(0);break;
                case "1":spinner1.setSelection(1);break;
                case "2":spinner1.setSelection(2);break;
                case "3":spinner1.setSelection(3);break;
            }


            switch(current.getUbicacionRack()){
                case "zitro":spinner2.setSelection(0);break;
                case "sala":spinner2.setSelection(1);break;
                case "no hay rack":spinner2.setSelection(2);break;
                case "no existe zona":spinner2.setSelection(3);break;
            }

            if(current.getStatus().equals("1")){this.imgStatus.setImageResource(R.drawable.icon_ok);}
            else{ this.imgStatus.setImageResource(R.drawable.icon_no_ok);}

            this.position=position;
            this.current=current;

        }

        //los siguientes metodos sn para implemetar el clock sobre cad item
        public void setListeners(){
            //spinner1.setOnClickListener(MyViewHolder.this);
            //spinner2.setOnClickListener(MyViewHolder.this);
            imgStatus.setOnClickListener(MyViewHolder.this);
            subsala.setOnClickListener(MyViewHolder.this);
        }

        @Override
        public void onClick(View v) {
       //  Log.i(TAG,"onclick antes de operacion a posicion: "+position+" Size:"+mData.size() );
            switch (v.getId()){
                case R.id.sp_rack_subsala:
                    //removeItem(position);
                    Log.i("--------------","click spinner1");
                    break;
                case R.id.sp_rack_subsala2:
                    //addItem(position,current);
                    Log.i("--------------","click spinner2");
                    break;
                case R.id.img_row_status:
                    Log.i("--------------","click imagen");
                   break;

                case R.id.tv_subsala:
                    Log.i("--------------","click tv subsala");

                   // ListaHardware ls =new ListaHardware();
                    //ls.showSubsala(position);

                    Listenerfragments myListener = (Listenerfragments) ac;
                    myListener.nextView(3, current.getSubsala());
                    break;
            }

          //  Log.i(TAG,"Onclick after operation Size"+mData.size()+"\n\n"+mData.toString());
        }

    }




 public String [] getSp1(){return SelectedItemsp1;}

 public String [] getSp2(){return SelectedItemsp2;}



}
