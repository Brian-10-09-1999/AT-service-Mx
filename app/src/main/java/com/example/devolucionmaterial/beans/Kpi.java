package com.example.devolucionmaterial.beans;

/**
 * Created by EDGAR ARANA on 05/06/2017.
 */

public class Kpi {

   /* private String operador;
    private String casino;
    private String maquinas;
    private String b;
    private String bw;
    private String bwp;
    private String bk;
    private String incidencias;
    private String noOperativa;
    private String Abiertas;
    private String cerradas;
    private String porcentaje;*/

    private int id;
    private String clientes;
    private String Incidencias_totales;
    private String Incidencias_por_cliente;
    private String Incidencias_ST;
    private String Pendientes;
    private String Cerrados;
    private String Promedio_dias;
    private String Promedio_horas_porcentaje_de_eficiencia;
    private String porcentaje;
    private String Abiertas_no;
    private String cerradas_no;
    private String porcentaje_no;
    private int mes;

    public Kpi() {

    }

    // TODO: 14/06/2017 para los nombres solamnet
    public Kpi(String clientes) {

        this.clientes = clientes;

    }


    public Kpi(
            int id,
            String clientes,
            String Incidencias_totales,
            String Incidencias_por_cliente,
            String Incidencias_ST,
            String Pendientes,
            String Cerrados,
            String Promedio_dias,
            String Promedio_horas_porcentaje_de_eficiencia,
            String porcentaje,
            String Abiertas_no,
            String cerradas_no,
            String porcentaje_no,
            int mes) {
        this.id = id;
        this.clientes = clientes;
        this.Incidencias_totales = Incidencias_totales;
        this.Incidencias_por_cliente = Incidencias_por_cliente;
        this.Incidencias_ST = Incidencias_ST;
        this.Pendientes = Pendientes;
        this.Cerrados = Cerrados;
        this.Promedio_dias = Promedio_dias;
        this.Promedio_horas_porcentaje_de_eficiencia = Promedio_horas_porcentaje_de_eficiencia;
        this.porcentaje = porcentaje;
        this.Abiertas_no = Abiertas_no;
        this.cerradas_no = cerradas_no;
        this.porcentaje_no = porcentaje_no;
        this.mes = mes;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientes() {
        return clientes;
    }

    public void setClientes(String clientes) {
        this.clientes = clientes;
    }

    public String getIncidencias_totales() {
        return Incidencias_totales;
    }

    public void setIncidencias_totales(String incidencias_totales) {
        Incidencias_totales = incidencias_totales;
    }

    public String getIncidencias_por_cliente() {
        return Incidencias_por_cliente;
    }

    public void setIncidencias_por_cliente(String incidencias_por_cliente) {
        Incidencias_por_cliente = incidencias_por_cliente;
    }

    public String getIncidencias_ST() {
        return Incidencias_ST;
    }

    public void setIncidencias_ST(String incidencias_ST) {
        Incidencias_ST = incidencias_ST;
    }

    public String getPendientes() {
        return Pendientes;
    }

    public void setPendientes(String pendientes) {
        Pendientes = pendientes;
    }

    public String getCerrados() {
        return Cerrados;
    }

    public void setCerrados(String cerrados) {
        Cerrados = cerrados;
    }

    public String getPromedio_dias() {
        return Promedio_dias;
    }

    public void setPromedio_dias(String promedio_dias) {
        Promedio_dias = promedio_dias;
    }

    public String getPromedio_horas_porcentaje_de_eficiencia() {
        return Promedio_horas_porcentaje_de_eficiencia;
    }

    public void setPromedio_horas_porcentaje_de_eficiencia(String promedio_horas_porcentaje_de_eficiencia) {
        Promedio_horas_porcentaje_de_eficiencia = promedio_horas_porcentaje_de_eficiencia;
    }

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getAbiertas_no() {
        return Abiertas_no;
    }

    public void setAbiertas_no(String abiertas_no) {
        Abiertas_no = abiertas_no;
    }

    public String getCerradas_no() {
        return cerradas_no;
    }

    public void setCerradas_no(String cerradas_no) {
        this.cerradas_no = cerradas_no;
    }

    public String getPorcentaje_no() {
        return porcentaje_no;
    }

    public void setPorcentaje_no(String porcentaje_no) {
        this.porcentaje_no = porcentaje_no;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }
}
