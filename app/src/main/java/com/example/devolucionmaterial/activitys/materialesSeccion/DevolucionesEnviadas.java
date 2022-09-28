package com.example.devolucionmaterial.activitys.materialesSeccion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.activitys.LectorActivity;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.PedidosListQrActivity;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.adapter.PedidosQrApapter;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model.Datos;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.beans.Refacciones;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.lists.Lista_adaptador;
import com.example.devolucionmaterial.lists.Lista_item;
import com.example.devolucionmaterial.services.CheckService;
import com.thanosfisherman.mayi.PermissionBean;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevolucionesEnviadas extends BaseActivity {
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
    private String valSala, idDevolucion="";
    private Menu mMenu;
    int ToqueInicialX, posicionX = 0;
    HorizontalScrollView horizontalScrollView;

    ListView listaReportes;
    private TextView estatusDev,tvGuia;
    private EditText etReadGuia;
    private Button btnEnviaGuia,btnReadtvGuia;
    private final static int RESULT_LECTOR = 100;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte_devoluciones_enviadas);
        initToolbar("Menú de Reportes", true , true);
        context = this;
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        } else {
            initSetUp();
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            final String guia = data.getStringExtra("result");
            if (requestCode == RESULT_LECTOR) {
                new MaterialDialog.Builder(this)
                        .title("¿Enviar?")
                        .content("Guía " + guia)
                        .cancelable(false)
                        .negativeText("Cancelar")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .positiveText(R.string.aceptar)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                Log.e("----------------------------", "idDevolucion"+idDevolucion);

                                if(idDevolucion.equals("Pendiente")){toastAlert("Error No Existe Folio");   }
                                else{sendGuia(Integer.parseInt(idDevolucion), guia);}
                            }
                        })
                        .show();

            }

        }





        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
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


    private void devolucionDetalles(int folioInt) {

        pDialog= new MaterialDialog.Builder(context)
                .title(context.getString(R.string.Conectando_con_servidor_remoto))
                .content("Cargando...")
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(false)
                .show();

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
                        tvGuia.setText(response.body().getGuia());
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








    private void initSetUp() {
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
        TextView guia =(TextView) findViewById(R.id.tvGuia);
        estatusDev = (TextView) findViewById(R.id.tvEstatusDev);
        tvGuia=(TextView) findViewById(R.id.tvGuia);

        btnEnviaGuia=(Button) findViewById(R.id.but_eviar_guia);
        btnEnviaGuia.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent iLector = new Intent(DevolucionesEnviadas.this, LectorActivity.class);
                startActivityForResult(iLector, RESULT_LECTOR);

                // click handling code
            }
        });


        etReadGuia=(EditText) findViewById(R.id.tv_readguia);


         btnReadtvGuia=(Button) findViewById(R.id.but_readtv_guia);
         btnReadtvGuia.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View view) {

              if(etReadGuia.getText().toString().equals("")){ toastAlert("El No. Guia No puede estar vacio");}
              else {


                  if(idDevolucion.equals("Pendiente")){toastAlert("Error No Existe Folio");   }
                  else{sendGuia(Integer.parseInt(idDevolucion), etReadGuia.getText().toString());}

              }

             }
         });




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
        folioString = cursorIDSalidaSeleccionada.getString(6); idDevolucion=folioString;
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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }else{
            initSetUp();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        actualizaEstatusTecnico();
    }












   private void sendGuia(int devolucionId, String guia) {


        ProgressDialog pDialog;
        pDialog = new ProgressDialog(this);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);

        /**
         * se usa de @link{@BeanResponse} solo result y comment
         *
         * */
        ServiceApi serviceApi= ServiceApi.retrofit.create(ServiceApi.class);
        Call<BeanResponse> call= serviceApi.enviarGuia(devolucionId,guia );

        call.enqueue(new Callback<BeanResponse>() {
            @Override
            public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {
                pDialog.dismiss();
                if(response.body().getResult() == 1){


                    tvGuia.setText(guia);


                }else if(response.body().getResult() == 0){



                }

                else{


                }

            }

            @Override
            public void onFailure(Call<BeanResponse> call, Throwable t) {
                pDialog.dismiss();

            }
        });


    }



    private void toastAlert(String ms){
        Toast.makeText(getApplicationContext(),ms, Toast.LENGTH_LONG).show();}





}
//322