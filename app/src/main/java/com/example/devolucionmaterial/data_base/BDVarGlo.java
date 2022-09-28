package com.example.devolucionmaterial.data_base;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.devolucionmaterial.checks.Device;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

/*Aqui ponemos todas las variables globales guardadas en disco*/
public class BDVarGlo {
    static BDmanager manager = null;
    static Context context;
    static String metodo;

    public static void INICIALIZA(Context context1) {
        context = context1;
        manager = new BDmanager(context);

        //Varriable para controlar si se tiene en pruebas o productivo
        //setVarGlo(context, "APP_PRUEBAS_o_PRODUCCION","PRUEBAS");
        //VARIABLES GLOBALES
        setVarGlo(context, "MUESTRA_DIALOGO_LISTA_REGIONES", "SI");
        setVarGlo(context, "PRUEBA_ESTRESS_TIEMPO_SINCRONIZA", "0");
        setVarGlo(context, "PRUEBA_ESTRESS_TIEMPO_ACTUAL", "0");
        setVarGlo(context, "PRUEBA_ESTRESS_URL_CONNECT", "http://pruebasisco.ddns.net:8082/Android/PruebaEstresServidor/estres.php");
        setVarGlo(context, "PRUEBA_DATO_TECPRO_ID", "0");
        //DATOS PERSONALES DE LOS USUARIOS
        //setVarGlo(context, "INFO_USUARIO_ID","0");
        //setVarGlo(context, "INFO_USUARIO_ID_SUPERVISOR","0");
        //setVarGlo(context, "INFO_USUARIO_ID_REGION","0");
        //setVarGlo(context, "INFO_USUARIO_ID_ESTADO","0");
        //setVarGlo(context, "INFO_USUARIO_NOMBRE_COMPLETO","0");
        //setVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE","0");
        //setVarGlo(context, "INFO_USUARIO_SEGUNDO_NOMBRE","0");
        //setVarGlo(context, "INFO_USUARIO_PRIMER_APELLIDO","0");
        //setVarGlo(context, "INFO_USUARIO_SEGUNDO_APELLIDO","0");
        //setVarGlo(context, "INFO_USUARIO_EMAIL","0");
        //setVarGlo(context, "INFO_USUARIO_TELEFONO","0");
        //setVarGlo(context, "INFO_USUARIO_ALIAS","0");
        //setVarGlo(context, "INFO_USUARIO_PASSWORD","0");
        //setVarGlo(context, "INFO_USUARIO_TIPO","0");
        //setVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO","0");
        //setVarGlo(context, "INFO_USUARIO_ID_ESTATUS","0");
        //setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO","0");
        //setVarGlo(context, "INFO_USUARIO_HORAS_ACTIVIDAD","0");
        //setVarGlo(context, "INFO_USUARIO_NOTIFICACION_NUMERO_TOTAL","0");
        //UBICACION DE SERVICIO
        setVarGlo(context, "INFO_USUARIO_OFFICE_ID", "0");
        setVarGlo(context, "INFO_USUARIO_FOLIO_ID", "0");
        //PARA MOSTRAR SALA Y TIEMPO DE INICIO DE ACTIVIDAD
        //setVarGlo(context, "INFO_USUARIO_SALA_INICIA","0");
        //setVarGlo(context, "INFO_USUARIO_HORA_INICIA","0");
        //VALORES DE LA APP


        setVarGlo(context, "INFO_APP_SERVIDOR_VERSION", Device.versionName(context));


        //-------------------- FUNCIONES y URLS DE CONEXION CON android.php-----------------------------------------------------

        //setVarGlo(context, "URL_DOMINIO_PRUEBAS","http://10.10.0.6:8082/");
        //setVarGlo(context, "URL_DOMINIO_PRUEBAS","http://10.10.0.244:8082/");

        // TODO: 05/09/2017 esta url es para hacer pruebas con otro servidor
        //setVarGlo(context, "URL_DOMINIO_PRUEBAS", "http://213.170.37.111/");
        //setVarGlo(context, "URL_DOMINIO_PRODUCCION", "http://213.170.37.111/");
        //setVarGlo(context, "URL_DOMINIO_PRODUCCION", "http://213.170.37.111/"); //URL ESPECIAL PARA PRUEBAS EN ESPAÑA

        //setVarGlo(context, "URL_DOMINIO_PRUEBAS", "http://sisco2.globalzitro.com/");
        setVarGlo(context, "URL_DOMINIO_PRUEBAS", "http://pruebasisco.ddns.net:8082/");
        //setVarGlo(context, "URL_DOMINIO_PRUEBAS", "http://pruebasisco.ddns.net:8082/AndroidEspana/");
        setVarGlo(context, "URL_DOMINIO_PRODUCCION", "http://10.10.0.252:8082/"); // POR DEFAULT
        setVarGlo(context, "URL_DOMINIO_PRODUCCION_LOCAL", "http://10.10.0.252:8082/");


        setVarGlo(context, "FUNCTION_CARGAR_INFO_USUARIO", "funcion=Carga_Info_Usuario");
        setVarGlo(context, "FUNCTION_VALIDAR_USUARIO", "funcion=Validar_Usuario");
        setVarGlo(context, "FUNCTION_VALIDAR_DATO", "funcion=Validar_Dato");
        setVarGlo(context, "FUNCTION_INSERTA_DESCARGA", "funcion=Inserta_Descarga");
        setVarGlo(context, "FUNCTION_NUEVOS_FOLIOS_ASIGNADOS", "funcion=Nuevos_folios_Asignados");




        //FRAGMENTOS Y SUS VARIABLES -------------------------------------------------------------------------------------------
        setVarGlo(context, "FRAGMENTO_FOOT_IMG_OPEN_CLOSE", "OPEN");
        //VARIABLES
        setVarGlo(context, "VAR_ESTATUS_ESTATUS_ID_ANTERIOR", "0");
        setVarGlo(context, "VAR_ESTATUS_ESTATUS_EN_SERVICIO_ANTERIOR", "0");
        //setVarGlo(context, "VAR_VISTA_M_LEME_INVITADO","0");
        //setVarGlo(context, "VAR_BD_CONTROL_CREFACCION","0");
        //setVarGlo(context, "VAR_BD_CONTROL_CSALA","0");
        //setVarGlo(context, "VAR_BD_CONTROL_CESTATUS","0");
        setVarGlo(context, "TIEMPO_SINCRONIZACIONES", "5");
        setVarGlo(context, "TIEMPO_ACTUAL_FOLIOS_ASIGNADOS", "0");
        //PRUEBAS DE BORRADO en version 2.1.7 -> en versiones superiores borrarRegion este registro
        if ("".equals(getVarGlo(context, "PRUEBA_BORRADO_CONTROL_TOTAL_30092016"))) {
            context.startService(new Intent(context, ActualizaBDcrefacciones.class));
            context.startService(new Intent(context, ActualizaBDcsala.class));
            context.startService(new Intent(context, ActualizaBDcestatus.class));
            setVarGlo(context, "PRUEBA_BORRADO_CONTROL_TOTAL_30092016", "OK");
        }



        if ("SSS".equals(manager.consulta("SELECT nombre FROM control_total WHERE nombre = 'cestatus'", "SSS"))) {
            manager.actualiza("INSERT INTO control_total (nombre, control) VALUES ('cestatus',0)");
        }
        if ("SSS".equals(manager.consulta("SELECT nombre FROM control_total WHERE nombre = 'csala'", "SSS"))) {
            manager.actualiza("INSERT INTO control_total (nombre, control) VALUES ('control',0), ('crefacciones',0), ('csala',0)");
        }
        if ("SSS".equals(manager.consulta("SELECT salaID FROM csala WHERE salaID = 1", "SSS"))) {
            //Aqui es que no existe
            manager.actualiza("INSERT INTO csala VALUES ('1', '1', '1', '1', '1', '1', '1')");
        }
        if ("SSS".equals(manager.consulta("SELECT _idRef FROM crefacciones WHERE _idRef = 1", "SSS"))) {
            //Aqui es que no existe
            manager.actualiza("INSERT INTO crefacciones VALUES (1, 1, 1, '1', 1, '1', '1')");
        }
    }

