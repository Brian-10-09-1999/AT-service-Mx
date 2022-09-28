package com.example.devolucionmaterial.data_base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by EDGAR ARANA on 08/06/2017.
 */

public class DataBaseCrearPedidoHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_CREAR_PEDIDO = "CREAR_PEDIDO";

    // Table contactos
    public static final String _ID = "id";
    public static final String SALA = "sala_id";
    public static final String MAQUINA = "maquina";
    public static final String COMPONENTE = "componente";
    public static final String ID_TECNICO = "tecnico_id";
    public static final String ESTATUS = "estatus";
    public static final String PEDIDO = "peiddo";
    public static final String FOLIO = "folio";

    // Database Information
    static final String DB_NAME = "CREAR_PEDIDO_ODN.DB";

    // database version
    static final int DB_VERSION = 2;

    private static final String CREATE_TABLE = "create table if not exists  "
            + TABLE_CREAR_PEDIDO + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SALA + " INTEGER, "
            + MAQUINA + " TEXT,"
            + COMPONENTE + " TEXT,"
            + ID_TECNICO + " INTEGER ,"
            + ESTATUS + " INTEGER, "
            + PEDIDO + " TEXT, "
            + FOLIO+ " TEXT );";

    public DataBaseCrearPedidoHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREAR_PEDIDO);
        /**
         * se agrego @param FOLIO
         * */
        if (2 > oldVersion) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_CREAR_PEDIDO + " ADD COLUMN " + FOLIO + " TEXT DEFAULT ''");
            } catch (Exception e) {

            }

        }
        onCreate(db);
    }

    public SQLiteDatabase getContextlist() {
        SQLiteDatabase context = this.getWritableDatabase();
        return context;
    }

}
