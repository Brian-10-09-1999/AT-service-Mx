package com.example.devolucionmaterial.static_class;
public class ServerErrors {
    private int tipoError;
    private String otroError;

    public ServerErrors(int tipoError, String otroError){
        this.tipoError = tipoError;
        this.otroError = otroError;
    }

    public String getErrorServer(){
        String mensaje;
        switch (tipoError){
            case 400:
                mensaje = "Existe un problema con la programación del sitio. Contacte al área de programación de ZITRO.";
                break;
            case 409:
                mensaje = "Existe un conflicto con el protocolo HTTP.";
                break;
            case 417:
                mensaje = "Existe un fallo de expectativa. Contacte al área de programación de ZITRO";
                break;
            case 403:
                mensaje = "El acceso a este recurso esta denegado. Contacte al área de programación de ZITRO";
                break;
            case 410:
                mensaje = "El acceso al recurso ya no existe en la ubicación seleccionado. Contacte al área de programación de ZITRO";
                break;
            case 204:
                mensaje = "No existe contenido en el recurso solicitado. Contacte al área de programación de ZITRO";
                break;
            case 404:
                mensaje = "El recurso solicitado no existe o esta en mantenimiento. Si el problema persiste por varios minutos, contacte al área de programación";
                break;
            case 503:
                mensaje = "Servidor no disponible debido a una sobrecarga de trabajo temporal o por mantenimiento. Espera unos minutos y vuelve a intentarlo";
                break;
            case 500:
                mensaje = "Error Interno del servidor. Contacte al área de programación de ZITRO";
                break;
            case 429:
                mensaje = "Demasiadas peticiones a la vez. Espera unos minutos porfavor";
                break;
            default:
                mensaje = "Codigo de Error : "+tipoError+"\n ERROR : "+otroError;
        }
        return  mensaje;
    }
}