    public static void setVarGlo(Context context, String nombre, String valor) {
        metodo = "setVarGlo()";
        //MensajeEnConsola.log(context, metodo, "nombre -> "+nombre+"\nvalor -> "+valor);
        try {
            if (manager == null) {
                manager = new BDmanager(context);
            }
            if ("SSS".equals(manager.consulta("SELECT valor FROM variable_global WHERE nombre = '" + nombre + "'", "SSS"))) {
                manager.actualiza("INSERT INTO variable_global (nombre, valor) VALUES ('" + nombre + "','" + valor + "')");
            } else {
                manager.actualiza("UPDATE variable_global SET valor = '" + valor + "' WHERE nombre = '" + nombre + "'");
            }
        } catch (Exception e) {
            MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
        }
    }



    public static String getVarGlo(Context context, String nombre) {
        try {
            if (manager == null) {
                manager = new BDmanager(context);
            }
            return manager.consulta("SELECT valor FROM variable_global WHERE nombre = '" + nombre + "'", "");
        } catch (Exception e) {
            MensajeEnConsola.log(context, "getVarGlo()", "Exception e = " + e.getMessage());
        }
        return "";
    }





    public static void setDatosUsuario(Context context) {
        manager.actualizarEstatusTec(BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_ESTATUS"),
                BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"),
                BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID_REGION"),
                BDVarGlo.getVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO"),
                BDVarGlo.getVarGlo(context, "INFO_USUARIO_PASSWORD"),
                BDVarGlo.getVarGlo(context, "INFO_USUARIO_NOMBRE_COMPLETO"),
                BDVarGlo.getVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE"));
    }

    public static void setSeteaVarGloUsuario() {
        setVarGlo(context, "INFO_USUARIO_ID", "0");
        setVarGlo(context, "INFO_USUARIO_ID_SUPERVISOR", "0");
        setVarGlo(context, "INFO_USUARIO_ID_REGION", "0");
        setVarGlo(context, "INFO_USUARIO_ID_ESTADO", "0");
        setVarGlo(context, "INFO_USUARIO_NOMBRE_COMPLETO", "0");
        setVarGlo(context, "INFO_USUARIO_PRIMER_NOMBRE", "0");
        setVarGlo(context, "INFO_USUARIO_SEGUNDO_NOMBRE", "0");
        setVarGlo(context, "INFO_USUARIO_PRIMER_APELLIDO", "0");
        setVarGlo(context, "INFO_USUARIO_SEGUNDO_APELLIDO", "0");
        setVarGlo(context, "INFO_USUARIO_EMAIL", "0");
        setVarGlo(context, "INFO_USUARIO_TELEFONO", "0");
        setVarGlo(context, "INFO_USUARIO_ALIAS", "0");
        setVarGlo(context, "INFO_USUARIO_PASSWORD", "0");
        setVarGlo(context, "INFO_USUARIO_TIPO", "0");
        setVarGlo(context, "INFO_USUARIO_TIPO_DE_USUARIO", "0");
        setVarGlo(context, "INFO_USUARIO_ID_ESTATUS", "0");
        setVarGlo(context, "INFO_USUARIO_ESTATUS_EN_SERVICIO", "0");
        setVarGlo(context, "INFO_USUARIO_HORAS_ACTIVIDAD", "0");
        setVarGlo(context, "INFO_USUARIO_NOTIFICACION_NUMERO_TOTAL", "0");
    }
}
