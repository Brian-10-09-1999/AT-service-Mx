package com.example.devolucionmaterial.activitys.Rack.RackFragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.Rack.Adapters.RecyclerAdapter;
import com.example.devolucionmaterial.activitys.Rack.Adapters.itemModel;
import com.example.devolucionmaterial.activitys.Rack.RackBaseActivity;
import com.example.devolucionmaterial.api.PostGet;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.PrimitiveIterator;

/**
 * Created by EDGAR ARANA on 13/04/2018.
 */

public class ListaHardware extends Fragment{

    //se recaban de lña base de datos del telefono
    private ArrayList<String> arrSalas = new ArrayList<String>();
    private ArrayList<String> idSalas = new ArrayList<String>();

    private BDmanager manager;
    private Spinner spSala,spRack;
    private TextView tvRack;
    private int regionidx;

    private Activity ac;

    private String[] itemSp1;
    private String[] itemSp2;

    private String[] SubSalas;
    private String[] rack;
    private String[] ubicacionRack;
    private String[] status;
    private String[] idSubsalas;
    private String idSala;

    private RecyclerView recyclerView;
    private Button btnSig;
    private RackBaseActivity ManagerShow;

    //se defone una etiqueta para esta clase
    private static final String TAG="(Rack) ---------- RackFragment.ListaHardware";
    private RackBaseActivity managerActivityBase;
    private Context context;
    private String url1="http://pruebasisco.ddns.net:8082/Android/rack/subsalas.php?";
    private String url2="http://pruebasisco.ddns.net:8082/Android/rack/ingresaDatosRack.php";




    RecyclerAdapter adapter;

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
        View view=inflater.inflate(R.layout.fragment_rack_a,container,false);
        Log.i(TAG,"onCreatedView");
        context=container.getContext();

        ac=getActivity();

        regionidx= Integer.parseInt((BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION")));
        manager = new BDmanager(context);

        tvRack=(TextView) view.findViewById(R.id.tv_rack);

        spSala=(Spinner) view.findViewById(R.id.sp_rack_sala);

        spRack=(Spinner) view.findViewById(R.id.sp_rack);
        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerView_rack1);
        btnSig=(Button) view.findViewById(R.id.btn_rack_siguiente);



        tvRack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listenerfragments myListener = (Listenerfragments) getActivity();
                myListener.nextView(2, "");


            }});


        btnSig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }});

        String items1[] = {"0","1","2","3"};
        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(context,   android.R.layout.simple_spinner_item,items1);
        spinnerArrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spRack.setAdapter(spinnerArrayAdapter1);

        spSala.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {

                      idSala=idSalas.get(posicion);
                      new ConectaConServer().execute();

                    }
                    public void onNothingSelected(AdapterView<?> spn) {

                    }
                });





        llenaSpinnerSala(regionidx);


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


    public void showSubsala(int n){



    }


    public void llenaSpinnerSala(int regionID) {

        arrSalas.clear();idSalas.clear();


        Cursor fila=manager.consulta("SELECT nombre,salaID FROM csala WHERE regionidfk= '" + regionID + "' ORDER BY nombre");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {

            arrSalas.add(fila.getString(0));
            idSalas.add(fila.getString(1));
            Log.i(TAG+".llenarSpinnerSala",fila.getString(0)+ "  "+fila.getString(1));
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrSalas);
        spSala.setAdapter(adapter2);



    }




     //baja las subsalas y estatus de cada sala
    class ConectaConServer extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;
        private String Exito="0";

        protected void onPreExecute() {
            pDialog = new ProgressDialog(context);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        protected Void doInBackground(Void... JSONurl) {

            PostGet servicios=new PostGet(context);
            String respuesta;

            try {
                Log.i(TAG+"ConectaServer GET url",url1+"salaidx="+idSala);
                respuesta=servicios.ConexionGet(url1+"salaidx="+idSala);
                Log.i(TAG+"ConectaServer RESPUESTA",respuesta);

                JSONObject json = new JSONObject(respuesta);
                JSONArray jsonArray = json.getJSONArray("subsalas");

                SubSalas = new String[jsonArray.length()];
                status = new String [jsonArray.length()];
                idSubsalas=new  String [jsonArray.length()];
                rack=new  String [jsonArray.length()];
                ubicacionRack=new  String [jsonArray.length()];


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objetoJson = jsonArray.getJSONObject(i);
                    SubSalas[i] = objetoJson.getString("sala");
                    status[i]=objetoJson.getString("estatus");
                    rack[i]=objetoJson.getString("rack");
                    ubicacionRack[i]=objetoJson.getString("ubicacionRack");
                    idSubsalas[i]=objetoJson.getString("salaid");

                }

                Exito="1";

            } catch (Exception e) {
                Exito="0";
                Log.e(TAG+"ConectaConServer", "ERROR En doInBackground: Exception :" + e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(Exito.equals("1")){
                adapter=new RecyclerAdapter(context, itemModel.getData(SubSalas,status,idSubsalas,rack,ubicacionRack),ac);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager mLinearLayoutManagerVertical=new LinearLayoutManager(context);
                mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }
            pDialog.dismiss();
        }

    }







    //baja las subsalas y estatus de cada sala
    class EnviaInfoRack extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;
        private String Exito="0";
        private String SelectRac="";



        protected void onPreExecute() {
            pDialog = new ProgressDialog(context);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);

            itemSp1=adapter.getSp1();
            itemSp2=adapter.getSp2();

            SelectRac=spRack.getSelectedItem().toString();
        }

        protected Void doInBackground(Void... JSONurl) {

            PostGet servicios=new PostGet(context);
            String respuesta;
            ArrayList<String> list = new ArrayList<String>();
            for (int k=0;k<itemSp1.length;k++){list.add(idSubsalas[k]+ "|"+itemSp1[k]+ "|"+itemSp2[k]);}

            try {
                final JSONObject jsonParam = new JSONObject();
                jsonParam.put("rack",SelectRac);
                jsonParam.put("idsala",idSala);
                jsonParam.put("subsalas",new JSONArray(list));
                Log.i(TAG+"EnviaInfoRack"," Post url="+url2 +"\n Cuerpo="+jsonParam.toString());
                respuesta=servicios.ConexionPost(url2,jsonParam);
                Log.i(TAG+"EnviaInfoRack","  Respuesta="+respuesta);

                Exito="1";

            } catch (Exception e) {
                Exito="0";
                Log.e(TAG+"EnviaInfoRack", "ERROR En doInBackground: Exception :" + e.getMessage());

            }
            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(Exito.equals("1")){

            }
            pDialog.dismiss();


        }

    }










}
