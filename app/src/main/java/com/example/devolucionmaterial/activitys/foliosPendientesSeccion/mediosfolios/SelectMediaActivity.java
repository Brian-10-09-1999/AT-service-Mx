package com.example.devolucionmaterial.activitys.foliosPendientesSeccion.mediosfolios;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.annca.Annca;
import com.example.annca.internal.configuration.AnncaConfiguration;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.chat.api.ProgressRequestBody;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectMediaActivity extends AppCompatActivity implements View.OnClickListener, ProgressRequestBody.UploadCallbacks {

    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    private final String EXTENCION_IMAGEN = ".jpg";

    private final String IMAGEN_TEMPORAL = "temporal" + timeStamp + "" + EXTENCION_IMAGEN;
    private final int SELECCIONA_GALLERY = 100;
    private final int SELECCIONA_FOTO = 200;
    private static final int SELECCIONA_VIDEO = 300;

    private Context context;
    String imagePath;
    int type;
    View parentView;
    ImageView imageView;
    TextView textView;
    int numeroAleatorio;

    ProgressDialog barProgressDialog;


    private String folio;
    private String idUsuario;
    private int pais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_media);
        context = this;
        initToolbar();
        initSetup();
        if (getIntent().getExtras() != null) {
            folio = getIntent().getExtras().getString("folio");
            idUsuario = getIntent().getExtras().getString("idusuario");
            pais = getIntent().getExtras().getInt("pais");
            String delimiter = "-";
            String[] temp = folio.split(delimiter);
            folio = temp[1];


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECCIONA_GALLERY) {
            if (data == null) {
                Snackbar.make(parentView, R.string.string_unable_to_pick_image, Snackbar.LENGTH_INDEFINITE).show();
                return;
            }
            Uri selectedImageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagePath = cursor.getString(columnIndex);
                type = 1;
                Log.e("imagePath", imagePath);

                Glide.with(context).load(new File(imagePath)).into(imageView);

                Snackbar.make(parentView, R.string.string_reselect, Snackbar.LENGTH_LONG).show();
                cursor.close();

                textView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                Snackbar.make(parentView, R.string.string_unable_to_load_image, Snackbar.LENGTH_LONG).show();
            }
        }
        //tomar foto ok
        if (resultCode == RESULT_OK && requestCode == SELECCIONA_FOTO) {

            String path = data.getStringExtra(AnncaConfiguration.Arguments.FILE_PATH);
            Log.e("paht", path);
            // String path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO + File.separator + IMAGEN_TEMPORAL;
            //Log.e("path", path);
            imagePath = path;
            type = 1;
            Glide.with(context).load(new File(path)).into(imageView);
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Snackbar.make(parentView, R.string.string_reselect, Snackbar.LENGTH_LONG).show();

        }
        if (resultCode == RESULT_OK && requestCode == SELECCIONA_VIDEO) {

            String path = data.getStringExtra(AnncaConfiguration.Arguments.FILE_PATH);
            Log.e("paht", path);
            //sendVideo(path);
            // String path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO + File.separator + IMAGEN_TEMPORAL;
            Log.e("path", path);
            imagePath = path;
            type = 2;
            Glide.with(context).load(new File(path)).into(imageView);
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Snackbar.make(parentView, R.string.string_reselect, Snackbar.LENGTH_LONG).show();

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_upload:
                if (!TextUtils.isEmpty(imagePath)) {
                    /**
                     * Uploading AsyncTask
                     */
                    senFile(imagePath, type);
                } else {
                    Snackbar.make(parentView, R.string.string_message_to_attach_file, Snackbar.LENGTH_INDEFINITE).show();
                }
                break;
        }

    }

    void initSetup() {
        // TODO: 08/03/2017  mCommChListner es la interface ára aactualizar el rv desde esta actividad

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        parentView = findViewById(R.id.parentView);
        numeroAleatorio = (int) (Math.random() * 95000546 + 1);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_upload);
        fab.setOnClickListener(this);
    }

    void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mandar imagen");
        }
    }

    void iniciaCamaraFoto() {
        AnncaConfiguration.Builder photo = new AnncaConfiguration.Builder(SelectMediaActivity.this, SELECCIONA_FOTO);
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

    void iniciarCameraVideo() {
        AnncaConfiguration.Builder videoLimited = new AnncaConfiguration.Builder(SelectMediaActivity.this, SELECCIONA_VIDEO);
        videoLimited.setMediaAction(AnncaConfiguration.MEDIA_ACTION_VIDEO);
        videoLimited.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_AUTO);
        //videoLimited.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_HIGHEST);
        videoLimited.setVideoFileSize(10 * 1024 * 1024);
        videoLimited.setMinimumVideoDuration(5 * 60 * 1000);
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
        new Annca(videoLimited.build()).launchCamera();
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


    public void showImagePopup(View view) {

        CharSequence colors[] = new CharSequence[]{"Galeria", "Foto", "Video"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione una");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    iniciaGaleria();
                } else if (which == 1) {
                    iniciaCamaraFoto();
                } else if (which == 2) {
                    iniciarCameraVideo();
                }
            }
        });
        builder.show();
    }


    private void senFile(String imagePath, int tipo) {

        barProgressDialog = new ProgressDialog(SelectMediaActivity.this);

        barProgressDialog.setTitle("Subiendo Archivo  ...");
        barProgressDialog.setMessage("Espere  ...");
        barProgressDialog.setCancelable(false);
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(100);
        barProgressDialog.show();

        File file = new File(imagePath);
        /**
         * se agrega un numero alaeatorio a las fots para que no cause probelmas a la hora de enviar
         **/
        String nombreArchivo = file.getName();
        /**
         * se agrega l aimagen a curpo  para enviarlo por retrofit
         * */
        ProgressRequestBody fileBody = new ProgressRequestBody(file, this);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file", nombreArchivo, fileBody);

        RequestBody rFolio = RequestBody.create(MediaType.parse("text/plain"), folio);
        RequestBody rIdUsuario = RequestBody.create(MediaType.parse("text/plain"), idUsuario);
        RequestBody rPais = RequestBody.create(MediaType.parse("text/plain"), "1");
        RequestBody rTipo = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(tipo));

        /**
         * se crea la instacia de retorit
         * */
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<ResponseCall> call = serviceApi.sendMedios(filePart, rFolio, rIdUsuario, rPais, rTipo);
        call.enqueue(new Callback<ResponseCall>() {
            @Override
            public void onResponse(Call<ResponseCall> call, Response<ResponseCall> response) {
                barProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getResult().equals("success")) {
                        Log.e("url", response.body().getFile());
                        // TODO: 10/03/2017 se recibe el url de la imagen enviada y se mada el json completo
                        // sendMessageImage(response.body().getFile(), id, position);

                        sendSucces();


                    } else {
                        Log.e("string_upload_fail", "no esta respondiendo bien");
                        failPorcess();

                    }

                } else {
                    Log.e("string_upload_fail", response.body().getFile());
                    failPorcess();
                }
            }

            @Override
            public void onFailure(Call<ResponseCall> call, Throwable t) {
                Log.e("error cargar imagen", String.valueOf(t));
                Log.e("url", String.valueOf(call.request().url()));
                barProgressDialog.dismiss();
                failPorcess();

            }
        });

    }


    @Override
    public void onProgressUpdate(int percentage) {
        Log.e("percentage", String.valueOf(percentage));
        barProgressDialog.setProgress(percentage);

    }

    @Override
    public void onError(Exception e) {
        Log.e("error ", e+"");
    }

    @Override
    public void onFinish(String finish) {
        Log.e("finsish ", "se termino el proceso ");
    }

    void sendSucces() {
        AlertDialog.Builder alertaSimple = new AlertDialog.Builder(context);
        alertaSimple.setTitle("Envio correcto");
        alertaSimple.setMessage("El archivo se envio correctamente");
        alertaSimple.setPositiveButton(context.getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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

    void failPorcess() {
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
}
