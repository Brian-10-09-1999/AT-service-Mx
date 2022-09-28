package com.example.devolucionmaterial.activitys.graficos;

import android.content.Context;
import android.util.Log;

import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.data_base.dbgraficas.BDmanagerGaficas;
import com.example.devolucionmaterial.utils.JSONRead;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by EDGAR ARANA on 26/06/2017.
 */

public class RegisterDataJsonMchart {

    Context context;
    BDmanagerGaficas bDmanagerGaficas;

    public RegisterDataJsonMchart(Context context) {
        this.context = context;
        bDmanagerGaficas = new BDmanagerGaficas(context);
        bDmanagerGaficas.open();

    }


    void initInserRegister() {

        JSONRead jsonread = new JSONRead();
        // TODO: 19/06/2017 no operativas
        //de julio en adelnate
        InputStream isDetails = context.getResources().openRawResource(R.raw.details);
        //detalles de jinio hacia a tras
        InputStream isDetailsPast = context.getResources().openRawResource(R.raw.detaisl_past);

        try {

            /***
             *
             * se tuvo que sumar los valores ya que estan serpardos en dos json
             * entonces el primer for compara mediante el cliente los jsons y suma las catidades
             * y el segundo solo filtra los clientes nuevo que se allan agregado despues de julio
             *
             *
             * @param jsonArrayPastMont  es el json de los meses 2017 enenro a junio
             * @param jsonArrayOpertiva es el josn de los meses de julio en adelante
             * */
            JSONArray jsonArrayPastMont = new JSONArray(jsonread.loadJSONFromAsset(isDetailsPast));
            JSONArray jsonArrayOpertiva = new JSONArray(jsonread.loadJSONFromAsset(isDetails));
            // TODO: 29/09/17 for de comparacion de suma entre clientes
            for (int i = 0; i < jsonArrayOpertiva.length(); i++) {
                Log.e("i", String.valueOf(i));
                JSONObject joO = jsonArrayOpertiva.getJSONObject(i);
                JSONObject joA;
                for (int a = 0; a < jsonArrayPastMont.length(); a++) {
                    joA = jsonArrayPastMont.getJSONObject(a);

                    if (joO.getString("clientes").toUpperCase().equals(joA.getString("clientes").toUpperCase())) {

                        Log.e("cliente", joO.getString("clientes"));
                        Log.e("ABIERTAS1", String.valueOf(joO.getInt("ABIERTAS1")));
                        Log.e("ABIERTAS1", String.valueOf(joA.getInt("ABIERTAS1")));

                        int sumaAbiertas1 = joO.getInt("ABIERTAS1") + joA.getInt("ABIERTAS1");
                        int sumaCerradas1 = joO.getInt("CERRADAS1") + joA.getInt("CERRADAS1");
                        int sumaAbiertas2 = joO.getInt("ABIERTAS2") + joA.getInt("ABIERTAS2");
                        int sumaCerradas2 = joO.getInt("CERRADAS2") + joA.getInt("CERRADAS2");
                        Log.e("sumaAbiertas1", String.valueOf(sumaAbiertas1));

                        bDmanagerGaficas.insertDetalles(
                                joO.getString("clientes").toUpperCase(),
                                joO.getString("total"),
                                joO.getString("blue"),
                                joO.getString("black"),
                                joO.getString("blackplus"),
                                joO.getString("bryke"),
                                joO.getString("Abiertas"),
                                joO.getString("cerradas"),
                                joO.getString("porcentaje"),
                                joO.getString("casinos"),
                                String.valueOf(sumaAbiertas1),
                                String.valueOf(sumaCerradas1),
                                String.valueOf(sumaAbiertas2),
                                String.valueOf(sumaCerradas2),
                                joO.getString("fusion")
                        );
                    }

                }


            }
            // TODO: 29/09/17 for para ver que los clientes nuevos se agregen
            for (int i = 0; i < jsonArrayOpertiva.length(); i++) {
                Log.e("i", String.valueOf(i));
                JSONObject joO = jsonArrayOpertiva.getJSONObject(i);
                JSONObject joA;
                for (int a = 0; a < jsonArrayPastMont.length(); a++) {
                    joA = jsonArrayPastMont.getJSONObject(a);

                    String nombre = bDmanagerGaficas.verficarCliente(joO.getString("clientes").toUpperCase());
                    // Log.e("cliente", nombre);
                    if (nombre == null) {
                        bDmanagerGaficas.insertDetalles(
                                joO.getString("clientes").toUpperCase(),
                                joO.getString("total"),
                                joO.getString("blue"),
                                joO.getString("black"),
                                joO.getString("blackplus"),
                                joO.getString("bryke"),
                                joO.getString("Abiertas"),
                                joO.getString("cerradas"),
                                joO.getString("porcentaje"),
                                joO.getString("casinos"),
                                joO.getString("ABIERTAS1"),
                                joO.getString("CERRADAS1"),
                                joO.getString("ABIERTAS2"),
                                joO.getString("CERRADAS2"),
                                joO.getString("fusion")
                        );

                        Log.e("siiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii", joO.getString("clientes") + " num " + i);
                    } else {
                        Log.e("cliente", nombre);
                    }

                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error isDetails", e + "");
        }

        // TODO: mayo-diciembre 2017


      /*  InputStream isMayo = context.getResources().openRawResource(R.raw.mayo);
        InputStream isJunio = context.getResources().openRawResource(R.raw.junio);
        InputStream isJulio = context.getResources().openRawResource(R.raw.julio);
        InputStream isAgosto = context.getResources().openRawResource(R.raw.agosto);
        InputStream isSeptiembre = context.getResources().openRawResource(R.raw.septiembre);

        InputStream isOctubre = context.getResources().openRawResource(R.raw.octubre);
        InputStream isNoviembre = context.getResources().openRawResource(R.raw.noviembre);
        InputStream isDiciembre = context.getResources().openRawResource(R.raw.diciembre);
        // TODO  enero abril 2018
        InputStream isEnero = context.getResources().openRawResource(R.raw.enero2);
        InputStream isFebrero = context.getResources().openRawResource(R.raw.febrero2);
        InputStream isMarzo = context.getResources().openRawResource(R.raw.marzo2);
        InputStream isAbril = context.getResources().openRawResource(R.raw.abril2);*/

        InputStream isMayo = context.getResources().openRawResource(R.raw.octubre);
        InputStream isJunio = context.getResources().openRawResource(R.raw.noviembre);
        InputStream isJulio = context.getResources().openRawResource(R.raw.diciembre);
        InputStream isAgosto = context.getResources().openRawResource(R.raw.enero2);
        InputStream isSeptiembre = context.getResources().openRawResource(R.raw.febrero2);
        InputStream isOctubre = context.getResources().openRawResource(R.raw.marzo2);
        InputStream isNoviembre = context.getResources().openRawResource(R.raw.abril2);

        InputStream isDiciembre = context.getResources().openRawResource(R.raw.mayo18);
        InputStream isEnero = context.getResources().openRawResource(R.raw.junio18);
        InputStream isFebrero = context.getResources().openRawResource(R.raw.julio18);
        InputStream isMarzo = context.getResources().openRawResource(R.raw.agosto18);
        InputStream isAbril = context.getResources().openRawResource(R.raw.septiembre18);




        try {
            // TODO: 14/06/2017 mayo
            JSONArray jsonArrayMayo = new JSONArray(jsonread.loadJSONFromAsset(isMayo));
            for (int i = 0; i < jsonArrayMayo.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                JSONObject joE = jsonArrayMayo.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        1
                );

            }

            // TODO: 14/06/2017 junio
            JSONArray jsonArrayJunio = new JSONArray(jsonread.loadJSONFromAsset(isJunio));
            for (int i = 0; i < jsonArrayJunio.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                JSONObject joE = jsonArrayJunio.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        2
                );

            }
            // TODO: 28/09/2017 julio
            JSONArray jsonArrayJulio = new JSONArray(jsonread.loadJSONFromAsset(isJulio));
            for (int i = 0; i < jsonArrayJulio.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                JSONObject joE = jsonArrayJulio.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        3
                );

            }
            // TODO: 28/09/2017 agosto
            JSONArray jsonArrayAgosto = new JSONArray(jsonread.loadJSONFromAsset(isAgosto));
            for (int i = 0; i < jsonArrayAgosto.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                JSONObject joE = jsonArrayAgosto.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        4
                );

            }

