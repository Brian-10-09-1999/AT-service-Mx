package com.example.devolucionmaterial.internet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.muestra_ayuda);
        context = this;
        setupMuestraAyuda();
    }
    void setupMuestraAyuda() {
        webView = (WebView) findViewById(R.id.muestra_ayuda_id_web_view);
        progressBar = (ProgressBar) findViewById(R.id.muestra_ayuda_id_progress_bar);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(formaURLManual(context));
        //muestra la barra de progreso mientras se carga la pagina web
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
    public static String formaURLManual(Context context) {
        String dominio = "http://pruebasisco.ddns.net:8082/";
        String directorio = "Android/ManualUsuarioATService.html";
        return dominio+directorio;
    }
}
