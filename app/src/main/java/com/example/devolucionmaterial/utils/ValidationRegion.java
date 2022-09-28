package com.example.devolucionmaterial.utils;



/**
 * Created by EDGAR ARANA on 14/03/2017.
 */

public class ValidationRegion {

    public static boolean validationRegionEspana(String region) {
        if (region.equals("1") || region.equals("2") || region.equals("3")
                || region.equals("4") || region.equals("5")
                || region.equals("6") || region.equals("7")
                || region.equals("59") || region.equals("60")
                || region.equals("61") || region.equals("62")) {
            return false;
        } else {
            return true;
        }
    }
}
