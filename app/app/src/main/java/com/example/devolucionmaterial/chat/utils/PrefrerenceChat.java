package com.example.devolucionmaterial.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by EDGAR ARANA on 29/03/2017.
 */

public class PrefrerenceChat {
    public  Context context;


    public PrefrerenceChat(Context context) {
        this.context = context;
    }

    // TODO: 17/04/2017 para saber si ya se habia ingresado con anterioridad y no mandar a recuperar de nuevo los chats
    public void setValidationInit(int value) {
        String init="init_chat";
        SharedPreferences chatOnline= context.getSharedPreferences(init, MODE_PRIVATE);
        SharedPreferences.Editor editor = chatOnline.edit();
        editor.putInt("init", value);

        editor.apply();
    }
    public int getValidationInit() {
        String init="init_chat";
        SharedPreferences recuperarValor = context.getSharedPreferences(init, MODE_PRIVATE);

        return recuperarValor.getInt("init", 0);
    }


    // TODO: 17/04/2017
    public void setTokenchat(int tokenchat) {
        String token_chat="token_chat";
        SharedPreferences chatOnline= context.getSharedPreferences(token_chat, MODE_PRIVATE);
        SharedPreferences.Editor editor = chatOnline.edit();
        editor.putInt("token_chat", tokenchat);

        editor.apply();
    }

    // TODO: 05/04/2017 el token_chat es el id de usuario
    public  int getTokenChat() {
        String token_chat="token_chat";
        SharedPreferences recuperarValor = context.getSharedPreferences(token_chat, MODE_PRIVATE);

        return recuperarValor.getInt("token_chat", 0);
    }
    /**
     * se actuliza los estatus  del chat para saber si esta en lia y mandar notificaciones o actulizar la lista de chat
     * */
    public void setStatusChat(int status, int id_chat) {
        String state="statusOnline";
        SharedPreferences chatOnline= context.getSharedPreferences(state, MODE_PRIVATE);
        SharedPreferences.Editor editor = chatOnline.edit();
        editor.putInt("status", status);
        editor.putInt("id_chat", id_chat);
        editor.apply();
    }

    /**
     * obtienes el estatus de id del ususario
     * **/
    public  int getStatusChat() {
        String state="statusOnline";
        SharedPreferences recuperarValor = context.getSharedPreferences(state, MODE_PRIVATE);
        int valorRecuperado = recuperarValor.getInt("status", 0);

        return valorRecuperado;
    }
    /**
     * revisa el id  si esta en lina el usuario para saber si se muestra la notificacion o se actiliza la lista
     * */
    public  int getIdChatStatus() {
        String state="statusOnline";
        SharedPreferences recuperarValor = context.getSharedPreferences(state, MODE_PRIVATE);
        int valorRecuperado = recuperarValor.getInt("id_chat", 0);

        return valorRecuperado;
    }





}
