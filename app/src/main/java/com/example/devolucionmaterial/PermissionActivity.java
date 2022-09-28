package com.example.devolucionmaterial;

import android.*;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.thanosfisherman.mayi.Mayi;
import com.thanosfisherman.mayi.PermissionBean;
import com.thanosfisherman.mayi.PermissionToken;

/**
 * Created by EDGAR ARANA on 06/12/2017.
 */

public abstract class PermissionActivity extends AppCompatActivity {



    public String[] permissions = {};
    public CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions = permissions();
           // examplePermissionMultiple();
        }


    }


    public abstract String[] permissions();

    protected void examplePermission() {

        Mayi.withActivity(this)
                .withPermission(android.Manifest.permission.CAMERA)
                .onResult(this::permissionResultSingle)
                .onRationale(this::permissionRationaleSingle)
                .check();


    }


    protected void permissionMultiple() {


        Mayi.withActivity(this)
                .withPermissions(permissions)
                .onRationale(this::permissionRationaleMulti)
                .onResult(this::permissionResultMulti)
                .onErrorListener(this::inCaseOfError)
                .check();



    }

    private void permissionResultSingle(PermissionBean permission) {

        Toast.makeText(PermissionActivity.this, "PERMISSION RESULT " + permission, Toast.LENGTH_LONG).show();
    }

    private void permissionRationaleSingle(PermissionBean bean, PermissionToken token) {
        if (bean.getSimpleName().toLowerCase().contains("contacts")) {
            //Toast.makeText(PermissionActivity.this, "Should show rationale for " + bean.getSimpleName() + " permission", Toast.LENGTH_LONG).show();
            token.skipPermissionRequest();
        } else {
            //Toast.makeText(PermissionActivity.this, "Should show rationale for " + bean.getSimpleName() + " permission", Toast.LENGTH_LONG).show();
            token.continuePermissionRequest();
        }
    }

    protected abstract void permissionResultMulti(PermissionBean[] permissions);

    /***
     * Aquien entra cuando se rechaza alguno de los permisos y si s e quiere se meuestra un cuadro de dialogos
     * explicando por que se necesita el permiso
     * */
    private void permissionRationaleMulti(PermissionBean[] permissions, PermissionToken token) {
        //Toast.makeText(PermissionActivity.this, "Rationales for Multiple Permissions " + Arrays.deepToString(permissions), Toast.LENGTH_LONG).show();
        token.continuePermissionRequest();
    }

    private void inCaseOfError(Exception e) {
        //Toast.makeText(PermissionActivity.this, "ERROR " + e.toString(), Toast.LENGTH_SHORT).show();
    }

    public void showSnackBarPermission() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.error_permission_requred, Snackbar.LENGTH_INDEFINITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        snackbar.setAction("Configurar", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", "com.example.devolucionmaterial", null);
                intent.setData(uri);
                startActivity(intent);
            }
        }).show();
    }


}