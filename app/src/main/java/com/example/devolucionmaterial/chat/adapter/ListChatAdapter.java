package com.example.devolucionmaterial.chat.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.api.ServiceApi;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.db.DatabaseChatHelper;
import com.example.devolucionmaterial.chat.model.ChatList;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;


import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrador on 20/12/2016.
 */

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.ChatsViewHolder> {

    private List<ChatList> chatListList;
    private Context context;
    private ClickListener clickListener;
    private DBChatManager dbChatManager;
    private OnUpdate onUpdate;
    private PrefrerenceChat prefrerenceChat;


    public ListChatAdapter(List<ChatList> chatListList, Context context) {
        this.chatListList = chatListList;
        this.context = context;
        prefrerenceChat = new PrefrerenceChat(context);
    }

    @Override
    public void onBindViewHolder(final ChatsViewHolder chatsViewHolder, final int i) {

        chatsViewHolder.chatName.setText(chatListList.get(i).getTitle());
        chatsViewHolder.chatDesc.setText(chatListList.get(i).getDesc());

        chatsViewHolder.tvtimeStamp.setText(new SimpleDateFormat("HH:mm").format(chatListList.get(i).getTimeStamp()));
        Glide.with(chatsViewHolder.chatPhoto.getContext())
                .load(chatListList.get(i).getImage()).
                centerCrop().
                transform(new CircleTransform(chatsViewHolder.chatPhoto.getContext()))
                .override(40, 40).into(chatsViewHolder.chatPhoto);

        //si es 1 es que ya esta leido los mensjes de ese chat
        if (chatListList.get(i).getStatus() == 1) {
            chatsViewHolder.rv_acount.setVisibility(View.GONE);
        } else {
            chatsViewHolder.rv_acount.setVisibility(View.VISIBLE);
        }

        if (chatListList.get(i).getType() == DatabaseChatHelper.TYPE_CHAT_INDIVIDUAL) {
            chatsViewHolder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    CharSequence colors[] = new CharSequence[]{"Borrar chat"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Seleccione una opción");
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbChatManager = new DBChatManager(context);
                            dbChatManager.open();
                            if (which == 0) {
                                dbChatManager.deleteChatComplete(chatListList.get(i).getId_chat());

                                notifyDataSetChanged();
                                if (onUpdate != null) {
                                    onUpdate.updateInterface();
                                }
                            }
                        }
                    });
                    builder.show();
                    return false;
                }
            });

            // TODO: 12/04/2017  menu para el chat en grupo
        } else if (chatListList.get(i).getType() == DatabaseChatHelper.TYPE_CHAT_GROUP) {

            if (Integer.valueOf(chatListList.get(i).getUsers()) == prefrerenceChat.getTokenChat()) {

                chatsViewHolder.root.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        CharSequence colors[] = new CharSequence[]{"Terminar grupo"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Menu grupo");
                        builder.setItems(colors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    new MaterialDialog.Builder(context)
                                            .iconRes(android.R.drawable.ic_dialog_alert)
                                            .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                                            .title(R.string.finish_group)
                                            .content(R.string.seguro_determinar_grupo)
                                            .positiveText(R.string.aceptar).onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            finishGroup(i);
                                        }
                                    })
                                            .negativeText(R.string.cancelar)
                                            .show();
                                }
                            }
                        });
                        builder.show();
                        return false;
                    }
                });

            } else {
                chatsViewHolder.root.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new MaterialDialog.Builder(context)
                                .content("No se puede eliminar este grupo")
                                .iconRes(R.drawable.warning)
                                .positiveText(R.string.aceptar)
                                //.negativeText(R.string.disagree)
                                .show();
                        return false;
                    }
                });

                // TODO: 12/04/2017 si el chat no se puede elmimnar
                //Toast.makeText(context, "No se puede eliminar este grupo", Toast.LENGTH_SHORT).show();
                //chatsViewHolder.ivBlock.setVisibility(View.VISIBLE);
            }
        }
        chatsViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.itemClicked(i);
                }
            }
        });

    }


    @Override
    public ChatsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_chats, viewGroup, false);
        ChatsViewHolder pvh = new ChatsViewHolder(v);
        return pvh;
    }

    @Override
    public int getItemCount() {
        return chatListList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface OnUpdate {
        void updateInterface();
    }

    public void setOnUpdate(OnUpdate onUpdate) {
        this.onUpdate = onUpdate;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public interface ClickListener {
        void itemClicked(int item);
    }


    static class ChatsViewHolder extends RecyclerView.ViewHolder {
        TextView chatName;
        TextView chatDesc;
        TextView tvtimeStamp;
        ImageView chatPhoto;
        ImageView ivBlock;

        RelativeLayout rv_acount;
        FrameLayout root;


        ChatsViewHolder(View itemView) {
            super(itemView);
            chatName = (TextView) itemView.findViewById(R.id.chat_name);
            chatDesc = (TextView) itemView.findViewById(R.id.chat_desc);
            tvtimeStamp = (TextView) itemView.findViewById(R.id.ilc_tv_time);
            chatPhoto = (ImageView) itemView.findViewById(R.id.chat_photo);
            ivBlock = (ImageView) itemView.findViewById(R.id.itc_iv_bloque);

            rv_acount = (RelativeLayout) itemView.findViewById(R.id.ilc_rv_acount_chat);
            root = (FrameLayout) itemView.findViewById(R.id.ilc_root);
        }
    }


    private void finishGroup(final int position) {
        dbChatManager = new DBChatManager(context);
        dbChatManager.open();
        final int chat_id = chatListList.get(position).getId_chat();

        final MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(context.getString(R.string.Conectando_con_servidor_remoto))
                .content("Cargando...")
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(false)
                .show();
        RequestBody idChat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(chat_id));
        Log.e("id", String.valueOf(chat_id));

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<ResponseCall> call = serviceApi.finishChatGroup(idChat);
        call.enqueue(new Callback<ResponseCall>() {
            @Override
            public void onResponse(Call<ResponseCall> call, Response<ResponseCall> response) {
                materialDialog.dismiss();
                try {
                    dbChatManager.deleteChatComplete(chat_id);
                    chatListList.clear();
                    chatListList = dbChatManager.getListChat();
                    notifyDataSetChanged();
                    if (onUpdate != null) {
                        onUpdate.updateInterface();
                    }
                } catch (Exception e) {

                    new MaterialDialog.Builder(context)
                            .content(R.string.ocurrio_un_error)
                            .positiveText(R.string.aceptar)
                            //.negativeText(R.string.disagree)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResponseCall> call, Throwable t) {
                materialDialog.dismiss();
                new MaterialDialog.Builder(context)
                        .content(R.string.error_conect)
                        .positiveText(R.string.aceptar)
                        .show();
            }
        });
    }

}