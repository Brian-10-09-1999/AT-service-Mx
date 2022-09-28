package com.example.devolucionmaterial.activitys.Rack.RackFragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.devolucionmaterial.R;

/**
 * Created by EDGAR ARANA on 23/04/2018.
 */

public class DetallesSubSala extends Fragment {




    //se defone una etiqueta para esta clase
    private static final String TAG="Rack.RackFragments.DetallesDeRack";
    private String nameSubsala="",idSubsala="";
    private Spinner spComp1,spModel;
    private Button save,btnNoSerie,btnNoSerieQr;
    private Context context;
    private TextView tvNameSubsala;




    @Override
    public void onAttach(Context context) {
        //este metodo solo aplica para la api 24 o superio para api23 no se ejecuta
        super.onAttach(context);
        Log.i(TAG,"onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreated");



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_rack_subsala,container,false);
        Log.i(TAG,"onCreatedView");
        context=view.getContext();

        Bundle bundle=getArguments();
        nameSubsala=bundle.getString("name");

        spComp1=(Spinner) view.findViewById(R.id.sp_rack_subsala_comp);
        spModel=(Spinner) view.findViewById(R.id.sp_rack_subsala_model);
        tvNameSubsala=(TextView) view.findViewById(R.id.titulo_fragment_subsala);
        tvNameSubsala.setText(nameSubsala);

        String items1[] = {"Switch","Servidor","Zitro Replace","Servidor de grupo","UPS"};

        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(context,   android.R.layout.simple_spinner_item,items1);
        spinnerArrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spComp1.setAdapter(spinnerArrayAdapter1);

        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG,"onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG,"onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onresume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG,"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"ondestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG,"onDetach");
    }





}
