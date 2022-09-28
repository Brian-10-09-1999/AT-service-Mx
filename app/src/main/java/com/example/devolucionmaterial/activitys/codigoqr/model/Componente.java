package com.example.devolucionmaterial.activitys.codigoqr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 07/12/2017.
 */

public class Componente {

    @SerializedName("nombre")
    @Expose
    public String nombre;
    @SerializedName("codigo")
    @Expose
    public String codigo;

}
