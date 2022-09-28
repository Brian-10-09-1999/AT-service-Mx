package com.example.devolucionmaterial;

import org.json.JSONObject;

import android.Manifest;
import android.app.ActionBar;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.activitys.MenuInicial;
import com.example.devolucionmaterial.activitys.VistaMLeme;
import com.example.devolucionmaterial.activitys.viaticos.Activity_Lista;
import com.example.devolucionmaterial.beans.BeansGlobales;
import com.example.devolucionmaterial.checks.Device;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.data_base.ControlTotalLocalxRemoto;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSON;
import com.example.devolucionmaterial.internet.AppDownload;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.services.InitService;
import com.example.devolucionmaterial.sharedpreferences.PreferencesVar;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.utils.LocationRegion;
import com.thanosfisherman.mayi.PermissionBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends BaseActivity {
    BDmanager manager;
    Context context;
    public static String url = "";
    private EditText txtUsername, txtPass;
    Button btnLogin;
    private ImageView logo;

    int contador = 1;
    LocationManager mlocManager;
    LocationRegion mlocListener;
    //protected MaterialDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        metodo = "onCreate()";
        Window window = getWindow();


        if(Build.VERSION.SDK_INT>=21){
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));}
        else{ }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initToolbar("Entrar", false, false);
        context = this;
        manager = new BDmanager(context);
        new PreferencesVar(context);
        localizandoPais();
        logo = (ImageView) findViewById(R.id.img);
