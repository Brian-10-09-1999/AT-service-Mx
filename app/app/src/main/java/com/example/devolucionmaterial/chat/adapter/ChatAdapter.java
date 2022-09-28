package com.example.devolucionmaterial.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.activitys.ChatActivity;
import com.example.devolucionmaterial.chat.api.ProgressRequestBody;
import com.example.devolucionmaterial.chat.api.ServiceApi;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;
import com.example.devolucionmaterial.chat.api.model.ResponseMessage;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.db.DatabaseChatHelper;
import com.example.devolucionmaterial.chat.interface_chat.ClickListenerChat;
import com.example.devolucionmaterial.chat.interface_chat.OnLoadMoreListener;
import com.example.devolucionmaterial.chat.model.MessageCheckout;
import com.example.devolucionmaterial.chat.model.ModelChat;
import com.example.devolucionmaterial.chat.utils.MyMessageStatusFormatter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by WingHinChan on 2015/12/23.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int RIGHT_MSG = 0;
    public static final int LEFT_MSG = 1;
    public static final int RIGHT_MSG_IMG = 2;
    public static final int LEFT_MSG_IMG = 3;
    private static final int VIEW_PROG = 4;

    private static final int chat_success = 1;
    private static final int chat_error = 3;

    public List<ModelChat> modelChats;
    private Activity activity;
    private Context context;
    private ClickListenerChat clickListenerChat;

    private MyMessageStatusFormatter myMessageStatusFormatter;
    private LayoutInflater mLayoutInflater;

    private DBChatManager dbChatManager;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public ChatAdapter(ArrayList<ModelChat> myDataSet, RecyclerView recyclerView, Activity activity) {
        this.modelChats = myDataSet;
        this.activity = activity;
        this.context = activity;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dbChatManager = new DBChatManager(context);
        this.dbChatManager.open();
        myMessageStatusFormatter = new MyMessageStatusFormatter(context);
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager
                && modelChats.size() >= ChatActivity.acountItemMax) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    // TODO: 01/03/2017 este metodo se elige el tipo de vissta que va inflar por item el rv
    @Override
    public int getItemViewType(int position) {
        ModelChat modelChat = modelChats.get(position);
        //Log.e("typeview", String.valueOf(mc.getType()));
        if (modelChat.getType() == LEFT_MSG) {
            return LEFT_MSG;
        } else if (modelChat.getType() == RIGHT_MSG) {
            return RIGHT_MSG;
        } else if (modelChat.getType() == RIGHT_MSG_IMG) {
            return RIGHT_MSG_IMG;
        } else if (modelChat.getType() == LEFT_MSG_IMG) {
            return LEFT_MSG_IMG;
        } else {
            Log.e("VIEW_PROG", "VIEW_PROG");
            return VIEW_PROG;
            // throw new RuntimeException("ItemViewType unknown");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == RIGHT_MSG) {
            // TODO: 08/02/2017 viewholder de los mensajes enviados
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new ChatAdapter.ChatsViewHolder(view);
        } else if (viewType == LEFT_MSG) {
            // TODO: 08/02/2017 viewholder de los mensajes recibidos
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new ChatAdapter.ChatsViewHolder(view);
        } else if (viewType == RIGHT_MSG_IMG) {
            // TODO: 08/02/2017 viewholder de los imagenes enviados
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_img, parent, false);
            return new ChatAdapter.ChatsViewHolder(view);
        } else if (viewType == LEFT_MSG_IMG) {
            // TODO: 08/02/2017 viewholder de los imagenes recibidos
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_img, parent, false);
            return new ChatAdapter.ChatsViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_item, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ModelChat modelChat = modelChats.get(position);

        if (holder instanceof ChatsViewHolder) {
            ChatsViewHolder chatsViewHolder = (ChatsViewHolder) holder;
            // TODO: 06/03/2017 chat recibido
            if (modelChat.getType() == LEFT_MSG) {
                chatsViewHolder.setTxtMessageRecibe(modelChat.getMessage());
                chatsViewHolder.setTvTimestamp(modelChat.getTimeStamp());
                chatsViewHolder.setTxtUser(modelChat.getUser());
            } else if (modelChat.getType() == LEFT_MSG_IMG) {
                chatsViewHolder.setIvChatPhotoRecibe(modelChat.getMessage(), position);
                chatsViewHolder.setTvTimestamp(modelChat.getTimeStamp());
                chatsViewHolder.setTxtUser(modelChat.getUser());
            }
            // TODO: 06/03/2017 chat enviado
            else if (modelChat.getType() == RIGHT_MSG) {
                chatsViewHolder.setTxtMessage(modelChat.getMessage(), position, modelChat.getId(), modelChat.getStatus());
                chatsViewHolder.setTvTimestamp(modelChat.getTimeStamp());
                chatsViewHolder.setTxtUser(modelChat.getUser());

            }
            // TODO: 06/03/2017 imagen enviada
            else if (modelChat.getType() == RIGHT_MSG_IMG) {
                chatsViewHolder.setIvChatPhoto(modelChat.getMessage(), position, modelChat.getId(), modelChat.getStatus());
                chatsViewHolder.setTvTimestamp(modelChat.getTimeStamp());
            }
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return modelChats.size();
    }

    // TODO: 02/03/2017 es el loader que aparacese en el paginador
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }

    // TODO: 30/01/2017 convierte los milisegunbdos a fecha convencional
    private CharSequence converteTimestamp(String mileSegundos) {
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }


    public void setClickListenerChat(ClickListenerChat clickListener) {
        this.clickListenerChat = clickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private class ChatsViewHolder extends RecyclerView.ViewHolder implements ProgressRequestBody.UploadCallbacks, View.OnClickListener {
        Boolean flagStatus = false;
        TextView tvTimestamp, tvLocation, tvUser;
        EmojiconTextView txtMessage;
        ImageView ivUser, ivChatPhoto, statusIcon;
        Button btn_reintentar;
        View progress;
        FrameLayout statusContainer;
        View statusIconFame;

        ChatsViewHolder(View itemView) {
            super(itemView);
            tvTimestamp = (TextView) itemView.findViewById(R.id.timestamp);
            txtMessage = (EmojiconTextView) itemView.findViewById(R.id.txtMessage);
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

        void setTxtMessageRecibe(String message) {
            if (txtMessage == null) return;
            txtMessage.setText(message);
        }

        void setTxtMessage(String message, final int position, long id, int status) {
            if (txtMessage == null) return;
            txtMessage.setText(message);
            statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(status));
            if (status != chat_success && !flagStatus)
                sendMessage(message, id, position);
        }

        void setTxtUser(String user) {
            tvUser.setText(user);
        }

        void setIvUser(String urlPhotoUser) {
            if (ivUser == null) return;
            Glide.with(ivUser.getContext()).load(urlPhotoUser).centerCrop().transform(new CircleTransform(ivUser.getContext())).override(40, 40).into(ivUser);
        }

        void setTvTimestamp(String timestamp) {
            if (tvTimestamp == null) return;
            tvTimestamp.setText(converteTimestamp(timestamp));
        }

        void setIvChatPhotoRecibe(final String url, final int position) {
            if (ivChatPhoto == null) return;
            ivChatPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListenerChat != null) {
                        clickListenerChat.ClickListenerChat(url, position);
                    }
                }
            });
            Glide.with(ivChatPhoto.getContext()).load(url)
                    .override(150, 150)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(ivChatPhoto);

        }

        void setIvChatPhoto(final String url, final int position, long id, int status) {
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
            Glide.with(ivChatPhoto.getContext()).load(url)
                    .override(150, 150)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(ivChatPhoto);

            //se checa el estatus del chat para saber si se debe enviar
            if (status != chat_success && !flagStatus)
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
            /**
             * se obtienen la densidad  de la pantalla para ajustar bien la imagen
             * */
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            File file = new File(imagePath);

            /**
             * se agrega un numero alaeatorio a las fots para que no cause probelmas a la hora de enviar
             **/
            String nombreArchivo = String.valueOf((int) (Math.random() * 95000546 + 1)) + "_" + file.getName();
            /**
             * se agrega l aimagen a curpo  para enviarlo por retrofit
             * */
            ProgressRequestBody fileBody = new ProgressRequestBody(file, this);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("uploaded_file", nombreArchivo, fileBody);

            /**
             * se crea la instacia de retorit
             * */
            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<ResponseCall> call = serviceApi.sendImageChat(filePart, "texto de prueba");
            call.enqueue(new Callback<ResponseCall>() {
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
                            statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(chat_error));
                            btn_reintentar.setVisibility(View.VISIBLE);

                        }

                    } else {
                        Log.e("string_upload_fail", response.body().getFile());
                        statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(chat_error));
                        btn_reintentar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseCall> call, Throwable t) {
                    Log.e("error cargar imagen", String.valueOf(t));
                    Log.e("url", String.valueOf(call.request().url()));
                    flagStatus = false;
                    statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(chat_error));
                    btn_reintentar.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
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
            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<MessageCheckout> call = serviceApi.sendMessegeChatImagen(bodyJsonMessge);
            call.enqueue(new Callback<MessageCheckout>() {
                @Override
                public void onResponse(Call<MessageCheckout> call, Response<MessageCheckout> response) {
                    flagStatus = false;
                    if (response.body().getSuccess() > 0) {
                        dbChatManager.updateStatusChatDetails(id, chat_success);
                        statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(chat_success));
                        ModelChat mc = null;
                        try {
                            mc = dbChatManager.getChatsDetailsUnoXuno(id).get(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        modelChats.set(position, new ModelChat(mc.getId(), mc.getMessage(), mc.getTimeStamp(), mc.getUser(), mc.getType(), chat_success));
                        //notifyDataSetChanged();
                        progress.setVisibility(View.GONE);
                    } else {
                        statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(chat_error));
                    }

                }

                @Override
                public void onFailure(Call<MessageCheckout> call, Throwable t) {
                    flagStatus = false;
                    Log.e("error", String.valueOf(t));
                    statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(chat_error));
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


            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<MessageCheckout> call = serviceApi.sendMessegeChat(bodyJsonMessge);
            call.enqueue(new Callback<MessageCheckout>() {
                @Override
                public void onResponse(Call<MessageCheckout> call, Response<MessageCheckout> response) {
                    flagStatus = false;

                    if (response.body().getSuccess() > 0) {
                        dbChatManager.updateStatusChatDetails(id, chat_success);
                        statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(chat_success));
                        ModelChat mc = null;
                        try {
                            mc = dbChatManager.getChatsDetailsUnoXuno(id).get(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        modelChats.set(position, new ModelChat(mc.getId(), mc.getMessage(), mc.getTimeStamp(), mc.getUser(), mc.getType(), chat_success));
                    } else {
                        statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(chat_error));
                    }

                }

                @Override
                public void onFailure(Call<MessageCheckout> call, Throwable t) {
                    flagStatus = false;
                    Log.e("error", String.valueOf(t));
                    statusIcon.setImageDrawable(myMessageStatusFormatter.getStatusIcon(chat_error));
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
        public void onFinish() {
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
    }


}
