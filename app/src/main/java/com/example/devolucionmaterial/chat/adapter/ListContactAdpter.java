package com.example.devolucionmaterial.chat.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.model.ContactList;
import com.example.devolucionmaterial.utils.MaterialLetterIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrador on 22/12/2016.
 */

public class ListContactAdpter extends RecyclerView.Adapter<ListContactAdpter.ItemViewHolder> {
    private final TypedValue mTypedValue = new TypedValue();
    public List<ContactList> mContact;
    private List<ContactList> mOriginalCountryModel;
    private ClickListener clickListener;
    private int mBackground;
    private int[] mMaterialColors;
    Random RANDOM = new Random();

    public ListContactAdpter(Context context, List<ContactList> mContact) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mMaterialColors = context.getResources().getIntArray(R.array.colors);
        mBackground = mTypedValue.resourceId;
        this.mContact = mContact;
        this.mOriginalCountryModel = mContact;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, final int i) {
        final ContactList model = mContact.get(i);
       /* Glide.with(itemViewHolder.imageView.getContext())
                .load(R.drawable.user_icn).
                centerCrop().
                transform(new CircleTransform(itemViewHolder.imageView.getContext()))
                .override(40, 40).into(itemViewHolder.imageView);*/
        itemViewHolder.mIcon.setInitials(true);
        itemViewHolder.mIcon.setInitialsNumber(2);
        itemViewHolder.mIcon.setLetterSize(18);

        itemViewHolder.name_TextView.setText(model.getName());
        itemViewHolder.tvAlias.setText(model.getAlias());
        itemViewHolder.mIcon.setLetter(model.getName());
        itemViewHolder.mIcon.setShapeColor(mMaterialColors[RANDOM.nextInt(mMaterialColors.length)]);
        itemViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.itemClicked(i);
                }
            }
        });

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact_chat, viewGroup, false);
        view.setBackgroundResource(mBackground);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mContact.size();
    }

    public void setFilter(List<ContactList> contacModels) {
        mContact = new ArrayList<>();
        mContact.addAll(contacModels);

        notifyDataSetChanged();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void itemClicked(int item);

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView name_TextView;
        TextView tvAlias;
        LinearLayout llContent;
        FrameLayout root;
        MaterialLetterIcon mIcon;


        ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            root = (FrameLayout) itemView.findViewById(R.id.ilc_root);
            name_TextView = (TextView) itemView.findViewById(R.id.icc_contact_name);
            tvAlias = (TextView) itemView.findViewById(R.id.icc_contact_alias);
            llContent = (LinearLayout) itemView.findViewById(R.id.icc_ll_content);
            //imageView = (ImageView) itemView.findViewById(R.id.icc_image_contact);
            mIcon = (MaterialLetterIcon) itemView.findViewById(R.id.icc_image_contact);

        }


    }


}