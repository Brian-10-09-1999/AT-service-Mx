package com.example.devolucionmaterial.chat.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.beans.BeanResponse;
import com.example.devolucionmaterial.chat.activitys.ChatActivity;
import com.example.devolucionmaterial.chat.activitys.NewChatActivity;
import com.example.devolucionmaterial.chat.api.ServiceApi;
import com.example.devolucionmaterial.chat.model.Contact;
import com.example.devolucionmaterial.chat.utils.NumRandom;
import com.example.devolucionmaterial.chat.adapter.ListContactAdpter;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.db.DatabaseChatHelper;
import com.example.devolucionmaterial.chat.model.ChatList;
import com.example.devolucionmaterial.chat.model.ContactList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactChatFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener, ListContactAdpter.ClickListener {

    private DBChatManager dbManager;
    private RecyclerView rvContact;
    private List<ContactList> mContacModel = new ArrayList<>();
    static ListContactAdpter adapter;
    public LinearLayout llError;
    private Button btnReintentar;
   private MaterialDialog materialDialog;
    private Context context;

    private View v;

    public ContactChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_contact_chat, container, false);
        setHasOptionsMenu(true);
        dbManager = new DBChatManager(getContext());
        dbManager.open();
        context = getContext();
        rvContact = (RecyclerView) v.findViewById(R.id.recyclerview);
        llError = (LinearLayout) v.findViewById(R.id.ec_ll_root);
        btnReintentar = (Button) v.findViewById(R.id.ec_btn_reintentar);
        btnReintentar.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvContact.getContext(), llm.getOrientation());
        rvContact.addItemDecoration(dividerItemDecoration);
        rvContact.setLayoutManager(layoutManager);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContacModel=dbManager.getContactList();
        adapter = new ListContactAdpter(getContext(), mContacModel);
        adapter.setClickListener(this);
        rvContact.setAdapter(adapter);
        if (dbManager.getContactList().isEmpty()) {
            getContact();
        } else {
            adapter = new ListContactAdpter(getContext(), mContacModel);
            adapter.setClickListener(this);
            rvContact.setAdapter(adapter);
        }
    }


    private void setUpContact() {
        mContacModel.clear();
        mContacModel=dbManager.getContactList();
        adapter = new ListContactAdpter(getContext(), mContacModel);
        adapter.setClickListener(this);
        rvContact.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private void getContact() {
          materialDialog= new MaterialDialog.Builder(context)
                .title(getString(R.string.Conectando_con_servidor_remoto))
                .content("Cargando...")
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(false)
                .show();



        dbManager = new DBChatManager(context);
        dbManager.open();

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<List<Contact>> call = serviceApi.OBTENER_CONTACTOS_CHAT();
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                materialDialog.dismiss();
                Log.e("url", String.valueOf(call.request().url()));
                dbManager.deleteAllContacts();
                try {
                    for (int i = 0; i < response.body().size(); i++) {
                        Contact ct = response.body().get(i);
                        dbManager.insert(
                                ct.getAlias(),
                                ct.getNombre(),
                                ct.getId(),
                                ct.getEmail(),
                                ct.getToken());
                    }
                    setUpContact();
                    Toast.makeText(context, "Contactos sincronizados", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("execpytion", String.valueOf(e));
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                materialDialog.dismiss();
                Log.e("onFailure", String.valueOf(t));
                Toast.makeText(context, "Algo fallo intentalo de nuevo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact_chat, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        adapter.setFilter(mContacModel);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.sync_contact) {
            getContact();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        final List<ContactList> filteredModelList = filter(mContacModel, newText);
        adapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private List<ContactList> filter(List<ContactList> models, String query) {
        query = query.toLowerCase();

        final List<ContactList> filteredModelList = new ArrayList<>();
        for (ContactList model : models) {
            final String name = model.getName().toLowerCase();
            final String alias = model.getAlias().toLowerCase();
            if (name.contains(query) || alias.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ec_btn_reintentar:
                llError.setVisibility(View.GONE);
                getContact();
                break;
        }
    }

    public void erroConexion() {
        llError.setVisibility(View.VISIBLE);
    }

    @Override
    public void itemClicked(int item) {
        Log.e("position", String.valueOf(item));
// TODO: 01/02/2017 se busca el id_Alias del contacto para ver si ya tiene un chat o si no crear uno nuevo
        ContactList singleContact = adapter.mContact.get(item);
        Log.e("alias", String.valueOf(singleContact.getId_alias()));
        if (dbManager.getIdContact(singleContact.getId_alias()).isEmpty()) {

            //se agrega a la tabla miebro para ver si ya existe un chat en la lista
            dbManager.insertMembersGroup(singleContact.getId_alias(),singleContact.getId_alias(), singleContact.getName(), singleContact.getAlias());
            dbManager.insertChat(
                    singleContact.getId_alias(),
                    singleContact.getName(),
                    singleContact.getAlias(),
                    R.drawable.ic_chat_icon_list,
                    String.valueOf(singleContact.getId_alias()),
                    DatabaseChatHelper.chatRead,
                    Long.valueOf(String.valueOf(Calendar.getInstance().getTime().getTime())),
                    DatabaseChatHelper.TYPE_CHAT_INDIVIDUAL);
            Intent iChat = new Intent(getContext(), ChatActivity.class);
            iChat.putExtra("id", singleContact.getId_alias());
            startActivity(iChat);
            if (getActivity() instanceof NewChatActivity) {
                getActivity().finish();
            }
        } else {
            ChatList cl = dbManager.getIdContact(singleContact.getId_alias()).get(0);
            Log.e("alias", String.valueOf(singleContact.getId_alias()));
            Intent iChat = new Intent(getContext(), ChatActivity.class);
            iChat.putExtra("id", cl.getId_chat());
            startActivity(iChat);
            if (getActivity() instanceof NewChatActivity) {
                getActivity().finish();
            }
        }

    }


}
