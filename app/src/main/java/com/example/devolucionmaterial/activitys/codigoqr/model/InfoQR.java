package com.example.devolucionmaterial.activitys.codigoqr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EDGAR ARANA on 07/12/2017.
 */

public class InfoQR {
    @SerializedName("tipo")
    @Expose
    public int tipo;
    @SerializedName("serieCompleta")
    @Expose
    public String serieCompleta;
    @SerializedName("sala")
    @Expose
    public String Sala;
    @SerializedName("componentes")
    @Expose
    public ArrayList<Componente> componentes = null;

}