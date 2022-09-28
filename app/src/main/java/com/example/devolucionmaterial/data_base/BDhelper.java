package com.example.devolucionmaterial.data_base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.devolucionmaterial.static_class.MensajeEnConsola;

public class BDhelper extends SQLiteOpenHelper {
    String metodo;
    Context context;

    public static final String DATABASE_NAME = "administracion.db";
    public static final int DATABASE_VERSION = 9;//2.1.5 => 8
    private static final String DATABASE__ALTER_SALIDAS = "ALTER TABLE " + BDmanager.TABLE_SALIDAS +
            " ADD COLUMN " + BDmanager.CN_TECNICO_ID_BAJA + " string";

    private static final String DATABASE__ALTER_TIPO_SALIDAS = "ALTER TABLE " + BDmanager.TABLE_SALIDAS +
            " ADD COLUMN " + BDmanager.CN_TIPO + " string";


    public BDhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnte, int versionNue) {
        metodo = "onUpgrade()";
        MensajeEnConsola.log(context, metodo, "versionAnte = " + versionAnte + " versionNue = " + versionNue);


        // TODO: 09/06/2017 se agrega este ajuste para alterar la tabla y agregar la maquina a la tabla descripcionDeSalida
        if (9 >versionAnte ) {
            try {
                db.execSQL("ALTER TABLE descripcionDeSalida ADD COLUMN " + BDmanager.CN_MAQUINA + " TEXT DEFAULT ''");
            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
            }

        }
        if (versionAnte < 8) {
            try {
                db.execSQL("ALTER TABLE descripcionDeSalida ADD COLUMN estatus TEXT DEFAULT ''");

            } catch (Exception e) {
                MensajeEnConsola.log(context, metodo, "Exception e = " + e.getMessage());
            }
            db.execSQL(BDmanager.CREATE_TABLE_ESTATUS);
            db.execSQL(BDmanager.CREATE_TABLE_REGION);
            db.execSQL(BDmanager.CREATE_TABLE_VARIABLES_GLOBALES);

            db.execSQL(BDmanager.CREATE_TABLE_CONTROL_TOTAL);
            db.execSQL(BDmanager.CREATE_TABLE_HORAS_SERVICIO);
        }//if(versionAnte<6){//version 2.1.5.APK
//
        //	db.execSQL(BDmanager.CREATE_TABLE_CONTROL_TOTAL);
        //	db.execSQL("insert into control_total (nombre, control) values ('crefacciones',0), ('csala',0)");
        //}
        if (versionAnte < 5) {//registro de 18 de julio de 2016 en version 2.1.2.APK

            db.execSQL(BDmanager.CREATE_TABLE_CONTROL_TABLAS);
            db.execSQL("insert into ccontrol_tablas (nombre_tabla, num_registros) values ('crefacciones',0), ('csala',0)");
        }
        //Registros anteriores al 18 de junio de 2016
        if (versionAnte < 2) {
            //Aqui se agrego la tabla csala (officeID integer, nombre text, cliente text, region text, localizacion text, regionidfk integer, salaID integer)
        }

        if (versionAnte < 3) {
            //Aqui se agrego la tabla csala (officeID integer, nombre text, cliente text, region text, localizacion text, regionidfk integer, salaID integer)
            db.execSQL(BDmanager.CREATE_TABLE_ESTATUS_TECNICO);
            db.execSQL("insert into " + BDmanager.TABLE_ESTATUS_TEC + " values" + "(null,'0','0', '0', '0', '1', '0', '0', 'n', 0)");
            db.execSQL(DATABASE__ALTER_SALIDAS);
            db.execSQL(DATABASE__ALTER_TIPO_SALIDAS);
            db.execSQL(BDmanager.CREATE_TABLE_COMENTARIOS_OS);
        }

        if (versionAnte < 4) {
            //Aqui se agrego la tabla csala (officeID integer, nombre text, cliente text, region text, localizacion text, regionidfk integer, salaID integer)
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //tablas anteriores en version 2.1.1
        db.execSQL(BDmanager.CREATE_TABLE_ESTATUS_TECNICO);
        db.execSQL("insert into " + BDmanager.TABLE_ESTATUS_TEC + " values" + "(null,'0','0', '0', '0', '1', '0', '0', 'n', 0)");
        db.execSQL("create table if not exists csala(officeID integer, nombre text, cliente text, region text, localizacion text, regionidfk integer, salaID integer)");
        db.execSQL("create table if not exists csalida(salidaid integer primary key autoincrement, usuarioidfk integer, officeID integer, fecha integer, estatus text)");
        db.execSQL("create table if not exists rpiezas(salidaidfk integer, codigo text, cantidad integer, serie text, fecha integer,estatus text)");
        /*SERVIDOR*/
        db.execSQL(BDmanager.CREATE_TABLE_REFACCIONES);
        db.execSQL(BDmanager.CREATE_TABLE_SALA);
        db.execSQL(BDmanager.CREATE_TABLE_FALLAS);
        db.execSQL(BDmanager.CREATE_TABLE_SUBFALLAS);
        db.execSQL(BDmanager.CREATE_TABLE_SALA_JUEGO_LIC);
        db.execSQL(BDmanager.CREATE_TABLE_SALIDAS);
        db.execSQL(BDmanager.CREATE_TABLE_DESCRIPCION_SALIDAS);
        db.execSQL(BDmanager.CREATE_TABLE_SALIDAS_MEMORIA);
        db.execSQL(BDmanager.CREATE_TABLE_PIEZAS_MEMORIAS);
        /*AQUI y SERVIDOR*/
        db.execSQL(BDmanager.CREATE_TABLE_COMENTARIOS_OS);
        //despues de la version 2.1.2 se crea
        db.execSQL(BDmanager.CREATE_TABLE_CONTROL_TABLAS);
        //para la version 2.1.5 se crea
        db.execSQL(BDmanager.CREATE_TABLE_CONTROL_TOTAL);
        db.execSQL(BDmanager.CREATE_TABLE_HORAS_SERVICIO);
        /*SERVIDOR*/
        db.execSQL(BDmanager.CREATE_TABLE_ESTATUS);
        db.execSQL(BDmanager.CREATE_TABLE_REGION);
        db.execSQL(BDmanager.CREATE_TABLE_VARIABLES_GLOBALES);
    }
}