package com.example.devolucionmaterial.chat.activitys;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.abstrac.AbstracChatActivity;
import com.example.devolucionmaterial.chat.adapter.ChatAdapter;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.db.DatabaseChatHelper;
import com.example.devolucionmaterial.chat.interface_chat.ClickListenerChat;
import com.example.devolucionmaterial.chat.interface_chat.CommunicationChannelChat;
import com.example.devolucionmaterial.chat.interface_chat.OnLoadMoreListener;
import com.example.devolucionmaterial.chat.model.ChatList;
import com.example.devolucionmaterial.chat.model.ContactList;
import com.example.devolucionmaterial.chat.model.ModelChat;
import com.example.devolucionmaterial.chat.task.BadgerAction;
import com.example.devolucionmaterial.chat.task.BrodcastBean;

import com.example.devolucionmaterial.chat.task.NotificationCancel;
import com.example.devolucionmaterial.chat.utils.MyBounceInterpolator;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
//import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AbstracChatActivity
        implements View.OnClickListener, ClickListenerChat, CommunicationChannelChat {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = this;
        dbManager = new DBChatManager(this);
        dbManager.open();
        // TODO: 29/03/2017 las preferencias del chat le da el status para saber si se activa las notificicaciones
        prefrerenceChat = new PrefrerenceChat(this);

        initToolbar();
        // TODO: 14/12/2016  con el shared preference se recuper el token
        if (getIntent().getExtras() != null) {
            id_chat = getIntent().getExtras().getInt("id");
            // TODO: 11/04/2017 cuando los nuevos mensajes son leidos se quita la notifiacion
            NotificationCancel.cancelNotification(ChatActivity.this, id_chat);
            // TODO: 05/07/2017 bager notificaion sobre el icono de escritorio}

            setUpTitleBar();
        }
        initBrodcast();
        setUp();
        initMenu();
        //initMenuFragment();
    }

    void setUpTitleBar() {
        // Log.e("id_", String.valueOf(id_chat));
        // TODO: 31/03/2017 actualiza el estatus de la lita de chat de quue fue leido
        dbManager.updateStatusChatListChange(id_chat, DatabaseChatHelper.chatRead);
        new BadgerAction(context).update();
        // TODO: 25/01/2017 se obtiene el tipo de chat
        ChatList cl = dbManager.getTypeChat(id_chat).get(0);
        typeChat = cl.getType();
        if (typeChat == 2) {
            tvTitle.setText(cl.getTitle());
            tvDesc.setText(getMembers(id_chat));
            // TODO: 05/04/2017 se valida si es el adm del grupo
            if (Integer.valueOf(cl.getUsers()) == prefrerenceChat.getTokenChat())
                manager = true;

        } else {
            tvTitle.setText(cl.getTitle());
            tvDesc.setVisibility(View.GONE);

        }
    }


    // TODO: 26/01/2017 se crea la cadena con los nombres de los miembros
    String getMembers(int id_chat) {
        String groupName = "";
        for (int i = 0; i < dbManager.getMembersChat(id_chat).size(); i++) {
            ContactList cl = dbManager.getMembersChat(id_chat).get(i);
            if (i == 0) {
                groupName = groupName + cl.getAlias();
            } else {
                groupName = groupName + "," + cl.getAlias();
            }
        }
        return groupName + ",tu";
    }


    void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTitle = (TextView) findViewById(R.id.tb_title_chat);
        tvDesc = (TextView) findViewById(R.id.tb_desc_chat);
        ll_toolbar_titles = (LinearLayout) findViewById(R.id.ac_ll_toolbar_titles);
        ll_toolbar_titles.setOnClickListener(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void setUp() {
        fragmentManager = getSupportFragmentManager();
        contentRoot = findViewById(R.id.contentRoot);
        //edMessage = (EmojiconEditText) findViewById(R.id.editTextMessage);
        btSendMessage = (ImageView) findViewById(R.id.buttonMessage);
        btSendMessage.setOnClickListener(this);
        btEmoji = (ImageView) findViewById(R.id.buttonEmoji);
        //emojIcon = new EmojIconActions(this, contentRoot, edMessage, btEmoji);
        //emojIcon.ShowEmojIcon();
        rvListMessage = (RecyclerView) findViewById(R.id.ac_messageRecyclerView);
        llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        rvListMessage.setLayoutManager(llm);
        modelChats = new ArrayList<>();
        try {
            modelChats = dbManager.getChatsDetails(id_chat);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error", String.valueOf(e));
        }
        chatAdapter = new ChatAdapter(modelChats, rvListMessage, ChatActivity.this);
        chatAdapter.setClickListenerChat(this);
        rvListMessage.setAdapter(chatAdapter);
        // TODO: 01/02/2017 es el escucha cuando va llegando a las ultimas possiones
        chatAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add progress item
                //si el chat trae menos de 20 mensajes no carga
                if (modelChats.size() >= acountItemMax) {
                    modelChats.add(new ModelChat(0, "", "", "", 4, DatabaseChatHelper.ChatSending));
                    chatAdapter.notifyItemInserted(modelChats.size() - 1);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //remove progress item
                            modelChats.remove(modelChats.size() - 1);
                            chatAdapter.notifyItemRemoved(modelChats.size());
                            //add items one by one
                            List<ModelChat> newList = null;
                            try {
                                // se consulta la ultima fecha para qu eel paginador cargue los demas items
                                Long time = Long.parseLong(modelChats.get(modelChats.size() - 1).getTimeStamp());
                                newList = dbManager.getChatsDetailsPage(id_chat, time);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            for (int i = 0; i < newList.size(); i++) {
                                ModelChat mc = newList.get(i);
                                modelChats.add(new ModelChat(mc.getId(), mc.getMessage(), mc.getTimeStamp(), mc.getUser(), mc.getType(), mc.getStatus()));
                                chatAdapter.notifyItemInserted(modelChats.size());
                            }
                            chatAdapter.setLoaded();

                            //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                        }
                    }, 1000);

                }
            }
        });
        // TODO: 02/02/2017 es la escuha por si la pantalla abre el teclado se ajuste el rv
        rvListMessage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    rvListMessage.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rvListMessage.smoothScrollToPosition(0);
                        }
                    }, 100);
                }
            }
        });
        rvListMessage.scrollToPosition(0);

    }

    // TODO: 11/10/2017 si es que entra en onresume se canselan los envios
    void cancelCall() {
        if (chatAdapter != null) {
            if (chatAdapter.call != null) {
                chatAdapter.call.cancel();
            }
            if (chatAdapter.callSendImage != null) {
                chatAdapter.callSendImage.cancel();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        prefrerenceChat.setStatusChat(online, id_chat);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefrerenceChat.setStatusChat(online, id_chat);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            setUpTitleBar();
        } catch (Exception e) {
            finish();
            Log.e("error", String.valueOf(e));
        }

        prefrerenceChat.setStatusChat(online, id_chat);
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefrerenceChat.setStatusChat(offline, id_chat);
        cancelCall();
    }

    @Override
    protected void onStop() {
        prefrerenceChat.setStatusChat(offline, id_chat);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefrerenceChat.setStatusChat(offline, id_chat);
        // TODO: 11/10/2017 si es que entra en onresume se canselan los envios
        cancelCall();
        unregisterReceiver(myReceiver);
        if (typeChatValidation()) {
            unregisterReceiver(receiverDeleteChatGroup);
            unregisterReceiver(receiverFinishChatGroup);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_lateral:
                if (!menuOpen) {
                    revealMenu();
                } else {
                    hideMenu();
                }
                return true;
            case R.id.clear_char:
                clearChat();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMessage:

                sendMessage();

                break;
            case R.id.ac_ll_toolbar_titles:
                if (typeChatValidation()) {
                    // TODO: 05/04/2017 revisa si es adm o no
                    Intent i = new Intent(ChatActivity.this, ChatDetailsActivity.class);
                    i.putExtra("id", id_chat);
                    i.putExtra("magaer", manager);
                    startActivity(i);

                }
                break;


        }
    }


    @Override
    public void onBackPressed() {

        if (menuOpen) {
            hideMenu();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            }
            super.onBackPressed();
        }

    }

    // TODO: 23/01/2017  se guarda el mensaje en la base de datos
    private void sendMessage() {
        String msj = null; //edMessage.getText().toString().trim();
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(msj)) {
            //edMessage.setError(getString(R.string.error_field_required));
            focusView = null;//edMessage;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            animateButton();
            /*dbManager.insertMessageChat(
                    id_chat,
                    assembleMessage(edMessage.getText().toString()),
                    Long.valueOf(String.valueOf(Calendar.getInstance().getTime().getTime()))
                    , DatabaseChatHelper.ChatSending);
            edMessage.setText(null);
            dbManager.updateStatusChatList(id_chat, DatabaseChatHelper.chatRead, Calendar.getInstance().getTime().getTime());*/
            // TODO: 31/03/2017 se busca el mensaje que se inserto
            try {
                ModelChat mc = dbManager.getChatsDetails(id_chat).get(0);
                chatAdapter.modelChats.add(0, mc);
                chatAdapter.notifyItemInserted(0);
                chatAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("error al insertar el mensaje", String.valueOf(e));
            }
        }
    }

    // TODO: 01/02/2017  se crea el json con la informacion del menasje
    private JSONObject assembleMessage(String msj) {
        PrefrerenceChat prefrerenceChat = new PrefrerenceChat(this);
        JSONObject jsonMessage = new JSONObject();
        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser.put(DatabaseChatHelper.user, "Tu");
            jsonUser.put(DatabaseChatHelper.id_emisor, prefrerenceChat.getTokenChat());
            jsonUser.put(DatabaseChatHelper.id_resceptor, id_chat);
            jsonMessage.put(DatabaseChatHelper.message, msj);
            jsonMessage.put(DatabaseChatHelper.type, ChatAdapter.RIGHT_MSG);
            jsonMessage.put(DatabaseChatHelper.userModel, jsonUser);
            jsonMessage.put(DatabaseChatHelper.typeChat, typeChat);
            jsonMessage.put(DatabaseChatHelper.timeStamp, Calendar.getInstance().getTime().getTime() + "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("json message", jsonMessage.toString());
        return jsonMessage;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    ModelChat mc = dbManager.getChatsDetails(id_chat).get(0);
                    chatAdapter.modelChats.add(0, mc);
                    chatAdapter.notifyItemInserted(0);
                    chatAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error al recibir el mensaje recibido", String.valueOf(e));
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


    @Override
    public void ClickListenerChat(String url, int position) {
        Intent intentImage = new Intent(ChatActivity.this, ImageFullActivity.class);
        intentImage.putExtra("url", url);
        startActivity(intentImage);
    }

    @Override
    public void setCommunication(Activity activity) {
        if (activity instanceof ChatActivity)
            updateChat();
    }

    protected void clearChat() {
        new MaterialDialog.Builder(ChatActivity.this)
                .iconRes(R.drawable.ic_alert_material)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title("Limpiar chat")
                .cancelable(true)
                .content("Se eliminaran todos los mensajes ")
                .positiveText(R.string.aceptar).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dbManager.deleteChatDetails(id_chat);
                updateChat();
            }
        })
                .show();
    }


    // TODO: 02/02/2017 se vuelve a cargar la visat a la hora de entrar un nuevo mensaje
    protected void updateChat() {
        setUp();
    }

    public void menuClick(View view) {
        hideMenu();
        switch (view.getId()) {
            case R.id.mc_ib_imagen:
                Intent imageIntent = new Intent(ChatActivity.this, ImageSelectActivity.class);
                imageIntent.putExtra("type", 1);
                imageIntent.putExtra("id", id_chat);
                imageIntent.putExtra("typeChat", typeChat);
                startActivityForResult(imageIntent, 1);
                break;

            case R.id.mc_ib_take_photo:
                // tomar foto
                Intent cameraIntent = new Intent(ChatActivity.this, ImageSelectActivity.class);
                cameraIntent.putExtra("type", 2);
                cameraIntent.putExtra("id", id_chat);
                cameraIntent.putExtra("typeChat", typeChat);
                startActivityForResult(cameraIntent, 1);
                break;
            case R.id.mc_ib_clear_chat:
                clearChat();
                break;
            case R.id.mc_ib_galery:

                Intent galleryIntent = new Intent(ChatActivity.this, GalleryChatActivity.class);
                galleryIntent.putExtra("id", id_chat);
                startActivity(galleryIntent);
                break;


        }

    }


}
