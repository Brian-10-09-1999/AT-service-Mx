package com.example.devolucionmaterial.data_base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.devolucionmaterial.beans.PedidosGuardados;
import com.example.devolucionmaterial.utils.LocationRegion;

import java.util.ArrayList;


/**
 * Created by EDGAR ARANA on 08/06/2017.
 */

public class DBCrearPedido {
    private DataBaseCrearPedidoHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBCrearPedido(Context c) {
        context = c;
    }

    public DBCrearPedido open() throws SQLException {
        dbHelper = new DataBaseCrearPedidoHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // TODO: 26/12/2016  se agrega el id del tecnico directo desde el insert para no pedir lo cada que se quiera ihacer insert
    public void insert(int sala, String maquina, String componente, int status, String pedido, String folio) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DataBaseCrearPedidoHelper.SALA, sala);//1
        contentValue.put(DataBaseCrearPedidoHelper.MAQUINA, maquina);//2
        contentValue.put(DataBaseCrearPedidoHelper.COMPONENTE, componente);//3
        contentValue.put(DataBaseCrearPedidoHelper.ID_TECNICO, BDVarGlo.getVarGlo(context, "INFO_USUARIO_ID"));//4
        contentValue.put(DataBaseCrearPedidoHelper.ESTATUS, status);//5
        contentValue.put(DataBaseCrearPedidoHelper.PEDIDO, pedido);//6
        contentValue.put(DataBaseCrearPedidoHelper.FOLIO, folio);//7
        database.insert(DataBaseCrearPedidoHelper.TABLE_CREAR_PEDIDO, null, contentValue);
    }

    public void delete(long _id) {
        database.delete(DataBaseCrearPedidoHelper.TABLE_CREAR_PEDIDO, DataBaseCrearPedidoHelper._ID + "=" + _id, null);
    }

    /**
     * Se agrego orderby desc
     * */
    public ArrayList<PedidosGuardados> getListPedidosGuardados() {
        String selectQuery = "SELECT  * FROM " + DataBaseCrearPedidoHelper.TABLE_CREAR_PEDIDO+" ORDER BY "+DataBaseCrearPedidoHelper._ID+" DESC";
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<PedidosGuardados> FavList = new ArrayList<PedidosGuardados>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new PedidosGuardados(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getString(7)
                ));
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        //Log.e("ohfoiwsgauio", cursor.getInt(4)+"");
        return FavList;
    }

    public int update(long _id, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseCrearPedidoHelper.ESTATUS, status);//

        int i = database.update(DataBaseCrearPedidoHelper.TABLE_CREAR_PEDIDO, contentValues, DataBaseCrearPedidoHelper._ID + " = " + _id, null);
        return i;
    }

    public int getStatus(long id) {
        String selectQuery = "SELECT  " + DataBaseCrearPedidoHelper.ESTATUS + " FROM " + DataBaseCrearPedidoHelper.TABLE_CREAR_PEDIDO + " Where " + DataBaseCrearPedidoHelper._ID + " = " + id + "";
        int status = 0;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<PedidosGuardados> FavList = new ArrayList<PedidosGuardados>();
        if (cursor.moveToFirst()) {
            do {
                status = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        if (cursor != null) cursor.close();
        return status;
    }

    // TODO: 09/06/2017 consulta para tarea en segundo planao para enviar automanticamente los pedidos
    public ArrayList<PedidosGuardados> getListBackGround() {
        String selectQuery = "SELECT  * FROM " + DataBaseCrearPedidoHelper.TABLE_CREAR_PEDIDO
                + " WHERE " + DataBaseCrearPedidoHelper.ESTATUS + " = 0";
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<PedidosGuardados> FavList = new ArrayList<PedidosGuardados>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new PedidosGuardados(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getString(7)
                ));
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        //Log.e("ohfoiwsgauio", cursor.getInt(4)+"");
        return FavList;
    }


}
