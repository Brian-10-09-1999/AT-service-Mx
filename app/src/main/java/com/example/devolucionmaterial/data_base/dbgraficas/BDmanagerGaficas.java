package com.example.devolucionmaterial.data_base.dbgraficas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.devolucionmaterial.beans.DetalisGraficas;
import com.example.devolucionmaterial.beans.Kpi;


import java.util.ArrayList;


/**
 * Created by EDGAR ARANA on 14/06/2017.
 */

public class BDmanagerGaficas {

    private BDhelperGaficas dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public BDmanagerGaficas(Context c) {
        context = c;
    }

    public BDmanagerGaficas open() throws SQLException {
        dbHelper = new BDhelperGaficas(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    // TODO: 14/06/2017 inserta los clientes
    public void insert(String cliente) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(BDhelperGaficas.CLIENTE, cliente);//1
        database.insert(BDhelperGaficas.TABLE_CLIENTES, null, contentValue);
    }

    public ArrayList<String> getNombreClientes() {
        String selectQuery = "SELECT  * FROM " + BDhelperGaficas.TABLE_CLIENTES;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<String> FavList = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(
                        cursor.getString(1)
                );
            } while (cursor.moveToNext());
        }

        if (cursor != null) cursor.close();
        return FavList;
    }


    // TODO: 14/06/2017 se agrga a la tabla de
    public void insertItemMes(
            String id_clinente,
            String incidencias_totales,
            String incidencias_por_cliente,
            String incidencias_st,
            String pendientes,
            String cerrados,
            String promedio_dias,
            String promedio_horas_porcentaje_de_eficiencia,
            String porcentaje,
            String abiertas_no,
            String cerradas_no,
            String porcentaje_no,
            int numMes) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(BDhelperGaficas.CLIENTE_MES, id_clinente);//1
        contentValue.put(BDhelperGaficas.INCIDENCIAS_TOTALES, incidencias_totales);//2
        contentValue.put(BDhelperGaficas.INCIDENCIAS_POR_CLIENTE, incidencias_por_cliente);//3
        contentValue.put(BDhelperGaficas.INCIDENCIAS_ST, incidencias_st);//4
        contentValue.put(BDhelperGaficas.PENDIENTES, pendientes);//5
        contentValue.put(BDhelperGaficas.CERRADOS, cerrados);//6
        contentValue.put(BDhelperGaficas.PROMEDIO_DIAS, promedio_dias);//7
        contentValue.put(BDhelperGaficas.PROMEDIO_HORAS_PORCENTAJE_DE_EFICIENCIA, promedio_horas_porcentaje_de_eficiencia);//8
        contentValue.put(BDhelperGaficas.PORCENTAJE_MES, porcentaje);//9
        contentValue.put(BDhelperGaficas.ABIERTAS_NO_MES, abiertas_no);//10
        contentValue.put(BDhelperGaficas.CERRADAS_NO_MES, cerradas_no);//11
        contentValue.put(BDhelperGaficas.PORCENTAJE_NO_MES, porcentaje_no);//12
        contentValue.put(BDhelperGaficas.MES_ID, numMes);//13

        database.insert(BDhelperGaficas.TABLE_MES, null, contentValue);


    }


    public ArrayList<Kpi> getListForName(String nombre) {


        String selectQuery = "SELECT  * FROM " + BDhelperGaficas.TABLE_MES
                + " WHERE " + BDhelperGaficas.CLIENTE_MES + " = " + "'" + nombre + "'" + "  ORDER by  " + BDhelperGaficas.MES_ID + " ASC";
        SQLiteDatabase db = dbHelper.getContextlist();
        //Log.e("consulta", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Kpi> FavList = new ArrayList<Kpi>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new Kpi(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getInt(13)//mes
                ));
            } while (cursor.moveToNext());
        }

      //  for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
        //    Log.i("-------juan------------",cursor.getString(0)+ "   "+cursor.getString(1)+"   "+cursor.getString(2)+"   "+cursor.getString(13));
        //}

        if (cursor != null) cursor.close();





        return FavList;




    }

    // TODO: 19/06/2017 metodos de la tabala no operatvo


    public void insertDetalles(String cliente, String total, String blue, String black, String blackplus, String bryke, String abiertas, String cerrados, String porcentaje, String casino, String abierta1, String cerradas1, String abierta2, String cerradas2, String fusion) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(BDhelperGaficas.CLIENTE_NO, cliente);//1
        contentValue.put(BDhelperGaficas.TOTAL_NO, total);//2
        contentValue.put(BDhelperGaficas.BLUE_NO, blue);//3
        contentValue.put(BDhelperGaficas.BLACK, black);//4
        contentValue.put(BDhelperGaficas.BLACKPLUS, blackplus);//5
        contentValue.put(BDhelperGaficas.BRYKE, bryke);//6
        contentValue.put(BDhelperGaficas.ABIERTAS, abiertas);//7
        contentValue.put(BDhelperGaficas.CERRADAS, cerrados);//8
        contentValue.put(BDhelperGaficas.PORCENTAJE_NO, porcentaje);//9
        contentValue.put(BDhelperGaficas.CASINOS, casino);//10
        contentValue.put(BDhelperGaficas.ABIERTAS1, abierta1);//11
        contentValue.put(BDhelperGaficas.CERRADAS1, cerradas1);//12
        contentValue.put(BDhelperGaficas.ABIERTAS2, abierta2);//13
        contentValue.put(BDhelperGaficas.CERRADAS2, cerradas2);//14
        contentValue.put(BDhelperGaficas.FUSION, fusion);//15
        database.insert(BDhelperGaficas.TABLE_DETAILS, null, contentValue);
    }


    public ArrayList<DetalisGraficas> getListForDetalles(String nombre) {
        String selectQuery = "SELECT  * FROM " + BDhelperGaficas.TABLE_DETAILS
                + " WHERE " + BDhelperGaficas.CLIENTE_NO + " = " + "'" + nombre + "'";
        SQLiteDatabase db = dbHelper.getContextlist();
        // Log.e("consulta", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<DetalisGraficas> FavList = new ArrayList<DetalisGraficas>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new DetalisGraficas(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14),
                        cursor.getString(15)
                ));
            } while (cursor.moveToNext());
        }

        if (cursor != null) cursor.close();
        return FavList;
    }

    public String verficarCliente(String nombre) {
        String selectQuery = "SELECT  * FROM " + BDhelperGaficas.TABLE_DETAILS
                + " WHERE " + BDhelperGaficas.CLIENTE_NO + " = " + "'" + nombre + "'";
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String nombreCliente = cursor.getString(1);
                if (cursor != null) cursor.close();
                return nombreCliente;
            } while (cursor.moveToNext());
        } else {
            if (cursor != null)
                cursor.close();
            return null;
        }


    }

    /**
     * boorado de datos
     */
    public void borarDatos() {
        database.execSQL("delete from  " + BDhelperGaficas.TABLE_DETAILS);
        database.execSQL("delete from  " + BDhelperGaficas.TABLE_CLIENTES);
        database.execSQL("delete from  " + BDhelperGaficas.TABLE_DETAILS);

    }

}
