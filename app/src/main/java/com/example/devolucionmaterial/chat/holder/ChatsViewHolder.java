package com.example.devolucionmaterial.chat.holder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.adapter.ChatAdapter;
import com.example.devolucionmaterial.chat.adapter.CircleTransform;
import com.example.devolucionmaterial.chat.api.ProgressRequestBody;
import com.example.devolucionmaterial.chat.api.ServiceApi;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.db.DatabaseChatHelper;
import com.example.devolucionmaterial.chat.interface_chat.ClickListenerChat;
import com.example.devolucionmaterial.chat.model.MessageCheckout;
import com.example.devolucionmaterial.chat.model.ModelChat;
import com.example.devolucionmaterial.chat.utils.MyMessageStatusFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.Normalizer;
import java.util.List;

//import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by EDGAR ARANA on 11/10/2017.
 */

public class ChatsViewHolder extends RecyclerView.ViewHolder implements ProgressRequestBody.UploadCallbacks, View.OnClickListener {
    Boolean flagStatus = false;
    TextView tvTimestamp, tvLocation, tvUser;
    //EmojiconTextView txtMessage;
    ImageView ivUser, ivChatPhoto, statusIcon;
    Button btn_reintentar;
    View progress;
    FrameLayout statusContainer;
    View statusIconFame;
    Activity activity;

    private DBChatManager dbChatManager;

    public List<ModelChat> modelChats;

    public ServiceApi serviceApi;
    public Call<MessageCheckout> call;
    public Call<ResponseCall> callSendImage;

    private ClickListenerChat clickListenerChat;

    private MyMessageStatusFormatter myMessageStatusFormatter;

    OnChangeItemAdapter onChangeItemAdapter;

    public ChatsViewHolder(View itemView,
                           LayoutInflater mLayoutInflater,
                           MyMessageStatusFormatter myMessageStatusFormatter,
                           ClickListenerChat clickListenerChat,
                           Activity activity, List<ModelChat> modelChats, DBChatManager dbChatManager,
                           ServiceApi serviceApi, Call<MessageCheckout> call, Call<ResponseCall> callSendImage) {
        super(itemView);

        this.myMessageStatusFormatter = myMessageStatusFormatter;
        this.clickListenerChat = clickListenerChat;
        this.activity = activity;
        this.modelChats = modelChats;
        this.dbChatManager = dbChatManager;
        this.serviceApi = serviceApi;
        this.call = call;
        this.callSendImage = callSendImage;

        tvTimestamp = (TextView) itemView.findViewById(R.id.timestamp);
        //txtMessage = (EmojiconTextView) itemView.findViewById(R.id.txtMessage);
        tvUser = (TextView) itemView.findViewById(R.id.txtUser);
        tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
        ivChatPhoto = (ImageView) itemView.findViewById(R.id.img_chat);
        ivUser = (ImageView) itemView.findViewById(R.id.ivUserChat);
        // view de imageview
        btn_reintentar = (Button) itemView.findViewById(R.id.imri_btn_reintentar);

        progress = itemView.findViewById(R.id.imri_progress);
        statusContainer = (FrameLayout) itemView.findViewById(R.id.message_status_container);
        // view de estatus
        statusIconFame = mLayoutInflater.inflate(R.layout.status_icon_image_view, statusContainer);
        statusIcon = (ImageView) statusIconFame.findViewById(R.id.status_icon_image_view);


    }
/*
    public void setTxtMessageRecibe(String message) {
        if (txtMessage == null) return;
        txtMessage.setText(message);
    }
    */

