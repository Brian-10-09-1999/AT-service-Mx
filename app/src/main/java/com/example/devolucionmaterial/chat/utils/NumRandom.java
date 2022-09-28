package com.example.devolucionmaterial.chat.utils;

import java.util.Random;

/**
 * Created by Administrador on 17/01/2017.
 */

public class NumRandom {

    public static int getRadom() {
        Random rand = new Random();
        int n = rand.nextInt(999999999);
        return n;
    }

}
