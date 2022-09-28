package com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 22/08/2017.
 */

public class PedidoQr {
    @SerializedName("respuesta")
    @Expose
    private String respuesta;
    @SerializedName("pedido")
    @Expose
    private String pedido;
    @SerializedName("datos")
    @Expose
    private Datos datos;

    /**
     * No args constructor for use in serialization
     */
    public PedidoQr() {
    }

    /**
     * @param pedido
     * @param datos
     * @param respuesta
     */
    public PedidoQr(String respuesta, String pedido, Datos datos) {
        super();
        this.respuesta = respuesta;
        this.pedido = pedido;
        this.datos = datos;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public Datos getDatos() {
        return datos;
    }

    public void setDatos(Datos datos) {
        this.datos = datos;
    }

}
