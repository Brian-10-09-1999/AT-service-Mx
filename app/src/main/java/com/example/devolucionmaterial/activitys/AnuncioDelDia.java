package com.example.devolucionmaterial.activitys;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class AnuncioDelDia extends Activity{
    String metodo;
    Context context;
    Cursor cursor;
    BDmanager manager;
    TextView tvNombreUser, tvMensajeUser;
    ImageView btnClose, imgFotoUser;
    String jsonAdsid, jsonAdsdireccion;
    //LinearLayout imgFondo;
    ImageView imgFondo;
    String docPHPanuncio_hoy = "anuncio_hoy.php?opc=VISTO&idusu=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anuncio_del_dia);
        context = this;
        setupAnuncioDelDias();
    }
    void setupAnuncioDelDias(){
        metodo = "setupAnuncioDelDias()";
        MensajeEnConsola.log(context, metodo, "");
        manager = new BDmanager(context);

        tvNombreUser = (TextView) findViewById(R.id.tvadsdayNombreUsuario);
        tvMensajeUser = (TextView) findViewById(R.id.tvadsdayMensaje);
        imgFotoUser = (ImageView) findViewById(R.id.imgadsdayFotoUsuario);
        //imgFondo = (LinearLayout) findViewById(R.id.lladsday);
        imgFondo = (ImageView) findViewById(R.id.imgFondo);
        btnClose = (ImageView) findViewById(R.id.imgadsdayCerrar);

        tvNombreUser.setText("");
        tvMensajeUser.setText("");
        imgFotoUser.setImageDrawable(getResources().getDrawable(R.drawable.icon_v));

        jsonAdsid = Alert.jsonAdsid;
        jsonAdsdireccion = Alert.jsonAdsdireccion;

        PonerImagenBntCerrar();
        PonerImagenFondo(jsonAdsdireccion);
        //PonerImagenFotoUsuario("http://www.iceni.com/images/boy.png");
        //PonerNombre(MainActivity.nombreusuario);
        //PonerMensaje("Este es un mensaje solo de prueba, para cambiarlo diriguite a AnuncioDelDia.class dentro de este mismo paquete");

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Aqui hacer lo necesario y/o relacionado a contar y cerrar los anuncios del dia
                RegistraAdsVISTO();
                finish();
            }
        });

        //OpcionesPopPup_Anuncio();
        MensajeEnConsola.log(context, metodo, "Se mostro Anuncio del día!!!");
    }
    void PonerImagenBntCerrar(){
        Picasso.with(this)
                .load(R.drawable.error)
                .placeholder(R.drawable.error)
                .error(R.drawable.error)
                .into(btnClose, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError() {}
                });
        Picasso.with(this).load(R.drawable.error).memoryPolicy(MemoryPolicy.NO_CACHE);//borra la cache para que este libre despues de mostrarse y listo para el siguiente
    }
    public void PonerImagenFondo(String URLimagen){
        final ImageView postfile = new ImageView(this);
        Picasso.with(this)
                .load(URLimagen)
                .placeholder(R.drawable.dev)
                .error(R.drawable.devolucion)
                .into(postfile, new Callback() {
                    @Override
                    public void onSuccess() {
                        new Handler().postDelayed(new Runnable() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void run() {
                                imgFondo.setBackground(postfile.getDrawable());
                            }
                        }, 100);
                    }
                    @Override
                    public void onError() {}
                });
        Picasso.with(this).load(URLimagen).memoryPolicy(MemoryPolicy.NO_CACHE);//borra la cache para que este libre despues de mostrarse y listo para el siguiente
    }
    void PonerImagenFotoUsuario(String URLimagen){
        Picasso.with(this)
                .load(URLimagen)
                .placeholder(R.drawable.user)
                .error(R.drawable.cc)
                .resize(100, 100)
                .transform(new transformImgCir())
                .into(imgFotoUser, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError() {}
                });
        Picasso.with(this).load(URLimagen).memoryPolicy(MemoryPolicy.NO_CACHE);//borra la cache para que este libre despues de mostrarse y listo para el siguiente
    }
    void PonerNombre(String nombre){
        tvNombreUser.setText(nombre);
        tvNombreUser.setTextColor(Color.BLACK);
    }
    void PonerMensaje(String mensaje){
        tvMensajeUser.setText(mensaje);
        tvMensajeUser.setTextColor(Color.BLACK);
    }
    void OpcionesPopPup_Anuncio(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int ancho = displayMetrics.widthPixels;//extrae el ancho de la pantalla en pixeles
        int alto = displayMetrics.heightPixels;//extrae el alto de la pantalla en pixeles
        getWindow().setLayout((int)(ancho*.8), (int)(alto*.6));//cambia el tamaño del Layaut de la actividad
    }
    //detectado los botones del telefono
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            RegistraAdsVISTO();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    void RegistraAdsVISTO(){
        MensajeEnConsola.log(context, metodo, "Cerro el Anuncio del día!!!");
        WebView visorOculto = (WebView) findViewById(R.id.visorWebOculto);
        visorOculto.loadUrl(MainActivity.url+docPHPanuncio_hoy+ BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID")+"&id_visto="+jsonAdsid);
    }
    public static class transformImgCir implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            if (source == null || source.isRecycled()) {
                return null;
            }

            int borderwidth = 10;
            final int width = source.getWidth() + borderwidth + borderwidth;
            final int height = source.getHeight() + borderwidth + borderwidth;

            Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            BitmapShader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(shader);

            Canvas canvas = new Canvas(canvasBitmap);
            float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
            canvas.drawCircle(width / 2, height / 2, radius, paint);

            //border code
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(borderwidth);
            canvas.drawCircle(width / 2, height / 2, radius - borderwidth / 2, paint);
            //--------------------------------------

            if (canvasBitmap != source) {
                source.recycle();
            }
            return canvasBitmap;
        }
        @Override
        public String key() {
            return "circle";
        }
    }
}