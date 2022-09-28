package com.example.devolucionmaterial.chat.abstrac;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.activitys.ChatActivity;
import com.example.devolucionmaterial.chat.activitys.GalleryChatActivity;
import com.example.devolucionmaterial.chat.activitys.ImageSelectActivity;
import com.example.devolucionmaterial.chat.adapter.ChatAdapter;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.model.ModelChat;
import com.example.devolucionmaterial.chat.task.BrodcastBean;
import com.example.devolucionmaterial.chat.utils.MyBounceInterpolator;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;

import org.json.JSONException;

import java.util.ArrayList;

//import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
//import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by EDGAR ARANA on 09/10/2017.
 */

public abstract class AbstracChatActivity extends AppCompatActivity {
    public final static int acountItemMax = 20;//si se cambia este valor tambien se debe cambiar la consulta en l abse de datos "getChatsDetails", "getChatsDetailsPage"
    protected Context context;
    protected RecyclerView rvListMessage;
    protected ChatAdapter chatAdapter;
    protected LinearLayoutManager mLinearLayoutManager;
    protected ArrayList<ModelChat> modelChats;
    protected ImageView btSendMessage;
    protected ImageView btEmoji;
    //protected EmojiconEditText edMessage;
    protected View contentRoot;
    //protected EmojIconActions emojIcon;
    protected int id_chat; // el id_chat es el id_alias receptor
    protected DBChatManager dbManager;
    protected TextView tvTitle, tvDesc;
    protected int typeChat;
    protected LinearLayout ll_toolbar_titles;
    protected LinearLayoutManager llm;
    protected FragmentManager fragmentManager;
    protected PrefrerenceChat prefrerenceChat;
    protected static final int online = 1;
    protected static final int offline = 0;
    protected boolean manager = false;
    /**
     * variables de menu
     */
    protected View theMenu;
    protected View menu1;
    protected View menu2;
    protected View menu3;
    protected View menu4;
    protected View overlay;

    protected boolean menuOpen = false;

