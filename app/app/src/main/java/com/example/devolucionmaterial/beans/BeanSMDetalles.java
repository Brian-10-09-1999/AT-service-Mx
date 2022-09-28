package com.example.devolucionmaterial.beans;

/**
 * Created by Usuario on 08/11/2016.
 */

public class BeanSMDetalles {
    String clave;
    String nombre;
    String cantidad;

    public BeanSMDetalles(String clave, String nombre, String cantidad) {
        this.clave = clave;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public String getClave() {
        return clave;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCantidad() {
        return cantidad;
    }
}
