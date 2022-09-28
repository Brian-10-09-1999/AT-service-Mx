package com.example.devolucionmaterial.activitys.materialesSeccion.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.lists.Lista_item;

import java.util.ArrayList;

/**
 * Created by EDGAR ARANA on 28/11/2017.
 */

public class DevolutionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Lista_item> listDev;
    private Context context;
    private ClickListener clickListener;


    public DevolutionAdapter(Context context, ArrayList<Lista_item> listDev) {
        this.context = context;
        this.listDev = listDev;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_dev_pend_env, parent, false);
        ViewHolderCustom viewHolderCustom = new ViewHolderCustom(itemView);
        return viewHolderCustom;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Lista_item lista_item = listDev.get(position);
        if (holder instanceof ViewHolderCustom) {
            ViewHolderCustom viewHolderCustom = (ViewHolderCustom) holder;

            viewHolderCustom.tvTextoSuperior.setText(lista_item.gettxtSuperior());

            viewHolderCustom.tvTextoInferior.setText(lista_item.gettxtInferior());

           // viewHolderCustom.l.setBackgroundColor(Color.parseColor(lista_item.getColor()));
            viewHolderCustom.cardView.setCardBackgroundColor(Color.parseColor(lista_item.getColor()));
            viewHolderCustom.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.itemClicked(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listDev.size();
    }

    public class ViewHolderCustom extends RecyclerView.ViewHolder {
        FrameLayout root;
        TextView tvTextoSuperior;
        TextView tvTextoInferior;
        LinearLayout l;
        CardView cardView;

        ViewHolderCustom(View view) {
            super(view);
            tvTextoSuperior = (TextView) view.findViewById(R.id.tvSuperior);
            tvTextoInferior = (TextView) view.findViewById(R.id.tvInferior);
            l = (LinearLayout) view.findViewById(R.id.layoutListaDevPendEnv);
            root = (FrameLayout) view.findViewById(R.id.ildpe_fl_root);
            cardView = (CardView) view.findViewById(R.id.card);

        }

    }

    /**
     * inteface que comunica con {@ReporteDeDevoluciones1}
     */
    public interface ClickListener {
        /**
         * @param position es el posicion del objeto que se le hizo click
         */
        void itemClicked(int position);
    }

    public void setClickListener(DevolutionAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
