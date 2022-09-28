package com.example.devolucionmaterial.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EDGAR ARANA on 16/03/2017.
 */

public class BeanGaleria {
    @SerializedName("exitoso")
    @Expose
    private String exitoso;
    @SerializedName("galeria")
    @Expose
    private List<Galerium> galeria = null;

    public String getExitoso() {
        return exitoso;
    }

    public void setExitoso(String exitoso) {
        this.exitoso = exitoso;
    }

    public List<Galerium> getGaleria() {
        return galeria;
    }

    public void setGaleria(List<Galerium> galeria) {
        this.galeria = galeria;
    }


}
