package com.example.devolucionmaterial.activitys.viaticos.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrador on 02/03/2017.
 *
 * esta pojo es el caalback cuando subes imagens o videos
 */

public class ResponseCallXml {

    @SerializedName("result")
    private String result;
    @SerializedName("value")
    private String value;
    @SerializedName("file")
    private String file;



    /**

     * @return The result
     */
    public String getResult() {
        return result;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @param result The result
     */
    public void setResult(String result) {
        this.result = result;
    }
}
