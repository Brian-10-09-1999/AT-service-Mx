package com.example.devolucionmaterial;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

public class Lista_Devolucion extends Activity {
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
    private ProgressDialog pDialog;
    private String[] valoresSeleccion;
    private String pedido, noGuia, tipo, tecnicoidBaja;
    private Menu mMenu;

    public final static int EDICION_REQUEST_CODE = 1;
    public static final String EXTRA_CODIGO = "codigo";
    public static final String EXTRA_CANTIDAD = "cantidad";
    public static final String EXTRA_SERIE = "serie";
    public static final String EXTRA_ID_PIEZA = "idPieza";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__devolucion);
        context = this;
        setupLista_Devolucion();
    }

    void setupLista_Devolucion() {
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
                        new Progreso().execute(url + "ingresaSalidaMat.php?usuariox=" + usuarioID + "&cadenax=" + cadena + "&officeIDx=" + officeID + "&salidaidx=" + salidaid + "&pedidox=" + pedido + "&guiax=" + noGuia + "&tipox=" + tipo + "&tecnicoBajaidx=" + tecnicoidBaja);
                    } else {
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

    public void llenaLista() {
        metodo = "llenaLista()";
        Cursor piezasSalidaActual = manager.buscarReportesAsociados(salidaid);
        piezasSalidaActual.moveToFirst();
        for (piezasSalidaActual.moveToFirst(); !piezasSalidaActual.isAfterLast(); piezasSalidaActual.moveToNext()) {
            listItems.add(piezasSalidaActual.getString(0) + " - " + piezasSalidaActual.getString(2) + " - " + piezasSalidaActual.getInt(4));
            cadena = cadena + piezasSalidaActual.getString(2) + "x-x" + piezasSalidaActual.getInt(4) + "x-x" + piezasSalidaActual.getString(5) + "*-*";
        }

		/*BDhelper admin = new BDhelper(this);
		SQLiteDatabase bd = admin.getWritableDatabase();
		Cursor fila = bd.rawQuery(
				"select * from rpiezas where salidaidfk= "+salidaid+"", null);
		tvSalida.setText("SALIDA NO." + salidaid);
		for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext())
	    {
			listItems.add(fila.getString(1)+" - "+fila.getInt(2));
			cadena=cadena+fila.getString(1)+"x-x"+fila.getInt(2)+"x-x"+fila.getString(3)+"*-*";
	    }*/
        if (listItems.isEmpty()) {
            btnEnviar.setEnabled(false);
            Alert.Alerta(context, metodo, 1, getString(R.string.notDev));
        } else
            list.setAdapter(adapter);
        //bd.close();
    }

    private class Progreso extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        @Override
        protected String doInBackground(String... url) {
            metodo = "Progreso<AsyncTask>.doInBackground()";
            String regresa = "";
            try {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_est_tec);
        Cursor cursorEstatusTec = manager.cargarCursorEstatusTec();
        cursorEstatusTec.moveToFirst();
        try{
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
        }catch (Exception e){
            Log.e("error menu", String.valueOf(e));
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
//355