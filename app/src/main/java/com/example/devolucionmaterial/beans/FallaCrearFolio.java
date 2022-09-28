package com.example.devolucionmaterial.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 05/05/2017.
 */
public class FallaCrearFolio {

    @SerializedName("fallaid")
    @Expose
    private String fallaid;
    @SerializedName("falla")
    @Expose
    private String falla;


    public FallaCrearFolio(String fallaid, String falla){
        this.fallaid=fallaid;
        this.falla=falla;
    }

    public String getFallaid() {
        return fallaid;
    }

    public void setFallaid(String fallaid) {
        this.fallaid = fallaid;
    }

    public String getFalla() {
        return falla;
    }

    public void setFalla(String falla) {
        this.falla = falla;
    }

}