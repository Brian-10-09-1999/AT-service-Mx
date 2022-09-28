package com.example.devolucionmaterial.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import com.example.devolucionmaterial.FoliosPendientes;
import com.example.devolucionmaterial.ListaDeSalasPedidos;
import com.example.devolucionmaterial.ListaOSAsignadas;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.Menu_Devolucion;
import com.example.devolucionmaterial.PedidosPorIncidencia;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.Registro;
import com.example.devolucionmaterial.RegistroDeMemorias;
import com.example.devolucionmaterial.ReporteDeDevoluciones1;
import com.example.devolucionmaterial.activitys.CrearFolioActivity;
import com.example.devolucionmaterial.chat.activitys.MenuChatActivity;
import com.example.devolucionmaterial.activitys.SolicitudMaterialActivity;
import com.example.devolucionmaterial.chat.activitys.LoginChatActity;
import com.example.devolucionmaterial.data_base.ActualizaBDestatusDevMaterial;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.sharedpreferences.PreferencesVar;
import com.example.devolucionmaterial.utils.ValidationRegion;

import static android.content.ContentValues.TAG;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragmentBody extends Fragment {
    String metodo;
    Context context;
    View view;

    BDmanager manager;
    private Bundle bndMyBundle;
    private static final int CALLING_ID_FOR_ACTIV2 = 123;
    public static String ACTIVIDAD_ORIGEN_MEMORIAS = "actividadOrigenMemorias";

    ScrollView scrollBtns;
    Button btnFoliosPendientes, btnMateriales, btnPedidos, btnOSAsignadas, btnDevolucionMemorias, btnChat, btnCreateFolio;

    public FragmentBody() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_body, container, false);
        context = view.getContext();
        manager = new BDmanager(context);
        FragmentHead.CambiarTitulo("Menu Inicial");
        setupFragmentBody();

        return view;
    }

    void setupFragmentBody() {
        metodo = "FragmentBody.setupFragmentBody()";
        scrollBtns = (ScrollView) view.findViewById(R.id.frag_body_id_sv_btns);
        btnFoliosPendientes = (Button) view.findViewById(R.id.frag_body_id_btn_folios_pendientes);
        btnMateriales = (Button) view.findViewById(R.id.frag_body_id_btn_materiales);
        btnPedidos = (Button) view.findViewById(R.id.frag_body_id_btn_pedidos);
        btnOSAsignadas = (Button) view.findViewById(R.id.frag_body_id_btn_os_asignadas);
        btnDevolucionMemorias = (Button) view.findViewById(R.id.frag_body_id_btn_devolver_memorias);
        btnCreateFolio = (Button) view.findViewById(R.id.frag_body_create_folio);
        btnChat = (Button) view.findViewById(R.id.frag_body_id_btn_chat);




        btnChat.setVisibility(View.VISIBLE);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 30/03/2017  hay que ver si es necesario el login
                Intent chat = new Intent(getActivity(), MenuChatActivity.class);
                startActivity(chat);

            }
        });

        if ("tecnico".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO"))) {
            scrollBtns.setVisibility(View.VISIBLE);
        } else {
            scrollBtns.setVisibility(View.GONE);
        }

        btnFoliosPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFoliosPendientes();
            }
        });
        btnMateriales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMateriales();
            }
        });
        btnPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPedidos();
            }
        });
        btnOSAsignadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOSAsignadas();
            }
        });
        btnDevolucionMemorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDevolucionMemorias();
            }
        });
        // TODO: 14/03/2017 se consulta el id y se valida que se login de españa para mostar el boton
        PreferencesVar preferencesVar = new PreferencesVar(getActivity());
        if (ValidationRegion.validationRegionEspana(String.valueOf(preferencesVar.getIdRegion()))) {
            btnCreateFolio.setVisibility(View.GONE);
        }

        btnCreateFolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickCrearFolio();
            }
        });

    }

    void clickFoliosPendientes() {
        startActivity(new Intent(context, FoliosPendientes.class));
    }

    void clickMateriales() {
        metodo = "crearDialogoListaAlmacen()";
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final CharSequence[] items = new CharSequence[2];

        items[0] = getString(R.string.devMat);
        items[1] = getString(R.string.repdev);

	    /*final CharSequence[] items = new CharSequence[3];

	    items[0] = getString(R.string.devMat);
	    items[1] = getString(R.string.adeudoMaterial);
	    items[2] = getString(R.string.repdev);*/

        builder.setTitle(getString(R.string.seleccionaOpcion))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intentMenuDevolucion = new Intent(context, Menu_Devolucion.class);

                                bndMyBundle = new Bundle();
                                try {
                                    bndMyBundle.putInt("usuarioidx", Integer.parseInt((BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"))));
                                    bndMyBundle.putString("usuariox", (BDVarGlo.getVarGlo(context, "INFO_USUARIO_NOMBRE_COMPLETO")));
                                    bndMyBundle.putString("urlx", (MainActivity.url));
                                    bndMyBundle.putInt("regionidx", Integer.parseInt((BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION"))));
                                    bndMyBundle.putInt("tipo", Integer.parseInt((BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO"))));
                                    bndMyBundle.putString("nombreusuariox", (BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE")));
                                } catch (Throwable ex) {
                                    bndMyBundle.putInt("usuario", 0);
                                }
                                intentMenuDevolucion.putExtras(bndMyBundle);
                                startActivityForResult(intentMenuDevolucion, CALLING_ID_FOR_ACTIV2);
                                break;
                        /*case 1:
                            Intent intentPedidosPorIncidencia = new Intent(context, AdeudoDeMaterial.class);
							bndMyBundle = new Bundle();
							bndMyBundle.putString("usuarioidx", (""+usuarioID));
							bndMyBundle.putString("nombreusuariox", (nombreUsuario));
							intentPedidosPorIncidencia.putExtras(bndMyBundle);
							startActivity(intentPedidosPorIncidencia);
							break;*/
                            case 1:
                                //para actualizar los estatus de reportes de dovulucion de material
                                context.startService(new Intent(context, ActualizaBDestatusDevMaterial.class));
                                Intent intentReporteDev = new Intent(context, ReporteDeDevoluciones1.class);
                                bndMyBundle = new Bundle();
                                try {
                                    bndMyBundle.putString("urlx", (MainActivity.url));
                                } catch (Throwable ex) {
                                    bndMyBundle.putInt("usuario", 0);
                                }
                                intentReporteDev.putExtras(bndMyBundle);
                                startActivity(intentReporteDev);

                                break;
                        }
                    }
                });

        builder.create().show();
    }

    void clickPedidos() {
        metodo = "MenuInicialTecnico." + "crearListaDeDialogoPedidos()";
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final CharSequence[] items = new CharSequence[2];

        items[0] = getString(R.string.pedidosPorSala);
        items[1] = getString(R.string.pedidosPorIncidencia);
        //items[2] = getString(R.string.solicitud_de_material);

        builder.setTitle(getString(R.string.seleccionaOpcion))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                try {
                                    Intent intentListaDeSalasPedidos = new
                                            Intent(context, ListaDeSalasPedidos.class);
                                    bndMyBundle = new Bundle();
                                    bndMyBundle.putInt("regionidx", Integer.parseInt((BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION"))));
                                    bndMyBundle.putString("nombrex", BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
                                    intentListaDeSalasPedidos.putExtras(bndMyBundle);
                                    startActivity(intentListaDeSalasPedidos);
                                } catch (Exception e) {
                                    Log.d(TAG, "" + e.toString());
                                }
                                break;
                            case 1:
                                Intent intentPedidosPorIncidencia = new Intent(context, PedidosPorIncidencia.class);
                                bndMyBundle = new Bundle();
                                try {
                                    bndMyBundle.putString("aliasx", (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ALIAS")));
                                    bndMyBundle.putString("passwordx", (BDVarGlo.getVarGlo(context, "INFO_USUARIO_PASSWORD")));
                                    bndMyBundle.putString("usuarioidx", (BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID")));
                                    bndMyBundle.putString("nombrex", BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
                                } catch (Throwable ex) {
                                    bndMyBundle.putInt("usuario", 0);
                                }
                                intentPedidosPorIncidencia.putExtras(bndMyBundle);
                                startActivity(intentPedidosPorIncidencia);
                                break;
                            case 2:
                                Intent intentSolicitudMaterial = new Intent(context, SolicitudMaterialActivity.class);
                                startActivity(intentSolicitudMaterial);
                                break;
                        }
                    }
                });

        builder.create().show();
    }

    void clickOSAsignadas() {
        startActivity(new Intent(context, ListaOSAsignadas.class));
    }

    void clickDevolucionMemorias() {
        manager.insertarSalidaMemoria(Integer.parseInt(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID")), Registro.obtenerHora(), 0);
        Intent intentMenuDevolucion = new Intent(context, RegistroDeMemorias.class);
        bndMyBundle = new Bundle();
        try {
            bndMyBundle.putInt("usuarioidx", Integer.parseInt((BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"))));
            bndMyBundle.putString("urlx", (MainActivity.url));
            bndMyBundle.putInt(ACTIVIDAD_ORIGEN_MEMORIAS, 0);
        } catch (Throwable ex) {
            bndMyBundle.putInt("usuario", 0);
        }
        intentMenuDevolucion.putExtras(bndMyBundle);
        startActivity(intentMenuDevolucion);
    }

    void clickCrearFolio() {
        Intent intentCrearFolio = new Intent(getActivity(), CrearFolioActivity.class);
        startActivity(intentCrearFolio);
    }
}