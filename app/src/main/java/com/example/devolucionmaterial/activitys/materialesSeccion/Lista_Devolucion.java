package com.example.devolucionmaterial.activitys.materialesSeccion;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.EdicionRegistroMaterial;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.ingresaSalidaMat2;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.thanosfisherman.mayi.PermissionBean;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Lista_Devolucion extends BaseActivity {
    String metodo;
    Context context;

    Bundle bndMyBundle;
    Bundle bndReceptor;
    Intent intReceptor;
    Intent myIntent;
    private int usuarioID, salidaid, officeID, actividadOrigen;
    String url, cadena;
    TextView tvSalida, tvUsuario;
    ListView list;
    Button btnEnviar;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private EditText etNoGuia;
    private int colAfectadas = -2, codigoHTTP = 0;
    private BDmanager manager;
    private String[] valoresSeleccion;
    private String pedido, noGuia, tipo, tecnicoidBaja, maquina;
    private Menu mMenu;

    public final static int EDICION_REQUEST_CODE = 1;
    public static final String EXTRA_CODIGO = "codigo";
    public static final String EXTRA_CANTIDAD = "cantidad";
    public static final String EXTRA_SERIE = "serie";
    public static final String EXTRA_ID_PIEZA = "idPieza";

    private JSONArray jsonArrayPiezas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__devolucion);
        initToolbar("Lista de Devoluciones", true, true);
        context = this;
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

    void initSetUp() {
        manager = new BDmanager(this);
        cadena = "";
        etNoGuia = (EditText) findViewById(R.id.etNoGuia);
        intReceptor = this.getIntent();
        bndReceptor = intReceptor.getExtras();
        myIntent = this.getIntent();
        usuarioID = bndReceptor.getInt("usuarioidx");
        url = bndReceptor.getString("urlx");
        String nombre = bndReceptor.getString("nombrex");
        salidaid = bndReceptor.getInt("salidaidx");
        officeID = bndReceptor.getInt("officeIDx");
        pedido = bndReceptor.getString("pedidox");
        maquina = bndReceptor.getString("maquina");

        actividadOrigen = bndReceptor.getInt(Menu_Devolucion.ACTIVIDAD_ORIGEN);
        ImageView imgMensajeria = (ImageView) findViewById(R.id.imgMensajeria);
        ImageView imgTaxi = (ImageView) findViewById(R.id.imgTaxi);
        tvSalida = (TextView) findViewById(R.id.tvSalida);

        list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(seleccion);
        tvUsuario = (TextView) findViewById(R.id.tvUsuariold);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        tvSalida.setText(getString(R.string.salidaNo) + salidaid);
        tvUsuario.setText(nombre);
        btnEnviar = (Button) this.findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                noGuia = etNoGuia.getText().toString().replace(" ", "");
                if (noGuia.length() >= 4) {









                    if (CheckService.internet(context)) {

                        pDialog = new MaterialDialog.Builder(context)
                                .title(context.getString(R.string.Conectando_con_servidor_remoto))
                                .content("Cargando...")
                                .progress(true, 0)
                                .cancelable(false)
                                .progressIndeterminateStyle(false)
                                .show();


                        // TODO: 07/08/2017 se regresa a la conexion anterior aun no se livera la del json
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("usuariox", usuarioID);
                            jsonObject.put("officeIDx", officeID);
                            jsonObject.put("salidaidx", salidaid);
                            jsonObject.put("pedidox", pedido);
                            jsonObject.put("guiax", noGuia);
                            jsonObject.put("tipo", tipo);
                            jsonObject.put("tecnicoBajaidx", tecnicoidBaja);
                            jsonObject.put("maquina", maquina);
                            jsonObject.put("piezas", jsonArrayPiezas);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.e("JSON", String.valueOf(jsonObject));

                        RequestBody body =
                                RequestBody.create(MediaType.parse("application/json"), String.valueOf(jsonObject));
                        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
                        Call<ingresaSalidaMat2> call = serviceApi.sendDevolcionDeMaterial(body);

                        call.enqueue(new Callback<ingresaSalidaMat2>() {
                            @Override
                            public void onResponse(Call<ingresaSalidaMat2> call, Response<ingresaSalidaMat2> response) {
                                pDialog.dismiss();
                                if (response.body().getRes() == 1) {
                                    Alert.Alerta(context, metodo, 0, getResources().getString(R.string.datCorrect));
                                    colAfectadas = manager.actualizarRegistroReporte(response.body().getDevolucionid(), Registro.obtenerHora(), salidaid);
                                    if (actividadOrigen == 0) {
                                        startActivity(new Intent(context, Menu_Devolucion.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                    } else {
                                        startActivity(new Intent(context, ReporteDeDevoluciones1.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                    }
                                }

                                if(response.body().getRes()==0){  Alert.Alerta(context, metodo, 0,"Error En la devolución");  }
                                if(response.body().getRes()==2){  Alert.Alerta(context, metodo, 0,"No se puede devolver un componente que esta asociado a una Máquina");}

                            }

                            @Override
                            public void onFailure(Call<ingresaSalidaMat2> call, Throwable t) {
                                pDialog.dismiss();
                                Alert.Alerta(context, metodo, 2, getResources().getString(R.string.errSal));
                            }
                        });
                        // new Progreso().execute(url + "ingresaSalidaMat.php?usuariox=" + usuarioID + "&cadenax=" + cadena + "&officeIDx=" + officeID + "&salidaidx=" + salidaid + "&pedidox=" + pedido + "&guiax=" + noGuia + "&tipox=" + tipo + "&tecnicoBajaidx=" + tecnicoidBaja);


                    }





                    else {
                        Alert.ActivaInternet(context);
                    }





                } else if (noGuia.length() == 0)
                    Alert.Alerta(context, "btnEnviar.setOnClickListener()", 1, getString(R.string.notNoGuia));
                else if (noGuia.length() < 4)
                    Alert.Alerta(context, "btnEnviar.setOnClickListener()", 1, getString(R.string.guiaIncom));
            }
        });
        llenaLista();

        Cursor cursorSalidas = manager.buscarSalidasPorId(salidaid);
        cursorSalidas.moveToLast();
        tipo = cursorSalidas.getString(10);
        tecnicoidBaja = cursorSalidas.getString(11);

        imgMensajeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                etNoGuia.setText(getString(R.string.mensajeria));
            }
        });

        imgTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                etNoGuia.setText(getString(R.string.taxi));
            }
        });
    }

    private class Progreso extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {

            pDialog = new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.Conectando_con_servidor_remoto))
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();
        }

        @Override
        protected String doInBackground(String... url) {
            metodo = "Progreso<AsyncTask>.doInBackground()";
            String regresa = "";
            try {
                Log.e("url envio ", url[0]);
                JSONObject object = JSONparse.consultaURL(context, metodo, url[0]);
                assert object != null;
                regresa = object.getString("res");
            } catch (JSONException e) {
                MensajeEnConsola.log(context, metodo, "JSONException e = " + e.getMessage());
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
            }
            return regresa;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            pDialog.dismiss();
            String[] result = res.split("--");
            if (result[0].equals("0")) {
                Alert.Alerta(context, metodo, 0, getResources().getString(R.string.datCorrect));
                colAfectadas = manager.actualizarRegistroReporte(result[1], Registro.obtenerHora(), salidaid);
                if (actividadOrigen == 0) {
                    startActivity(new Intent(context, Menu_Devolucion.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                } else {
                    startActivity(new Intent(context, ReporteDeDevoluciones1.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                }
            } else {
                Alert.Alerta(context, metodo, 2, getResources().getString(R.string.errSal));
            }
        }
    }

    public void llenaLista() {
        jsonArrayPiezas = new JSONArray();

        metodo = "llenaLista()";
        Cursor piezasSalidaActual = manager.buscarReportesAsociados(salidaid);
        piezasSalidaActual.moveToFirst();
        for (piezasSalidaActual.moveToFirst(); !piezasSalidaActual.isAfterLast(); piezasSalidaActual.moveToNext()) {
            listItems.add(piezasSalidaActual.getString(0) + " - " + piezasSalidaActual.getString(2) + " - " + piezasSalidaActual.getInt(4));
            cadena = cadena + piezasSalidaActual.getString(2) + "x-x" + piezasSalidaActual.getInt(4) + "x-x" + piezasSalidaActual.getString(5) + "*-*";

            JSONObject jsonObjectPiezas = new JSONObject();
            try {
                jsonObjectPiezas.put("pieza", piezasSalidaActual.getString(2));
                jsonObjectPiezas.put("cantidad", piezasSalidaActual.getString(4));
                jsonObjectPiezas.put("serie", piezasSalidaActual.getString(5));
                jsonArrayPiezas.put(jsonObjectPiezas);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.e("cadena", cadena);

        if (listItems.isEmpty()) {
            btnEnviar.setEnabled(false);
            Alert.Alerta(context, metodo, 1, getString(R.string.notDev));
        } else
            list.setAdapter(adapter);
        //bd.close();
    }


    public OnItemClickListener seleccion = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long id) {
            String cadenaSeleccion = list.getItemAtPosition(posicion).toString();
            valoresSeleccion = cadenaSeleccion.split(" - ");
            mostrarDialogoEditar();
        }
    };

    public void mostrarDialogoEditar() {
        final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(context);
        alertaSimple.setTitle(getString(R.string.editRegMat));
        alertaSimple.setMessage(getString(R.string.infoEdRegMat1) + valoresSeleccion[1] + getString(R.string.infoEdRegMat2)
                + valoresSeleccion[2] + "?");
        alertaSimple.setPositiveButton(getString(R.string.si),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, EdicionRegistroMaterial.class);
                        Cursor cursorRegistroAEditar = manager.buscarRegistroPieza(Integer.valueOf(valoresSeleccion[0]));
                        cursorRegistroAEditar.moveToFirst();
                        bndMyBundle = new Bundle();
                        bndMyBundle.putString(EXTRA_ID_PIEZA, valoresSeleccion[0]);
                        bndMyBundle.putInt(EXTRA_CODIGO, cursorRegistroAEditar.getInt(2));
                        bndMyBundle.putInt(EXTRA_CANTIDAD, cursorRegistroAEditar.getInt(4));
                        bndMyBundle.putString(EXTRA_SERIE, cursorRegistroAEditar.getString(5));
                        intent.putExtras(bndMyBundle);
                        startActivityForResult(intent, EDICION_REQUEST_CODE);
                    }
                });
        alertaSimple.setNegativeButton(getString(R.string.cancelar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
        alertaSimple.setIcon(R.drawable.warning);
        alertaSimple.create();
        alertaSimple.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDICION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                cadena = "";
                listItems.clear();
                llenaLista();
            }
        }
    }


    public void onResume() {
        super.onResume();
        actualizaEstatusTecnico();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        actualizaEstatusTecnico();
       /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }*/
    }
}
//355