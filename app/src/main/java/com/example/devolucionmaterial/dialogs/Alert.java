package com.example.devolucionmaterial.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ImageView;

import com.example.devolucionmaterial.activitys.AnuncioDelDia;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.fragments.FragmentSolicitudMaterial;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.services.CheckService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class Alert {
    static Vibrator vibrator;
    static String metodo;
    static String docPHPanuncio_hoy = "anuncio_hoy.php?opc=SOLICITUD&idusu=";
    static Context context;
    public static String jsonAdsid;
    public static String jsonAdsdireccion;
    static String jsonAdsimagenUsuario;

    @SuppressWarnings("deprecation")
    public static void Error(Context context, String mensajeLog, final String tituloError, final String cuerpoError) {
        metodo = mensajeLog + ".ERROR()";
        MensajeEnConsola.log(context, metodo, mensajeLog + "\n" + tituloError + "\n" + cuerpoError);
        try {
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(tituloError);
            alertDialog.setMessage(cuerpoError);
            alertDialog.setButton(context.getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setIcon(R.drawable.errorinternet);
            alertDialog.show();
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception en Alert.Error = " + e.getMessage());
        }
    }

    public static void Alerta(Context context, String metodo1, int n, String dato) {
        metodo = metodo1 + ".Alerta()";
        //MensajeEnConsola.log(context, metodo, "n = "+n+" -- dato = "+dato);
        try {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200);
            if (n == 0) {
                ToastManager.show(context, dato, ToastManager.INFORMATION);
            }
            if (n == 1) {
                ToastManager.show(context, dato, ToastManager.WARNING);
            }
            if (n == 2) {
                ToastManager.show(context, dato, ToastManager.ERROR);
            }
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception en Alert.Alerta = " + e.getMessage());
        }
    }

    public static void ActivaInternet(final Context context) {
        metodo = "Alert.ActivaInternet()";
        MensajeEnConsola.log(context, metodo, "Muestra mensaje de activar internet!!!");
        try {
            //Activa WIFI o DATOS
            final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(context);
            alertaSimple.setTitle(context.getResources().getString(R.string.sinInternet));
            alertaSimple.setMessage(context.getResources().getString(R.string.requiereWifioDatos));
            alertaSimple.setPositiveButton(context.getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Abrimos intent de SETTINGS del dispositivo
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            alertaSimple.setNegativeButton("No, mas tarde", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertaSimple.setIcon(R.drawable.warning);
            alertaSimple.create();
            alertaSimple.show();
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception en Alert.ActivaInternet = " + e.getMessage());
        }
    }


    public static void confirmacionSM(final Context context, String titulo, String desc, final int solicitud_refaccionid) {


        final AlertDialog.Builder alert = new AlertDialog.Builder(
                context);
        alert.setTitle(titulo);
        alert.setMessage(desc);
        alert.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Log.e("which", String.valueOf(which));

                        if (CheckService.internet(context))
                            FragmentSolicitudMaterial.conexionActulizar(solicitud_refaccionid);
                        else
                            Alert.ActivaInternet(context);



                    }
                });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });
        alert.show();
    }

    public static void Anuncio(Context context) {
        //Aqui revisamos si se mostrara el anuncio del dia
        //primero revisamos si le mostraremos el anuncio o no
        //trabaja en conjunto con el archivo PHP llamado ../Android/anuncion_hoy.php
        Alert.context = context;
        try {
            if (CheckService.internet(context)) {
                new RevizarAnuncioHoy().execute(MainActivity.url + docPHPanuncio_hoy + BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));

            }
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception en Alert.Anuncio = " + e.getMessage());
        }
    }

    static class RevizarAnuncioHoy extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            metodo = "RevizarAnuncioHoy<AsyncTask>doInBackground()";
            String regresa = "";
            try

            {
                JSONObject object=new JSONObject( "{'':''}");//esta linea debe quitarse
                /*se comenta para que no haga consultas a servidor  ya que no se esta usando por ahora

                JSONObject object = JSONparse.consultaURL(Alert.context, metodo, params[0]);
                assert object != null;
                if ("0".equals(object.getString("id"))) {
                    regresa = "NO";
                } else {
                    jsonAdsid = object.getString("id");
                    jsonAdsdireccion = object.getString("direccion");
                    regresa = "SI";
                }
                */

                regresa="NO";

            } catch (JSONException e) {
                regresa = "NO";
                MensajeEnConsola.log(context, metodo, "JSONException = " + e.getMessage());
            } catch (Exception e) {
                regresa = "NO";
                MensajeEnConsola.log(Alert.context, metodo, "Exception = " + e.getMessage());
            }

            return regresa;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            metodo = "RevizarAnuncioHoy<AsyncTask>onPostExecute()";