/*
        BDVarGlo.setVarGlo(context, "INFO_USUARIO_ALIAS", "gilbertor");
        BDVarGlo.setVarGlo(context, "INFO_USUARIO_PASSWORD", "10028");
        BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID", "152");
        BDVarGlo.setVarGlo(context, "INFO_USUARIO_TIPO", "0");
        //BDVarGlo.setVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO", "noTecnico");
        BDVarGlo.setVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO", "Tecnico");
*/


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        } else {
            initSetUp();
        }

        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
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

        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }*/

    }

    private void initSetUp() {
        BDVarGlo.INICIALIZA(context);

        try {boolean isProduccion = Boolean.valueOf(com.example.devolucionmaterial.Util.getProperty("IsProduccion", getApplicationContext()));

            if (isProduccion) {

                BDVarGlo.setVarGlo(context, "APP_PRUEBAS_o_PRODUCCION", "PRODUCCION");
               // BDVarGlo.setVarGlo(context, "APP_PRUEBAS_o_PRODUCCION", "PRODUCCION-LOCAL");
            }
            else{
                logo.setImageResource(R.drawable.icon_pruebas);
                BDVarGlo.setVarGlo(context, "APP_PRUEBAS_o_PRODUCCION", "PRUEBAS");

            }

            //BDVarGlo.setVarGlo(context, "APP_PRUEBAS_o_PRODUCCION", "PRODUCCION");
            //BDVarGlo.setVarGlo(context, "APP_PRUEBAS_o_PRODUCCION", "PRODUCCION-LOCAL");
            //URL_NUBE_ZITRO
        }
        catch (IOException e){ e.printStackTrace();}


        //lo siguiente solo se ocupara hasta que pasemos todos los scripts a android.php
        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION")) {
            url = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRODUCCION") + "Android/";

        }
        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")) {
            url = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRUEBAS") + "Android/";
            //url = BDVarGlo.getVarGlo(context, "URL_NUBE_ZITRO") + "Android/";
        }
        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION-LOCAL")) {
            url = BDVarGlo.getVarGlo(context, "URL_DOMINIO_PRODUCCION_LOCAL") + "Android/";
        }

        new BeansGlobales(context);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPass = (EditText) findViewById(R.id.txtPass);
        compruebaInvitado();




        if (CheckService.internet(context)) {
            if( !BDVarGlo.getVarGlo(context, "INFO_USUARIO_ALIAS").equals("") || !BDVarGlo.getVarGlo(context, "INFO_USUARIO_PASSWORD").equals("")){
                Log.i("--------------******","alias:"+BDVarGlo.getVarGlo(context, "INFO_USUARIO_ALIAS")+"|pass:"+BDVarGlo.getVarGlo(context, "INFO_USUARIO_PASSWORD")+"|");
                new CargaInfoUsuario().execute();
            }
            else {
                setupMainActivity();
            }
        }
        else {
            Alert.ActivaInternet(context);
        }

         InitService.start(context);

        //registramos el receiver para ser notificados del final de la descarga cuando la haya
        //IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        //context.registerReceiver(downloadReceiver, filter);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }





    public void setLocation(Location loc) {

        new PreferencesVar(context);
        //Obtener la direcci—n de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {

                    Address address = list.get(0);

                    String pais = String.format("%s",
                            address.getCountryName());

                    //Log.e("pais", pais);
                    MensajeEnConsola.log(context,"setLocation","pais: "+pais);

                    int valor = 0;
                    String[] lPaises = getResources().getStringArray(R.array.paises);
                    for (int i = 0; i < lPaises.length; i++) {
                        if (lPaises[i].equals(pais)) {
                            valor = i;

                        }
                    }

                    switch (valor) {
                        case 0:
                            PreferencesVar.setPais(pais, 0);

                            break;
                        case 1:
                            PreferencesVar.setPais(pais, 1);
                            break;

                    }

                    if (!pais.equals("")) {
                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mlocManager.removeUpdates(mlocListener);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                //Log.e("errorlocation", String.valueOf(e));

                MensajeEnConsola.log(context,"setLocation","errorlocation: "+ String.valueOf(e));

            }
        }
    }

    private void localizandoPais() {

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new LocationRegion();
        mlocListener.setMainActivity(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                (LocationListener) mlocListener);
    }


    private void compruebaInvitado() {
        if ("invitado".equals(BDVarGlo.getVarGlo(context, "VAR_VISTA_M_LEME_INVITADO"))) {
            BDVarGlo.setSeteaVarGloUsuario();
            txtUsername.setText(BDVarGlo.getVarGlo(context, "VAR_VISTA_M_LEME_INVITADO"));
        } else {
            BDVarGlo.setVarGlo(context, "VAR_VISTA_M_LEME_INVITADO", "");
        }
    }






    class CargaInfoUsuario extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;


        protected void onPreExecute() {

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
          /*  pDialog = new MaterialDialog.Builder(context)
                    .title(getString(R.string.Conectando_con_servidor_remoto))
                    //.customView(R.layout.custom_progressdialog, true)
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                  //  .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT))
                    .show();*/
        }

        protected Void doInBackground(Void... JSONurl) {
            // Log.e("correo", Device.getCuentas(context));
            metodo = "CargaInfoUsuario()";
            String alias;
            String password;
            String email = null;
            String telefono = null;
            if (txtUsername.getText().toString().trim().length() > 0 ||
                    txtPass.getText().toString().trim().length() > 0) {
                alias = BDVarGlo.getVarGlo(context, "INFO_USUARIO_ALIAS");
                password = BDVarGlo.getVarGlo(context, "INFO_USUARIO_PASSWORD");
            } else {
                email = BDVarGlo.getVarGlo(context, "INFO_USUARIO_EMAIL");
                Device device = new Device();
                if ("".equals(email) || "0".equals(email)) email = device.getCuentas(context);
                telefono = BDVarGlo.getVarGlo(context, "INFO_USUARIO_TELEFONO");
                if ("".equals(telefono) || "0".equals(telefono))
                    // telefono = Device.getPhoneNumber1(context);todo se cambio por segurida
                    telefono = "0";
                alias = BDVarGlo.getVarGlo(context, "INFO_USUARIO_ALIAS");
                password = BDVarGlo.getVarGlo(context, "INFO_USUARIO_PASSWORD");
            }


            try {


                JSONObject object = JSON.load(context, metodo, BDVarGlo.getVarGlo(context, "FUNCTION_CARGAR_INFO_USUARIO") + "&email=" + email + "&telefono=" + telefono + "&alias=" + alias + "&password=" + password);
                assert object != null;
                MensajeEnConsola.log(context, metodo, "respuesta="+object.toString());
                String region = object.getString("USUARIO_ID_REGION");
                //DATOS PERSONALES DE LOS USUARIOS
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID", object.getString("USUARIO_ID"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_SUPERVISOR", object.getString("USUARIO_ID_SUPERVISOR"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_REGION", object.getString("USUARIO_ID_REGION"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTADO", object.getString("USUARIO_ID_ESTADO"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_NOMBRE_COMPLETO", object.getString("USUARIO_NOMBRE_COMPLETO"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE", object.getString("USUARIO_PRIMER_NOMBRE"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_SEGUNDO_NOMBRE", object.getString("USUARIO_SEGUNDO_NOMBRE"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_PRIMER_APELLIDO", object.getString("USUARIO_PRIMER_APELLIDO"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_SEGUNDO_APELLIDO", object.getString("USUARIO_SEGUNDO_APELLIDO"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_EMAIL", object.getString("USUARIO_EMAIL"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_TELEFONO", object.getString("USUARIO_TELEFONO"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ALIAS", object.getString("USUARIO_ALIAS"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_PASSWORD", object.getString("USUARIO_PASSWORD"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_TIPO", object.getString("USUARIO_TIPO"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO", object.getString("USUARIO_TIPO_DE_USUARIO"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", object.getString("USUARIO_ID_ESTATUS"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", object.getString("USUARIO_ESTATUS_EN_SERVICIO"));
                BDVarGlo.setVarGlo(context, "INFO_APP_SERVIDOR_VERSION", object.getString("APP_SERVIDOR_VERSION"));

                // TODO: 25/01/2017 esta metodo limpia las salas y regiones cuando ya existe un usuario de otra region y se logea con otra region diferente
                if (PreferencesVar.getIdRegion() != Integer.valueOf(region)) {
                    manager.borrarRegion();
                    // manager.borrarSalas();
                    //manager.borrarVariableGlobal();
                }

                // TODO: 20/01/2017 actualiza la tabal de regiones cada que entra un numevo user
                if (region.equals("1") || region.equals("2") || region.equals("3")
                        || region.equals("4") || region.equals("5")
                        || region.equals("6") || region.equals("7")
                        || region.equals("59") || region.equals("60")
                        || region.equals("61") || region.equals("62")) {
                    //manager.borrarRegion();
                    if ("SSS".equals(manager.consulta("SELECT nombre FROM cregion WHERE id = 1", "SSS"))) {
                        manager.actualiza("INSERT INTO cregion (id, nombre) VALUES (1,'CENTRO'),(2,'DFZ1'),(59,'DFZ2'),(4,'GUADALAJARA'),(" +
                                "5,'MONTERREY'),(6,'PACIFICO'),(60,'PENINSULA'),(3,'PUEBLA / VERACRUZ'),(7,'TIJUANA')");
                        PreferencesVar.setRegionId(Integer.parseInt(region));
                        PreferencesVar.setPaisRegion(1);//uno es para mexico
                    }
                } else {
                    // manager.borrarRegion();
                    if ("SSS".equals(manager.consulta("SELECT nombre FROM cregion WHERE id = 19", "SSS"))) {
                        PreferencesVar.setRegionId(Integer.parseInt(region));
                        PreferencesVar.setPaisRegion(2);//uno es para España
                        manager.actualiza("INSERT INTO cregion (id, nombre)" +
                                " VALUES " +
                                "(19,'ANDALUCIA')," +
                                "(20,'ARAGON')," +
                                "(40,'ASTURIAS')," +
                                "(24,'CANARIAS')," +
                                "(48,'CANTABRIA')," +
                                "(26,'CASTILLA LA MANCHA')," +
                                "(18,'CASTILLA Y LEON')," +
                                "(57,'CATALUÑA')," +
                                "(36,'CEUTA')," +
                                "(14,'ESPAÑA')," +
                                "(27,'EXTREMADURA')," +
                                "(51,'LA RIOJA')," +
                                "(23,'MADRID')," +
                                "(37,'MELILLA')," +
                                "(42,'MURCIA')," +
                                "(49,'NAVARRA')," +
                                "(50,'PAIS VASCO')," +
                                "(47,'VALENCIA')");
                    }
                }
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            metodo = "ProgresoPass<AsyncTask>onPostExecute";
            pDialog.dismiss();

            //aqui se evalua si existe el usuario en la base de datos en el servidor
            if (!"0".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"))) {
                if (!BDVarGlo.getVarGlo(context, "INFO_APP_SERVIDOR_VERSION").equals(Device.versionName(context))) {
                    actualizarApp();
                } else {
                    enviarDatosAActividad();
                }
            } else {
                txtUsername.setText("");
                txtPass.setText("");

                if (contador == 1) {
                    contador++;
                    Alert.Alerta(context, metodo, 1, getResources().getString(R.string.e_mailIncorrecto));
                } else {
                    Alert.Alerta(context, metodo, 2, getResources().getString(R.string.usuarioIncorrecto));
                }

            }
            setupMainActivity();
        }
    }








    private void setupMainActivity() {
        metodo = "setupMainActivity()";
        compruebaInvitado();


        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //startActivity(new Intent(context, VistaMLeme.class));

                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ALIAS", txtUsername.getText().toString());
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_PASSWORD", txtPass.getText().toString());

                if (CheckService.internet(context)) {
                    if ("invitado".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ALIAS"))) {
                        BDVarGlo.setVarGlo(context, "VAR_VISTA_M_LEME_INVITADO", "invitado");
                        BDVarGlo.setVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE", "INVITADO");
                        startActivity(new Intent(context, VistaMLeme.class));
                    } else {
                        BDVarGlo.setVarGlo(context, "VAR_VISTA_M_LEME_INVITADO", "");
                        //new ProgresoPass().execute(BDVarGlo.getVarGlo(context, "FUNCTION_VALIDAR_USUARIO") + "&usuariox=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ALIAS") + "&passx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_PASSWORD"));
                        new CargaInfoUsuario().execute();
                    }
                } else {
                    Alert.ActivaInternet(context);
                }



            }
        });



    }








    private void enviarDatosAActividad() {
        //Acontinuacion se hace una extencion para el uso de base de datos distribuidas para ser actualizada desta base de datos
        startService(new Intent(context, ControlTotalLocalxRemoto.class));

        BDVarGlo.setDatosUsuario(context);
        BDVarGlo.setVarGlo(context, "PRUEBA_DATO_TECPRO_ID", BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
        BDVarGlo.setDatosUsuario(context);
        if ("286".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID")) || "702".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"))) {
            startActivity(new Intent(context, VistaMLeme.class));
        } else if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO").equals("comercial")) {
            startActivity(new Intent(context, MenuInicial.class));
            //
        }else if (BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO").equals("noTecnico")){
            startActivity(new Intent(context, MenuInicialNoTecnicos.class));
        }

        else {
            startActivity(new Intent(context, MenuInicial.class));
        }
    }

    private void actualizarApp() {
        metodo = "actualizarApp()";
        MensajeEnConsola.log(context, metodo, "");
        final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(context);
        alertaSimple.setTitle(getString(R.string.titActDisp));
        alertaSimple.setMessage(getString(R.string.cuerpoActDisp));
        alertaSimple.setPositiveButton(getString(R.string.descargar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {



                MensajeEnConsola.log(context, metodo, "Se inicia la descarga");
                AppDownload.descargar(context, BDVarGlo.getVarGlo(context, "INFO_APP_SERVIDOR_VERSION"));
                //Log.e("nombre apk ", BDVarGlo.getVarGlo(context, "INFO_APP_SERVIDOR_VERSION"));
                MensajeEnConsola.log(context,metodo,"Nombre de .apk: "+ BDVarGlo.getVarGlo(context, "INFO_APP_SERVIDOR_VERSION"));
                //enviarDatosAActividad();



            }
        });


        if (BDVarGlo.getVarGlo(context, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")) {
            alertaSimple.setNegativeButton("No, mas tarde",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            enviarDatosAActividad();
                        }
                    }
            );
        }
        alertaSimple.setIcon(R.drawable.warning);
        alertaSimple.create();
        alertaSimple.show();
    }




    @Override
    protected void onResume() {
        super.onResume();
      //  IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
         registerReceiver(downloadReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
       unregisterReceiver(downloadReceiver);


    }




    public static long id;
    public static String metodo;
    ArrayList<Long> list = new ArrayList<>();
    public static DownloadManager downloadManager;


    BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        //gestionamos la finalización de la descarga
        @Override
        public void onReceive(Context context, Intent intent) {
            metodo = "AppDownload.BroadcastReceiver()";
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id, 0);
            Cursor cursor = downloadManager.query(query);

            Toast.makeText(context, "ENTRO AL BROADCAST", Toast.LENGTH_LONG).show();

            if (cursor.moveToFirst())
            {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));


                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    ParcelFileDescriptor file = null;
                    try {
                        file = downloadManager.openDownloadedFile(id);
                        Toast.makeText(context, "Actualización " + BDVarGlo.getVarGlo(context, "INFO_APP_SERVIDOR_VERSION") + " listo!! ", Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException ex) {
                        Toast.makeText(context, "Exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Error por : " + reason, Toast.LENGTH_LONG).show();
                }
            }
            cursor.close();
        }
    };




