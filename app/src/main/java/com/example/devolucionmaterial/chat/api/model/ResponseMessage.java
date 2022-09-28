package com.example.devolucionmaterial.chat.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrador on 07/03/2017.
 */

public class ResponseMessage {

    @SerializedName("messege")
    private String messege;
    @SerializedName("value")
    private int value;

    public String getMessege() {
        return messege;
    }

    public void setMessege(String messege) {
        this.messege = messege;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
