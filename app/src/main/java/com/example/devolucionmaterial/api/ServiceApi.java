package com.example.devolucionmaterial.api;

import com.example.devolucionmaterial.Application;
import com.example.devolucionmaterial.activitys.RemplazoComponente.ModelRC;
import com.example.devolucionmaterial.activitys.codigoqr.InfoCodigoQRActivity;
import com.example.devolucionmaterial.activitys.codigoqr.model.InfoQR;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model.PedidoQr;
import com.example.devolucionmaterial.beans.BeanGaleria;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.beans.BeanSMDetalles;
import com.example.devolucionmaterial.beans.BeanSolicitudMaterial;
import com.example.devolucionmaterial.beans.BeansGlobales;
import com.example.devolucionmaterial.beans.CrearPedido;
import com.example.devolucionmaterial.beans.FallaCrearFolio;
import com.example.devolucionmaterial.beans.HorsGrafic;
import com.example.devolucionmaterial.beans.ImageOS;
import com.example.devolucionmaterial.beans.LicenciaPorSala;
import com.example.devolucionmaterial.beans.Token;
import com.example.devolucionmaterial.beans.createFolio;
import com.example.devolucionmaterial.beans.ingresaSalidaMat2;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Usuario on 07/11/2016.
 * TODO ESTA INTERFACE SE CREA PARA HACER LA CONEXION HTTP AL SERVIDOR
 */

public interface ServiceApi {