    public void setTxtMessage(String message, final int position, long id, int status) {
        //if (txtMessage == null) return;
        //txtMessage.setText(message);
        statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(status));
        if (status != ChatAdapter.chat_success && !flagStatus)
            sendMessage(message, id, position);
    }

    public void setTxtUser(String user) {
        tvUser.setText(user);
    }

    void setIvUser(String urlPhotoUser) {
        if (ivUser == null) return;
        Glide.with(ivUser.getContext()).load(urlPhotoUser).centerCrop().transform(new CircleTransform(ivUser.getContext())).override(40, 40).into(ivUser);
    }

    public void setTvTimestamp(String timestamp) {
        if (tvTimestamp == null) return;
        tvTimestamp.setText(ChatAdapter.converteTimestamp(timestamp));
    }


    public void setIvChatPhotoRecibe(final String url, final int position) {
        if (ivChatPhoto == null) return;
        ivChatPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListenerChat != null) {
                    clickListenerChat.ClickListenerChat(url, position);
                }
            }
        });
        progress.setVisibility(View.VISIBLE);
        Glide.with(ivChatPhoto.getContext()).load(url)
                .override(150, 150)
                .thumbnail(0.5f)
                .crossFade().listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                progress.setVisibility(View.GONE);
                btn_reintentar.setVisibility(View.VISIBLE);
                btn_reintentar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_reintentar.setVisibility(View.GONE);
                        setIvChatPhotoRecibe(url, position);

                    }
                });
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model,
                                           Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progress.setVisibility(View.GONE);
                return false;
            }
        })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(ivChatPhoto);

    }

    public void setIvChatPhoto(final String url, final int position, long id, int status) {
        if (ivChatPhoto == null) return;
        ivChatPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListenerChat != null) {
                    clickListenerChat.ClickListenerChat(url, position);
                }
            }
        });
        btn_reintentar.setOnClickListener(this);
        // statusIcon es el estatus del chat
        statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(status));
        progress.setVisibility(View.VISIBLE);
        Glide.with(ivChatPhoto.getContext()).load(url)
                .override(150, 150)
                .thumbnail(0.5f).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                progress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progress.setVisibility(View.GONE);
                return false;
            }
        })
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(ivChatPhoto);

        //se checa el estatus del chat para saber si se debe enviar
        if (status != ChatAdapter.chat_success && !flagStatus)
            sendImage(url, id, position);

    }

    public void tvIsLocation(int visible) {
        if (tvLocation == null) return;
        tvLocation.setVisibility(visible);
    }

    // TODO: 02/03/2017 recibe la ubicacion de la imagen y el id de la basa de datos par aactualizar estatus del cat
    private void sendImage(String imagePath, final long id, final int position) {
        flagStatus = true;
        progress.setVisibility(View.VISIBLE);
        btn_reintentar.setVisibility(View.GONE);
        if (onChangeItemAdapter != null) {
            onChangeItemAdapter.UpdateIU(position);
        }

        File file = new File(imagePath);

        /**
         * se agrega un numero alaeatorio a las fots para que no cause probelmas a la hora de enviar
         **/
        String nombreArchivo = String.valueOf((int) (Math.random() * 95000546 + 1)) + "_" + limpiarAcentos(file.getName());
        /**
         * se agrega l aimagen a curpo  para enviarlo por retrofit
         * */
        ProgressRequestBody fileBody = new ProgressRequestBody(file, this);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file", nombreArchivo, fileBody);

        /**
         * se crea la instacia de retorit
         * */

        callSendImage = serviceApi.sendImageChat(filePart, "texto de prueba");
        callSendImage.enqueue(new Callback<ResponseCall>() {
            @Override
            public void onResponse(Call<ResponseCall> call, Response<ResponseCall> response) {
                flagStatus = false;

                if (response.isSuccessful()) {
                    if (response.body().getResult().equals("success")) {
                        Log.e("url", response.body().getFile());
                        // TODO: 10/03/2017 se recibe el url de la imagen enviada y se mada el json completo
                        sendMessageImage(response.body().getFile(), id, position);


                    } else {
                        Log.e("string_upload_fail", "no esta respondiendo bien");
                        statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(ChatAdapter.chat_error));
                        btn_reintentar.setVisibility(View.VISIBLE);



                    }

                } else {
                    Log.e("string_upload_fail", response.body().getFile());
                    statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(ChatAdapter.chat_error));
                    btn_reintentar.setVisibility(View.VISIBLE);
                }
                if (onChangeItemAdapter != null)
                    onChangeItemAdapter.UpdateIU(position);
            }

            @Override
            public void onFailure(Call<ResponseCall> call, Throwable t) {
                Log.e("error cargar imagen", String.valueOf(t));
                Log.e("url", String.valueOf(call.request().url()));
                flagStatus = false;
                statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(ChatAdapter.chat_error));
                btn_reintentar.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                if (onChangeItemAdapter != null)
                    onChangeItemAdapter.UpdateIU(position);


            }
        });

    }

    // TODO: 06/03/2017 recibe el json del mensaje
    private void sendMessageImage(String url, final long id, final int position) {
        flagStatus = true;
        JSONObject jsonObjectMessage = dbChatManager.getChatsDetailsOnly(id);

        try {
            jsonObjectMessage.put(DatabaseChatHelper.message, url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("jsonObjectMessage modificado ", String.valueOf(jsonObjectMessage));
        RequestBody bodyJsonMessge =
                RequestBody.create(MediaType.parse("application/json"), String.valueOf(jsonObjectMessage));

        call = serviceApi.sendMessegeChatImagen(bodyJsonMessge);
        call.enqueue(new Callback<MessageCheckout>() {
            @Override
            public void onResponse(Call<MessageCheckout> call, Response<MessageCheckout> response) {
                flagStatus = false;
                if (response.body().getSuccess() > 0) {
                    dbChatManager.updateStatusChatDetails(id, ChatAdapter.chat_success);
                    statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(ChatAdapter.chat_success));
                    ModelChat mc = null;
                    try {
                        mc = dbChatManager.getChatsDetailsUnoXuno(id).get(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    modelChats.set(position, new ModelChat(mc.getId(), mc.getMessage(), mc.getTimeStamp(), mc.getUser(), mc.getType(), ChatAdapter.chat_success));
                    //notifyDataSetChanged();
                    progress.setVisibility(View.GONE);
                } else {
                    statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(ChatAdapter.chat_error));
                }

            }

            @Override
            public void onFailure(Call<MessageCheckout> call, Throwable t) {
                flagStatus = false;
                Log.e("error", String.valueOf(t));
                statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(ChatAdapter.chat_error));
                btn_reintentar.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);


            }
        });

    }


    // TODO: 06/03/2017 recibe el json del mensaje
    private void sendMessage(String message, final long id, final int position) {
        flagStatus = true;
        JSONObject jsonObjectMessage = dbChatManager.getChatsDetailsOnly(id);
        RequestBody bodyJsonMessge =
                RequestBody.create(MediaType.parse("application/json"), String.valueOf(jsonObjectMessage));

        call = serviceApi.sendMessegeChat(bodyJsonMessge);
        call.enqueue(new Callback<MessageCheckout>() {
            @Override
            public void onResponse(Call<MessageCheckout> call, Response<MessageCheckout> response) {
                flagStatus = false;

                if (response.body().getSuccess() > 0) {
                    dbChatManager.updateStatusChatDetails(id, ChatAdapter.chat_success);
                    statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(ChatAdapter.chat_success));
                    ModelChat mc = null;
                    try {
                        mc = dbChatManager.getChatsDetailsUnoXuno(id).get(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    modelChats.set(position, new ModelChat(mc.getId(), mc.getMessage(), mc.getTimeStamp(), mc.getUser(), mc.getType(), ChatAdapter.chat_success));
                } else {
                    statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(ChatAdapter.chat_error));
                }

            }

            @Override
            public void onFailure(Call<MessageCheckout> call, Throwable t) {
                flagStatus = false;
                Log.e("error", String.valueOf(t));
                statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(ChatAdapter.chat_error));

            }
        });

    }

    @Override
    public void onProgressUpdate(int percentage) {

    }

    @Override
    public void onError(Exception e) {
        // progress.setVisibility(View.GONE);
    }

    @Override
    public void onFinish(String finish) {
        progress.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imri_btn_reintentar:
                //btn_reintentar.setVisibility(View.GONE);
                final ModelChat modelChat = modelChats.get(getAdapterPosition());
                sendImage(modelChat.getMessage(), modelChat.getId(), getAdapterPosition());
                break;
        }
    }

    public static String limpiarAcentos(String cadena) {
        String limpio = null;
        if (cadena != null) {

            String cadenaNormalize = Normalizer.normalize(cadena, Normalizer.Form.NFD);
            limpio = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
            limpio = limpio.replace("", "_");
        }
        return limpio;
    }

    /**
     * This interface is comunicaqute wirh {@link ChatAdapter]
     * and update IU
     */
    public interface OnChangeItemAdapter {
        void UpdateIU(int position);
    }

    public void setOnChangeItemAdapter(OnChangeItemAdapter onChangeItemAdapter) {
        this.onChangeItemAdapter = onChangeItemAdapter;
    }
}