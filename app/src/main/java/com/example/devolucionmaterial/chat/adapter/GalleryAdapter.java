package com.example.devolucionmaterial.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.model.ModelChat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by EDGAR ARANA on 20/04/2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private List<ModelChat> mListObjects = new ArrayList<>();
    private Context mContext;
    ClickListener clickListener;


    public GalleryAdapter(List<ModelChat> listObjects, Context context) {
        this.mListObjects = listObjects;
        this.mContext = context;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gallery_chat, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.txtViewTitle.setText(mListObjects.get(position).getUser());
        viewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.url(mListObjects.get(position).getMessage());
                }
            }
        });
        Glide.with(mContext)
                .load(mListObjects.get(position).getMessage())
                .override(500, 500)
                .centerCrop()
                .into(viewHolder.imgViewIcon);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_title_user)
         TextView txtViewTitle;
        @BindView(R.id.item_icon)
         ImageView imgViewIcon;
        @BindView(R.id.igc_root)
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
        void url(String url);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}