            // TODO: 28/09/2017 septiembre
            JSONArray jsonArraySeptiembre = new JSONArray(jsonread.loadJSONFromAsset(isSeptiembre));
            for (int i = 0; i < jsonArraySeptiembre.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                JSONObject joE = jsonArraySeptiembre.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        5
                );

            }




            JSONArray jsonArrayOctubre = new JSONArray(jsonread.loadJSONFromAsset(isOctubre));
            for (int i = 0; i < jsonArrayOctubre.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                JSONObject joE = jsonArrayOctubre.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        6
                );


            }



            JSONArray jsonArrayNoviembre = new JSONArray(jsonread.loadJSONFromAsset(isNoviembre));
            for (int i = 0; i < jsonArrayNoviembre.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                JSONObject joE = jsonArrayNoviembre.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        7
                );


            }


            JSONArray jsonArrayDiciembre = new JSONArray(jsonread.loadJSONFromAsset(isDiciembre));
            for (int i = 0; i < jsonArrayDiciembre.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                JSONObject joE = jsonArrayDiciembre.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        8
                );


            }



            // TODO: 14/06/2017 enero

            JSONArray jsonArrayEnero = new JSONArray(jsonread.loadJSONFromAsset(isEnero));
            for (int i = 0; i < jsonArrayEnero.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                bDmanagerGaficas.insert(jsonArrayEnero.getJSONObject(i).getString("clientes").toUpperCase());
                JSONObject joE = jsonArrayEnero.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        9
                );

            }


            // TODO: 14/06/2017 febrero
            JSONArray jsonArrayFebrero = new JSONArray(jsonread.loadJSONFromAsset(isFebrero));
            for (int i = 0; i < jsonArrayFebrero.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes


                JSONObject joE = jsonArrayFebrero.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        10
                );

            }

            // TODO: 14/06/2017 marzo
            JSONArray jsonArrayMarzo = new JSONArray(jsonread.loadJSONFromAsset(isMarzo));
            for (int i = 0; i < jsonArrayMarzo.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                JSONObject joE = jsonArrayMarzo.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        11
                );

            }

            // TODO: 14/06/2017 abril
            JSONArray jsonArrayAbril = new JSONArray(jsonread.loadJSONFromAsset(isAbril));
            for (int i = 0; i < jsonArrayAbril.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                JSONObject joE = jsonArrayAbril.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        12
                );

            }
