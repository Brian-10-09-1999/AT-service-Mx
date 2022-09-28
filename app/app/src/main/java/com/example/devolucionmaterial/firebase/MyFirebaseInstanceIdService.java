package com.example.devolucionmaterial.firebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.api.ServiceApi;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.beans.BeansGlobales;
import com.example.devolucionmaterial.beans.Token;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Usuario on 11/11/2016.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();
    Context contextM;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        contextM = getApplicationContext();
        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);
        conexionActulizarToken(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(ConfigToken.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {

        SharedPreferences recuperarToken = getSharedPreferences(ConfigToken.SHARED_PREF, MODE_PRIVATE);
        String tokenRecuperado = recuperarToken.getString("regId", "");

        SharedPreferences guardarToken = getApplicationContext().getSharedPreferences(ConfigToken.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = guardarToken.edit();
        editor.putString("regId", token);
        editor.apply();
    }

    public void conexionActulizarToken(String token) {

        if (!BDVarGlo.getVarGlo(getApplicationContext(), "INFO_USUARIO_ID").equals("")) {

            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<Token> call =
                    serviceApi.ACTUALIZAR_TOKEN(
                            BDVarGlo.getVarGlo(getApplicationContext(), "INFO_USUARIO_ID"),
                            token);
            call.enqueue(new Callback<Token>() {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response) {
                    try {
                        if (response.body().getValue() == 1) {
                            Log.e("Bien", "si esta actualizando");
                            Log.e("token de regerso", response.body().getToken());
                            // TODO: 17/04/2017 se pone las preferencia del token en lo que se ve lo de catalogo nuevo
                            PrefrerenceChat prefrerenceChat = new PrefrerenceChat(getApplicationContext());
                            prefrerenceChat.setTokenchat(Integer.parseInt(BDVarGlo.getVarGlo(getApplicationContext(), "INFO_USUARIO_ID")));
                        }
                    } catch (Exception e) {
                        // Toast.makeText(getApplicationContext(), getString(R.string.vuelve_a_intertarlo), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<Token> call, Throwable t) {
                    Log.e("error", String.valueOf(t));
                }
            });
        }

    }
}