///storage/emulated/0/ZITRO/2.4.2.apk


    //se ejecuta al terminar LA DESCARGA.
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if ( MainActivity.id == id) {
                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
                // "/storage/emulated/0/ZITRO/2.4.2.apk"
                // se lanza la instalacion

                /*
                File toInstall = new File("/storage/emulated/0/ZITRO/", BDVarGlo.getVarGlo(context, "INFO_APP_SERVIDOR_VERSION") + ".apk");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //Uri apkUri = FileProvider.getUriForFile(context, "com.example.devolucionmaterial.provider", toInstall);
                    Uri apkUri = FileProvider.getUriForFile(context,"com.example.devolucionmaterial.provider", toInstall);
                    Intent intent2 = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent2.setData(apkUri);
                    intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent2);

                } else {
                    Uri apkUri = Uri.fromFile(toInstall);
                    Intent intent3 = new Intent(Intent.ACTION_VIEW);
                    intent3.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent3);
                }
                */


                String strApkToInstall = BDVarGlo.getVarGlo(context, "INFO_APP_SERVIDOR_VERSION");
                File fileApkToInstall = new File("/storage/emulated/0/ZITRO/", strApkToInstall);

                Log.i("*************","*** filepath:"+fileApkToInstall.toString());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    Uri apkUri = FileProvider.getUriForFile(MainActivity.this, "com.example.devolucionmaterial.provider", fileApkToInstall);
                    Intent intent2 = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent2.setData(apkUri);
                    intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    MainActivity.this.startActivity(intent2);
                } else {
                    Uri apkUri = Uri.fromFile(fileApkToInstall);
                    Intent intent3 = new Intent(Intent.ACTION_VIEW);
                    intent3.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.this.startActivity(intent3);
                }

            }
        }
    };




}

//453