package com.example.devolucionmaterial.chat.abstrac;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.chat.activitys.UpdateGroupActivity;
import com.example.devolucionmaterial.chat.api.ServiceApi;
import com.example.devolucionmaterial.chat.api.model.ResponseCall;
import com.example.devolucionmaterial.chat.db.DBChatManager;

import com.example.devolucionmaterial.chat.model.ContactList;
import com.example.menuswipe.SwipeHorizontalMenuLayout;
import com.example.menuswipe.SwipeMenuRecyclerView;


import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * clase padre para de el rv de administrador de grupos
 */
public abstract class AbstractRvActivity extends AppCompatActivity implements View.OnClickListener {

    protected Context mContext;
    protected List<ContactList> users;
    protected AbstractRvAdapter mAdapter;
    protected SwipeMenuRecyclerView mRecyclerView;
    protected DBChatManager dbManager;
    protected LinearLayout ll_add_ccontact;
    protected Button btnFinish;
    protected int chat_id;
    protected CardView cvTitle, cvFinish;
    protected boolean manager = false;
    private MaterialDialog materialDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        mContext = this;
        dbManager = new DBChatManager(this);
        dbManager.open();

        chat_id = getIntent().getExtras().getInt("id");
        manager = getIntent().getExtras().getBoolean("magaer");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Administrar Grupo");
        }
        mRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.acd_rv_contacts);
        ll_add_ccontact = (LinearLayout) findViewById(R.id.acd_ll_add_contact);
        btnFinish = (Button) findViewById(R.id.acd_btn_finish_group);
        cvTitle = (CardView) findViewById(R.id.acd_cv_title);
        cvFinish = (CardView) findViewById(R.id.acd_cv_finish);
        ll_add_ccontact.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

        if (!manager) {
            cvTitle.setVisibility(View.GONE);
            cvFinish.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Participantes del Grupo");
        }

        updateRv();
    }

     protected void updateRv() {
        users = dbManager.getMembersChat(chat_id);
        mAdapter = createAppAdapter(this, users);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.acd_ll_add_contact:
                Intent i = new Intent(this, UpdateGroupActivity.class);
                i.putExtra("id", chat_id);
                startActivity(i);
                break;
            case R.id.acd_btn_finish_group:
                new MaterialDialog.Builder(this)
                        .iconRes(android.R.drawable.ic_dialog_alert)
                        .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                        .title(R.string.finish_group)
                        .content(R.string.seguro_determinar_grupo)
                        .positiveText(R.string.aceptar).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finishGroup();
                    }
                })
                        .negativeText(R.string.cancelar)
                        .show();
                break;
        }
    }

    void finishGroup() {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        materialDialog= new MaterialDialog.Builder(AbstractRvActivity.this)
                .title("Sincronizando...")
                .content("Cargando...")
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(false)
                .show();

        RequestBody idChat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(chat_id));

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<ResponseCall> call= serviceApi.finishChatGroup(idChat);
        call.enqueue(new Callback<ResponseCall>() {
            @Override
            public void onResponse(Call<ResponseCall> call, Response<ResponseCall> response) {
                materialDialog.dismiss();
                try {
                    if (response.body().getValue().equals("1")) {
                        dbManager.deleteChatComplete(chat_id);
                        Toast.makeText(getApplicationContext(), "Grupo terminado", Toast.LENGTH_LONG).show();
                        finish();

                    } else {
                        new MaterialDialog.Builder(AbstractRvActivity.this)
                                .content(R.string.ocurrio_un_error)
                                .positiveText(R.string.aceptar)
                                .show();
                    }
                } catch (Exception e) {

                    new MaterialDialog.Builder(AbstractRvActivity.this)
                            .content(R.string.ocurrio_un_error)
                            .positiveText(R.string.aceptar)
                            //.negativeText(R.string.disagree)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResponseCall> call, Throwable t) {
                materialDialog.dismiss();
                new MaterialDialog.Builder(AbstractRvActivity.this)
                        .content(R.string.error_conect)
                        .positiveText(R.string.aceptar)
                        .show();
            }
        });
    }

    protected abstract AbstractRvAdapter createAppAdapter(AbstractRvActivity baseRvActivity, List<ContactList> users);


    protected abstract class AbstractRvAdapter extends RecyclerView.Adapter {

        protected static final int VIEW_TYPE_ENABLE = 0;
        protected static final int VIEW_TYPE_DISABLE = 1;

        List<ContactList> users;

        public AbstractRvAdapter(Context context, List<ContactList> users) {
            this.users = users;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_contact_chat_details, parent, false);
            return createViewHolder(itemView);
        }

        protected boolean swipeEnableByViewType(int viewType) {
            if (viewType == VIEW_TYPE_ENABLE)
                return true;
            else
                return viewType != VIEW_TYPE_DISABLE;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
            final AbstractViewHolder myViewHolder = (AbstractViewHolder) vh;
            final ContactList user = users.get(position);
            final SwipeHorizontalMenuLayout itemView = (SwipeHorizontalMenuLayout) myViewHolder.itemView;

            myViewHolder.getBtOpen().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            // TODO: 05/04/2017 si es el adm entonces se puede ver el boton
            if (manager) myViewHolder.getBtDelete().setVisibility(View.VISIBLE);
            else myViewHolder.getBtDelete().setVisibility(View.GONE);

            myViewHolder.getBtDelete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // must close normal
                    itemView.smoothCloseMenu();
                    users.remove(vh.getAdapterPosition());
                    mAdapter.notifyItemRemoved(vh.getAdapterPosition());
                }
            });
            myViewHolder.getTvName().setText(user.getName());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        protected abstract RecyclerView.ViewHolder createViewHolder(View itemView);
    }

    protected static abstract class AbstractViewHolder extends RecyclerView.ViewHolder {
        private View btOpen;
        private View btDelete;
        private TextView tvName;

        public AbstractViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            btOpen = itemView.findViewById(R.id.btOpen);
            btDelete = itemView.findViewById(R.id.btDelete);
        }

        public View getBtOpen() {
            return btOpen;
        }

        public View getBtDelete() {
            return btDelete;
        }

        public TextView getTvName() {
            return tvName;
        }
    }


}

