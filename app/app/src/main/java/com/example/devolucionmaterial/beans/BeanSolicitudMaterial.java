package com.example.devolucionmaterial.beans;

/**
 * Created by Usuario on 07/11/2016.
 */
// TODO: 08/11/2016  este es el modelado de datos que recibe como respuesta de ña comsulta de solicitud dse materiales
public class BeanSolicitudMaterial {
    int solicitud_refaccionid;
    String folio_solicitudidfk;
    String fecha_creacion;
    String salaidfk;

    public BeanSolicitudMaterial(int solicitud_refaccionid, String folio_solicitudidfk, String fecha_creacion, String salaidfk) {
        this.solicitud_refaccionid = solicitud_refaccionid;
        this.folio_solicitudidfk = folio_solicitudidfk;
        this.fecha_creacion = fecha_creacion;
        this.salaidfk = salaidfk;

    }

    public int getSolicitud_refaccionid() {
        return solicitud_refaccionid;
    }

    public String getFolio_solicitudidfk() {
        return folio_solicitudidfk;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public String getSalaidfk() {
        return salaidfk;
    }
}
