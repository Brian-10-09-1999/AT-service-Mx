package com.example.devolucionmaterial.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 12/06/2017.
 */

public class ingresaSalidaMat2 {
    @SerializedName("res")
    @Expose
    private int res;
    @SerializedName("devolucionid")
    @Expose
    private String devolucionid;

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getDevolucionid() {
        return devolucionid;
    }

    public void setDevolucionid(String devolucionid) {
        this.devolucionid = devolucionid;
    }

}
