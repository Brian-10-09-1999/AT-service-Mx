package com.example.devolucionmaterial.activitys.graficos;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
//import com.example.devolucionmaterial.Adapters.GraficaAdapter;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.DetalisGraficas;
import com.example.devolucionmaterial.beans.HorsGrafic;
import com.example.devolucionmaterial.beans.Kpi;
import com.example.devolucionmaterial.data_base.dbgraficas.BDmanagerGaficas;
import com.example.devolucionmaterial.utils.JSONRead;
import com.example.libraryvideo.subtitle.CaptionsView;

//import com.odn.mpchartlib.charts.LineChart;
//import com.odn.mpchartlib.data.Entry;
//import com.odn.mpchartlib.highlight.Highlight;
//import com.odn.mpchartlib.listener.OnChartValueSelectedListener;


import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GrafciosActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener {
    private Context context;
   // private LineChart mChart;
   // private LineChart mChart2;
   // private LineChart mChart3;
   // private LineChart mChart4;
   //  private GraficaAdapter graficaAdapter;
    private RecyclerView rvGraficos;
    private List<Kpi> kpiList;

    private LinearLayout graph1,graph2,graph3,graph4;
    private PlotPlanitoMesY plot1,plot2,plot3,plot4;
    private   float X[];


    private Spinner spinnerClinete;
    MaterialDialog materialDialog;
    private ProgressDialog pDialog;


    BDmanagerGaficas bDmanagerGaficas;

    private TextView tvAbietas, tvCerredas, tvPorcenjae;
    private TextView tvAbiertasNo, tvCerradasNo, getTvPorcenjaNo;
    private TextView tvCasinos, tvMaquinas, tvBlue, tvBlack, tvBlackPlus, tvBrike, tvFusion;
    private TextView tvHoras, tvHorasNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafcios);
        initToolbar();
        initSetUp();
        context=this;

        X=new float[12];
        X[0]=1.0f;
        X[1]=2.0f;
        X[2]=3.0f;
        X[3]=4.0f;
        X[4]=5.0f;
        X[5]=6.0f;
        X[6]=7.0f;
        X[7]=8.0f;
        X[8]=9.0f;
        X[9]=10.0f;
        X[10]=11.0f;
        X[11]=12.0f;


    }

    void initSetUp() {
        context = this;
        bDmanagerGaficas = new BDmanagerGaficas(this);
        bDmanagerGaficas.open();
        //bDmanagerGaficas.borarDatos();
        tvAbietas = (TextView) findViewById(R.id.ag_tv_abiertas);
        tvCerredas = (TextView) findViewById(R.id.ag_tv_cerrdas);
        tvPorcenjae = (TextView) findViewById(R.id.ag_tv_porcentaje);

        graph1=(LinearLayout) findViewById(R.id.ly_graph1);
        graph2=(LinearLayout) findViewById(R.id.ly_graph2);
        graph3=(LinearLayout) findViewById(R.id.ly_graph3);
        graph4=(LinearLayout) findViewById(R.id.ly_graph4);

        tvAbiertasNo = (TextView) findViewById(R.id.ag_tv_abiertas_no);
        tvCerradasNo = (TextView) findViewById(R.id.ag_tv_cerradas_no);
        getTvPorcenjaNo = (TextView) findViewById(R.id.ag_tv_porcentaje_no);

        tvCasinos = (TextView) findViewById(R.id.ag_tv_casinos);
        tvMaquinas = (TextView) findViewById(R.id.ag_tv_maquinas);
        tvBlue = (TextView) findViewById(R.id.ag_tv_b);
        tvBlack = (TextView) findViewById(R.id.ag_tv_bw);
        tvBlackPlus = (TextView) findViewById(R.id.ag_tv_bwp);
        tvBrike = (TextView) findViewById(R.id.ag_tv_bk);
        tvFusion = (TextView) findViewById(R.id.ag_tv_f);

        tvHoras = (TextView) findViewById(R.id.ag_tv_horas);
        tvHorasNo = (TextView) findViewById(R.id.ag_tv_horas_no);

        spinnerClinete = (Spinner) findViewById(R.id.ag_sp_cliente);
        if (bDmanagerGaficas.getNombreClientes().isEmpty()) {
            new Progreso().execute();
        } else {
            cargarSpinner();
        }
        rvGraficos = (RecyclerView) findViewById(R.id.ag_rv_graficos);
    }

