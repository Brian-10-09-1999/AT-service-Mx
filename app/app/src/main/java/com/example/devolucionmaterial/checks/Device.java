package com.example.devolucionmaterial.checks;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.example.devolucionmaterial.static_class.MensajeEnConsola;

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

    public static String getCuentas(Context context) {
        metodo = "getCuentas()";
        AccountManager am = AccountManager.get(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {}
        Account[] accounts = am.getAccounts();
        String[] cadena = new String[accounts.length];
        String regresa = "";
        int i = 0;
        try {
            for (Account ac : accounts) {
                if (ac.type.equals("com.whatsapp")) {
                    cadena[i] = ac.name;
                }
                if (ac.type.equals("com.google")) {
                    cadena[i] = ac.name;
                }
                i++;
            }

            for (String aTemporal : cadena) {
                String[] temporal2 = aTemporal.split("@");
                if ("operacionesdelnorte.com".equals(temporal2[1])) {
                    regresa = aTemporal;
                }
            }
        }catch (Exception e){MensajeEnConsola.log(context, metodo, "Exception e = "+e.getMessage());}
        return regresa;
    }
}
