package com.example.devolucionmaterial.activitys.osSeccion;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.Request;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.ImageOS;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.odn.qr_manager.model.Response;
import com.thanosfisherman.mayi.PermissionBean;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.services.concurrency.internal.DefaultRetryPolicy;
import io.fabric.sdk.android.services.concurrency.internal.RetryPolicy;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Brian Martinez on 21/06/2022
 */

public class CameraSalaFotoActivity extends BaseActivity  {

    private Intent intReceptor;
    private Bundle bndReceptor, bndMuestraImagen;
    private String usuarioID, nombreDeUsuario, OSid, tipoDeVistaOS, jsonResult;
    private String comentariosParaServidor, apellido;
    private TextView tvUsuario, tvFolio, tvSala, tvActividad, tvIniAct, tvFinAct;
    private Button btn_maquina_frontal_add;
    private Button btn_maquina_lateral_add;
    private Button btn_maquina_alineacion_posapies_add;
    private Button btn_maquina_espacio_maquinas_add;
    private Button btn_maquina_peinado_general_add;
    private Button btn_finales_isla_vista_frontal_add;
    private Button btn_finales_isla_vista_lateral_add;
    private Button btn_sing_alineacion_add;
    private Button btn_sing_pantallas_add;
    private Button btn_sing_estabilizadores_add;
    private Button btn_peinado_rack_add;
    private Button btn_operacion_APC_add;

    ImageView IDProf;

    public static String ESTATUS_MAQUINAS = "20";
    public static String ESTATUS_FINALES_ISLAS = "21";
    public static String ESTATUS_SIGN = "22";
    public static String ESTATUS_SITE = "23";

