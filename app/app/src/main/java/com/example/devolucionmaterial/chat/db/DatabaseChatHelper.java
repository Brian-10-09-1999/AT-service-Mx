package com.example.devolucionmaterial.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrador on 22/12/2016.
 */

public class DatabaseChatHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_CONTACTS = "CHAT_CONTACTOS";
    public static final String TABLE_CHAT = "CHATS";
    public static final String TABLE_CHAT_DETAILS = "CHATS_DETAILS";
    public static final String TABLE_CHAT_GRUPO_MEMBERS = "CHATS_MEMBERS";

    // Table contactos
    public static final String _ID = "id";
    public static final String ALIAS = "alias";
    public static final String NOMBRE = "nombre";
    public static final String ID_ALIAS = "id_alias";
    public static final String EMAIL = "email";
    public static final String TOKEN = "token";

    //tabla chat
    public static final String ID_CHAT = "id_chat";
    public static final String MODEL_CHAT = "model_chat";
    public static final String NAME_CHAT = "name";
    public static final String DESC_CHAT = "description";
    public static final String IMAGE_CHAT = "image";
    public static final String USERS_CHAT = "users";
    public static final String CHAT_STATUS = "status";
    public static final String CHAT_TIME_STAMP = "timeStamp";
    public static final String TYPE_CHAT = "type";

    //TABLE MIEBROS DEL CHAT
    public static final String ID_CHAT_MEMBER = "id_chat";
    public static final String ID_ALIAS_CHAT_MEMBER = "id_alias";
    public static final String NOMBRE_CHAT_MEMBER = "nombre";
    public static final String ALIAS_CHAT_MEMBER = "alias";

    //TABLE CHAT DETAILS
    public static final String ID_CHAT_DETAIL = "id_chat";
    public static final String MODEL_CHAT_DETAIAIL = "model_chat";
    public static final String TIME_STAMP_DETAIAIL = "timeStamp";
    public static final String STATUS_DETAIAIL = "status";


    //CHAT MODELS STRING
    public static final String message = "message";
    public static final String timeStamp = "timeStamp";
    public static final String userModel = "userModel";
    public static final String user = "user";
    public static final String file = "file";
    public static final String type = "type";
    public static final String id_emisor = "id_emisor";
    public static final String id_resceptor = "id_receptor";
    public static final String typeChat = "typeChat";

    //TYPE CHAT
    public static final int TYPE_CHAT_INDIVIDUAL = 1;
    public static final int TYPE_CHAT_GROUP = 2;

    //Estatus chat
    public static final int ChatSending = 0;
    public static final int ChatSent = 1;
    public static final int ChatRecibe = 4;

    //estatus list chat
    public static final int chatRead = 1;
    public static final int chatNoRead = 0;

    //manager chat grupo




    // Database Information
    static final String DB_NAME = "CHAT_ODN.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table "
            + TABLE_CONTACTS + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ALIAS + " TEXT, "
            + NOMBRE + " TEXT,"
            + ID_ALIAS + " INTEGER,"
            + EMAIL + " TEXT,"
            + TOKEN + " TEXT);";


    private static final String CREATE_CHAT_DETAILS_TABLE = "create table "
            + TABLE_CHAT_DETAILS + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID_CHAT_DETAIL + " INTEGER ,"
            + MODEL_CHAT_DETAIAIL + " TEXT ,"
            + TIME_STAMP_DETAIAIL + " INTEGER ,"
            + STATUS_DETAIAIL + " INTEGER);";

    private static final String CREATE_CHAT_TABLE = "create table " +
            TABLE_CHAT + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID_CHAT + " INTEGER,"
            + NAME_CHAT + " TEXT,"
            + DESC_CHAT + " TEXT,"
            + IMAGE_CHAT + " TEXT,"
            + USERS_CHAT + " TEXT,"
            + CHAT_STATUS + " INTEGER,"
            + CHAT_TIME_STAMP + " INTEGER,"
            + TYPE_CHAT + " INTEGER);";


    private static final String CREATE_CHAT_MEMBERS_TABLE = "create table " +
            TABLE_CHAT_GRUPO_MEMBERS + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID_CHAT_MEMBER + " INTEGER,"
            + ID_ALIAS_CHAT_MEMBER + " INTEGER,"
            + NOMBRE_CHAT_MEMBER + " TEXT,"
            + ALIAS_CHAT_MEMBER + " TEXT);";


    public DatabaseChatHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_CHAT_TABLE);
        db.execSQL(CREATE_CHAT_DETAILS_TABLE);
        db.execSQL(CREATE_CHAT_MEMBERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_CHAT_MEMBERS_TABLE);
        onCreate(db);
    }

    public SQLiteDatabase getContextlist() {
        SQLiteDatabase context = this.getWritableDatabase();
        return context;
    }
}