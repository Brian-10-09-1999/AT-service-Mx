package com.example.devolucionmaterial.chat.adapter;

import android.app.Activity;
import android.content.Context;

import android.graphics.Point;
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


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.activitys.ChatActivity;
import com.example.devolucionmaterial.chat.api.ProgressRequestBody;
import com.example.devolucionmaterial.chat.api.ServiceApi;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.db.DatabaseChatHelper;
import com.example.devolucionmaterial.chat.holder.ChatsViewHolder;
import com.example.devolucionmaterial.chat.interface_chat.ClickListenerChat;
import com.example.devolucionmaterial.chat.interface_chat.OnLoadMoreListener;
import com.example.devolucionmaterial.chat.model.MessageCheckout;
import com.example.devolucionmaterial.chat.model.ModelChat;
import com.example.devolucionmaterial.chat.utils.MyMessageStatusFormatter;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


import java.util.ArrayList;
import java.util.List;

//import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by WingHinChan on 2015/12/23.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ChatsViewHolder.OnChangeItemAdapter {
    public static final int RIGHT_MSG = 0;
    public static final int LEFT_MSG = 1;
    public static final int RIGHT_MSG_IMG = 2;
    public static final int LEFT_MSG_IMG = 3;
    private static final int VIEW_PROG = 4;

    public static final int chat_success = 1;
    public static final int chat_error = 3;

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

    public ServiceApi serviceApi;
    public Call<MessageCheckout> call;
    public Call<ResponseCall> callSendImage;

    public ChatAdapter(ArrayList<ModelChat> myDataSet, RecyclerView recyclerView, Activity activity) {
        this.modelChats = myDataSet;
        this.activity = activity;
        this.context = activity;
        serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dbChatManager = new DBChatManager(context).open();
        myMessageStatusFormatter = new MyMessageStatusFormatter(context);
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager
                && modelChats.size() >= ChatActivity.acountItemMax) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            return new ChatsViewHolder(view, mLayoutInflater, myMessageStatusFormatter, clickListenerChat, activity, modelChats, dbChatManager, serviceApi, call, callSendImage);
        } else if (viewType == LEFT_MSG) {
            // TODO: 08/02/2017 viewholder de los mensajes recibidos
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new ChatsViewHolder(view, mLayoutInflater, myMessageStatusFormatter, clickListenerChat, activity, modelChats, dbChatManager, serviceApi, call, callSendImage);
        } else if (viewType == RIGHT_MSG_IMG) {
            // TODO: 08/02/2017 viewholder de los imagenes enviados
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_img, parent, false);
            return new ChatsViewHolder(view, mLayoutInflater, myMessageStatusFormatter, clickListenerChat, activity, modelChats, dbChatManager, serviceApi, call, callSendImage);
        } else if (viewType == LEFT_MSG_IMG) {
            // TODO: 08/02/2017 viewholder de los imagenes recibidos
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_img, parent, false);
            return new ChatsViewHolder(view, mLayoutInflater, myMessageStatusFormatter, clickListenerChat, activity, modelChats, dbChatManager, serviceApi, call, callSendImage);
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
                //chatsViewHolder.setTxtMessageRecibe(modelChat.getMessage());
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
                chatsViewHolder.setOnChangeItemAdapter(this);

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

    @Override
    public void UpdateIU(int position) {
        notifyItemChanged(position);
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
    public static CharSequence converteTimestamp(String mileSegundos) {
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }


    public void setClickListenerChat(ClickListenerChat clickListener) {
        this.clickListenerChat = clickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }




}
