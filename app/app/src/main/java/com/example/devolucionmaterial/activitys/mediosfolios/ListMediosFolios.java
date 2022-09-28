package com.example.devolucionmaterial.activitys.foliosPendientesSeccion.mediosfolios;


import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.example.devolucionmaterial.Adapters.MediosFoliosAdapter;
import com.example.devolucionmaterial.R;


import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanGaleria;
import com.example.devolucionmaterial.beans.BeansGlobales;
import com.example.devolucionmaterial.beans.Galerium;
import com.example.devolucionmaterial.beans.MedioFolios;
import com.example.devolucionmaterial.chat.activitys.ChatActivity;
import com.example.devolucionmaterial.chat.activitys.ImageFullActivity;
import com.example.devolucionmaterial.chat.activitys.ImageSelectActivity;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListMediosFolios extends AppCompatActivity implements MediosFoliosAdapter.ClickListener {
    private List<MedioFolios> medioFolioses = new ArrayList<>();
    private RecyclerView rvMedios;
    private MediosFoliosAdapter mAdapter;
    private Context context;
    private String folio;

    private ProgressDialog pDialog;

    private TextView tvNoData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_list_medios_folios);
        rvMedios = (RecyclerView) findViewById(R.id.almf_rv_medios);
        if (getIntent().getExtras() != null) {
            folio = getIntent().getExtras().getString("folio");
            String delimiter = "-";
            String[] temp = folio.split(delimiter);
            folio = temp[1];
        }
        tvNoData = (TextView) findViewById(R.id.almmf_tv_no_hay_datos);

        initToolbar();
        conectionList();


    }

    void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Archivos de Folio");
        }
    }

    void conectionList() {

        pDialog = new ProgressDialog(ListMediosFolios.this);
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);

        String url;
        url = BeansGlobales.getUrl().substring(0, BeansGlobales.getUrl().length() - 8);
        Log.e("folio", folio);
        RequestBody rFolio = RequestBody.create(MediaType.parse("text/plain"), folio);
        RequestBody rUrl = RequestBody.create(MediaType.parse("text/plain"), url);

        /**
         * se crea la instacia de retorit
         * */
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanGaleria> call = serviceApi.getMedios(rFolio, rUrl);
        call.enqueue(new Callback<BeanGaleria>() {
            @Override
            public void onResponse(Call<BeanGaleria> call, Response<BeanGaleria> response) {
                pDialog.dismiss();
                try {
                    if (response.body().getExitoso().equals("SI")) {
                        for (int i = 0; i < response.body().getGaleria().size(); i++) {
                            Galerium galerium = response.body().getGaleria().get(i);
                            medioFolioses.add(new MedioFolios(galerium.getImagenUrl(), galerium.getTipo()));
                        }
                        initRv(medioFolioses);
                    } else if (response.body().getExitoso().equals("NO")) {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e("error", String.valueOf(e));
                }
            }

            @Override
            public void onFailure(Call<BeanGaleria> call, Throwable t) {
                pDialog.dismiss();
                Log.e("error cargar imagen", String.valueOf(t));
            }
        });
    }

    void initRv(List<MedioFolios> medioFolioses) {
        if (medioFolioses.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);

        }
        mAdapter = new MediosFoliosAdapter(context, medioFolioses, this);
        mAdapter.setClickListener(ListMediosFolios.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvMedios.setLayoutManager(mLayoutManager);
        rvMedios.setItemAnimator(new DefaultItemAnimator());
        rvMedios.setAdapter(mAdapter);
        rvMedios.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING || newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter.onScrolled(recyclerView);
                }
            }

        });
    }

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


    @Override
    public void itemClicked(int position) {
        Intent intentImage = new Intent(ListMediosFolios.this, ImageFullActivity.class);
        intentImage.putExtra("url", medioFolioses.get(position).getUrl());
        startActivity(intentImage);
    }
}
