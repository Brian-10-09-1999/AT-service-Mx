package com.example.devolucionmaterial.activitys.osSeccion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.activitys.RemplazoComponente.Scan3QR;
import com.example.devolucionmaterial.activitys.RemplazoComponente.ScanRemplazaComponente;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.MenuOpciones;
import com.thanosfisherman.mayi.PermissionBean;

public class ListaOSAsignadas extends BaseActivity {
    String metodo;
    private ListView lvOSAsignadas;
    private MenuOpciones mo;
    private Bundle bndMyBundle;
    private String usuarioID, nombreDeUsuario, OSid, apellido;
    private List<OSAsignada> listaOSAsignada;
    private ArrayAdapter<OSAsignada> adaptador;
    private Menu mMenu;
    private BDmanager manager;
    public static String VISTA_SOLO_COMENTARIOS = "1";
    public static String VISTA_COMPLETA = "2";
    public static String OS_DEL_DIA = "3";

    public static String OS_OTRA_FECHA = "4";
    public static String OS_INICIADA = "1";
    public static String OS_FINALIZADA = "2";
    public static String OS_EN_TRAMITE = "0";
    private static String docPHPactualizaEstActTec = "actualizaEstActTec.php?";
    private static String docPHPOSAsignada = "OSAsignadas.php?";
    private  String[] estatus,OrdenIdX;//liberaFinaliza;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_os_asignadas);
        initToolbar("OS Asignadas",true,true);
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



    private void initSetUp() {
        manager = new BDmanager(context);
        lvOSAsignadas = (ListView) findViewById(R.id.lvOSAsignadas);
        TextView tvUsuario = (TextView) findViewById(R.id.tvUsuarioOSA);
        ImageView imgEmail = (ImageView) findViewById(R.id.imgCorreoosa);
        ImageView imgCallCC = (ImageView) findViewById(R.id.imgLlamarosa);
        ImageView imgInfo = (ImageView) findViewById(R.id.imgInfoosa);
        mo = new MenuOpciones();


        lvOSAsignadas.setOnItemClickListener(selectedItem);


        usuarioID = BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID");
        nombreDeUsuario = BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE");
        apellido = BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_APELLIDO");
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

        barraProgresoOSAsignadas();
    }







