package com.example.devolucionmaterial.chat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 27/03/2017.
 */

public class TokenGroup {
    @SerializedName("token")
    @Expose
    private int token;

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }
}
