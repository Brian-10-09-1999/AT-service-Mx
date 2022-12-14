package com.example.devolucionmaterial;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.dialogs.DialogoBusquedaTecnicoBaja;
import com.example.devolucionmaterial.lists.ListViewAdapterSpinerRegiones;
import com.example.devolucionmaterial.lists.ListViewItem;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.MenuOpciones;

public class Menu_Devolucion extends Activity implements OnItemSelectedListener {
    String metodo;
    Context context;
    ArrayList<ListViewItem> data = new ArrayList<>();
    private ArrayList<String> arrSalas = new ArrayList<String>();
    protected ArrayAdapter<CharSequence> adapter, adapterOpciones;
    private Spinner sala;
    private Spinner spnOpciones;
    private int usuarioID;
    private int regionid;
    private String url;
    private String usuario;
    private String nombreUsuario;
    private String tecnicoidBaja = "0";
    private BDmanager manager;
    public static final String NOMBRE_SALA = "NOMBRE_SALA";
    private static final int CALLING_ID_FOR_ACTIV2 = 111,
            BUSQUEDA_CODE = 1;
    public static String ACTIVIDAD_ORIGEN = "actividadOrigen",
            VERIFICAR_PEDIDO = "verificarPedidox",
            EXTRA_REGION = "region";
    private MenuOpciones mo;
    private Menu mMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__devolucion);
        context = this;
        TextView tvNomUsuario = (TextView) findViewById(R.id.tvUsuariomd);
        mo = new MenuOpciones();
        ImageView imgEmail = (ImageView) findViewById(R.id.imgCorreomd);
        ImageView imgInfo = (ImageView) findViewById(R.id.imgInfomd);
        ImageView imgCallCC = (ImageView) findViewById(R.id.imgLlamarmd);
        manager = new BDmanager(context);
        sala = (Spinner) findViewById(R.id.sala);
        Spinner spnRegion = (Spinner) findViewById(R.id.spnRegion);
        spnOpciones = (Spinner) findViewById(R.id.spnOpciones);
        TextView tvRegion = (TextView) findViewById(R.id.tvRegion);

        Intent intReceptor = this.getIntent();
        Bundle bndReceptor = intReceptor.getExtras();
        usuarioID = bndReceptor.getInt("usuarioidx");
        url = bndReceptor.getString("urlx");
        usuario = bndReceptor.getString("usuariox");
        regionid = bndReceptor.getInt("regionidx");
        Log.i("hola", "region: " + regionid);
        int tipo = bndReceptor.getInt("tipo");
        nombreUsuario = bndReceptor.getString("nombreusuariox");

        tvNomUsuario.setText(nombreUsuario);

        if (tipo == 1) {
            tvRegion.setVisibility(View.VISIBLE);
            spnRegion.setVisibility(View.VISIBLE);
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

        } else {
            llenaComboSala(regionid);
        }

        //Asignas el origen de datos desde los recursos
        adapterOpciones = ArrayAdapter.createFromResource(this, R.array.Opciones, android.R.layout.simple_spinner_item);
        //Asignas el layout a inflar para cada elemento al momento de desplegar la lista
        adapterOpciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Seteas el adaptador
        spnOpciones.setAdapter(adapterOpciones);
        spnOpciones.setSelection(0);
        spnOpciones.setOnItemSelectedListener(this);

        Button btncrear = (Button) this.findViewById(R.id.btncrear);

        btncrear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                agregaSalida();
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
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int idSpinner = parent.getId();
        switch (idSpinner) {
            case R.id.sala:
                int seleccion = position + 1;
                llenaComboSala(seleccion);
                break;
            case R.id.spnOpciones:
                if (spnOpciones.getSelectedItemPosition() == 1) {
                    Intent intent = new Intent(context, DialogoBusquedaTecnicoBaja.class);
                    intent.putExtra(EXTRA_REGION, "" + regionid);
                    startActivityForResult(intent, BUSQUEDA_CODE);
                }
                break;
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
        }
    }

    public void llenaComboSala(int regionID) {
        metodo = "llenaComboSala()";
        //MensajeEnConsola.log(context, metodo, "regionidfk = "+regionID);
        arrSalas.clear();
        Cursor fila = manager.consulta("SELECT nombre FROM csala WHERE regionidfk= '" + regionID + "' ORDER BY nombre");
        MensajeEnConsola.log(context, metodo, "SELECT nombre FROM csala WHERE regionidfk= '" + regionID + "' ORDER BY nombre");
        for (fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
            arrSalas.add(fila.getString(0));
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrSalas);
        sala.setAdapter(adapter2);
    }

    public void agregaSalida() {
        String valSala = sala.getSelectedItem().toString();
//        BDhelper admin = new BDhelper(this);
//        SQLiteDatabase bd = admin.getWritableDatabase();

        manager.actualiza("insert into csalida(usuarioidfk, officeID, fecha, estatus)" +
                "values(" + usuarioID + ",(select officeID from csala where nombre='" + valSala + "'),date('now'),'1')");
        Cursor fila = manager.consulta("SELECT salidaid,officeID FROM csalida ORDER BY salidaid desc LIMIT 1");

        fila.moveToFirst();
        int salidaid = fila.getInt(0);
        int officeID = fila.getInt(1);
        fila.close();
        manager.insertarSala(valSala);
        Cursor cursorSala = manager.cargarCursorSala();
        cursorSala.moveToLast();
        int idSala = cursorSala.getInt(0);
        cursorSala.close();

        String tipo = "m";
        if (spnOpciones.getSelectedItemPosition() == 0)
            tipo = "ORDINARIA";
        else if (spnOpciones.getSelectedItemPosition() == 1)
            tipo = "BAJA";

        manager.insertarSalida(idSala, salidaid, usuarioID, Registro.obtenerHora(), "1",
                "Pendiente", officeID, "pendiente", "pendiente", tipo, tecnicoidBaja);

        String pedido = "0";
        Intent intent;
        intent = new Intent(context, Registro.class);
        Bundle bndMyBundle = new Bundle();
        bndMyBundle.putInt("usuarioidx", (usuarioID));
        bndMyBundle.putString("usuariox", (usuario));
        bndMyBundle.putString("urlx", (url));
        bndMyBundle.putInt("salidaidx", (salidaid));
        bndMyBundle.putInt("officeIDx", (officeID));
        bndMyBundle.putString(NOMBRE_SALA, valSala);
        bndMyBundle.putString("pedidox", pedido);
        bndMyBundle.putString("nombrex", nombreUsuario);
        bndMyBundle.putInt(ACTIVIDAD_ORIGEN, 0);
        if (tipo.equals("ORDINARIA"))
            bndMyBundle.putString(VERIFICAR_PEDIDO, "si");
        else
            bndMyBundle.putString(VERIFICAR_PEDIDO, "no");

        intent.putExtras(bndMyBundle);

        startActivityForResult(intent, CALLING_ID_FOR_ACTIV2);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == BUSQUEDA_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                tecnicoidBaja = intent.getStringExtra("tecnicoidBajax");
                Alert.Alerta(context, "Menu_Devolucion", 0, getString(R.string.regBaja));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                spnOpciones.setSelection(0);
                tecnicoidBaja = "0";
                Alert.Alerta(context, "Menu_Devolucion", 2, getString(R.string.notRegBaja));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_est_tec);
        try {
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("39")) {
                if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("1")) {
                    item.setIcon(R.drawable.est_activo);
                } else if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("2")) {
                    item.setIcon(R.drawable.est_traslado_sala);
                } else if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("3")) {
                    item.setIcon(R.drawable.est_asignado);
                }
                if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("4")) {
                    item.setIcon(R.drawable.est_en_comida);
                }
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("40")) {
                item.setIcon(R.drawable.est_inactivo);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("109")) {
                item.setIcon(R.drawable.est_incidencia);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("79")) {
                item.setIcon(R.drawable.est_dia_descanso);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("80")) {
                item.setIcon(R.drawable.mix_peq);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("81")) {
                item.setIcon(R.drawable.est_vacaciones);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("89")) {
                item.setIcon(R.drawable.est_incapacidad);
            }
        } catch (Exception e) {
        }
        mMenu = menu;
        actualizaEstatusTecnico();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_est_tec) {
            MenuInicialTecnico.iniciaMenuEstatusTec(context);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onResume() {
        super.onResume();
        actualizaEstatusTecnico();
    }

    public void actualizaEstatusTecnico() {
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_est_tec);
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("39")) {
                if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("1")) {
                    item.setIcon(R.drawable.est_activo);
                } else if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("2")) {
                    item.setIcon(R.drawable.est_traslado_sala);
                } else if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("3")) {
                    item.setIcon(R.drawable.est_asignado);
                }
                if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("4")) {
                    item.setIcon(R.drawable.est_en_comida);
                }
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("40")) {
                item.setIcon(R.drawable.est_inactivo);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("109")) {
                item.setIcon(R.drawable.est_incidencia);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("79")) {
                item.setIcon(R.drawable.est_dia_descanso);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("80")) {
                item.setIcon(R.drawable.mix_peq);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("81")) {
                item.setIcon(R.drawable.est_vacaciones);
            }
            if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("89")) {
                item.setIcon(R.drawable.est_incapacidad);
            }
        }
    }


}
//358