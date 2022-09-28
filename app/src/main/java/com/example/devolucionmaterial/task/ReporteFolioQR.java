package com.example.devolucionmaterial.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.example.devolucionmaterial.R;

/**
 * Created by EDGAR ARANA on 14/08/2017.
 */

public class ReporteFolioQR {
    private AlertDialog.Builder builder;
    Activity activity;
    private ProgressDialog pDialog;

    public ReporteFolioQR(Activity activity) {

        this.activity = activity;
    }

    void sendqr() {
        pDialog = new ProgressDialog(activity);
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progressdialog);

    }


}
