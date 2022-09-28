package com.example.devolucionmaterial.activitys.osSeccion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.Adapters.ComentarioOSArrayAdapter;
import com.example.devolucionmaterial.Adapters.GalleryAdapter;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.MuestraSoloImagen;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.ComentarioOS;
import com.example.devolucionmaterial.beans.ImageOS;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.fragments.ImageOsFragment;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.ServerErrors;
import com.example.devolucionmaterial.utils.FileFromBitmap;
import com.odn.multiphotopicker.PhotoPickerActivity;
import com.odn.multiphotopicker.utils.PhotoPickerIntent;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.thanosfisherman.mayi.PermissionBean;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

public class ReporteActividadOS extends BaseActivity implements OnClickListener, FileFromBitmap.CommunicationChannel, ImageOsFragment.Callback {
    private static String CONTENIDO_IMAGEN = "";
    String metodo;
    private ListView lvListaComentariosOS;
    private List<ComentarioOS> listaComentariosOS;
    private ArrayAdapter<ComentarioOS> adaptador;
    private Bundle bndReceptor, bndMuestraImagen;
    private Intent intReceptor;
    private String usuarioID, nombreDeUsuario, OSid, tipoDeVistaOS, jsonResult;
    private LinearLayout llComentarios;
    private Button btnAddComentarios, btnFinalizarActividad, btnIngresarFotosSala;
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
    //private String statusButFinalizaAct;

