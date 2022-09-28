package com.example.devolucionmaterial.data_base.dbPedidosQr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by EDGAR ARANA on 17/08/2017.
 */

public class DBPedidoQrHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_PEDIDO_QR = "PEDIDOS_QR";

    // Table contactos
    public static final String _ID = "id";
    public static final String PEDIDO = "pedido";
    public static final String COMPONENETE_NUEVO = "componenteNuevo";
    public static final String COMPONENTE_ANTERIOR = "componenteAnterior";
    public static final String COD_MAQUINA = "codMaquina";
    public static final String FOLIO = "folio";
    public static final String ID_TECNICO = "id_tecnico";
    public static final String SALA = "sala";
    public static final String ESTATUS = "estatus";
    public static final String ID_DEVOLUCION = "devolucionId";


    // Database Information
    static final String DB_NAME = "PEDIDO_QR_ODN.DB";

    // database version
    static final int DB_VERSION = 2;

    private static final String CREATE_TABLE = "create table if not exists "
            + TABLE_PEDIDO_QR + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PEDIDO + " INTEGER, "
            + COMPONENETE_NUEVO + " TEXT,"
            + COMPONENTE_ANTERIOR + " TEXT,"
            + COD_MAQUINA + " INTEGER ,"
            + FOLIO + " TEXT ,"
            + ID_TECNICO + " INTEGER ,"
            + SALA + " TEXT ,"
            + ESTATUS + " INTEGER ,"
            + ID_DEVOLUCION + " INTEGER );";

    public DBPedidoQrHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREAR_PEDIDO);

        onCreate(db);

        // TODO: 30/10/2017 se agrego el idDevolicon a loa tabla
        if (2 > oldVersion) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_PEDIDO_QR + " ADD COLUMN " + ID_DEVOLUCION + " TEXT DEFAULT ''");
            } catch (Exception e) {

            }

        }
    }

    public SQLiteDatabase getContextlist() {
        SQLiteDatabase context = this.getWritableDatabase();
        return context;
    }

}