package com.example.devolucionmaterial.activitys.pedidosSeccion;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.LectorActivity;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.ListPedidosGuardadosActivity;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.CrearPedido;
import com.example.devolucionmaterial.customview.AutoscaleEditText;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.data_base.DBCrearPedido;
import com.example.devolucionmaterial.lists.ListViewItem;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CrearPedidoActivity extends AppCompatActivity
        implements MaterialSpinner.OnItemSelectedListener, View.OnClickListener, Callback<CrearPedido> {

    private MaterialDialog materialDialog;

    private MaterialSpinner spRegion;
    private MaterialSpinner spSala;
    private BDmanager manager;
    private DBCrearPedido dbCrearPedido;

    List<String> arrSalas;
    List<Integer> arrSalasID;
    private String nombreSala;
    private int salaID;
    private ImageButton ibQrMaquina;
    private ImageButton ibQrComponente;
    private AutoscaleEditText aetMaquina;
    private AutoscaleEditText aetComponente;
    private ImageButton fabSend;

    private boolean vFolio = false;

    private LinearLayout llRegion, llSala;
    private String folio = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_pedido);
        initToolbar();

        if (getIntent().getExtras() != null) {
            vFolio = true;
            salaID = Integer.parseInt(getIntent().getExtras().getString("salaId"));
            folio = getIntent().getExtras().getString("folio");
            Log.e("salaID", String.valueOf(salaID));
            Log.e("folio", String.valueOf(folio));

        }
        manager = new BDmanager(this);
        dbCrearPedido = new DBCrearPedido(this);
        dbCrearPedido.open();
        spRegion = (MaterialSpinner) findViewById(R.id.acf_ms_region);
        spSala = (MaterialSpinner) findViewById(R.id.acf_ms_sala);

        llRegion = (LinearLayout) findViewById(R.id.acp_ll_region);
        llSala = (LinearLayout) findViewById(R.id.acp_ll_sala);

        fabSend = (ImageButton) findViewById(R.id.btn_send);

        ibQrMaquina = (ImageButton) findViewById(R.id.acp_ib_qr_maquina);
        ibQrComponente = (ImageButton) findViewById(R.id.acp_ib_qr_componentes);

        aetMaquina = (AutoscaleEditText) findViewById(R.id.acp_ae_maquina);
        aetComponente = (AutoscaleEditText) findViewById(R.id.acp_ae_componente);
        spRegion.setItems(listRegionesNombres());


        ibQrMaquina.setOnClickListener(this);
        ibQrComponente.setOnClickListener(this);
        fabSend.setOnClickListener(this);

        spRegion.setOnItemSelectedListener(this);
        spSala.setOnItemSelectedListener(this);


        llenaComboSala(listRegiones().get(spRegion.getSelectedIndex()).getId());
        // TODO: 04/05/2017 se inicia el spiner de salas en 0


        // TODO: 07/06/2017 si ya viene en el intenet el id de la sala ya no se muestra los spinners
        if (vFolio) {
            spRegion.setVisibility(View.GONE);
            spSala.setVisibility(View.GONE);

            llRegion.setVisibility(View.GONE);
            llSala.setVisibility(View.GONE);


        } else {
            nombreSala = arrSalas.get(spSala.getSelectedIndex());
            salaID = arrSalasID.get(spSala.getSelectedIndex());
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();


    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cleanup in onPause()

    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crear_folio, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_list_crear:
                Intent iList = new Intent(this, ListPedidosGuardadosActivity.class);
                startActivity(iList);
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
            getSupportActionBar().setTitle("Crear Pedido");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("result");
            if (requestCode == 1) {
                aetMaquina.setText(result);
            }
            if (requestCode == 2) {
                aetComponente.setText(result);
            }
        }

        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }

    }


    private List<ListViewItem> listRegiones() {
        List<ListViewItem> data = new ArrayList<>();
        Cursor fila = manager.consulta("select id, nombre from cregion ORDER BY nombre");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
            data.add(new ListViewItem(fila.getInt(0), fila.getString(1)));
        }
        return data;
    }

    private void llenaComboSala(int regionID) {
        Log.e("region", String.valueOf(regionID));
        arrSalas = new ArrayList<>();
        arrSalasID = new ArrayList<>();
        arrSalas.add("Seleccione una sala");
        arrSalasID.add(0);
        Cursor fila = manager.consulta("select salaid , nombre from csala where regionidfk= '" + regionID + "' ORDER BY nombre");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
            arrSalasID.add(fila.getInt(0));
            arrSalas.add(fila.getString(1));

        }
        fila.close();
        spSala.setItems(arrSalas);


    }

    private List<String> listRegionesNombres() {
        List<String> data = new ArrayList<>();
        Cursor fila = manager.consulta("select id, nombre from cregion ORDER BY nombre");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
            data.add(fila.getString(1));
        }
        return data;
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        switch (view.getId()) {
            case R.id.acf_ms_region:
                llenaComboSala(listRegiones().get(position).getId());
                break;

            case R.id.acf_ms_sala:
                nombreSala = arrSalas.get(position);
                Log.e("nombreSala", nombreSala);
                salaID = arrSalasID.get(spSala.getSelectedIndex());
                Log.e("idSala", String.valueOf(arrSalasID.get(spSala.getSelectedIndex())));
                if (salaID != 0) {

                } else {

                }

                break;

        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.acp_ib_qr_maquina:
                Intent iMaquina = new Intent(this, LectorActivity.class);
                startActivityForResult(iMaquina, 1);
                break;

            case R.id.acp_ib_qr_componentes:
                Intent iComponente = new Intent(this, LectorActivity.class);
                startActivityForResult(iComponente, 2);
                break;
            case R.id.btn_send:
                send();
                break;
        }
    }


    void send() {
        if (salaID != 0) {
            if (!aetMaquina.getText().
                    toString().trim().isEmpty() ||
                    !aetComponente.getText()
                            .toString().trim().isEmpty()) {

                String maquina = aetMaquina.getText().toString();
                String componente = aetComponente.getText().toString();

                Log.e("maquina", maquina);
                Log.e("componente", componente);
                Log.e("salaID", String.valueOf(salaID));

                materialDialog = new MaterialDialog.Builder(this)
                        .title(getString(R.string.Conectando_con_servidor_remoto))
                        .content("Cargando...")
                        .progress(true, 0)
                        .cancelable(false)
                        .progressIndeterminateStyle(false)
                        .show();

                ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
                Call<CrearPedido> call = serviceApi.sendCrearPedido(
                        salaID, maquina, componente, BDVarGlo.getVarGlo(CrearPedidoActivity.this, "INFO_USUARIO_ID"), folio);
                call.enqueue(this);


            } else {
                new MaterialDialog.Builder(this)
                        .content("Faltan datos ")
                        .iconRes(R.drawable.warning)
                        .positiveText(R.string.aceptar)
                        //.negativeText(R.string.disagree)
                        .show();
            }

        } else {
            new MaterialDialog.Builder(this)
                    .content("Falta selecionar la sala ")
                    .iconRes(R.drawable.warning)
                    .positiveText(R.string.aceptar)
                    //.negativeText(R.string.disagree)
                    .show();
        }
    }

    @Override
    public void onResponse(Call<CrearPedido> call, retrofit2.Response<CrearPedido> response) {
        materialDialog.dismiss();
        // TODO: 08/06/2017 cuando respuesta es 11 es correcto
        try {
            final String maquina = aetMaquina.getText().toString();
            final String componente = aetComponente.getText().toString();


            if (response.body().getRespuesta() == 11) {

                String pedido = "Número de pedido" + " " + response.body().getPedido() + "";
                new MaterialDialog.Builder(CrearPedidoActivity.this)
                        .title("Pedido exitoso!!")
                        .content(pedido)
                        .iconRes(R.drawable.delivery_ok)
                        .cancelable(false)
                        .positiveText(R.string.aceptar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dbCrearPedido.insert(salaID, maquina, componente, 1, "0", folio);
                                finish();
                            }
                        })
                        .show();
            } else if (response.body().getRespuesta() == 01) {
                new MaterialDialog.Builder(CrearPedidoActivity.this)
                        .content("Numeros incorrectos")
                        .iconRes(R.drawable.warning)
                        .positiveText(R.string.aceptar)
                        .cancelable(false)
                        .show();
            } else {
                errorPedido(response.body().getRespuesta());
            }


        } catch (Exception e) {
            Log.e("error", String.valueOf(e));
            new MaterialDialog.Builder(CrearPedidoActivity.this)
                    .content("Ocurrio un error al procesar solicitud")
                    .iconRes(R.drawable.warning)
                    .positiveText(R.string.aceptar)
                    .negativeText("Guardar pedido")
                    .cancelable(false)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            String maquina = aetMaquina.getText().toString();
                            String componente = aetComponente.getText().toString();
                            dbCrearPedido.insert(salaID, maquina, componente, 0, "0", "0");
                            Toast.makeText(CrearPedidoActivity.this, "Pedido guardado", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onFailure(Call<CrearPedido> call, Throwable t) {

        Log.e("error", t + "");
        materialDialog.dismiss();
        new MaterialDialog.Builder(CrearPedidoActivity.this)
                .content("Ocurrio un error al conectar")
                .iconRes(R.drawable.warning)
                .positiveText(R.string.aceptar)
                .cancelable(false)
                .negativeText("Guardar pedido")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String maquina = aetMaquina.getText().toString();
                        String componente = aetComponente.getText().toString();
                        dbCrearPedido.insert(salaID, maquina, componente, 0, "0", "0");
                        Toast.makeText(CrearPedidoActivity.this, "Pedido guardado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .show();
    }

    void errorPedido(int codigo) {
        String mensaje = "";
        switch (codigo) {
            case 1:
                mensaje = "Código de la máquina incorrecto";
                break;
            case 2:
                mensaje = "Código de la máquina incorrecto";
                break;
            case    3:
                mensaje = "Código del componente incorrecto";
                break;
            case 4:
                mensaje = "Código del componente incorrecto";
                break;
            case 5:
                mensaje = "Código del componente incorrecto";
                break;
            case 6:
                mensaje = "El componente ya está asignado";
                break;

        }
        String codigosss = "Código de error 0" + codigo;

        new MaterialDialog.Builder(CrearPedidoActivity.this)
                .title(mensaje)
                .content(codigosss)
                .iconRes(R.drawable.warning)
                .positiveText(R.string.aceptar)
                .show();
    }
}
