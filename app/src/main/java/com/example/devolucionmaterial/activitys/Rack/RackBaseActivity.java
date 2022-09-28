package com.example.devolucionmaterial.activitys.Rack;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.annca.Annca;
import com.example.annca.internal.configuration.AnncaConfiguration;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.Rack.RackFragments.DetallesDeRack;
import com.example.devolucionmaterial.activitys.Rack.RackFragments.DetallesSubSala;
import com.example.devolucionmaterial.activitys.Rack.RackFragments.ListaHardware;
import com.example.devolucionmaterial.activitys.Rack.RackFragments.Listenerfragments;
import com.example.devolucionmaterial.activitys.codigoqr.LectorQRActivity;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.File;

public class RackBaseActivity extends AppCompatActivity implements Listenerfragments {

    FragmentManager manager =getFragmentManager();
    final private String metodo="Rack.RackBaseActivity";

    protected Toolbar toolbar;
    private boolean enabledMenu;
    public static int InPantalla=1;

    private final int SELECCIONA_FOTO = 200;
    private final int SELECCIONA_QR = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rack_base);
        initToolbar("Levantamiento de Hardware", true, true);



        addFragListaHardware();
        //addFragDetallesSubsala("juancho");
        //addFragDetallesDeRack();
    }


    protected void initToolbar(String title, Boolean homeAsUpEnabled, Boolean enabledMenu) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setLogo(R.drawable.icon_mis_viajes);
        this.enabledMenu = enabledMenu;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            if (homeAsUpEnabled)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setTitle(title);
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
          if(InPantalla!=1){  addFragListaHardware();InPantalla=1;}
          else finish();
        }
        return super.onOptionsItemSelected(item);

    }









    public void addFragDetallesSubsala(String name){

        Bundle bundle =new Bundle();
        bundle.putString("name",name);

        DetallesSubSala FragmentA=new DetallesSubSala();
        FragmentA.setArguments(bundle);
        FragmentTransaction transaction =manager.beginTransaction();
        transaction.replace(R.id.ContentFragmet,FragmentA,"DetallesSubSala");
        transaction.commit();


    }


    public void addFragListaHardware(){
        ListaHardware FragmentA=new ListaHardware();
        FragmentTransaction transaction =manager.beginTransaction();
        transaction.replace(R.id.ContentFragmet,FragmentA,"ListaHardware");
        transaction.commit();
    }

   public void addFragDetallesDeRack(){

       DetallesDeRack Fragment=new DetallesDeRack();
       FragmentTransaction transaction =manager.beginTransaction();
       transaction.replace(R.id.ContentFragmet,Fragment,"DetallesDeRack");
       transaction.commit();

   }

    @Override
    public void nextView(int nv,String name) {
       if(nv==1){addFragListaHardware();InPantalla=1;}
       if(nv==2){addFragDetallesDeRack();InPantalla=2;}
       if(nv==3){addFragDetallesSubsala(name);InPantalla=3;}

    }


    @Override
    public void iniciaCamara() {
        AnncaConfiguration.Builder photo = new AnncaConfiguration.Builder(this , SELECCIONA_FOTO);
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


    @Override
    public void iniciaScanQr() {

        Intent infoQR = new Intent(this, LectorQRActivity.class);
        infoQR.putExtra("goto",2);
        startActivityForResult(infoQR, SELECCIONA_QR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //tomar foto ok
        if (resultCode == RESULT_OK && requestCode == SELECCIONA_FOTO) {
            String imagePath;
            imagePath = data.getStringExtra(AnncaConfiguration.Arguments.FILE_PATH);
            Log.e(metodo+"onActivityResult ","imagePath="+ imagePath);
            File file = new File(imagePath);
        }

        if(resultCode == RESULT_OK &&  requestCode==SELECCIONA_QR){
            String qr;
            qr = data.getStringExtra("result");
            Log.e(metodo+"onActivityResult "," Codigo QR="+ qr);
            //File file = new File(imagePath);
        }
        Log.e(metodo+"onActivityResult","llllllll");
    }


}
