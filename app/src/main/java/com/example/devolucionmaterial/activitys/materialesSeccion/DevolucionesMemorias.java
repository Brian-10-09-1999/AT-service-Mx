package com.example.devolucionmaterial.activitys.materialesSeccion;

import java.util.ArrayList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.devolucionmaterial.MenuInicialTecnico;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.RegistroDeMemorias;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.lists.Lista_adaptador;
import com.example.devolucionmaterial.lists.Lista_item;
import com.thanosfisherman.mayi.PermissionBean;

public class DevolucionesMemorias extends BaseActivity {
    String metodo;
    Context context;
    private TextView salida, hora, fecha, status;
    private BDmanager manager;
    int folioInt, OSInt, cantidadInt, statusInt;
    private String tipo, folioString, OSString;
    private LinearLayout layoutStatusMemoria;
    final ArrayList<Lista_item> datos = new ArrayList<Lista_item>();
    private ListView listaReportes;
    private Bundle bndMyBundle;
    private int usuarioID;
    private int salidaid;
    private String url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte_devolucion_memorias);
        initToolbar("Menú de Reportes", true, true);

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
        String[] fechaString;
        datos.clear();
        manager = new BDmanager(this);
        layoutStatusMemoria = (LinearLayout) findViewById(R.id.layoutStatusMemorias);
        salida = (TextView) findViewById(R.id.tvSalidaMem);
        fecha = (TextView) findViewById(R.id.tvFechaMem);
        hora = (TextView) findViewById(R.id.tvHoraMem);
        status = (TextView) findViewById(R.id.tvStatusMem);


        //Obteniendo la instancia del Intent
        Intent intent = getIntent();

        //Extrayendo el extra de tipo cadena
        salidaid = Integer.valueOf(intent.getStringExtra(ReporteDeDevoluciones1.SELECCION_SALIDA_MEM));
        url = intent.getStringExtra(ReporteDeDevoluciones1.SELECCION_URL);

        Cursor cursorSalidasMemoria = manager.buscarSalidaMemoriaID(salidaid);
        cursorSalidasMemoria.moveToFirst();


        fechaString = cursorSalidasMemoria.getString(2).split("/");
        statusInt = cursorSalidasMemoria.getInt(3);

        if (statusInt == 0) {
            layoutStatusMemoria.setBackgroundColor(Color.YELLOW);
            status.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            status.setText("¡Pendiente!   Toca para terminar la salida");
            usuarioID = cursorSalidasMemoria.getInt(1);
        } else {
            status.setText("Salida Enviada");
            layoutStatusMemoria.setBackgroundColor(Color.parseColor("#C0C0C0"));
        }
        fecha.setText(fechaString[0]);
        hora.setText(fechaString[1]);
        Cursor cursorPiezaSalidasMemoria = manager.buscarPiezaSalidaMemoriaID(salidaid);
        cursorPiezaSalidasMemoria.moveToFirst();
        try {
            do {
                tipo = cursorPiezaSalidasMemoria.getString(6);
                folioInt = cursorPiezaSalidasMemoria.getInt(2);

                cantidadInt = cursorPiezaSalidasMemoria.getInt(3);
                OSInt = cursorPiezaSalidasMemoria.getInt(4);
                Log.e("tipo", tipo);
                Log.e("folioInt", String.valueOf(folioInt));
                Log.e("cantidadInt", String.valueOf(cantidadInt));
                Log.e("OSInt", String.valueOf(OSInt));
                if (folioInt == 0)
                    folioString = "NA";
                else
                    folioString = "" + folioInt;

                if (OSInt == 0)
                    OSString = "NA";
                else
                    OSString = "" + OSInt;
                datos.add(new Lista_item(tipo, folioString, OSString, "" + cantidadInt, ""));
            } while (cursorPiezaSalidasMemoria.moveToNext());
        } catch (IndexOutOfBoundsException e) {
            datos.clear();

        }

        salida.setText(String.valueOf(salidaid));

        listaReportes = (ListView) findViewById(R.id.listaReportesMemorias);

        listaReportes.setOnTouchListener(new ListView.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        listaReportes.setAdapter(new Lista_adaptador(this, R.layout.item_lista_memorias, datos) {
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    TextView tvCodigo = (TextView) view.findViewById(R.id.tvTipoMem);
                    if (tvCodigo != null)
                        tvCodigo.setText(((Lista_item) entrada).getCodigo());

                    TextView tvDescripcion = (TextView) view.findViewById(R.id.tvfolioMem);
                    if (tvDescripcion != null)
                        tvDescripcion.setText(((Lista_item) entrada).getDescripcion());

                    TextView tvCantidad = (TextView) view.findViewById(R.id.tvOSMem);
                    if (tvCantidad != null)
                        tvCantidad.setText(((Lista_item) entrada).getCantidad());

                    TextView tvSerie = (TextView) view.findViewById(R.id.tvCantidadMem);
                    if (tvSerie != null)
                        tvSerie.setText(((Lista_item) entrada).getSerie());


                }
            }
        });
        layoutStatusMemoria.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusInt == 0) {
                    Intent intent;
                    intent = new Intent(DevolucionesMemorias.this, RegistroDeMemorias.class);
                    bndMyBundle = new Bundle();
                    bndMyBundle.putInt("usuarioidx", (usuarioID));
                    bndMyBundle.putString("urlx", (url));
                    bndMyBundle.putInt("salidaidx", (salidaid));
                    bndMyBundle.putInt(MenuInicialTecnico.ACTIVIDAD_ORIGEN_MEMORIAS, 1);
                    intent.putExtras(bndMyBundle);
                    startActivity(intent);

                }
            }
        });

    }

    public void onRestart() {
        super.onRestart();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }else{
            initSetUp();
        }
    }


}