    // TODO: 08/11/2016  este objeto retrofit recibe la url general
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BeansGlobales.getUrl())
            .client(Application.httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    /*@GET("listaReiting.php")
    Call<List<ListaReiting>> listaRaiting();
*/
    // TODO: 08/11/2016  a este metodo se le coloca el endpoint al que va apuntar y se le ppasan los parametros que requiere el enpoint
    @FormUrlEncoded
    @POST("solitudMaterial.php")
    Call<List<BeanSolicitudMaterial>> SOLICITUD_MATERIAL_CALL(
            @Field("funcion") String funcion,
            @Field("tecnicoid") String tecnicoid);

    // TODO: 28/11/2016  hace una consulta y regresa una lista de solicitudes
    @FormUrlEncoded
    @POST("solitudMaterial.php")
    Call<List<BeanSMDetalles>> SOLICITUD_MATERIAL_CALL_DETALLES(@Field("funcion") String funcion, @Field("solicitud_refaccionid") String solicitud_refaccionid);

    // TODO: 09/11/2016 se actializa el status del pedido
    @FormUrlEncoded
    @POST("solitudMaterial.php")
    Call<BeanResponse> ACTUALIZAR_SM_CALL(@Field("funcion") String funcion,
                                          @Field("estatusidfk") int estatusidfk, @Field("solicitud_refaccionid") String solicitud_refaccionid);

    // TODO: 28/11/2016 actualiza el valor del token de notificaciones
    @FormUrlEncoded
    @POST("chat/token.php")
    Call<Token> ACTUALIZAR_TOKEN(
            @Field("tecnicoid") String tecnicoid,
            @Field("token_id") String token_id);

    // TODO: 19/01/2017 envia el comentario del reporte al servidor
    @FormUrlEncoded
    @POST("guardarComentarioReporteFolio.php")
    Call<BeanResponse> CARGAR_COMENTARIIO(
            @Field("solx") String solicitud,
            @Field("comentx") String comentario);

    // TODO: 25/11/2016  envia la el nombre y el cargo de la supervision de la sala a la tabal tsupervision_sala
    @FormUrlEncoded
    @POST("datosSupervisionSala.php")
    Call<BeanResponse> DATOS_SUPERVISION_SALA(
            @Field("folioidf") String folio,
            @Field("nombre") String nombre, @Field("cargo") String cargo, @Field("salaid") int salaid);


    // TODO: 28/11/2016  revisa si existe contacto en la sala
    @FormUrlEncoded
    @POST("datosSupervisionSala.php")
    Call<BeanResponse> REVISAR_DATOS_SUPERVISION_SALA(
            @Field("folio_consulta") String folio, @Field("salaid") int salaid);

    // TODO: 29/11/2016  regresa el detalle de las devoluciones  en menu => materiales=> reportde devolucion => menu de reportes
    @FormUrlEncoded
    @POST("reporteDevolucionDetalles.php")
    Call<BeanResponse> RREPORTE_DEVELUCION_DETALLES(
            @Field("folio") int folio);

    // TODO: 07/12/2016 se conecta al php cuando la falla es visita de supervision "131"
    @FormUrlEncoded
    @POST("verFolioPendVisitaSupervision.php")
    Call<BeanResponse> ACTIVIDADES_TECNICO_FOLIO(
            @Field("estatusx") String estatusx,
            @Field("respuestax") String respuestax,
            @Field("solicitudx") String solicitudx,
            @Field("tecnicoidx") String tecnicoidx,
            @Field("estatusTecx") String estatusTecx,
            @Field("estEnServx") String estEnServx);


    // TODO: 08/12/2016 sustituye al metodo pedirDatosReporteFolioPendiente de la actividad ReportFolioPendiente
    @GET("verFolioPend.php?")
    Call<BeanResponse> OBTENER_ACTIVIDADES(@Query("idtecnico") String id,
                                           @Query("password") String password,
                                           @Query("solx") String solicitud);

    // TODO: 09/12/2016 cerra el folio que ya estan termiandos
    @FormUrlEncoded
    @POST("terminarFolio.php")
    Call<BeanResponse> CERRAR_FOLIO_PENDIENTES(
            @Field("foliox") String foliox,
            @Field("idtecnico") String idtecnico);

    @FormUrlEncoded
    @POST("guardarUbicacion.php")
    Call<BeanResponse> ENVIAR_LOC_EQUIPO(
            @Field("idtecnico") String idtecnico,
            @Field("lat") String lat,
            @Field("long") String lon,
            @Field("fecha") String fecha);

    /***
     * conexiones para subir y bajasr imagenes o videos
     *
     * */


    // TODO: 15/03/2017  se envia el video al servidor
    @Multipart
    @POST("CU_medios/subirMedios.php")
    Call<ResponseCall> sendMedios(@Part MultipartBody.Part file,
                                  @Part("foliox") RequestBody folio,
                                  @Part("usuarioIDx") RequestBody usuario,
                                  @Part("paisIDx") RequestBody paisIDx,
                                  @Part("type") RequestBody tipo
    );

    // TODO: 16/03/2017 obtienes los url  de las incidencias
    @Multipart
    @POST("CU_medios/listaGaleriasMedios.php")
    Call<BeanGaleria> getMedios(@Part("folioOS") RequestBody folio,
                                @Part("dominio") RequestBody dominio
    );

    /***
     * conexiones para crear folios
     *
     * */
    // TODO: 05/05/2017 regresa las licenias dependiendo del id de la sala
    @Multipart
    @POST("traeLicencias.php")
    Call<List<LicenciaPorSala>> getLicenciasPorSala(@Part("salaid") RequestBody sala);

    // TODO: 05/05/2017 ontiene todas la incidencias
    @GET("spinerFallas.php")
    Call<List<FallaCrearFolio>> getFallasMaquinas();

    @FormUrlEncoded
    @POST("spinerSubfallas.php")
    Call<List<FallaCrearFolio>> setFallaId(@Field("fallaid") String fallaid);


    @FormUrlEncoded
    @POST("nuevoFolioEsp.php")
    Call<createFolio> createFolio(
            @Field("origenLlamada") String llamada,
            @Field("medioContacto") String medioContacto,
            @Field("motivo") String motivo,
            @Field("region") String region,
            @Field("sala") String sala,
            @Field("licencia") String licencia,
            @Field("ip") String ip,
            @Field("juego") String juego,
            @Field("version") String version,
            @Field("serie") String serie,
            @Field("gabinete") String gabinete,
            @Field("modelo") String modelo,
            @Field("falla") String falla,
            @Field("subfalla") String subfalla,
            @Field("noMaquina") String noMaquina,
            @Field("reclamado") String reclamado,
            @Field("validado") String validado,
            @Field("maquinaInoperaiva") String maquinaInoperaiva,
            @Field("comentarios") String comentarios,
            @Field("id") String usuario
    );

    @FormUrlEncoded
    @POST("ingresaPedido.php")
    Call<CrearPedido> sendCrearPedido(
            @Field("salaid") int sala,
            @Field("codigoMaq") String maquina,
            @Field("codigoCom") String componente,
            @Field("tecnicoid") String tecnicoid,
            @Field("folio") String folio
    );

    // TODO: 29/12/2016 con este metodo se envian json
    @POST("ingresaSalidaMat2.php")
    Call<ingresaSalidaMat2> sendDevolcionDeMaterial(@Body RequestBody params);


    // TODO: 26/06/2017 servicio qu eregresa inidecias para las graficas
    @FormUrlEncoded
    @POST("KPI/foliosAbiertos.php")
    Call<HorsGrafic> sendClienteGafica(
            @Field("clientex") String cliente
    );


    // TODO: 01/09/2017 servico de qr pedidos
    @FormUrlEncoded
    @POST("codigoQR/verificaDatospedido.php")
    Call<PedidoQr> setVerificardatosPedido(
            @Field("codigoMaq") String codigoMaq,
            @Field("codigoCom") String codigoCom,
            @Field("folioCC") String folioCC,
            @Field("tecnicoid") String tecnicoid
    );


    // TODO: 28/08/2017 solo se usa el result de este bean
    @FormUrlEncoded
    @POST("codigoQR/reemplazo2.php")
    Call<BeanResponse> sendValidationPedido(
            @Field("idmaquina") String codigoMaq,
            @Field("codigo") String codAnteriro,
            @Field("codigo2") String codNuevo,
            @Field("usuID") String tecnico,
            @Field("pedido") int pedido
    );

    // TODO: 28/08/2017 solo se usa el result, devolucionid de este bean
    @FormUrlEncoded
    @POST("codigoQR/ingresaDevolucion.php")
    Call<BeanResponse> devolutionQR(
            @Field("pedido") int pedido,
            @Field("codigo") String codigoCom,
            @Field("folioCC") String folioCC,
            @Field("tecnicoid") int tecnicoid,
            @Field("salidaid") int salidaid
    );


    // TODO: 11/09/2017 este cambia la manera d e subir la imagenes de la os
    @Multipart
    @POST("SubirFotoFromAndroidToPhp.php")
    Call<ImageOS> uploadImageOS(
            @Part List<MultipartBody.Part> files,
            @Part("folioOS") RequestBody folioOS,
            @Part("idusu") RequestBody idusu,
            @Part("urlAbsoluta") RequestBody urlAbsoluta
    );

    // TODO: 21-06-2022 Los tecnicos suben fotos de las islas en salas
    @Multipart
    @POST("cargaFotosAliMaquOS.php")
    Call<ImageOS> uploadSalaFotoOS(
            @Part List<MultipartBody.Part> files,
            @Part("folioOS") RequestBody folioOS,
            @Part("nombreAli") RequestBody nombreAli,
            @Part("estatus") RequestBody estatus,
            @Part("idusu") RequestBody idusu
    );


    /**
     * se usa de @link{@BeanResponse} solo result y comment
     */
    @FormUrlEncoded
    @POST("codigoQR/recibirGuia.php")
    Call<BeanResponse> enviarGuia(
            @Field("devolucionId") int devolucionId,
            @Field("guiax") String guiax
    );


    /**
     *  Activity {@link InfoCodigoQRActivity }
     * Pojo {@link InfoQR }
     **/
    @FormUrlEncoded
    @POST("codigoQR/validacionMacCom.php")
    Call<InfoQR> sendValidationCodeQR(
            @Field("codigox") String codigox
    );



    //Unicamente usado para remplazo de componentes esta funcionalidad no esta disponible para todos los tecnicos
    // TODO: 01/09/2017 servico de qr pedidos
    @FormUrlEncoded
    @POST("codigoQR/reemplazoProduccion.php")
    Call<ModelRC> RemplazoComponente(@Field("idmaquina") String codigoMaq, @Field("codigo") String codigoCom, @Field("codigo2")   String codigoNewCom, @Field("usuID") String tecnicoid, @Field("ordenid") String ordenid);
    //servicios para Boton Rack

}