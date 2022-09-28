package com.example.devolucionmaterial.activitys.materialesSeccion;

import java.util.ArrayList;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.lists.Lista_item;
import com.example.devolucionmaterial.activitys.materialesSeccion.adapter.DevolutionAdapter;
import com.thanosfisherman.mayi.PermissionBean;

public class ReporteDeDevoluciones1 extends BaseActivity implements DevolutionAdapter.ClickListener {
    String metodo;
    Context context;
    private ArrayList<Lista_item> listItems = new ArrayList<Lista_item>();
    private BDmanager manager;
    public static String SELECCION_SALA = "SALA";
    public static String SELECCION_SALIDA = "SALIDA";
    public static String SELECCION_URL = "URL";
    public static String SELECCION_SALIDA_MEM = "SALIDAMEM";
    int j = 5, contador, particiones, residuo;
    int material = 0, memorias = 0;
    int materialNew = 0, memoriasNew = 0;
    private String url;
    Bundle bndMyBundle;
    Bundle bndReceptor;
    Intent intReceptor;
    Intent myIntent;
    private Menu mMenu;
    private RecyclerView rvDevolution;
    private DevolutionAdapter devolutionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_pend_env);
        context = this;
        manager = new BDmanager(context);
        initToolbar("Lista de Devoluciones", true, true);
        rvDevolution = (RecyclerView) findViewById(R.id.adpe_rv_dev);

        intReceptor = this.getIntent();
        bndReceptor = intReceptor.getExtras();
        myIntent = this.getIntent();
        url = bndReceptor.getString("urlx");

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




    @SuppressWarnings("deprecation")
    private void initSetUp() {

        try {
            String sala, salida, folioString;
            int salidaMemorias;
            int idSala, statusMemoria;
            listItems.clear();
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
                        listItems.add(new Lista_item(sala + " - " + salida, getString(R.string.titDevMat), "#fdd835"));
                    else
                        listItems.add(new Lista_item(sala + " - " + salida, getString(R.string.titDevMat), "#7cb342"));
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


            setAdaptador(listItems);


        } catch (IndexOutOfBoundsException e) {
            //Toast.makeText(ReporteDeDevoluciones1.this, e + "", Toast.LENGTH_LONG).show();
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




    private void setAdaptador(ArrayList<Lista_item> listaDeItems) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
     /*   DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvDevolution.getContext(),
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.sk_line_divider));
        rvDevolution.addItemDecoration(dividerItemDecoration);*/
        devolutionAdapter = new DevolutionAdapter(this, listaDeItems);
        devolutionAdapter.setClickListener(this);
        rvDevolution.setLayoutManager(linearLayoutManager);
        rvDevolution.setAdapter(devolutionAdapter);
        rvDevolution.hasFixedSize();
        devolutionAdapter.notifyDataSetChanged();
    }

    @Override
    public void itemClicked(int position) {
        Lista_item item = listItems.get(position);

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

    public void onRestart() {
        super.onRestart();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }else{
            initSetUp();
        }
    }



    public void onResume() {
        super.onResume();
        actualizaEstatusTecnico();
    }




}
//338
