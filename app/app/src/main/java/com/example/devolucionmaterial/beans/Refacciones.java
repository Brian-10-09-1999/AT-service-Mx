package com.example.devolucionmaterial.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Usuario on 29/11/2016.
 */

public class Refacciones {

    @SerializedName("clave")
    @Expose
    public String clave;
    @SerializedName("nombre")
    @Expose
    public String nombre;
    @SerializedName("cantidad")
    @Expose
    public String cantidad;
    @SerializedName("serie")
    @Expose
    public String serie;
    @SerializedName("estatus")
    @Expose
    public String estatus;


    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
