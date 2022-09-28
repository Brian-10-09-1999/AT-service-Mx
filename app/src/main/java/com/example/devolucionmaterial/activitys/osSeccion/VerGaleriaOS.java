package com.example.devolucionmaterial.activitys.osSeccion;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.MuestraSoloImagen;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.squareup.picasso.Picasso;
import com.thanosfisherman.mayi.PermissionBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VerGaleriaOS extends BaseActivity {
    String metodo;
    Context context;
    ListView lvListaImagenesOS;
    private List<url_imagenes> listaImagenesOS;
    private ArrayAdapter<url_imagenes> adaptador;
    static String docPHPListaGaleriaOS = "ListaGaleriaOS.php?", folio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_ver_galeria_img_os);


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
     /*   if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }*/

    }

    private void initSetUp() {
        Intent intReceptor = this.getIntent();
        Bundle bndReceptor = intReceptor.getExtras();
        folio = bndReceptor.getString("folio");
        initToolbar("Galeria de OS " + folio, true, true);
        //setTitle("Galeria de OS " + folio);
        listaImagenesOS = new ArrayList<url_imagenes>();
        lvListaImagenesOS = (ListView) findViewById(R.id.listaVergaLeria);
        lvListaImagenesOS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View vista, int posicion, long id) {
                metodo = "selectedItem.OnItemClickListener()";
                ImageView imagen1 = (ImageView) vista.findViewById(R.id.imgItemVG1);
                ImageView imagen2 = (ImageView) vista.findViewById(R.id.imgItemVG2);
                final url_imagenes item = (url_imagenes) listaImagenesOS.get(posicion);
                imagen1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //MensajeEnConsola.log(context, metodo, "Muestra solo imagen = "+item.getUrl1());
                        MuestraImagen(item.getUrl1());
                    }
                });
                imagen2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //MensajeEnConsola.log(context, metodo, "Muestra solo imagen = "+item.getUrl2());
                        MuestraImagen(item.getUrl2());
                    }
                });
            }
        });
        String[] temp = MainActivity.url.split("Android/");
        String dominio = temp[0];
        new ConsultaListaImagenesOS().execute(MainActivity.url + docPHPListaGaleriaOS + "folioOS=" + folio + "&dominio=" + dominio);
    }

    private void MuestraImagen(String url) {
        Intent intent = new Intent(context, MuestraSoloImagen.class);
        Bundle bndMuestraImagen = new Bundle();
        bndMuestraImagen.putString("imagen", url);
        intent.putExtras(bndMuestraImagen);
        startActivity(intent);
    }

    private class ConsultaListaImagenesOS extends AsyncTask<String, String[], String[]> {
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
        protected String[] doInBackground(String... params) {
            metodo = "ConsultaListaImagenesOS<>.doInBackground()";
            String[] regresa = null;
            try {
                JSONObject object = JSONparse.consultaURL(context, metodo, params[0]);
                assert object != null;
                //MensajeEnConsola.log(context, metodo, "EXITOSO = "+object.getString("exitoso")+"\nGALERIA = "+object.getJSONArray("galeria"));
                if ("SI".equals(object.getString("exitoso"))) {
                    JSONArray array = object.getJSONArray("galeria");
                    regresa = new String[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object1 = array.getJSONObject(i);
                        regresa[i] = object1.getString("imagen_url");
                        //MensajeEnConsola.log(context, metodo, "\nIMAGEN URL = "+regresa[i]);
                    }
                }
            } catch (JSONException e) {
                MensajeEnConsola.log(context, metodo, "JSONException e = " + e.getMessage());
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
            }
            return regresa;
        }

        @Override
        protected void onPostExecute(String[] result) {
            metodo = "ConsultaListaImagenesOS<>.onPostExecute()";
            super.onPostExecute(result);
            pDialog.dismiss();
            //aqui cargamos la galeria y las mostramos
            listaImagenesOS.clear();
            try {
                if (result.length > 0 && !"".equals(result)) {
                    int temp;
                    for (int i = 0; i < result.length; i = i + 2) {
                        temp = i + 1;
                        //MensajeEnConsola.log(context, metodo, "url imagen 1 = "+result[i]);
                        //MensajeEnConsola.log(context, metodo, "url imagen 2 = "+result[temp]);
                        try {
                            if (temp < result.length) {
                                listaImagenesOS.add(new url_imagenes(result[i], result[temp]));
                            } else {
                                listaImagenesOS.add(new url_imagenes(result[i], ""));
                            }
                        } catch (Exception e) {
                            MensajeEnConsola.log(context, metodo, "Exception con valor e = " + e.getMessage());
                        }
                    }
                    adaptador = new ArregloUrlImagenesVerGaleria(context, listaImagenesOS);
                    lvListaImagenesOS.setAdapter(adaptador);
                    lvListaImagenesOS.setSelection(lvListaImagenesOS.getAdapter().getCount() - 1);
                } else {
                    Alert.Alerta(context, metodo, 1, "No hay imagenes para esta OS");
                    VerGaleriaOS.this.finish();
                }
            } catch (Exception e) {
                Alert.Alerta(context, metodo, 1, "No hay imagenes de esta OS");
                MensajeEnConsola.log(context, metodo, "Error exception valor e = " + e.getMessage());
                VerGaleriaOS.this.finish();
            }
        }
    }

    private class ViewHolderItem {
        ImageView imagen1;
        ImageView imagen2;
    }

    private class url_imagenes {
        String url1;
        String url2;

        public url_imagenes(String url1, String url2) {
            this.url1 = url1;
            this.url2 = url2;
        }

        public String getUrl1() {
            return url1;
        }

        public String getUrl2() {
            return url2;
        }
    }

    public class ArregloUrlImagenesVerGaleria extends ArrayAdapter<url_imagenes> {
        public ArregloUrlImagenesVerGaleria(Context context, List<url_imagenes> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderItem viewHolder;
            View listItemView = convertView;
            if (null == convertView) {
                LayoutInflater inflater = (LayoutInflater) ((Activity) getContext()).getLayoutInflater();
                listItemView = inflater.inflate(R.layout.item_lista_galeria_img_os, parent, false);

                viewHolder = new ViewHolderItem();
                viewHolder.imagen1 = (ImageView) listItemView.findViewById(R.id.imgItemVG1);
                viewHolder.imagen2 = (ImageView) listItemView.findViewById(R.id.imgItemVG2);
                // store the holder with the view.
                listItemView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderItem) convertView.getTag();
            }

            url_imagenes item = (url_imagenes) getItem(position);
            if (item != null) {
                Picasso.with(context).load(item.getUrl1()).placeholder(android.R.drawable.stat_sys_download).error(android.R.drawable.stat_notify_error).into(viewHolder.imagen1);
                if (item.getUrl2().equals("")) {
                    Picasso.with(context).load(R.drawable.icon_v).placeholder(android.R.drawable.stat_sys_download).error(android.R.drawable.stat_notify_error).into(viewHolder.imagen2);
                } else {
                    Picasso.with(context).load(item.getUrl2()).placeholder(android.R.drawable.stat_sys_download).error(android.R.drawable.stat_notify_error).into(viewHolder.imagen2);
                }
            }
            return listItemView;
        }
    }
}