    ImageView btnVerGaleria;
    GalleryAdapter galleryAdapter;
    RecyclerView rvGallery;
    public List<MultipartBody.Part> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_actividad_os);
        initToolbar("Reporte de OS",true,true);
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

    @Override
    protected void onRestart() {
        super.onRestart();

      /*  if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }*/

    }

    private void initSetUp() {
        metodo = "setupActividadOS()";
        setupFotoyGaleria();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        manager = new BDmanager(context);
        etComentariosOS = (EditText) findViewById(R.id.etComentarios);
        btnAddComentarios = (Button) findViewById(R.id.btnIngresarComentario);
        btnFinalizarActividad = (Button) findViewById(R.id.btnFinalizarActividad);
        /**NEW FUNCION 2022**/
        btnIngresarFotosSala = (Button) findViewById(R.id.btnIngresarFotosSala);

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
        rvGallery = (RecyclerView) findViewById(R.id.aiao_rv_galerry_preview);
        btnVerGaleria.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AbreGaleriaImagenesSubidasOS();
            }
        });
        files = new ArrayList<>();
        intReceptor = this.getIntent();
        bndReceptor = intReceptor.getExtras();
        usuarioID = bndReceptor.getString("usuarioidx");
        apellido = bndReceptor.getString("apellidox");
        nombreDeUsuario = bndReceptor.getString("nombreusuariox");
        OSid = bndReceptor.getString("OSidx");
        tipoDeVistaOS = bndReceptor.getString("tipoDeVistaOSx");

       //statusButFinalizaAct=bndReceptor.getString("liberaFinaliza");


        usuarioID = bndReceptor.getString("usuarioidx");
        listaComentariosOS = new ArrayList<ComentarioOS>();
        tvUsuario.setText(nombreDeUsuario);

        // if(statusButFinalizaAct.equals("1")){ btnFinalizarActividad.setVisibility(View.VISIBLE);}
        // else{ btnFinalizarActividad.setVisibility(View.GONE);}


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
        btnIngresarFotosSala.setOnClickListener(this);
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
                bndMuestraImagen.putString("imagen", item.getComentario_url());
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
                    Log.e("comentarioImagen", comentarioImagen);
                } else {//NO detecta url imagen
                    //	MensajeEnConsola.log(context, metodo, "MENSAJE COMENTARIO SIN IMAGEN");
                    comentarioTexto = cargarComentarios.getString(GET_COMENTARIOS);
                }

                listaComentariosOS.add(
                        new ComentarioOS(
                                cargarComentarios.getString(GET_OS_ID),
                                hora,
                                fecha,
                                comentarioTexto,
                                comentarioImagen,
                                cargarComentarios.getString(GET_ESTATUS_DE_ENTREGA),
                                cargarComentarios.getInt(GET_ID_TABLA_COMENT_OS))
                );
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFinalizarActividad:
                crearDialogoFinAct();
                break;
            case R.id.btnIngresarComentario:
                if (rvGallery.getVisibility() == View.GONE)
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
            case R.id.btnIngresarFotosSala:
                setActivateCameraFotoSala();
                break;
        }
    }

    private void setActivateCameraFotoSala() {
        //seApretoBotonFotoGaleria();
        btnIngresarFotosSala.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(context, CameraSalaFotoActivity.class));
            }
        });

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
                    //iniciaCamara();
                } else if (opciones[seleccion] == "Galeria") {
                    //iniciaGaleria();
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
        rvGallery.setVisibility(View.VISIBLE);
    }

    private void muestraIngresoComentarios() {
        Picasso.with(context).load(R.drawable.ic_camara).resize(50, 50).memoryPolicy(MemoryPolicy.NO_CACHE).into(btnCamaraImgComentario);
        imgComentario.setVisibility(View.GONE);
        rvGallery.setVisibility(View.GONE);
        etComentariosOS.setVisibility(View.VISIBLE);
    }

    private void ingresaComentarioImagen() {
        metodo = "ingresaComentarioImagen()";
        MensajeEnConsola.log(context, metodo, "");
        /**AMIGO SE DEBE DE SUBIR AL SERVIDOR AL MOENTO DE SELECCIONARLO EN DONDE TE DIJE QUE LO OCUPABAN ES UN CHAT Y PUEDES VISUALIZARLO, AQUI NO */

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
            pDialog= new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.Conectando_con_servidor_remoto))
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();
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
            pDialog= new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.Conectando_con_servidor_remoto))
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();
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
            pDialog= new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.Conectando_con_servidor_remoto))
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();
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



    public void onResume() {
        super.onResume();
        actualizaEstatusTecnico();
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

        PhotoPickerIntent intent = new PhotoPickerIntent(this);
        intent.setPhotoCount(5);
        intent.setShowCamera(true);
        intent.setShowGif(true);
        intent.setMultiChoose(true);
        startActivityForResult(intent, REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        metodo = "onActivityResult()";

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                /*for (int i = 0; i < photos.size(); i++) {
                    Log.e("xfbhfghfyh", photos.get(i));

                }*/
                FileFromBitmap fileFromBitmap = new FileFromBitmap(photos, this);
                fileFromBitmap.setmCommChListner(this);
                fileFromBitmap.execute();

            } else {
                this.files = new ArrayList<>();
                muestraIngresoComentarios();
            }
        }else{
            muestraIngresoComentarios();
        }
    }

    @Override
    public void setCommunication(List<MultipartBody.Part> files, List<String> images) {
        this.files = files;
        ArrayList<String> iamgess = (ArrayList<String>) images;
        Bundle args = new Bundle();
        args.putStringArrayList("list", iamgess);

        ImageOsFragment imageOsFragment = new ImageOsFragment();
        imageOsFragment.setArguments(args);
        imageOsFragment.setCancelable(false);
        imageOsFragment.show(getFragmentManager(), "newGroupFragment");
       // initRv(images);
    }

    void initRv(List<String> photos) {
        galleryAdapter = new GalleryAdapter(this, photos);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvGallery.setLayoutManager(horizontalLayoutManagaer);
        rvGallery.setAdapter(galleryAdapter);
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

        if (!files.isEmpty()) {
            pDialog= new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.Conectando_con_servidor_remoto))
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();

            String[] temp = MainActivity.url.split("Android/");
            String dominio = temp[0];
            RequestBody folio = RequestBody.create(MediaType.parse("text/plain"), tvFolio.getText().toString());
            RequestBody idusu = RequestBody.create(MediaType.parse("text/plain"), BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
            RequestBody urlAbsoluta = RequestBody.create(MediaType.parse("text/plain"), dominio);


            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<ImageOS> call = serviceApi.uploadImageOS(files, folio, idusu, urlAbsoluta);
            call.enqueue(new Callback<ImageOS>() {
                @Override
                public void onResponse(Call<ImageOS> call, retrofit2.Response<ImageOS> response) {
                    pDialog.dismiss();
                    files.clear();
                    for (int i = 0; i < response.body().getListUrl().size(); i++) {
                       // regresaURLimagen = response.body().getListUrl().get(i);
                        //procesoImagenAsComentario();
                        startGuardarfromPicasso(response.body().getListUrl().get(i));
                    }
                    cargarComentariosOSTec();
                }

                @Override
                public void onFailure(Call<ImageOS> call, Throwable t) {files.clear();
                    files.clear();
                    pDialog.dismiss();
                    Toast.makeText(context, "ERROR al subir imagen", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(context, "Toma una Foto o seleciona de Galeria!!!", Toast.LENGTH_SHORT).show();
        }


    }


    public void procesoImagenAsComentario() {
        try {
            metodo = "procesoImagenAsComentario()";
            String[] temp = regresaURLimagen.split("---");
            MensajeEnConsola.log(context, metodo, "Envia = " + temp[0] + "\nResponde = " + temp[1]);

            if (temp[1].equals("ERROR") || !detectaURL(temp[1])) {
                //fallo la subida y solo mostraremos un mensaje
                Toast.makeText(context, "ERROR al subir imagen", Toast.LENGTH_LONG).show();
            } else {
                //exitoso en la subida de la imagen
                startGuardarfromPicasso(regresaURLimagen);
            }
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "ERROR en Exception con e = " + e.getMessage());
        }
    }

    // TODO: 12/09/2017 se modifico para que solo guarde la url
    private void startGuardarfromPicasso(String nombreBruto) {
        Log.e("imagen para guadsar",nombreBruto);
        metodo = "startGuardarfromPicasso()";
        BorrarImagendeView();
        muestraIngresoComentarios();
        etComentariosOS.setText(nombreBruto);
        // ingresaComentarioTexto();

        manager.insertarComentarioOS(OSid, nombreBruto, getFechaCompleta(), ESTATUS_PENDIENTE, nombreDeUsuario + " " + apellido);
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

    }

    @Override
    public void setCallback(Boolean msg) {
        if (msg) {
            ingresaComentarioImagen();
        } else {
          rvGallery.setVisibility(View.GONE);
            muestraIngresoComentarios();
        }
    }
}




//1280