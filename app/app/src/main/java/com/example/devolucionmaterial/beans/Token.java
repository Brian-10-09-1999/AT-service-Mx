package com.example.devolucionmaterial.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 30/03/2017.
 */

public class Token {
    @SerializedName("result")
    int result;
    @SerializedName("value")
    int value;
    @SerializedName("messege")
    String messege;

    @SerializedName("token")
    String token;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getMessege() {
        return messege;
    }

    public void setMessege(String messege) {
        this.messege = messege;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
