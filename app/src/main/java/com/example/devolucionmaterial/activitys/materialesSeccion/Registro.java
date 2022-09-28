package com.example.devolucionmaterial.activitys.materialesSeccion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.CoordinatorLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.activitys.LectorActivity;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDhelper;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.MenuOpciones;
import com.example.devolucionmaterial.static_class.ServerErrors;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thanosfisherman.mayi.PermissionBean;


public class Registro extends BaseActivity implements OnClickListener {
    private static String metodo;
    private Context context;
    private EditText etmaquina, etcodigo, etcantidad, etserie, etPedido;
    private Button btningresa, scanBtn, btnMostrar, btnVerificar;
    private TextView tvSalida, tvMensaje, tvCodigo, tvDescripcion, tvDescripcionLabel, tvcantidad,
            tvSerie;
    private LinearLayout llBotones, llMenuPedidos;
    private Bundle bndMyBundle;
    private int usuarioID, salidaid, officeID, clave, actividadOrigen, existeSerie, idSustituto, codigoHTTP = 0;
    private String url, cantidad, verificarPedido, nombreUsuario,
            nombre, pedido, noGuia = "pendiente", jsonResult;
    private static final int CALLING_ID_FOR_ACTIV2 = 122;
    private BDmanager manager;
    private Cursor cursorGeneral;
    private boolean verificarSerie = false;
    private static String docPHPVerificaNoGuia = "verificaPedidoYNoGuia.php?";
    private static String docPHPVerificaRefaccion = "verificaRefaccion.php?";

    private MenuOpciones mo;
    private ImageView imgEmail, imgCallCC, imgInfo;
    private Menu mMenu;
    private int existePieza, status;
    private int codigo, estatus, suplente, refaccionid;
    private String nombrePieza;

    private ImageButton ibQrMaquina;
    private ImageButton ibQrComponente;

    private LinearLayout llContent;
    private boolean codigoQR = false;
    private String codigoOiezaCompleto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        initToolbar("Registro de Devolución", true, true);
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


    private void initSetUp() {
        mo = new MenuOpciones();
        imgEmail = (ImageView) findViewById(R.id.imgCorreor);
        imgInfo = (ImageView) findViewById(R.id.imgInfor);
        imgCallCC = (ImageView) findViewById(R.id.imgLlamarr);
        tvSalida = (TextView) findViewById(R.id.tvSalida);
        tvMensaje = (TextView) findViewById(R.id.tvusuarioRD);
        tvCodigo = (TextView) findViewById(R.id.txtcodigo);
        tvDescripcion = (TextView) findViewById(R.id.tvDescripcion);
        tvDescripcionLabel = (TextView) findViewById(R.id.textView1);
        tvcantidad = (TextView) findViewById(R.id.txtcantidad);
        tvSerie = (TextView) findViewById(R.id.txtserie);

        btnMostrar = (Button) findViewById(R.id.btMostrar);
        btningresa = (Button) findViewById(R.id.btnIngresa);
        scanBtn = (Button) this.findViewById(R.id.btnEscaner);
        btnVerificar = (Button) findViewById(R.id.btnVerificarPedido);

        etmaquina = (EditText) findViewById(R.id.etmaquina);
        etcodigo = (EditText) findViewById(R.id.etcodigo);
        etcantidad = (EditText) findViewById(R.id.etcantidad);
        etserie = (EditText) findViewById(R.id.etserie);
        etPedido = (EditText) findViewById(R.id.etPedido);

        llBotones = (LinearLayout) findViewById(R.id.llbotones);
        llMenuPedidos = (LinearLayout) findViewById(R.id.llVerif);
        llContent = (LinearLayout) findViewById(R.id.ll_content);

        ibQrMaquina = (ImageButton) findViewById(R.id.ar_ib_qr_maquina);
        ibQrComponente = (ImageButton) findViewById(R.id.ar_ib_qr_componente);

        ibQrMaquina.setOnClickListener(this);
        ibQrComponente.setOnClickListener(this);

        manager = new BDmanager(context);
        Intent intReceptor = this.getIntent();
        Bundle bndReceptor = intReceptor.getExtras();
        usuarioID = bndReceptor.getInt("usuarioidx");
        url = bndReceptor.getString("urlx");
        nombreUsuario = bndReceptor.getString("nombrex");
        salidaid = bndReceptor.getInt("salidaidx");
        officeID = bndReceptor.getInt("officeIDx");
        verificarPedido = bndReceptor.getString(Menu_Devolucion.VERIFICAR_PEDIDO);
        actividadOrigen = bndReceptor.getInt(Menu_Devolucion.ACTIVIDAD_ORIGEN);
        tvMensaje.setText(nombreUsuario);
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

        if (verificarPedido.equals("si"))
            setupMenuPedidosYNoGuia();
        else if (verificarPedido.equals("no")) {
            pedido = bndReceptor.getString("pedidox");
            setupRegistroPiezas();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ar_ib_qr_maquina:
                Intent iMaquina = new Intent(this, LectorActivity.class);
                startActivityForResult(iMaquina, 1);

                break;
            case R.id.ar_ib_qr_componente:
                Intent iComponente = new Intent(this, LectorActivity.class);
                startActivityForResult(iComponente, 2);
                break;

        }

    }


