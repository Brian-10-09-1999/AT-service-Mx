package com.example.devolucionmaterial.internet;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.data_base.BDVarGlo;

public class MuestraAyuda extends Activity {
    String metodo;
    static Context context;
    WebView webView;
    ProgressBar progressBar;

    private String url1="http://pruebasisco.ddns.net:8082/Android/ManualUsuarioATService.html";
    private String url2="http://drive.google.com/viewerng/viewer?embedded=true&url="+"http://pruebasisco.ddns.net:8082/Android/manual.pdf";
    private String url3="http://pruebasisco.ddns.net:8082/Android/imagenAT/video1.mp4";

    private String SetUrl="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.muestra_ayuda);
        context = this;

        int  op=getIntent().getExtras().getInt("op");

        switch (op) {
            case 1:  SetUrl=url1;
                break;
            case 2:  SetUrl=url2;
                break;
            case 3:  SetUrl=url3;
                break;

            default: SetUrl=url2;
                break;
        }

        setupMuestraAyuda();
    }
    void setupMuestraAyuda() {
        webView = (WebView) findViewById(R.id.muestra_ayuda_id_web_view);
        progressBar = (ProgressBar) findViewById(R.id.muestra_ayuda_id_progress_bar);
        webView.getSettings().setJavaScriptEnabled(true);
       // webView.setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(formaURLManual(context));



        //muestra la barra de progreso mientras se carga la pagina web

        Log.i("--------url ",formaURLManual(context));

         webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress * 1000);

                progressBar.incrementProgressBy(progress);

                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });



    }
    public  String formaURLManual(Context context) {
        String dominio = "http://pruebasisco.ddns.net:8082/";
        //String directorio = "Android/ManualUsuarioATService.html";
        String directorio = "Android/manual.pdf";
        String pdf = dominio+directorio;

       // return dominio+directorio;
        //return  ("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdf);
        //return "http://pruebasisco.ddns.net:8082/Android/imagenAT/video1.mp4";

        return SetUrl;
    }


    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        webView.reload();

    }
}
