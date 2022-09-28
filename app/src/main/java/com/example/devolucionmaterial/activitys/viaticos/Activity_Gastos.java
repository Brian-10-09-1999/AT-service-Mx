package com.example.devolucionmaterial.activitys.viaticos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.annca.Annca;
import com.example.annca.internal.configuration.AnncaConfiguration;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.mediosfolios.SelectMediaActivity;
import com.example.devolucionmaterial.activitys.viaticos.retrofit.ResponseCallPdf;
import com.example.devolucionmaterial.activitys.viaticos.retrofit.ResponseCallXml;
import com.example.devolucionmaterial.activitys.viaticos.retrofit.ServiceApiViaticos;
import com.example.devolucionmaterial.api.PostGet;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.chat.activitys.MenuChatActivity;
import com.example.devolucionmaterial.chat.api.ProgressRequestBody;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.sharedpreferences.PreferencesViaticos;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.libraryvideo.subtitle.CaptionsView;
//import com.odn.mpchartlib.utils.FileUtils;
import com.squareup.picasso.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Gastos extends AppCompatActivity implements  ProgressRequestBody.UploadCallbacks, CalendarDialogBuilder.OnDateSetListener  {

    protected Toolbar toolbar;
    private ListView list;
    private Spinner spConcepto;

    private LinearLayout lyRadioButons;
    private TextView tv_especifica,tv_anticipo,tv_saldo,tv_MuestraFecha,tv_NameImage,tv_NamePdf,tv_NameXml,tv_ViajeFinalizado;
    private EditText et_especifica,etMonto,etPropina,etComentario;
    private Button btn_imagen,btn_nuevoGasto, btn_pdf,btn_xml,btn_deleteImage,btn_deletePdf,btn_deleteXml,btn_terminarViaje;

    TabHost tabHost;
    Bitmap bitmap;
    String UPLOAD_KEY = "image";
    private int PICK_IMAGE_REQUEST = 1,archivosAdjuntos=0,ArchivosAenviar=0;
    private Uri filePath;

    private String metodo="(Viaticos) ---------- Activity_Gastos.";
    public String TIPO_VERSION="";
    private Context context;


    /*
    private String url2="http://189.254.111.195:8082/Android/Viaticos/ingresoGasto.php";
    private String url4="http://189.254.111.195:8082/Android/Viaticos/busquedaGastos.php?viajeidx=";
    private String url5="http://189.254.111.195:8082/Android/Viaticos/editarGasto.php";
    private String url6="http://189.254.111.195:8082/Android/Viaticos/gastosRegistrados.php";
    private String url7="http://189.254.111.195:8082/Android/Viaticos/eliminarGasto.php?";
    //private String url8="http://189.254.111.195:8082/Android/Viaticos/cambioEstatusViaje.php?";
    private String url8="http://189.254.111.195:8082/Android/Viaticos/formatoGastos.php?";
    private String url9="http://189.254.111.195:8082/Android/Viaticos/editarAnticipo.php?";

    */


    private String url2="Viaticos/ingresoGasto.php";
    private String url4="Viaticos/busquedaGastos.php?viajeidx=";
    private String url5="Viaticos/editarGasto.php";
    private String url6="Viaticos/gastosRegistrados.php";
    private String url7="Viaticos/eliminarGasto.php?";
    // private String url8="http://189.254.111.195:8082/Android/Viaticos/cambioEstatusViaje.php?";
    private String url8="Viaticos/formatoGastos.php?";
    private String url9="Viaticos/editarAnticipo.php?";



    private String respuesta;
    private PreferencesViaticos preferencesViaticos;

    private int idviaje,status,archivosAenviar=0;
    private double saldo,anticipo;
    private String concepto="",propina="",fechaInicial,fechaFinal;
    private Boolean isotros=false,ispropina=false;
    private double SaldoTotal,LimiteDegasto;

    private CheckBox cbPropina,cbUnir;
    private  String[] monto;
    private  String[] propina2;
    private  String[] total;
    private  String[] concepto2;
    private  String[] fecha;
    private  String[] fechaConsumo;
    private  String[] comentario;
    private String[] idGastoList;
    private String[] tikets;
    private String[] pdfs;
    private String[] xmls;
    private String[] isExcedente;
    private String[] excedenteMonto;
    private String[] excedentePropina;
    private JSONArray[] jsonArrayFiles;

    private String editarConcepto="";
    private String editarMonto="";
    private String editarPropina="";
    private String editarComentario="";
    private String idGastoAmodificar="",idGastoNuevo="";

    private String desayunoIsFree="";
    private String comidaIsFree="";
    private String cenaIsFree="";
    private String saldoHoy="";
    private String unircon="";

    private String TipoFilePdfXml="";
    private final int SELECCIONA_GALLERY = 100;
    private final int SELECCIONA_FOTO = 200;
    private static final int FILE_SELECT_CODE = 1;
    String imagePath, imagePath2,pdfPath="",xmlPath="";
    private String descontarDe="";
    private boolean conDescuento=false;

    private  boolean ImageAdjunta=false,IsModificacion=false,enviarPdf=false,enviarXml=false,enviarImagen=false;

    ProgressDialog barProgressDialog;

    private Date startDate = new Date();
    private Date endDate = new Date();
    private String fechaDeGasto="";
    private String fechaDeHoy="";

    private String baseUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaticos_gastos);

       // cachamos los datos del activity anterior
        list=(ListView)findViewById(R.id.ls_resume);
        idviaje=getIntent().getExtras().getInt("idviaje");
        status=getIntent().getExtras().getInt("status");
        saldo=getIntent().getExtras().getDouble("saldo");
        fechaInicial=getIntent().getExtras().getString("fechaInicial");
        fechaFinal=getIntent().getExtras().getString("fechaFinal");

        String inicio=getIntent().getExtras().getString("inicio");
        String termino=getIntent().getExtras().getString("termino");

        //String inicio="20/05/2018";
        //String termino="23/05/2018";

        LimiteDegasto= Double.parseDouble(getIntent().getExtras().getString("limite"));
        anticipo=getIntent().getExtras().getDouble("anticipo");
        //----------------------------------------------------------------

        context=this;
        Date c = Calendar.getInstance().getTime();SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        fechaDeHoy = df.format(c);
       // Log.i(metodo+"onCreate","id="+idviaje+"   status="+status+"  saldo=$"+saldo+"  fecha Inicial="+fechaInicial+"  fecha final="+fechaFinal+" fecha Hoy="+fechaDeHoy+ "   anticipo=$"+anticipo );

        MensajeEnConsola.logInfo(context,"onCreated","id="+idviaje+"   status="+status+"  saldo=$"+saldo+"  fecha Inicial="+fechaInicial+"  fecha final="+fechaFinal+" fecha Hoy="+fechaDeHoy+ "   anticipo=$"+anticipo);

        fechaDeGasto=fechaDeHoy;

        preferencesViaticos=new PreferencesViaticos(Activity_Gastos.this);

        //ajustes calenmdar
        //Log.i(metodo+"onCreate", "Getinicio "+inicio+"   Getfin "+termino);
        MensajeEnConsola.logInfo(context,"onCreated","Getinicio "+inicio+"   Getfin "+termino);
        try{
            startDate=new SimpleDateFormat("dd/MM/yyyy").parse(inicio);
            if(Build.VERSION.SDK_INT==22 || "M4 SS4456".equals(android.os.Build.MODEL)){
                // esta validacion es por un bug en la fecha final del viaje no deja seleccionar el ultimo dia en android 5.1 y tambien en el modelo "M4 SS4456"
                Date endDate0 =new SimpleDateFormat("dd/MM/yyyy").parse(termino);
                endDate=new Date(endDate0.getYear(),endDate0.getMonth(),endDate0.getDate()+1);

            }
            else{endDate=new SimpleDateFormat("dd/MM/yyyy").parse(termino);}
        }
        catch(Exception e){
           // Log.i(metodo+"onCreate", "Error parsear fecha");
            MensajeEnConsola.logInfo(context,"onCreated","Error al parsear fecha");
        }
       // Log.i(metodo+"onCreate", "inicio "+startDate+"   fin "+endDate);
        MensajeEnConsola.logInfo(context,"onCreated","inicio "+startDate+"   fin "+endDate);


        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION") + "Android/";
        }
        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRUEBAS") + "Android/";
        }
        if (BDVarGlo.getVarGlo(this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION-LOCAL")) {
            baseUrl = BDVarGlo.getVarGlo(this, "URL_DOMINIO_PRODUCCION_LOCAL") + "Android/";
        }


        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.progress_loading);
        btn_imagen=(Button)findViewById(R.id.but_imagen);
        btn_pdf=(Button)findViewById(R.id.but_pdf);
        btn_xml=(Button)findViewById(R.id.but_xml);
        btn_nuevoGasto=(Button)findViewById(R.id.but_nuevo);
        btn_deleteImage=(Button)findViewById(R.id.but_delete_image);btn_deleteImage.setVisibility(View.GONE);
        btn_deletePdf=(Button)findViewById(R.id.but_delete_pdf);btn_deletePdf.setVisibility(View.GONE);
        btn_deleteXml=(Button)findViewById(R.id.but_delete_xml);btn_deleteXml.setVisibility(View.GONE);
        btn_terminarViaje=(Button)findViewById(R.id.but_terminar);

        lyRadioButons=(LinearLayout) findViewById(R.id.layout_radiobutton);
        spConcepto=(Spinner) findViewById(R.id.sp_concepto);

        tv_especifica=(TextView) findViewById(R.id.tv_especificar);
        tv_anticipo=(TextView) findViewById(R.id.tv_anticipo);tv_anticipo.setText("$"+String.format("%.2f",anticipo));
        tv_saldo=(TextView)findViewById(R.id.tv_saldo); tv_saldo.setText("$ "+saldo);
        tv_MuestraFecha=(TextView)findViewById(R.id.tv_muestra_fecha); tv_MuestraFecha.setText(fechaDeHoy);
        tv_NameImage=(TextView) findViewById(R.id.tv_muestra_namefile);
        tv_NamePdf=(TextView)findViewById(R.id.tv_name_pdf);
        tv_NameXml=(TextView)findViewById(R.id.tv_name_xml);
        tv_ViajeFinalizado=(TextView) findViewById(R.id.tv_mensaje_finaliza);
        tv_ViajeFinalizado.setText("Al dar clic en TERMINAR se enviará tu reporte con documentos a comprobaciones y no podrás ingresar más gastos ni subir archivos.");

        et_especifica=(EditText)findViewById(R.id.et_especificacion);
        etMonto=(EditText)findViewById(R.id.et_monto);
        etPropina=(EditText)findViewById(R.id.et_propina);etPropina.setVisibility(View.GONE);
        etComentario=(EditText)findViewById(R.id.et_comentario);

        tv_especifica.setVisibility(View.GONE);
        et_especifica.setVisibility(View.GONE);
        lyRadioButons.setVisibility(View.GONE);

        cbPropina=(CheckBox)findViewById(R.id.cb_propina); cbPropina.setChecked(false);
        cbUnir=(CheckBox)findViewById(R.id.cb_unir); cbUnir.setVisibility(View.GONE);

       /* etMonto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // your action here
                    toastAlert("..");
                }
                else{ /*double m=Double.parseDouble(etMonto.getText().toString());
                  if(m>LimiteDegasto)
                      toastAlert("Especifica excedente");
                }
            }
        });
*/

       cbUnir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

               if(unircon.equals("Comida")){
                   if(comidaIsFree.equals("1") || desayunoIsFree.equals("1") ){
                       toastAlert("Gasto ya Elegido");spConcepto.setSelection(0);

                   }

               }
               else if(unircon.equals("Cena") ){
                    if(cenaIsFree.equals("1") || comidaIsFree.equals("1")){

                        toastAlert("Gasto ya Elegido");spConcepto.setSelection(0);
                    }

               }


           }
         }
       );


        etMonto.addTextChangedListener(new TextWatcher() {
           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {}
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
           @Override
           public void afterTextChanged(Editable s) {

             if(s.toString().equals("")){}
             else{

                 if(ValidaLimiteGasto()){}
                 else{toastAlert("Excedes el monto o propina, por favor justifica en comentarios.");}
             }


           }
       });



        //ArrayAdapter<CharSequence> adapterConcepto= ArrayAdapter.createFromResource(this,R.array.concepto,android.R.layout.simple_spinner_item);
        String [] conceptos = new String[]{"....", "Desayuno", "Comida", "Cena", "Bebidas o Alimentos", "Taxi", "Boleto de Autobus", "Deposito", "Otros"};
        CustomListSpinerConcepto adapterConcepto= new CustomListSpinerConcepto(getApplicationContext(),conceptos);


        spConcepto.setAdapter(adapterConcepto);
        spConcepto.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn,
                                               android.view.View v,
                                               int posicion,
                                               long id) {

                        Log.i("------------spinner"," posicion "+posicion);

                        conDescuento=false;unircon="";
                        cbUnir.setChecked(false); cbUnir.setVisibility(View.GONE);cbPropina.setVisibility(View.GONE);
                        if(posicion==0){concepto="";}
                        if(posicion==1){
                            cbPropina.setVisibility(View.VISIBLE);
                            concepto="Desayuno";
                            cbUnir.setVisibility(View.VISIBLE);
                            cbUnir.setText("Unir Con Comida");unircon="Comida";
                            if(desayunoIsFree.equals("1")){ toastAlert("Gasto ya Elegido");spConcepto.setSelection(0);}}
                        if(posicion==2){
                            cbPropina.setVisibility(View.VISIBLE);
                            concepto="Comida";
                            cbUnir.setText("Unir Con Cena");unircon="Cena";
                            cbUnir.setVisibility(View.VISIBLE);
                            if(comidaIsFree.equals("1")){ toastAlert("Gasto ya Elegido");spConcepto.setSelection(0);}}
                        if(posicion==3){
                            cbPropina.setVisibility(View.VISIBLE);
                            concepto="Cena";
                            cbUnir.setText("Unir Con desayuno dia Siguiente"); unircon="Desayuno";
                            cbUnir.setVisibility(View.VISIBLE);
                            if(cenaIsFree.equals("1")){ toastAlert("Gasto ya Elegido");spConcepto.setSelection(0);}}

                        if(posicion==4){concepto="Bebidas o Alimentos";ConfigSpinner(2); conDescuento=true;}
                        else{;ConfigSpinner(3);}
                        if(posicion==5)concepto="Taxi";
                        if(posicion==6)concepto="Boleto Autobus";
                        if(posicion==7)concepto="Deposito";
                        if(posicion==8){ConfigSpinner(1);isotros=true;concepto="Otros"; cbPropina.setVisibility(View.VISIBLE);}
                        else{ConfigSpinner(0);isotros=false;}
                    }
                    public void onNothingSelected(AdapterView<?> spn) {

                    }
                });



        btn_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePopup();
            }
        });

        btn_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TipoFilePdfXml="pdf";
                Intent i = new Intent(Activity_Gastos.this, ActivityFileExplore.class );
                startActivityForResult(i, FILE_SELECT_CODE);
            }
        });

        btn_xml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TipoFilePdfXml="xml";
                Intent i = new Intent(Activity_Gastos.this, ActivityFileExplore.class );
                startActivityForResult(i, FILE_SELECT_CODE);
            }
        });

        btn_deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               tv_NameImage.setText("");enviarImagen=false;
               btn_imagen.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_image2));
               btn_deleteImage.setVisibility(View.GONE);
            }
        });

        btn_deletePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_NamePdf.setText("");enviarPdf=false;
                btn_pdf.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_pdf2));
                btn_deletePdf.setVisibility(View.GONE);
            }
        });

        btn_deleteXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_NameXml.setText("");enviarXml=false;
                btn_xml.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_xml2));
                btn_deleteXml.setVisibility(View.GONE);
            }
        });


        btn_nuevoGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 30/03/2017  hay que ver si es necesario el login

           //spConcepto.getSelectedItem().toString().equals("....")
               if(isOnline())
               {
                   if(etMonto.getText().toString().equals("") ||  spConcepto.getSelectedItemPosition()==0   ){toastAlert("Monto y Concepto No Puede Estar Vacios");}

                   else{
                       if(ValidaLimiteGasto()){
                           new Activity_Gastos.IngresaNuevoGasto().execute();
                       }
                       else{
                           if(etComentario.getText().toString().equals("")){ toastAlert("Justifica tu excedente en comentarios. " + "Recuerda tus máximos: 1 alimento $"+String.format("%.2f", LimiteDegasto)+ ", 2 unidos $"+String.format("%.2f", LimiteDegasto*2) + " y propina 10%.");}
                          else{
                               new Activity_Gastos.IngresaNuevoGasto().execute();
                           }
                       }

                   }
               }
               else{alertas("No Estas Conectado a Internet",2);}

            }
        });

        cbPropina.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {

                    etPropina.setText("");
                    etPropina.setVisibility(View.VISIBLE);
                }
                else
                {
                    //not checked
                    etPropina.setText("");
                    etPropina.setVisibility(View.GONE);
                }

            }});

        btn_terminarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {ShowAlertTerminarViaje();}
        });



        ConfigTabhost();

        if(isOnline()) new Activity_Gastos.ActualizaHoy().execute();
        else{alertas("No Estas Conectado a Internet",2);}

        if(status==2) DesabilitaTodo();

    }



 //------------------------------------------------------------------------------------------------------



     private void ConfigSpinner(int i){
        if (i == 1) {
             tv_especifica.setVisibility(View.VISIBLE);
             et_especifica.setVisibility(View.VISIBLE);
             tv_especifica.setText("Especificar");
             lyRadioButons.setVisibility(View.GONE);
        }
         if(i==0){
             tv_especifica.setVisibility(View.GONE);
             et_especifica.setVisibility(View.GONE);}

         if(i==2){lyRadioButons.setVisibility(View.VISIBLE);}
         if(i==3){lyRadioButons.setVisibility(View.GONE);}
    }

    public boolean ValidaLimiteGasto(){

        if(concepto.equals("Desayuno") || concepto.equals("Comida") || concepto.equals("Cena")){}else{return true;}

         boolean isCorrect=false;
        double monto = Double.parseDouble(etMonto.getText().toString());
        double limite=LimiteDegasto;


        if(cbUnir.isChecked()) {limite=LimiteDegasto*2;}

        if(monto<=limite){isCorrect=true;}

        if(cbPropina.isChecked()) {

            if(etPropina.getText().toString().equals("")){}

            else {
                double propina = Double.parseDouble(etPropina.getText().toString());
                if (propina <= limite * (0.1) & monto <= limite) {
                    isCorrect = true;
                } else {
                    isCorrect = false;
                }
            }
        }



         return isCorrect;
    }



    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rb_desayuno:
                if (checked)
                     descontarDe="Desayuno";
                    break;
            case R.id.rb_comida:
                if (checked)
                     descontarDe="Comida";
                    break;
            case R.id.rb_cena:
                if (checked)
                    descontarDe="Cena";
                    break;
        }
    }


    @Override
    public void onDateSet(int Year, int Month, int Day) {
        if(Month<10)fechaDeGasto=Day+"/0"+(Month+1)+"/"+Year+"";
        else fechaDeGasto=Day+"/"+(Month+1)+"/"+Year+"";

        if(fechaDeGasto.equals("0/01/0")){fechaDeGasto=fechaDeHoy;}

        tv_MuestraFecha.setText(fechaDeGasto);
       // Log.i(metodo+"onDataset: fecha--------------",fechaDeGasto);
        MensajeEnConsola.logInfo(context,"onDataSet","fecha--------------"+fechaDeGasto);

        if(isOnline()) new Activity_Gastos.ActualizaHoy().execute();
        else{alertas("No Estas Conectado a Internet",2);}
    }


    public void ShowCalendar(View view){
    CalendarDialogBuilder calendar;

    calendar = new CalendarDialogBuilder(Activity_Gastos.this, this);
    //calendar = new CalendarDialogBuilder(Activity_Gastos.this, this, initialDate.getTime());

/*
    if(initialDate != null)
    {
        calendar = new CalendarDialogBuilder(this, this, initialDate.getTime());
    } else {
        calendar = new CalendarDialogBuilder(this, this);
    }

    calendar.setStartDate(startDate.getTime());
    calendar.setEndDate(endDate.getTime());
    */

    calendar.setStartDate(startDate.getTime());
    calendar.setEndDate(endDate.getTime());
    calendar.showCalendar();
}

    public void ShowEditAnticipo(View view){
        EditarAnticipo();

    }

    private void ConfigTabhost() {

        tabHost = (TabHost) findViewById(R.id.tabhost1); //llamamos al Tabhost
        tabHost.setup();
        //lo activamos

        TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1");  //aspectos de cada Tab (pestaña)
        TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2");
        TabHost.TabSpec tab3 =tabHost.newTabSpec("tab3");

        Intent tabIntent = new Intent(this, new Activity_Gastos().getClass());

       // tab1.setContent(R.id.tab1); //definimos el id de cada Tab (pestaña)
        tab1.setIndicator("Gasto");
        tab1.setContent(R.id.tab1);
        tab2.setIndicator("Resum("+idviaje+")");
        tab2.setContent(R.id.tab2);
        tab3.setIndicator("Status");
        tab3.setContent(R.id.tab3);

      //  tab1.setIndicator(getResources().getDrawable(R.drawable.custom_button_menu));
        tabHost.addTab(tab1); //añadimos los tabs ya programados
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
        tv.setTextColor(Color.parseColor("#ffffff"));
          //if(i==0) {  tv.setHeight(34);tv.setBackgroundResource(R.drawable.ico_nuevo_gasto);}
          //if(i==1) {  tv.setHeight(30);tv.setWidth(30);tv.setBackgroundResource(R.drawable.ico_resum_gasto4);}
          //if(i==2) {  tv.setHeight(31);tv.setWidth(31);tv.setBackgroundResource(R.drawable.ico_status_gasto);}
          tv.setTextSize(14);


        }

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
              //  Log.d("Activity_Gastos:click tabHost", "onTabChanged: tab number=" + tabHost.getCurrentTab());
                switch (tabHost.getCurrentTab()) {
                    case 0:
                        //do what you want when tab 0 is selected
                        break;
                    case 1:
                        //do what you want when tab 1 is selected
                        if(isOnline())new Activity_Gastos.ActualizaResumen(true).execute();
                        else{toastAlert("Sin Conexion a Internet, Gastos No Actualizados");
                            new ActualizaResumen(false).execute();
                        }
                        break;
                    case 2:
                        //do what you want when tab 2 is
                        break;
                    default:
                        break;
                }
            }
        });
    }


    //este metodo solo es llamado si el viaje se ha terminado y su estatus es "2"
     private void DesabilitaTodo(){

        list.setClickable(false);//el bloqueo del click de lisitview se restringe en "ActualizaResumen"
        btn_nuevoGasto.setEnabled(false);
        btn_terminarViaje.setEnabled(false);
        spConcepto.setEnabled(false);
        etMonto.setEnabled(false);
        etComentario.setEnabled(false);
        cbPropina.setEnabled(false);
        btn_imagen.setEnabled(false);
        btn_xml.setEnabled(false);
        btn_pdf.setEnabled(false);

        tv_ViajeFinalizado.setTextColor(Color.RED);
        tv_ViajeFinalizado.setText("Este Viaje Ya ha Sido Terminado.");
     }
    //-----------------------------------------------------  TAREAS EN SEGUNDO PLANO PARA PETICIONES Y PARSEOS-------------------------





    class IngresaNuevoGasto extends AsyncTask<Void, Void, Void> {
     //   ProgressDialog loading;
        String exito="0",gastoId="";
        private ProgressDialog pDialog;
        protected void onPreExecute() {
       //     loading = ProgressDialog.show(Activity_Gastos.this, "Conectando...", null,true,false);
            pDialog = new ProgressDialog(Activity_Gastos.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }

        protected Void doInBackground(Void... JSONurl) {

            PostGet servicios=new PostGet(context);

            final JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("tecnicoidx", BDVarGlo.getVarGlo(context,"INFO_USUARIO_ID"));
                jsonParam.put("viajeidx", idviaje);
                if(cbUnir.isChecked()){ jsonParam.put("nombreGastox", concepto+" - "+unircon);} else{ jsonParam.put("nombreGastox", concepto);}
                if(isotros)  jsonParam.put("otrox", et_especifica.getText().toString());else{jsonParam.put("otrox", "");}
                jsonParam.put("costox", etMonto.getText().toString());
                if("".equals(etPropina.getText().toString())){jsonParam.put("propinax", "0.0");}else{  jsonParam.put("propinax", etPropina.getText().toString());}
                jsonParam.put("comentariosx",etComentario.getText().toString());
                jsonParam.put("fecha",fechaDeGasto);
                if(conDescuento){jsonParam.put("descontarDe",descontarDe);}  else{jsonParam.put("descontarDe","");}
            } catch (JSONException e) {
               // Log.i(metodo+"IngresaNuevoGasto doInBackground:","Error al crear json del body");

                MensajeEnConsola.logInfo(context,"IngresaNuevoGasto","doInBackground: "+"Error en json:"+e.toString());

            }

            try {
                //Log.i(metodo+"IngresaNuevoGasto ","Peticion Post Url "+ baseUrl+url2);
                MensajeEnConsola.logInfo(context,"IngresaNuevoGasto","Peticion POST Url= "+ baseUrl+url2);
                //Log.i(metodo+"IngresaNuevoGasto ","Cuerpo="+jsonParam.toString());
                MensajeEnConsola.logInfo(context,"IngresaNuevoGasto","Cuerpo= "+jsonParam.toString());

                respuesta=servicios.ConexionPost(baseUrl+url2,jsonParam);
                //Log.i(metodo+"IngresaNuevoGasto ","Respuesta "+respuesta);
                MensajeEnConsola.logInfo(context,"IngresaNuevoGasto","Respuesta= "+respuesta);

                JSONObject json=new JSONObject(respuesta);
                exito=json.getString("respuesta");
                gastoId=json.getString("gastoid");
                SaldoTotal=json.getDouble("saldo");

            } catch (Exception e) {
                //Log.e(metodo+"IngresaNuevoGasto doInBackground:", "Peticion Post  Error" + e.getMessage());
                MensajeEnConsola.logError(context,"IngresaNuevoGasto","Peticion POST  Error:" + e.getMessage());


            }

            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(exito.equals("1")){
                tv_saldo.setText("$ "+SaldoTotal);
                tv_MuestraFecha.setText(fechaDeHoy);
                spConcepto.setSelection(0);
                toastAlert("Nuevo Gasto Agregado");

                SendFileconRetrofit(gastoId);
               //if(enviarPdf)    SendFileconRetrofit(gastoId,pdfPath);
               //if(enviarXml)    SendFileconRetrofit(gastoId,xmlPath);
                et_especifica.setText("");
                etMonto.setText("");
                etComentario.setText("");
                etPropina.setText("");
                cbPropina.setChecked(false);

            }
            else{
                toastAlert("Error Al Ingresar Nuevo Gasto ");
            }

           // loading.dismiss();
            pDialog.dismiss();
        }
    }




    class ActualizaHoy extends AsyncTask<Void, Void, Void> {
       // ProgressDialog loading;
        private ProgressDialog pDialog;
        boolean modExito=false;
        protected void onPreExecute() {
         //   loading = ProgressDialog.show(Activity_Gastos.this, "Conectando...", null,true,false);
            pDialog = new ProgressDialog(Activity_Gastos.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }
        protected Void doInBackground(Void... JSONurl) {
            PostGet  servicios=new PostGet(context);

            try {
                //Log.i(metodo+"EditarGasto","peticion Post Url "+baseUrl+url6+"?fecha="+fechaDeGasto+"&idviaje="+idviaje);
                MensajeEnConsola.logInfo(context,"EditarGasto","Peticion POST Url "+baseUrl+url6+"?fecha="+fechaDeGasto+"&idviaje="+idviaje);

                respuesta=servicios.ConexionGet(baseUrl+url6+"?fecha="+fechaDeGasto+"&idviaje="+idviaje);
                //Log.i(metodo+"EditarGasto","Respuesta "+respuesta);
                MensajeEnConsola.logInfo(context,"EditarGasto","Respuesta= "+respuesta);

                JSONObject json=new JSONObject(respuesta);

                desayunoIsFree=json.getString("desayuno");
                comidaIsFree=json.getString("comida");
                cenaIsFree=json.getString("cena");
                saldoHoy=json.getString("saldo");

                modExito=true;
            } catch (Exception e) {
                Log.e(metodo+"ActualizaHoy ", "Error en peticion Get " + e.getMessage());
                modExito=false;
            }
            return null;
        }
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);


            if( modExito) {  tv_saldo.setText("$ "+saldoHoy);
                spConcepto.setSelection(0);
            }
            else{    }
            //loading.dismiss();
            pDialog.dismiss();
        }

    }




    class EliminaGasto extends AsyncTask<Void, Void, Void> {
       // ProgressDialog loading;
        String Exito="0"; String saldo="";
        private ProgressDialog pDialog;
        protected void onPreExecute() {
         //   loading = ProgressDialog.show(Activity_Gastos.this, "Conectando...", null,true,false);
            pDialog = new ProgressDialog(Activity_Gastos.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }
        protected Void doInBackground(Void... JSONurl) {

            PostGet servicios=new PostGet(context);

            try {
                //Log.i(metodo+"EliminaGasto ","peticion Get Url "+baseUrl+url7+"idgasto="+idGastoAmodificar);

                MensajeEnConsola.logInfo(context,"EliminaGasto","Peticion GET Url "+baseUrl+url7+"idgasto="+idGastoAmodificar );

                respuesta=servicios.ConexionGet(baseUrl+url7+"idgasto="+idGastoAmodificar);
                //Log.i(metodo+"EliminaGasto ","Respuesta "+respuesta);
                MensajeEnConsola.logInfo(context,"EliminaGasto","Respuesta= "+respuesta);

                JSONObject json=new JSONObject(respuesta);

               Exito=json.getString("responder");
               saldo=json.getString("saldo");
            } catch (Exception e) {
                //Log.e(metodo+"EliminaGasto ", "Error en peticion Get " + e.getMessage());

                MensajeEnConsola.logError(context,"EliminaGasto","Error en peticion Get " + e.getMessage());
            }
            return null;
        }
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            if(Exito.equals("1")){new Activity_Gastos.ActualizaResumen(true).execute(); tv_saldo.setText("$ "+saldo);  }
            //loading.dismiss();
            pDialog.dismiss();
        }

    }



    class EditarAnticipo extends AsyncTask<Void, Void, Void> {
        //ProgressDialog loading;
        String Exito="0"; double newAnticipo=0;
        private ProgressDialog pDialog;
        protected void onPreExecute() {
            //loading = ProgressDialog.show(Activity_Gastos.this, "Conectando...", null,true,false);
            pDialog = new ProgressDialog(Activity_Gastos.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }
        protected Void doInBackground(Void... JSONurl) {
            PostGet  servicios=new PostGet(context);

            try {
               // Log.i(metodo+"EditarAnticipo ","peticion Get Url "+baseUrl+url9+"idviaje="+idviaje+"&nuevoAnticipo="+anticipo);

                MensajeEnConsola.logInfo(context,"EditarAnticipo","peticion GET Url "+baseUrl+url9+"idviaje="+idviaje+"&nuevoAnticipo="+anticipo);

                respuesta=servicios.ConexionGet(baseUrl+url9+"idviaje="+idviaje+"&nuevoAnticipo="+anticipo);
              //  Log.i(metodo+"EditarAnticipo ","Respuesta "+respuesta);

                MensajeEnConsola.logInfo(context,"EditarAnticipo","Respuesta= "+respuesta);

                JSONObject json=new JSONObject(respuesta);

                Exito=json.getString("responder");
                newAnticipo=json.getDouble("anticipo");


            } catch (Exception e) {
               // Log.e(metodo+"EditarAnticipo ", "Error en peticion Get " + e.getMessage());
                MensajeEnConsola.logError(context,"EditarAnticipo","Error en Peticion GET " + e.getMessage() );
            }
            return null;
        }
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            if(Exito.equals("1")){
                tv_anticipo.setText("$"+ String.format("%.2f",anticipo));
                tv_saldo.setText("$ "+ String.format("%.2f",newAnticipo));
            }
            else{}
            //loading.dismiss();
            pDialog.dismiss();
        }

    }


    class EditaGasto extends AsyncTask<Void, Void, Void> {
       // ProgressDialog loading;
       private ProgressDialog pDialog;
        String Exito="0";
        protected void onPreExecute() {
          //  loading = ProgressDialog.show(Activity_Gastos.this, "Conectando...", null,true,false);
            pDialog = new ProgressDialog(Activity_Gastos.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }
        protected Void doInBackground(Void... JSONurl) {
            PostGet servicios=new PostGet(context);

            final JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("nombreGastox", editarConcepto);
                jsonParam.put("costox", editarMonto);
                jsonParam.put("propinax", editarPropina);
                jsonParam.put("comentariosx", editarComentario);
                jsonParam.put("idx",idGastoAmodificar );
            } catch (JSONException e) {  Log.i(metodo+"EditaGasto ","Error en doInBackground al crear json del body");}

            try {
                Log.i(metodo+"EditaGasto","peticion Post Url "+baseUrl+url5);
                Log.i(metodo+"EditaGasto","Cuerpo="+jsonParam.toString());
                respuesta=servicios.ConexionPost(baseUrl+url5,jsonParam);
                Log.i(metodo+"EditarGasto","Respuesta "+respuesta);
                JSONObject json=new JSONObject(respuesta);
                Exito=json.getString("responder");
            } catch (Exception e) {
                Log.e(metodo+"EditaGastos", "Error en peticion Post " + e.getMessage());
            }
            return null;
        }
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
           if( Exito.equals("1")){ Toast.makeText(Activity_Gastos.this, "Gasto Actualizado", Toast.LENGTH_SHORT).show();
               new Activity_Gastos.ActualizaResumen(true).execute();
           }
           else{Toast.makeText(Activity_Gastos.this, "Gasto No Actualizado", Toast.LENGTH_SHORT).show();}
            //loading.dismiss();
            pDialog.dismiss();
        }

    }




    class ActualizaResumen extends AsyncTask<Void, Void, Void> {

        //ProgressDialog loading;
        boolean isConect,Exito=false;
        String saldo=tv_saldo.getText().toString();
        private ProgressDialog pDialog;

        public ActualizaResumen(boolean online) {
            super();
            if(online)isConect=true;
            else{isConect=false;}
        }

        protected void onPreExecute() {
          //  loading = ProgressDialog.show(Activity_Gastos.this, "Conectando...", null,true,false);
            pDialog = new ProgressDialog(Activity_Gastos.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }
        protected Void doInBackground(Void... JSONurl) {

            PostGet servicios=new PostGet(context);

            try {
                //si esta conectado lanza peticio si no jala el ultimo json guardado
                if(isConect){
                    //Log.i(metodo+"ActualizaResumen ", "Peticion Get " + baseUrl+url4+idviaje);

                    MensajeEnConsola.logInfo(context,"ActualizaResumen","Peticion GET " + baseUrl+url4+idviaje);

                    respuesta = servicios.ConexionGet(baseUrl+url4+idviaje);
                    //Log.i(metodo+"ActualizaResumen ", "Respuesta " + respuesta);

                    MensajeEnConsola.logInfo(context,"ActualizaResumen","Respuesta= "+respuesta);

                }
                else{

                    //Log.i(metodo+"ActualizaResumen ", "(No conectado) Peticion preferencesViaticos ");
                    MensajeEnConsola.logInfo(context,"ActualizaResumen","(No conectado) Peticion preferencesViaticos ");
                    respuesta=preferencesViaticos.getJson("ListaGastos"+idviaje);
                    //Log.i(metodo+"ActualizaResumen ", "Respuesta " + respuesta);
                    MensajeEnConsola.logInfo(context,"ActualizaResumen", "Respuesta" + respuesta);

                }

                if(respuesta.equals("") || respuesta.equals("{\"gastos\":[]}")){} else{Exito=true;}

                JSONObject json2;
                if(Exito){ json2 = new JSONObject(respuesta);}
                else {json2 = new JSONObject("{\"gastos\":[]}");}
                JSONArray jsonArray2 = json2.getJSONArray("gastos");


                monto = new String[jsonArray2.length()];
                propina2=new String[jsonArray2.length()];
                total=new String[jsonArray2.length()];
                concepto2=new String[jsonArray2.length()];
               // fecha=new String[jsonArray2.length()];
                fechaConsumo=new String[jsonArray2.length()];
                comentario=new String[jsonArray2.length()];
                idGastoList=new String[jsonArray2.length()];
                tikets=new String[jsonArray2.length()];
                pdfs=new String[jsonArray2.length()];
                xmls=new String[jsonArray2.length()];
                isExcedente=new String[jsonArray2.length()];
                excedenteMonto=new String[jsonArray2.length()];
                excedentePropina=new String[jsonArray2.length()];
                jsonArrayFiles=new  JSONArray [jsonArray2.length()];


                for (int i = 0; i < jsonArray2.length(); i++) {
                    JSONObject objetoJson = jsonArray2.getJSONObject(i);

                    concepto2[i]=objetoJson.getString("gasto");
                    monto[i]=objetoJson.getString("costo");
                    propina2[i]=objetoJson.getString("propina");

                    total[i]=""+(objetoJson.getDouble("costo")+objetoJson.getDouble("propina"));

                   // fecha[i]="Registro:"+objetoJson.getString("fecha");
                    fechaConsumo[i]="Consumo:"+objetoJson.getString("fechaGasto");
                    comentario[i]=objetoJson.getString("comentario");
                    idGastoList[i]=objetoJson.getString("id");
                    tikets[i]=objetoJson.getString("imagenTicket");
                    pdfs[i]=objetoJson.getString("pdf");
                    xmls[i]=objetoJson.getString("xml");
                    isExcedente[i]=objetoJson.getString("isexcedente");
                    excedenteMonto[i]=  objetoJson.getString("excedenteGasto");
                    excedentePropina[i]=objetoJson.getString("excedentePropina");

                   //todo jsonArrayFiles[i] = objetoJson.getJSONArray("files");



                }

                if(Exito){
                    saldo=json2.getString("saldo");
                    preferencesViaticos.setJson("ListaGastos"+idviaje,respuesta);//se guarda el json para cuando no exista conexion
                     }

            }
            catch (Exception e) {
               // Log.e(metodo+"ActualizaResumen ", "ERROR En doInBackground: Exception :" + e.getMessage());

                MensajeEnConsola.logError(context,"ActualizaResumen","ERROR En doInBackground: Exception :" + e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(Exito){ tv_saldo.setText("$ "+saldo);} else{toastAlert("Ningun Gasto Ingresado");}

            CustomListAdaperResum adapter = new CustomListAdaperResum(Activity_Gastos.this, monto, propina2, total, concepto2, comentario,tikets,pdfs,xmls,fechaConsumo,isExcedente,excedenteMonto,
                    excedentePropina);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     String idd= ""+idGastoList[+position];
                     idGastoAmodificar=idGastoList[+position];
                     editarConcepto=concepto2[+position];
                     editarMonto=monto[+position];
                     editarPropina=propina2[+position];
                     editarComentario=comentario[+position];
                    // Toast.makeText(getApplicationContext(), idd, Toast.LENGTH_SHORT).show();
                    if(status != 2) { // si aun no se ha cerrado el viaje
                        showPopupEditGasto(idd);
                    }
                    else{toastAlert("Haz finalizado tu viaje, ya no puedes realizar ningún cambio. De ser necesario comunícate al área de comprobaciones.");}
                }
            });

            //loading.dismiss();
           pDialog.dismiss();
        }
    }





    class TerminaViaje extends AsyncTask<Void, Void, Void> {
        //ProgressDialog loading;
        String Exito="";

        private ProgressDialog pDialog;

        protected void onPreExecute() {
           // loading = ProgressDialog.show(Activity_Gastos.this, "Conectando...", null,true,false);
            pDialog = new ProgressDialog(Activity_Gastos.this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.setContentView(R.layout.custom_progressdialog);
        }
        protected Void doInBackground(Void... JSONurl) {

            PostGet servicios=new PostGet(context);
            try {

               // Log.i(metodo+"TerminaViaje ","peticion Get Url "+baseUrl+url8+"idviajex="+idviaje+"&estatusx=2");
                //Exito=servicios.ConexionGetPDF(url8+"idviajex="+idviaje+"&estatusx=2",idviaje+"");

                MensajeEnConsola.logInfo(context,"TerminaViaje","Peticion GET Url "+baseUrl+url8+"idviajex="+idviaje+"&estatusx=2");

                respuesta=servicios.ConexionGet(baseUrl+url8+"idviajex="+idviaje+"&estatusx=2");
               // Log.i(metodo+"TerminaViaje Respuesta",respuesta );
                MensajeEnConsola.logInfo(context,"TerminaViaje","Respuesta ="+respuesta);

                JSONObject  json= new JSONObject(respuesta);
                Exito=json.getString("respuesta");


            } catch (Exception e) {
               // Log.e(metodo+"TerminaViaje ", "Error en peticion Get " + e.getMessage());
                MensajeEnConsola.logError(context,"TerminaViaje","Error en Peticion GET " + e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            pDialog.dismiss();// loading.dismiss();
            if(Exito.equals("1")) {

                 toastAlert("Se ha enviado tu comprobante de Gastos a tu correo");
                finish();//se termina la actividad para obligar se recargue el nuevo estatus en la actividad anterior
            }
            else{  toastAlert("No se pudo Terminar el Viaje");  }


        }

    }



//------------------------------------------------------ captura de medios -----------------------------------------------------------




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == SELECCIONA_GALLERY) {

            if (data == null) {
                //Snackbar.make(parentView, R.string.string_unable_to_pick_image, Snackbar.LENGTH_INDEFINITE).show();
                return;
            }
            Uri selectedImageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagePath=cursor.getString(columnIndex);
                //Log.e(metodo+"onActivityResult ","imagePath="+ imagePath);
                MensajeEnConsola.logError(context,"onActivityResult","imagePath="+ imagePath);

                File file = new File(imagePath);
                enviarImagen=true;
                cursor.close();

                if(IsModificacion){ if(isOnline()) SendFileconRetrofit(idGastoAmodificar); else{toastAlert("No Estas Conectado a Internet");}IsModificacion=false;}
                else{
                    tv_NameImage.setText(file.getName());
                    btn_deleteImage.setVisibility(View.VISIBLE);
                    btn_imagen.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_image1));
                }


            } else {
                //Snackbar.make(parentView, R.string.string_unable_to_load_image, Snackbar.LENGTH_LONG).show();
            }
        }
        //tomar foto ok
        if (resultCode == RESULT_OK && requestCode == SELECCIONA_FOTO) {

            imagePath = data.getStringExtra(AnncaConfiguration.Arguments.FILE_PATH);
           // Log.e(metodo+"onActivityResult ","imagePath="+ imagePath);
            MensajeEnConsola.logError(context,"onActivityResult","imagePath="+ imagePath);

            File file = new File(imagePath);
            enviarImagen=true;

            if(IsModificacion){ if(isOnline()) SendFileconRetrofit(idGastoAmodificar); else{toastAlert("No Estas Conectado a Internet");}IsModificacion=false;}
            else{
                tv_NameImage.setText(file.getName());
                btn_deleteImage.setVisibility(View.VISIBLE);
                btn_imagen.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_image1));
            }

        }

        if (resultCode == RESULT_OK && requestCode == FILE_SELECT_CODE) {

               String path=data.getStringExtra("Path");
               String name=data.getStringExtra("NameFile");
               String path2=path+"/"+name;
               File file= new File(name);

              // Log.e(metodo+"onActivityResult","path File="+path2);
               //Log.e(metodo+"onActivityResult","Extencion File="+getFileExtension(file));

               MensajeEnConsola.logError(context,"onActivityResult","path File="+path2+"  Extencion File="+getFileExtension(file));

               String extFile=getFileExtension(file);

               if(extFile.equals("pdf") || extFile.equals("Pdf") || extFile.equals("PDF")){

                       pdfPath=path2;
                       enviarPdf=true;
                       if(IsModificacion){ if(isOnline()){ SendFileconRetrofit(idGastoAmodificar);} else{toastAlert("No Estas Conectado a Internet");}IsModificacion=false;}
                       else{

                           tv_NamePdf.setText(name);
                           btn_deletePdf.setVisibility(View.VISIBLE);
                           btn_pdf.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_pdf1));
                       }
                   }
                 //  else{toastAlert("El archivo Elegido no es PDF"); tv_NamePdf.setText("...");  enviarPdf=false;}
               else if(extFile.equals("xml") || extFile.equals("Xml") || extFile.equals("XML")){

                       xmlPath=path2;
                       enviarXml=true;

                       if(IsModificacion){ if(isOnline()) SendFileconRetrofit(idGastoAmodificar); else{toastAlert("No Estas Conectado a Internet");} IsModificacion=false;}
                       else{

                           tv_NameXml.setText(name);
                           btn_deleteXml.setVisibility(View.VISIBLE);
                           btn_xml.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_xml1));


                       }
                   }
               else if( !extFile.equals("pdf") & !extFile.equals("xml") & !extFile.equals("Xml") & !extFile.equals("XML") & !extFile.equals("Pdf")& !extFile.equals("PDF")){
                   toastAlert("Archivo Elegido No es pdf ni xml");

               }
                 //  else{toastAlert("El archivo Elegido no es XML");tv_NameXml.setText("...");enviarXml=false;}

        }
       // Log.e(metodo+"onActivityResult","Archivos a Enviar: imagen="+enviarImagen+"  pdf="+enviarPdf+"  xml="+enviarXml);

        MensajeEnConsola.logError(context,"onActivityResult","Archivos a Enviar: imagen="+enviarImagen+"  pdf="+enviarPdf+"  xml="+enviarXml );


    }








    private String getFileExtension(File file) {
        String name = file.getName();
        try {return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {return "";
        }
    }


    public void showPopupEditGasto(String idg) {

        CharSequence colors[] = new CharSequence[]{"Añadir Imagen","Añadir Pdf o Xml","Borrar Gasto"};//se quito editar campos

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione una Opción");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                    IsModificacion = false;

                /* if (which == 0) {
                  EditarCampos(idg);
                } else */

                    if (which == 0) {
                        IsModificacion = true;
                        showImagePopup();
                    } else if (which == 1) {
                        Intent i = new Intent(Activity_Gastos.this, ActivityFileExplore.class);
                        startActivityForResult(i, FILE_SELECT_CODE);
                        IsModificacion = true;
                    } else if (which == 2) {

                        if (isOnline()) new Activity_Gastos.EliminaGasto().execute();
                        else {
                            toastAlert("No Estas Conectado a Internet");
                        }

                    }


            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        //builder.show();
    }


    public void EditarAnticipo(){

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.layout_viaticos_editar_campos, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);


        final EditText ETdeposito = (EditText) promptsView.findViewById(R.id.te_monto_recibido);

        ETdeposito.setText(""+anticipo);


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Modificar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // accion dar click en aceptar
                                if(!ETdeposito.getText().toString().equals(""))
                                {anticipo=Double.parseDouble(ETdeposito.getText().toString());
                                new Activity_Gastos.EditarAnticipo().execute();}

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }


    public void showImagePopup() {

        CharSequence colors[] = new CharSequence[]{"Galeria", "Tomar Foto"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione una Opcion");
        builder.setCancelable(true);
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    iniciaGaleria();
                } else if (which == 1) {
                    iniciaCamaraFoto();
                }

               /* else if (which == 2) {

                }*/
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }


    private void ShowAlertTerminarViaje(){

        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        //adb.setView(alertDialogView);
        adb.setTitle("Alerta");
        adb.setMessage("Ya no podras Agregar Ningun Gasto, estas seguro que deseas Terminar este Viaje?");
        adb.setIcon(R.drawable.warning);

        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(isOnline()){ new Activity_Gastos.TerminaViaje().execute();}
                else{toastAlert("Sin conexion a Internet");}
            } });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                toastAlert("Cancelado");
            } });

        adb.show();
    }


    void iniciaGaleria() {
        // File System.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of file system options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.string_choose_image));
        startActivityForResult(chooserIntent, SELECCIONA_GALLERY);

    }


    void iniciaCamaraFoto() {
        AnncaConfiguration.Builder photo = new AnncaConfiguration.Builder(Activity_Gastos.this, SELECCIONA_FOTO);
        photo.setMediaAction(AnncaConfiguration.MEDIA_ACTION_PHOTO);
        photo.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_LOW);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        new Annca(photo.build()).launchCamera();

    }












