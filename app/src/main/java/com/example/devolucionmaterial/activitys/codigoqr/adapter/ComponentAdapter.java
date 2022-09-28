package com.example.devolucionmaterial.activitys.codigoqr.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.codigoqr.InfoCodigoQRActivity;
import com.example.devolucionmaterial.activitys.codigoqr.model.Componente;

import java.util.ArrayList;

/**
 * Created by EDGAR ARANA on 06/12/2017.
 */

public class ComponentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Componente> lComponentes;
    ClickListener clickListener;
    private  String[] resitem;





    public ComponentAdapter(Context context, ArrayList<Componente> lComponentes,String[] resItem ) {
        this.context = context;
        this.lComponentes = lComponentes;
       if(resItem != null){ this.resitem=resItem;}
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_component_qr, parent, false);

        return new VHComponent(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Componente c = lComponentes.get(position);
        if (holder instanceof VHComponent) {
            VHComponent vhComponent = (VHComponent) holder;
            vhComponent.tvName.setText(c.nombre);
            vhComponent.tvCode.setText(c.codigo);

            if(resitem[position] != null){
                Log.i("-------------------","item:"+resitem[position]+ "   codigo:"+c.codigo+"   position:"+position);
                if(resitem[position].equals("1")){
                    vhComponent.tvCode.setBackgroundResource(R.drawable.custom_item_list_viaticos2);
                    Log.i("-------------------","  position into:"+position);

                }
            }

            vhComponent.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onClick(position);
                    }
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return lComponentes.size();
    }

    class VHComponent extends RecyclerView.ViewHolder {
        FrameLayout root;
        TextView tvName, tvCode;
        LinearLayout lyItem;

        public VHComponent(View itemView) {
            super(itemView);
            root = (FrameLayout) itemView.findViewById(R.id.icq_fl_content);
           // root.setBackgroundColor(0xFF4F8F00);
            lyItem=(LinearLayout) itemView.findViewById(R.id.ly_item);
           // lyItem.setBackgroundResource(R.drawable.custom_item_list_viaticos2);
            tvName = (TextView) itemView.findViewById(R.id.icq_tv_name);
            tvCode = (TextView) itemView.findViewById(R.id.icq_tv_code);
        }

    }


    /**
     * this interface is to commend the activated {@link InfoCodigoQRActivity} one with the adapter
     */
    public interface ClickListener {
        /**
         * @param position is de position the list clicked
         */
        void onClick(int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
