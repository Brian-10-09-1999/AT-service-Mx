package com.example.devolucionmaterial.beans;

/**
 * Created by EDGAR ARANA on 15/03/2017.
 */

public class MedioFolios {
    String url;
    int type;

    public MedioFolios(String url , int type) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public int getType() {
        return type;
    }
}
