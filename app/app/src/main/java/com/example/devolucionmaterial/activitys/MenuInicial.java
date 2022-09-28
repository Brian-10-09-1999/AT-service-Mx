package com.example.devolucionmaterial.activitys;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.Token;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.model.Contact;
import com.example.devolucionmaterial.chat.task.ContactChat;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;
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
import com.example.devolucionmaterial.internet.MuestraAyuda;

import com.example.devolucionmaterial.services.Bloqueo;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.sharedpreferences.PreferencesVar;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

import java.util.List;
import java.util.logging.Handler;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuInicial extends Activity implements Callback<ResponseCall> {
    String metodo;
    Context context;

    DrawerLayout drawerLayout;
    TextView tv_nombre, tv_email;
    LinearLayout ly_menu_cambia_estatus, ly_menu_sincroniza, ly_menu_ayuda;
    private DBChatManager dbChatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial);
        context = this;
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
        FragmentHead fragmentHead = new FragmentHead();
        FragmentManager fragmentManagerHead = getFragmentManager();
        FragmentTransaction fragmentTransactionHead = fragmentManagerHead.beginTransaction();
        fragmentTransactionHead.add(R.id.id_act_menu_init_ly_head, fragmentHead).commit();

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
                startActivity(new Intent(context, MuestraAyuda.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentHead.ActualizaImagenEstatus();
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
}
//142