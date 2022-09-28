package com.example.devolucionmaterial.data_base.dbPedidosQr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.devolucionmaterial.activitys.foliosPendientesSeccion.peidosqr.model.Datos;


import java.util.ArrayList;

/**
 * Created by EDGAR ARANA on 17/08/2017.
 */

public class DBPedidoQrManger {

    private DBPedidoQrHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBPedidoQrManger(Context c) {
        context = c;
    }

    public DBPedidoQrManger open() throws SQLException {
        dbHelper = new DBPedidoQrHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }



    public void close() {
        dbHelper.close();
    }


    // TODO: 17/08/2017 inserta el pedido si es que no hay señal
    /**
     *
     *
     * @param devolucionId   siempre se inicia el id de la devulcion en -1
     * */


    public void insert(int pedido,
                       String componenteNuevo,
                       String componenteAnterior,
                       String codMaquina,
                       String folio,
                       int id_tecnico,
                       String sala,
                       int status, int devolucionId) {

        //se valida que no exita el mis pedido para que no se duplique en la lista
        String selectQuery = "SELECT  * FROM " + DBPedidoQrHelper.TABLE_PEDIDO_QR + " where " + DBPedidoQrHelper.PEDIDO + "= " + pedido;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Datos datos = null;
        if (cursor.moveToFirst()) {
            do {
                datos = new Datos(
                        cursor.getInt(0),
                        String.valueOf(cursor.getInt(1)),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(6),
                        cursor.getString(7),
                        cursor.getInt(8),
                        cursor.getInt(9)
                );
            } while (cursor.moveToNext());
        }

        if (datos == null) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DBPedidoQrHelper.PEDIDO, pedido);//1
            contentValue.put(DBPedidoQrHelper.COMPONENETE_NUEVO, componenteNuevo);//2
            contentValue.put(DBPedidoQrHelper.COMPONENTE_ANTERIOR, componenteAnterior);//3
            contentValue.put(DBPedidoQrHelper.COD_MAQUINA, codMaquina);//4
            contentValue.put(DBPedidoQrHelper.FOLIO, folio);//5
            contentValue.put(DBPedidoQrHelper.ID_TECNICO, id_tecnico);//6
            contentValue.put(DBPedidoQrHelper.SALA, sala);//7
            contentValue.put(DBPedidoQrHelper.ESTATUS, status);//8
            contentValue.put(DBPedidoQrHelper.ID_DEVOLUCION, devolucionId);//9
            database.insert(DBPedidoQrHelper.TABLE_PEDIDO_QR, null, contentValue);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBPedidoQrHelper.COMPONENETE_NUEVO, componenteNuevo);//2
            contentValues.put(DBPedidoQrHelper.COMPONENTE_ANTERIOR, componenteAnterior);//3
            contentValues.put(DBPedidoQrHelper.FOLIO, folio);//5
            contentValues.put(DBPedidoQrHelper.ID_TECNICO, id_tecnico);//6
            contentValues.put(DBPedidoQrHelper.SALA, sala);//7
            contentValues.put(DBPedidoQrHelper.ESTATUS, status);//8
            contentValues.put(DBPedidoQrHelper.ID_DEVOLUCION, devolucionId);//9
            int i = database.update(DBPedidoQrHelper.TABLE_PEDIDO_QR, contentValues, DBPedidoQrHelper._ID + " = " + datos.getId(), null);
        }


    }

    public void delete(long _id) {
        database.delete(DBPedidoQrHelper.TABLE_PEDIDO_QR, DBPedidoQrHelper._ID + "=" + _id, null);
    }

    /**
     * Se agrego orderby desc
     * */




    public ArrayList<Datos> getListPedidosGuardados() {
        String selectQuery = "SELECT  * FROM " + DBPedidoQrHelper.TABLE_PEDIDO_QR +" ORDER BY "+DBPedidoQrHelper._ID+" DESC";
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Datos> FavList = new ArrayList<Datos>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new Datos(
                        cursor.getInt(0),
                        String.valueOf(cursor.getInt(1)),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(6),
                        cursor.getString(7),
                        cursor.getInt(8),
                        cursor.getInt(9)
                ));
            } while (cursor.moveToNext());
        }

        //Log.e("ohfoiwsgauio", cursor.getInt(4)+"");
        if (cursor != null) cursor.close();
        return FavList;
    }







    public Datos getPeido(int pedido) {
        String selectQuery = "SELECT  * FROM " + DBPedidoQrHelper.TABLE_PEDIDO_QR + " where " + DBPedidoQrHelper.PEDIDO + "= " + pedido;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Datos datos = null;
        if (cursor.moveToFirst()) {
            do {
                datos = new Datos(
                        cursor.getInt(0),
                        String.valueOf(cursor.getInt(1)),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(6),
                        cursor.getString(7),
                        cursor.getInt(8),
                        cursor.getInt(9)
                );
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return datos;
    }


    public int getStatus(long id) {
        String selectQuery = "SELECT  "
                + DBPedidoQrHelper.ESTATUS
                + " FROM "
                + DBPedidoQrHelper.TABLE_PEDIDO_QR +
                " Where " + DBPedidoQrHelper._ID + " = " + id + "";
        int status = 0;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Datos> FavList = new ArrayList<Datos>();
        if (cursor.moveToFirst()) {
            do {
                status = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        if (cursor != null) cursor.close();
        return status;
    }

    /***
     * estatus 0 es d eentrada
     * estatus 1 falta validacion de componente
     * estatos 2 es que se envio la confirmacion
     * estatus 3 termiando pero con opcion de mandar guia
     * estatus 4 terminado con todo y guia
     * */
    public int update(long _id, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBPedidoQrHelper.ESTATUS, status);//

        int i = database.update(DBPedidoQrHelper.TABLE_PEDIDO_QR, contentValues, DBPedidoQrHelper._ID + " = " + _id, null);
        return i;
    }


    /**
     * Actualizar el estatus del item y ssi es verdaddero regresa el numero de filas afectadas
     * */

    public int updatePedido(int pedido, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBPedidoQrHelper.ESTATUS, status);//

        int i = database.update(DBPedidoQrHelper.TABLE_PEDIDO_QR, contentValues, DBPedidoQrHelper.PEDIDO + " = " + pedido, null);
        return i;

    }

    /**
     * @param pedido        numero del pedido a buscar
     * @param  status       para cambiar el estatus de item
     * @param  develocionId guarda el id de la devolucion que regresa el servicio
     *
     * */
    public int updatePedidoDevolucionId(int pedido, int status, int develocionId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBPedidoQrHelper.ESTATUS, status);//
        contentValues.put(DBPedidoQrHelper.ID_DEVOLUCION, develocionId);//

        int i = database.update(DBPedidoQrHelper.TABLE_PEDIDO_QR, contentValues, DBPedidoQrHelper.PEDIDO + " = " + pedido, null);
        return i;
    }

    /**
     * Este metodo es para borrar todo el contenido de las tablas
     * se usa para pruebas
     *
     * */
    public void borrartodo() {
        database.execSQL("delete from  " + DBPedidoQrHelper.TABLE_PEDIDO_QR);

    }


}
