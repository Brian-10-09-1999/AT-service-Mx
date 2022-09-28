package com.example.devolucionmaterial.chat.api;

import com.example.devolucionmaterial.Application;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.beans.BeansGlobales;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;
import com.example.devolucionmaterial.chat.api.model.ResponseMessage;
import com.example.devolucionmaterial.chat.model.Contact;
import com.example.devolucionmaterial.chat.model.MessageCheckout;
import com.example.devolucionmaterial.chat.model.TokenGroup;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Administrador on 02/03/2017.
 */

public interface ServiceApi {

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BeansGlobales.getUrl())
            .client(Application.httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    @POST("chat/enviarMensaje.php")
    Call<MessageCheckout> sendMessegeChat(@Body RequestBody params);


    @POST("chat/enviarMensajeImagen.php")
    Call<MessageCheckout> sendMessegeChatImagen(@Body RequestBody params);


    // TODO: 10/03/2017 sube la imagen al servidor
    @Multipart
    @POST("imageUpload.php")
    Call<ResponseCall> sendImageChat(@Part MultipartBody.Part file, @Part("url") String url);

    @GET("chat/contactChat.php")
    Call<List<Contact>> OBTENER_CONTACTOS_CHAT();


    // TODO: 27/03/2017
    @POST("chat/crearNuevogrupo.php")
    Call<TokenGroup> createGroup(@Body RequestBody params);

    // TODO: 06/04/2017 manda el id delchat de grupo y los usuario a agregar
    @POST("chat/addMiembro.php")
    Call<ResponseCall> addMember(@Body RequestBody params);

    // TODO: 06/04/2017 manda el id delchat de grupo y el id que se va eliminar
    @POST("chat/deleteMemeberChatGroup.php")
    Call<ResponseCall> deleteMember(@Body RequestBody params);

    // TODO: 06/04/2017 termina el grupo
    @Multipart
    @POST("chat/finishChatGroup.php")
    Call<ResponseCall> finishChatGroup(
            @Part("id_chat") RequestBody idChat
    );

    // TODO: 06/04/2017 recupera los chats si es que ya pertenecia en un grupo si es que se reinstala la app
    @Multipart
    @POST("chat/recoverChats.php")
    Call<ResponseCall> recoverChats(
            @Part("id_user") RequestBody id_user

    );


}
