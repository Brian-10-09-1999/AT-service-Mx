package com.example.devolucionmaterial.chat.activitys;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.adapter.MyPagerAdapter;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.fragments.ChatListFragment;
import com.example.devolucionmaterial.chat.fragments.ContactChatFragment;
import com.example.devolucionmaterial.chat.model.ModelChat;
import com.example.devolucionmaterial.chat.task.BrodcastBean;
import com.example.devolucionmaterial.chat.task.CatchMessageChat;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;
import com.example.devolucionmaterial.data_base.BDVarGlo;

import org.json.JSONException;

public class MenuChatActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    private AlertDialog.Builder builder;
    private MyPagerAdapter myPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         
        PrefrerenceChat prefrerenceChat= new PrefrerenceChat(this);
        prefrerenceChat.setTokenchat(Integer.parseInt(BDVarGlo.getVarGlo(getApplicationContext(), "INFO_USUARIO_ID")));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Menu chat");
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BrodcastBean.INTENT_UPDATE_LIST_CHAT);
        registerReceiver(receiverListChat, filter);

        setUp();

    }

    private BroadcastReceiver receiverListChat = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String json = intent.getExtras().getString("json");
            myPagerAdapter.notifyDataSetChanged();
        }
    };


    private void setUp() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {

        ChatListFragment chatListFragment = new ChatListFragment();

        ContactChatFragment contactChatFragment = new ContactChatFragment();

        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.addFragment(chatListFragment, "Chats");
        myPagerAdapter.addFragment(contactChatFragment, "Contactos");
        //adapter.addFragment(upComingFragment, getString(R.string.upcoming));
        viewPager.setAdapter(myPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_main, menu);
        //textNum = (TextView) findViewById(R.id.textoNum);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.new_group:
                Intent i = new Intent(MenuChatActivity.this, NewGroupActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // setUp();
        // TODO: 09/03/2017 al entra en notifyDataSetChanged se manda a llmar getItemPosition el cual llama los metodos correspondientes para actualizar el  fragmento
        myPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiverListChat);
    }
}
