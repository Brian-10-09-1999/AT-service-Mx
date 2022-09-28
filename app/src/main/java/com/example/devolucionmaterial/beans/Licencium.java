package com.example.devolucionmaterial.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 05/05/2017.
 */

public class Licencium {
    @SerializedName("Licencia")
    @Expose
    private String licencia;
    @SerializedName("ip")
    @Expose
    private String ip;
    @SerializedName("juego")
    @Expose
    private String juego;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("mueble")
    @Expose
    private String mueble;
    @SerializedName("modelo")
    @Expose
    private String modelo;

    @SerializedName("serie")
    @Expose
    private String serie;


    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getJuego() {
        return juego;
    }

    public void setJuego(String juego) {
        this.juego = juego;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMueble() {
        return mueble;
    }

    public void setMueble(String mueble) {
        this.mueble = mueble;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }
}
