package com.example.devolucionmaterial.activitys.viaticos;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devolucionmaterial.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import static com.example.devolucionmaterial.R2.id.toolbar;

public class ActivityFileExplore extends ListActivity {
    private List<String> listaNombresArchivos;
    private List<String> listaRutasArchivos;
    private ArrayAdapter<String> adaptador;
    private String directorioRaiz,RutaCarpetaActual;
    private TextView carpetaActual;

    private String[] tipeFile;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explore);
        carpetaActual = (TextView) findViewById(R.id.rutaActual);
        directorioRaiz = Environment.getExternalStorageDirectory().getPath();
        //verArchivosDirectorio(directorioRaiz);
        verArchivosDirectorio("/storage/emulated/0/Download/");
    }







    /**
     * Muestra los archivos del directorio pasado como parametro en un listView
     *
     * @param rutaDirectorio
     */
    private void verArchivosDirectorio(String rutaDirectorio) {
        //carpetaActual.setText("Estas en: " + rutaDirectorio);
        carpetaActual.setText("Selecciona Archivo a Subir ");
        RutaCarpetaActual=rutaDirectorio;
        listaNombresArchivos = new ArrayList<String>();
        listaRutasArchivos = new ArrayList<String>();
        File directorioActual = new File(rutaDirectorio);
        File[] listaArchivos = directorioActual.listFiles();
        int x = 0;




        // Si no es nuestro directorio raiz creamos un elemento que nos
        // permita volver al directorio padre del directorio actual
        if (!rutaDirectorio.equals(directorioRaiz)) {
            listaNombresArchivos.add("...");
            listaRutasArchivos.add(directorioActual.getParent());
            x = 1;
        }
        try {

            // Almacenamos las rutas de todos los archivos y carpetas del directorio
            for (File archivo : listaArchivos) {
                listaRutasArchivos.add(archivo.getPath());
            }

        }
        catch (Exception e){Toast toast3 =
                Toast.makeText(getApplicationContext(), "Error aqui ", Toast.LENGTH_SHORT);

            toast3.show();}
        // Ordenamos la lista de archivos para que se muestren en orden alfabetico
        Collections.sort(listaRutasArchivos, String.CASE_INSENSITIVE_ORDER);
        // Recorredos la lista de archivos ordenada para crear la lista de los nombres
        // de los archivos que mostraremos en el listView

        Log.i("-----------------","listaRutasarchivos"+listaRutasArchivos.size()+"  listaArchivos="+listaArchivos.length);

        if(listaArchivos.length!=0){ tipeFile=new String[listaRutasArchivos.size()];

        }
        else{tipeFile=new String[2];

        }

        for (int i = x; i < listaRutasArchivos.size(); i++) {
            File archivo = new File(listaRutasArchivos.get(i));
            if (archivo.isFile()) {
                listaNombresArchivos.add(archivo.getName());
                tipeFile[i]="f";
            } else {
                //listaNombresArchivos.add("/" + archivo.getName());
                listaNombresArchivos.add(archivo.getName());
                tipeFile[i]="d";
            }

        }

        // Si no hay ningun archivo en el directorio lo indicamos
        if (listaArchivos.length < 1) {

            Log.i("------------------","in  iffff");
            listaNombresArchivos.add("No hay ningun archivo");
            listaRutasArchivos.add(rutaDirectorio);
            tipeFile[0]="b";//back
            tipeFile[1]="e";//empty
        }


        // Creamos el adaptador y le asignamos la lista de los nombres de los
        // archivos y el layout para los elementos de la lista
   /*     adaptador = new ArrayAdapter<String>(this,
                R.layout.text_view_lista_archivos, listaNombresArchivos);*/
        onContentChanged();
        String[] names=(String[]) listaNombresArchivos.toArray(new String[listaNombresArchivos.size()] );
        CustomListAdapterExplore adapter2=new CustomListAdapterExplore(ActivityFileExplore.this, names,tipeFile);

       // setListAdapter(adaptador);

        setListAdapter(adapter2);

    }



    protected void onListItemClick(ListView l, View v, int position, long id) {
        // Obtenemos la ruta del archivo en el que hemos hecho click en el
        // listView
        File archivo = new File(listaRutasArchivos.get(position));
        // Si es un archivo se muestra un Toast con su nombre y si es un directorio
        // se cargan los archivos que contiene en el listView
        if (archivo.isFile()) {

            /*Toast.makeText(this,
                    "Has seleccionado el archivo: " + archivo.getName(),
                    Toast.LENGTH_LONG).show();*/
            Intent data = new Intent();
            data.putExtra("NameFile",archivo.getName());
            data.putExtra("Path",RutaCarpetaActual);
            //data.setData(Uri.parse(listaRutasArchivos.get(position)));
            setResult(RESULT_OK, data);
            finish();

        } else {
            // Si no es un directorio mostramos todos los archivos que contiene
            verArchivosDirectorio(listaRutasArchivos.get(position));
        }

    }

}
