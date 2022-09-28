package com.example.devolucionmaterial.fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.devolucionmaterial.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragListaOSAsignadas extends Fragment{
    String metodo;
    Context context;
    View view;

    public FragListaOSAsignadas(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_body, container, false);
        context = view.getContext();
        return view;
    }
}
