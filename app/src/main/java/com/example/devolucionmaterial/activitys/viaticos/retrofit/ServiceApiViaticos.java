package com.example.devolucionmaterial.activitys.viaticos.retrofit;

import com.example.devolucionmaterial.Application;
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

public interface ServiceApiViaticos {

    // TODO: 08/11/2016  este objeto retrofit recibe la url general
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BeansGlobales.getUrl())
            .client(Application.httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    /*@GET("listaReiting.php")
    Call<List<ListaReiting>> listaRaiting();
    */



    // TODO: 15/03/2017  se envia el video al servidor
    @Multipart
    @POST("Viaticos/agregaImagenViaticos.php")
    Call<ResponseCall> sendMedios(@Part MultipartBody.Part file,
                                  @Part("idgasto") RequestBody idgasto
                                  );

    @Multipart
    @POST("Viaticos/agregaImagenViaticos.php")
    Call<ResponseCallPdf> sendMedios2(@Part MultipartBody.Part file,
                                  @Part("idgasto") RequestBody idgasto
    );

    @Multipart
    @POST("Viaticos/agregaImagenViaticos.php")
    Call<ResponseCallXml> sendMedios3(@Part MultipartBody.Part file,
                                      @Part("idgasto") RequestBody idgasto
    );




}