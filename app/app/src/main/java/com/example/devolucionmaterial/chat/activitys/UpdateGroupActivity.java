package com.example.devolucionmaterial.chat.activitys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.adapter.SelectContactAdapter;
import com.example.devolucionmaterial.chat.api.ServiceApi;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.db.DatabaseChatHelper;
import com.example.devolucionmaterial.chat.model.ChatList;
import com.example.devolucionmaterial.chat.model.ContactList;
import com.example.devolucionmaterial.chat.utils.NumRandom;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateGroupActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {
    private int chat_id;
    protected DBChatManager dbManager;
    private SelectContactAdapter adapter;
    private RecyclerView rvContact;
    private FloatingActionButton btnGetSelected;
    private List<ContactList> contactLists;
    private MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_group);
        dbManager = new DBChatManager(this);
        dbManager.open();
        setUp();
    }

    void setUp() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Agregar al Grupo");
        }

        chat_id = getIntent().getExtras().getInt("id");
        rvContact = (RecyclerView) findViewById(R.id.aug_rv_contact_select);
        btnGetSelected = (FloatingActionButton) findViewById(R.id.btnGetSelected);
        btnGetSelected.setOnClickListener(this);
        setUpRv();
    }

    void setUpRv() {
        contactLists = new ArrayList<>();
        contactLists = getListMembers();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvContact.getContext(),
                llm.getOrientation());
        rvContact.addItemDecoration(dividerItemDecoration);
        rvContact.setLayoutManager(llm);
        adapter = new SelectContactAdapter(contactLists);
        rvContact.setAdapter(adapter);
    }

    List<ContactList> getListMembers() {
        List<String> parameters = new ArrayList<>();
        for (int i = 0; i < dbManager.getMembersChat(chat_id).size(); i++) {
            ContactList cl = dbManager.getMembersChat(chat_id).get(i);
            parameters.add(String.valueOf(cl.getId_alias()));

        }
        Log.e("ids excliodos", "(" + TextUtils.join(",", parameters) + ")");
        return dbManager.getMemberMissing("(" + TextUtils.join(",", parameters) + ")");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_group, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        adapter.setFilter(contactLists);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });

        return true;
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
    public boolean onQueryTextChange(String newText) {
        final List<ContactList> filteredModelList = filter(contactLists, newText);
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
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onClick(View v) {
        insertNewMember();

    }

    void insertNewMember() {
        final PrefrerenceChat prefrerenceChat = new PrefrerenceChat(this);
        boolean addValidation = false;
        final ProgressDialog pDialog;
        adapter.setFilter(contactLists);
        JSONObject jsonChatGroup = new JSONObject();
        final JSONArray jsonArrayMembers = new JSONArray();
        // TODO: 27/01/2017 se recorrer el array buscando miembro seleccionados
        final List<ContactList> stList = adapter.getContacttist();
        for (int i = 0; i < stList.size(); i++) {
            ContactList singleContact = stList.get(i);
            if (singleContact.isSelected()) {
                addValidation = true;
                jsonArrayMembers.put(singleContact.getId_alias());
            }
        }
        try {
            jsonChatGroup.put("idchat", chat_id);
            jsonChatGroup.put("members", jsonArrayMembers);
            jsonChatGroup.put("emisor", prefrerenceChat.getTokenChat());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("json grupo ", String.valueOf(jsonChatGroup));

        RequestBody jsonGroup = RequestBody.create(MediaType.parse("text/plain"), jsonChatGroup + "");
        if (!addValidation) {
            Toast.makeText(getApplicationContext(), "Debes seleccionar al menos un contacto", Toast.LENGTH_SHORT).show();
        } else {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            materialDialog= new MaterialDialog.Builder(UpdateGroupActivity.this)
                    .title(getString(R.string.Conectando_con_servidor_remoto))
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();


            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<ResponseCall> call = serviceApi.addMember(jsonGroup);
            call.enqueue(new Callback<ResponseCall>() {
                @Override
                public void onResponse(Call<ResponseCall> call, Response<ResponseCall> response) {
                    materialDialog.dismiss();
                    if (response.body().getValue().equals("1")){
                        for (int i = 0; i < stList.size(); i++) {
                            ContactList singleContact = stList.get(i);
                            if (singleContact.isSelected()) {
                                dbManager.insertMembersGroup(chat_id,
                                        singleContact.getId_alias(), singleContact.getName(), singleContact.getAlias());

                            }
                        }
                        Toast.makeText(UpdateGroupActivity.this, "Se agrego con exito ", Toast.LENGTH_LONG);
                        finish();
                    }else {
                        new MaterialDialog.Builder(UpdateGroupActivity.this)
                                .content(R.string.ocurrio_un_error)
                                .positiveText(R.string.aceptar)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseCall> call, Throwable t) {
                    materialDialog.dismiss();
                    AlertDialog.Builder alertaSimple = new AlertDialog.Builder(UpdateGroupActivity.this);
                    alertaSimple.setTitle("Error al agregar al grupo");
                    alertaSimple.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });

                    alertaSimple.setIcon(R.drawable.warning);
                    alertaSimple.create();
                    alertaSimple.show();
                }
            });


        }
    }


}
