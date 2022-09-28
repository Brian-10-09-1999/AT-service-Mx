package com.example.devolucionmaterial.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Usuario on 09/11/2016.
 */

public class BeanResponse {

    @SerializedName("result")
    int result;
    @SerializedName("value")
    int value;
    @SerializedName("messege")
    String messege;

    @SerializedName("comment")
    String comment;

    // TODO: 13/12/2016  obtiene los conatacos de sala para el spinner
    @SerializedName("contactos_sala")
    @Expose
    public List<ContactosSala> contactosSala = null;
    ///////////////////////////////
    @SerializedName("salaidfk")
    @Expose
    private String salaidfk;
    @SerializedName("guia")
    @Expose
    private String guia;
    @SerializedName("fecha")
    @Expose
    private String fecha;
    @SerializedName("estatus")
    @Expose
    private String estatus;
    @SerializedName("sala")
    @Expose
    private String sala;
    @SerializedName("refacciones")
    @Expose
    private List<Refacciones> refacciones = new ArrayList<Refacciones>();

    ///////////////////
    @SerializedName("res")
    @Expose
    private String res;

    @SerializedName("officeidx")
    @Expose
    private String officeidx;

    // TODO: 08/12/2016 respuestas de obtener actividades folio pendintes

    @SerializedName("datosFoliopend")
    @Expose
    public List<DatosFoliopend> datosFoliopend = new ArrayList<>();

    @SerializedName("devolucionid")
    @Expose
    private String devolucionid;



    public List<DatosFoliopend> getDatosFoliopend() {
        return datosFoliopend;
    }

    public void setDatosFoliopend(List<DatosFoliopend> datosFoliopend) {
        this.datosFoliopend = datosFoliopend;
    }

    public List<ContactosSala> getContactosSala() {
        return contactosSala;
    }

    public void setContactosSala(List<ContactosSala> contactosSala) {
        this.contactosSala = contactosSala;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getOfficeidx() {
        return officeidx;
    }

    public void setOfficeidx(String officeidx) {
        this.officeidx = officeidx;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }



    public String getGuia() {
        return guia;
    }

    public void setGuia(String fecha) {
        this.guia = fecha;
    }



    public String getSalaidfk() {
        return salaidfk;

    }

    public void setSalaidfk(String salaidfk) {
        this.salaidfk = salaidfk;
    }

    public List<Refacciones> getRefacciones() {
        return refacciones;
    }

    public void setRefacciones(List<Refacciones> refacciones) {
        this.refacciones = refacciones;
    }

    public String getSala() {
        return sala;

    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }


    BeanResponse(int result, int value, String messege) {
        this.result = result;
        this.value = value;
        this.messege = messege;
    }

    public String getComment() {
        return comment;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getMessege() {
        return messege;
    }

    public void setMessege(String messege) {
        this.messege = messege;
    }

    public String getDevolucionid() {
        return devolucionid;
    }

    public void setDevolucionid(String devolucionid) {
        this.devolucionid = devolucionid;
    }
}
