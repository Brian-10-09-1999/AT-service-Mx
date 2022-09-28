package com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 23/08/2017.
 */

public class Datos {
    int id;

    @SerializedName("pedido")
    @Expose
    private String pedido;
    @SerializedName("componenteNuevo")
    @Expose
    private String componenteNuevo;
    @SerializedName("componenteAnterior")
    @Expose
    private String componenteAnterior;
    @SerializedName("codMaquina")
    @Expose
    private String codMaquina;


    private String folio;

    private int id_tecnico;
    private String sala;

    private int status;

    private int devolucionId;

    public Datos(int id, String pedido, String componenteNuevo, String componenteAnterior, String codMaquina, String folio, int id_tecnico, String sala, int status, int devolucionId) {
        this.id = id;
        this.pedido = pedido;
        this.componenteNuevo = componenteNuevo;
        this.componenteAnterior = componenteAnterior;
        this.codMaquina = codMaquina;
        this.folio = folio;
        this.id_tecnico = id_tecnico;
        this.sala = sala;
        this.status = status;
        this.devolucionId = devolucionId;
    }

    public int getId() {
        return id;
    }

    public String getPedido() {
        return pedido;
    }

    public String getComponenteNuevo() {
        return componenteNuevo;
    }

    public String getComponenteAnterior() {
        return componenteAnterior;
    }

    public String getCodMaquina() {
        return codMaquina;
    }

    public String getFolio() {
        return folio;
    }

    public int getId_tecnico() {
        return id_tecnico;
    }

    public String getSala() {
        return sala;
    }

    public int getStatus() {
        return status;
    }

    public int getDevolucionId() {
        return devolucionId;
    }
}

