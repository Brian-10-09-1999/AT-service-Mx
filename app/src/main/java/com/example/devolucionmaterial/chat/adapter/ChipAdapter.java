package com.example.devolucionmaterial.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.model.ChipContact;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by EDGAR ARANA on 21/04/2017.
 */

public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.ViewHolder> {
    private List<ChipContact> mListObjects = new ArrayList<>();
    private Context mContext;
    ClickListener clickListener;


    public ChipAdapter(List<ChipContact> listObjects, Context context) {
        this.mListObjects = listObjects;
        this.mContext = context;
    }

    @Override
    public ChipAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_croos_chip, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.txtViewTitle.setText(mListObjects.get(position).getName());
        viewHolder.imgViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.click(mListObjects.get(position).getPosition());
                }
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icc_tv_name)
        TextView txtViewTitle;
        @BindView(R.id.delete)
        ImageView imgViewIcon;
        @BindView(R.id.icc_root)
        FrameLayout root;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            ButterKnife.bind(this, itemLayoutView);
        }
    }


    @Override
    public int getItemCount() {
        return mListObjects.size();
    }


    public interface ClickListener {
        void click(int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}