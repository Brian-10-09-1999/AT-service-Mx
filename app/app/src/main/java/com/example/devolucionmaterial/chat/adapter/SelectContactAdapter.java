package com.example.devolucionmaterial.chat.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.model.ContactList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrador on 16/01/2017.
 */

public class SelectContactAdapter extends RecyclerView.Adapter<SelectContactAdapter.ItemViewHolder> {

    private List<ContactList> mContact;
    private List<ContactList> mOriginalCountryModel;
    private ClickListener clickListener;


    public SelectContactAdapter(List<ContactList> mContact) {
        this.mContact = mContact;
        this.mOriginalCountryModel = mContact;
    }

    @Override
    public void onBindViewHolder(final SelectContactAdapter.ItemViewHolder itemViewHolder, final int i) {
        final ContactList model = mContact.get(i);
        Glide.with(itemViewHolder.imageView.getContext())
                .load(R.drawable.user_icn).
                centerCrop().
                transform(new CircleTransform(itemViewHolder.imageView.getContext()))
                .override(40, 40).into(itemViewHolder.imageView);
        itemViewHolder.name_TextView.setText(model.getName());
        itemViewHolder.tvAlias.setText(model.getAlias());
        itemViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (clickListener != null) {
                    clickListener.itemClicked(i);
                }*/
            }
        });
        itemViewHolder.chkSelected.setChecked(mContact.get(i).isSelected());

        itemViewHolder.chkSelected.setTag(mContact.get(i));


        itemViewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                ContactList contact = (ContactList) cb.getTag();
                contact.setSelected(cb.isChecked());
                mContact.get(i).setSelected(cb.isChecked());
            }
        });

    }

    @Override
    public SelectContactAdapter.ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contac_select_chat, viewGroup, false);
        return new SelectContactAdapter.ItemViewHolder(view);
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
        private CheckBox chkSelected;
        ImageView imageView;


        ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            root = (FrameLayout) itemView.findViewById(R.id.ilc_root);
            name_TextView = (TextView) itemView.findViewById(R.id.icc_contact_name);
            tvAlias = (TextView) itemView.findViewById(R.id.icc_contact_alias);
            llContent = (LinearLayout) itemView.findViewById(R.id.icc_ll_content);
            chkSelected = (CheckBox) itemView.findViewById(R.id.checkbox);
            imageView = (ImageView) itemView.findViewById(R.id.icsc_image_contact);

        }

    }

    public List<ContactList> getContacttist() {
        return mContact;
    }


}