package com.example.devolucionmaterial.lists;

import android.content.Context;

public class Lista_item {
	String metodo;
	Context context;
	private String codigo, descripcion, cantidad, serie, estatus;
	private String txtSuperior, txtInferior, color;
	private String folio, sala, falla, subfalla, licencia, juego, opciones;
	
	public Lista_item(String txtSuperior, String txtInferior, String color) {
		this.txtSuperior = txtSuperior;
		this.txtInferior = txtInferior;
		this.color = color;
	}

	public Lista_item(String codigo, String descripcion, String cantidad, String serie, String estatus) {
		this.codigo = codigo;
		this.descripcion = descripcion;
		this.cantidad = cantidad;
		this.serie = serie;
		this.estatus = estatus;
	}
	
	public Lista_item(String folio, String sala, String falla, String subfalla, String licencia, String juego, String opciones) {
		this.folio = folio;
		this.sala = sala;
		this.falla = falla;
		this.subfalla = subfalla;
		this.licencia = licencia;
		this.juego = juego;
		this.opciones = opciones;
	}
	
	public String gettxtSuperior() {
        return txtSuperior;
    }
	
	public String gettxtInferior() {
        return txtInferior;
    }
	
	public String getCodigo() {
        return codigo;
    }
	
	public String getDescripcion() {
        return descripcion;
    }
	
	public String getCantidad() {
        return cantidad;
    }
	
	public String getSerie() {
        return serie;
    }

	public String getEstatus() {
		return estatus;
	}
	
	public String getColor() {
        return color;
    }

}
