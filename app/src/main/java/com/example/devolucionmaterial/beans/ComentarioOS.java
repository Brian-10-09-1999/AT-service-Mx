package com.example.devolucionmaterial.beans;

/**
 * Created by EDGAR ARANA on 12/09/2017.
 */

public class ComentarioOS {
    private String idOS, hora, fecha, comentario, estatusDeEntrega;
    private int idElementoOS;
    private String Comentario_url;

    public ComentarioOS(String idOS, String hora, String fecha, String comentario, String Comentario_url, String estatusDeEntrega, int idElementoOS) {
        this.idOS = idOS;
        this.hora = hora;
        this.fecha = fecha;
        this.comentario = comentario;

        this.Comentario_url = Comentario_url;

        this.estatusDeEntrega = estatusDeEntrega;
        this.idElementoOS = idElementoOS;
    }

    public String getIdOS() {
        return idOS;
    }

    public String getHora() {
        return hora;
    }

    public String getFecha() {
        return fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public String getComentario_url() {
        return Comentario_url;
    }

    public String getEstatusDeEntrega() {
        return estatusDeEntrega;
    }

    public int getIdElementoOS() {
        return idElementoOS;
    }
}