    private void setupMenuPedidosYNoGuia() {
        metodo = "setupMenuPedidosYNoGuia()";
        tvMensaje.setVisibility(View.VISIBLE);
        tvCodigo.setVisibility(View.GONE);
        tvDescripcion.setVisibility(View.GONE);
        tvDescripcionLabel.setVisibility(View.GONE);
        tvcantidad.setVisibility(View.GONE);
        tvSerie.setVisibility(View.GONE);
        llContent.setVisibility(View.GONE);


        etcodigo.setVisibility(View.GONE);
        etcantidad.setVisibility(View.GONE);
        etserie.setVisibility(View.GONE);

        llBotones.setVisibility(View.GONE);
        llMenuPedidos.setVisibility(View.VISIBLE);

        tvSalida.setText(   getString(R.string.noPedido));

        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPedido.getText().length() > 0) {
                    pedido = etPedido.getText().toString();
                    barraProgresoVerificarPedidoYNoGuia();
                } else
                    Alert.Alerta(context, metodo, 1, getString(R.string.campoVacio));
            }
        });


    }

    private void barraProgresoVerificarPedidoYNoGuia() {
        if (CheckService.internet(context)) {
            new Progreso().execute();
        } else {
            Alert.ActivaInternet(context);
        }
    }


    private class Progreso extends AsyncTask<Void, Void, String> {
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
        protected String doInBackground(Void... params) {
            metodo = "Progreso<AyncTask>.doInBackground()";
            String respuesta = "";
            try {
                MensajeEnConsola.log(context, metodo, " URL:"+MainActivity.url + docPHPVerificaNoGuia + "pedidox=" + pedido);
                JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPVerificaNoGuia + "pedidox=" + pedido);
                MensajeEnConsola.log(context, metodo, "RESPUESTA"+object.getString("res"));
                assert object != null;
                respuesta = object.getString("res");

            } catch (JSONException e) {
                MensajeEnConsola.log(context, metodo, getString(R.string.titJsonException) + "\n" + new ServerErrors(codigoHTTP, jsonResult).getErrorServer());
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Error MA001" + "\n" + e.getMessage());
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            if (s.equals("1"))
                setupRegistroPiezas();
            else if (s.equals("0")) {
                Alert.Alerta(context, metodo, 2, getResources().getString(R.string.noPedidoIncorrecto));
                etPedido.setText("");
            } else {
                Alert.Alerta(context, metodo, 2, getResources().getString(R.string.probelmas_con_el_conexion));
                etPedido.setText("");
            }
        }
    }

    private void setupRegistroPiezas() {
        metodo = "setupRegistroPiezas()";
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                manager.actualizarPedidoYGuia(pedido, noGuia, salidaid);
                                verificarPedido = "no";
                                tvMensaje.setVisibility(View.VISIBLE);
                                tvCodigo.setVisibility(View.VISIBLE);
                                tvDescripcion.setVisibility(View.VISIBLE);
                                tvDescripcionLabel.setVisibility(View.VISIBLE);
                                tvcantidad.setVisibility(View.VISIBLE);
                                tvSerie.setVisibility(View.VISIBLE);
                                llContent.setVisibility(View.VISIBLE);

                                etcodigo.setVisibility(View.VISIBLE);
                                etcantidad.setVisibility(View.VISIBLE);
                                etserie.setVisibility(View.VISIBLE);

                                llBotones.setVisibility(View.VISIBLE);
                                llMenuPedidos.setVisibility(View.GONE);

                                tvSalida.setText(getString(R.string.salidaNo) + salidaid);

                                btningresa.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String codigo = etcodigo.getText().toString();

                                        String maquina = etmaquina.getText().toString();
                                        cantidad = etcantidad.getText().toString();

                                        String serie = etserie.getText().toString();
                                        String descripcion = tvDescripcion.getText().toString();
                                        String seriePartida[] = serie.split(" ");
                                        String serieValidada = "";

                                        if (verificarSerie) {
                                            if (serie.length() == 0 || cantidad.length() == 0 || codigo.length() == 0)
                                                Alert.Alerta(context, metodo, 2, getString(R.string.camposVacios));
                                            else {
                                                int cantidadInt = Integer.valueOf(cantidad);
                                                if (cantidadInt == 0) {
                                                    Alert.Alerta(context, metodo, 2, getString(R.string.notCanCero));
                                                    etcantidad.requestFocus();
                                                    getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                                } else if (cantidadInt > 99) {
                                                    Alert.Alerta(context, metodo, 2, getString(R.string.notCienPie));
                                                    etcantidad.requestFocus();
                                                    getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                                } else if (etserie.getText().length() < 6) {
                                                    Alert.Alerta(context, metodo, 1, getString(R.string.serieBad));
                                                    etserie.setText("");
                                                } else {
                                                    // TODO: 12/06/2017 se verifica si viene el codigo de el qr para madarlo completo
                                                    if (codigoOiezaCompleto != null) {
                                                        codigo = codigoOiezaCompleto;
                                                    }
                                                    for (String aSeriePartida : seriePartida)
                                                        serieValidada = serieValidada + aSeriePartida;
                                                    ingresaLista(codigo, cantidad, serieValidada);
                                                    Cursor cursorSalidas = manager.cargarCursorSalidas();
                                                    cursorSalidas.moveToLast();

                                                    manager.insertarDescripcionSalida(salidaid, codigo, descripcion, Integer.valueOf(cantidad), serieValidada, "1", maquina);
                                                    codigoOiezaCompleto = null;
                                                    etcantidad.setEnabled(true);
                                                }
                                            }
                                        } else {
                                            if (cantidad.length() == 0 || codigo.length() == 0)
                                                Alert.Alerta(context, metodo, 2, getString(R.string.notCant));
                                            else {
                                                int cantidadInt = Integer.valueOf(cantidad);
                                                if (cantidadInt == 0) {
                                                    Alert.Alerta(context, metodo, 2, getString(R.string.notCanCero));
                                                    etcantidad.requestFocus();
                                                    getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                                } else if (cantidadInt > 99) {
                                                    Alert.Alerta(context, metodo, 2, getString(R.string.notCienPie));
                                                    etcantidad.requestFocus();
                                                    getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                                } else {

                                                    // TODO: 12/06/2017 se verifica si viene el codigo de el qr para madarlo completo
                                                    if (codigoOiezaCompleto != null) {
                                                        codigo = codigoOiezaCompleto;
                                                    }

                                                    ingresaLista(codigo, cantidad, serieValidada);
                                                    Cursor cursorSalidas = manager.cargarCursorSalidas();
                                                    cursorSalidas.moveToLast();
                                                    manager.insertarDescripcionSalida(salidaid, codigo, descripcion, Integer.valueOf(cantidad), serieValidada, "1", maquina);
                                                    codigoOiezaCompleto = null;
                                                    etcantidad.setEnabled(true);
                                                }
                                            }
                                        }
                                    }
                                });

                                btnMostrar.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Cursor cursorSalidasPorId = manager.buscarSalidasPorId(salidaid);
                                        cursorSalidasPorId.moveToFirst();

                                        Intent intent;
                                        intent = new Intent(context, Lista_Devolucion.class);
                                        bndMyBundle = new Bundle();
                                        bndMyBundle.putInt("usuarioidx", (usuarioID));
                                        bndMyBundle.putString("urlx", (url));
                                        bndMyBundle.putString("nombrex", nombreUsuario);
                                        bndMyBundle.putInt("salidaidx", (salidaid));
                                        bndMyBundle.putInt("officeIDx", (officeID));
                                        bndMyBundle.putString("pedidox", cursorSalidasPorId.getString(8));
                                        bndMyBundle.putString("noGuiax", cursorSalidasPorId.getString(9));
                                        bndMyBundle.putString("maquina", etmaquina.getText().toString());
                                        bndMyBundle.putInt(Menu_Devolucion.ACTIVIDAD_ORIGEN, actividadOrigen);
                                        intent.putExtras(bndMyBundle);
                                        startActivityForResult(intent, CALLING_ID_FOR_ACTIV2);
                                    }
                                });

                                scanBtn.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        muestraEscaner();
                                    }
                                });
                            }
                        });
                    }
                }
        ).start();
        etcodigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                metodo += "onTextChanged()";

                try {
                    // TODO: 13/06/2017 si a la hora de escanear el codigo se hace movimientos a la vaja de codigo se elimina lo que se alla extraido de qr
                    if (etcodigo.getText().toString().length() != 6) {
                        codigoOiezaCompleto = null;
                    }
                    if (s.length() == 6) {
                        clave = Integer.valueOf(etcodigo.getText().toString());
                        cursorGeneral = manager.buscarClave(clave);
                        cursorGeneral.moveToFirst();
                        existePieza = cursorGeneral.getCount();
                        if (existePieza > 0) {
                            nombre = cursorGeneral.getString(3);
                            existeSerie = cursorGeneral.getInt(6);
                            status = cursorGeneral.getInt(4);
                            if (cursorGeneral.getString(5) == null)
                                idSustituto = 0;
                            else
                                idSustituto = cursorGeneral.getInt(5);
                            if (status == 124) {
                                if (idSustituto != 0) {
                                    Cursor cursorBuscarSustituto = manager.buscarId(idSustituto);
                                    cursorBuscarSustituto.moveToFirst();
                                    final String claveSustituto = cursorBuscarSustituto.getString(2);
                                    final String nombreSustituto = cursorBuscarSustituto.getString(3);
                                    final int existeSerieEnSustituto = cursorBuscarSustituto.getInt(6);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage(getString(R.string.infoRef1) + clave + getString(R.string.infoRef2) +
                                            claveSustituto + getString(R.string.infoRef3)).setTitle(getString(R.string.refSusUb))
                                            .setPositiveButton(getString(R.string.aceptar),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            if (existeSerieEnSustituto == 1) {
                                                                etcantidad.setText("1");
                                                                etcantidad.setEnabled(false);
                                                                etserie.setEnabled(true);
                                                                etserie.setHint(getString(R.string.ingresaSerie));
                                                                etserie.requestFocus();
                                                                getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                                                verificarSerie = true;
                                                            } else {
                                                                etserie.setEnabled(false);
                                                                etcantidad.setEnabled(true);
                                                                etcantidad.setText("");
                                                                etserie.setHint(getString(R.string.campoCompleto));
                                                                etcantidad.requestFocus();
                                                                getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                                                verificarSerie = false;
                                                            }
                                                            etcodigo.setText(claveSustituto);
                                                            tvDescripcion.setText(nombreSustituto);
                                                            btningresa.setEnabled(true);
                                                        }
                                                    })
                                            .setNegativeButton("Cancelar",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            tvDescripcion.setText(R.string.tvDescripcion);
                                                            etcodigo.setHint(R.string.hintSeisDigitos);
                                                            etserie.setText("");
                                                            etcantidad.setText("");
                                                            btningresa.setEnabled(false);
                                                            etcantidad.setEnabled(false);
                                                            etserie.setEnabled(false);
                                                            etcodigo.requestFocus();
                                                            getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                                            Alert.Alerta(context, metodo, 2, getString(R.string.errorPieDes));
                                                        }
                                                    });
                                    builder.show();

                                } else {
                                    if (existeSerie == 1) {
                                        etserie.setEnabled(true);
                                        etcantidad.setText("1");
                                        etcantidad.setEnabled(false);
                                        etserie.setHint(getString(R.string.ingresaSerie));
                                        etserie.requestFocus();
                                        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                        verificarSerie = true;
                                    } else {
                                        etserie.setEnabled(false);
                                        etserie.setHint(getString(R.string.campoCompleto));
                                        etcantidad.setEnabled(true);
                                        etcantidad.setText("");
                                        etcantidad.requestFocus();
                                        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                        verificarSerie = false;
                                    }
                                    tvDescripcion.setText(nombre);
                                    btningresa.setEnabled(true);
                                    getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                    Alert.Alerta(context, metodo, 1, getString(R.string.pieDesc));
                                    ///////////////////
                                }
                            } else {
                                tvDescripcion.setText(nombre);
                                etcantidad.setEnabled(true);
                                btningresa.setEnabled(true);
                                if (existeSerie == 1) {
                                    etserie.setEnabled(true);
                                    etcantidad.setText("1");
                                    etcantidad.setEnabled(false);
                                    etserie.setHint(getString(R.string.ingresaSerie));
                                    etserie.requestFocus();
                                    getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                    verificarSerie = true;
                                } else {
                                    etserie.setEnabled(false);
                                    etserie.setHint(getString(R.string.campoCompleto));
                                    etcantidad.setEnabled(true);
                                    etcantidad.setText("");
                                    etcantidad.requestFocus();
                                    getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                    verificarSerie = false;
                                }
                            }
                        } else {
                            barraProgresoRefaccion();
                            Alert.Alerta(context, metodo, 1, "La pieza solicitada no existe en la base de datos del celular.\nVerificando existencia de pieza en SisCo...");
                        }
                    }
                } catch (Exception e) {
                    Log.e("error de codigo", String.valueOf(e));
                    new MaterialDialog.Builder(Registro.this)
                            .content("Error con el código")
                            .iconRes(R.drawable.warning)
                            .positiveText(R.string.aceptar)
                            //.negativeText(R.string.disagree)
                            .show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    public void ingresaLista(String codigo, String cantidad, String serie) {
        metodo = "ingresaLista()";
        manager.actualiza("insert into rpiezas(salidaidfk, codigo, cantidad, serie, fecha,estatus)" +
                "values(" + salidaid + ",'" + codigo + "'," + cantidad + ",'" + serie + "',date('now'),'1')");
        etcodigo.setText("");
        etcantidad.setText("");
        etserie.setText("");
        etmaquina.setText("");
        tvDescripcion.setText(R.string.tvDescripcion);
        etcodigo.requestFocus();
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        etcantidad.setEnabled(false);
        etserie.setEnabled(false);
        btningresa.setEnabled(false);
        Alert.Alerta(context, metodo, 0, getString(R.string.datIngresados));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        metodo = "onActivityResult()";
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            Toast.makeText(context, "Codigo escaneado!", Toast.LENGTH_SHORT).show();
            metodo += ".Escaner";
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanningResult != null) {
                try {
                    String scanContent = scanningResult.getContents();
                    etserie.setText(scanContent);
                } catch (NullPointerException e) {
                    MensajeEnConsola.log(context, metodo, "ERROR.NullPointerException e = " + e.getMessage());
                }
            } else {
                Toast.makeText(context, "No hay datos de escaneo!", Toast.LENGTH_SHORT).show();
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            String result = intent.getStringExtra("result");
            if (requestCode == 1) {

                etmaquina.setText(result);
            }
            if (requestCode == 2) {
                codigoOiezaCompleto = result;
                etcodigo.setText(result);
                String[] resulta = result.split(" ");


                try {
                    String validar = resulta[2];
                    //se corta la cadena par asaber si es barras
                    codigoOiezaCompleto = resulta[0];
                    //se envia completo el codigo
                    codigoOiezaCompleto = result;
                    etcantidad.setEnabled(false);
                    etcantidad.setText("1");

                } catch (Exception e) {
                    Log.e("error", "no es de barras");
                    etcantidad.setEnabled(true);
                    etcantidad.setText("0");
                    try {
                        //se corta la cadena par saber si es qr
                        codigoOiezaCompleto = resulta[0] + " " + resulta[1];
                        //se envia completo el codigo
                        codigoOiezaCompleto = result;
                        etcantidad.setEnabled(false);
                        etcantidad.setText("1");
                        Log.e("error", codigoOiezaCompleto);
                    } catch (Exception er) {
                        codigoOiezaCompleto = null;
                        etcantidad.setText("0");
                        etcantidad.setEnabled(true);
                        Log.e("error", "no es de qr");
                    }
                }


            }
        }

        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }

    public void muestraEscaner() {
        metodo = "muestraEscaner()";
        IntentIntegrator integrator = new IntentIntegrator(this);
        //integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        //integrator.setPrompt("Escaner de codigo de barras y QR");
        //integrator.setCameraId(0);  // Use a specific camera of the device
        //integrator.setBeepEnabled(false);
        //integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    public void onBackPressed() {
        metodo = "Registro." + "onBackPressed()";
        if (verificarPedido.equals("no")) {
            salirReporte();
        } else if (verificarPedido.equals("si"))
            salirMenuPedidos();

    }

    private void salirMenuPedidos() {
        metodo = "Registro." + "salirMenuPedidos()";
        final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(context);
        alertaSimple.setTitle(getString(R.string.informacion));
        alertaSimple.setMessage(getString(R.string.salirRegistro));
        alertaSimple.setPositiveButton(getString(R.string.si),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        manager.eliminarSalida(salidaid);
                        finish();
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

    public void salirReporte() {
        metodo = "salirReporte()";
        final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(context);
        alertaSimple.setTitle(getString(R.string.informacion));
        alertaSimple.setMessage(getString(R.string.salirRegistro));
        alertaSimple.setPositiveButton(getString(R.string.si),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
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

    public static String obtenerHora() {
        metodo = "obtenerHora()";
        Calendar cal = Calendar.getInstance();
        int dia = cal.get(Calendar.DATE);
        int mes = cal.get(Calendar.MONTH) + 1;
        int segundos = cal.get(Calendar.SECOND);
        int minutos = cal.get(Calendar.MINUTE);

        //Formato de 24 horas
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String horaString, minutosString, segundosString, cadenaHora,
                diaString, mesString;

        if (hour < 10)
            horaString = "0" + hour;
        else
            horaString = "" + hour;

        if (minutos < 10)
            minutosString = "0" + minutos;
        else
            minutosString = "" + minutos;

        if (segundos < 10)
            segundosString = "0" + segundos;
        else
            segundosString = "" + segundos;

        if (dia < 10)
            diaString = "0" + dia;
        else
            diaString = "" + dia;

        if (mes < 10)
            mesString = "0" + mes;
        else
            mesString = "" + mes;

        cadenaHora = "" + diaString + "-" + mesString + "/" + horaString + ":" + minutosString + ":" + segundosString;
        return cadenaHora;
    }


    public static String obtenerDiaHora() {
        metodo = "obtenerDiaHora()";
        Calendar cal = Calendar.getInstance();
        int dia = cal.get(Calendar.DATE);
        int minutos = cal.get(Calendar.MINUTE);

        //Formato de 24 horas
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String horaString, minutosString, diaString;

        if (hour < 10)
            horaString = "0" + hour;
        else
            horaString = "" + hour;

        if (minutos < 10)
            minutosString = "0" + minutos;
        else
            minutosString = "" + minutos;

        if (dia < 10)
            diaString = "0" + dia;
        else
            diaString = "" + dia;
        Log.e("horas de registro", diaString + ":" + horaString + ":" + minutosString);
        return diaString + ":" + horaString + ":" + minutosString;
    }

    public static String diferenciaFechas(Context context, String inicio, String llegada) {
        if (inicio.equals("")) inicio = llegada;
        Date fechaInicio = null;
        Date fechaLlegada = null;

        Log.e("inicio", inicio);
        Log.e("llegada", llegada);

        // configuramos el formato en el que esta guardada la fecha en
        //  los strings que nos pasan
        //SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formato = new SimpleDateFormat("dd:HH:mm");

        try {
            // aca realizamos el parse, para obtener objetos de tipo Date de
            // las Strings
            fechaInicio = formato.parse(inicio);
            fechaLlegada = formato.parse(llegada);
            Log.e("fechaInicio", String.valueOf(fechaInicio));
            Log.e("fechaLlegada", String.valueOf(fechaLlegada));


        } catch (ParseException e) {
            // Log.e(TAG, "Funcion diferenciaFechas: Error Parse " + e);
        } catch (Exception e) {
            // Log.e(TAG, "Funcion diferenciaFechas: Error " + e);
        }

        // tomamos la instancia del tipo de calendario
        Calendar calendarInicio = Calendar.getInstance();
        Calendar calendarFinal = Calendar.getInstance();

        // Configramos la fecha del calendatio, tomando los valores del date que
        // generamos en el parse
        calendarInicio.setTime(fechaInicio);

        calendarFinal.setTime(fechaLlegada);

        // obtenemos el valor de las fechas en milisegundos
        long milisegundos1 = calendarInicio.getTimeInMillis();

        long milisegundos2 = calendarFinal.getTimeInMillis();

        // tomamos la diferencia
        long diferenciaMilisegundos = milisegundos2 - milisegundos1;


        // Despues va a depender en que formato queremos  mostrar esa
        // diferencia, minutos, segundo horas, dias, etc, aca van algunos
        // ejemplos de conversion

        // calcular la diferencia en segundos
        long diffSegundos = Math.abs(diferenciaMilisegundos / 1000);


        // calcular la diferencia en minutos
        long diffMinutos = Math.abs(diferenciaMilisegundos / (60 * 1000));

        long restominutos = diffMinutos % 60;

        // calcular la diferencia en horas
        long diffHoras = (diferenciaMilisegundos / (60 * 60 * 1000));


        // calcular la diferencia en dias
        long diffdias = Math.abs(diferenciaMilisegundos / (24 * 60 * 60 * 1000));
        Log.e("diffdias", String.valueOf(diffdias));
        // revisamos el cambio de ano y mes
        if (diffHoras >= 8064) diffHoras -= 8064;
        if (diffHoras >= 672) diffHoras -= 672;
        if (diffHoras <= 0) {
            diffHoras = Long.parseLong(BDVarGlo.getVarGlo(context, "INFO_USUARIO_HORAS_ACTIVIDAD"));
        }
        Log.e("diffHoras", String.valueOf(diffHoras));
        //if(diffHoras < 0)diffHoras=diffHoras*(-1);
        Log.e("INFO_USUARIO_HORAS_ACTIVIDAD", BDVarGlo.getVarGlo(context, "INFO_USUARIO_HORAS_ACTIVIDAD"));
        // devolvemos el resultado en un string
        return String.valueOf(diffHoras + "H " + restominutos + "m ");
    }


    private void verificaRefaccion(int claveRefaccion) {
        metodo = "verificaRefaccion()";
        try {
            JSONObject object = JSONparse.consultaURL(context, metodo, url + docPHPVerificaRefaccion + "codigox=" + claveRefaccion);
            assert object != null;
            refaccionid = object.getInt("refaccionidx");
            if (refaccionid != 0) {
                codigo = object.getInt("clavex");
                nombrePieza = object.getString("nombrex");
                estatus = object.getInt("estatusidfkx");
                suplente = object.getInt("suplentex");
            } else {
                refaccionid = 0;
            }
        } catch (JSONException e) {
            Alert.Error(context, metodo, getString(R.string.titJsonException), new ServerErrors(codigoHTTP, jsonResult).getErrorServer());
        } catch (Exception e) {
            Alert.Error(context, metodo, "Error MA001", e.getMessage());
        }
    }

    public void barraProgresoRefaccion() {
        new ProgresoRefaccion().execute();
    }

    private class ProgresoRefaccion extends AsyncTask<Void, Void, Void> {
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
        protected Void doInBackground(Void... params) {
            verificaRefaccion(clave);
            return null;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Void result) {
            metodo = "ProgresoRefaccion<AsyncTask>.onPostExecute()";
            super.onPostExecute(result);
            pDialog.dismiss();
            if (refaccionid != 0) {
                BDhelper admin = new BDhelper(context);
                SQLiteDatabase bd = admin.getWritableDatabase();
                ContentValues cv = new ContentValues(6);
                cv.put(BDmanager.CN_ID_REFACCIONES, refaccionid);
                cv.put(BDmanager.CN_CLAVE, codigo);
                cv.put(BDmanager.CN_NOMBRE, nombrePieza);
                cv.put(BDmanager.CN_STATUS, estatus);
                cv.put(BDmanager.CN_NO_SERIE, suplente);
                bd.insert(BDmanager.TABLE_REFACCIONES, null, cv);
                Alert.Alerta(context, metodo, 0, "Existencia validada");
                etcodigo.setText("");
                etcodigo.setText(String.valueOf(clave));
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(getString(R.string.errorPie));
                alertDialog.setMessage(getString(R.string.infoErrPie1) + clave + getString(R.string.infoErrPie2));
                alertDialog.setButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        tvDescripcion.setText(R.string.tvDescripcion);
                        etserie.setHint("");
                        etserie.setText("");
                        etcantidad.setText("");
                        etcantidad.setEnabled(false);
                        etserie.setEnabled(false);
                        btningresa.setEnabled(false);
                        etcodigo.requestFocus();
                        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    }
                });
                alertDialog.setIcon(R.drawable.ic_launcher);
                alertDialog.show();
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
      /*  if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }*/
    }


}
//890