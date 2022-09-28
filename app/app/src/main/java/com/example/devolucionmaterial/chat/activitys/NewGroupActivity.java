package com.example.devolucionmaterial.chat.activitys;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.adapter.SelectContactAdapter;
import com.example.devolucionmaterial.chat.api.ServiceApi;
import com.example.devolucionmaterial.chat.db.DBChatManager;
import com.example.devolucionmaterial.chat.db.DatabaseChatHelper;
import com.example.devolucionmaterial.chat.model.ContactList;
import com.example.devolucionmaterial.chat.model.TokenGroup;
import com.example.devolucionmaterial.chat.utils.NumRandom;
import com.example.devolucionmaterial.chat.utils.PrefrerenceChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGroupActivity extends AppCompatActivity implements SelectContactAdapter.ClickListener, View.OnClickListener, SearchView.OnQueryTextListener {
    private DBChatManager dbManager;
    private RecyclerView rvContact;
    private List<ContactList> mContacModel = new ArrayList<>();
    private SelectContactAdapter adapter;
    private FloatingActionButton btnGetSelected;
    private List<ContactList> contactListAdd;
    private AlertDialog.Builder builder;
    String nombre, desc;
    private NewGroupFragment newGroupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Agregar participantes");
        }
        setUpNewGroup();
        setUpContact();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    void setUpNewGroup() {
        dbManager = new DBChatManager(this);
        dbManager.open();
        this.mContacModel = dbManager.getContactList();
        rvContact = (RecyclerView) findViewById(R.id.ang_rv_contact_select);
        btnGetSelected = (FloatingActionButton) findViewById(R.id.btnGetSelected);
        btnGetSelected.setOnClickListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvContact.getContext(),
                llm.getOrientation());
        rvContact.addItemDecoration(dividerItemDecoration);
        rvContact.setLayoutManager(llm);



        newGroupFragment = new NewGroupFragment();
        newGroupFragment.setCancelable(false);
        newGroupFragment.show(getFragmentManager(), "newGroupFragment");

    }

    @Override
    public void itemClicked(int item) {

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
                        adapter.setFilter(mContacModel);
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

        int id = item.getItemId();
        if (id == R.id.sync_contact) {

        }
        return super.onOptionsItemSelected(item);
    }


    void setUpContact() {
        if (mContacModel.isEmpty()) {
            setUpContactUpdate();
        } else {
            adapter = new SelectContactAdapter(this.mContacModel);
            adapter.setClickListener(this);
            rvContact.setAdapter(adapter);
        }
    }


    private void setUpContactUpdate() {
        adapter = new SelectContactAdapter(this.mContacModel);
        adapter.setClickListener(this);
        rvContact.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        insertGroupNew();
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


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("ValidFragment")
    public class NewGroupFragment extends DialogFragment implements View.OnClickListener {

        public NewGroupFragment() {

        }

        FloatingActionButton btn_respodent;
        EditText et_nombre, et_desc;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_new_group_chat, container, false);
            et_nombre = (EditText) view.findViewById(R.id.anq_et_name);
            et_desc = (EditText) view.findViewById(R.id.anq_et_desc);
            btn_respodent = (FloatingActionButton) view.findViewById(R.id.fng_btn_next);
            btn_respodent.setOnClickListener(this);

            Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    // Handle the menu item
                    return true;
                }
            });
            //toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setTitle("Datos del grupo");
            toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                    finish();
                }
            });
            return view;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            // request a window without the title
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            return dialog;
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fng_btn_next:
                    if (!et_nombre.getText().toString().isEmpty() && !et_desc.getText().toString().isEmpty()) {
                        nombre = et_nombre.getText().toString();
                        desc = et_desc.getText().toString();
                        getDialog().dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Faltan campos por llenar", Toast.LENGTH_SHORT).show();
                    }
                    break;


            }
        }
    }

    void insertGroupNew() {
        final PrefrerenceChat prefrerenceChat = new PrefrerenceChat(this);
        final MaterialDialog materialDialog;
        final int idchat = NumRandom.getRadom();
        boolean addValidation = false;

        JSONObject jsonChatGroup = new JSONObject();
        JSONArray jsonArrayMembers = new JSONArray();

        // TODO: 27/01/2017 se recorrer el array buscando miembro seleccionados
        adapter.setFilter(mContacModel);
        final List<ContactList> stList = adapter.getContacttist();
        for (int i = 0; i < stList.size(); i++) {
            ContactList singleContact = stList.get(i);
            if (singleContact.isSelected()) {
                addValidation = true;
                jsonArrayMembers.put(singleContact.getId_alias());
            }
        }
        // TODO: 03/04/2017 se arma el json con los datos del grupo y miembros
        /**
         * el emisor  en el json idetifica el creador del chat
         * */
        try {
            jsonChatGroup.put("name", nombre);
            jsonChatGroup.put("descripcion", desc);
            jsonChatGroup.put("idchat", idchat);
            jsonChatGroup.put("members", jsonArrayMembers);
            jsonChatGroup.put("emisor", prefrerenceChat.getTokenChat());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("json grupo ", String.valueOf(jsonChatGroup));

        if (addValidation) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            materialDialog = new MaterialDialog.Builder(NewGroupActivity.this)
                    .title(getString(R.string.Conectando_con_servidor_remoto))
                    .content("Cargando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();

            RequestBody jsonGroup = RequestBody.create(MediaType.parse("text/plain"), jsonChatGroup + "");

            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<TokenGroup> call = serviceApi.createGroup(jsonGroup);
            call.enqueue(new Callback<TokenGroup>() {
                @Override
                public void onResponse(Call<TokenGroup> call, Response<TokenGroup> response) {
                    materialDialog.dismiss();
                    if (response.body().getToken() != 0) {
                        try {

                            for (int i = 0; i < stList.size(); i++) {
                                ContactList singleContact = stList.get(i);
                                if (singleContact.isSelected()) {
                                    dbManager.insertMembersGroup(response.body().getToken(),
                                            singleContact.getId_alias(), singleContact.getName(), singleContact.getAlias());
                                }
                            }
// TODO: 05/04/2017  en los chat de grupo se agrega en el campo user el id de manager para poder identificarlo
                            dbManager.insertChat(
                                    response.body().getToken(),
                                    nombre
                                    , desc,
                                    R.drawable.ic_chat_group,
                                    String.valueOf(prefrerenceChat.getTokenChat()),
                                    DatabaseChatHelper.chatRead,
                                    Long.valueOf(String.valueOf(Calendar.getInstance().getTime().getTime())),
                                    DatabaseChatHelper.TYPE_CHAT_GROUP);

                            Intent chatAtivity = new Intent(NewGroupActivity.this, ChatActivity.class);
                            chatAtivity.putExtra("id", response.body().getToken());
                            startActivity(chatAtivity);
                            finish();
                        } catch (Exception e) {
                            Log.e("error try", String.valueOf(e));
                        }
                    } else {
                        AlertDialog.Builder alertaSimple = new AlertDialog.Builder(NewGroupActivity.this);
                        alertaSimple.setTitle("Error al crear grupo");
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

                }

                @Override
                public void onFailure(Call<TokenGroup> call, Throwable t) {
                    materialDialog.dismiss();
                    Log.e("error ", String.valueOf(t));

                    new MaterialDialog.Builder(NewGroupActivity.this)
                            .content(R.string.error_conect)
                            .iconRes(R.drawable.warning)
                            .positiveText(R.string.aceptar)
                            //.negativeText(R.string.disagree)
                            .show();

                }
            });


        } else {
            Toast.makeText(getApplicationContext(), "Debes seleccionar al menos un contacto", Toast.LENGTH_SHORT).show();
        }

    }


}
