package com.example.devolucionmaterial.chat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by EDGAR ARANA on 28/03/2017.
 */

public class MessageCheckout {

    @SerializedName("multicast_id")
    @Expose
    private String multicastId;
    @SerializedName("success")
    @Expose
    private int success;
    @SerializedName("failure")
    @Expose
    private int failure;
    @SerializedName("canonical_ids")
    @Expose
    private int canonicalIds;


    public String getMulticastId() {
        return multicastId;
    }

    public void setMulticastId(String multicastId) {
        this.multicastId = multicastId;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonicalIds() {
        return canonicalIds;
    }

    public void setCanonicalIds(int canonicalIds) {
        this.canonicalIds = canonicalIds;
    }


}
