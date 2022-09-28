package com.example.devolucionmaterial.activitys;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.devolucionmaterial.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class LectorActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;


    private boolean onOffFlash=false;
    protected Menu mMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector);
        initToolbar();

        mScannerView = new ZXingScannerView(this);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
    }

    void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Leer códido");
        }
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
    protected void onRestart() {
        super.onRestart();
        mScannerView = new ZXingScannerView(this);// Programmatically initialize the scanner view
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cleanup in onPause()
        mScannerView.stopCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();
    }


    @Override
    public void handleResult(Result result) {
        mScannerView.stopCamera();
        String data = result.getText();
        Log.e("data", data);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", data);
        returnIntent.putExtra("format", String.valueOf(result.getBarcodeFormat()));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }
}