/*
            JSONArray jsonArrayMayo18 = new JSONArray(jsonread.loadJSONFromAsset(isMayo18));
            for (int i = 0; i < jsonArrayMayo18.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                JSONObject joE = jsonArrayMayo18.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        13
                );

            }

*/

/*
            JSONArray jsonArrayDiciembre2 = new JSONArray(jsonread.loadJSONFromAsset(isDiciembre2));
            for (int i = 0; i < jsonArrayDiciembre2.length(); i++) {
                // TODO: 14/06/2017 se sacan los clientes del primer mes
                JSONObject joE = jsonArrayDiciembre2.getJSONObject(i);
                bDmanagerGaficas.insertItemMes(
                        joE.getString("clientes").toUpperCase(),
                        validatEmpety(joE.getString("Incidencias_totales")),
                        validatEmpety(joE.getString("Incidencias_por_cliente")),
                        validatEmpety(joE.getString("Incidencias_ST")),
                        validatEmpety(joE.getString("Pendientes")),
                        validatEmpety(joE.getString("Cerrados")),
                        validatEmpety(joE.getString("Promedio_dias")),
                        validatEmpety(joE.getString("Promedio_horas_porcentaje_de_eficiencia")),
                        validatEmpety(joE.getString("porcentaje")),
                        validatEmpety(joE.getString("abiertas_no")),
                        validatEmpety(joE.getString("cerradas_no")),
                        validatEmpety(joE.getString("porcentaje_no")),
                        13
                );


            }

*/



        } catch (JSONException e) {
            e.printStackTrace();
        }

        bDmanagerGaficas.close();
    }

    private String validatEmpety(String value) {
        if (value.trim().isEmpty()) {
            return "0";
        } else {
            return value;
        }
    }
}
