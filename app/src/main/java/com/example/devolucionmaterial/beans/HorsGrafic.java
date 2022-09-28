package com.example.devolucionmaterial.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 27/06/2017.
 */

public class HorsGrafic {

    @SerializedName("horas")
    @Expose
    private Double horas;
    @SerializedName("numFolios")
    @Expose
    private Integer numFolios;
    @SerializedName("horas_no")
    @Expose
    private Double horasNo;
    @SerializedName("numFolios_no")
    @Expose
    private Integer numFoliosNo;

    public Double getHoras() {
        return horas;
    }

    public void setHoras(Double horas) {
        this.horas = horas;
    }

    public Integer getNumFolios() {
        return numFolios;
    }

    public void setNumFolios(Integer numFolios) {
        this.numFolios = numFolios;
    }

    public Double getHorasNo() {
        return horasNo;
    }

    public void setHorasNo(Double horasNo) {
        this.horasNo = horasNo;
    }

    public Integer getNumFoliosNo() {
        return numFoliosNo;
    }

    public void setNumFoliosNo(Integer numFoliosNo) {
        this.numFoliosNo = numFoliosNo;
    }
}
