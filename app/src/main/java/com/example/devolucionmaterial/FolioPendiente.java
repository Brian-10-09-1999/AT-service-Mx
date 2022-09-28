package com.example.devolucionmaterial;

import android.content.Context;

public class FolioPendiente {
	String metodo;
	Context context;
	private String idFolio;
	private String nombreSala;
	private String solSerId;
	private String SalaId;
	private String estatus;
	private String falla;
	private String numsFolio;
	private String garantias;

	public FolioPendiente(String idFol, String nomSal, String solSerId, String estatus,String garantias){
		this.idFolio = idFol;
		this.nombreSala = nomSal;
		this.solSerId = solSerId;
		this.estatus = estatus;
		this.garantias=garantias;
	}



	public String getFalla() {
		return falla;
	}

	public void setFalla(String falla) {
		this.falla = falla;
	}

	public FolioPendiente(String idFol, String nomSal, String solSerId, String estatus, String falla,String garantias){
		this.idFolio = idFol;
		this.nombreSala = nomSal;
		this.solSerId = solSerId;
		this.estatus = estatus;
		this.falla= falla;
		this.garantias= garantias;
	}


	public FolioPendiente(String salaId, String nomSala, String numsFolio){
		this.SalaId = salaId;
		this.nombreSala = nomSala;
		this.numsFolio = numsFolio;
	}


	public String getSalaId(){
		return SalaId;
	}
	
	
	public void setidFolio(String idFolio){
		this.idFolio = idFolio;
	}

	public void setGarantia(String garantias){
		this.garantias=garantias;

	}
	
	public void setNombreSala(String nomSal){
		this.nombreSala = nomSal;
	}
	
	public void setSolServId(String solServId){
		this.solSerId = solServId;
	}

	public String getIdFolio(){
		return idFolio;
	}
	
	public String getNombreSala(){
		return nombreSala;
	}
	
	public String getSolServId(){
		return solSerId;
	}

	public String getEstatus(){
		return estatus;
	}

	public String getNumsFolio() {
		return numsFolio;
	}

	public String getGarantias(){
		return garantias;

	}
}