public void SendFileconRetrofit(String idGasto){
    //imagePath


    if(enviarImagen || enviarPdf || enviarImagen){
        barProgressDialog = new ProgressDialog(Activity_Gastos.this);
        barProgressDialog.setTitle("Subiendo Archivo  ...");
        barProgressDialog.setMessage("Espere  ...");
        barProgressDialog.setCancelable(false);
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(100);
        barProgressDialog.show();
    }


    ServiceApiViaticos serviceApi = ServiceApiViaticos.retrofit.create(ServiceApiViaticos.class);

    if(enviarImagen){


        ArchivosAenviar=ArchivosAenviar+1;
        //File file = new File(imagePath);   Log.e(metodo+"SendFileRetrofit", "pathImage"+imagePath);


        //compresion de imagen
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 66,out);
        byte[] BYTE=out.toByteArray();
        File file = new File(context.getCacheDir(), new File(imagePath).getName());
        try{
        file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(BYTE);
            fos.flush();
            fos.close();
        }catch(Exception e){}



        String nombreArchivo = file.getName();
        ProgressRequestBody fileBody = new ProgressRequestBody(file,this);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file", nombreArchivo, fileBody);
        RequestBody idgasto = RequestBody.create(MediaType.parse("text/plain"),idGasto);



        Call<ResponseCall> call = serviceApi.sendMedios(filePart,idgasto);
        call.enqueue(new Callback<ResponseCall>() {
            @Override
            public void onResponse(Call<ResponseCall> call, Response<ResponseCall> response) {

                if (response.isSuccessful()) {
                    if (response.body().getResult().equals("success")) {

                        //Log.e("url", response.body().getFile());

                        MensajeEnConsola.logError(context,"SendFileconRetrofit","URL ="+ response.body().getFile());

                        //-- TODO: 10/03/2017 se recibe el url de la imagen enviada y se mada el json completo
                        // --sendMessageImage(response.body().getFile(), id, position);
                        //Log.e(metodo+"SendFileRetrofit", "imagen enviada");

                        MensajeEnConsola.logError(context,"SendFileRetrofit","imagen enviada");

                        tv_NameImage.setText("Imagen");enviarImagen=false;
                        btn_imagen.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_image2));
                        btn_deleteImage.setVisibility(View.GONE);


                          sendSucces();
                    } else {
                        //  Log.e("string_upload_fail", "no esta respondiendo bien");
                         failPorcess();
                       // Log.e(metodo+"SendFileRetrofit", "imagen NO enviada");
                        enviarImagen=false;
                        MensajeEnConsola.logError(context,"SendFileRetrofit","imagen NO enviada");
                    }

                } else {
                  //  Log.e("string_upload_fail", response.body().getFile());
                    MensajeEnConsola.logError(context,"SendFileRetrofit"," :" +response.body().getFile());

                    failPorcess();enviarImagen=false;
                }
            }

            @Override
            public void onFailure(Call<ResponseCall> call, Throwable t) {

               // Log.e("error cargar imagen", String.valueOf(t));
                //Log.e("url", String.valueOf(call.request().url()));

                MensajeEnConsola.logError(context,"SendFileRetrofit","error al cargar imagen"+String.valueOf(t)+" URL:"+String.valueOf(call.request().url()));

                barProgressDialog.dismiss();
                failPorcess();

            }
        });


    }

   if(enviarPdf) {

       ArchivosAenviar=ArchivosAenviar+1;

       File file2 = new File(pdfPath);
     //  Log.e(metodo + "SendFileRetrofit", "pathPDF" + pdfPath);



       String nombreArchivo2 = file2.getName();
       ProgressRequestBody fileBody2 = new ProgressRequestBody(file2, this);
       MultipartBody.Part filePart2 = MultipartBody.Part.createFormData("uploaded_file", nombreArchivo2, fileBody2);

       RequestBody idgasto2 = RequestBody.create(MediaType.parse("text/plain"), idGasto);

       Call<ResponseCallPdf> call2 = serviceApi.sendMedios2(filePart2,idgasto2);

       call2 = serviceApi.sendMedios2(filePart2, idgasto2);
       call2.enqueue(new Callback<ResponseCallPdf>() {
           @Override
           public void onResponse(Call<ResponseCallPdf> call, Response<ResponseCallPdf> response) {

               if (response.isSuccessful()) {
                   if (response.body().getResult().equals("success")) {
                       //Log.e("url", response.body().getFile());

                       MensajeEnConsola.logInfo(context,"SendFileRetrofit","URL "+ response.body().getFile());

                       // TODO: 10/03/2017 se recibe el url de la imagen enviada y se mada el json completo
                       // sendMessageImage(response.body().getFile(), id, position);

                       tv_NamePdf.setText("Pdf");enviarPdf=false;
                       btn_pdf.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_pdf2));
                       btn_deletePdf.setVisibility(View.GONE);

                       sendSucces();

                       //Log.e(metodo + "SendFileRetrofit", "pdf enviado");

                       MensajeEnConsola.logInfo(context,"SendFileRetrofit"," pdf enviado");

                   } else {
                       //Log.e("string_upload_fail", "no esta respondiendo bien");
                     //  Log.e(metodo + "SendFileRetrofit", "pdf NO enviado");

                       MensajeEnConsola.logError(context,"SendFileRetrofit"," pdf NO enviado");
                       enviarPdf=false;
                       failPorcess();
                   }
               } else {
                  // Log.e("string_upload_fail", response.body().getFile());
                   MensajeEnConsola.logError(context,"SendFileRetrofit",": "+ response.body().getFile());
                   failPorcess();enviarPdf=false;
               }
           }

           @Override
           public void onFailure(Call<ResponseCallPdf> call, Throwable t) {
               //Log.e("error cargar imagen", String.valueOf(t));
               //Log.e("url", String.valueOf(call.request().url()));

               MensajeEnConsola.logError(context,"SendFileRetrofit","error al cargar imagen"+String.valueOf(t)+"  URL"+ String.valueOf(call.request().url()));

               barProgressDialog.dismiss();
               failPorcess();

           }
       });

   }

   if(enviarXml) {
       ArchivosAenviar=ArchivosAenviar+1;
       File file3 = new File(xmlPath);
       //Log.e(metodo + "SendFileRetrofit", "pathXML" + xmlPath);



       String nombreArchivo3 = file3.getName();
       ProgressRequestBody fileBody3 = new ProgressRequestBody(file3, this);
       MultipartBody.Part filePart3 = MultipartBody.Part.createFormData("uploaded_file", nombreArchivo3, fileBody3);
       RequestBody idgasto3 = RequestBody.create(MediaType.parse("text/plain"), idGasto);

       Call<ResponseCallXml> call3 = serviceApi.sendMedios3(filePart3,idgasto3);
       call3 = serviceApi.sendMedios3(filePart3, idgasto3);
       call3.enqueue(new Callback<ResponseCallXml>() {
           @Override
           public void onResponse(Call<ResponseCallXml> call, Response<ResponseCallXml> response) {

               if (response.isSuccessful()) {
                   if (response.body().getResult().equals("success")) {
                       //  Log.e("url", response.body().getFile());
                       // TODO: 10/03/2017 se recibe el url de la imagen enviada y se mada el json completo
                       // sendMessageImage(response.body().getFile(), id, position);
                       // Log.e(metodo + "SendFileRetrofit", "xml enviado");

                       MensajeEnConsola.logInfo(context,"SendFileRetrofit"," xml enviado");

                       tv_NameXml.setText("Xml");enviarXml=false;
                       btn_xml.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_xml2));
                       btn_deleteXml.setVisibility(View.GONE);
                       sendSucces();
                   } else {
                       //Log.e("string_upload_fail", "no esta respondiendo bien");
                       failPorcess();enviarXml=false;

                       // Log.e(metodo + "SendFileRetrofit", "xml NO enviado");

                       MensajeEnConsola.logError(context,"SendFileRetrofit"," xml No enviado");
                   }
               } else {
                   Log.e("string_upload_fail", response.body().getFile());

                   MensajeEnConsola.logError(context,"SendFileRetrofit",": "+response.body().getFile());
                   failPorcess();
               }
           }

           @Override
           public void onFailure(Call<ResponseCallXml> call, Throwable t) {
               //Log.e("error cargar imagen", String.valueOf(t));
              //Log.e("url", String.valueOf(call.request().url()));

               MensajeEnConsola.logError(context,"SendFileRetrofit","error al cargar imagen "+String.valueOf(t)+" URL "+String.valueOf(call.request().url()));

               barProgressDialog.dismiss();
               failPorcess();

           }
       });

   }
}



