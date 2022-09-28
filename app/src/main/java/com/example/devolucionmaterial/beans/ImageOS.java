package com.example.devolucionmaterial.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EDGAR ARANA on 11/09/2017.
 */

public class ImageOS {
    @SerializedName("url")
    @Expose
    List<String> listUrl;

    public List<String> getListUrl() {
        return listUrl;
    }

    public void setListUrl(List<String> listUrl) {
        this.listUrl = listUrl;
    }
}
