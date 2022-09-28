package com.example.devolucionmaterial.activitys;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.activitys.materialesSeccion.Menu_Devolucion;
import com.example.devolucionmaterial.activitys.materialesSeccion.ReporteDeDevoluciones1;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.Token;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.model.Contact;
import com.example.devolucionmaterial.chat.task.ContactChat;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;
import com.example.devolucionmaterial.data_base.ActualizaBDestatusDevMaterial;
import com.example.devolucionmaterial.dialogs.Estatus;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.data_base.ActualizaBDcestatus;
import com.example.devolucionmaterial.data_base.ActualizaBDcrefacciones;
import com.example.devolucionmaterial.data_base.ActualizaBDcsala;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.firebase.ConfigToken;
import com.example.devolucionmaterial.fragments.FragmentBody;
import com.example.devolucionmaterial.fragments.FragmentFoot;
import com.example.devolucionmaterial.fragments.FragmentHead;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.internet.MuestraAyuda;

import com.example.devolucionmaterial.services.Bloqueo;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.sharedpreferences.PreferencesVar;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.libraryvideo.subtitle.CaptionsView;
import com.odn.multiphotopicker.utils.PhotoPickerIntent;
import com.thanosfisherman.mayi.PermissionBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.logging.Handler;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

public class MenuInicial extends BaseActivity implements Callback<ResponseCall> {
    String metodo;
    Context context;

    DrawerLayout drawerLayout;
    TextView tv_nombre, tv_email;
    LinearLayout ly_menu_cambia_estatus, ly_menu_sincroniza, ly_menu_ayuda, ly_menu_ayuda_manual;
    private DBChatManager dbChatManager;
    ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial);
        initToolbar("Menú Inicial", false, true);
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
        dbChatManager = new DBChatManager(context);
        dbChatManager.open();
        // TODO: 30/03/2017 se sincroniza contactos del chat
        if (dbChatManager.getContactList().isEmpty()) {
            getContactChat();
        }

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateToken();
            }
        }, 5000);

        Alert.Anuncio(context);
        setupFragmentos();
        setupMenuLateral();
        //ponemos un dialogo de cargando tablas como medida de bloqueo
        Bloqueo.BDUpdates(context);
    }


    void recoverChats() {
        RequestBody id_user = RequestBody.create(MediaType.parse("text/plain"), BDVarGlo.getVarGlo(getApplicationContext(), "INFO_USUARIO_ID"));
        com.example.devolucionmaterial.chat.api.ServiceApi serviceApi
                = com.example.devolucionmaterial.chat.api.ServiceApi.retrofit.create(com.example.devolucionmaterial.chat.api.ServiceApi.class);
        Call<ResponseCall> call = serviceApi.recoverChats(id_user);
        call.enqueue(this);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void setupFragmentos() {
       /* FragmentHead fragmentHead = new FragmentHead();
        FragmentManager fragmentManagerHead = getFragmentManager();
        FragmentTransaction fragmentTransactionHead = fragmentManagerHead.beginTransaction();
        fragmentTransactionHead.add(R.id.id_act_menu_init_ly_head, fragmentHead).commit();*/
        new ConectWithServerForStatus().execute();
        FragmentBody fragmentBody = new FragmentBody();
        FragmentManager fragmentManagerBody = getFragmentManager();
        FragmentTransaction fragmentTransactionBody = fragmentManagerBody.beginTransaction();
        fragmentTransactionBody.add(R.id.id_act_menu_init_ly_body, fragmentBody).commit();

        FragmentFoot fragmentFoot = new FragmentFoot();
        FragmentManager fragmentManagerFoot = getFragmentManager();
        FragmentTransaction fragmentTransactionFoot = fragmentManagerFoot.beginTransaction();
        fragmentTransactionFoot.add(R.id.id_act_menu_init_ly_foot, fragmentFoot).commit();
    }

    void setupMenuLateral() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        tv_nombre = (TextView) findViewById(R.id.id_nav_head_tv_username);
        tv_email = (TextView) findViewById(R.id.id_nav_head_tv_email);
        ly_menu_cambia_estatus = (LinearLayout) findViewById(R.id.nav_menu_id_ly_menu_cambia_estatus);
        ly_menu_sincroniza = (LinearLayout) findViewById(R.id.nav_menu_id_ly_menu_sincroniza_tablas);


        ly_menu_ayuda = (LinearLayout) findViewById(R.id.nav_menu_id_ly_menu_aydua);
        ly_menu_ayuda_manual=(LinearLayout) findViewById(R.id.nav_menu_id_ly_menu_manualviaticos);


        mToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                //getSupportActionBar().setTitle(R.string.navigation_drawer_close);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                // getSupportActionBar().setTitle(R.string.navigation_drawer_open);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.syncState();
        drawerLayout.addDrawerListener(mToggle);

        tv_nombre.setText(BDVarGlo.getVarGlo(context, "INFO_USUARIO_NOMBRE_COMPLETO"));
        tv_email.setText(BDVarGlo.getVarGlo(context, "INFO_USUARIO_EMAIL"));
        tv_nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Tocaste NOMBRE", Toast.LENGTH_SHORT).show();
            }
        });
        tv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Tocaste EMAIL", Toast.LENGTH_SHORT).show();
            }
        });
        ly_menu_cambia_estatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Estatus.class));
            }
        });
        ly_menu_sincroniza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckService.internet(context)) {
                    startService(new Intent(context, ActualizaBDcrefacciones.class));
                    startService(new Intent(context, ActualizaBDcestatus.class));
                    startService(new Intent(context, ActualizaBDcsala.class));
                    Toast.makeText(context, "Tablas sincronizadas", Toast.LENGTH_SHORT).show();
                } else {
                    Alert.ActivaInternet(context);
                }
            }
        });




        ly_menu_ayuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(context, MuestraAyuda.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                Intent vi = new Intent(getApplicationContext(), MuestraAyuda.class);
                vi.putExtra("op",1);
                startActivity(vi);
            }
        });



        ly_menu_ayuda_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clickAyudaManual();

               // startActivity(new Intent(context, MuestraAyuda.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

    }



