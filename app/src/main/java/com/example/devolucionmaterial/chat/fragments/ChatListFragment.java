package com.example.devolucionmaterial.chat.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.activitys.ChatActivity;

import com.example.devolucionmaterial.chat.activitys.NewChatActivity;
import com.example.devolucionmaterial.chat.activitys.NewGroupActivity;
import com.example.devolucionmaterial.chat.adapter.ListChatAdapter;
import com.example.devolucionmaterial.chat.api.ServiceApi;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.model.ChatList;


import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrador on 20/12/2016.
 */

public class ChatListFragment extends Fragment implements ListChatAdapter.ClickListener, ListChatAdapter.OnUpdate {
    Context context;
    View v;

    List<ChatList> chatLists = new ArrayList<ChatList>();
    private DBChatManager dbManager;
    private TextView txt_empty;

    private  RecyclerView rvListChat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        setHasOptionsMenu(true);
        context = getActivity();
        dbManager = new DBChatManager(getContext());
        dbManager.open();
        txt_empty = (TextView) v.findViewById(R.id.fcl_txt_empty);


        rvListChat = (RecyclerView) v.findViewById(R.id.fcl_rv_chat);
        //rvListChat.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvListChat.getContext(),
                llm.getOrientation());
        rvListChat.addItemDecoration(dividerItemDecoration);
        rvListChat.setLayoutManager(llm);
        update();


        return v;
    }


    @Override
    public void itemClicked(int item) {
        ChatList chatList = chatLists.get(item);
        Intent iChat = new Intent(getContext(), ChatActivity.class);
        iChat.putExtra("id", chatList.getId_chat());
        startActivity(iChat);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.new_chat) {
            Intent i = new Intent(getActivity(), NewChatActivity.class);
            startActivity(i);
        }
        if (id == R.id.new_group) {
            Intent i = new Intent(getActivity(), NewGroupActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }


    public void update() {
        chatLists.clear();
        chatLists = dbManager.getListChat();
        ListChatAdapter adapter = new ListChatAdapter(chatLists, context);
        adapter.setClickListener(this);
        adapter.setOnUpdate(this);
        rvListChat.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        if (dbManager.getListChat().isEmpty()) {
            rvListChat.setVisibility(View.GONE);
            txt_empty.setVisibility(View.VISIBLE);

        } else {
            rvListChat.setVisibility(View.VISIBLE);
            txt_empty.setVisibility(View.GONE);
        }


    }

    @Override
    public void updateInterface() {

        if (dbManager.getListChat().isEmpty()) {
            rvListChat.setVisibility(View.GONE);
            txt_empty.setVisibility(View.VISIBLE);

        } else {
            rvListChat.setVisibility(View.VISIBLE);
            txt_empty.setVisibility(View.GONE);
        }
        chatLists.clear();
        chatLists = dbManager.getListChat();
        ListChatAdapter adapter = new ListChatAdapter(chatLists, context);
        adapter.setClickListener(this);
        adapter.setOnUpdate(this);
        rvListChat.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }




}
