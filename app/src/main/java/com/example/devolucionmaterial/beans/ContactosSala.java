package com.example.devolucionmaterial.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrador on 13/12/2016.
 */

public class ContactosSala {

    @SerializedName("nombre")
    @Expose
    public String nombre;
    @SerializedName("cargo")
    @Expose
    public String cargo;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
