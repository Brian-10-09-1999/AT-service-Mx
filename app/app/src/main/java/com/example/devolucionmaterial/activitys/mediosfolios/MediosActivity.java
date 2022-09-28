package com.example.devolucionmaterial.activitys.foliosPendientesSeccion.mediosfolios;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.devolucionmaterial.R;
import com.example.libraryvideo.BetterVideoCallback;
import com.example.libraryvideo.BetterVideoPlayer;


public class MediosActivity extends AppCompatActivity {
    private BetterVideoPlayer bvp;
    private String TAG = "BetterSample";
    private String url;
    private int pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medios);


        iniitToolbar();
        initSetUp(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                bvp.stop();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void iniitToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    void initSetUp(Bundle savedInstanceState) {
        bvp = (BetterVideoPlayer) findViewById(R.id.bvp);

        url = getIntent().getExtras().getString("url");

        Log.e("pos",pos+"");
        bvp.setAutoPlay(true);
        bvp.setSource(Uri.parse(url));
        bvp.seekTo(pos);

        bvp.setHideControlsOnPlay(true);

  /*      bvp.setMenu(R.menu.menu_dizi);
        bvp.setMenuCallback(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.action_settings){
                    //Log.i(TAG, "Action Clicked");
                }
                return false;
            }
        });*/


        bvp.setWindow(getWindow());

        bvp.setCallback(new BetterVideoCallback() {
            @Override
            public void onStarted(BetterVideoPlayer player) {
                Log.i(TAG, "Started");
            }

            @Override
            public void onPaused(BetterVideoPlayer player) {
                Log.i(TAG, "Paused");
            }

            @Override
            public void onPreparing(BetterVideoPlayer player) {
                Log.i(TAG, "Preparing");
            }

            @Override
            public void onPrepared(BetterVideoPlayer player) {
                Log.i(TAG, "Prepared");
            }

            @Override
            public void onBuffering(int percent) {
                Log.i(TAG, "Buffering " + percent);
            }

            @Override
            public void onError(BetterVideoPlayer player, Exception e) {
                Log.i(TAG, "Error " + e.getMessage());
            }

            @Override
            public void onCompletion(BetterVideoPlayer player) {
                Log.i(TAG, "Completed");
            }

            @Override
            public void onToggleControls(BetterVideoPlayer player, boolean isShowing) {

            }
        });


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e("time", bvp.getCurrentPosition() + "");

        outState.putInt("seek", bvp.getCurrentPosition());
        outState.putString("url", url);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle recEstado) {
        super.onRestoreInstanceState(recEstado);
        if (recEstado != null) {
            Log.e("time recuperado", String.valueOf(recEstado.getInt("seek")));
            url = recEstado.getString("url");
            pos = recEstado.getInt("seek");

        }
    }

    @Override
    public void onPause() {
        bvp.pause();
        super.onPause();
    }
}
