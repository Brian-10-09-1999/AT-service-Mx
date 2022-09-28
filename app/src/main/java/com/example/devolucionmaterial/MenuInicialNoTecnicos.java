package com.example.devolucionmaterial;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devolucionmaterial.activitys.viaticos.Activity_Lista;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.internet.MuestraAyuda;

public class MenuInicialNoTecnicos extends AppCompatActivity {


    private Button btnGoViaticos,btnAyuda;
    private TextView tvUser,tvArea,tvMail;
    protected Toolbar toolbar; private  boolean enabledMenu;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial_no_tecnicos);

        btnGoViaticos=(Button) findViewById(R.id.but_viaticos);
        btnAyuda=(Button) findViewById(R.id.but_ayuda_viaticos);
        tvArea=(TextView) findViewById(R.id.tv_area);
        tvUser=(TextView) findViewById(R.id.tv_user);
        tvMail=(TextView) findViewById(R.id.tv_mail_viaticos);
        context = this;
        tvUser.setText( BDVarGlo.getVarGlo(this, "INFO_USUARIO_NOMBRE_COMPLETO"));
        tvArea.setText( BDVarGlo.getVarGlo(this, "INFO_USUARIO_TIPO"));
        tvMail.setText(BDVarGlo.getVarGlo(this, "INFO_USUARIO_EMAIL"));

        initToolbar(" Menu Principal", true, true);



        final AnimationDrawable drawable = new AnimationDrawable();
        final Handler handler = new Handler();


        drawable.addFrame(getResources().getDrawable(R.drawable.t1),450);
        drawable.addFrame(getResources().getDrawable(R.drawable.t2),200);
        drawable.addFrame(getResources().getDrawable(R.drawable.t3),200);
        drawable.addFrame(getResources().getDrawable(R.drawable.t4),200);
        drawable.addFrame(getResources().getDrawable(R.drawable.t5),200);
        drawable.addFrame(getResources().getDrawable(R.drawable.t6),200);
        drawable.addFrame(getResources().getDrawable(R.drawable.t7),200);
        drawable.addFrame(getResources().getDrawable(R.drawable.t8),200);
        drawable.addFrame(getResources().getDrawable(R.drawable.t9),200);
        drawable.addFrame(getResources().getDrawable(R.drawable.t1),450);


        drawable.setOneShot(false);
        drawable.setDither(true);




        btnGoViaticos.setBackgroundDrawable(drawable);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawable.start();
            }
        }, 100);




        btnGoViaticos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                vibra();
                startActivity(new Intent(MenuInicialNoTecnicos.this, Activity_Lista.class));
            }
        });

        btnAyuda.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
               // startActivity(new Intent(context, MuestraAyuda.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
             //   metodo = "crearDialogoListaAlmacen()";
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
        });

    }


    protected void initToolbar(String title, Boolean homeAsUpEnabled, Boolean enabledMenu) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.icon_menu_notecnicos);
        this.enabledMenu=enabledMenu;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            if (homeAsUpEnabled)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
    }

    private void vibra(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            //deprecated in API 26
            v.vibrate(100);
    }


    private void toastAlert(String ms){
        Toast.makeText(getApplicationContext(),ms, Toast.LENGTH_LONG).show();
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





}
