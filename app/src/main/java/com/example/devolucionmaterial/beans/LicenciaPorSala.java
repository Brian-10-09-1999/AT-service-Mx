package com.example.devolucionmaterial.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EDGAR ARANA on 05/05/2017.
 */

public class LicenciaPorSala {

    @SerializedName("posicion")
    @Expose
    private String posicion;
    @SerializedName("Licencia")
    @Expose
    private List<Licencium> licencia = null;

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public List<Licencium> getLicencia() {
        return licencia;
    }

    public void setLicencia(List<Licencium> licencia) {
        this.licencia = licencia;
    }


}
