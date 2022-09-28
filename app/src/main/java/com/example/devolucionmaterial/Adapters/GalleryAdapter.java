package com.example.devolucionmaterial.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.devolucionmaterial.R;


import java.util.List;

/**
 * Created by EDGAR ARANA on 07/09/2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<String> petList;
    private Context context;
    private ClickListener clickListener;


    public GalleryAdapter(Context context, List<String> petList) {
        this.petList = petList;
        this.context = context;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_gallery, parent, false);
        return new ViewHolderGallery(view);


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolderGallery viewHolderImgePet = (ViewHolderGallery) holder;
        Glide.with(context).load(petList.get(position)).centerCrop().override(120, 120).into(viewHolderImgePet.imageView);

    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {

        if (holder instanceof ViewHolderGallery) {
            ViewHolderGallery viewHolderVideo = (ViewHolderGallery) holder;
            super.onViewRecycled(viewHolderVideo);
        }

    }


    @Override
    public int getItemCount() {
        return petList.size();
    }


    // TODO: 27/04/2017 inteface de comuniacion cuando haces click
    public interface ClickListener {
        void itemClicked(int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public class ViewHolderGallery extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolderGallery(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iip_iv_pet);
        }
    }



}


