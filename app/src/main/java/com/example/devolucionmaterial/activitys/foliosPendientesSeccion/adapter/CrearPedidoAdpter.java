package com.example.devolucionmaterial.activitys.foliosPendientesSeccion.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.beans.PedidosGuardados;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.data_base.DBCrearPedido;

import java.util.List;

/**
 * Created by EDGAR ARANA on 08/06/2017.
 */

public class CrearPedidoAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PedidosGuardados> data;
    private Context context;
    private ClickListener clickListener;
    private DBCrearPedido dbCrearPedido;

    private final ViewBinderHelper binderHelper = new ViewBinderHelper();


    public CrearPedidoAdpter(Context context, List<PedidosGuardados> moviesList) {
        this.data = moviesList;
        this.context = context;
        dbCrearPedido = new DBCrearPedido(context);
        dbCrearPedido.open();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedidos_guardados, parent, false);


        return new CrearPedidoAdpter.ViewHolderCustom(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        PedidosGuardados pedidosGuardados = data.get(position);
        final CrearPedidoAdpter.ViewHolderCustom viewHolderCustom = (CrearPedidoAdpter.ViewHolderCustom) holder;
        viewHolderCustom.tvSala.setText(getNombreSala(pedidosGuardados.getSala()));
        viewHolderCustom.tvmMaquina.setText(pedidosGuardados.getMaquina());
        viewHolderCustom.tvComponente.setText(pedidosGuardados.getComponente());


        if (dbCrearPedido.getStatus(pedidosGuardados.getId()) == 1) {
            viewHolderCustom.tvEstastatus.setText("Enviado");
            viewHolderCustom.tvEstastatus.setTextColor(context.getResources().getColor(R.color.accentColor));
            viewHolderCustom.tvCancel.setVisibility(View.GONE);
            viewHolderCustom.tvEnviar.setVisibility(View.GONE);
            viewHolderCustom.tvSinOpciones.setVisibility(View.VISIBLE);
            viewHolderCustom.swipeLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
                @Override
                public void onClosed(SwipeRevealLayout view) {

                }

                @Override
                public void onOpened(SwipeRevealLayout view) {
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewHolderCustom.swipeLayout.close(true);
                        }
                    }, 1000);

                }

                @Override
                public void onSlide(SwipeRevealLayout view, float slideOffset) {

                }
            });

        }
        // TODO: 18/12/2017 estatus 2 es cuando se cancela el pedido
        else if(dbCrearPedido.getStatus(pedidosGuardados.getId()) == 2){
            viewHolderCustom.tvEstastatus.setText("Cancelado");
            viewHolderCustom.tvCancel.setVisibility(View.GONE);
            viewHolderCustom.tvEnviar.setVisibility(View.GONE);
            viewHolderCustom.tvSinOpciones.setVisibility(View.VISIBLE);
            viewHolderCustom.tvSinOpciones.setText("Pedido cancelado");
        }else {
            viewHolderCustom.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolderCustom.swipeLayout.open(true);
                }
            });
            viewHolderCustom.tvEstastatus.setText("Sin enviar");
            viewHolderCustom.tvEstastatus.setTextColor(context.getResources().getColor(R.color.red));
            //1 enviar
            viewHolderCustom.tvEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.itemClicked(position, viewHolderCustom,1);
                    }
                }
            });
            //2 cancelar
            viewHolderCustom.tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.itemClicked(position, viewHolderCustom,2);
                    }
                }
            });
        }


    }


    public class ViewHolderCustom extends RecyclerView.ViewHolder {

        public FrameLayout main;
        public TextView tvSala, tvmMaquina, tvComponente, tvEstastatus, tvCancel, tvSinOpciones, tvEnviar;
        private SwipeRevealLayout swipeLayout;
        private LinearLayout llRoot;


        ViewHolderCustom(View view) {
            super(view);
            main = (FrameLayout) view.findViewById(R.id.almf_root);
            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            tvSala = (TextView) view.findViewById(R.id.ipg_txt_sala);
            tvmMaquina = (TextView) view.findViewById(R.id.ipg_txt_maquina);
            tvComponente = (TextView) view.findViewById(R.id.ipg_txt_componente);
            tvEstastatus = (TextView) view.findViewById(R.id.ipg_txt_estatus);
            tvCancel = (TextView) view.findViewById(R.id.ipg_tv_cancel);
            tvSinOpciones = (TextView) view.findViewById(R.id.ipg_tv_sin_opciones);
            tvEnviar = (TextView) view.findViewById(R.id.ipg_tv_enviar);
            llRoot = (LinearLayout) view.findViewById(R.id.roor_ll);

        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface ClickListener {
        void itemClicked(int position, ViewHolderCustom viewHolder, int status);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private String getNombreSala(int sala) {
        BDmanager manager;
        manager = new BDmanager(context);
        Log.e("sala", String.valueOf(sala));
        String nombre = "";
        Cursor fila = manager.consulta("select salaid , nombre from csala where salaid= '" + sala + "' ORDER BY nombre");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
            // arrSalasID.add(fila.getInt(0));
            nombre = fila.getString(1);

        }

        return nombre;

    }


}