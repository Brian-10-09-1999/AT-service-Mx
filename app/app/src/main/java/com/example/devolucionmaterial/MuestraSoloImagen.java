package com.example.devolucionmaterial;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MuestraSoloImagen extends Activity{
    private static final int TAMANO_ANCHO = 1024;
    private static final int TAMANO_ALTO = 768;
    String metodo;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_muestra_solo_imagen);
        metodo = "";

        ImageView imagenNormal = (ImageView) findViewById(R.id.imgMuestraSoloImagen);
        final CustomImageView imageCustom = (CustomImageView) findViewById(R.id.customMuestraSoloImagen);
        Intent intReceptor = this.getIntent();
        Bundle bndReceptor = intReceptor.getExtras();
        String file_name = bndReceptor.getString("imagen");

        try {
            //MensajeEnConsola.log(context, metodo, "file_name imagen sola = "+file_name);
            final ImageView postfile = new ImageView(this);
            Picasso.with(context)
                    .load(file_name)
                    .placeholder(android.R.drawable.stat_sys_download).error(R.drawable.ic_no_imagen)
                    //.resize(TAMANO_ANCHO, TAMANO_ALTO)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(postfile, new Callback() {
                        @Override
                        public void onSuccess() {
                            new Handler().postDelayed(new Runnable() {
                                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void run() {
                                    imageCustom.setBitmap(((BitmapDrawable)postfile.getDrawable()).getBitmap());
                                }
                            }, 100);
                        }
                        @Override
                        public void onError() {}
                    });
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception valor e = " + e.getMessage());
            this.finish();
        }


        /*
        try {
            //MensajeEnConsola.log(context, metodo, "file_name imagen sola = "+file_name);
            Picasso.with(context).load(file_name).placeholder(android.R.drawable.stat_sys_download).error(R.drawable.ic_no_imagen)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imagen);
            //transforma la imagen para poderla manipular
            CustomImageView mImageView = (CustomImageView) findViewById(R.id.customMuestraSoloImagen);
            Bitmap bitmap = ((BitmapDrawable) imagen.getDrawable()).getBitmap();
            mImageView.setBitmap(bitmap);
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception valor e = " + e.getMessage());
            this.finish();
        }
        * */
    }
}
