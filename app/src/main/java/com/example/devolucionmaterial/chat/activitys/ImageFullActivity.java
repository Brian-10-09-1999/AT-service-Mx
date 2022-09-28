package com.example.devolucionmaterial.chat.activitys;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.utils.TouchImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageFullActivity extends AppCompatActivity {
    private TouchImageView mImageView;
    private ImageView mImageViewFull;
    private ProgressDialog progressDialog;
    String urlPhotoClick;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full);
        urlPhotoClick = getIntent().getExtras().getString("url");
        initToolbar();
        initSetUp();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Imagen completa");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    void initSetUp() {
        mImageView = (TouchImageView) findViewById(R.id.adsi_imageView);
        mImageViewFull = (ImageView) findViewById(R.id.adsi_imageFull);
        progressDialog = new ProgressDialog(this);
       /* Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        File image = new File(urlPhotoClick);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap,width,height,true);


        mImageView.setImageBitmap(bitmap);*/

        // TODO: 17/04/2017 se pone la restificon de versiopnes por la imagen no se infla entodas la versiones
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                File image = new File(urlPhotoClick);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                mImageView.setImageBitmap(bitmap);
            } catch (Exception e) {

            }
        }

     /*   try{
            File image = new File(urlPhotoClick);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            mImageView.setImageBitmap(bitmap);
        }catch (Exception e){

        }*/

// TODO: 27/02/2017 se obtiene la resolcuion de la pantalla  para visualizar la imagen
        Glide.with(this).load(urlPhotoClick).asBitmap().into(new SimpleTarget<Bitmap>() {

            @Override
            public void onLoadStarted(Drawable placeholder) {
                progressDialog.setMessage("Cargando Imagen...");
                progressDialog.show();
            }

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                progressDialog.dismiss();
                mImageView.setImageBitmap(resource);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                Toast.makeText(ImageFullActivity.this, "Error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }


}