//            MensajeEnConsola.log(Alert.context, metodo, "Respuesta de String s = "+s);
            if ("SI".equals(s)) {
                imagenesCargadas = 0;
                PreCargaImagenes();
            }
//            else if("NO".equals(s)){
//                //cierra el intent y como si no hubiera pasado nada
//                AnuncioDelDia.this.finish();
//            }
        }

        int imagenesCargadas;

        void PreCargaImagenes() {
            metodo = "PreCargaImagenes()";
            final ImageView imagen1 = new ImageView(Alert.context);
//            Picasso.with(context).load(R.drawable.error).memoryPolicy(MemoryPolicy.NO_CACHE);
            Picasso.with(Alert.context)
                    .load(R.drawable.error)
                    .placeholder(R.drawable.error)
                    .error(R.drawable.error)
                    .into(imagen1, new Callback() {
                        @Override
                        public void onSuccess() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    imagenesCargadas++;
                                    PostCargaImagenes();
                                    MensajeEnConsola.log(Alert.context, metodo, "Cargo imagen 1");
                                }
                            }, 100);
                        }

                        @Override
                        public void onError() {
                        }
                    });
            final ImageView imagen2 = new ImageView(Alert.context);
//            Picasso.with(context).load(jsonAdsdireccion).memoryPolicy(MemoryPolicy.NO_CACHE);
            Picasso.with(Alert.context)
                    .load(jsonAdsdireccion)
                    .placeholder(R.drawable.dev)
                    .error(R.drawable.devolucion)
                    .into(imagen2, new Callback() {
                        @Override
                        public void onSuccess() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    imagenesCargadas++;
                                    PostCargaImagenes();
                                    MensajeEnConsola.log(Alert.context, metodo, "Cargo imagen 2");
                                }
                            }, 100);
                        }

                        @Override
                        public void onError() {
                        }
                    });
            final ImageView imagen3 = new ImageView(Alert.context);
//            Picasso.with(context).load(jsonAdsimagenUsuario).memoryPolicy(MemoryPolicy.NO_CACHE);
            Picasso.with(Alert.context)
                    .load(jsonAdsimagenUsuario)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.cc)
                    .resize(100, 100)
                    .into(imagen3, new Callback() {
                        @Override
                        public void onSuccess() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    imagenesCargadas++;
                                    PostCargaImagenes();
                                    MensajeEnConsola.log(Alert.context, metodo, "Cargo imagen 3");
                                }
                            }, 100);
                        }

                        @Override
                        public void onError() {
                        }
                    });
            final ImageView imagen4 = new ImageView(Alert.context);
            final ImageView imagen5 = new ImageView(Alert.context);
        }

        void PostCargaImagenes() {
            if (imagenesCargadas >= 2) {//2 significa el numero de imagenes ocupadas
                MensajeEnConsola.log(Alert.context, "PostCargaImagenes()", "Imagenes Cargadas, se continua a mostrar anuncio del dia");
                Alert.context.startActivity(new Intent(Alert.context, AnuncioDelDia.class));
            } else {
                MensajeEnConsola.log(Alert.context, "PostCargaImagenes()", "Faltan imagenes por cargar... en espera...");
            }
        }
    }
}