    protected void animateButton() {
        // Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        double animationDuration = 2.0 * 1000;
        myAnim.setDuration((long) animationDuration);

        // Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.20, 20.0);

        myAnim.setInterpolator(interpolator);

        // Animate the button
        btSendMessage.startAnimation(myAnim);
        //playSound();

        // Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //animateButton();
            }
        });
    }


    protected void initBrodcast() {
        // TODO: 29/03/2017  IntentFilter que actulilza la lista de chat cuando llega un chat
        IntentFilter filter = new IntentFilter();
        filter.addAction(BrodcastBean.INTENT_FILTER);
        registerReceiver(myReceiver, filter);

        // TODO: 10/04/2017 solo se registra si es chat de grupo
        if (typeChatValidation()) {
            // TODO: 29/03/2017  finalizar el grupo si es que mo existe el chat
            IntentFilter filterFinishGROUP = new IntentFilter();
            filterFinishGROUP.addAction(BrodcastBean.INTENT_FINISH_GROUP_CHAT);
            registerReceiver(receiverFinishChatGroup, filterFinishGROUP);

            // TODO: 29/03/2017  por si eliminanal miembro y sacar una alerta
            IntentFilter filterDeleteMmember = new IntentFilter();
            filterDeleteMmember.addAction(BrodcastBean.INTENT_DELETE_MEMBER_GROUP_CHAT);
            registerReceiver(receiverDeleteChatGroup, filterDeleteMmember);
        }
    }

    // TODO: 01/02/2017 valida si es un chat individual o grupal
    protected Boolean typeChatValidation() {
        if (typeChat == 2) {
            return true;
        }
        return false;
    }


    // TODO: 27/03/2017 se declara un BroadcastReceiver interno para actulizar el rv cuando se reciba una nuevo mensaje
    protected BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getExtras().getString("json");
            try {
                ModelChat mc = dbManager.getChatsDetails(id_chat).get(0);
                chatAdapter.modelChats.add(0, mc);
                chatAdapter.notifyItemInserted(0);
                chatAdapter.notifyDataSetChanged();
                Log.e("mensaje", mc.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("error al recibir el mensaje recibido", String.valueOf(e));
            }
        }
    };

    protected BroadcastReceiver receiverFinishChatGroup = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long chat_id = intent.getExtras().getLong("idchat");
            if (chat_id == (long) id_chat) {
                new MaterialDialog.Builder(AbstracChatActivity.this)
                        .iconRes(R.drawable.ic_alert_material)
                        .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                        .title("Grupo terminado")
                        .cancelable(false)
                        .content("Ya no perteneces a este grupo ó el grupo a terminado ")
                        .positiveText(R.string.aceptar).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                        .show();
            }
        }

    };
    protected BroadcastReceiver receiverDeleteChatGroup = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PrefrerenceChat prefrerenceChat = new PrefrerenceChat(AbstracChatActivity.this);
            long chat_id = intent.getExtras().getLong("id_alias");
            Log.e("chat_id receiverDeleteChatGroup ", String.valueOf(chat_id));
            if (chat_id == (long) prefrerenceChat.getTokenChat()) {
                new MaterialDialog.Builder(AbstracChatActivity.this)
                        .iconRes(R.drawable.ic_alert_material)
                        .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                        .title("Grupo terminado")
                        .cancelable(false)
                        .content("Ya no perteneces a este grupo ó el grupo a terminado ")
                        .positiveText(R.string.aceptar).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                        .show();
            }
        }

    };

    protected void initMenu() {
        theMenu = findViewById(R.id.the_menu);
        menu1 = findViewById(R.id.menu1);
        menu2 = findViewById(R.id.menu2);
        menu3 = findViewById(R.id.menu3);
        menu4 = findViewById(R.id.menu4);
        overlay = findViewById(R.id.overlay);
    }

    // TODO: 19/04/2017 muetsra le menu
    public void revealMenu() {
        menuOpen = true;

        theMenu.setVisibility(View.INVISIBLE);
        int cx = theMenu.getRight() - 200;
        int cy = theMenu.getTop();
        int finalRadius = Math.max(theMenu.getWidth(), theMenu.getHeight());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(theMenu, cx, cy, 0, finalRadius);
            anim.setDuration(600);
            theMenu.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);
            anim.start();
        }

        theMenu.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);

        Animation popeup1 = AnimationUtils.loadAnimation(this, R.anim.popeup);
        Animation popeup2 = AnimationUtils.loadAnimation(this, R.anim.popeup);
        Animation popeup3 = AnimationUtils.loadAnimation(this, R.anim.popeup);
        Animation popeup4 = AnimationUtils.loadAnimation(this, R.anim.popeup);
        Animation popeup5 = AnimationUtils.loadAnimation(this, R.anim.popeup);
        Animation popeup6 = AnimationUtils.loadAnimation(this, R.anim.popeup);
        popeup1.setStartOffset(50);
        popeup2.setStartOffset(100);
        popeup3.setStartOffset(150);
        popeup4.setStartOffset(200);
        popeup5.setStartOffset(250);
        popeup6.setStartOffset(300);
        menu1.startAnimation(popeup1);
        menu2.startAnimation(popeup2);
        menu3.startAnimation(popeup3);
        menu4.startAnimation(popeup4);

    }


    // TODO: 19/04/2017 oculta el meu
    public void hideMenu() {
        menuOpen = false;
        int cx = theMenu.getRight() - 200;
        int cy = theMenu.getTop();
        int initialRadius = theMenu.getWidth();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator anim = null;
            anim = ViewAnimationUtils.createCircularReveal(theMenu, cx, cy, initialRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    theMenu.setVisibility(View.INVISIBLE);
                    theMenu.setVisibility(View.GONE);
                    overlay.setVisibility(View.INVISIBLE);
                    overlay.setVisibility(View.GONE);
                }
            });
            anim.start();
        } else {
            theMenu.setVisibility(View.INVISIBLE);
            theMenu.setVisibility(View.GONE);
            overlay.setVisibility(View.INVISIBLE);
            overlay.setVisibility(View.GONE);
        }
    }



    // TODO: 19/04/2017  overlayClick es un view con el que en cual quier parte de la patalla que se toque se oculta el menu
    public void overlayClick(View v) {
        hideMenu();
    }



}
