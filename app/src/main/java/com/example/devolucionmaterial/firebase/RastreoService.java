package com.example.devolucionmaterial.firebase;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.services.CheckService;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.devolucionmaterial.sharedpreferences.PreferencesVar.context;


/**
 * Created by Administrador on 14/12/2016.
 */

public class RastreoService extends Service {
    AsyncTaskHora asyncTaskHora;
    String lat, lon, message;
    android.location.Location localizacion;
    LocationListener locListener;
    LocationManager locManager;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(this, "Autosincronización Iniciada", Toast.LENGTH_SHORT).show();
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        localizacion = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (localizacion != null) {

            lat = String.valueOf(localizacion.getLatitude());
            lon = String.valueOf(localizacion.getLongitude());

        } else {
            //gps desactivado
        }

        locListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLocationChanged(android.location.Location localizacion) {
                lat = String.valueOf(localizacion.getLatitude());
                lon = String.valueOf(localizacion.getLongitude());
            }
        };

        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locListener);

        asyncTaskHora = new AsyncTaskHora(localizacion);
        asyncTaskHora.execute();
        //locManager.removeUpdates(locListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // inicia el servico
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //termin el servicio

        asyncTaskHora.cancel(true);
    }

    private class AsyncTaskHora extends AsyncTask<String, String, String> {
        Location location;

        public AsyncTaskHora(Location location) {
            this.location = location;
        }

        @Override
        protected String doInBackground(String... params) {
            locListener = new LocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onProviderEnabled(String provider) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onProviderDisabled(String provider) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onLocationChanged(android.location.Location localizacion) {
                    lat = String.valueOf(localizacion.getLatitude());
                    lon = String.valueOf(localizacion.getLongitude());
                }
            };
            return message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            if (lat != null && lon != null) {
                if (CheckService.internet(getBaseContext())) {
                    try {
                        sendLocation();
                    } catch (Exception e) {

                    }

                }
            }
            stopService(new Intent(getApplicationContext(), RastreoService.class));
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locManager.removeUpdates(locListener);
            //Toast.makeText(getApplicationContext(), values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

        }
    }

    private void sendLocation() {
        if (!BDVarGlo.getVarGlo(getBaseContext(), "INFO_USUARIO_ID").equals("") && BDVarGlo.getVarGlo(getBaseContext(), "INFO_USUARIO_ID") != null) {
            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<BeanResponse> call = serviceApi.ENVIAR_LOC_EQUIPO(
                    BDVarGlo.getVarGlo(getBaseContext(), "INFO_USUARIO_ID"),
                    lat, lon, getCurrentTimeStamp());
            call.enqueue(new Callback<BeanResponse>() {
                @Override
                public void onResponse(Call<BeanResponse> call, Response<BeanResponse> response) {

                }

                @Override
                public void onFailure(Call<BeanResponse> call, Throwable t) {

                }
            });
        }

    }

    public static String getCurrentTimeStamp() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return "";
        }
    }


}