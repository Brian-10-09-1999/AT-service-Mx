package com.example.devolucionmaterial.activitys.RemplazoComponente;

import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model.Datos;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 13/03/2018.
 */

public class ModelRC {


    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("alerta")
    @Expose
    private String alerta;

    /**
     * No args constructor for use in serialization
     */
    public ModelRC() {
    }

    /**
     * @param alerta
     *
     * @param result
     */
    public ModelRC(String result, String alerta) {
        super();
        this.result = result;
        this.alerta = alerta;

    }




    public String getResult() {
        return result;
    }

    public void   setResult(String respuesta) {
        this.result = respuesta;
    }

    public String getAlerta() {
        return alerta;
    }

    public void setAlerta(String pedido) {
        this.alerta = pedido;
    }










}