/*
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

        mChart.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);

    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    */



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Graficos");
        }
    }

    void cargarSpinner() {
        List<String> clienets = bDmanagerGaficas.getNombreClientes();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, clienets);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerClinete.setAdapter(dataAdapter);
        spinnerClinete.setOnItemSelectedListener(this);
    }

    void initRv(Context context, List<Kpi> kpiList) {
      //  graficaAdapter = new GraficaAdapter(context, kpiList);
        //graficaAdapter.setClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvGraficos.setLayoutManager(mLayoutManager);
        rvGraficos.setItemAnimator(new DefaultItemAnimator());
       // rvGraficos.setAdapter(graficaAdapter);
    }

    void getIncidenia(String cliente) {


       /* materialDialog = new MaterialDialog.Builder(GrafciosActivity.this)
                .title("Espere ....")
                .content("Cargando cliente")
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(false)
                .show();*/
        pDialog = new ProgressDialog(GrafciosActivity.this);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setCancelable(false);

        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);


        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<HorsGrafic> call = serviceApi.sendClienteGafica(cliente);
        call.enqueue(new Callback<HorsGrafic>() {
            @Override
            public void onResponse(Call<HorsGrafic> call, Response<HorsGrafic> response) {
               pDialog.dismiss();
                try {
                    String hora = String.valueOf(response.body().getHoras());
                    String numFoli = String.valueOf(response.body().getNumFolios());
                    String horaNo = String.valueOf(response.body().getHorasNo());
                    String numFoliNo = String.valueOf(response.body().getNumFoliosNo());
                    tvHoras.setText(numFoli + "(" + hora + ")hrs");
                    tvHorasNo.setText(numFoliNo + "(" + horaNo + ")hrs");
                } catch (Exception e) {
                    Log.e("error horas", e + "");
                    tvHoras.setText("");
                    tvHorasNo.setText("");
                }
            }

            @Override
            public void onFailure(Call<HorsGrafic> call, Throwable t) {
               pDialog.dismiss();
                Log.e("error", t.getMessage());
                tvHoras.setText("");
                tvHorasNo.setText("");
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) view).setTextColor(getResources().getColor(R.color.accentColor)); //Change selected text color
        String label = parent.getItemAtPosition(position).toString();
        getIncidenia(label);

        int [] dat=Datos(position);
        tvCasinos.setText(""+dat[0]);
        tvBlue.setText(""+dat[1]);
        tvBlack.setText(""+dat[2]);
        tvBlackPlus.setText(""+dat[3]);
        tvBrike.setText(""+dat[4]);
        tvFusion.setText(""+dat[5]);
        tvMaquinas.setText(""+dat[6]);

        double [] dat2=Datos2(position);

        tvAbietas.setText(""+dat2[0]);
        tvCerredas.setText(""+dat2[1]);
        tvPorcenjae.setText(""+dat2[2]+"%");


        tvAbiertasNo.setText(""+dat2[3]);
        tvCerradasNo.setText(""+dat2[4]);
        getTvPorcenjaNo.setText(""+dat2[5]+"%");

        inflateGrafica(bDmanagerGaficas.getListForName(label));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }








    private class Progreso extends AsyncTask<String, Void, String> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(GrafciosActivity.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);

            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);


            /*materialDialog = new MaterialDialog.Builder(GrafciosActivity.this)
                    .title("Cargando ....")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();*/
        }

        @Override
        protected String doInBackground(String... url) {

            RegisterDataJsonMchart registerDataJsonMchart = new RegisterDataJsonMchart(context);
            registerDataJsonMchart.initInserRegister();

            return "";
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            //materialDialog.dismiss();
            pDialog.dismiss();
            cargarSpinner();

        }
    }









    private String validatEmpety(String value) {
        if (value.trim().isEmpty()) {
            return "0";
        } else {
            return value;
        }
    }

    void inflateGrafica(final List<Kpi> listaPorCliente) {

        grafica2(listaPorCliente);
        grafica3(listaPorCliente);
        grafica4(listaPorCliente);
        // TODO: 20/06/2017 antes de pintar la grafica se saca los detalles del cliente
        DetalisGraficas dg = bDmanagerGaficas.getListForDetalles(listaPorCliente.get(0).getClientes()).get(0);
      //  tvCasinos.setText(dg.getCasinos());
        //tvMaquinas.setText(dg.getTotal());
       // tvBlue.setText(dg.getBlue());
        //tvBlack.setText(dg.getBlack());
        //tvBlackPlus.setText(dg.getBlackplus());
       // tvBrike.setText(dg.getBryke());
        //tvFusion.setText(dg.getFusion());



        // TODO: 20/06/2017 se ponen los datos de abietas y cerrdas de enero
/*
        Kpi kpiEnero = listaPorCliente.get(0);
        //tvAbietas.setText(validatEmpety(kpiEnero.getPendientes()));
        //tvCerredas.setText(validatEmpety(kpiEnero.getCerrados()));

        try {
            float abierta1 = Float.parseFloat(dg.getAbiertas1());
            float cerradas1 = Float.parseFloat(dg.getCerradas1());
            float porcentaje = (cerradas1 / abierta1) * 100;
            String por = String.valueOf(porcentaje);
            if (por.length() > 5)
                tvPorcenjae.setText(por.substring(0, 5) + "%");
            else {
                tvPorcenjae.setText(por + "%");
            }
        } catch (Exception e) {
            Log.e("1", e + "");
            tvPorcenjae.setText("0");
        }
        try {
            float abierta2 = Float.parseFloat(dg.getAbiertas2());
            float cerradas2 = Float.parseFloat(dg.getCerradas2());
            float porcentaje = (cerradas2 / abierta2) * 100;
            String por = String.valueOf(porcentaje);
            if (por.length() > 5)
                getTvPorcenjaNo.setText(por.substring(0, 5) + "%");
            else {
                getTvPorcenjaNo.setText(por + "%");
            }
        } catch (Exception e) {
            Log.e("2", e + "");
            getTvPorcenjaNo.setText("0");
        }




        // TODO: 22/06/2017 inicia la vista de la grafica
      //  mChart = (LineChart) findViewById(R.id.chart1);
        //mChart.clear();
        //mChart.setOnChartValueSelectedListener(this);
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e != null) {
                    Kpi kpi = (Kpi) e.getData();
                    if (e.getData() instanceof Kpi) {

                        // tvAbietas.setText(validatEmpety(kpi.getPendientes()));
                        //tvCerredas.setText(validatEmpety(kpi.getCerrados()));
//de
                        if (kpi.getPorcentaje().length() > 5)
                            tvPorcenjae.setText(kpi.getPorcentaje().substring(0, 5) + "%");
                        else {
                            tvPorcenjae.setText(kpi.getPorcentaje() + "%");
                        }

                        if (kpi.getPorcentaje_no().length() > 5)
                            getTvPorcenjaNo.setText(kpi.getPorcentaje_no().substring(0, 5) + "%");
                        else {
                            getTvPorcenjaNo.setText(kpi.getPorcentaje_no() + "%");
                        }//hasta

                        //  tvAbiertasNo.setText(kpi.getAbiertas_no());
                        //tvCerradasNo.setText(kpi.getCerradas_no());
                        // getTvPorcenjaNo.setText(kpi.getPorcentaje_no());

                    } else {
                     //de   tvAbietas.setText("0");
                        tvCerredas.setText("0");
                        tvPorcenjae.setText("0" + "%");
                        tvAbiertasNo.setText("0");
                        tvCerradasNo.setText("0");
                        getTvPorcenjaNo.setText("0");

                        //hasta

                    }
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

*/


        // TODO: 26/06/2017  la clase DataMchart reduce el codigo pasando las modificicaciones de la grafica
     //   DataMchart mchart = new DataMchart(mChart, context, listaPorCliente, 1, 120, 80, 100f);
      //  mChart.setData(mchart.getData());



        float y1[]=new float[listaPorCliente.size()];

        for (int i = 0; i < listaPorCliente.size(); i++) {
            Kpi kpi = listaPorCliente.get(i);
           y1[i]=  Float.valueOf(kpi.getPorcentaje());
           Log.i("-----------juan----","y["+i+"]="+y1[i]);

        }


        graph1.removeAllViews();
        plot1 = new PlotPlanitoMesY(context,"Porcentaje de Eficiencia","titulo eje x","titulo eje y");
        plot1.SetSerie1(X,y1,"graph 1",1,true);
        plot1.SetHD(true); //ajustamos la calidad hd que suaviza bordes del grafico. por default esta desactivado
        plot1.SetTouch(true);// activa el touch sobre el grafico no es necesario colocarlo ya que por default esta activado
        graph1.addView(plot1);

    }

    // TODO: 26/06/2017 grafica 2
    void grafica2(List<Kpi> listAtencionMensual) {

        /*
        mChart2 = (LineChart) findViewById(R.id.chart2);
        mChart2.clear();
        mChart2.setOnChartValueSelectedListener(this);

        mChart2.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {


            }

            @Override
            public void onNothingSelected() {

            }
        });

        DataMchart mchart = new DataMchart(mChart2, context, listAtencionMensual, 2, 3, 0, 3f);
        mChart2.setData(mchart.getData());*/

        float y2[]=new float[listAtencionMensual.size()];

        for (int i = 0; i < listAtencionMensual.size(); i++) {
            Kpi kpi = listAtencionMensual.get(i);
            y2[i]=  Float.valueOf(kpi.getPromedio_dias());
            Log.i("-----------juan 2----","y["+i+"]="+y2[i]);

        }


        graph2.removeAllViews();
        plot2 = new PlotPlanitoMesY(context,"Promedio Dias","titulo eje x","titulo eje y");
        plot2.SetSerie1(X,y2,"graph 1",1,true);
        plot2.SetHD(true); //ajustamos la calidad hd que suaviza bordes del grafico. por default esta desactivado
        plot2.SetTouch(true);// activa el touch sobre el grafico no es necesario colocarlo ya que por default esta activado
        graph2.addView(plot2);


    }


    // TODO: 26/06/2017 grafica 3
    void grafica3(List<Kpi> listAtencionMensual) {

        /*
        mChart3 = (LineChart) findViewById(R.id.chart3);
        mChart3.clear();
        mChart3.setOnChartValueSelectedListener(this);

        mChart3.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {


            }

            @Override
            public void onNothingSelected() {

            }
        });

        DataMchart mchart = new DataMchart(mChart3, context, listAtencionMensual, 3, 120, 80, 100f);
        mChart3.setData(mchart.getData());*/


        float y3[]=new float[listAtencionMensual.size()];

        for (int i = 0; i < listAtencionMensual.size(); i++) {
            Kpi kpi = listAtencionMensual.get(i);
            y3[i]=  Float.valueOf(kpi.getPorcentaje_no());
            Log.i("-----------juan 3----","y["+i+"]="+y3[i]);

        }


        graph3.removeAllViews();
        plot3 = new PlotPlanitoMesY(context,"Porcentaje de Eficiencia","titulo eje x","titulo eje y");
        plot3.SetSerie1(X,y3,"graph 1",1,true);
        plot3.SetHD(true); //ajustamos la calidad hd que suaviza bordes del grafico. por default esta desactivado
        plot3.SetTouch(true);// activa el touch sobre el grafico no es necesario colocarlo ya que por default esta activado
        graph3.addView(plot3);



    }

    // TODO: 26/06/2017 grafica 4
    void grafica4(List<Kpi> listAtencionMensual) {

        /*
        mChart4 = (LineChart) findViewById(R.id.chart4);
        mChart4.clear();
        mChart4.setOnChartValueSelectedListener(this);
        mChart4.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {


            }

            @Override
            public void onNothingSelected() {

            }
        });

        DataMchart mchart = new DataMchart(mChart4, context, listAtencionMensual, 4, 90, 30, 72f);
        mChart4.setData(mchart.getData());*/

        float y4[]=new float[listAtencionMensual.size()];

        for (int i = 0; i < listAtencionMensual.size(); i++) {
            Kpi kpi = listAtencionMensual.get(i);
            y4[i]=  Float.valueOf(kpi.getPromedio_horas_porcentaje_de_eficiencia());
            Log.i("-----------juan 4----","y["+i+"]="+y4[i]);

        }


        graph4.removeAllViews();
        plot4 = new PlotPlanitoMesY(context,"Promedio de Horas","titulo eje x","titulo eje y");
        plot4.SetSerie1(X,y4,"graph 1",1,true);
        plot4.SetHD(true); //ajustamos la calidad hd que suaviza bordes del grafico. por default esta desactivado
        plot4.SetTouch(true);// activa el touch sobre el grafico no es necesario colocarlo ya que por default esta activado
        graph4.addView(plot4);


    }



    private int [] Datos(int  id){

        int [] regresa;

        int[] d0={0,0,0,0,0,0,0};//aramazd gaming
        int[] d1={1,	30	,0,	0	,0,	0,	30 };//bge
        int[] d2= {18	,320	,690,	0	,0,	392,	1402};//bigbola
        //int[] d3={0,0,0,0,0,0,0};//andes
        int[] d3={1,	0,	40,	0,	6,	0,	46};//bingo retos
        int[] d4={2,	24,	0,	0,	0,	0,	24};//casino central
        int[] d5={1,	0	,18	,0,	8	,6,	32};//cem games
        int[] d6={53,887,305	,1023,	0	,64	,2279};//cie
        int[] d7={16,	112	,208,	0	,18,	0,	338};//cirsa
        int[] d8={33,	1035	,228	,934,	0	,42	,2239};//codere
        int[] d9={1,	16	,0	,0	,0	,0,	16};//comenchi montemorelos
        int[] d10={2,	0	,30	,0	,0	,0,	30};//crown casinos
        int[] d11={7	,64	,38,	0	,8	,0	,110};//crown city group
        int[] d12={0	,0	,0	,0,	0	,0	,0};//dine juegos
        int[] d13={1	,0	,30	,0	,0	,0	,30};//entretenimiento bahia
        int[] d14={1	,0	,40	,0	,8	,0	,48};//foliati
        int[] d15={6	,22	,123	,0	,18,	0	,163};//fuentes agora
        int[] d16={4	,30	,40	,0	,26	,20	,116};//grupo win
        int[] d17={1	,20	,0	,0	,16	,0	,36};//juegos y sorteos de jalisco
        int[] d18={1	,0	,28,	0	,0	,0	,28};//madero
        int[] d19={1,	0	,32	,0,	6,	0	,38};//magic fortune
        int[] d20={0	,0,	0	,0	,0,	0	,0};//mega casino
        int[] d21={5	,42	,169,	0	,42	,0	,253};//olivares
        int[] d22={10	,216	,220,	0,	0	,64	,500};//operadora class
        int[] d23={3	,105	,88,	0	,24,	0	,217};//orenes
        int[] d24={16	,30	,1062	,0	,116	,350	,1558};//play city
        int[] d25={2	,49	,12,	0	,0	,0,	61};//storm
        int[] d26={6	,30	,382,	0	,29	,54	,495};//twin lions
        int[] d27={2	,10	,59,	0	,0	,8	,77};//urban publicity
        int[] d28={17	,12	,485	,0	,6	,294	,797};//winpot


        switch (id){

            case 0:
                regresa=d0;
                break;
            case 1:
                regresa=d1;
                break;
            case 2:
                regresa=d2;
                break;
            case 3:
                regresa=d3;
                break;
            case 4:
                regresa=d4;
                break;
            case 5:
                regresa=d5;
                break;
            case 6:
                regresa=d6;
                break;
            case 7:
                regresa=d7;
                break;
            case 8:
                regresa=d8;
                break;
            case 9:
                regresa=d9;
                break;
            case 10:
                regresa=d10;
                break;
            case 11:
                regresa=d11;
                break;
            case 12:
                regresa=d12;
                break;
            case 13:
                regresa=d13;
                break;
            case 14:
                regresa=d14;
                break;
            case 15:
                regresa=d15;
                break;
            case 16:
                regresa=d16;
                break;
            case 17:
                regresa=d17;
                break;
            case 18:
                regresa=d18;
                break;
            case 19:
                regresa=d19;
                break;
            case 20:
                regresa=d20;
                break;
            case 21:
                regresa=d21;
                break;
            case 22:
                regresa=d22;
                break;
            case 23:
                regresa=d23;
                break;
            case 24:
                regresa=d24;
                break;
            case 25:
                regresa=d25;
                break;
            case 26:
                regresa=d26;
                break;
            case 27:
                regresa=d27;
                break;
            case 28:
                regresa=d28;
                break;



            default:
                regresa=d1;
                break;

        }

        return regresa;
    }


    private double [] Datos2(int  id){

        double [] regresa;
        double[] d0={60	,59	,98.33	,12	,11,	91.66};//aramazd gaming
        double[] d1={168,	168	,100,	48	,48,	100};//bge
        double[] d2={9187	,9162	,99.72	,1732	,1727	,99.71};//big bola
        double[] d3={239	,237	,99.16	,32	,32	,100};//bingo retos
        double[] d4={156	,156	,100	,29,	29	,100};//casino central
        double[] d5={81	,81	,100	,27	,27	,100};//cem games
        double[] d6={8926,	8902	,99.73	,2334	,2320	,99.40};//cie
        double[] d7={ 2203,	2195	,99.63	,414	,413	,99.75 };//cirsa
        double[] d8={7083	,7060	,99.67	,2089	,2083	,99.71};//codere
        double[] d9={84	,84	,100	,27	,27	,100};//comenchi montemorelos
        double[] d10={158	,157	,99.36	,29	,29	,100};//crow casino
        double[] d11={505	,505,	100	,67	,67	,100};//crow city group
        double[] d12={9290	,9281	,99.90	,1585	,1579	,99.62};//dinejuegos
        double[] d13={79	,79	,100	,14,	14	,100};//entretenimiento bahia
        double[] d14={482	,482	,100	,55	,55,	100};//foliatti
        double[] d15={2516	,2513,	99.88	,420	,419	,99.76};//fuentes agora
        double[] d16={522	,519	,99.42	,75	,74	,98.66};//grupo win
        double[] d17={108,	108	,100	,53	,53	,100};//juegos y sorteos de jalisco
        double[] d18={64	,64	,100	,11,	11,	100};//madero
        double[] d19={0	,0	,0	,0,	0	,0};//magic fortune
        double[] d20={239	,239	,100	,47	,47	,100};//mega casino
        double[] d21={2676	,2674,	99.92	,401	,401	,100};//olivares
        double[] d22={3070	,3058	,99.60	,501	,497	,99.20};//operadora class
        double[] d23={978	,977	,99.89,	140	,139	,99.28};//orenes
        double[] d24={7924	,7911	,99.83	,1207	,1202	,99.58};//play city
        double[] d25={342	,342	,100	,42	,42	,100};//storm
        double[] d26={3087	,3077	,99.67	,410	,409	,99.75};//twin lions
        double[] d27={305	,304	,99.67	,23	,23	,100};//urban publicity
        double[] d28={6852	,6831	,99.69,	847	,842	,99.40};//winpot


        switch (id){

            case 0:
                regresa=d0;
                break;
            case 1:
                regresa=d1;
                break;
            case 2:
                regresa=d2;
                break;
            case 3:
                regresa=d3;
                break;
            case 4:
                regresa=d4;
                break;
            case 5:
                regresa=d5;
                break;
            case 6:
                regresa=d6;
                break;
            case 7:
                regresa=d7;
                break;
            case 8:
                regresa=d8;
                break;
            case 9:
                regresa=d9;
                break;
            case 10:
                regresa=d10;
                break;
            case 11:
                regresa=d11;
                break;
            case 12:
                regresa=d12;
                break;
            case 13:
                regresa=d13;
                break;
            case 14:
                regresa=d14;
                break;
            case 15:
                regresa=d15;
                break;
            case 16:
                regresa=d16;
                break;
            case 17:
                regresa=d17;
                break;
            case 18:
                regresa=d18;
                break;
            case 19:
                regresa=d19;
                break;
            case 20:
                regresa=d20;
                break;
            case 21:
                regresa=d21;
                break;
            case 22:
                regresa=d22;
                break;
            case 23:
                regresa=d23;
                break;
            case 24:
                regresa=d24;
                break;
            case 25:
                regresa=d25;
                break;
            case 26:
                regresa=d26;
                break;
            case 27:
                regresa=d27;
                break;
            case 28:
                regresa=d28;
                break;



            default:
                regresa=d19;
                break;

        }


   return  regresa;

    }



}
