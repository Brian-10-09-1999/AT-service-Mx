package com.example.devolucionmaterial.activitys.Rack.RackFragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.annca.Annca;
import com.example.annca.internal.configuration.AnncaConfiguration;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.Rack.RackBaseActivity;
import com.example.devolucionmaterial.activitys.viaticos.Activity_Gastos;

/**
 * Created by EDGAR ARANA on 17/04/2018.
 */

public class DetallesDeRack extends Fragment{



    //se defone una etiqueta para esta clase
    private static final String TAG="Rack.RackFragments.DetallesDeRack";
    private Spinner spComp1,spComp2,spModel;
    private Button save,btnNoSerie,btnNoSerieQr;
    private Context context;




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
        View view=inflater.inflate(R.layout.fragment_rack_b,container,false);
        Log.i(TAG,"onCreatedView");
        context=view.getContext();

        spComp1=(Spinner) view.findViewById(R.id.sp_rack_comp1);
        spComp2=(Spinner) view.findViewById(R.id.sp_rack_comp2);
        spModel=(Spinner) view.findViewById(R.id.sp_rack_model);

        save=(Button) view.findViewById(R.id.but_rack_guardar);
        btnNoSerie=(Button) view.findViewById(R.id.but_rack_numserie);
        btnNoSerieQr=(Button) view.findViewById(R.id.but_rack_qr1);

        String items1[] = {"Monitor","Teclado","Mouse","UPS"};
        String items2[] = {"Zitro","Sala","No hay en sala"};

        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(context,   android.R.layout.simple_spinner_item,items1);
        spinnerArrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spComp1.setAdapter(spinnerArrayAdapter1);

        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(context,   android.R.layout.simple_spinner_item,items2);
        spinnerArrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spComp2.setAdapter(spinnerArrayAdapter2);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listenerfragments myListener=(Listenerfragments) getActivity();
                myListener.nextView(1,"");
            }
        });

        btnNoSerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listenerfragments myListener=(Listenerfragments) getActivity();
                myListener.iniciaCamara();
            }
        });


        btnNoSerieQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listenerfragments myListener=(Listenerfragments) getActivity();
                myListener.iniciaScanQr();
            }
        });


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
