package com.example.devolucionmaterial.activitys.codigoqr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.BaseActivity;
import com.google.zxing.Result;
import com.thanosfisherman.mayi.PermissionBean;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class LectorQRActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;


    private boolean onOffFlash=false;
    protected Menu mMenu;
    private int returnTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_qr);

        returnTo=getIntent().getExtras().getInt("goto");

        Log.i("------------ oncreates"," returnTo= "+returnTo);

        initToolbar("Leer código", true, false);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionMultiple();
        } else {
            initSetUp();
        }





    }


    void initSetUp() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lampara, menu);


        mMenu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MenuItem item2 = mMenu.findItem(R.id.lamparita);


        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;

            case R.id.lamparita:

                if(onOffFlash)
                { mScannerView.setFlash(false); onOffFlash=false;
                    item2.setIcon(R.drawable.ico_lampara1);
                }//do not forget to invalidate}}
               else { mScannerView.setFlash(true); onOffFlash=true;
                    item2.setIcon(R.drawable.ico_lampara2);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }










    @Override
    protected void onStart() {
        super.onStart();

        Log.i("qr-----------------","onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView = new ZXingScannerView(this);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();

        Log.i("qr-----------------","onResume");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mScannerView = new ZXingScannerView(this);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();

        Log.i("qr-----------------","onRestart");

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cleanup in onPause()
        mScannerView.stopCamera();
        Log.i("qr-----------------","onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();
       // mScannerView.cl
        Log.i("qr-----------------","onStop");
    }









    @Override
    public void handleResult(Result result) {
        mScannerView.stopCamera();

        Log.i("------------"," lector handleresult");
        Log.i("------------ "," returnTo= "+returnTo);

        String data = result.getText();
        if(returnTo==1) {

            //obsoleto ya no se usa
            Log.e("data", data);
            Intent infoC = new Intent(this, InfoCodigoQRActivity.class);
            infoC.putExtra("codigo", data);
            startActivity(infoC);

            Log.i("------------"," return 1");

            finish();


        }

        if(returnTo==2) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", data);
        returnIntent.putExtra("goto",2);
        //returnIntent.putExtra("format", String.valueOf(result.getBarcodeFormat()));
        setResult(Activity.RESULT_OK, returnIntent);
            Log.i("------------"," return 2");
        finish();

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



    //metodos usados para la camara y el flas-------------------------------------------------------------




}
