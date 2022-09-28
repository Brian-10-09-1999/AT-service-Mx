package com.example.devolucionmaterial.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.dialogs.DialogoBusquedaTecnicoBaja;
import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.materialesSeccion.Registro;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.lists.ListViewAdapterSpinerRegiones;
import com.example.devolucionmaterial.lists.ListViewItem;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragMenuDevolucion extends Fragment{
    String metodo;
    Context context;
    View view;

    ArrayList<ListViewItem> data = new ArrayList<>();
    private ArrayList<String> arrSalas = new ArrayList<String>();
    protected ArrayAdapter<CharSequence> adapter, adapterOpciones;
    private Spinner sala;
    private Spinner spnOpciones;
    private String tecnicoidBaja="0";
    private BDmanager manager;
    public static final String NOMBRE_SALA="NOMBRE_SALA";
    private static final int CALLING_ID_FOR_ACTIV2 = 111,
            BUSQUEDA_CODE=1;
    public static String ACTIVIDAD_ORIGEN = "actividadOrigen",
            VERIFICAR_PEDIDO = "verificarPedidox",
            EXTRA_REGION="region";
    public FragMenuDevolucion(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu_devolucion, container, false);
        context = view.getContext();
        setupFragMenuDevolucion();
        return view;
    }

    void setupFragMenuDevolucion() {
        manager = new BDmanager(context);
        sala=(Spinner) view.findViewById(R.id.frag_menu_devoluc_id_spn_sala);
        sala.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                int seleccion = position+1;
                llenaComboSala(seleccion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        Spinner spnRegion = (Spinner) view.findViewById(R.id.frag_menu_devoluc_id_spn_region);
        spnOpciones = (Spinner) view.findViewById(R.id.frag_menu_devoluc_id_spn_opciones);
        TextView tvRegion = (TextView) view.findViewById(R.id.frag_menu_devoluc_id_tv_region);

        if("1".equals(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO"))){
            tvRegion.setVisibility(View.VISIBLE);
            spnRegion.setVisibility(View.VISIBLE);
            data.clear();
            Cursor fila = manager.consulta("select id, nombre from cregion ORDER BY nombre");
            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                data.add(new ListViewItem(fila.getInt(0),fila.getString(1)));
            }
            fila.close();
            ListViewAdapterSpinerRegiones adapterRegiones = new ListViewAdapterSpinerRegiones(context, data);
            spnRegion.setAdapter(adapterRegiones);

            //Asignas el origen de datos desde los recursos
            //adapter = ArrayAdapter.createFromResource(this, R.array.Regiones, android.R.layout.simple_spinner_item);
            //Asignas el layout a inflar para cada elemento al momento de desplegar la lista
            //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Seteas el adaptador
            //spnRegion.setAdapter(adapter);
            spnRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView idR = (TextView) view.findViewById(R.id.id_spiner_id);
                    llenaComboSala(Integer.valueOf(idR.getText().toString()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }else{

            llenaComboSala(Integer.parseInt(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION")));

        }

        //Asignas el origen de datos desde los recursos
        adapterOpciones = ArrayAdapter.createFromResource(context, R.array.Opciones,android.R.layout.simple_spinner_item);
        //Asignas el layout a inflar para cada elemento al momento de desplegar la lista
        adapterOpciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Seteas el adaptador
        spnOpciones.setAdapter(adapterOpciones);
        spnOpciones.setSelection(0);
        spnOpciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spnOpciones.getSelectedItemPosition()==1){
                    Intent intent = new Intent(context, DialogoBusquedaTecnicoBaja.class);
                    intent.putExtra(EXTRA_REGION, BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION"));
                    startActivityForResult(intent, BUSQUEDA_CODE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btncrear = (Button) view.findViewById(R.id.frag_menu_devoluc_id_btn_crear);

        btncrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregaSalida();
            }
        });
    }







    public void llenaComboSala(int regionID) {
        metodo = "llenaComboSala()";
        arrSalas.clear();
        Cursor fila = manager.consulta("SELECT nombre FROM csala WHERE regionidfk= '"+regionID+"' ORDER BY nombre");
        for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
            arrSalas.add(fila.getString(0));
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,  arrSalas);
        sala.setAdapter(adapter2);
    }







    public void agregaSalida() {
        String valSala = sala.getSelectedItem().toString();
        manager.actualiza("insert into csalida(usuarioidfk, officeID, fecha, estatus)" +
                "values("+BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID")+",(select officeID from csala where nombre='"+ valSala +"'),date('now'),'1')");
        Cursor fila = manager.consulta("SELECT salidaid,officeID FROM csalida ORDER BY salidaid desc LIMIT 1");

        fila.moveToFirst();
        int salidaid = fila.getInt(0);
        int officeID = fila.getInt(1);
        fila.close();
        manager.insertarSala(valSala);
        Cursor cursorSala = manager.cargarCursorSala();
        cursorSala.moveToLast();
        int idSala = cursorSala.getInt(0);
        cursorSala.close();

        String tipo="m";
        if(spnOpciones.getSelectedItemPosition()==0)
            tipo="ORDINARIA";
        else if (spnOpciones.getSelectedItemPosition()==1)
            tipo="BAJA";

        manager.insertarSalida(idSala, salidaid, Integer.parseInt(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID")), Registro.obtenerHora(), "1",
                "Pendiente", officeID, "pendiente", "pendiente", tipo, tecnicoidBaja);

        String pedido="0";
        Intent intent;
        intent = new Intent(context, Registro.class);
        Bundle bndMyBundle = new Bundle();
        bndMyBundle.putInt("usuarioidx", Integer.parseInt((BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"))));
        bndMyBundle.putString("usuariox", (BDVarGlo.getVarGlo(context, "INFO_USUARIO_NOMBRE_COMPLETO")));
        bndMyBundle.putString("urlx", (MainActivity.url));
        bndMyBundle.putInt("salidaidx", (salidaid));
        bndMyBundle.putInt("officeIDx", (officeID));
        bndMyBundle.putString(NOMBRE_SALA, valSala);
        bndMyBundle.putString("pedidox", pedido);
        bndMyBundle.putString("nombrex", BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
        bndMyBundle.putInt(ACTIVIDAD_ORIGEN, 0);
        if(tipo.equals("ORDINARIA"))
            bndMyBundle.putString(VERIFICAR_PEDIDO, "si");
        else
            bndMyBundle.putString(VERIFICAR_PEDIDO, "no");

        intent.putExtras(bndMyBundle);

        startActivityForResult(intent, CALLING_ID_FOR_ACTIV2);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == BUSQUEDA_CODE){
            if(resultCode == Activity.RESULT_OK){
                tecnicoidBaja = intent.getStringExtra("tecnicoidBajax");
                Alert.Alerta(context, "Menu_Devolucion", 0, getString(R.string.regBaja));
            }else if(resultCode == Activity.RESULT_CANCELED){
                spnOpciones.setSelection(0);
                tecnicoidBaja = "0";
                Alert.Alerta(context, "Menu_Devolucion", 2, getString(R.string.notRegBaja));
            }
        }
    }
}
//358