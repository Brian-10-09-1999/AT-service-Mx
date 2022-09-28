package com.example.devolucionmaterial.chat.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.adapter.GalleryAdapter;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.model.ModelChat;

import org.json.JSONException;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryChatActivity extends AppCompatActivity implements GalleryAdapter.ClickListener {

    @BindView(R.id.agc_rv_galery)
    RecyclerView rvGallery;
    @BindView(R.id.agc_txt_empty)
    TextView txt_empty;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private DBChatManager dbManager;
    private List<ModelChat> listGallery;
    private int idChat;
    private GalleryAdapter galleryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_chat);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null)
            idChat = getIntent().getExtras().getInt("id");
        initToolbar();
        initSetUp();
    }

    void initSetUp() {
        dbManager = new DBChatManager(this);
        dbManager.open();
        try {
            listGallery = dbManager.getChatsDetailsGallery(idChat);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        galleryAdapter = new GalleryAdapter(listGallery, this);
        galleryAdapter.setClickListener(this);
        rvGallery.setLayoutManager(new GridLayoutManager(this, 2));
        rvGallery.setHasFixedSize(true);
        rvGallery.setAdapter(galleryAdapter);

        if (listGallery.isEmpty()) {
            rvGallery.setVisibility(View.GONE);
            txt_empty.setVisibility(View.VISIBLE);

        } else {
            rvGallery.setVisibility(View.VISIBLE);
            txt_empty.setVisibility(View.GONE);
        }

    }

    void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Galeria");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void url(String url) {
        Intent intentImage = new Intent(GalleryChatActivity.this, ImageFullActivity.class);
        intentImage.putExtra("url", url);
        startActivity(intentImage);
    }
}
