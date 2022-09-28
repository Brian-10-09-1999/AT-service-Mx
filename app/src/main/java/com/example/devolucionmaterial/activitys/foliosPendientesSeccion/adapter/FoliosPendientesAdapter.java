package com.example.devolucionmaterial.activitys.foliosPendientesSeccion.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.devolucionmaterial.FolioPendiente;
import  com.example.devolucionmaterial.R;

import java.util.ArrayList;

/**
 * Created by EDGAR ARANA on 14/12/2017.
 */

public class FoliosPendientesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<FolioPendiente> folioPendienteArrayList;
    private Context context;

    public FoliosPendientesAdapter(ArrayList<FolioPendiente> folioPendienteArrayList, Context context) {
        this.context = context;
        this.folioPendienteArrayList = folioPendienteArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=  LayoutInflater.from(context );
        // TODO: 14/12/2017 cambiar el layout
        View view =layoutInflater.inflate(R.layout.layout_regiones_id_nombre, parent);

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return folioPendienteArrayList.size();
    }

    class VHFoliosPedientes extends RecyclerView.ViewHolder {
        public VHFoliosPedientes(View itemView) {
            super(itemView);
        }
    }


}
