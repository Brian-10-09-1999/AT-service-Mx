package com.example.devolucionmaterial.chat.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.devolucionmaterial.R;


public class LoginChatActity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private EditText etUser, etPassword;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_chat_actity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Entrar al chat");
        }
        setUpLogin();

    }

    private void setUpLogin() {
        btnLogin = (Button) findViewById(R.id.lmc_btn_login);
        etUser = (EditText) findViewById(R.id.lmc_et_user);
        etPassword = (EditText) findViewById(R.id.lmc_et_password);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lmc_btn_login:
                Intent iMenuChat = new Intent(this, MenuChatActivity.class);
                startActivity(iMenuChat);
                break;
        }
    }


}