//los sisguientes tres metodos son debido ha la implementacion de la clase de un elemento dentro del envio retrofit
    @Override
    public void onProgressUpdate(int percentage) {
       // Log.e("percentage", String.valueOf(percentage));
        barProgressDialog.setProgress(percentage);
    }

    @Override
    public void onError(Exception e) {
       // Log.e("error ", e+"");
        MensajeEnConsola.logError(context,"onError","error:"+e.toString());
    }

    @Override
    public void onFinish(String finish) {
      //  Log.e("finsish ", "se termino el proceso ");

        MensajeEnConsola.logError(context,"onFinish","se termino el proceso");

    }


    void sendSucces() {

       // Log.e(metodo+"SendSucces ", "antes archivos A enviar="+ArchivosAenviar);
        MensajeEnConsola.logError(context,"sendSucces","antes archivos A enviar="+ArchivosAenviar);

        if(ArchivosAenviar==1) {
            ArchivosAenviar=0;
            barProgressDialog.dismiss();
            AlertDialog.Builder alertaSimple = new AlertDialog.Builder(context);
            alertaSimple.setTitle("Envio correcto");
            alertaSimple.setMessage("El archivo se envio correctamente");
            alertaSimple.setPositiveButton(context.getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // finish();

                }
            });
     /*   alertaSimple.setNegativeButton("No, mas tarde", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });*/
            alertaSimple.setIcon(R.drawable.warning);
            alertaSimple.create();
            alertaSimple.show();
           if(isOnline()) new Activity_Gastos.ActualizaResumen(true).execute();


        }

        else{ArchivosAenviar=ArchivosAenviar-1;

        }

     //   Log.e(metodo+"SendSucces ", "despues archivos A enviar="+ArchivosAenviar);

        MensajeEnConsola.logError(context,"sendSucces","despues archivos A enviar="+ArchivosAenviar);

    }

    void failPorcess()
    {
        AlertDialog.Builder alertaSimple = new AlertDialog.Builder(context);
        alertaSimple.setTitle("Error al subir archivo");
        alertaSimple.setMessage("Ocurrio un error al subir el archivo");
        alertaSimple.setPositiveButton(context.getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
     /*   alertaSimple.setNegativeButton("No, mas tarde", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });*/
        alertaSimple.setIcon(R.drawable.warning);
        alertaSimple.create();
        alertaSimple.show();
    }



    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }


    private void alertas(String mensaje, int caso) {
        final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(this);
        alertaSimple.setTitle("Información");
        alertaSimple.setMessage(mensaje);
        alertaSimple.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        if(caso==1) alertaSimple.setIcon(R.drawable.icon_timeout);
        if(caso==2) alertaSimple.setIcon(R.drawable.ico_nowifi);
        if(caso==3) alertaSimple.setIcon(R.drawable.icon_error_sever);
        alertaSimple.create();
        alertaSimple.show();
    }

    private void toastAlert(String ms){
        Toast.makeText(getApplicationContext(),ms, Toast.LENGTH_LONG).show();}

}


