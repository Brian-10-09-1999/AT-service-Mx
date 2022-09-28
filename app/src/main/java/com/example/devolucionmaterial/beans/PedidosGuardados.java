package com.example.devolucionmaterial.beans;

/**
 * Created by EDGAR ARANA on 08/06/2017.
 */

public class PedidosGuardados {

    private int id;
    private int sala;
    private String maquina;
    private String componente;
    private int tecnicoid;
    private int status;
    private String pedido;
    private String folio;

    public PedidosGuardados(int id, int sala, String maquina, String componente, int tecnicoid, int status, String pedido, String folio) {
        this.id = id;
        this.sala = sala;
        this.maquina = maquina;
        this.componente = componente;
        this.tecnicoid = tecnicoid;
        this.status = status;
        this.pedido = pedido;
        this.folio = folio;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSala() {
        return sala;
    }

    public void setSala(int sala) {
        this.sala = sala;
    }

    public String getMaquina() {
        return maquina;
    }

    public void setMaquina(String maquina) {
        this.maquina = maquina;
    }

    public String getComponente() {
        return componente;
    }

    public void setComponente(String componente) {
        this.componente = componente;
    }

    public int getStatus() {
        return status;
    }

    public int getTecnicoid() {
        return tecnicoid;
    }

    public void setTecnicoid(int tecnicoid) {
        this.tecnicoid = tecnicoid;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }
}