    public OnItemClickListener selectedItem = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {


           if(estatus[posicion].equals("2")){

               Intent iLector = new Intent(ListaOSAsignadas.this, Scan3QR.class);
               iLector.putExtra("tecnicoid", BDVarGlo.getVarGlo(ListaOSAsignadas.this, "INFO_USUARIO_ID"));
               iLector.putExtra("ordenid",OrdenIdX[posicion]);
               startActivity(iLector);

           }

           else
           {

               OSAsignada item = listaOSAsignada.get(posicion);
               OSid = item.getOrdenid();
               if (item.getEstatusSActTec().equals(OS_EN_TRAMITE) || item.getEstatusSActTec().equals(OS_OTRA_FECHA) || item.getEstatusSActTec().equals(OS_FINALIZADA) || item.getEstatusSActTec().equals(null) || item.getEstatusSActTec().equals("null")) {
                   Intent intentMenuDevolucion = new Intent(context, ReporteActividadOS.class);
                   bndMyBundle = new Bundle();
                   bndMyBundle.putString("OSidx", OSid);
                   bndMyBundle.putString("tecnicoidx", (usuarioID));
                   bndMyBundle.putString("nombreusuariox", (nombreDeUsuario));
                   bndMyBundle.putString("tipoDeVistaOSx", VISTA_SOLO_COMENTARIOS);
                   bndMyBundle.putString("apellidox", apellido);
                  // bndMyBundle.putString("liberaFinaliza", liberaFinaliza[posicion]);
                   intentMenuDevolucion.putExtras(bndMyBundle);
                   startActivity(intentMenuDevolucion);
               } else if (item.getEstatusSActTec().equals(OS_DEL_DIA) || item.getEstatusSActTec().equals(OS_INICIADA)) {
                   if (item.getEstatusSActTec().equals(OS_INICIADA)) {
                       Intent intentMenuDevolucion = new Intent(context, ReporteActividadOS.class);
                       bndMyBundle = new Bundle();
                       bndMyBundle.putString("OSidx", OSid);
                       bndMyBundle.putString("tecnicoidx", (usuarioID));
                       bndMyBundle.putString("nombreusuariox", (nombreDeUsuario));
                       bndMyBundle.putString("tipoDeVistaOSx", VISTA_COMPLETA);
                       bndMyBundle.putString("usuarioidx", usuarioID);
                       bndMyBundle.putString("apellidox", apellido);
                     //  bndMyBundle.putString("liberaFinaliza", liberaFinaliza[posicion]);
                       intentMenuDevolucion.putExtras(bndMyBundle);
                       startActivity(intentMenuDevolucion);
                   } else {
                       crearDialogoInicioActividad();
                   }
               }
           }
        }
    };

    private void barraProgresoOSAsignadas() {
        if (CheckService.internet(context)) {
            new Progreso().execute();
        } else {
            Alert.ActivaInternet(context);
        }
    }

    private class Progreso extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            listaOSAsignada = new ArrayList<OSAsignada>();
            listaOSAsignada.clear();
            pDialog= new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.Conectando_con_servidor_remoto))
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();
        }

        @Override
        protected String doInBackground(Void... params) {
            metodo = "Progreso<AsyncTask>.doInBackground()";
            String regresa = "";
            try {
                JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPOSAsignada + "tecnicoidx=" + usuarioID);
                assert object != null;
                JSONArray jsonArray = object.optJSONArray("OSAsignadasx");
                JSONObject jsonArrayChild = jsonArray.getJSONObject(0);

                estatus = new String[jsonArray.length()];
                OrdenIdX=new String[jsonArray.length()];
               // liberaFinaliza=new String[jsonArray.length()];

                if (jsonArrayChild.optString("ordenidx").equals("0")) {
                    regresa = jsonArrayChild.optString("ordenidx");
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonArrayChild = jsonArray.getJSONObject(i);


                        estatus[i]=jsonArrayChild.getString("estatus");
                        OrdenIdX[i]=jsonArrayChild.getString("ordenidx");





                      //  liberaFinaliza[i]=jsonArrayChild.getString("liberaFinaliza");




                        listaOSAsignada.add(new OSAsignada(jsonArrayChild.optString("ordenidx"), jsonArrayChild.optString("salax"),
                                jsonArrayChild.optString("fechax"), OS_EN_TRAMITE, jsonArrayChild.optString("estActTecx")));
                    }
                }
                adaptador = new OSAsignadaArrayAdapter(context, listaOSAsignada,estatus);
            } catch (JSONException e) {
                Alert.Error(context, metodo, getString(R.string.titJsonException), e.getMessage());
            } catch (Exception e) {
                Alert.Error(context, metodo, "Error MA001", e.getMessage());
            }
            return regresa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            metodo = "Progreso<AsyncTask>.onPostExecute()";
            if (s.equals("0")) {
                Alert.Alerta(context, metodo, 1, getString(R.string.notOSAs));
                //Alert.Error(context, metodo, getString(R.string.sinRegistros), getString(R.string.notOSAs));
                lvOSAsignadas.setVisibility(View.GONE);
                finish();
            }
            lvOSAsignadas.setAdapter(adaptador);
            pDialog.dismiss();
        }
    }

    private void barraProgresoIniciaReporteOS() {
        if (CheckService.internet(context)) {
            new ProgresoReporteOS().execute();
        } else {
            Alert.ActivaInternet(context);
        }
    }

    private class ProgresoReporteOS extends AsyncTask<Void, Void, String> {
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
        protected String doInBackground(Void... params) {
            String regresa = "";
            metodo = "ProgresoReporteOS<AsyncTask>.doInBackground()";
            try {
                JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url + docPHPactualizaEstActTec + "osidx=" + OSid + "&estActTecx=" + OS_INICIADA);
                assert object != null;
                JSONArray jsonArray = object.optJSONArray("estUpdate");
                JSONObject jsonArrayChild1 = jsonArray.getJSONObject(0);
                regresa = jsonArrayChild1.optString("res");
            } catch (JSONException e) {
                Alert.Error(context, metodo, getString(R.string.titJsonException), e.getMessage());
            } catch (Exception e) {
                Alert.Error(context, metodo, "Error MA001", e.getMessage());
            }
            return regresa;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            metodo = "ProgresoReporteOS<AsyncTask>.onPostExecute()";
            if (s.equals("1")) {
                Intent intentMenuDevolucion = new Intent(context, ReporteActividadOS.class);
                bndMyBundle = new Bundle();
                bndMyBundle.putString("OSidx", OSid);
                bndMyBundle.putString("tecnicoidx", (usuarioID));
                bndMyBundle.putString("nombreusuariox", (nombreDeUsuario));
                bndMyBundle.putString("tipoDeVistaOSx", VISTA_COMPLETA);
                bndMyBundle.putString("usuarioidx", usuarioID);
                intentMenuDevolucion.putExtras(bndMyBundle);
                startActivity(intentMenuDevolucion);
            }
        }
    }






    private class OSAsignada {
        private String ordenid, sala, fecha, OSdelDia, estatusActTec;

        public OSAsignada(String ordenid, String sala, String fecha, String OSDelDia, String estatusActTec) {
            this.ordenid = ordenid;
            this.sala = sala;
            this.fecha = fecha;
            this.OSdelDia = OSDelDia;
            this.estatusActTec = estatusActTec;
        }

        public String getOrdenid() {
            return ordenid;
        }

        public String getSala() {
            return sala;
        }

        public String getFecha() {
            return fecha;
        }

        public String getOSDelDia() {
            return OSdelDia;
        }

        public String getEstatusSActTec() {
            return estatusActTec;
        }

        public void setOSDelDia(String OS) {
            this.estatusActTec = OS;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }
    }












    public class OSAsignadaArrayAdapter extends ArrayAdapter<OSAsignada> {

        private  String[] estatus;

        public OSAsignadaArrayAdapter(Context context, List<OSAsignada> objects,String[] estatus) {
            super(context, 0, objects);
            this.estatus=estatus;


            Log.i("tamño de la lista estatus de OS","------"+estatus.length);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            metodo = "OSAsignadaArrayAdapter<ArrayAdapter>.getView()";
            //Obteniendo una instancia del inflater
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Salvando la referencia del View de la fila
            View listItemView = convertView;

            //Comprobando si el View no existe
            if (null == convertView) {
                //Si no existe, entonces inflarlo con two_line_list_item.xml
                listItemView = inflater.inflate(R.layout.item_lista_os_asignadas, parent, false);
            }

            //Obteniendo instancias de los text views
            TextView OSid = (TextView) listItemView.findViewById(R.id.tvSuperior);
            TextView sala = (TextView) listItemView.findViewById(R.id.tvInferior);
            TextView fecha = (TextView) listItemView.findViewById(R.id.tvFechaAct);
            LinearLayout llBackground = (LinearLayout) listItemView.findViewById(R.id.llBackItemListOsAsig);
            ImageView imgOsEstatus = (ImageView) listItemView.findViewById(R.id.imgOSEstatus);


            //Obteniendo instancia de la Tarea en la posicion actual
            OSAsignada item = getItem(position);
            String fechaOS;
            if (item.getFecha().equals("null")) {
                fechaOS = "SinFecha";
            } else {
                String fechaRecibida = item.getFecha().replace(".", "-");
                MensajeEnConsola.log(context, metodo, "FECHA RECIBIDA DEL SERVIDOR = " + item.getFecha());
                String[] fechaFormatoBad = fechaRecibida.split("-");
                fechaOS = fechaFormatoBad[2] + "-" + fechaFormatoBad[1] + "-" + fechaFormatoBad[0];
            }

            if (fechaOS.equals(getFecha())) {
                if (item.getEstatusSActTec().equals(OS_INICIADA)) {
                    llBackground.setBackgroundColor(Color.parseColor("#FA8072"));
                    imgOsEstatus.setBackgroundResource(R.drawable.en_proceso);
                } else if (item.getEstatusSActTec().equals(OS_FINALIZADA)) {
                    llBackground.setBackgroundColor(Color.parseColor("#00FA9A"));
                    imgOsEstatus.setBackgroundResource(R.drawable.os_ok);
                } else {
                    llBackground.setBackgroundColor(Color.YELLOW);
                    item.setOSDelDia(OS_DEL_DIA);
                    imgOsEstatus.setBackgroundResource(R.drawable.play);
                }
            } else {
                if (item.getEstatusSActTec().equals(OS_FINALIZADA)) {
                    llBackground.setBackgroundColor(Color.parseColor("#00FA9A"));
                    imgOsEstatus.setBackgroundResource(R.drawable.os_ok);
                } else {
                    llBackground.setBackgroundColor(Color.GRAY);
                    imgOsEstatus.setBackgroundResource(R.drawable.waiting_date);
                }
            }



            if(estatus[position].equals("2")) {

                llBackground.setBackgroundColor(Color.parseColor("#FFFFFF"));
                imgOsEstatus.setBackgroundResource(R.drawable.ico_scanqr);

            }



            OSid.setText("OS : " + item.getOrdenid());
            sala.setText(item.getSala());
            fecha.setText(devuelveFechaConFormato(item.getFecha()));

            //Devolver al ListView la fila creada
            return listItemView;
        }

        private String getFecha() {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DAY_OF_MONTH);


            String dia, mes;
            if (day < 10)
                dia = "0" + day;
            else
                dia = "" + day;

            if (month < 10)
                mes = "0" + month;
            else
                mes = "" + month;


            return dia + "-" + mes + "-" + year;
        }
    }


    private void crearDialogoInicioActividad() {
        final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(context);
        alertaSimple.setTitle(getString(R.string.titInicAct));
        alertaSimple.setMessage("Deseas iniciar la actividad para la OS " + OSid + "?");
        alertaSimple.setPositiveButton(getString(R.string.si),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        barraProgresoIniciaReporteOS();
                    }
                });
        alertaSimple.setNegativeButton(getString(R.string.cancelar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertaSimple.setIcon(R.drawable.warning);
        alertaSimple.create();
        alertaSimple.show();
    }

    public void onRestart() {
        super.onRestart();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        }else{
            initSetUp();
        }
    }

    private String devuelveFechaConFormato(String fechaSinFormato) {
        String fechaMod;
        if (fechaSinFormato.equals(null) || fechaSinFormato.equals("null")) {
            fechaMod = "Fecha Pendiente";
        } else {
            String fechaRecibida = fechaSinFormato.replace(".", "-");
            String[] fechaCortada = fechaRecibida.split("-");
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

            fechaMod = day + " " + month + " " + year;
        }
        return fechaMod;

    }



    public void onResume() {
        super.onResume();
        actualizaEstatusTecnico();
    }




}
//530