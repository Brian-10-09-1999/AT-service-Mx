package com.example.devolucionmaterial;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.MenuOpciones;

public class AdeudoDeMaterial extends Activity {
    String metodo;
    Context context;
    private ListView lvAdeudos;
    private MenuOpciones mo;
    private String usuarioID;
    private ArrayAdapter<Adeudo> adaptador;
    private ProgressDialog pDialog;
    private Menu mMenu;
    private BDmanager manager;
    private static String docPHPAdeudoPiezas = "AdeudodePiezas.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adeudo_de_material);
        context = this;
        setupAdeudoDeMaterial();
    }

    private void setupAdeudoDeMaterial() {
        metodo = "setupAdeudoDeMaterial()";
        manager = new BDmanager(this);
        lvAdeudos = (ListView) findViewById(R.id.lvAdeudoMaterial);
        TextView tvUsuario = (TextView) findViewById(R.id.tvUsuarioAdeudo);
        ImageView imgEmail = (ImageView) findViewById(R.id.imgCorreoam);
        ImageView imgCallCC = (ImageView) findViewById(R.id.imgLlamaram);
        ImageView imgInfo = (ImageView) findViewById(R.id.imgInfoam);
        mo = new MenuOpciones();

        Intent intReceptor = this.getIntent();
        Bundle bndReceptor = intReceptor.getExtras();
        usuarioID = bndReceptor.getString("usuarioidx");
        String nombreDeUsuario = bndReceptor.getString("nombreusuariox");
        tvUsuario.setText(nombreDeUsuario);

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
        new Progreso().execute();
    }

    private void pedirAdeudoDeMaterial() {
        metodo = "pedirAdeudoDeMaterial()";
        List<Adeudo> listaDeAdeudo = new ArrayList<Adeudo>();
        try {
            MensajeEnConsola.log(context, metodo, MainActivity.url + docPHPAdeudoPiezas + "tecnicoidx=" + usuarioID);
            JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPAdeudoPiezas + "tecnicoidx=" + usuarioID);
            assert object != null;
            JSONArray jsonArray = object.optJSONArray("adeudox");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                if (!jsonArrayChild.optString("cantidadx").equals("0")) {
                    listaDeAdeudo.add(new Adeudo(jsonArrayChild.optString("nombre"), jsonArrayChild.optString("cantidadx")));
                }
            }
            adaptador = new AdeudoArrayAdapter(context, listaDeAdeudo);
            if (listaDeAdeudo.size() == 0) {
                Alert.Error(context, metodo, getString(R.string.sinRegistros), getString(R.string.notMatPen));
            } else {
                Adeudo item = listaDeAdeudo.get(0);
                if (item.getNombre().equals("0")) {
                    Alert.Error(context, metodo, getString(R.string.sinRegistros), getString(R.string.notMatPen));
                    lvAdeudos.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            Alert.Error(context, metodo, getString(R.string.titJsonException), e.getMessage()
                    + getString(R.string.cuerpoJsonException));
        } catch (Exception e) {
            Alert.Error(context, metodo, "Error ADM001", e.getMessage());
        }
    }

    private class Progreso extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(AdeudoDeMaterial.this);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage(getString(R.string.conectando));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            pedirAdeudoDeMaterial();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            lvAdeudos.setAdapter(adaptador);
            pDialog.dismiss();
        }
    }

    private class Adeudo {
        private String nombre, cantidad;

        public Adeudo(String nombre, String cantidad) {
            this.nombre = nombre;
            this.cantidad = cantidad;
        }

        public String getNombre() {
            return nombre;
        }

        public String getCantidad() {
            return cantidad;
        }
    }

    public class AdeudoArrayAdapter extends ArrayAdapter<Adeudo> {
        public AdeudoArrayAdapter(Context context, List<Adeudo> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Obteniendo una instancia del inflater
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Salvando la referencia del View de la fila
            View listItemView = convertView;

            //Comprobando si el View no existe
            if (null == convertView) {
                //Si no existe, entonces inflarlo con two_line_list_item.xml
                listItemView = inflater.inflate(R.layout.item_lista_adeudo_de_material, parent, false);
            }

            //Obteniendo instancias de los text views
            TextView subfamila = (TextView) listItemView.findViewById(R.id.tvSubfamilia);
            TextView numero = (TextView) listItemView.findViewById(R.id.tvNoAdeudo);


            //Obteniendo instancia de la Tarea en la posicion actual
            Adeudo item = getItem(position);

            if (!item.getCantidad().equals("0")) {
                subfamila.setText(item.getNombre() + ":");
                numero.setText(item.getCantidad());
            }

            //Devolver al ListView la fila creada
            return listItemView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_est_tec);
        Cursor cursorEstatusTec = manager.cargarCursorEstatusTec();
        cursorEstatusTec.moveToFirst();

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
            item.setIcon(R.drawable.est_mix);
        }
        if (cursorEstatusTec.getString(1).equals("81")) {
            item.setIcon(R.drawable.est_vacaciones);
        }
        if (cursorEstatusTec.getString(1).equals("89")) {
            item.setIcon(R.drawable.est_incapacidad);
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
            MenuInicialTecnico.iniciaMenuEstatusTec(AdeudoDeMaterial.this);
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
}
//294