package com.example.devolucionmaterial.beans;

/**
 * Created by EDGAR ARANA on 19/06/2017.
 */

public class DetalisGraficas {
    int id;
    String cliente;
    String total;
    String blue;
    String black;
    String blackplus;
    String bryke;
    String abiertas;
    String cerrados;
    String porcentaje;
    String casinos;
    String abiertas1;
    String cerradas1;
    String abiertas2;
    String cerradas2;
    String fusion;

    public DetalisGraficas(int id, String cliente, String total, String blue, String black, String blackplus, String bryke, String abiertas, String cerrados, String porcentaje, String casinos, String abiertas1, String cerradas1, String abiertas2, String cerradas2, String fusion) {
        this.id = id;
        this.cliente = cliente;
        this.total = total;
        this.blue = blue;
        this.black = black;
        this.blackplus = blackplus;
        this.bryke = bryke;
        this.abiertas = abiertas;
        this.cerrados = cerrados;
        this.porcentaje = porcentaje;
        this.casinos = casinos;
        this.abiertas1 = abiertas1;
        this.cerradas1 = cerradas1;
        this.abiertas2 = abiertas2;
        this.cerradas2 = cerradas2;
        this.fusion = fusion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getBlue() {
        return blue;
    }

    public void setBlue(String blue) {
        this.blue = blue;
    }

    public String getBlack() {
        return black;
    }

    public void setBlack(String black) {
        this.black = black;
    }

    public String getBlackplus() {
        return blackplus;
    }

    public void setBlackplus(String blackplus) {
        this.blackplus = blackplus;
    }

    public String getBryke() {
        return bryke;
    }

    public void setBryke(String bryke) {
        this.bryke = bryke;
    }

    public String getFusion() {
        return fusion;
    }

    public void setFusion(String fusion) {
        this.fusion = fusion;
    }

    public String getAbiertas() {
        return abiertas;
    }

    public void setAbiertas(String abiertas) {
        this.abiertas = abiertas;
    }

    public String getCerrados() {
        return cerrados;
    }

    public void setCerrados(String cerrados) {
        this.cerrados = cerrados;
    }

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getCasinos() {
        return casinos;
    }

    public void setCasinos(String casinos) {
        this.casinos = casinos;
    }

    public String getAbiertas1() {
        return abiertas1;
    }

    public void setAbiertas1(String abiertas1) {
        this.abiertas1 = abiertas1;
    }

    public String getCerradas1() {
        return cerradas1;
    }

    public void setCerradas1(String cerradas1) {
        this.cerradas1 = cerradas1;
    }

    public String getAbiertas2() {
        return abiertas2;
    }

    public void setAbiertas2(String abiertas2) {
        this.abiertas2 = abiertas2;
    }

    public String getCerradas2() {
        return cerradas2;
    }

    public void setCerradas2(String cerradas2) {
        this.cerradas2 = cerradas2;
    }
}
