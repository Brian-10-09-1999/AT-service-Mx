package com.example.devolucionmaterial.activitys;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.MenuInicialTecnico;
import com.example.devolucionmaterial.PermissionActivity;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;

/**
 * Created by EDGAR ARANA on 04/12/2017.
 */

public abstract class BaseActivity extends PermissionActivity {
    protected Menu mMenu;
    protected Context context;
    protected BDmanager manager;
    protected MaterialDialog pDialog;
    protected Toolbar toolbar;
    private  boolean enabledMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        context = this;
        manager = new BDmanager(context);
    }



    /**
     * @param title           Is the name that goes on the toolbar
     * @param homeAsUpEnabled If the activity needs to activate the return flake
     * @param enabledMenu      if the activity needs a menu in toolbar
     */
   protected void initToolbar(String title, Boolean homeAsUpEnabled, Boolean enabledMenu) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.enabledMenu=enabledMenu;
        setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                if (homeAsUpEnabled)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                getSupportActionBar().setTitle(title);
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(enabledMenu){
            getMenuInflater().inflate(R.menu.menu_reporte_devolucon, menu);
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

            }

            mMenu = menu;
            actualizaEstatusTecnico();
        }
        return true;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_est_tec) {
            MenuInicialTecnico.iniciaMenuEstatusTec(context);

            return true;
        }
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }


        return super.onOptionsItemSelected(item);
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
