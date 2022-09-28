package com.example.devolucionmaterial.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrador on 08/12/2016.
 */

public class DatosFoliopend {
    @SerializedName("folio")
    @Expose
    public String folio;
    @SerializedName("sala")
    @Expose
    public String sala;
    @SerializedName("falla")
    @Expose
    public String falla;
    @SerializedName("subFalla")
    @Expose
    public String subFalla;
    @SerializedName("licencia")
    @Expose
    public String licencia;
    @SerializedName("juego")
    @Expose
    public String juego;
    @SerializedName("fallaid")
    @Expose
    public String fallaid;
    @SerializedName("comentarios")
    @Expose
    public String comentarios;
    @SerializedName("opciones")
    @Expose
    public String opciones;

    @SerializedName("garantia")
    @Expose
    public String garantia;

    @SerializedName("salaid")
    @Expose
    public int salaid;

    public int getSalaid() {
        return salaid;
    }

    public void setSalaid(int salaid) {
        this.salaid = salaid;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public String getFalla() {
        return falla;
    }

    public void setFalla(String falla) {
        this.falla = falla;
    }

    public String getSubFalla() {
        return subFalla;
    }

    public void setSubFalla(String subFalla) {
        this.subFalla = subFalla;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public String getJuego() {
        return juego;
    }

    public void setJuego(String juego) {
        this.juego = juego;
    }

    public String getFallaid() {
        return fallaid;
    }

    public void setFallaid(String fallaid) {
        this.fallaid = fallaid;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getOpciones() {
        return opciones;
    }

    public void setOpciones(String opciones) {
        this.opciones = opciones;
    }


    //garantias
    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }


}
