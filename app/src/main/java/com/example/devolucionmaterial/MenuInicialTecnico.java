package com.example.devolucionmaterial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.devolucionmaterial.data_base.ActualizaBDestatusDevMaterial;
import com.example.devolucionmaterial.data_base.BDVarGlo;
import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.dialogs.Alert;
import com.example.devolucionmaterial.dialogs.Estatus;
import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.FoliosPendientes;
import com.example.devolucionmaterial.activitys.materialesSeccion.Menu_Devolucion;
import com.example.devolucionmaterial.internet.JSONparse;
import com.example.devolucionmaterial.activitys.materialesSeccion.ReporteDeDevoluciones1;
import com.example.devolucionmaterial.activitys.pedidosSeccion.ListaDeSalasPedidos;
import com.example.devolucionmaterial.activitys.pedidosSeccion.PedidosPorIncidencia;
import com.example.devolucionmaterial.activitys.osSeccion.ListaOSAsignadas;
import com.example.devolucionmaterial.services.CheckService;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;
import com.example.devolucionmaterial.static_class.MenuOpciones;

public class MenuInicialTecnico extends Activity{
	private static String metodo;
	static Context context;
	private MenuOpciones mo;
	//private Button btnDevolverMemorias;
	private static BDmanager manager;
	private Bundle bndMyBundle;
	private int usuarioID,regionid, tipo;
	String url;
	private String usuario, alias, apellido, password;
	private static final int CALLING_ID_FOR_ACTIV2 = 123;
	public static String ACTIVIDAD_ORIGEN_MEMORIAS = "actividadOrigenMemorias";
	private static String TAG = "DebugMenuInicial";
    public static String nombreUsuario;
	private ProgressDialog  pDialog;
	private static Menu mMenu;
	private static String docPHPMenuTecFirma="MenuTecFirma.php?";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenu = menu;
		actualizaEstatusTecnico();
		return true;
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_inicial_tecnico);
		context = this;
		setupMenuInicial();
		AnunciosPrimeroLogin();
	}
	void AnunciosPrimeroLogin(){
		//muestrta un anuncio cada dia al usuario
		Alert.Anuncio(context);
	}
	private void setupMenuInicial(){
		metodo = "setupMenuInicial()";
		//startService(new Intent(context, FolioAsignadoService.class));

		mo = new MenuOpciones();
		TextView tvUsuario = (TextView) findViewById(R.id.tvUsuariomi);
		ImageView imgEmail = (ImageView) findViewById(R.id.imgCorreomi);
		ImageView imgCallCC = (ImageView) findViewById(R.id.imgLlamarmi);
		ImageView imgInfo = (ImageView) findViewById(R.id.imgInfomi);

		manager = new BDmanager(this);
		BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO");
		usuarioID= Integer.valueOf(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));
		url= MainActivity.url;
		usuario= BDVarGlo.getVarGlo(context, "INFO_USUARIO_NOMBRE_COMPLETO");
		regionid= Integer.parseInt(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION"));
		tipo= Integer.parseInt(BDVarGlo.getVarGlo(context, "INFO_USUARIO_TIPO"));
		alias = BDVarGlo.getVarGlo(context, "INFO_USUARIO_ALIAS");
		password = BDVarGlo.getVarGlo(context, "INFO_USUARIO_PASSWORD");
		nombreUsuario = BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE");
		apellido = BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_APELLIDO");

		tvUsuario.setText(getString(R.string.bienvenido) + nombreUsuario + "!");
		if(CheckService.internet(context)) {
			new Progreso().execute();
		}else{
			Alert.ActivaInternet(context);
		}
		Button btnFoliosPendientes = (Button) findViewById(R.id.btnFoliosPendientes);
		Button btnPedidos = (Button) findViewById(R.id.btnPedidos);
		Button btnOSAsignadas = (Button) findViewById(R.id.btnOSAsignadas);
		Button btnServAlm = (Button) findViewById(R.id.btnServAlmacen);
		//btnDevolverMemorias = (Button)findViewById(R.id.btnDevolucionMemorias);
		//btnFoliosPendientes.setVisibility(View.GONE);
		//btnPedidos.setVisibility(View.GONE);

		btnFoliosPendientes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, FoliosPendientes.class));
			}
		});
		
		btnPedidos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				crearListaDeDialogoPedidos().show();
			}
		});

		btnOSAsignadas.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, ListaOSAsignadas.class));
			}
		});
		
		btnServAlm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				crearDialogoListaAlmacen().show();
			}
		});
		
		imgCallCC.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mo.llamarContactCenter(MenuInicialTecnico.this);
			}
		});
		
		imgEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mo.enviarCorreo(MenuInicialTecnico.this);
			}
		});
		
		imgInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mo.mostrarInfoApp(MenuInicialTecnico.this);
			}
		});
		
		/*btnDevolverMemorias.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				manager.insertarSalidaMemoria(usuarioID, Registro.obtenerHora(), 0);
				Intent intentMenuDevolucion = new Intent(MenuInicialTecnico.this, RegistroDeMemorias.class);
				bndMyBundle = new Bundle();
				try{
					bndMyBundle.putInt("usuarioidx", (usuarioID));
					bndMyBundle.putString("urlx", (url));
					bndMyBundle.putInt(ACTIVIDAD_ORIGEN_MEMORIAS, 0);
				}catch (Throwable ex){
					bndMyBundle.putInt("usuario", 0);
				}
				intentMenuDevolucion.putExtras(bndMyBundle);
				startActivity(intentMenuDevolucion);
			}
		});*/
	}
	public void onResume(){
		metodo="onResume()";
		super.onResume();
		actualizaEstatusTecnico();	
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		metodo="onOptionsItemSelected()";
		int id = item.getItemId();
        if (id == R.id.action_est_tec) {
            iniciaMenuEstatusTec(MenuInicialTecnico.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	public AlertDialog crearListaDeDialogoPedidos() {
		metodo ="MenuInicialTecnico."+"crearListaDeDialogoPedidos()";
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

	    final CharSequence[] items = new CharSequence[2];

	    items[0] = getString(R.string.pedidosPorSala);
	    items[1] = getString(R.string.pedidosPorIncidencia);

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
									bndMyBundle.putInt("regionidx", (regionid));
									bndMyBundle.putString("nombrex", nombreUsuario);
									intentListaDeSalasPedidos.putExtras(bndMyBundle);
									startActivity(intentListaDeSalasPedidos);
								}catch (Exception e){
									Log.d(TAG,""+e.toString());
								}
								break;
							case 1:
								Intent intentPedidosPorIncidencia = new Intent(MenuInicialTecnico.this, PedidosPorIncidencia.class);
								bndMyBundle = new Bundle();
								try {
									bndMyBundle.putString("aliasx", (alias));
									bndMyBundle.putString("passwordx", (password));
									bndMyBundle.putString("usuarioidx", ("" + usuarioID));
									bndMyBundle.putString("nombrex", nombreUsuario);
									Log.d(TAG, "alias= " + alias + "contraseña= " + password);
								} catch (Throwable ex) {
									bndMyBundle.putInt("usuario", 0);
								}
								intentPedidosPorIncidencia.putExtras(bndMyBundle);
								startActivity(intentPedidosPorIncidencia);
								break;
						}
					}
				});

	    return builder.create();
	}
	public AlertDialog crearDialogoListaAlmacen() {
		metodo ="crearDialogoListaAlmacen()";
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
							try{
								bndMyBundle.putInt("usuarioidx", (usuarioID));
								bndMyBundle.putString("usuariox", (usuario));
								bndMyBundle.putString("urlx", (url));
								bndMyBundle.putInt("regionidx", (regionid));
								bndMyBundle.putInt("tipo", (tipo));
								bndMyBundle.putString("nombreusuariox", (nombreUsuario));
							}catch (Throwable ex){
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
							startService(new Intent(context, ActualizaBDestatusDevMaterial.class));
							Intent intentReporteDev = new Intent(context, ReporteDeDevoluciones1.class);
							bndMyBundle = new Bundle();
							try{
								bndMyBundle.putString("urlx", (url));
							}catch (Throwable ex){
								bndMyBundle.putInt("usuario", 0);
							}
							intentReporteDev.putExtras(bndMyBundle);
							startActivity(intentReporteDev);
							
							break;
						}
	                }
	            });

	    return builder.create();
	}
	private class Progreso extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(MenuInicialTecnico.this);
			pDialog.setCancelable(false);
			pDialog.show();
			pDialog.setContentView(R.layout.custom_progressdialog);
		}
		@Override
		protected Void doInBackground(Void... params) {
			try{
				JSONObject object = JSONparse.consultaURL(context, metodo, MainActivity.url+docPHPMenuTecFirma+"tecnicoidx="+usuarioID);
				assert object != null;
				JSONArray jsonArray = object.optJSONArray("tipoMenu");
				JSONObject jsonArrayChild1 = jsonArray.getJSONObject(0);
				BDVarGlo.setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", jsonArrayChild1.optString("estatusidx"));
				BDVarGlo.setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", jsonArrayChild1.optString("estEnServx"));
				BDVarGlo.setDatosUsuario(context);
			}catch(JSONException e) {
				MensajeEnConsola.log(context, metodo, "JSONException e = "+e.getMessage());
			}catch (Exception e) {
				MensajeEnConsola.log(context, metodo, "Exception e = "+e.getMessage());
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			actualizaEstatusTecnico();
		}
	}
	public static void iniciaMenuEstatusTec(Context context){
		BDVarGlo.setVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO", "tecnico");
		if(CheckService.internet(context)) {
			//context.startActivity(new Intent(context, EstatusTecnico.class));
			context.startActivity(new Intent(context, Estatus.class));
		}else{
			Alert.ActivaInternet(context);
		}
    }
	public static void actualizaEstatusTecnico(){
		if(mMenu!=null){
			MenuItem item = mMenu.findItem(R.id.action_est_tec);
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("39")){
				if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("1")){
					item.setIcon(R.drawable.est_activo);
				}
				else if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("2")){
					item.setIcon(R.drawable.est_traslado_sala);
				}
				else if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("3")){
					item.setIcon(R.drawable.est_asignado);
				}
				if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO").equals("4")){
					item.setIcon(R.drawable.est_en_comida);
				}
			}
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("40")){
				item.setIcon(R.drawable.est_inactivo);
			}
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("109")){
				item.setIcon(R.drawable.est_incidencia);
			}
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("79")){
				item.setIcon(R.drawable.est_dia_descanso);
			}
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("80")){
				item.setIcon(R.drawable.mix_peq);
			}
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("81")){
				item.setIcon(R.drawable.est_vacaciones);
			}
			if(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS").equals("89")){
				item.setIcon(R.drawable.est_incapacidad);
			}
		}
	}
}
//401