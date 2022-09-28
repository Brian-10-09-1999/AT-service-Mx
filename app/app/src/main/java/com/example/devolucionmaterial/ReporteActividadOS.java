package com.example.devolucionmaterial;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.example.devolucionmaterial.activitys.VerGaleriaOS;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.ServerErrors;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class ReporteActividadOS extends Activity implements OnClickListener {
    private static String CONTENIDO_IMAGEN = "";
    String metodo;
    Context context;
    private ListView lvListaComentariosOS;
    private List<ComentarioOS> listaComentariosOS;
    private ArrayAdapter<ComentarioOS> adaptador;
    private Bundle bndReceptor, bndMuestraImagen;
    private Intent intReceptor;
    private String usuarioID, nombreDeUsuario, OSid, tipoDeVistaOS, jsonResult;
    private ProgressDialog pDialog;
    private LinearLayout llComentarios;
    private Button btnAddComentarios, btnFinalizarActividad;
    private EditText etComentariosOS;
    private BDmanager manager;
    private String comentariosParaServidor, apellido;
    private Cursor cursorBuscarComentarioPendiente;
    public static String ESTATUS_ENTREGADO = "1";
    public static String ESTATUS_PENDIENTE = "0";
    private static int GET_ID_TABLA_COMENT_OS = 0;
    private static int GET_OS_ID = 1;
    private static int GET_COMENTARIOS = 2;
    private static int GET_FECHA = 3;
    private static int GET_ESTATUS_DE_ENTREGA = 4;
    private static int GET_NOMBRE_USUARIO = 5;
    private static int AGREGA = 1;
    private static int ACTUALIZA_ELEMENTO = 2;
    private int idRegistroOS;
    private TextView tvUsuario, tvFolio, tvSala, tvActividad, tvIniAct, tvFinAct;
    private JSONObject jsonArrayChildInicial;
    private String fechaCompleta;
    private Menu mMenu;
    private JSONObject jsonArrayChild;
    private int codigoHTTP = 0;
    private String docPHPguardaComentarioOS = "guardaComentarioOS.php?";
    private String docPHPdatosOSTec = "datosOSTec.php?";
    String regresaURLimagen = "";
    private ImageView btnCamaraImgComentario, imgComentario;
    ImageView btnVerGaleria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_actividad_os);
        context = this;
        setupActividadOS();
    }

    private void setupActividadOS() {
        metodo = "setupActividadOS()";
        setupFotoyGaleria();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        manager = new BDmanager(context);
        etComentariosOS = (EditText) findViewById(R.id.etComentarios);
        btnAddComentarios = (Button) findViewById(R.id.btnIngresarComentario);
        btnFinalizarActividad = (Button) findViewById(R.id.btnFinalizarActividad);
        llComentarios = (LinearLayout) findViewById(R.id.llComentarios);
        tvUsuario = (TextView) findViewById(R.id.tvUsuarioAdeudo);
        tvFolio = (TextView) findViewById(R.id.tvFolioOS);
        tvSala = (TextView) findViewById(R.id.tvSalaOS);
        tvActividad = (TextView) findViewById(R.id.tvActividadOS);
        tvIniAct = (TextView) findViewById(R.id.tvIniAct);
        tvFinAct = (TextView) findViewById(R.id.tvFinAct);
        lvListaComentariosOS = (ListView) findViewById(R.id.lvComentariosOS);
        lvListaComentariosOS.setOnItemClickListener(selectedItem);
        btnCamaraImgComentario = (ImageView) findViewById(R.id.btnCamaraImgComentario);
        imgComentario = (ImageView) findViewById(R.id.imgComentarioOS);
        btnVerGaleria = (ImageView) findViewById(R.id.id_btnVerGaleria);
        btnVerGaleria.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AbreGaleriaImagenesSubidasOS();
            }
        });

        intReceptor = this.getIntent();
        bndReceptor = intReceptor.getExtras();
        usuarioID = bndReceptor.getString("usuarioidx");
        apellido = bndReceptor.getString("apellidox");
        nombreDeUsuario = bndReceptor.getString("nombreusuariox");
        OSid = bndReceptor.getString("OSidx");
        tipoDeVistaOS = bndReceptor.getString("tipoDeVistaOSx");
        usuarioID = bndReceptor.getString("usuarioidx");
        listaComentariosOS = new ArrayList<ComentarioOS>();
        tvUsuario.setText(nombreDeUsuario);
        if (CheckService.internet(context)) {
            new Progreso().execute();
        } else {
            Alert.ActivaInternet(context);
        }
        cargarComentariosOSTec();
        if (tipoDeVistaOS.equals(ListaOSAsignadas.VISTA_COMPLETA)) {
            setupVistaCompleta();
        } else {
            setupVistaComentarios();
        }

    }

    void AbreGaleriaImagenesSubidasOS() {
        if (CheckService.internet(context)) {
            Intent intent = new Intent(context, VerGaleriaOS.class);
            Bundle bndVergaLeria = new Bundle();
            bndVergaLeria.putString("folio", String.valueOf(tvFolio.getText()));
            intent.putExtras(bndVergaLeria);
            startActivity(intent);
        } else {
            Alert.ActivaInternet(context);
        }
    }

    private void setupVistaCompleta() {
        //metodo="setupVistaCompleta()";
        llComentarios.setVisibility(View.VISIBLE);
        btnAddComentarios.setOnClickListener(this);
        imgComentario.setOnClickListener(this);
        btnCamaraImgComentario.setOnClickListener(this);
        btnFinalizarActividad.setOnClickListener(this);
    }

    private void setupVistaComentarios() {
        //metodo="setupVistaComentarios()";
        llComentarios.setVisibility(View.GONE);
    }

    public OnItemClickListener selectedItem = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
            metodo = "selectedItem.OnItemClickListener()";
            ComentarioOS item = (ComentarioOS) listaComentariosOS.get(posicion);
            if (!item.getHora().equals("0")) {
                if (item.getEstatusDeEntrega().equals(ESTATUS_PENDIENTE)) {
                    cursorBuscarComentarioPendiente = manager.buscarComentarioPorIdRegistroOS(item.getIdElementoOS());
                    cursorBuscarComentarioPendiente.moveToLast();
                    fechaCompleta = cursorBuscarComentarioPendiente.getString(GET_FECHA);
                    comentariosParaServidor = cursorBuscarComentarioPendiente.getString(GET_NOMBRE_USUARIO) + ": " +
                            fechaCompleta + " " +
                            cursorBuscarComentarioPendiente.getString(GET_COMENTARIOS) + " ";
                    idRegistroOS = cursorBuscarComentarioPendiente.getInt(GET_ID_TABLA_COMENT_OS);
                    MensajeEnConsola.log(context, metodo, "Comentarios: " + comentariosParaServidor);
                    barraProgresoGuardaComentario(OSid, comentariosParaServidor, idRegistroOS, ACTUALIZA_ELEMENTO, usuarioID, fechaCompleta);
                }
            }
            //detectamos si es un aimagen para mostrarsela al usuario en tamaño mas grande para que la veo mejor
            if (!item.getComentario_url().equals("")) {
                //inicia un nuevo intent para ver la imagen
                Intent intent = new Intent(context, MuestraSoloImagen.class);
                bndMuestraImagen = new Bundle();
                String[] nombre = item.getComentario_url().split("ImagenesOS/");
                String dirFile = SD_CARD + DIRECTORIO + File.separator + nombre[1];
                bndMuestraImagen.putString("imagen", dirFile);
                intent.putExtras(bndMuestraImagen);
                startActivity(intent);
            }
            //Detectatmos si es el boton de verga leria
            /*if(!item.getHora().equals("VG")){
				if(CheckService.internet(context)) {
					Intent intent = new Intent(context, VerGaleriaOS.class);
					Bundle bndVergaLeria = new Bundle();
					bndVergaLeria.putString("folio", String.valueOf(tvFolio.getText()));
					intent.putExtras(bndVergaLeria);
					startActivity(intent);
				}else{
					Alert.ActivaInternet(context);
				}
			}*/
        }
    };

    private void cargarComentariosOSTec() {
        metodo = "cargarComentariosOSTec()";
        int bandera = 0;
        String fechaActual, fechadmaDB;
        listaComentariosOS.clear();
        Cursor cargarComentarios = manager.buscarComentariosPorOSid(OSid);
        cargarComentarios.moveToFirst();
        fechaActual = getFechaCompleta().substring(0, 11);
        if (cargarComentarios.getCount() == 0) {
            Alert.Alerta(context, metodo, 1, "No hay comentarios tuyos para esta OS...");
        } else {
            do {
                fechadmaDB = cargarComentarios.getString(GET_FECHA).substring(0, 11);
                if (fechadmaDB.equals(fechaActual)) {
                    if (bandera == 0) {
                        listaComentariosOS.add(new ComentarioOS("0", "0", "0", fechaActual, "", "0", 0));
                        bandera = 1;
                    }
                } else {
                    bandera = 1;
                    fechaActual = cargarComentarios.getString(GET_FECHA).substring(0, 11);
                    listaComentariosOS.add(new ComentarioOS("0", "0", "0", fechaActual, "", "0", 0));
                }

                String[] fechaCortada = cargarComentarios.getString(GET_FECHA).split(" ");
                String hora = fechaCortada[3];
                String fecha = fechaCortada[0] + " " + fechaCortada[1] + " " + fechaCortada[2];

                String comentarioTexto = "", comentarioImagen = "";
                if (detectaURL(cargarComentarios.getString(GET_COMENTARIOS))) {//SI detecta url imagen
                    //	MensajeEnConsola.log(context, metodo, "MENSAJE COMENTARIO CON IMAGEN");
                    comentarioImagen = cargarComentarios.getString(GET_COMENTARIOS);
                } else {//NO detecta url imagen
                    //	MensajeEnConsola.log(context, metodo, "MENSAJE COMENTARIO SIN IMAGEN");
                    comentarioTexto = cargarComentarios.getString(GET_COMENTARIOS);
                }

                listaComentariosOS.add(new ComentarioOS(cargarComentarios.getString(GET_OS_ID), hora, fecha, comentarioTexto, comentarioImagen, cargarComentarios.getString(GET_ESTATUS_DE_ENTREGA),
                        cargarComentarios.getInt(GET_ID_TABLA_COMENT_OS)));
                //MensajeEnConsola.log(context, metodo, "\n"+cargarComentarios.getString(GET_OS_ID)+
                //									"\n"+hora+
                //									"\n"+fecha+
                //									"\n"+cargarComentarios.getString(GET_COMENTARIOS)+
                //									"\n"+cargarComentarios.getString(GET_ESTATUS_DE_ENTREGA)+
                //									"\n"+cargarComentarios.getInt(GET_ID_TABLA_COMENT_OS));

            } while (cargarComentarios.moveToNext());
        }
        adaptador = new ComentarioOSArrayAdapter(ReporteActividadOS.this, listaComentariosOS);
        lvListaComentariosOS.setAdapter(adaptador);
        lvListaComentariosOS.setSelection(lvListaComentariosOS.getAdapter().getCount() - 1);
		/*
		adaptador.notifyDataSetChanged();
		lvListaComentariosOS.setSelection(lvListaComentariosOS.getAdapter().getCount()-1);
		*/
    }

    boolean detectaURL(String cadena) {
        metodo = "detectaURL()";
        try {
            String[] temp1 = cadena.split("://");
            String[] temp2 = temp1[0].split("htt");
            MensajeEnConsola.log(context, metodo, "valor temp1[0] : " + temp1[0] + "\nvalor temp2[1] : " + temp2[1]);
            return "p".equals(temp2[1]);
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception valor e = " + e.getMessage());
        }
        return false;
    }

    private void actualizaEstatusActTec(String estatusOSActTec) {
        metodo = "actualizaEstatusActTec()";
        try {
            MensajeEnConsola.log(context, metodo, MainActivity.url + "actualizaEstActTec.php?osidx=" + OSid + "&estActTecx=" + estatusOSActTec);
            JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url + "actualizaEstActTec.php?osidx=" + OSid + "&estActTecx=" + estatusOSActTec);
            assert object != null;
            JSONArray jsonArray = object.optJSONArray("estUpdate");
            JSONObject jsonArrayChild1 = jsonArray.getJSONObject(0);
            if (jsonArrayChild1.optString("res").equals("1")) {
                ReporteActividadOS.this.finish();
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Alert.Alerta(context, metodo, 0, "¡Se ha finalizado la actividad con éxito!");
                                    }
                                });
                            }
                        }
                ).start();
            }
        } catch (JSONException e) {
            Alert.Error(context, metodo, getString(R.string.titJsonException), new ServerErrors(codigoHTTP, jsonResult).getErrorServer());
        } catch (Exception e) {
            Alert.Error(context, metodo, "Error MA001", e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFinalizarActividad:
                crearDialogoFinAct();
                break;
            case R.id.btnIngresarComentario:
                if (imgComentario.getVisibility() == View.GONE)
                    ingresaComentarioTexto();
                else
                    ingresaComentarioImagen();
                break;
            case R.id.btnCamaraImgComentario:
                startFotoGaleria();
                break;
            case R.id.imgComentarioOS:
                seApretoImagenFotoGaleria();
                break;
        }
    }

    private void seApretoImagenFotoGaleria() {
        metodo = "seApretoBotonFotoGaleria()";
        final CharSequence[] opciones = {"Ver", "Borrar", "Guardar", "Nueva_Foto", "Galeria", "Cancelar"};
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Selecciona...");
        alertDialog.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int seleccion) {
                if (opciones[seleccion] == "Ver") {
                    startMuestraSoloImagen();
                } else if (opciones[seleccion] == "Borrar") {
                    BorrarImagendeView();
                } else if (opciones[seleccion] == "Guardar") {
                    startGuardar();
                } else if (opciones[seleccion] == "Nueva_Foto") {
                    iniciaCamara();
                } else if (opciones[seleccion] == "Galeria") {
                    iniciaGaleria();
                }
				/*else if(opciones[seleccion] == "Cancelar"){
					Picasso.with(context).load(R.drawable.ic_camara).resize(50,50).into(btnCamaraImgComentario);
					imgComentario.setVisibility(View.GONE);
					etComentariosOS.setVisibility(View.VISIBLE);

				}*/
            }
        });
        alertDialog.create().show();
    }

    private void BorrarImagendeView() {
        metodo = "BorrarImagendeView()";
        CONTENIDO_IMAGEN = "";
        try {
            Picasso.with(context).load(R.drawable.ic_no_imagen).placeholder(R.drawable.ic_no_imagen).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgComentario);
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "ERROR al intentar poner la imagen de default con e = " + e.getMessage());
        }
    }

    private void startMuestraSoloImagen() {
        //primero la guardamos la imagen y despues le enviamos solo la direccion para que habra la imagen
        String file_name = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO + File.separator + IMAGEN_TEMPORAL;
        File new_file = new File(file_name);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new_file);

            Bitmap bitmap = ((BitmapDrawable) imgComentario.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RefrescarGaleria(new_file);

        //inicia un nuevo intent para ver la imagen
        Intent intent = new Intent(context, MuestraSoloImagen.class);
        bndMuestraImagen = new Bundle();
        bndMuestraImagen.putString("imagen", (SD_CARD + DIRECTORIO + File.separator + IMAGEN_TEMPORAL));
        intent.putExtras(bndMuestraImagen);
        startActivity(intent);
    }

    private void startFotoGaleria() {
        //cambiamos imagen del icono y hacemos visible su respectivo VIEW
        if (imgComentario.getVisibility() == View.VISIBLE) {//Aqui esta el view para mostrar imagen y el icono cambia a comentario texto
            muestraIngresoComentarios();
        } else {//Aqui esta el view para mostrar texto y el icono cambia a comentario imagen
            muestraIngresoImagen();
        }
    }

    private void muestraIngresoImagen() {
        seApretoBotonFotoGaleria();//inicia mecanismo para tomar foto o extraer de galeria
        Picasso.with(context).load(R.drawable.ic_no_imagen).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgComentario);
        Picasso.with(context).load(R.drawable.ic_libreta).resize(50, 50).memoryPolicy(MemoryPolicy.NO_CACHE).into(btnCamaraImgComentario);
        etComentariosOS.setVisibility(View.GONE);
        imgComentario.setVisibility(View.VISIBLE);
    }

    private void muestraIngresoComentarios() {
        Picasso.with(context).load(R.drawable.ic_camara).resize(50, 50).memoryPolicy(MemoryPolicy.NO_CACHE).into(btnCamaraImgComentario);
        imgComentario.setVisibility(View.GONE);
        etComentariosOS.setVisibility(View.VISIBLE);
    }

    private void ingresaComentarioImagen() {
        metodo = "ingresaComentarioImagen()";
        MensajeEnConsola.log(context, metodo, "");


        //startGuardar();//Guarda la imagen en el dispositivo
        if (CheckService.internet(context)) {
            startCompartirServidor();//Comparte con servidor la imagen
        } else {
            Alert.ActivaInternet(context);
        }
    }

    private void ingresaComentarioTexto() {
        metodo = "ingresaComentarioTexto()";
        if (etComentariosOS.getText().length() == 0) {
            Alert.Alerta(context, metodo, 1, "No has ingresado comentarios aún...");
        } else {
            manager.insertarComentarioOS(OSid, etComentariosOS.getText().toString(), getFechaCompleta(), ESTATUS_PENDIENTE, nombreDeUsuario + " " + apellido);
            cursorBuscarComentarioPendiente = manager.buscarComentariosPorOSidPendientes(OSid);
            cursorBuscarComentarioPendiente.moveToLast();
            fechaCompleta = cursorBuscarComentarioPendiente.getString(GET_FECHA);
            //fechaCompleta = fechaCompleta.replace(" ", "_");
            comentariosParaServidor = cursorBuscarComentarioPendiente.getString(GET_NOMBRE_USUARIO) + ": " +
                    fechaCompleta + " " +
                    cursorBuscarComentarioPendiente.getString(GET_COMENTARIOS) + " ";
            idRegistroOS = cursorBuscarComentarioPendiente.getInt(GET_ID_TABLA_COMENT_OS);
            comentariosParaServidor = comentariosParaServidor.replace("'", "");
            MensajeEnConsola.log(context, metodo, "Comentarios: " + comentariosParaServidor);
            etComentariosOS.getText().clear();
            barraProgresoGuardaComentario(OSid, comentariosParaServidor, idRegistroOS, AGREGA, usuarioID, fechaCompleta);
            cargarComentariosOSTec();
        }
    }

    private class ProgresoFinAct extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ReporteActividadOS.this);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        protected Void doInBackground(Void... params) {
            actualizaEstatusActTec(ListaOSAsignadas.OS_FINALIZADA);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();
        }

    }

    private class Progreso extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ReporteActividadOS.this);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        @Override
        protected Void doInBackground(Void... params) {
            metodo = "Progreso<AsyncTask>.doInBackground()";
            try {
                JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPdatosOSTec + "osidx=" + OSid);
                assert object != null;
                JSONArray jsonArray = object.optJSONArray("datosOSx");
                jsonArrayChildInicial = jsonArray.getJSONObject(0);
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvFolio.setText(jsonArrayChildInicial.optString("foliox"));
                                        tvSala.setText(jsonArrayChildInicial.optString("salax"));
                                        tvActividad.setText(jsonArrayChildInicial.optString("actividadx"));
                                        if (jsonArrayChildInicial.optString("fecIniActx").equals(null) || jsonArrayChildInicial.optString("fecIniActx").equals("null")) {
                                            tvIniAct.setText("Fecha Pendiente");
                                        } else {
                                            tvIniAct.setText(devuelveFechaConFormato(jsonArrayChildInicial.optString("fecIniActx")));
                                        }
                                        if (jsonArrayChildInicial.optString("fecFinActx").equals(null) || jsonArrayChildInicial.optString("fecFinActx").equals("null")) {
                                            tvFinAct.setText("Fecha Pendiente");
                                        } else {
                                            tvFinAct.setText(devuelveFechaConFormato(jsonArrayChildInicial.optString("fecFinActx")));
                                        }
                                    }
                                });
                            }
                        }).start();
            } catch (JSONException e) {
                MensajeEnConsola.log(context, metodo, "JSON Error\n" + e.getMessage() + "\nError de conexión con servidor remoto");
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Error FP001\n" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();
        }
    }

    @SuppressWarnings("deprecation")
    private void barraProgresoGuardaComentario(String osid, String comentario, int idRegistroOSComentarios, int agregaOActualiza, String usuarioid, String fechaTec) {
        metodo = "barraProgresoGuardaComentario()";
        MensajeEnConsola.log(context, metodo, "Comentario : " + comentario + "\ndetecta URL : " + detectaURL(comentario));
        this.idRegistroOSComentarios = idRegistroOSComentarios;
        this.agregaOActualiza = agregaOActualiza;
        if (CheckService.internet(context)) {
            if (detectaURL(comentario)) {
                comentarioEntregado(ESTATUS_ENTREGADO);
            } else {
                new ProgresoGuardaComentario().execute(MainActivity.url + docPHPguardaComentarioOS + "osidx=" + osid + "&comentx=" + URLEncoder.encode(comentario) + "&tecnicoidx=" + usuarioid + "&fechaTecx=" + URLEncoder.encode(fechaTec));
            }
        } else {
            Alert.ActivaInternet(context);
        }
    }

    int idRegistroOSComentarios, agregaOActualiza;

    private class ProgresoGuardaComentario extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ReporteActividadOS.this);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        @Override
        protected String doInBackground(String... params) {
            String regresa = "";
            try {
                JSONObject object = JSONparse.consultaURL(context, metodo, params[0]);
                assert object != null;
                JSONArray jsonArray = object.optJSONArray("guardaComent");
                jsonArrayChild = jsonArray.getJSONObject(0);
                regresa = jsonArrayChild.optString("guardaComentario");
            } catch (JSONException e) {
                MensajeEnConsola.log(context, metodo, getString(R.string.titJsonException) + "\n" + new ServerErrors(codigoHTTP, jsonResult).getErrorServer());
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Error MA001\n" + e.getMessage());
            }
            return regresa;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            comentarioEntregado(result);
        }
    }

    void comentarioEntregado(String result) {
        if (result.equals(ESTATUS_ENTREGADO)) {
            manager.actualizarEstatusDeEntrega(ESTATUS_ENTREGADO, idRegistroOSComentarios);
        }
        cargarComentariosOSTec();
    }

    private String getFechaCompleta() {
        Time now = new Time();
        now.setToNow();
        String year = now.toString().substring(0, 4);
        String month = now.toString().substring(4, 6);
        String day = now.toString().substring(6, 8);
        String hour = now.toString().substring(9, 11);
        String minutes = now.toString().substring(11, 13);

        if (month.equals("01"))
            month = "Ene";
        if (month.equals("02"))
            month = "Feb";
        if (month.equals("03"))
            month = "Mar";
        if (month.equals("04"))
            month = "Abr";
        if (month.equals("05"))
            month = "May";
        if (month.equals("06"))
            month = "Jun";
        if (month.equals("07"))
            month = "Jul";
        if (month.equals("08"))
            month = "Ago";
        if (month.equals("09"))
            month = "Sep";
        if (month.equals("10"))
            month = "Oct";
        if (month.equals("11"))
            month = "Nov";
        if (month.equals("12"))
            month = "Dic";

        return day + " " + month + " " + year + " " + hour + ":" + minutes;

    }

    private String devuelveFechaConFormato(String fechaSinFormato) {
        MensajeEnConsola.log(context, metodo, "Fecha sin formato= " + fechaSinFormato);
        String fechaMod;
        if (fechaSinFormato.equals(null) || fechaSinFormato.equals("null")) {
            fechaMod = "Fecha Pendiente";
        } else {
            String[] fechaDividida = fechaSinFormato.split(" ");
            String[] fechaCortada = fechaDividida[0].split("-");
            String day = fechaCortada[2];
            String month = fechaCortada[1];
            String year = fechaCortada[0];

            if (month.equals("01"))
                month = "Ene";
            if (month.equals("02"))
                month = "Feb";
            if (month.equals("03"))
                month = "Mar";
            if (month.equals("04"))
                month = "Abr";
            if (month.equals("05"))
                month = "May";
            if (month.equals("06"))
                month = "Jun";
            if (month.equals("07"))
                month = "Jul";
            if (month.equals("08"))
                month = "Ago";
            if (month.equals("09"))
                month = "Sep";
            if (month.equals("10"))
                month = "Oct";
            if (month.equals("11"))
                month = "Nov";
            if (month.equals("12"))
                month = "Dic";

            fechaMod = day + " " + month + " " + year + "\n" + fechaDividida[1];
        }
        return fechaMod;

    }

    private class ComentarioOS {
        private String idOS, hora, fecha, comentario, estatusDeEntrega;
        private int idElementoOS;
        private String Comentario_url;

        public ComentarioOS(String idOS, String hora, String fecha, String comentario, String Comentario_url, String estatusDeEntrega, int idElementoOS) {
            this.idOS = idOS;
            this.hora = hora;
            this.fecha = fecha;
            this.comentario = comentario;

            this.Comentario_url = Comentario_url;

            this.estatusDeEntrega = estatusDeEntrega;
            this.idElementoOS = idElementoOS;
        }

        public String getIdOS() {
            return idOS;
        }

        public String getHora() {
            return hora;
        }

        public String getFecha() {
            return fecha;
        }

        public String getComentario() {
            return comentario;
        }

        public String getComentario_url() {
            return Comentario_url;
        }

        public String getEstatusDeEntrega() {
            return estatusDeEntrega;
        }

        public int getIdElementoOS() {
            return idElementoOS;
        }
    }

    static class ViewHolderItem {
        TextView hora;
        TextView comentarios;
        RelativeLayout llBackground;
        TextView entregado;
        TextView falla;
        ImageView imgEntregado;
        ImageView imgFalla;

        ImageView imgComentarios;
        LinearLayout llComentario;
        RelativeLayout rlInfoComentario, rlbtnGaleria;
    }

    public class ComentarioOSArrayAdapter extends ArrayAdapter<ComentarioOS> {
        public ComentarioOSArrayAdapter(Context context, List<ComentarioOS> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderItem viewHolder;

            //Obteniendo una instancia del inflater
            //LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Contexto.LAYOUT_INFLATER_SERVICE);

            //Salvando la referencia del View de la fila
            View listItemView = convertView;


            //Comprobando si el View no existe
            if (null == convertView) {
                //Si no existe, entonces inflarlo con two_line_list_item.xml
                LayoutInflater inflater = (LayoutInflater) ((Activity) getContext()).getLayoutInflater();
                listItemView = inflater.inflate(R.layout.item_lista_comentarios_os, parent, false);

                viewHolder = new ViewHolderItem();
                viewHolder.hora = (TextView) listItemView.findViewById(R.id.tvHoraComOS);
                viewHolder.comentarios = (TextView) listItemView.findViewById(R.id.tvComOS);

                viewHolder.imgComentarios = (ImageView) listItemView.findViewById(R.id.imgComOS);
                viewHolder.llComentario = (LinearLayout) listItemView.findViewById(R.id.llComentarioOS);
                //viewHolder.rlInfoComentario = (RelativeLayout) listItemView.findViewById(R.id.rlComentariosOS1);
                //viewHolder.rlbtnGaleria = (RelativeLayout) listItemView.findViewById(R.id.rlComentariosOS2);

                viewHolder.llBackground = (RelativeLayout) listItemView.findViewById(R.id.llItemListaComent);
                viewHolder.entregado = (TextView) listItemView.findViewById(R.id.tvEntregado);
                viewHolder.falla = (TextView) listItemView.findViewById(R.id.tvFallaEntregado);
                viewHolder.imgEntregado = (ImageView) listItemView.findViewById(R.id.imgOk);
                viewHolder.imgFalla = (ImageView) listItemView.findViewById(R.id.imgError);

                // store the holder with the view.
                listItemView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderItem) convertView.getTag();
            }


            //Obteniendo instancia de la Tarea en la posicion actual
            ComentarioOS item = (ComentarioOS) getItem(position);
            if (item != null) {
                //Verga leria
				/*if("VG".equals(item.getHora())){
					viewHolder.llBackground.setBackgroundColor(Color.argb(0,0,0,0));
					viewHolder.llComentario.setVisibility(View.GONE);
					viewHolder.rlInfoComentario.setVisibility(View.GONE);
					viewHolder.rlbtnGaleria.setVisibility(View.VISIBLE);
				}else{
					viewHolder.llComentario.setVisibility(View.VISIBLE);
					viewHolder.rlInfoComentario.setVisibility(View.VISIBLE);
					viewHolder.rlbtnGaleria.setVisibility(View.GONE);
				}*/
                //Ver imagenes
                if ("".equals(item.getComentario_url())) {
                    viewHolder.imgComentarios.setVisibility(View.GONE);
                    viewHolder.comentarios.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.imgComentarios.setVisibility(View.VISIBLE);
                    viewHolder.comentarios.setVisibility(View.GONE);
                    String[] nombre = item.getComentario_url().split("ImagenesOS/");
                    String dirFile = SD_CARD + DIRECTORIO + File.separator + nombre[1];
                    Picasso.with(context).load(dirFile).placeholder(android.R.drawable.stat_sys_download).resize(90, 90).memoryPolicy(MemoryPolicy.NO_CACHE).into(viewHolder.imgComentarios);
                }
                //Ver solo el titulo en negro
                if (item.getHora().equals("0")) {
                    viewHolder.llBackground.setBackgroundColor(Color.parseColor("#000000"));
                    viewHolder.falla.setVisibility(View.GONE);
                    viewHolder.imgFalla.setVisibility(View.GONE);
                    viewHolder.entregado.setVisibility(View.GONE);
                    viewHolder.imgEntregado.setVisibility(View.GONE);
                    viewHolder.hora.setText("");
                    viewHolder.comentarios.setTextColor(Color.parseColor("#ffffff"));
                    viewHolder.comentarios.setText(item.getComentario());
                } else {
                    viewHolder.hora.setText(item.getHora());
                    viewHolder.comentarios.setText(item.getComentario());
                    viewHolder.comentarios.setTextColor(Color.parseColor("#000000"));

                    if (item.getEstatusDeEntrega().equals(ESTATUS_PENDIENTE)) {
                        viewHolder.falla.setVisibility(View.VISIBLE);
                        viewHolder.imgFalla.setVisibility(View.VISIBLE);
                        viewHolder.entregado.setVisibility(View.GONE);
                        viewHolder.imgEntregado.setVisibility(View.GONE);
                        viewHolder.llBackground.setBackgroundColor(Color.parseColor("#FA8072"));
                    } else {
                        viewHolder.falla.setVisibility(View.GONE);
                        viewHolder.imgFalla.setVisibility(View.GONE);
                        viewHolder.entregado.setVisibility(View.VISIBLE);
                        viewHolder.imgEntregado.setVisibility(View.VISIBLE);
                        viewHolder.llBackground.setBackgroundColor(Color.parseColor("#00FA9A"));
                    }
                }
            }

            //Devolver al ListView la fila creada
            return listItemView;
        }
    }

    private void crearDialogoFinAct() {
        final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(ReporteActividadOS.this);
        alertaSimple.setTitle("Fin de Actividad");
        alertaSimple.setMessage("¿Deseas finalizar la Actividad para la OS:" + OSid + "?");
        alertaSimple.setPositiveButton("Sí",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (CheckService.internet(context)) {
                            new ProgresoFinAct().execute();
                        } else {
                            Alert.ActivaInternet(context);
                        }
                    }
                });
        alertaSimple.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertaSimple.setIcon(R.drawable.warning);
        alertaSimple.create();
        alertaSimple.show();
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
            MenuInicialTecnico.iniciaMenuEstatusTec(ReporteActividadOS.this);
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


    //FOTO y GALERIA, TOMA y SUBIDA al servidor/////////////////////////////////////////////////////


    private static final int SELECCIONA_IMAGEN = 100;
    private static final int SELECCIONA_FOTO = 200;
    private static final int TAMANO_ANCHO = 1024;
    private static final int TAMANO_ALTO = 768;
    private static final String EXTENCION_IMAGEN = ".jpg";
    private static final String IMAGEN_TEMPORAL = "temporal" + EXTENCION_IMAGEN;
    private static final String DIRECTORIO = "ZITRO";
    private static final String docPHPSubirFotoFromAndroidToPhp = "SubirFotoFromAndroidToPhp.php?";
    private static final String NOMBRE_IMG_TEMP = "temporal";
    private static final String SD_CARD = "file:/sdcard/";

    private void setupFotoyGaleria() {
        File file = new File(Environment.getExternalStorageDirectory(), DIRECTORIO);
        file.mkdirs();
    }

    private void seApretoBotonFotoGaleria() {
        metodo = "seApretoBotonFotoGaleria()";
        final CharSequence[] opciones = {"Camara", "Galeria", "Cancelar"};
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Selecciona...");
        alertDialog.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int seleccion) {
                if (opciones[seleccion] == "Camara") {
                    iniciaCamara();
                } else if (opciones[seleccion] == "Galeria") {
                    iniciaGaleria();
                } else if (opciones[seleccion] == "Cancelar") {
                    muestraIngresoComentarios();
                }
            }
        });
        alertDialog.create().show();
    }

    private void iniciaCamara() {
        String path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO + File.separator + IMAGEN_TEMPORAL;

        File newfile = new File(path);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newfile));
        startActivityForResult(intent, SELECCIONA_FOTO);
    }

    private void iniciaGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, "Selecciona imagen"), SELECCIONA_IMAGEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        metodo = "onActivityResult()";
        switch (requestCode) {
            case SELECCIONA_FOTO:
                if (resultCode == RESULT_OK) {
                    CONTENIDO_IMAGEN = "LLENA";
                    String path = SD_CARD + DIRECTORIO + File.separator + IMAGEN_TEMPORAL;
                    RefrescarGaleria(new File(path));
                    Picasso.with(context).load(path).placeholder(R.drawable.ic_no_imagen).resize(TAMANO_ANCHO, TAMANO_ALTO).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgComentario);
                }
                break;
            case SELECCIONA_IMAGEN:
                if (resultCode == RESULT_OK) {
                    CONTENIDO_IMAGEN = "LLENA";
                    Uri path = data.getData();
                    Picasso.with(context).load(path).placeholder(R.drawable.ic_no_imagen).resize(TAMANO_ANCHO, TAMANO_ALTO).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgComentario);
                }
                break;
        }
    }

    private void startGuardar() {
        if (imgComentario.getDrawable() != null) {
            File file = getDisc();
            String file_name = file.getAbsolutePath() + File.separator + getCode() + EXTENCION_IMAGEN;
            File new_file = new File(file_name);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new_file);

                Bitmap bitmap = ((BitmapDrawable) imgComentario.getDrawable()).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

                Toast.makeText(context, "Imagen guardada con nombre : " + file_name, Toast.LENGTH_LONG).show();

                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            RefrescarGaleria(new_file);
        } else {
            Toast.makeText(context, "Toma una Foto o seleciona de Galeria!!!", Toast.LENGTH_SHORT).show();
        }
    }

    void RefrescarGaleria(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    public File getDisc() {
        return new File(Environment.getExternalStorageDirectory(), DIRECTORIO);
    }

    @SuppressLint("SimpleDateFormat")
    public String getCode() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        return "pic_" + date;
    }

    void startCompartirServidor() {
        metodo = "setupCompartir()";
        if (imgComentario.getDrawable() != null && CONTENIDO_IMAGEN.equals("LLENA")) {
            //Primero guardamos el archivo imagen
            File file = getDisc();
            String file_name = file.getAbsolutePath() + File.separator + NOMBRE_IMG_TEMP + EXTENCION_IMAGEN;
            File new_file = new File(file_name);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new_file);

                Bitmap bitmap = ((BitmapDrawable) imgComentario.getDrawable()).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            RefrescarGaleria(new_file);
            String[] temp = MainActivity.url.split("Android/");
            String dominio = temp[0];
            new SubirImagenAServidor("", "", "").execute(MainActivity.url + docPHPSubirFotoFromAndroidToPhp + "folioOS=" + tvFolio.getText().toString() + "&idusu=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID") + "&urlAbsoluta=" + dominio);
        } else {
            Toast.makeText(context, "Toma una Foto o seleciona de Galeria!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public class SubirImagenAServidor extends AsyncTask<String, Void, Void> {

        URL connectURL;
        String params;
        String fileName;

        SubirImagenAServidor(String urlString, String params, String fileName) {
            metodo = "SubirImagenAServidor()";
            try {
                connectURL = new URL(urlString);
                MensajeEnConsola.log(context, metodo, "urlString = " + urlString);
            } catch (Exception ex) {
                MensajeEnConsola.log(context, metodo, "Exception = " + ex.getMessage());
            }
            this.params = params + "=";
            this.fileName = fileName;
        }

        void doStart(FileInputStream stream) {
            fileInputStream = stream;
            thirdTry();
        }

        FileInputStream fileInputStream = null;

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        void thirdTry() {
            metodo = "thirdTry()";
            String exsistingFileName = fileName;

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            try {
                //------------------ CLIENT REQUEST

                MensajeEnConsola.log(context, metodo, "");
                // Abrimos una conexión http con la URL

                HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();

                // Permitimos Inputs
                conn.setDoInput(true);

                // Permitimos Outputs
                conn.setDoOutput(true);

                // Deshabilitamos el uso de la copia cacheada.
                conn.setUseCaches(false);

                // Usamos el método post esto podemos cambiarlo.
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + exsistingFileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                MensajeEnConsola.log(context, metodo, "Escribio HEADERS = " + dos);

                // creamos un buffer con el tamaño maximo de archivo, lo pondremos en 1MB

                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // enviar multipart form data

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // cerramos
                MensajeEnConsola.log(context, metodo, "Archivo se esta escribiendo");
                fileInputStream.close();
                dos.flush();

                InputStream is = conn.getInputStream();
                // retrieve the response from server
                int ch;

                StringBuffer b = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    b.append((char) ch);
                }
                String s = b.toString();//Mensaje recivido, lo tomamos para guardarlo en nuestra base y enviarlo como comentario
                regresaURLimagen = connectURL + "---" + s;

                dos.close();

            } catch (NetworkOnMainThreadException ex) {
                MensajeEnConsola.log(context, metodo, "NetworkOnMainThreadException ex = " + ex.getMessage());
            } catch (MalformedURLException ex) {
                MensajeEnConsola.log(context, metodo, "MalformedURLException ex = " + ex.getMessage());
            } catch (IOException ex) {
                MensajeEnConsola.log(context, metodo, "IOException ex = " + ex.getMessage());
            } catch (Exception ex) {
                MensajeEnConsola.log(context, metodo, "Exception ex = " + ex.getMessage());
            }
        }

        @Override
        protected void onPreExecute() {
            MensajeEnConsola.log(context, metodo, " onPreExecute()");
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        @Override
        protected Void doInBackground(String... url) {
            try {
                String filename = SD_CARD + DIRECTORIO + File.separator + NOMBRE_IMG_TEMP + EXTENCION_IMAGEN;
                File file = getDisc();
                String file_name = file.getAbsolutePath() + File.separator + NOMBRE_IMG_TEMP + EXTENCION_IMAGEN;
                File new_file = new File(file_name);
                FileInputStream fis = new FileInputStream(new_file);
                MensajeEnConsola.log(context, metodo, "SUBIENDO IMAGEN\nurl" + url[0] + "\nnoparamshere\nfilename = " + filename);
                SubirImagenAServidor htfu = new SubirImagenAServidor(url[0], "noparamshere", filename);
                htfu.doStart(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            MensajeEnConsola.log(context, metodo, "onPostExecute()");
            super.onPostExecute(result);
            pDialog.dismiss();
            procesoImagenAsComentario();
        }
    }

    private void procesoImagenAsComentario() {
        try {
            metodo = "procesoImagenAsComentario()";
            String[] temp = regresaURLimagen.split("---");
            MensajeEnConsola.log(context, metodo, "Envia = " + temp[0] + "\nResponde = " + temp[1]);

            if (temp[1].equals("ERROR") || !detectaURL(temp[1])) {
                //fallo la subida y solo mostraremos un mensaje
                Toast.makeText(context, "ERROR al subir imagen", Toast.LENGTH_LONG).show();
            } else {
                //exitoso en la subida de la imagen
                startGuardarfromPicasso(temp[1]);
            }
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "ERROR en Exception con e = " + e.getMessage());
        }
    }

    private void startGuardarfromPicasso(String nombreBruto) {
        metodo = "startGuardarfromPicasso()";
        String[] nombre = nombreBruto.split("ImagenesOS/");

        try {
            File file = getDisc();
            String file_name = file.getAbsolutePath() + File.separator + nombre[1];
            File new_file = new File(file_name);

            FileOutputStream fileOutputStream = new FileOutputStream(new_file);

            Bitmap bitmap = ((BitmapDrawable) imgComentario.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

            MensajeEnConsola.log(context, metodo, "Imagen guardada con \nurl = " + nombreBruto + "\nnombre = " + nombre[1] + "\ndir = " + file_name);
            fileOutputStream.flush();
            fileOutputStream.close();
            RefrescarGaleria(new_file);
        } catch (IOException e) {
            MensajeEnConsola.log(context, metodo, "ERROR IOException con e = " + e.getMessage());
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "ERROR Exception con e = " + e.getMessage());
        }
        BorrarImagendeView();
        muestraIngresoComentarios();
        etComentariosOS.setText(nombreBruto);
        ingresaComentarioTexto();
    }
}
//1280