package com.example.devolucionmaterial;

import java.util.ArrayList;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.devolucionmaterial.activitys.BaseActivity;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.lists.ListViewAdapterSpinerRegiones;
import com.example.devolucionmaterial.lists.ListViewItem;
import com.thanosfisherman.mayi.PermissionBean;

public class MenuPedidosPorSala extends BaseActivity implements OnItemSelectedListener{
	String metodo;
	ArrayList<ListViewItem> data = new ArrayList<>();
	ArrayList<String> arrSalas=new ArrayList<String>();
	ArrayList<String> arrRegion = new ArrayList<String>();
	protected ArrayAdapter<CharSequence> adapter;
	Spinner sala, spnRegion;
	Bundle bndMyBundle, bndReceptor;
	Intent intReceptor, myIntent;
	private String url, usuario;
	private TextView txtUsuario, tvRegion;
	int usuarioID,regionid,officeID;
	private BDmanager manager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu__devolucion);
		initToolbar("Menú pedidos por sala", true , true);

		coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			permissionMultiple();
		} else {
			initSetUp();
		}

	}


	@Override
	public String[] permissions() {
		return new String[]{
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
				android.Manifest.permission.CAMERA,
				android.Manifest.permission.ACCESS_FINE_LOCATION,
				android.Manifest.permission.RECORD_AUDIO,
				android.Manifest.permission.CALL_PHONE,
				android.Manifest.permission.GET_ACCOUNTS
		};
	}

	@Override
	protected void permissionResultMulti(PermissionBean[] permissions) {
		Boolean val = true;
		//Toast.makeText(PermissionActivity.this, "MULTI PERMISSION RESULT " + Arrays.deepToString(permissions), Toast.LENGTH_LONG).show();
		for (int i = 0; i < permissions.length; i++) {
			if (!permissions[i].isGranted()) {
				val = false;
			}
		}
		if (val) {
			initSetUp();
		} else {
			showSnackBarPermission();
		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		/*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			permissionMultiple();
		}*/

	}
	
	private void initSetUp(){
		 manager = new BDmanager(this);
		sala=(Spinner) findViewById(R.id.sala);
		spnRegion = (Spinner)findViewById(R.id.spnRegion);
		tvRegion  = (TextView)findViewById(R.id.tvRegion);
		
		intReceptor = this.getIntent();
		bndReceptor = intReceptor.getExtras();
		myIntent = this.getIntent();
		usuarioID=bndReceptor.getInt("usuarioidx");
		url=bndReceptor.getString("urlx");
		usuario=bndReceptor.getString("usuariox");
		regionid=bndReceptor.getInt("regionidx");
		
		tvRegion.setVisibility(View.VISIBLE);
		spnRegion.setVisibility(View.VISIBLE);

		data.clear();
		Cursor fila = manager.consulta("select id, nombre from cregion ORDER BY nombre");
		for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
			data.add(new ListViewItem(fila.getInt(0),fila.getString(1)));
		}
		fila.close();
		ListViewAdapterSpinerRegiones adapterRegiones = new ListViewAdapterSpinerRegiones(this, data);
		spnRegion.setAdapter(adapterRegiones);

		//Asignas el origen de datos desde los recursos
		//adapter = ArrayAdapter.createFromResource(this, R.array.Regiones, android.R.layout.simple_spinner_item);
		//Asignas el layout a inflar para cada elemento al momento de desplegar la lista
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Seteas el adaptador
		//spnRegion.setAdapter(adapter);
		spnRegion.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
		TextView idR = (TextView) view.findViewById(R.id.id_spiner_id);
		llenaComboSala(Integer.valueOf(idR.getText().toString()));
			/*int seleccion;
			if(position>6){
				seleccion = 52+position;
			}else{
				seleccion = position+1;
			}
			*/
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}
	
	public void llenaComboSala(int regionID) {
		metodo = "llenaComboSala()";
		//MensajeEnConsola.log(context, metodo, "regionidfk = "+regionID);
		arrSalas.clear();
		Cursor fila = new BDmanager(this).consulta("SELECT nombre FROM csala WHERE regionidfk= '"+regionID+"' ORDER BY nombre");
		for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
			arrSalas.add(fila.getString(0));
	        /*if (fila.getLong(fila.getColumnIndex(SituacionDbAdapter.C_COLUMNA_ID)) == id)
	        {
	            return fila.getPosition() ;
	        }*/
	    }
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,  arrSalas);
				sala.setAdapter(adapter2);
		fila.close();
	}
}
