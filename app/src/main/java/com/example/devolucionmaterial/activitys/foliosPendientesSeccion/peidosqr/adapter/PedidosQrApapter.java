package com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model.Datos;

import com.example.devolucionmaterial.data_base.dbPedidosQr.DBPedidoQrManger;

import java.util.List;

/**
 * Created by EDGAR ARANA on 22/08/2017.
 */

public class PedidosQrApapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Datos> data;
    private Context context;
    private ClickListener clickListener;
    private DBPedidoQrManger dbPedidoQrManger;


    public PedidosQrApapter(Context context, List<Datos> moviesList) {
        this.data = moviesList;
        this.context = context;
        dbPedidoQrManger = new DBPedidoQrManger(context).open();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido_qr, parent, false);


        return new ViewHolderCustom(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Datos pedidoQ = data.get(position);
        final ViewHolderCustom viewHolderCustom = (ViewHolderCustom) holder;
        viewHolderCustom.tvPedido.setText(pedidoQ.getPedido());
        viewHolderCustom.tvmMaquina.setText(pedidoQ.getCodMaquina());
        viewHolderCustom.tvComponenteA.setText(pedidoQ.getComponenteAnterior());
        viewHolderCustom.tvComponenteN.setText(pedidoQ.getComponenteNuevo());

        int status = dbPedidoQrManger.getStatus(pedidoQ.getId());
        Log.e("status", String.valueOf(status));
        if (status == 4) {
            viewHolderCustom.tvEstastatus.setText("Completo");
            viewHolderCustom.tvEstastatus.setTextColor(context.getResources().getColor(R.color.accentColor));
            viewHolderCustom.btnStatus.setVisibility(View.GONE);
            viewHolderCustom.btnGuia.setVisibility(View.GONE);
        }
        // TODO: 30/10/2017 se agrega el boton de guia como opcional
        if (status == 3) {

            viewHolderCustom.tvEstastatus.setText("Completo");
            viewHolderCustom.tvEstastatus.setTextColor(context.getResources().getColor(R.color.accentColor));
            viewHolderCustom.btnStatus.setVisibility(View.GONE);
            viewHolderCustom.btnGuia.setVisibility(View.VISIBLE);
            viewHolderCustom.btnGuia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.itemClicked(position, viewHolderCustom, 3);
                    }
                }
            });
        }
        if (status == 2) {
            viewHolderCustom.tvEstastatus.setText("Falta devolución");
            viewHolderCustom.tvEstastatus.setTextColor(context.getResources().getColor(R.color.red));
            viewHolderCustom.btnStatus.setVisibility(View.VISIBLE);
            viewHolderCustom.btnStatus.setText("Enviar");
            viewHolderCustom.btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.itemClicked(position, viewHolderCustom, 2);
                    }
                }
            });
        } else if (status == 1) {
            viewHolderCustom.tvEstastatus.setText("En processo de envio");
            viewHolderCustom.tvEstastatus.setTextColor(context.getResources().getColor(R.color.red));
            viewHolderCustom.btnStatus.setVisibility(View.VISIBLE);
            viewHolderCustom.btnStatus.setText("Enviar");
            viewHolderCustom.btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.itemClicked(position, viewHolderCustom, 1);
                    }
                }
            });
        } else if (status == 0) {
            viewHolderCustom.tvEstastatus.setText("Validar componente");
            viewHolderCustom.tvEstastatus.setTextColor(context.getResources().getColor(R.color.yelow));
            viewHolderCustom.btnStatus.setVisibility(View.VISIBLE);
            viewHolderCustom.btnStatus.setText("validar");
            viewHolderCustom.btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.itemClicked(position, viewHolderCustom, 0);
                    }
                }
            });
        }


    }


    public class ViewHolderCustom extends RecyclerView.ViewHolder {

        public FrameLayout main;
        public TextView tvPedido, tvmMaquina, tvComponenteA, tvComponenteN, tvEstastatus;
        public Button btnStatus, btnGuia;


        ViewHolderCustom(View view) {
            super(view);
            main = (FrameLayout) view.findViewById(R.id.ipq_root);

            tvPedido = (TextView) view.findViewById(R.id.ipq_txt_sala);
            tvmMaquina = (TextView) view.findViewById(R.id.ipq_txt_maquina);
            tvComponenteA = (TextView) view.findViewById(R.id.ipq_txt_componentea);
            tvComponenteN = (TextView) view.findViewById(R.id.ipq_txt_componenten);
            tvEstastatus = (TextView) view.findViewById(R.id.ipq_txt_estatus);

            btnStatus = (Button) view.findViewById(R.id.ipq_btn_status);
            btnGuia = (Button) view.findViewById(R.id.ipq_btn_guia);

        }

    }



    @Override
    public int getItemCount() {
        return data.size();
    }


    /**
     * inteface que comunica con {@PedidosListQrActivity}
     *
     *
     * */
    public interface ClickListener {
        /**
         * @param position es el posicion del objeto que se le hizo click
         * @param viewHolder es la vista espeficica de ese objeto
         * @param status es el estado en el que se encuantra ese objeto
         * */
        void itemClicked(int position, ViewHolderCustom viewHolder, int status);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


}


