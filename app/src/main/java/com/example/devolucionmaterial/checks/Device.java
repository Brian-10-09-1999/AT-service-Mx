package com.example.devolucionmaterial.checks;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;

import com.example.devolucionmaterial.static_class.MensajeEnConsola;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Device {
    private static String metodo;

    public static String versionName(Context context) {
        metodo = "Device.versionName()";
        String regresa = "";
        PackageManager manager = context.getPackageManager();
        {
            try {
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                regresa = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                MensajeEnConsola.log(context, metodo, "Error.PackageManager.NameNotFoundException e = " + e.getMessage());
            }
        }
        return regresa;
    }

    public static int versionCode(Context context) {
        metodo = "Device.versionCode()";
        int regresa = 0;
        PackageManager manager = context.getPackageManager();
        {
            try {
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                regresa = info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                MensajeEnConsola.log(context, metodo, "Error.PackageManager.NameNotFoundException e = " + e.getMessage());
            }
        }
        return regresa;
    }

    public static String packageName(Context context) {
        metodo = "Device.packageName()";
        String regresa = "";
        PackageManager manager = context.getPackageManager();
        {
            try {
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                regresa = info.packageName;
            } catch (PackageManager.NameNotFoundException e) {
                MensajeEnConsola.log(context, metodo, "Error.PackageManager.NameNotFoundException e = " + e.getMessage());
            }
        }
        return regresa;
    }

    public static String getPhoneNumber1(Context context) {
        metodo = "getPhoneNumber1()";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            MensajeEnConsola.log(context, metodo, "num de tel: " + telephonyManager.getLine1Number());
            return telephonyManager.getLine1Number();
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
        }
        return "";
    }

    public String getCuentas(Context context) {
        metodo = "getCuentas()";
        AccountManager am = AccountManager.get(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
        }
        String userEmailId;
        Account[] accounts = am.getAccounts();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;


        String[] cadena = new String[accounts.length];
        List<String> cdena = new ArrayList<>();
        String regresa = "";
        int i = 0;
        try {



            for (Account ac : accounts) {

                if (emailPattern.matcher(ac.name).matches()) {
                    userEmailId = ac.name;
                    Log.e("Device.getCuentas:userEmailId=", userEmailId);
                    //cadena[i] = ac.name;
                    cdena.add(ac.name);
                }
                i++;
            }

            for (int ia = 0; ia < cdena.size(); ia++) {
                String[] parts = cdena.get(ia).split("@");
                for (int ias = 0; ias < parts.length; ias++) {
                    if ("operacionesdelnorte.com".equals(parts[ias])) {
                        regresa = cdena.get(ia);
                        Log.e("regresa", regresa);
                    }
                }

            }

        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
            Log.e("error cuenat", e + "");
        }
        return regresa;
    }
}