    /*public static String DESCRIPCION_FRONTAL_MAQUINAS = String.valueOf(R.string.mq_vista_frontal);
    public static String DESCRIPCION_LATERAL_MAQUINAS = String.valueOf(R.string.mq_vista_lateral);
    public static String DESCRIPCION_REPOSAPIES_MAQUINAS = String.valueOf(R.string.mq_alineacion_posapies);
    public static String DESCRIPCION_ESPACIO_MAQUINAS = String.valueOf(R.string.mq_espacio_entre_maquinas);
    public static String DESCRIPCION_PEINADO_MAQUINAS = String.valueOf(R.string.mq_peinado_general_isla);
    public static String DESCRIPCION_VISTA_FRONTAL_FISLAS = String.valueOf(R.string.mq_vista_frontal_d_i);
    public static String DESCRIPCION_VISTA_LATERAL_FISLAS = String.valueOf(R.string.mq_vista_lateral_d_i);
    public static String DESCRIPCION_ALINEACION_SIGN = String.valueOf(R.string.mq_sign_alineacion);
    public static String DESCRIPCION_PANTALLAS_SIGN = String.valueOf(R.string.mq_sign_pantallas);
    public static String DESCRIPCION_ESTABILIZADORES_SIGN = String.valueOf(R.string.mq_estabilizadoes);
    public static String DESCRIPCION_PEINADO_RACK_SITE = String.valueOf(R.string.mq_peinado_rack);
    public static String DESCRIPCION_OPERACION_SITE = String.valueOf(R.string.mq_operacion_APC);
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_fotos);//activity_info_actividad_os);
        initToolbar("Doc Fotografica",true,true);
        context = this;
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.contraint_layout_camera);

        btn_maquina_frontal_add =  (Button) findViewById(R.id.btn_maquina_frontal);
        btn_maquina_lateral_add =  (Button) findViewById(R.id.btn_maquina_lateral);
        btn_maquina_alineacion_posapies_add =  (Button) findViewById(R.id.btn_maquina_alineacion_posapies);
        btn_maquina_espacio_maquinas_add =  (Button) findViewById(R.id.btn_maquina_espacio_maquinas);
        btn_maquina_peinado_general_add =  (Button) findViewById(R.id.btn_maquina_peinado_general);
        btn_finales_isla_vista_frontal_add =  (Button) findViewById(R.id.btn_finales_isla_vista_frontal);
        btn_finales_isla_vista_lateral_add =  (Button) findViewById(R.id.btn_finales_isla_vista_lateral);
        btn_sing_alineacion_add =  (Button) findViewById(R.id.btn_sing_alineacion);
        btn_sing_pantallas_add =  (Button) findViewById(R.id.btn_sing_pantallas);
        btn_sing_estabilizadores_add =  (Button) findViewById(R.id.btn_sing_estabilizadores);
        btn_peinado_rack_add =  (Button) findViewById(R.id.btn_peinado_rack);
        btn_operacion_APC_add =  (Button) findViewById(R.id.btn_operacion_APC);

        /**17**/
        btn_maquina_frontal_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("Alineación de isla vista frontal");
            }
        });
        btn_maquina_lateral_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("Alineación de isla vista lateral");
            }
        });
        btn_maquina_alineacion_posapies_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("Alineación de reposapiés");
            }
        });
        btn_maquina_espacio_maquinas_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("Espacio entre máquinas");
            }
        });
        btn_maquina_peinado_general_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("Peinado general de isla");
            }
        });

        /**18**/
        btn_finales_isla_vista_frontal_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("Vista frontal (derecha e/o izquierda)");
            }
        });
        btn_finales_isla_vista_lateral_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("Vista lateral (derecha e/o izquierda)");
            }
        });

        /**19**/
        btn_sing_alineacion_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("Alineación del sign con respecto a la isla");
            }
        });
        btn_sing_pantallas_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("Alineación de pantallas");
            }
        });
        btn_sing_estabilizadores_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("Estabilizadores");
            }
        });

        /**20**/
        btn_peinado_rack_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("Peinado de rack");
            }
        });
        btn_operacion_APC_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("Operación de APC");
            }
        });
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
        for (int i = 0; i < permissions.length; i++) {
            if (!permissions[i].isGranted()) {
                val = false;
            }
        }
        if (val) {
            //initSetUp();
        } else {
            showSnackBarPermission();
        }

    }

    double aleatorio = new Double(Math.random() * 100).intValue();
    String foto = Environment.getExternalStorageDirectory() + "/imagen/"+ aleatorio +".jpg";
    private  static final int TAKE_PICTURE=1;


    private void selectImage(String typeImage) {
        final CharSequence[] options = { "Tomar fotografia"/* ,"Agregar foto de la galeria"*/,"Cancelar" };
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(CameraSalaFotoActivity.this);
        builder.setTitle("Agrega una Foto!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Tomar fotografia"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                /*else if (options[item].equals("Agregar foto de la galeria"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }*/
                else if (options[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**Retrofit*/
    /*void startCompartirServidor() {
        metodo = "setupCompartir()";

        if (!files.isEmpty()) {
            pDialog= new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.Conectando_con_servidor_remoto))
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();

            String[] temp = MainActivity.url.split("Android/");
            String dominio = temp[0];
            RequestBody folio = RequestBody.create(MediaType.parse("text/plain"), tvFolio.getText().toString());
            RequestBody idusu = RequestBody.create(MediaType.parse("text/plain"), BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
            RequestBody urlAbsoluta = RequestBody.create(MediaType.parse("text/plain"), dominio);


            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<ImageOS> call = serviceApi.uploadSalaFotoOS(files, folio, nombreAli, estatus,idusu);
            call.enqueue(new Callback<ImageOS>() {
                @Override
                public void onResponse(Call<ImageOS> call, retrofit2.Response<ImageOS> response) {
                    pDialog.dismiss();
                    files.clear();
                    for (int i = 0; i < response.body().getListUrl().size(); i++) {
                        // regresaURLimagen = response.body().getListUrl().get(i);
                        //procesoImagenAsComentario();
                    }
                }
                @Override
                public void onFailure(Call<ImageOS> call, Throwable t) {files.clear();
                    files.clear();
                    pDialog.dismiss();
                    Toast.makeText(context, "ERROR al subir imagen", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(context, "Toma una Foto o seleciona de Galeria!!!", Toast.LENGTH_SHORT).show();
        }
    }*/
    /**ADD**/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                    //bitmap=getResizedBitmap(bitmap, 400);
                        IDProf.setImageBitmap(bitmap);
                    //BitMapToString(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                //thumbnail=getResizedBitmap(thumbnail, 400);
                Log.w("path of image from gallery......******************.........", picturePath+"");
                IDProf.setImageBitmap(thumbnail);
                //BitMapToString(thumbnail);
            }
        }
    }
   /* public String BitMapToString(Bitmap userImage1) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userImage1.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        Document_img1 = Base64.encodeToString(b, Base64.DEFAULT);
        return Document_img1;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void SendDetail() {
        final ProgressDialog loading = new ProgressDialog(Uplode_Reg_Photo.this);
        loading.setMessage("Please Wait...");
        loading.show();
        loading.setCanceledOnTouchOutside(false);
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfiURL.Registration_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            loading.dismiss();
                            Log.d("JSON", response);

                            JSONObject eventObject = new JSONObject(response);
                            String error_status = eventObject.getString("error");
                            if (error_status.equals("true")) {
                                String error_msg = eventObject.getString("msg");
                                ContextThemeWrapper ctw = new ContextThemeWrapper( Uplode_Reg_Photo.this, R.style.Theme_AlertDialog);
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                                alertDialogBuilder.setTitle("Vendor Detail");
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setMessage(error_msg);
                                alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                                alertDialogBuilder.show();

                            } else {
                                String error_msg = eventObject.getString("msg");
                                ContextThemeWrapper ctw = new ContextThemeWrapper( Uplode_Reg_Photo.this, R.style.Theme_AlertDialog);
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                                alertDialogBuilder.setTitle("Registration");
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setMessage(error_msg);
//                                alertDialogBuilder.setIcon(R.drawable.doubletick);
                                alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent=new Intent(Uplode_Reg_Photo.this,Log_In.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alertDialogBuilder.show();
                            }
                        }catch(Exception e){
                            Log.d("Tag", e.getMessage());

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            ContextThemeWrapper ctw = new ContextThemeWrapper( Uplode_Reg_Photo.this, R.style.Theme_AlertDialog);
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                            alertDialogBuilder.setTitle("No connection");
                            alertDialogBuilder.setMessage(" Connection time out error please try again ");
                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            alertDialogBuilder.show();
                        } else if (error instanceof AuthFailureError) {
                            ContextThemeWrapper ctw = new ContextThemeWrapper( Uplode_Reg_Photo.this, R.style.Theme_AlertDialog);
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                            alertDialogBuilder.setTitle("Connection Error");
                            alertDialogBuilder.setMessage(" Authentication failure connection error please try again ");
                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            alertDialogBuilder.show();
                            //TODO
                        } else if (error instanceof ServerError) {
                            ContextThemeWrapper ctw = new ContextThemeWrapper( Uplode_Reg_Photo.this, R.style.Theme_AlertDialog);
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                            alertDialogBuilder.setTitle("Connection Error");
                            alertDialogBuilder.setMessage("Connection error please try again");
                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            alertDialogBuilder.show();
                            //TODO
                        } else if (error instanceof NetworkError) {
                            ContextThemeWrapper ctw = new ContextThemeWrapper( Uplode_Reg_Photo.this, R.style.Theme_AlertDialog);
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                            alertDialogBuilder.setTitle("Connection Error");
                            alertDialogBuilder.setMessage("Network connection error please try again");
                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            alertDialogBuilder.show();
                            //TODO
                        } else if (error instanceof ParseError) {
                            ContextThemeWrapper ctw = new ContextThemeWrapper( Uplode_Reg_Photo.this, R.style.Theme_AlertDialog);
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                            alertDialogBuilder.setTitle("Error");
                            alertDialogBuilder.setMessage("Parse error");
                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            alertDialogBuilder.show();
                        }
//                        Toast.makeText(Login_Activity.this,error.toString(), Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put(KEY_User_Document1,Document_img1);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(mRetryPolicy);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onClick(View v) {
        if (Document_img1.equals("") || Document_img1.equals(null)) {
            ContextThemeWrapper ctw = new ContextThemeWrapper( Uplode_Reg_Photo.this, R.style.Theme_AlertDialog);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
            alertDialogBuilder.setTitle("Id Prof Can't Empty ");
            alertDialogBuilder.setMessage("Id Prof Can't empty please select any one document");
            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            alertDialogBuilder.show();
            return;
        }
        else{

            if (AppStatus.getInstance(this).isOnline()) {
                SendDetail();


                //           Toast.makeText(this,"You are online!!!!",Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this,"You are not online!!!!", Toast.LENGTH_LONG).show();
                Log.v("Home", "############################You are not online!!!!");
            }

        }
    }
}*/



}