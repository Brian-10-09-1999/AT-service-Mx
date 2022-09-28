package com.example.devolucionmaterial.activitys;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ViewFlipper;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.MenuInicialTecnico;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.checks.Device;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSON;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.MenuOpciones;
import com.thanosfisherman.mayi.PermissionBean;

import org.json.JSONObject;

public class VistaMLeme extends BaseActivity implements OnItemSelectedListener {
    String metodo;
    BDmanager manager;
    private Spinner spnTecnico;
    protected ArrayAdapter<CharSequence> adapter;
    private ViewFlipper vfZitro;
    private TextView tvSupervisor;
    private ImageView imgEmail, imgCallCC, imgInfo, imgScroll;
    private MenuOpciones mo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_mleme);
        initToolbar("Menú de Supervisor", false, false);
        context = this;
        manager = new BDmanager(context);
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
        // TODO: 26/06/2017 comentario tardado sirve para agragar las regiones por la modificaciones de login de españa
        manager.borrarRegion();
        if ("SSS".equals(manager.consulta("SELECT nombre FROM cregion WHERE id = 1", "SSS"))) {
            manager.actualiza("INSERT INTO cregion (id, nombre) VALUES (1,'CENTRO'),(2,'DFZ1'),(59,'DFZ2'),(4,'GUADALAJARA'),(" +
                    "5,'MONTERREY'),(6,'PACIFICO'),(60,'PENINSULA'),(3,'PUEBLA / VERACRUZ'),(7,'TIJUANA')");
        }

        mo = new MenuOpciones();
        imgEmail = (ImageView) findViewById(R.id.imgCorreovml);
        imgCallCC = (ImageView) findViewById(R.id.imgLlamarvml);
        imgInfo = (ImageView) findViewById(R.id.imgInfovml);
        imgScroll = (ImageView) findViewById(R.id.scrolls);
        tvSupervisor = (TextView) findViewById(R.id.tvUsuarioSup);
        spnTecnico = (Spinner) findViewById(R.id.spnTecnico);
        spnTecnico.setSelected(false);
        adapter = ArrayAdapter.createFromResource(this, R.array.Tecnicos, android.R.layout.simple_spinner_item);
        //Asignas el layout a inflar para cada elemento al momento de desplegar la lista
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Seteas el adaptador
        spnTecnico.setAdapter(adapter);
        spnTecnico.setSelection(0);
        spnTecnico.setOnItemSelectedListener(this);
        vfZitro = (ViewFlipper) findViewById(R.id.vfZitro);
        vfZitro.setFlipInterval(2000);
        vfZitro.startFlipping();
        tvSupervisor.setText("¡Bienvenido " + BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE") + "!");
        tvSupervisor.setTextSize(15);

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

        imgScroll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { mo.scrollDynamicInit(context);}
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String region, estado, ciudad;

        switch (position) {
            case 1:
                //   <item>Gervacio Cerón     - PUEBLA/VERACRUZ</item>
                JSONCargaInfoUsuarios("gceron", "10437");
                break;
            case 2:
                //  <item>Miguel Campos      - MONTERREY</item>
                JSONCargaInfoUsuarios("mcampos", "10000");
                break;
            case 3:
                //  <item>Nayeli Malagón     - PENINSULA</item>
                JSONCargaInfoUsuarios("nmalagon", "10554");
                break;
            case 4:
                // <item>Herli Castañeda    - DFZ1</item>
                JSONCargaInfoUsuarios("hcastaneda", "10101");
                break;
            case 5:
                // <item>Guillermo López    - CENTRO</item>
                JSONCargaInfoUsuarios("glopez", "10438");
                break;
            case 6:
                //  <item>Cecilia Navarro    - GUADALAJARA</item>
                JSONCargaInfoUsuarios("Bmartell", "10684");
                break;
            case 7:
                // <item>Juan Luis Vera     - DFZ2</item>
                JSONCargaInfoUsuarios("Jvera", "10502");
                break;
            case 8:
                // <item>Juan Mancilla      - PACIFICO</item>
                JSONCargaInfoUsuarios("jmancilla", "10578");
                break;
            case 9:
                // <item>Alfonso Vargas     - TIJUANA</item>
                JSONCargaInfoUsuarios("avargas", "10045");
                break;
        }
    }

    void JSONCargaInfoUsuarios(String alias, String pass) {
        String url = BDVarGlo.getVarGlo(context, "FUNCTION_CARGAR_INFO_USUARIO") + "&email=&telefono=&alias=" + alias + "&password=" + pass;
        if (CheckService.internet(context)) {
            new CargaInfoUsuario().execute(url);
        } else {
            Alert.ActivaInternet(context);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }



    class CargaInfoUsuario extends AsyncTask<String, Void, Void> {

        private ProgressDialog pDialog;


        protected void onPreExecute() {
           /* pDialog= new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.Conectando_con_servidor_remoto))
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();*/

            pDialog = new ProgressDialog(VistaMLeme.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);

            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);

        }

        protected Void doInBackground(String... JSONurl) {
            metodo = "CargaInfoUsuario()";

            try {
                JSONObject object = JSON.load(context, metodo, JSONurl[0]);
                assert object != null;

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
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            metodo = "ProgresoPass<AsyncTask>onPostExecute";
            pDialog.dismiss();
            if (!"0".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"))) {
                alertaTecnicoInfo("", "", "");
            } else {
                Alert.Alerta(context, metodo, 2, getResources().getString(R.string.usuarioIncorrecto));
            }
        }
    }


    public void alertaTecnicoInfo(String region, String estado, String ciudad) {
        BDVarGlo.setVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO", "tecnico");
        final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(VistaMLeme.this);
        alertaSimple.setTitle("Información de Técnico");
        alertaSimple.setMessage("Nombre: " + BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE") + "\n" +
                "Región: " + NOMBREregion(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION")) + "\n" +
                "Estado: " + NOMBREregion(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION")) + "\n" +
                "\n\n¿Deseas continuar con este perfil?");
        alertaSimple.setPositiveButton("Continuar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //startActivity(new Intent(context, MenuInicialTecnico.class));
                        startActivity(new Intent(context, MenuInicial.class));
                    }
                });
        alertaSimple.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertaSimple.setIcon(R.drawable.question);
        alertaSimple.create();
        alertaSimple.show();
    }

    private String NOMBREregion(String id_region) {
        return manager.consulta("SELECT nombre FROM cregion WHERE id = " + id_region + "", id_region);
    }

    public void onRestart() {
        super.onRestart();
        spnTecnico.setSelection(0);
    }



}
//175