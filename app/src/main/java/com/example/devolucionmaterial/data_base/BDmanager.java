package com.example.devolucionmaterial.data_base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BDmanager {

    /**
     * si se van alterar las tablas se debe agrgar aqui tabien la columna ya que
     * en la versiones de sqlite  no vulve a crear la tabla,
     * pero si se vuelve a instalar desde 0 debe arrancar conodas las tablas actulizadas ya que no entra en las condiciones de onUpgrade
     **/

    //Nombres de las tablas a crear
    public static final String TABLE_REFACCIONES = "crefacciones"; //Servidor-------------------------------------------------------------
    public static final String TABLE_SALA = "sala";
    public static final String TABLE_SALIDAS = "salidas";
    public static final String TABLE_DESCRIPCION_SALIDA = "descripcionDeSalida";
    public static final String TABLE_SALIDAS_MEMORIA = "salidasMemoria";
    public static final String TABLE_DESCRIPCION_SALIDAS_MEMORIA = "descripcionSalidasMemoria";
    public static final String TABLE_COMENTARIOS_OS = "comentariosOS";                              //Aqui y Servidor
    public static final String TABLE_ESTATUS_TEC = "estatusTec";
    public static final String TABLE_SALA_JUEGO_LIC="csalajuegolic";
    public static final String TABLE_FALLAS="cfallas";
    public static final String TABLE_SUBFALLAS="csubfallas";



    //Datos de la tabla Fallas
    public static final String ID = "_id";
    public static final String ID_FALLA = "idFalla";
    public static final String NOMBRE_FALLA = "nombreFalla";

    //Datos de la tabla SubFallas
    public static final String ID_SUBFALLA = "idSubFalla";
    public static final String NOMBRE_SUBFALLA = "nombreSubFalla";
    public static final String ID_SUBFALLAFALLA = "idSubFallaFalla";

    //datos para tabla SalaJuegoLic
    public static final String ID_MAQUINA="maquinaid";
    public static final String ID_SALA="salaid";
    public static final String LICENCIA="licencia";
    public static final String ID_JUEGO="juegoid";
    public static final String NOMBRE_JUEGO="nombreJuego";




    //Datos de la tabla SalidasMemoria
    public static final String CN_ID_SALIDA_MEMORIA = "_id";
    public static final String CN_USUARIO_MEMORIA = "usuarioMemoria";
    public static final String CN_FECHA_MEMORIAS = "fechaMemoria";
    public static final String CN_STATUS_MEMORIAS = "statusMemoria";

    //Datos de la tabla PiezaSalidasMemoria
    public static final String CN_ID_PIEZA_SALIDA_MEMORIA = "_id";
    public static final String CN_FK_SALIDA_MEMORIA = "fksalidaMemoria";
    public static final String CN_FOLIO_MEMORIA = "folio";
    public static final String CN_CANTIDAD_MEMORIA = "cantidadMemoria";
    public static final String CN_OS = "os";
    public static final String CN_STATUS_PIEZA_MEMORIA = "statusPiezaMemoria";
    public static final String CN_TIPO_MEMORIA = "tipoMemoria";

    //Datos de la tabla crefacciones---------------------------------------------------------------
    public static final String CN_ID = "_id";
    public static final String CN_ID_REFACCIONES = "_idRef";
    public static final String CN_CLAVE = "clave";
    public static final String CN_NOMBRE = "nombre";
    public static final String CN_STATUS = "status";
    public static final String CN_SUSTITUTO = "sustituto";
    public static final String CN_NO_SERIE = "noSerie";

    //Datos de la tabla Sala
    public static final String CN_ID_SALA = "_id";
    public static final String CN_NOMBRE_SALA = "sala";

    //Datos de la tabla Salidas
    public static final String CN_ID_SALIDA = "_id";
    public static final String CN_FK_SALA = "_idSala";
    public static final String CN_NO_SALIDA = "noSalida";
    public static final String CN_USUARIO = "usuario";
    public static final String CN_FECHA_SALIDA = "fecha";
    public static final String CN_STATUS_SALIDA = "statusSalida";
    public static final String CN_FOLIO = "folio";
    public static final String CN_OFFICE_ID = "officeId";
    public static final String CN_PEDIDO = "pedido";
    public static final String CN_NO_GUIA = "noGuia";
    public static final String CN_TIPO = "tipo";
    public static final String CN_TECNICO_ID_BAJA = "tecnicoidBaja";

    //Datos de la tabla DescripcionSalida
    public static final String CN_ID_DESCRIPCION_SALIDA = "_id";
    public static final String CN_FK_SALIDA = "_idSalida";
    public static final String CN_CODIGO = "codigo";
    public static final String CN_DESCRIPCION = "descripcion";
    public static final String CN_CANTIDAD = "cantidad";
    public static final String CN_SERIE = "serie";
    public static final String CN_MAQUINA = "maquina"; // TODO: 09/06/2017 se agrego estae campo alterando la tabla desde el helper

    //Datos de la tabla ComentariosOS
    public static final String CN_ID_COMENTARIOS_OS = "_id";
    public static final String CN_OS_ID = "osid";
    public static final String CN_COMENTARIOS_OS = "comentariosOS";
    public static final String CN_FECHA_OS = "fechaComentarios";
    public static final String CN_ESTATUS_ENTREGA = "estatusEntrega";
    public static final String CN_NOMBRE_USUARIO_OS = "nombreUsuario";

    //Datos de la tabla EstatusTecnico
    public static final String CN_ID_ESTATUS_TEC = "_id";
    public static final String CN_ESTATUS_TEC = "estatusid";
    public static final String TECNICO_ID = "tecnicoid";
    public static final String CN_REGION_ID = "regionid";
    public static final String CN_EST_EN_SERV = "estEnServ";
    public static final String CN_OFFICE_ID_ESTATUS = "officeid";
    public static final String CN_PASSWORD = "password";
    public static final String CN_USER = "user";
    public static final String CN_NOMBRE_USER = "nombreUser";
    public static final String CN_CONTADOR_NOTIF = "contadorNotif";

   // private SQLiteDatabase db; //real
    private SQLiteDatabase db;//cambiado


    public static final String CREATE_TABLE_FALLAS = "create table if not exists " + TABLE_FALLAS + " ("
            + ID + " integer primary key autoincrement,"
            + ID_FALLA+ " text not null,"
            + NOMBRE_FALLA + " text not null);";

    public static final String CREATE_TABLE_SUBFALLAS = "create table if not exists " + TABLE_SUBFALLAS + " ("
            + ID + " integer primary key autoincrement,"
            + ID_SUBFALLA+ " text not null,"
            + NOMBRE_SUBFALLA+ " text not null,"
            + ID_SUBFALLAFALLA + " text not null);";

    public static final String CREATE_TABLE_SALA_JUEGO_LIC = "create table if not exists " + TABLE_SALA_JUEGO_LIC + " ("
            + ID + " integer primary key autoincrement,"
            + ID_MAQUINA + " text not null,"
            + ID_SALA + " text not null,"
            + LICENCIA + " text not null,"
            + ID_JUEGO + " text not null,"
            + NOMBRE_JUEGO + " text not null);";


    //Crear tabla con llave primaria de tipo entero
    //Valores correspondientes a la tabla SALIDAS MEMORIA
    public static final String CREATE_TABLE_SALIDAS_MEMORIA = "create table if not exists " + TABLE_SALIDAS_MEMORIA + " ("
            + CN_ID_SALIDA_MEMORIA + " integer primary key autoincrement,"
            + CN_USUARIO_MEMORIA + " integer,"
            + CN_FECHA_MEMORIAS + " text not null,"
            + CN_STATUS_MEMORIAS + " integer);";

    //Crear tabla con llave primaria de tipo entero
    //Valores correspondientes a la tabla PIEZAS MEMORIA
    public static final String CREATE_TABLE_PIEZAS_MEMORIAS = "create table if not exists " + TABLE_DESCRIPCION_SALIDAS_MEMORIA + " ("
            + CN_ID_PIEZA_SALIDA_MEMORIA + " integer primary key autoincrement,"
            + CN_FK_SALIDA_MEMORIA + " integer,"
            + CN_FOLIO_MEMORIA + " integer,"
            + CN_CANTIDAD_MEMORIA + " integer,"
            + CN_OS + " integer,"
            + CN_STATUS_PIEZA_MEMORIA + " integer,"
            + CN_TIPO_MEMORIA + " text not null);";


    //Crear tabla con llave primaria de tipo entero--------------------------------------------------------------
    //Valores correspondientes a la tabla REFACCIONES
    public static final String CREATE_TABLE_REFACCIONES = "create table if not exists " + TABLE_REFACCIONES + " ("
            + CN_ID + " integer primary key autoincrement,"
            + CN_ID_REFACCIONES + " integer,"
            + CN_CLAVE + " integer,"
            + CN_NOMBRE + " text not null,"
            + CN_STATUS + " integer,"
            + CN_SUSTITUTO + " text,"
            + CN_NO_SERIE + " integer);";

    //Crear tabla con llave primaria de tipo entero
    //Valores correspondientes a la tabla SALA
    public static final String CREATE_TABLE_SALA = "create table if not exists " + TABLE_SALA + " ("
            + CN_ID_SALA + " integer primary key autoincrement,"
            + CN_NOMBRE_SALA + " text not null);";

    //Crear tabla con llave primaria de tipo entero
    //Valores correspondientes a la tabla SALIDAS
    public static final String CREATE_TABLE_SALIDAS = "create table if not exists " + TABLE_SALIDAS + " ("
            + CN_ID_SALIDA + " integer primary key autoincrement,"
            + CN_FK_SALA + " integer,"
            + CN_NO_SALIDA + " integer,"
            + CN_USUARIO + " integer,"
            + CN_FECHA_SALIDA + " text,"
            + CN_STATUS_SALIDA + " text,"
            + CN_FOLIO + " text,"
            + CN_OFFICE_ID + " integer,"
            + CN_PEDIDO + " text,"
            + CN_NO_GUIA + " text,"
            + CN_TIPO + " text,"
            + CN_TECNICO_ID_BAJA + " text);";

    //Crear tabla con llave primaria de tipo entero
    //Valores correspondientes a la tabla DESCRIPCION SALIDAS
    public static final String CREATE_TABLE_DESCRIPCION_SALIDAS = "create table if not exists " + TABLE_DESCRIPCION_SALIDA + " ("
            + CN_ID_DESCRIPCION_SALIDA + " integer primary key autoincrement,"
            + CN_FK_SALIDA + " integer,"
            + CN_CODIGO + " text,"
            + CN_DESCRIPCION + " text,"
            + CN_CANTIDAD + " integer,"
            + CN_SERIE + " text,"
            + "estatus text ,"
            + CN_MAQUINA + "  text);";

    //Crear tabla con llave primaria de tipo entero
    //Valores correspondientes a la tabla TABLA COMENTARIOS OS
    public static final String CREATE_TABLE_COMENTARIOS_OS = "create table if not exists " + TABLE_COMENTARIOS_OS + " ("
            + CN_ID_COMENTARIOS_OS + " integer primary key autoincrement,"
            + CN_OS_ID + " text,"
            + CN_COMENTARIOS_OS + " text,"
            + CN_FECHA_OS + " text,"
            + CN_ESTATUS_ENTREGA + " text,"
            + CN_NOMBRE_USUARIO_OS + " text not null);";

    //Crear tabla con llave primaria de tipo entero
    //Valores correspondientes a la tabla ESTATUS TECNICO
    public static final String CREATE_TABLE_ESTATUS_TECNICO = "create table if not exists " + TABLE_ESTATUS_TEC + " ("
            + CN_ID_ESTATUS_TEC + " integer primary key autoincrement,"
            + CN_ESTATUS_TEC + " text,"
            + TECNICO_ID + " text,"
            + CN_REGION_ID + " text,"
            + CN_EST_EN_SERV + " text,"
            + CN_OFFICE_ID_ESTATUS + " text,"
            + CN_PASSWORD + " text,"
            + CN_USER + " text,"
            + CN_NOMBRE_USER + " text not null,"
            + CN_CONTADOR_NOTIF + " integer);";

    //Eliminar esta tabla en alguna version posterior, se deja de ocupar en la version 2.1.5
    public static final String CREATE_TABLE_CONTROL_TABLAS = "create table if not exists ccontrol_tablas (" +
            "id_tabla integer primary key autoincrement, " +
            "nombre_tabla text, " +
            "num_registros integer" +
            ");";

    public static final String CREATE_TABLE_CONTROL_TOTAL = "create table if not exists control_total (" +
            "id integer primary key autoincrement, " +
            "nombre text, " +
            "control text" +
            ");";
    //se deja de ocupar en la version 2.1.7
    public static final String CREATE_TABLE_HORAS_SERVICIO = "create table if not exists horas_servicio (" +
            "id integer primary key autoincrement, " +
            "sala_inicia text, " +
            "hora_inicia text" +
            ");";

    public static final String CREATE_TABLE_ESTATUS = "create table if not exists cestatus (" +
            "id integer primary key, " +
            "tarea_referente text, " +
            "nombre text" +
            ");";

    public static final String CREATE_TABLE_REGION = "create table if not exists cregion (" +
            "id integer primary key, " +
            "nombre text" +
            ");";

    public static final String CREATE_TABLE_VARIABLES_GLOBALES = "create table if not exists variable_global (" +
            "id integer primary key autoincrement, " +
            "nombre text, " +
            "valor text" +
            ");";

    public BDmanager(Context context) {
        BDhelper helper = new BDhelper(context);
        db = helper.getWritableDatabase();

    }






    //Metodo para generar valores correspondientes a las tabla FALLAS
    public ContentValues generarContentValuesFALLAS(String idfalla, String nombreFalla) {
        ContentValues valores = new ContentValues();
        valores.put(ID_FALLA, idfalla);
        valores.put(NOMBRE_FALLA, nombreFalla);
        return valores;
    }

    //Metodo para generar valores correspondientes a las tabla SUBFALLAS
    public ContentValues generarContentValuesSubFALLAS(String idSubfalla, String nombreSubFalla,String idSubFallaFalla) {
        ContentValues valores = new ContentValues();
        valores.put(ID_SUBFALLA, idSubfalla);
        valores.put(NOMBRE_SUBFALLA, nombreSubFalla);
        valores.put(ID_SUBFALLAFALLA, idSubFallaFalla);
        return valores;
    }

    //Metodo para generar valores correspondientes a las tabla SUBFALLAS
    public ContentValues generarContentValuesSalaJuegoLic(String idMaquina, String idSala,String licencia,String idJuego,String nombreJuego) {
        ContentValues valores = new ContentValues();
        valores.put(ID_MAQUINA, idMaquina);
        valores.put(ID_SALA,idSala );
        valores.put(LICENCIA, licencia);
        valores.put(ID_JUEGO,idJuego );
        valores.put(NOMBRE_JUEGO,nombreJuego);
        return valores;
    }





    //Metodo para generar valores correspondientes a las tabla SALIDAS MEMORIA
    public ContentValues generarContentValuesEstatusTecnico(String estatusTec, String tecnicoid, String regionid,
                                                            String estEnServ, String officeid, String password, String usuario, String nombreUser, int contador) {
        ContentValues valores = new ContentValues();
        valores.put(CN_ESTATUS_TEC, estatusTec);
        valores.put(TECNICO_ID, tecnicoid);
        valores.put(CN_REGION_ID, regionid);
        valores.put(CN_EST_EN_SERV, estEnServ);
        valores.put(CN_OFFICE_ID_ESTATUS, officeid);
        valores.put(CN_PASSWORD, password);
        valores.put(CN_USER, usuario);
        valores.put(CN_NOMBRE_USER, nombreUser);
        valores.put(CN_CONTADOR_NOTIF, contador);
        return valores;
    }

    //Metodo para generar valores correspondientes a las tabla SALIDAS MEMORIA
    public ContentValues generarContentValuesSalidasMemoria(int usuario, String fecha, int status) {
        ContentValues valores = new ContentValues();
        valores.put(CN_USUARIO_MEMORIA, usuario);
        valores.put(CN_FECHA_MEMORIAS, fecha);
        valores.put(CN_STATUS_MEMORIAS, status);

        return valores;
    }

    //Metodo para generar valores correspondientes a las tabla PIEZAS SALIDA MEMORIA
    public ContentValues generarContentValuesPiezasSalidaMemoria(int idSalidaMemoria, int folio, int cantidad, int OS, int status, String tipo) {
        ContentValues valores = new ContentValues();
        valores.put(CN_FK_SALIDA_MEMORIA, idSalidaMemoria);
        valores.put(CN_FOLIO_MEMORIA, folio);
        valores.put(CN_CANTIDAD_MEMORIA, cantidad);
        valores.put(CN_OS, OS);
        valores.put(CN_STATUS_PIEZA_MEMORIA, status);
        valores.put(CN_TIPO_MEMORIA, tipo);

        return valores;
    }

    //Metodo para generar valores correspondientes a las tabla REFACCIONES
    public ContentValues generarContentValuesRefacciones(int idRef, int clave, String nombre, int piezasEx, String sustituto, String noSerie) {
        ContentValues valores = new ContentValues();
        valores.put(CN_ID_REFACCIONES, idRef);
        valores.put(CN_CLAVE, clave);
        valores.put(CN_NOMBRE, nombre);
        valores.put(CN_STATUS, piezasEx);
        valores.put(CN_SUSTITUTO, sustituto);
        valores.put(CN_NO_SERIE, noSerie);

        return valores;
    }

    //Metodo para generar valores correspondientes a las tabla SALA
    public ContentValues generarContentValuesSala(String nombre) {
        ContentValues valores = new ContentValues();
        valores.put(CN_NOMBRE_SALA, nombre);
        return valores;
    }

    //Metodo para generar valores correspondientes a las tabla SALIDAS
    public ContentValues generarContentValuesSalidas(int fksala, int noSalida, int usuario, String fecha,
                                                     String status, String folio, int office, String pedido, String noGuia, String tipo,
                                                     String tecnicoidBaja) {
        ContentValues valores = new ContentValues();
        valores.put(CN_FK_SALA, fksala);
        valores.put(CN_NO_SALIDA, noSalida);
        valores.put(CN_USUARIO, usuario);
        valores.put(CN_FECHA_SALIDA, fecha);
        valores.put(CN_STATUS_SALIDA, status);
        valores.put(CN_FOLIO, folio);
        valores.put(CN_OFFICE_ID, office);
        valores.put(CN_PEDIDO, pedido);
        valores.put(CN_NO_GUIA, noGuia);
        valores.put(CN_TIPO, tipo);
        valores.put(CN_TECNICO_ID_BAJA, tecnicoidBaja);
        return valores;
    }

    //Metodo para generar valores correspondientes a las tabla DEESCRIPCION SALIDAS
    public ContentValues generarContentValuesDescripcionSalidas(int fkSalida, String codigo, String descripcion, int cantidad, String noSerie, String estatus, String maquina) {
        ContentValues valores = new ContentValues();
        valores.put(CN_FK_SALIDA, fkSalida);
        valores.put(CN_CODIGO, codigo);
        valores.put(CN_DESCRIPCION, descripcion);
        valores.put(CN_CANTIDAD, cantidad);
        valores.put(CN_SERIE, noSerie);
        valores.put("estatus", estatus);
        valores.put(CN_MAQUINA, maquina);

        return valores;
    }

    //Metodo para generar valores correspondientes a las tabla DEESCRIPCION SALIDAS
    public ContentValues generarContentValuesComentariosOS(String osID, String comentarios,
                                                           String fecha, String estatusEntrega, String nombreUsuario) {
        ContentValues valores = new ContentValues();
        valores.put(CN_OS_ID, osID);
        valores.put(CN_COMENTARIOS_OS, comentarios);
        valores.put(CN_FECHA_OS, fecha);
        valores.put(CN_ESTATUS_ENTREGA, estatusEntrega);
        valores.put(CN_NOMBRE_USUARIO_OS, nombreUsuario);

        return valores;
    }






    //Inserta en una fila de la tabla los campos solicitados
    public void insertarFalla(String idfalla,String nombreFalla) {
        db.insert(TABLE_FALLAS, null, generarContentValuesFALLAS(idfalla,nombreFalla));
    }

    public void insertarSubFalla(String idSubfalla,String nombreSubFalla,String idFalla) {
        db.insert(TABLE_SUBFALLAS, null, generarContentValuesSubFALLAS(idSubfalla,nombreSubFalla,idFalla));
    }

    public void insertaSalaJuegoLic(String idMaquina,String idSala,String licencia,String idJuego,String nombreJuego ) {
        db.insert(CREATE_TABLE_SALA_JUEGO_LIC, null, generarContentValuesSalaJuegoLic(idMaquina,idSala,licencia,idJuego,nombreJuego));
    }


    //Inserta en una fila de la tabla los campos solicitados


    //Inserta en una fila de la tabla los campos solicitados
    public void insertarSalidaMemoria(int usuario, String fecha, int status) {
        db.insert(TABLE_SALIDAS_MEMORIA, null, generarContentValuesSalidasMemoria(usuario, fecha, status));
    }

    //Inserta en una fila de la tabla los campos solicitados
    public void insertarPiezaSalidaMemoria(int idSalidaMemoria, int folio, int cantidad, int OS, int status, String tipo) {
        db.insert(TABLE_DESCRIPCION_SALIDAS_MEMORIA, null, generarContentValuesPiezasSalidaMemoria(idSalidaMemoria, folio, cantidad, OS, status, tipo));
    }

    //Inserta en una fila de la tabla los campos solicitados
    public void insertarRefaccion(int idRef, int clave, String nombre, int piezasEx, String sustituto, String noSerie) {
        db.insert(TABLE_REFACCIONES, null, generarContentValuesRefacciones(idRef, clave, nombre, piezasEx, sustituto, noSerie));
    }

    //Inserta en una fila de la tabla los campos solicitados
    public void insertarSala(String nombreSala) {
        db.insert(TABLE_SALA, null, generarContentValuesSala(nombreSala));
    }

    //Inserta en una fila de la tabla los campos solicitados
    public int insertarSalida(int fksala, int noSalida, int usuario, String fecha, String status,
                               String folio, int office, String pedido, String noGuia, String tipo, String tecnicoidBaja) {
       return (int) db.insert(TABLE_SALIDAS, null, generarContentValuesSalidas(fksala, noSalida, usuario,
                fecha, status, folio, office, pedido, noGuia, tipo, tecnicoidBaja));
    }

    //Inserta en una fila de la tabla los campos solicitados
    public int insertarDescripcionSalida(int fkSalida, String codigo, String descripcion, int cantidad, String noSerie, String estatus, String maquina) {
        return (int)db.insert(TABLE_DESCRIPCION_SALIDA, null, generarContentValuesDescripcionSalidas(fkSalida, codigo, descripcion, cantidad, noSerie, estatus, maquina));
    }

    //Inserta en una fila de la tabla los campos solicitados
    public void insertarComentarioOS(String osID, String comentarios,
                                     String fecha, String estatusEntrega, String nombreUsuario) {
        db.insert(TABLE_COMENTARIOS_OS, null, generarContentValuesComentariosOS(osID, comentarios, fecha, estatusEntrega, nombreUsuario));
    }









    public Cursor cargarCursorFallas() {
        String[] columnas = new String[]{ID,ID_FALLA, NOMBRE_FALLA};
        return db.query(TABLE_FALLAS, columnas, null, null, null, null, null);
    }

    public Cursor cargarCursorSubFallas() {
        String[] columnas = new String[]{ID,ID_SUBFALLA, NOMBRE_SUBFALLA,ID_SUBFALLAFALLA};
        return db.query(TABLE_SUBFALLAS, columnas, null, null, null, null, null);
    }

    public Cursor cargarCursorSalaJuegoLic() {
        String[] columnas = new String[]{ID,ID_MAQUINA, CN_NOMBRE_SALA,LICENCIA,ID_JUEGO,NOMBRE_JUEGO};
        return db.query(TABLE_SALA_JUEGO_LIC, columnas, null, null, null, null, null);
    }




    //Carga el Cursor de la tabla seleccionada
    public Cursor cargarCursorEstatusTec() {
        String[] columnas = new String[]{CN_ID_ESTATUS_TEC, CN_ESTATUS_TEC, TECNICO_ID, CN_REGION_ID, CN_EST_EN_SERV,
                CN_OFFICE_ID_ESTATUS, CN_PASSWORD, CN_USER, CN_NOMBRE_USER, CN_CONTADOR_NOTIF};
        return db.query(TABLE_ESTATUS_TEC, columnas, null, null, null, null, null);
    }

    //Carga el Cursor de la tabla seleccionada
    public Cursor cargarCursorSalidasMemoria() {
        String[] columnas = new String[]{CN_ID_SALIDA_MEMORIA, CN_USUARIO_MEMORIA, CN_FECHA_MEMORIAS, CN_STATUS_MEMORIAS};
        return db.query(TABLE_SALIDAS_MEMORIA, columnas, null, null, null, null, null);
    }

    //Carga el Cursor de la tabla seleccionada
    public Cursor cargarCursorPiezasSalidasMemoria() {
        String[] columnas = new String[]{CN_ID_PIEZA_SALIDA_MEMORIA, CN_FK_SALIDA_MEMORIA, CN_FOLIO_MEMORIA, CN_CANTIDAD_MEMORIA, CN_OS, CN_STATUS_PIEZA_MEMORIA, CN_TIPO_MEMORIA};
        return db.query(TABLE_DESCRIPCION_SALIDAS_MEMORIA, columnas, null, null, null, null, null);
    }

    //Carga el Cursor de la tabla seleccionada
    public Cursor cargarCursorSala() {
        String[] columnas = new String[]{CN_ID_SALA, CN_NOMBRE_SALA};
        return db.query(TABLE_SALA, columnas, null, null, null, null, null);
    }

    //Carga el Cursor de la tabla seleccionada
    /**
     * se agrego el orderby en desc
     * */
    public Cursor cargarCursorSalidas() {
        String[] columnas = new String[]{CN_ID_SALIDA, CN_FK_SALA, CN_NO_SALIDA, CN_USUARIO, CN_FECHA_SALIDA,
                CN_STATUS_SALIDA, CN_FOLIO, CN_OFFICE_ID, CN_PEDIDO, CN_NO_GUIA, CN_TIPO, CN_TECNICO_ID_BAJA};
        return db.query(TABLE_SALIDAS, columnas, null, null, null, null, " "+CN_ID_SALIDA+" DESC" );
    }

    //Carga el Cursor de la tabla seleccionada
    public Cursor cargarCursorDescripcionSalida() {
        String[] columnas = new String[]{CN_ID_DESCRIPCION_SALIDA, CN_FK_SALIDA, CN_CODIGO, CN_DESCRIPCION, CN_CANTIDAD, CN_SERIE, "estatus"};
        return db.query(TABLE_DESCRIPCION_SALIDA, columnas, null, null, null, null, null);
    }

    //Carga el Cursor de la tabla seleccionada
    public Cursor cargarCursorRefacciones() {
        String[] columnas = new String[]{CN_ID, CN_ID_REFACCIONES, CN_CLAVE, CN_NOMBRE, CN_STATUS, CN_SUSTITUTO, CN_NO_SERIE};
        return db.query(TABLE_REFACCIONES, columnas, null, null, null, null, null);
    }













    //Carga el cursor para una busqueda dee la columna seleccionada
    public Cursor buscarClave(int clave1) {
        String clave = Integer.toString(clave1);
        String[] columnas = new String[]{CN_ID, CN_ID_REFACCIONES, CN_CLAVE, CN_NOMBRE, CN_STATUS, CN_SUSTITUTO, CN_NO_SERIE};
        return db.query(TABLE_REFACCIONES, columnas, CN_CLAVE + "=?", new String[]{clave}, null, null, null);
    }

    public Cursor buscarId(int id) {
        String ID = Integer.toString(id);
        String[] columnas = new String[]{CN_ID, CN_ID_REFACCIONES, CN_CLAVE, CN_NOMBRE, CN_STATUS, CN_SUSTITUTO, CN_NO_SERIE};
        return db.query(TABLE_REFACCIONES, columnas, CN_ID_REFACCIONES + "=?", new String[]{ID}, null, null, null);
    }


    public Cursor buscarSustituto(int sustituto1) {
        String sustituto = Integer.toString(sustituto1);
        String[] columnas = new String[]{CN_CLAVE, CN_NOMBRE, CN_STATUS, CN_SUSTITUTO};
        return db.query(TABLE_REFACCIONES, columnas, CN_SUSTITUTO + "=?", new String[]{sustituto}, null, null, null);
    }

    public Cursor buscarSala(String nombreSala) {
        String[] columnas = new String[]{CN_ID_SALA, CN_NOMBRE_SALA};
        return db.query(TABLE_SALA, columnas, CN_NOMBRE_SALA + "=?", new String[]{nombreSala}, null, null, null);
    }

    public Cursor buscarSalaPorId(int IdSala) {
        String Id = Integer.toString(IdSala);
        String[] columnas = new String[]{CN_ID_SALA, CN_NOMBRE_SALA};
        return db.query(TABLE_SALA, columnas, CN_ID_SALA + "=?", new String[]{Id}, null, null, null);
    }

    public Cursor buscarSalidaMemoriaID(int ID) {
        String IDString = Integer.toString(ID);
        String[] columnas = new String[]{CN_ID_SALIDA_MEMORIA, CN_USUARIO_MEMORIA, CN_FECHA_MEMORIAS, CN_STATUS_MEMORIAS};
        return db.query(TABLE_SALIDAS_MEMORIA, columnas, CN_ID_SALIDA_MEMORIA + "=?", new String[]{IDString}, null, null, null);
    }

    public Cursor buscarPiezaSalidaMemoriaID(int ID) {
        String IDString = Integer.toString(ID);
        String[] columnas = new String[]{CN_ID_PIEZA_SALIDA_MEMORIA, CN_FK_SALIDA_MEMORIA, CN_FOLIO_MEMORIA, CN_CANTIDAD_MEMORIA, CN_OS, CN_STATUS_PIEZA_MEMORIA, CN_TIPO_MEMORIA};
        return db.query(TABLE_DESCRIPCION_SALIDAS_MEMORIA, columnas, CN_FK_SALIDA_MEMORIA + "=?", new String[]{IDString}, null, null, null);
    }


    public Cursor buscarSalidas(int ID) {
        String IDString = Integer.toString(ID);
        String[] columnas = new String[]{CN_ID_SALIDA, CN_FK_SALA, CN_NO_SALIDA, CN_USUARIO, CN_FECHA_SALIDA,
                CN_STATUS_SALIDA, CN_FOLIO, CN_OFFICE_ID, CN_PEDIDO, CN_NO_GUIA, CN_TIPO, CN_TECNICO_ID_BAJA};
        return db.query(TABLE_SALIDAS, columnas, CN_FK_SALA + "=?", new String[]{IDString}, null, null, null);
    }

    public Cursor buscarSalidasPorId(int ID) {
        String IDString = Integer.toString(ID);
        String[] columnas = new String[]{CN_ID_SALIDA, CN_FK_SALA, CN_NO_SALIDA, CN_USUARIO, CN_FECHA_SALIDA,
                CN_STATUS_SALIDA, CN_FOLIO, CN_OFFICE_ID, CN_PEDIDO, CN_NO_GUIA, CN_TIPO, CN_TECNICO_ID_BAJA};
        return db.query(TABLE_SALIDAS, columnas, CN_NO_SALIDA + "=?", new String[]{IDString}, null, null, null);
    }

    public Cursor buscarSalidasSeleccionadas(int numeroDeSalida) {
        String numeroSalidaString = Integer.toString(numeroDeSalida);
        String[] columnas = new String[]{CN_ID_SALIDA, CN_FK_SALA, CN_NO_SALIDA, CN_USUARIO, CN_FECHA_SALIDA,
                CN_STATUS_SALIDA, CN_FOLIO, CN_OFFICE_ID, CN_PEDIDO, CN_NO_GUIA, CN_TIPO, CN_TECNICO_ID_BAJA};
        return db.query(TABLE_SALIDAS, columnas, CN_NO_SALIDA + "=?", new String[]{numeroSalidaString}, null, null, null);
    }

    public Cursor buscarReportesAsociados(int claveReporte) {
        String claveReporteString = Integer.toString(claveReporte);
        String[] columnas = new String[]{CN_ID_DESCRIPCION_SALIDA, CN_FK_SALIDA, CN_CODIGO, CN_DESCRIPCION, CN_CANTIDAD, CN_SERIE, "estatus"};
        return db.query(TABLE_DESCRIPCION_SALIDA, columnas, CN_FK_SALIDA + "=?", new String[]{claveReporteString}, null, null, null);
    }

    public Cursor buscarRegistroPieza(int idRegistroPieza) {
        String idRegistroPiezaString = Integer.toString(idRegistroPieza);
        String[] columnas = new String[]{CN_ID_DESCRIPCION_SALIDA, CN_FK_SALIDA, CN_CODIGO, CN_DESCRIPCION, CN_CANTIDAD, CN_SERIE, "estatus"};
        return db.query(TABLE_DESCRIPCION_SALIDA, columnas, CN_ID_DESCRIPCION_SALIDA + "=?", new String[]{idRegistroPiezaString}, null, null, null);
    }

    //Carga el Cursor de la tabla seleccionada
    public Cursor buscarComentariosPorOSidPendientes(String osID) {
        String[] columnas = new String[]{CN_ID_COMENTARIOS_OS, CN_OS_ID, CN_COMENTARIOS_OS,
                CN_FECHA_OS, CN_ESTATUS_ENTREGA, CN_NOMBRE_USUARIO_OS};
        return db.query(TABLE_COMENTARIOS_OS, columnas, CN_OS_ID + "=? and " + CN_ESTATUS_ENTREGA + "=0", new String[]{osID}, null, null, null);
    }

    public Cursor buscarComentariosPorOSid(String osID) {
        String[] columnas = new String[]{CN_ID_COMENTARIOS_OS, CN_OS_ID, CN_COMENTARIOS_OS,
                CN_FECHA_OS, CN_ESTATUS_ENTREGA, CN_NOMBRE_USUARIO_OS};
        return db.query(TABLE_COMENTARIOS_OS, columnas, CN_OS_ID + "=?", new String[]{osID}, null, null, null);
    }

    public Cursor buscarComentarioPorIdRegistroOS(int idRegistroOSComent) {
        String idRegistro = Integer.toString(idRegistroOSComent);
        String[] columnas = new String[]{CN_ID_COMENTARIOS_OS, CN_OS_ID, CN_COMENTARIOS_OS,
                CN_FECHA_OS, CN_ESTATUS_ENTREGA, CN_NOMBRE_USUARIO_OS};
        return db.query(TABLE_COMENTARIOS_OS, columnas, CN_ID_COMENTARIOS_OS + "=?", new String[]{idRegistro}, null, null, null);
    }

    public void EliminarTabla(String tabla) {
        db.delete(tabla, null, null);
    }

    public int eliminarRegistroCSalida(int idSalida) {
        return db.delete("csalida", "salidaid" + " = " + idSalida, null);
    }

    public int eliminarRegistrosReportes(int idReporteElimar) {
        return db.delete(TABLE_DESCRIPCION_SALIDA, CN_ID_DESCRIPCION_SALIDA + " = " + idReporteElimar, null);
    }

    public int eliminarSalida(int idSalida) {
        return db.delete(TABLE_SALIDAS, CN_ID_SALIDA + " = " + idSalida, null);
    }

    public int eliminarSalidaMemorias(int idSalida) {
        return db.delete(TABLE_SALIDAS_MEMORIA, CN_ID_SALIDA_MEMORIA + " = " + idSalida, null);
    }

    public int actualizarRegistroReporte(String folio, String hora, int id) {
        ContentValues valores = new ContentValues();
        valores.put(CN_FOLIO, folio);
        valores.put(CN_FECHA_SALIDA, hora);
        return db.update(TABLE_SALIDAS, valores, CN_NO_SALIDA + " = " + id, null);

    }

    public int actualizarRegistroPieza(int idPieza, int codigoNew, String descripcionNew, int cantidadNew, String serieNew, String estatusNew) {
        ContentValues valores = new ContentValues();
        valores.put(CN_CODIGO, codigoNew);
        valores.put(CN_DESCRIPCION, descripcionNew);
        valores.put(CN_CANTIDAD, cantidadNew);
        valores.put(CN_SERIE, serieNew);
        valores.put("estatus", estatusNew);
        return db.update(TABLE_DESCRIPCION_SALIDA, valores, CN_ID_DESCRIPCION_SALIDA + " = " + idPieza, null);

    }

    public void actualizarStatus(String status, int id) {
        ContentValues valores = new ContentValues();
        valores.put(CN_STATUS_SALIDA, status);
        db.update(TABLE_SALIDAS, valores, "noSalida=" + id, null);
    }

    public void actualizarPedidoYGuia(String pedido, String noGuia, int id) {
        ContentValues valores = new ContentValues();
        valores.put(CN_PEDIDO, pedido);
        valores.put(CN_NO_GUIA, noGuia);
        db.update(TABLE_SALIDAS, valores, "noSalida=" + id, null);
    }

    public void actualizarSalidaMemoria(String fecha, int status, int id) {
        ContentValues valores = new ContentValues();
        valores.put(CN_FECHA_MEMORIAS, fecha);
        valores.put(CN_STATUS_MEMORIAS, status);
        db.update(TABLE_SALIDAS_MEMORIA, valores, "_id=" + id, null);
    }

    public void actualizarEstatusDeEntrega(String statusEntrega, int id) {
        ContentValues valores = new ContentValues();
        valores.put(CN_ESTATUS_ENTREGA, statusEntrega);
        db.update(TABLE_COMENTARIOS_OS, valores, "_id=" + id, null);
    }

    public void actualizarEstatusTec(String estatus, String tecnicoid, String regionid, String estEnServ, String pass, String user,
                                     String nombreUsuario) {
        ContentValues valores = new ContentValues();
        valores.put(CN_ESTATUS_TEC, estatus);
        valores.put(TECNICO_ID, tecnicoid);
        valores.put(CN_REGION_ID, regionid);
        valores.put(CN_EST_EN_SERV, estEnServ);
        valores.put(CN_PASSWORD, pass);
        valores.put(CN_USER, user);
        valores.put(CN_NOMBRE_USER, nombreUsuario);

        db.update(TABLE_ESTATUS_TEC, valores, "_id=1", null);

    }

    public void actualizarOfficeid(String officeid) {
        ContentValues valores = new ContentValues();
        valores.put(CN_OFFICE_ID, officeid);
        db.update(TABLE_ESTATUS_TEC, valores, "_id=1", null);
    }

    public void actualizarContadorNotif(int contador) {
        ContentValues valores = new ContentValues();
        valores.put(CN_CONTADOR_NOTIF, contador);
        db.update(TABLE_ESTATUS_TEC, valores, "_id=1", null);
    }

    public Cursor consulta(String consulta) {
        return db.rawQuery(consulta, null);
    }

    public String consulta(String consulta, String regresa) {
        Cursor c = db.rawQuery(consulta, null);
        if (c.moveToFirst()) {
            regresa = c.getString(0);
        }
        c.close();
        return regresa;
    }

    public int consulta(String consulta, int regresa) {
        Cursor c = db.rawQuery(consulta, null);
        if (c.moveToFirst()) {
            regresa = c.getInt(0);
        }
        c.close();
        return regresa;
    }

    public void actualiza(String consulta) {
        db.execSQL(consulta);
    }

    public void borrarRegion() {
        db.execSQL("delete from  cregion");
    }

  /*  public void borrarSalas() {
        db.execSQL("delete from  " + TABLE_SALA);

    }


*/

    public void borarCsalida(int salidaid) {
        db.execSQL("delete from  csalida where salidaid = " + salidaid);

    }

    public void borrarVariableGlobal() {
        db.execSQL("delete from  variable_global");

    }
}
