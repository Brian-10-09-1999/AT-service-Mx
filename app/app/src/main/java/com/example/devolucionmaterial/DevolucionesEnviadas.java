package com.example.devolucionmaterial;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.beans.Refacciones;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.lists.Lista_adaptador;
import com.example.devolucionmaterial.lists.Lista_item;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevolucionesEnviadas extends Activity {
    String metodo;
    Context context;
    private BDmanager manager;
    private int IDSalida;
    final ArrayList<Lista_item> datos = new ArrayList<Lista_item>();
    private Bundle bndMyBundle;
    private int usuarioID;
    private int salidaid;
    private int officeID;
    private String url;
    private String valSala;
    private Menu mMenu;
    int ToqueInicialX, posicionX = 0;
    HorizontalScrollView horizontalScrollView;
    ListView listaReportes;
    private ProgressDialog pDialog;
    private TextView estatusDev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte_devoluciones_enviadas);
        context = this;
        setupDevolucionesEnviadas();
    }

    private void devolucionDetalles(int folioInt) {

        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanResponse> call = serviceApi.RREPORTE_DEVELUCION_DETALLES(folioInt);
        call.enqueue(new Callback<BeanResponse>() {
            @Override
            public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                pDialog.dismiss();
                Log.e("url retrofit", String.valueOf(call.request().url()));
                try {
                    if (Integer.valueOf(response.body().getEstatus()) == 1) {
                        estatusDev.setText("Pendiente");
                    } else {
                        estatusDev.setText("Entregado");
                    }
                    for (int i = 0; i < response.body().getRefacciones().size(); i++) {
                        String estatus = "";
                        Refacciones item = response.body().getRefacciones().get(i);
                        if (Integer.valueOf(item.getEstatus()) == 1)
                            estatus = "Pendiente";
                        else
                            estatus = "Entregado";
                        datos.add(new Lista_item(
                                item.getClave(),
                                item.getNombre(),
                                item.getCantidad(),
                                item.getSerie(),
                                estatus));

                    }
                    listaMateriales();
                } catch (Exception e) {
                    pDialog.dismiss();
                    Log.e("error", String.valueOf(e));
                }
            }

            @Override
            public void onFailure(Call<BeanResponse> call, Throwable t) {

                pDialog.dismiss();
            }
        });
    }

    private void setupDevolucionesEnviadas() {
        metodo = "setupDevolucionesEnviadas()";
        final String folioString;
        String[] fechaString;
        datos.clear();
        manager = new BDmanager(context);
        LinearLayout layoutFolio = (LinearLayout) findViewById(R.id.layoutFolio);
        TextView sala = (TextView) findViewById(R.id.tvSalaDev);
        TextView id = (TextView) findViewById(R.id.tvIdDev);
        TextView folio = (TextView) findViewById(R.id.tvFolioDev);
        TextView hora = (TextView) findViewById(R.id.tvHoraDev);
        TextView fecha = (TextView) findViewById(R.id.tvFechaDev);
        estatusDev = (TextView) findViewById(R.id.tvEstatusDev);

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.id_rep_dev_sv);

        //Obteniendo la instancia del Intent
        Intent intent = getIntent();

        //Extrayendo el extra de tipo cadena
        salidaid = Integer.valueOf(intent.getStringExtra(ReporteDeDevoluciones1.SELECCION_SALIDA));
        valSala = intent.getStringExtra(ReporteDeDevoluciones1.SELECCION_SALA);
        url = intent.getStringExtra(ReporteDeDevoluciones1.SELECCION_URL);

        //Cursor cursorSalidas = manager.cargarCursorSalidas();
        //cursorSalidas.moveToFirst();
        sala.setText(valSala);
        id.setText(String.valueOf(salidaid));

        Cursor cursorIDSalidaSeleccionada = manager.buscarSalidasSeleccionadas(salidaid);
        cursorIDSalidaSeleccionada.moveToLast();
        folioString = cursorIDSalidaSeleccionada.getString(6);
        Log.e("folioString", folioString);
        fechaString = cursorIDSalidaSeleccionada.getString(4).split("/");

        if (folioString.equals("Pendiente")) {
            layoutFolio.setBackgroundColor(Color.YELLOW);
            folio.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            folio.setText("¡" + folioString + "!" + getResources().getString(R.string.terminarSalInfo));
            usuarioID = cursorIDSalidaSeleccionada.getInt(3);
            officeID = cursorIDSalidaSeleccionada.getInt(7);
        } else {
            folio.setText(folioString);
            if (CheckService.internet(context)) {
                devolucionDetalles(Integer.valueOf(folioString));
                actualizaEstatusTecnico();
            } else
                Alert.ActivaInternet(context);

        }
        fecha.setText(fechaString[0]);
        hora.setText(fechaString[1]);
        //estatusDev.setText(manager.consulta("SELECT nombre FROM cestatus WHERE id = " + cursorIDSalidaSeleccionada.getString(5) + "", ""));

        IDSalida = cursorIDSalidaSeleccionada.getInt(0);
        listaReportes = (ListView) findViewById(R.id.listaReportes);
        listaReportes.setOnTouchListener(new ListView.OnTouchListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        ToqueInicialX = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (ToqueInicialX < event.getX()) {//izquierda invertido
                            moverScrollIzqDer("IZQ");
                        } else {//derecha invertido
                            moverScrollIzqDer("DER");
                        }
                        break;
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        layoutFolio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folioString.equals("Pendiente")) {
                    Cursor cursorSalidas = manager.buscarSalidasPorId(salidaid);
                    cursorSalidas.moveToLast();
                    String pedido = cursorSalidas.getString(8);
                    Intent intent;
                    intent = new Intent(context, Registro.class);
                    bndMyBundle = new Bundle();
                    bndMyBundle.putInt("usuarioidx", (usuarioID));
                    bndMyBundle.putString("urlx", (url));
                    bndMyBundle.putString(Menu_Devolucion.VERIFICAR_PEDIDO, "no");
                    bndMyBundle.putInt("salidaidx", (salidaid));
                    bndMyBundle.putInt("officeIDx", (officeID));
                    bndMyBundle.putInt(Menu_Devolucion.ACTIVIDAD_ORIGEN, 1);
                    bndMyBundle.putString(Menu_Devolucion.NOMBRE_SALA, valSala);
                    bndMyBundle.putString("pedidox", (pedido));
                    intent.putExtras(bndMyBundle);
                    startActivity(intent);
                }
            }
        });

    }

    private void listaMateriales() {
        Cursor cursorReportes = manager.buscarReportesAsociados(IDSalida);
        cursorReportes.moveToFirst();
   /*     try {
            do {
                datos.add(new Lista_item(
                        Integer.toString(cursorReportes.getInt(2)),
                        cursorReportes.getString(3),
                        Integer.toString(cursorReportes.getInt(4)),
                        cursorReportes.getString(5),
                        manager.consulta("SELECT nombre FROM cestatus WHERE id = " + cursorReportes.getString(6) + "", "")));
            } while (cursorReportes.moveToNext());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
        }*/

        if (datos.isEmpty())
            datos.add(new Lista_item("No hay", "Devoluciones", "por", "mostrar", "."));


        listaReportes.setAdapter(new Lista_adaptador(this, R.layout.item_list_reporte, datos) {
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    TextView tvCodigo = (TextView) view.findViewById(R.id.tvcodigo);
                    if (tvCodigo != null)
                        tvCodigo.setText(((Lista_item) entrada).getCodigo());

                    TextView tvDescripcion = (TextView) view.findViewById(R.id.tvdescrip);
                    if (tvDescripcion != null)
                        tvDescripcion.setText(((Lista_item) entrada).getDescripcion());

                    TextView tvCantidad = (TextView) view.findViewById(R.id.tvcantidad);
                    if (tvCantidad != null)
                        tvCantidad.setText(((Lista_item) entrada).getCantidad());

                    TextView tvSerie = (TextView) view.findViewById(R.id.tvserie);
                    if (tvSerie != null)
                        tvSerie.setText(((Lista_item) entrada).getSerie());

                    TextView tvEstatus = (TextView) view.findViewById(R.id.tvestatus);
                    if (tvEstatus != null)
                        tvEstatus.setText(((Lista_item) entrada).getEstatus());


                }
            }
        });
    }

    void moverScrollIzqDer(String s) {
        if (posicionX >= listaReportes.getWidth()) {
            posicionX = listaReportes.getWidth();
        }
        if (posicionX <= 0) {
            posicionX = 0;
        }
        if (s.equals("IZQ")) {
            posicionX -= 10;
        }
        if (s.equals("DER")) {
            posicionX += 10;
        }
        horizontalScrollView.scrollTo(posicionX, 0);
    }

    public void onRestart() {
        super.onRestart();
        setupDevolucionesEnviadas();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        actualizaEstatusTecnico();
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
            MenuInicialTecnico.iniciaMenuEstatusTec(DevolucionesEnviadas.this);
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
//322