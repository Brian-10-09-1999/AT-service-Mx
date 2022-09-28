package com.example.devolucionmaterial.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.devolucionmaterial.Adapters.GalleryAdapter;
import com.example.devolucionmaterial.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EDGAR ARANA on 12/09/2017.
 * Modifier by Brian Martinez on 21/06/2022
 */

public class ImageOsFragment extends DialogFragment {

    public ImageOsFragment() {

    }

    RecyclerView rvGallery;
    ArrayList<String> images;
    GalleryAdapter galleryAdapter;
    ImageView imageViewBack;
    ImageView imageViewSend;
    Callback mCallback = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_os, container, false);

        Bundle b = getArguments();
        images = b.getStringArrayList("list");
        imageViewBack = (ImageView) view.findViewById(R.id.back);
        imageViewSend = (ImageView) view.findViewById(R.id.send);

        imageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.setCallback(true);
                getDialog().dismiss();
            }
        });


        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.setCallback(false);
                getDialog().dismiss();
                //getActivity().finish();
            }
        });
        rvGallery = (RecyclerView) view.findViewById(R.id.fio_rv_image);
        initRv(images);

        return view;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    void initRv(List<String> photos) {
        galleryAdapter = new GalleryAdapter(getActivity(), photos);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvGallery.setLayoutManager(horizontalLayoutManagaer);
        rvGallery.setAdapter(galleryAdapter);
    }
    //create an interface which will help us to communicate with fragments by help of Activity
   public  interface Callback {
        public void setCallback(Boolean msg);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        } else {
            throw new ClassCastException();
        }

    }


    public void sendMessage(boolean msg) {

        mCallback.setCallback(msg);
    }


}