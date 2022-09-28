package com.example.devolucionmaterial;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.lists.Lista_adaptador;
import com.example.devolucionmaterial.lists.Lista_item;

public class ReporteDeDevoluciones1 extends Activity {
    String metodo;
    Context context;
    private ListView listaDevoluciones;
    private ArrayList<Lista_item> listItems = new ArrayList<Lista_item>();
    private BDmanager manager;
    public static String SELECCION_SALA = "SALA";
    public static String SELECCION_SALIDA = "SALIDA";
    public static String SELECCION_URL = "URL";
    public static String SELECCION_SALIDA_MEM = "SALIDAMEM";
    int j = 5, contador, particiones, residuo, totalItems;
    int material = 0, memorias = 0;
    int materialNew = 0, memoriasNew = 0;
    private String url;
    Bundle bndMyBundle;
    Bundle bndReceptor;
    Intent intReceptor;
    Intent myIntent;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_pend_env);
        context = this;
        manager = new BDmanager(context);
        listaDevoluciones = (ListView) findViewById(R.id.lvRepPendEnv);
        listaDevoluciones.setOnItemClickListener(seleccion);
        intReceptor = this.getIntent();
        bndReceptor = intReceptor.getExtras();
        myIntent = this.getIntent();
        url = bndReceptor.getString("urlx");

        llenarLista();
    }

    public OnItemClickListener seleccion = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int posicion,
                                long id) {
            Lista_item item = (Lista_item) listaDevoluciones.getItemAtPosition(posicion);
            if (item.gettxtSuperior().equals(getString(R.string.mosMasDev))) {
                contador++;
                if (contador != particiones) {
                    j = j + 5;
                    llenarLista();
                } else {
                    j = j + residuo;
                    llenarLista();
                }
            } else {
                String itemTxtSuperior = item.gettxtSuperior();
                String[] cadena = itemTxtSuperior.split(" - ");
                if (cadena[0].equals(getString(R.string.salMem))) {
                    Intent intent = new Intent(context, DevolucionesMemorias.class);
                    intent.putExtra(SELECCION_SALIDA_MEM, cadena[1]);
                    intent.putExtra(SELECCION_URL, url);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, DevolucionesEnviadas.class);
                    intent.putExtra(SELECCION_SALA, cadena[0]);
                    intent.putExtra(SELECCION_SALIDA, cadena[1]);
                    intent.putExtra(SELECCION_URL, url);
                    startActivity(intent);
                }
            }
        }
    };

    @SuppressWarnings("deprecation")
    private void llenarLista() {
        String sala, salida, folioString;
        int salidaMemorias;
        int idSala, statusMemoria;
        listItems.clear();
        try {
            Cursor cursorSalidas = manager.cargarCursorSalidas();
            cursorSalidas.moveToFirst();
            Cursor cursorSalidasmemorias = manager.cargarCursorSalidasMemoria();
            cursorSalidasmemorias.moveToFirst();

            if (cursorSalidas.getCount() == 0 && cursorSalidasmemorias.getCount() == 0) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(getString(R.string.notInfo));
                alertDialog.setMessage(getString(R.string.devInfo));
                alertDialog.setButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.setIcon(R.drawable.ic_launcher);
                alertDialog.show();
            }
            Cursor buscarSala;
            if (cursorSalidas.getCount() > 0) {

                material = 0;
                do {
                    salida = Integer.toString(cursorSalidas.getInt(2));
                    idSala = cursorSalidas.getInt(1);
                    buscarSala = manager.buscarSalaPorId(idSala);
                    buscarSala.moveToLast();
                    sala = buscarSala.getString(1);
                    folioString = cursorSalidas.getString(6);
                    if (folioString.equals("Pendiente"))
                        listItems.add(new Lista_item(sala + " - " + salida, getString(R.string.titDevMat), "#ffff00"));
                    else
                        listItems.add(new Lista_item(sala + " - " + salida, getString(R.string.titDevMat), "#2EFE2E"));
                    material++;
                } while (cursorSalidas.moveToNext());
            }
            try {
                do {
                    salidaMemorias = cursorSalidasmemorias.getInt(0);
                    statusMemoria = cursorSalidasmemorias.getInt(3);
                    if (statusMemoria == 1)
                        listItems.add(new Lista_item(getString(R.string.salMem) + " - " + salidaMemorias, getString(R.string.titDevMem), "#2EFE2E"));
                    else
                        listItems.add(new Lista_item(getString(R.string.salMem) + " - " + salidaMemorias, getString(R.string.titDevMem), "#ffff00"));
                    memorias++;
                } while (cursorSalidasmemorias.moveToNext());
            } catch (IndexOutOfBoundsException ignored) {
            }


            totalItems = listItems.size();
            if (totalItems <= 5) {
                setAdaptador(listaDevoluciones, listItems);
            } else {
                particiones = totalItems / 5;
                residuo = totalItems % 5;
                listItems.clear();
                materialNew = 0;
                cursorSalidas = manager.cargarCursorSalidas();
                cursorSalidas.moveToFirst();
                cursorSalidasmemorias = manager.cargarCursorSalidasMemoria();
                cursorSalidasmemorias.moveToFirst();
                for (int h = 0; h < j; h++) {
                    materialNew++;
                    Log.d("Material", "Material = " + material + "---MaterialNew= " + materialNew);
                    if (materialNew <= material) {
                        salida = Integer.toString(cursorSalidas.getInt(2));
                        idSala = cursorSalidas.getInt(1);
                        buscarSala = manager.buscarSalaPorId(idSala);
                        buscarSala.moveToLast();
                        sala = buscarSala.getString(1);
                        folioString = cursorSalidas.getString(6);
                        if (folioString.equals("Pendiente"))
                            listItems.add(new Lista_item(sala + " - " + salida, getString(R.string.titDevMat), "#ffff00"));
                        else
                            listItems.add(new Lista_item(sala + " - " + salida, getString(R.string.titDevMat), "#2EFE2E"));
                        cursorSalidas.moveToNext();
                    } else {
                        salidaMemorias = cursorSalidasmemorias.getInt(0);
                        statusMemoria = cursorSalidasmemorias.getInt(3);
                        if (statusMemoria == 1)
                            listItems.add(new Lista_item(getString(R.string.salMem) + " - " + salidaMemorias, getString(R.string.titDevMem), "#2EFE2E"));
                        else
                            listItems.add(new Lista_item(getString(R.string.salMem) + " - " + salidaMemorias, getString(R.string.titDevMem), "#ffff00"));
                        memoriasNew++;
                        if (memoriasNew != memorias)
                            cursorSalidasmemorias.moveToNext();
                    }

                }
                if (j < totalItems)
                    listItems.add(new Lista_item(getString(R.string.mosMasDev), getString(R.string.mosCincoDev), "#FE2EF7"));
                setAdaptador(listaDevoluciones, listItems);

            }

        } catch (IndexOutOfBoundsException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(getString(R.string.notInfo));
            alertDialog.setMessage(getString(R.string.devInfo));
            alertDialog.setButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.setIcon(R.drawable.ic_launcher);
            alertDialog.show();

        }
    }

    private void setAdaptador(ListView lista, ArrayList<Lista_item> listaDeItems) {
        lista.setAdapter(new Lista_adaptador(this, R.layout.item_lista_dev_pend_env, listaDeItems) {
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    TextView tvTextoSuperior = (TextView) view.findViewById(R.id.tvSuperior);
                    if (tvTextoSuperior != null)
                        tvTextoSuperior.setText(((Lista_item) entrada).gettxtSuperior());

                    TextView tvTextoInferior = (TextView) view.findViewById(R.id.tvInferior);
                    if (tvTextoInferior != null)
                        tvTextoInferior.setText(((Lista_item) entrada).gettxtInferior());

                    LinearLayout l = (LinearLayout) view.findViewById(R.id.layoutListaDevPendEnv);
                    if (l != null)
                        l.setBackgroundColor(Color.parseColor((((Lista_item) entrada).getColor())));

                }
            }
        });
    }

    public void onRestart() {
        super.onRestart();
        llenarLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_est_tec);
        Cursor cursorEstatusTec = manager.cargarCursorEstatusTec();
        cursorEstatusTec.moveToFirst();

        try {

            if (cursorEstatusTec.getString(1).equals("39")) {
                if (cursorEstatusTec.getString(4).equals("1")) {
                    item.setIcon(R.drawable.est_activo);
                } else if (cursorEstatusTec.getString(4).equals("2")) {
                    item.setIcon(R.drawable.est_traslado_sala);
                } else if (cursorEstatusTec.getString(4).equals("3")) {
                    item.setIcon(R.drawable.est_asignado);
                }
                if (cursorEstatusTec.getString(4).equals("4")) {
                    item.setIcon(R.drawable.est_en_comida);
                }
            }
            if (cursorEstatusTec.getString(1).equals("40")) {
                item.setIcon(R.drawable.est_inactivo);
            }
            if (cursorEstatusTec.getString(1).equals("109")) {
                item.setIcon(R.drawable.est_incidencia);
            }
            if (cursorEstatusTec.getString(1).equals("79")) {
                item.setIcon(R.drawable.est_dia_descanso);
            }
            if (cursorEstatusTec.getString(1).equals("80")) {
                item.setIcon(R.drawable.mix_peq);
            }
            if (cursorEstatusTec.getString(1).equals("81")) {
                item.setIcon(R.drawable.est_vacaciones);
            }
            if (cursorEstatusTec.getString(1).equals("89")) {
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
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
//338
