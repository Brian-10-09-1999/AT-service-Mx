package com.example.devolucionmaterial.data_base.dbgraficas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.devolucionmaterial.data_base.BDmanager;
import com.example.devolucionmaterial.static_class.MensajeEnConsola;

/**
 * Created by EDGAR ARANA on 14/06/2017.
 */

public class BDhelperGaficas extends SQLiteOpenHelper {

    public static final String TABLE_CLIENTES = "CLIENTES";
    public static final String TABLE_MES = "MESES";
    public static final String TABLE_DETAILS = "DETALAILS";

    //Datos de la tabla TABLE_CLIENTES
    public static final String ID_CLIENTE = "id_cliente";
    public static final String CLIENTE = "cliente";

    //Datos de la tabla de todos los meses
    public static final String ID = "_id";
    public static final String CLIENTE_MES = "cliente";
    public static final String INCIDENCIAS_TOTALES = "incidencias_toatles";
    public static final String INCIDENCIAS_POR_CLIENTE = "incidencias_por_cliente";
    public static final String INCIDENCIAS_ST = "incidencias_st";
    public static final String PENDIENTES = "pendientes";
    public static final String CERRADOS = "cerrados";
    public static final String PROMEDIO_DIAS = "promedio_dias";
    public static final String PROMEDIO_HORAS_PORCENTAJE_DE_EFICIENCIA = "promedio_horas_porcentaje_de_eficiencia";
    public static final String PORCENTAJE_MES = "porcentaje";
    public static final String ABIERTAS_NO_MES = "abiertas_no";
    public static final String CERRADAS_NO_MES = "cerrada_no";
    public static final String PORCENTAJE_NO_MES = "porcentaje_no";
    public static final String MES_ID = "mes";

    //datos tabla DETTALES

    public static final String ID_NO = "id";
    public static final String CLIENTE_NO = "cliente";
    public static final String TOTAL_NO = "total";
    public static final String BLUE_NO = "blue";
    public static final String BLACK = "black";
    public static final String BLACKPLUS = "blackplus";
    public static final String BRYKE = "brike";
    public static final String FUSION = "fusion";
    public static final String ABIERTAS = "abiertas";
    public static final String CERRADAS = "cerradas";
    public static final String PORCENTAJE_NO = "pocentaje";
    public static final String CASINOS = "casinos";
    public static final String ABIERTAS1 = "abiertas1";
    public static final String CERRADAS1 = "cerrdas1";
    public static final String ABIERTAS2 = "abierta2";
    public static final String CERRADAS2 = "cerrdas2";

    static final String DB_NAME = "GRAFICAS_ODN.DB";

    static final int DB_VERSION = 2;

    private static final String CREATE_TABLE_CLIENTES = "create table if not exists "
            + TABLE_CLIENTES + "(" + ID_CLIENTE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CLIENTE + " TEXT );";

    private static final String CREATE_TABLE_MES = "create table if not exists " + TABLE_MES + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CLIENTE_MES + " TEXT, "
            + INCIDENCIAS_TOTALES + " TEXT,"
            + INCIDENCIAS_POR_CLIENTE + " TEXT,"
            + INCIDENCIAS_ST + " TEXT ,"
            + PENDIENTES + " TEXT, "
            + CERRADOS + " TEXT, "
            + PROMEDIO_DIAS + " TEXT, "
            + PROMEDIO_HORAS_PORCENTAJE_DE_EFICIENCIA + " TEXT, "
            + PORCENTAJE_MES + " TEXT, "
            + ABIERTAS_NO_MES + " TEXT, "
            + CERRADAS_NO_MES + " TEXT, "
            + PORCENTAJE_NO_MES + " TEXT, "
            + MES_ID + " INTEGER );";

    private static final String CREATE_TABLE_DETAILS = "create table if not exists " + TABLE_DETAILS + "(" + ID_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CLIENTE_NO + " TEXT, "
            + TOTAL_NO + " TEXT,"
            + BLUE_NO + " TEXT,"
            + BLACK + " TEXT ,"
            + BLACKPLUS + " TEXT, "
            + BRYKE + " TEXT, "
            + ABIERTAS + " TEXT, "
            + CERRADAS + " TEXT, "
            + PORCENTAJE_NO + " TEXT ,"
            + CASINOS + " TEXT ,"
            + ABIERTAS1 + " TEXT,"
            + CERRADAS1 + " TEXT,"
            + ABIERTAS2 + " TEXT,"
            + CERRADAS2 + " TEXT,"
            + FUSION + " TEXT);";


    public BDhelperGaficas(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLIENTES);
        db.execSQL(CREATE_TABLE_MES);
        db.execSQL(CREATE_TABLE_DETAILS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CLIENTES);
        onCreate(db);

        /**
         * se agrego @FUSION a la tabal de detalles y se modifica para la version 2
         * */


        if (2 > oldVersion) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_DETAILS + " ADD COLUMN " + FUSION + " TEXT DEFAULT ''");
            } catch (Exception e) {

            }

        }

    }

    public SQLiteDatabase getContextlist() {
        SQLiteDatabase context = this.getWritableDatabase();
        return context;
    }


}
