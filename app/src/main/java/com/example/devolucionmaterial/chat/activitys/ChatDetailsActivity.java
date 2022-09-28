package com.example.devolucionmaterial.chat.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.abstrac.AbstractRvActivity;
import com.example.devolucionmaterial.chat.api.ServiceApi;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;
import com.example.devolucionmaterial.chat.db.DatabaseChatHelper;
import com.example.devolucionmaterial.chat.model.ChatList;
import com.example.devolucionmaterial.chat.model.ContactList;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;
import com.example.menuswipe.SwipeHorizontalMenuLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatDetailsActivity extends AbstractRvActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpRV();
    }

    void setUpRV() {
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                llm.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(llm);
    }

    @Override
    protected AbstractRvAdapter createAppAdapter(AbstractRvActivity baseRvActivity, List<ContactList> users) {
        return new SimpleRvAppAdapter(baseRvActivity, users);
    }

    private class SimpleRvAppAdapter extends AbstractRvAdapter {

        public SimpleRvAppAdapter(Context context, List<ContactList> users) {
            super(context, users);
        }

        @Override
        protected RecyclerView.ViewHolder createViewHolder(View itemView) {
            return new SimpleRvViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
            final ContactList singleContact = users.get(position);
            final SimpleRvViewHolder myViewHolder = (SimpleRvViewHolder) vh;

            if (manager) myViewHolder.btDelete.setVisibility(View.VISIBLE);
            else myViewHolder.btDelete.setVisibility(View.GONE);

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myViewHolder.sml.smoothOpenEndMenu();
                }
            });
            // TODO: 23/03/2017 boton para abrir chat individual
            myViewHolder.btOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ContactList singleContact = dbManager.getContactList().get(position);
                    if (dbManager.getIdContact(singleContact.getId_alias()).isEmpty()) {

                        // TODO: 23/03/2017 se inserta los miembros del grupo
                        dbManager.insertMembersGroup(singleContact.getId_alias(),
                                singleContact.getId_alias(),
                                singleContact.getName(),
                                singleContact.getAlias());
                        // TODO: 23/03/2017 se inserta el la tabala del chat
                        dbManager.insertChat(singleContact.getId_alias(),
                                singleContact.getName(),
                                singleContact.getAlias(),
                                R.drawable.ic_chat_icon_list,
                                singleContact.getAlias(),
                                DatabaseChatHelper.chatRead,
                                Long.valueOf(String.valueOf(Calendar.getInstance().getTime().getTime())),
                                DatabaseChatHelper.TYPE_CHAT_INDIVIDUAL);
                        Intent iChat = new Intent(mContext, ChatActivity.class);
                        iChat.putExtra("id", singleContact.getId_alias());
                        startActivity(iChat);
                    } else {
                        ChatList cl = dbManager.getIdContact(singleContact.getId_alias()).get(0);
                        Intent iChat = new Intent(mContext, ChatActivity.class);
                        iChat.putExtra("id", cl.getId_chat());
                        startActivity(iChat);
                    }
                }
            });

            myViewHolder.btDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // must close normal
                    final PrefrerenceChat prefrerenceChat = new PrefrerenceChat(ChatDetailsActivity.this);

                    new MaterialDialog.Builder(ChatDetailsActivity.this)
                            .title("Eliminar del chat")
                            .content("¿Estas seguro de elminar ?")
                            .positiveText(R.string.accept)
                            .negativeText(R.string.cancelar)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    JSONObject jsonDeleteMemberChatGroup = new JSONObject();
                                    JSONArray jsonArrayMembers = new JSONArray();
                                    jsonArrayMembers.put(singleContact.getId_alias());

                                    try {
                                        jsonDeleteMemberChatGroup.put("idchat", chat_id);
                                        jsonDeleteMemberChatGroup.put("members", jsonArrayMembers);
                                        jsonDeleteMemberChatGroup.put("emisor", prefrerenceChat.getTokenChat());


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("json delete  grupo ", String.valueOf(jsonDeleteMemberChatGroup));
                                    RequestBody jsonDeleteMember = RequestBody.create(MediaType.parse("text/plain"), jsonDeleteMemberChatGroup + "");
                                     final  MaterialDialog materialDialog;

                                    setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                    materialDialog= new MaterialDialog.Builder(mContext)
                                            .title(getString(R.string.Conectando_con_servidor_remoto))
                                            .content("Cargando...")
                                            .progress(true, 0)
                                            .cancelable(false)
                                            .progressIndeterminateStyle(false)
                                            .show();

                                    ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
                                    Call<ResponseCall> call = serviceApi.deleteMember(jsonDeleteMember);
                                    call.enqueue(new Callback<ResponseCall>() {
                                        @Override
                                        public void onResponse(Call<ResponseCall> call, Response<ResponseCall> response) {
                                            materialDialog.dismiss();
                                            try {
                                                if (response.body().getValue().equals("1")) {

                                                    dbManager.deleteMemberId(singleContact.getId_alias());
                                                    myViewHolder.sml.smoothCloseMenu();
                                                    users.remove(vh.getAdapterPosition());
                                                    mAdapter.notifyItemRemoved(vh.getAdapterPosition());
                                                    Toast.makeText(getApplicationContext(), "Miembro Eliminado", Toast.LENGTH_LONG).show();
                                                } else {
                                                    new MaterialDialog.Builder(ChatDetailsActivity.this)
                                                            .content(R.string.ocurrio_un_error)
                                                            .positiveText(R.string.aceptar)
                                                            .show();
                                                }
                                            } catch (Exception e) {

                                                new MaterialDialog.Builder(ChatDetailsActivity.this)
                                                        .content(R.string.ocurrio_un_error)
                                                        .positiveText(R.string.aceptar)
                                                        //.negativeText(R.string.disagree)
                                                        .show();
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<ResponseCall> call, Throwable t) {
                                            materialDialog.dismiss();
                                            new MaterialDialog.Builder(ChatDetailsActivity.this)
                                                    .content(R.string.error_conect)
                                                    .positiveText(R.string.aceptar)
                                                    //.negativeText(R.string.disagree)
                                                    .show();
                                        }
                                    });
                                }
                            })
                            .show();


                }
            });

          /*  myViewHolder.btLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Left click", Toast.LENGTH_SHORT).show();
                }
            });*/

            myViewHolder.tvName.setText(singleContact.getName());
            boolean swipeEnable = swipeEnableByViewType(getItemViewType(position));

            myViewHolder.sml.setSwipeEnable(swipeEnable);
        }
    }

    public static class SimpleRvViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        View btOpen;
        View btDelete;
        View btLeft;
        SwipeHorizontalMenuLayout sml;

        public SimpleRvViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);

            btOpen = itemView.findViewById(R.id.btOpen);
            btDelete = itemView.findViewById(R.id.btDelete);
            btLeft = itemView.findViewById(R.id.btLeft);
            sml = (SwipeHorizontalMenuLayout) itemView.findViewById(R.id.sml);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateRv();
    }


}
