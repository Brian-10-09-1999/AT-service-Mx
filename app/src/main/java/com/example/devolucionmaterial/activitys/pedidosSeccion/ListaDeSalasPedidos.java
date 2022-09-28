package com.example.devolucionmaterial.activitys.pedidosSeccion;

import java.util.ArrayList;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.devolucionmaterial.PedidosDeSala;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.lists.ListViewAdapterSpinerRegiones;
import com.example.devolucionmaterial.lists.ListViewItem;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.MenuOpciones;
import com.thanosfisherman.mayi.PermissionBean;

public class ListaDeSalasPedidos extends BaseActivity implements OnItemSelectedListener {
    String metodo;
    Context context;
    private Spinner spnSala;
    private String nombreSala, nombre;
    ArrayList<ListViewItem> data = new ArrayList<>();
    ArrayList<String> arrRegiones = new ArrayList<String>();
    ArrayList<String> arrSalas = new ArrayList<String>();
    protected ArrayAdapter<CharSequence> adapter;
    private MenuOpciones mo;
    private Menu mMenu;
    private BDmanager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__devolucion);
        initToolbar("Región y sala", true, true);
        context = this;
        manager = new BDmanager(context);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        } else {
            initSetUp();
        }

    }

    @Override
    public String[] permissions() {
        return new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CALL_PHONE,
                Manifest.permission.GET_ACCOUNTS
        };
    }

    @Override
    protected void permissionResultMulti(PermissionBean[] permissions) {
        Boolean val = true;
        //Toast.makeText(PermissionActivity.this, "MULTI PERMISSION RESULT " + Arrays.deepToString(permissions), Toast.LENGTH_LONG).show();
        for (int i = 0; i < permissions.length; i++) {
            if (!permissions[i].isGranted()) {
                val = false;
            }
        }
        if (val) {
            initSetUp();
        } else {
            showSnackBarPermission();
        }

    }



    private void initSetUp() {
        metodo = "setupListaDeSalasPedidos()";
        TextView tvNomUsuario = (TextView) findViewById(R.id.tvUsuariomd);
        mo = new MenuOpciones();
        ImageView imgEmail = (ImageView) findViewById(R.id.imgCorreomd);
        ImageView imgInfo = (ImageView) findViewById(R.id.imgInfomd);
        ImageView imgCallCC = (ImageView) findViewById(R.id.imgLlamarmd);
        Intent intReceptor = this.getIntent();
        Bundle bndReceptor = intReceptor.getExtras();
        spnSala = (Spinner) findViewById(R.id.sala);
        Spinner spnRegion = (Spinner) findViewById(R.id.spnRegion);
        Spinner spnOpciones = (Spinner) findViewById(R.id.spnOpciones);
        Button btnAceptar = (Button) findViewById(R.id.btncrear);
        TextView tvTitulo = (TextView) findViewById(R.id.tvTituloMenuDevolucion);
        TextView tvRegion = (TextView) findViewById(R.id.tvRegion);
        TextView tvOpciones = (TextView) findViewById(R.id.tvOpciones);

        spnRegion.setVisibility(View.VISIBLE);
        spnSala.setVisibility(View.VISIBLE);
        tvRegion.setVisibility(View.VISIBLE);
        spnOpciones.setVisibility(View.GONE);
        tvOpciones.setVisibility(View.GONE);

        btnAceptar.setText(getString(R.string.aceptar));
        tvTitulo.setText(getString(R.string.salaYreg));
        int regionId = bndReceptor.getInt("regionidx");
        nombre = bndReceptor.getString("nombrex");
        tvNomUsuario.setText(nombre);

        data.clear();

        Cursor fila = manager.consulta("select id, nombre from cregion ORDER BY nombre");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
            data.add(new ListViewItem(fila.getInt(0), fila.getString(1)));
        }
        fila.close();
        ListViewAdapterSpinerRegiones adapterRegiones = new ListViewAdapterSpinerRegiones(this, data);
        spnRegion.setAdapter(adapterRegiones);
        //Asignas el origen de datos desde los recursos
        //adapter = ArrayAdapter.createFromResource(this, R.array.Regiones, android.R.layout.simple_spinner_item);
        //Asignas el layout a inflar para cada elemento al momento de desplegar la lista
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Seteas el adaptador
        //spnRegion.setAdapter(adapter);
        spnRegion.setOnItemSelectedListener(this);
        spnSala.setOnItemSelectedListener(this);

// TODO: 08/03/2017 se pone este if para saber si las regiones son de mexico
        if (regionId == 1 || regionId == 2
                || regionId == 3 || regionId == 3 ||
                        regionId == 4 || regionId == 5
                || regionId == 6 || regionId == 7
                || regionId == 59
                || regionId == 60
                || regionId == 61
                || regionId == 62
                || regionId == 62) {
            if (regionId > 7) {
                spnRegion.setSelection(66 - regionId);
                llenaComboSala(66 - regionId);
            } else {
                spnRegion.setSelection(regionId - 1);
                llenaComboSala(regionId - 1);
            }


        }


        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarDatos();
            }
        });

        imgCallCC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mo.llamarContactCenter(context);
            }
        });

        imgEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mo.enviarCorreo(context);
            }
        });

        imgInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mo.mostrarInfoApp(context);
            }
        });
        try {

        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception e = " + e.toString());
        }
    }

    private void enviarDatos() {
        metodo = "enviarDatos()";
        Intent intent;
        intent = new Intent(ListaDeSalasPedidos.this, PedidosDeSala.class);
        int salaID = manager.consulta("SELECT salaID FROM csala WHERE nombre='" + nombreSala + "'", 0);
        MensajeEnConsola.log(context, metodo, "nombreSala = " + nombreSala + "\nsalaID = " + salaID + "\nnombre = " + nombre);
        Bundle bndMyBundle = new Bundle();
        bndMyBundle.putString("salaIDx", "" + salaID);
        bndMyBundle.putString("nombrex", nombre);
        intent.putExtras(bndMyBundle);
        startActivity(intent);
    }

    private void llenaComboSala(int regionID) {
        metodo = "llenaComboSala()";
        //MensajeEnConsola.log(context, metodo, "regionidfk = "+regionID);
        arrSalas.clear();
        Cursor fila = manager.consulta("select nombre from csala where regionidfk= '" + regionID + "' ORDER BY nombre");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext())
            arrSalas.add(fila.getString(0));
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrSalas);
        spnSala.setAdapter(adapter2);
        fila.close();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int idSpinner = parent.getId();
        switch (idSpinner) {
            case R.id.spnRegion:
                TextView idR = (TextView) view.findViewById(R.id.id_spiner_id);
                llenaComboSala(Integer.valueOf(idR.getText().toString()));
            /*int seleccion;
            if(position>6){
				seleccion = 52+position;
			}else{
				seleccion = position+1;
			}
			*/
                break;
            case R.id.sala:
                nombreSala = spnSala.getSelectedItem().toString();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }


    public void onResume() {
        super.onResume();
        actualizaEstatusTecnico();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        actualizaEstatusTecnico();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }
    }
}
//294