/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return !drawerLayout.isDrawerOpen(GravityCompat.START) || super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        actualizaEstatusTecnico();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
       /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }*/
        actualizaEstatusTecnico();
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    void updateToken() {
        // TODO: 17/04/2017 se pone las preferencia del token en lo que se ve lo de catalogo nuevo
        final PrefrerenceChat prefrerenceChat = new PrefrerenceChat(getApplicationContext());
        prefrerenceChat.setTokenchat(Integer.parseInt(BDVarGlo.getVarGlo(getApplicationContext(), "INFO_USUARIO_ID")));

        // TODO: 14/12/2016  con el shared preference se recuper el token
        SharedPreferences recuperarToken = getSharedPreferences(ConfigToken.SHARED_PREF, MODE_PRIVATE);
        String tokenRecuperado = recuperarToken.getString("regId", "");
        Log.e("token_id", tokenRecuperado);

        if (prefrerenceChat.getValidationInit() == 0) {
            if (CheckService.internet(context)) {
                ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
                Call<Token> call =
                        serviceApi.ACTUALIZAR_TOKEN(
                                BDVarGlo.getVarGlo(getApplicationContext(), "INFO_USUARIO_ID"),
                                tokenRecuperado);
                call.enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {

                        try {
                            if (response.body().getValue() == 1) {
                                // TODO: 17/04/2017 ya que se actuliza se mada 1 que no vuelva a entrar  
                                prefrerenceChat.setValidationInit(1);
                                // TODO: 17/04/2017 se invoca solo una vez este metodo manda le id de usuario para recuperar chat de grupo que ya estaban creados
                                recoverChats();
                                Log.e("Bien", "si esta actualizando");
                                Log.e("token de regerso", response.body().getToken());
                            }
                        } catch (Exception e) {
                            //  Toast.makeText(context, getString(R.string.vuelve_a_intertarlo), Toast.LENGTH_SHORT).show();
                            Log.e("error token", String.valueOf(e));
                        }


                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        Log.e("error token", String.valueOf(t));
                    }
                });
            }

        }


    }

    private void getContactChat() {
        com.example.devolucionmaterial.chat.api.ServiceApi serviceApi = com.example.devolucionmaterial.chat.api.ServiceApi.retrofit.create(com.example.devolucionmaterial.chat.api.ServiceApi.class);
        Call<List<Contact>> call = serviceApi.OBTENER_CONTACTOS_CHAT();
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                Log.e("url", String.valueOf(call.request().url()));

                try {
                    ContactChat contactChat = new ContactChat(MenuInicial.this, response.body());
                    contactChat.execute();
                } catch (Exception e) {
                    Log.e("execpytion", String.valueOf(e));
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.e("onFailure", String.valueOf(t));
            }
        });

    }


    @Override
    public void onResponse(Call<ResponseCall> call, Response<ResponseCall> response) {
        Log.e("url", String.valueOf(call.request().url()));
        try {

            if (response.body().getValue().equals("1")) {
                Log.e("response", response.body().getResult());
            } else {

            }
        } catch (Exception e) {
            Log.e("catch error", String.valueOf(e));
        }
    }

    @Override
    public void onFailure(Call<ResponseCall> call, Throwable t) {
        Log.e("url", String.valueOf(call.request().url()));
        Log.e("error  recuperar ", String.valueOf(t));
    }

    private class ConectWithServerForStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String docPHPMenuProFirma = "MenuProFirma.php?";
            String docPHPMenuTecFirma = "MenuTecFirma.php?";
            try {
                JSONObject object = null;
                if ("comercial".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
                    Log.e("url firma", MainActivity.url + docPHPMenuProFirma + "promotoraidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
                    object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPMenuProFirma + "promotoraidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
                }
                if ("tecnico".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
                    object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPMenuTecFirma + "tecnicoidx=" + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
                }
                assert object != null;
                JSONArray jsonArray = object.optJSONArray("tipoMenu");
                JSONObject jsonArrayChild = jsonArray.getJSONObject(0);
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", jsonArrayChild.optString("estatusidx"));
                BDVarGlo.setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", jsonArrayChild.optString("estEnServx"));
            } catch (Exception ignore) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            actualizaEstatusTecnico();
        }
    }


    private String tipoRed() {
        String d = "";

        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    d = "wifi";
                    return d;
                }
            }
        }

        ConnectivityManager connectivity2 = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity2 != null) {
            NetworkInfo info = connectivity2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    d = "datos";
                    return d;
                }
            }
        }


         /*  //este bloque posiblemnet permitiria la conexion a internet por bluetooth
        ConnectivityManager connectivity3 = (ConnectivityManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);
            if (info != null) {
                if (info.isConnected()) {
                    d = "blue";
                    return d;
                }
            }
        }*/

        d = "sin internet";
        return d;
    }




    private void clickAyudaManual(){


        metodo = "crearDialogoListaAlmacen()";
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final CharSequence[] items = new CharSequence[2];
        items[0] = "Video";
        items[1] = "Manual PDF";
	    /*final CharSequence[] items = new CharSequence[3];
	    items[0] = getString(R.string.devMat);
	    items[1] = getString(R.string.adeudoMaterial);
	    items[2] = getString(R.string.repdev);*/

        builder.setTitle(getString(R.string.seleccionaOpcion))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent vi = new Intent(getApplicationContext(), MuestraAyuda.class);

                        switch (which) {
                            case 0:

                                if(tipoRed().equals("wifi")){
                                    vi.putExtra("op",3);
                                    startActivity(vi);

                                }
                                else{
                                    toastAlert("Esta opción requiere conexión WIFI");
                                }

                                break;
                            case 1:

                                vi.putExtra("op",2);
                                startActivity(vi);


                                break;
                        }
                    }
                });

        builder.create().show();

    }





    private void toastAlert(String ms){
        Toast.makeText(getApplicationContext(),ms, Toast.LENGTH_LONG).show();
    }



}
