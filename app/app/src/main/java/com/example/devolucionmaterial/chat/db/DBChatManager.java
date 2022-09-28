package com.example.devolucionmaterial.chat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.devolucionmaterial.chat.adapter.ChatAdapter;
import com.example.devolucionmaterial.chat.model.ChatList;
import com.example.devolucionmaterial.chat.model.ContactList;
import com.example.devolucionmaterial.chat.model.ModelChat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrador on 22/12/2016.
 */

public class DBChatManager {

    private DatabaseChatHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBChatManager(Context c) {
        context = c;
    }

    public DBChatManager open() throws SQLException {
        dbHelper = new DatabaseChatHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // TODO: 26/12/2016  crud de contactos chat
    public void insert(String alias, String nombre, String id_alias, String email, String token) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseChatHelper.ALIAS, alias);//1
        contentValue.put(DatabaseChatHelper.NOMBRE, nombre);//2
        contentValue.put(DatabaseChatHelper.ID_ALIAS, id_alias);//3
        contentValue.put(DatabaseChatHelper.EMAIL, email);//4
        contentValue.put(DatabaseChatHelper.TOKEN, token);//5
        database.insert(DatabaseChatHelper.TABLE_CONTACTS, null, contentValue);
    }

    // actualiza la inforomacion de los contectos
    public int update(long _id, String alias, String nombre, String id_alias, String email, String token) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseChatHelper.ALIAS, alias);
        contentValues.put(DatabaseChatHelper.NOMBRE, nombre);
        contentValues.put(DatabaseChatHelper.ID_ALIAS, id_alias);
        contentValues.put(DatabaseChatHelper.EMAIL, email);
        contentValues.put(DatabaseChatHelper.TOKEN, token);

        int i = database.update(DatabaseChatHelper.TABLE_CONTACTS, contentValues, DatabaseChatHelper._ID + " = " + _id, null);
        return i;
    }

    public Boolean deleteAllContacts() {
        database.execSQL("delete from  " + DatabaseChatHelper.TABLE_CONTACTS);
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CONTACTS + " ORDER BY " + DatabaseChatHelper.NOMBRE + " COLLATE NOCASE";
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ContactList> FavList = new ArrayList<ContactList>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new ContactList(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5)
                ));
            } while (cursor.moveToNext());
        }

        if (FavList.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    //obtines los contactos por orden alfabeitco
    public ArrayList<ContactList> getContactList() {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CONTACTS + " ORDER BY " + DatabaseChatHelper.NOMBRE + " COLLATE NOCASE";
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ContactList> FavList = new ArrayList<ContactList>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new ContactList(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5)
                ));

            } while (cursor.moveToNext());
        }
        return FavList;
    }

    // EL IDCHAT ES EL ID_ALIAS de los contactos
    public String getContactAlias(int id_chat) {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CONTACTS + " WHERE " + DatabaseChatHelper.ID_ALIAS + " = " + id_chat;
        SQLiteDatabase db = dbHelper.getContextlist();
        String alias = "";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                alias = c.getString(1);
                String nombre = c.getString(2);
            } while (c.moveToNext());
        }
        return alias;
    }

    // cuando es grupo //  EL IDCHAT ES EL ID_ALIAS de los contactos
    public String getContactNameChat(int id_chat) {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CHAT + " WHERE " + DatabaseChatHelper.ID_CHAT + "= " + id_chat;
        SQLiteDatabase db = dbHelper.getContextlist();
        String alias = "";
        String nombre = "";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                alias = c.getString(1);
                 nombre = c.getString(2);
            } while (c.moveToNext());
        }
        return nombre;
    }

    //obtines l ainformacion de contacto por su id_alias
    public ArrayList<ContactList> getInfoContact(long id_alias) {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CONTACTS + " WHERE " + DatabaseChatHelper.ID_ALIAS + " = " + id_alias;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ContactList> FavList = new ArrayList<ContactList>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new ContactList(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5)
                ));

            } while (cursor.moveToNext());
        }
        return FavList;
    }


    // TODO: 26/12/2016 crud de lista de chats
    public void insertChat(int id_chat, String name, String desc, int image, String users, int status, long time, int type) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseChatHelper.ID_CHAT, id_chat);//1
        contentValue.put(DatabaseChatHelper.NAME_CHAT, name);//2
        contentValue.put(DatabaseChatHelper.DESC_CHAT, desc);//3
        contentValue.put(DatabaseChatHelper.IMAGE_CHAT, image);//4
        contentValue.put(DatabaseChatHelper.USERS_CHAT, users);//5
        contentValue.put(DatabaseChatHelper.CHAT_STATUS, status);//6
        contentValue.put(DatabaseChatHelper.CHAT_TIME_STAMP, time);//7
        contentValue.put(DatabaseChatHelper.TYPE_CHAT, type);//8
        database.insert(DatabaseChatHelper.TABLE_CHAT, null, contentValue);
    }

    //obtienes la lista de chats
    public ArrayList<ChatList> getListChat() {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CHAT + " order by " + DatabaseChatHelper.CHAT_TIME_STAMP + " desc ";
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ChatList> FavList = new ArrayList<ChatList>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new ChatList(
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        Integer.valueOf(cursor.getString(4)),
                        cursor.getString(5),
                        cursor.getInt(6),
                        cursor.getLong(7),
                        cursor.getInt(8)
                ));
            } while (cursor.moveToNext());
        }
        return FavList;
    }

    //RECUPERA EL TIPO DE CHAT QUE SE VA ABRIR
    public ArrayList<ChatList> getTypeChat(int id_chat) {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CHAT + " WHERE " + DatabaseChatHelper.ID_CHAT + "= " + id_chat;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ChatList> FavList = new ArrayList<ChatList>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new ChatList(
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        Integer.valueOf(cursor.getString(4)),
                        cursor.getString(5),
                        cursor.getInt(6),
                        cursor.getInt(7),
                        cursor.getInt(8)
                ));
            } while (cursor.moveToNext());
        }
        return FavList;
    }

    // con esta consulta se revisa si ya hay un chat en la base par ano crear duplicidad
    public ArrayList<ChatList> getIdContact(long id_alias) {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CHAT +
                " WHERE " + DatabaseChatHelper.USERS_CHAT + "=" + id_alias+
                " and "+ DatabaseChatHelper.TYPE_CHAT +"= 1" ;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ChatList> FavList = new ArrayList<ChatList>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new ChatList(
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        Integer.valueOf(cursor.getString(4)),
                        cursor.getString(5),
                        cursor.getInt(6),
                        cursor.getInt(7),
                        cursor.getInt(8)
                ));
            } while (cursor.moveToNext());
        }
        return FavList;
    }
    //rercupera los miembros de los chats

    public ArrayList<ChatList> getUserChats(int id_chat) {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CHAT + " WHERE " + DatabaseChatHelper.ID_CHAT + "=" + id_chat;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ChatList> FavList = new ArrayList<ChatList>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new ChatList(
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        Integer.valueOf(cursor.getString(4)),
                        cursor.getString(5),
                        cursor.getInt(6),
                        cursor.getInt(7),
                        cursor.getInt(8)
                ));

            } while (cursor.moveToNext());
        }
        return FavList;
    }


    //metodo para actualizar el estus de la lista de chat msotardo si estan leidos o no y darle el tiempo en que llego
    public int updateStatusChatList(int id_chat, int status, long time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseChatHelper.CHAT_STATUS, status);
        contentValues.put(DatabaseChatHelper.CHAT_TIME_STAMP, time);
        int i = database.update(DatabaseChatHelper.TABLE_CHAT, contentValues, DatabaseChatHelper.ID_CHAT + " = " + id_chat, null);
        Log.e("int de regreso  de actulizacion ", String.valueOf(i));
        return i;
    }

    //metodo paracambiar el estatus de leido cuando entra al ala aactividad del chat
    public int updateStatusChatListChange(int id_chat, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseChatHelper.CHAT_STATUS, status);
        int i = database.update(DatabaseChatHelper.TABLE_CHAT, contentValues, DatabaseChatHelper.ID_CHAT + " = " + id_chat, null);
        Log.e("int de regreso  de actulizacion ", String.valueOf(i));
        return i;
    }


    public int getStatusChatList(int id_chat) {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CHAT + " WHERE " + DatabaseChatHelper.ID_CHAT + " = " + id_chat;
        SQLiteDatabase db = dbHelper.getContextlist();
        int status = 0;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                status = c.getInt(6);

            } while (c.moveToNext());
        }
        return status;
    }


    // TODO: 26/12/2016  crud de mensajes de chat TABLE_CHAT_DETAILS

    public void insertMessageChat(int id_chat, JSONObject modelJson, long time, int status) {
        String modelChat = String.valueOf(modelJson);
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseChatHelper.ID_CHAT_DETAIL, id_chat);//1
        contentValue.put(DatabaseChatHelper.MODEL_CHAT_DETAIAIL, modelChat);//2
        contentValue.put(DatabaseChatHelper.TIME_STAMP_DETAIAIL, time);//3
        contentValue.put(DatabaseChatHelper.STATUS_DETAIAIL, status);//4
        database.insert(DatabaseChatHelper.TABLE_CHAT_DETAILS, null, contentValue);
    }

    public int insertMessageRecibe(int id_chat, JSONObject modelJson, long time, int status) {
        String modelChat = String.valueOf(modelJson);
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseChatHelper.ID_CHAT_DETAIL, id_chat);//1
        contentValue.put(DatabaseChatHelper.MODEL_CHAT_DETAIAIL, modelChat);//2
        contentValue.put(DatabaseChatHelper.TIME_STAMP_DETAIAIL, time);//3
        contentValue.put(DatabaseChatHelper.STATUS_DETAIAIL, status);//4

        return (int) database.insert(DatabaseChatHelper.TABLE_CHAT_DETAILS, null, contentValue);
    }

    public void deleteChatDetails(int id_chat) {
        database.delete(DatabaseChatHelper.TABLE_CHAT_DETAILS, DatabaseChatHelper.ID_CHAT_DETAIL + "=" + id_chat, null);
    }
    //se borrar de las 3 tablas del chat cuando es elinado de el Grupo
    public void deleteChatComplete(long id_chat) {
        database.delete(DatabaseChatHelper.TABLE_CHAT_DETAILS, DatabaseChatHelper.ID_CHAT_DETAIL + "=" + id_chat, null);
        database.delete(DatabaseChatHelper.TABLE_CHAT, DatabaseChatHelper.ID_CHAT + "=" + id_chat, null);
        database.delete(DatabaseChatHelper.TABLE_CHAT_GRUPO_MEMBERS, DatabaseChatHelper.ID_CHAT_MEMBER + "=" + id_chat, null);
    }

    //aqui se consulta todos los chats guardados y se taren los ultimos 20 de acuerdo al tiempo en milisegundos
    public ArrayList<ModelChat> getChatsDetails(int id_chat) throws JSONException {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CHAT_DETAILS
                + "  WHERE " + DatabaseChatHelper.ID_CHAT_DETAIL + "="
                + id_chat + " order by " + DatabaseChatHelper.TIME_STAMP_DETAIAIL + " desc " + " LIMIT 20";
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ModelChat> modelChats = new ArrayList<ModelChat>();
        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonChat = new JSONObject(cursor.getString(2));
                if (jsonChat.getInt(DatabaseChatHelper.type) == ChatAdapter.LEFT_MSG) {
                    // TODO: 07/02/2017 mensajes recibidos
                    String mensaje = jsonChat.getString(DatabaseChatHelper.message);
                    String time = jsonChat.getString(DatabaseChatHelper.timeStamp);
                    int type = jsonChat.getInt(DatabaseChatHelper.type);
                    String userModel = jsonChat.getString(DatabaseChatHelper.userModel);
                    JSONObject jsonUserModel = new JSONObject(userModel);
                    String user = jsonUserModel.getString(DatabaseChatHelper.user);
                    modelChats.add(new ModelChat(
                            cursor.getLong(0),
                            mensaje,
                            time,
                            user,
                            type,
                            cursor.getInt(4)
                    ));

                } else if (jsonChat.getInt(DatabaseChatHelper.type) == ChatAdapter.RIGHT_MSG) {
                    // TODO: 07/02/2017 mensajes enviados
                    String mensaje = jsonChat.getString(DatabaseChatHelper.message);
                    String time = jsonChat.getString(DatabaseChatHelper.timeStamp);
                    int type = jsonChat.getInt(DatabaseChatHelper.type);
                    String userModel = jsonChat.getString(DatabaseChatHelper.userModel);
                    JSONObject jsonUserModel = new JSONObject(userModel);
                    String user = jsonUserModel.getString(DatabaseChatHelper.user);
                    modelChats.add(new ModelChat(
                            cursor.getLong(0),
                            mensaje,
                            time,
                            user,
                            type,
                            cursor.getInt(4)
                    ));
                } else if (jsonChat.getInt(DatabaseChatHelper.type) == ChatAdapter.RIGHT_MSG_IMG) {
                    // TODO: 07/02/2017 imagenes enviadas
                    String imagePath = jsonChat.getString(DatabaseChatHelper.message);
                    String time = jsonChat.getString(DatabaseChatHelper.timeStamp);
                    int type = jsonChat.getInt(DatabaseChatHelper.type);
                    String userModel = jsonChat.getString(DatabaseChatHelper.userModel);
                    JSONObject jsonUserModel = new JSONObject(userModel);
                    String user = jsonUserModel.getString(DatabaseChatHelper.user);
                    modelChats.add(new ModelChat(
                            cursor.getLong(0),
                            imagePath,
                            time,
                            user,
                            type,
                            cursor.getInt(4)
                    ));
                } else if (jsonChat.getInt(DatabaseChatHelper.type) == ChatAdapter.LEFT_MSG_IMG) {
                    // TODO: 07/02/2017 imagenes enviadas
                    String imagePath = jsonChat.getString(DatabaseChatHelper.message);
                    String time = jsonChat.getString(DatabaseChatHelper.timeStamp);
                    int type = jsonChat.getInt(DatabaseChatHelper.type);
                    String userModel = jsonChat.getString(DatabaseChatHelper.userModel);
                    JSONObject jsonUserModel = new JSONObject(userModel);
                    String user = jsonUserModel.getString(DatabaseChatHelper.user);
                    modelChats.add(new ModelChat(
                            cursor.getLong(0),
                            imagePath,
                            time,
                            user,
                            type,
                            cursor.getInt(4)
                    ));
                }

            } while (cursor.moveToNext());
        }
        return modelChats;
    }


    //este metodo se tiene que modificar junto con el metod getChatsDetails ya que esta consulta trae los items por medio del paginador
    public ArrayList<ModelChat> getChatsDetailsPage(int id_chat, long timeStamp) throws JSONException {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CHAT_DETAILS
                + "  WHERE " + DatabaseChatHelper.ID_CHAT_DETAIL + "="
                + id_chat + "  AND " + timeStamp + ">" + DatabaseChatHelper.TIME_STAMP_DETAIAIL + " " +
                " order by " + DatabaseChatHelper.TIME_STAMP_DETAIAIL + " desc " + " LIMIT 20";
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ModelChat> modelChats = new ArrayList<ModelChat>();
        if (cursor.moveToFirst()) {
            Log.e("id", cursor.getString(0));
            do {
                JSONObject jsonChat = new JSONObject(cursor.getString(2));
                if (jsonChat.getInt(DatabaseChatHelper.type) == ChatAdapter.LEFT_MSG) {
                    String mensaje = jsonChat.getString(DatabaseChatHelper.message);
                    String time = jsonChat.getString(DatabaseChatHelper.timeStamp);
                    int type = jsonChat.getInt(DatabaseChatHelper.type);
                    String userModel = jsonChat.getString(DatabaseChatHelper.userModel);
                    JSONObject jsonUserModel = new JSONObject(userModel);
                    String user = jsonUserModel.getString(DatabaseChatHelper.user);
                    modelChats.add(new ModelChat(cursor.getLong(0), mensaje, time, user, type, cursor.getInt(4)));

                } else if (jsonChat.getInt(DatabaseChatHelper.type) == ChatAdapter.RIGHT_MSG) {
                    String mensaje = jsonChat.getString(DatabaseChatHelper.message);
                    String time = jsonChat.getString(DatabaseChatHelper.timeStamp);
                    int type = jsonChat.getInt(DatabaseChatHelper.type);
                    String userModel = jsonChat.getString(DatabaseChatHelper.userModel);
                    JSONObject jsonUserModel = new JSONObject(userModel);
                    String user = jsonUserModel.getString(DatabaseChatHelper.user);
                    modelChats.add(new ModelChat(cursor.getLong(0), mensaje, time, user, type, cursor.getInt(4)));
                }
                // TODO: 07/03/2017  regresa el la lista de de los menajes con imagen
                else if (jsonChat.getInt(DatabaseChatHelper.type) == ChatAdapter.RIGHT_MSG_IMG) {
                    String mensaje = jsonChat.getString(DatabaseChatHelper.message);
                    String time = jsonChat.getString(DatabaseChatHelper.timeStamp);
                    int type = jsonChat.getInt(DatabaseChatHelper.type);
                    String userModel = jsonChat.getString(DatabaseChatHelper.userModel);
                    JSONObject jsonUserModel = new JSONObject(userModel);
                    String user = jsonUserModel.getString(DatabaseChatHelper.user);
                    modelChats.add(new ModelChat(cursor.getLong(0), mensaje, time, user, type, cursor.getInt(4)));
                }

            } while (cursor.moveToNext());
        }
        return modelChats;
    }

    // este metodo recupera el mensaje individual por chat
    public ArrayList<ModelChat> getChatsDetailsUnoXuno(long _id) throws JSONException {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CHAT_DETAILS
                + "  WHERE " + DatabaseChatHelper._ID + "=" + _id;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ModelChat> modelChats = new ArrayList<ModelChat>();
        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonChat = new JSONObject(cursor.getString(2));
                if (jsonChat.getInt(DatabaseChatHelper.type) == ChatAdapter.RIGHT_MSG) {
                    // TODO: 07/02/2017 mensajes enviados
                    String mensaje = jsonChat.getString(DatabaseChatHelper.message);
                    String time = jsonChat.getString(DatabaseChatHelper.timeStamp);
                    int type = jsonChat.getInt(DatabaseChatHelper.type);
                    String userModel = jsonChat.getString(DatabaseChatHelper.userModel);
                    JSONObject jsonUserModel = new JSONObject(userModel);
                    String user = jsonUserModel.getString(DatabaseChatHelper.user);
                    modelChats.add(new ModelChat(
                            cursor.getLong(0),
                            mensaje,
                            time,
                            user,
                            type,
                            cursor.getInt(4)
                    ));
                } else if (jsonChat.getInt(DatabaseChatHelper.type) == ChatAdapter.RIGHT_MSG_IMG) {
                    // TODO: 07/02/2017 imagenes enviadas
                    String imagePath = jsonChat.getString(DatabaseChatHelper.message);
                    String time = jsonChat.getString(DatabaseChatHelper.timeStamp);
                    int type = jsonChat.getInt(DatabaseChatHelper.type);
                    String userModel = jsonChat.getString(DatabaseChatHelper.userModel);
                    JSONObject jsonUserModel = new JSONObject(userModel);
                    String user = jsonUserModel.getString(DatabaseChatHelper.user);
                    modelChats.add(new ModelChat(
                            cursor.getLong(0),
                            imagePath,
                            time,
                            user,
                            type,
                            cursor.getInt(4)
                    ));
                }

            } while (cursor.moveToNext());
        }
        return modelChats;
    }

    //aqui se consulta el json por chat id
    public JSONObject getChatsDetailsOnly(long _id) {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CHAT_DETAILS
                + "  WHERE " + DatabaseChatHelper._ID + "=" + _id;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ModelChat> modelChats = new ArrayList<ModelChat>();
        JSONObject jsonChat = null;
        if (cursor.moveToFirst()) {
            do {
                try {
                    jsonChat = new JSONObject(cursor.getString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }
        return jsonChat;
    }


    //metodo para actualizar el estus del mensaje
    public boolean updateStatusChatDetails(long _id, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseChatHelper.STATUS_DETAIAIL, status);
        int i = database.update(DatabaseChatHelper.TABLE_CHAT_DETAILS, contentValues, DatabaseChatHelper._ID + " = " + _id, null);
        Log.e("int de regreso ", String.valueOf(i));
        return false;
    }


    // TODO: 23/01/2017  metodos para los miembros de grupo

    public void insertMembersGroup(int id_chat, int id_alias, String nombre, String alias) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseChatHelper.ID_CHAT_MEMBER, id_chat);//1
        contentValue.put(DatabaseChatHelper.ID_ALIAS_CHAT_MEMBER, id_alias);//2
        contentValue.put(DatabaseChatHelper.NOMBRE_CHAT_MEMBER, nombre);//3
        contentValue.put(DatabaseChatHelper.ALIAS_CHAT_MEMBER, alias);//4
        database.insert(DatabaseChatHelper.TABLE_CHAT_GRUPO_MEMBERS, null, contentValue);
    }

    //se consulta los miembros pertenecientes a chat respectivo
    public ArrayList<ContactList> getMembersChat(int id_chat) {
        String selectQuery = "SELECT  * FROM " + DatabaseChatHelper.TABLE_CHAT_GRUPO_MEMBERS + " WHERE " + DatabaseChatHelper.ID_CHAT_MEMBER + "=" + id_chat;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ContactList> FavList = new ArrayList<ContactList>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new ContactList(
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4)

                ));

            } while (cursor.moveToNext());
        }
        return FavList;
    }

    //borra un miebro del grupo del chat predeterminado
    public void deleteMemberId(long id_alias) {
        database.delete(DatabaseChatHelper.TABLE_CHAT_GRUPO_MEMBERS, DatabaseChatHelper.ID_ALIAS_CHAT_MEMBER + "=" + id_alias , null);
    }

    //se reciben los ids de los miebro que ya pertencen al chat para evitar dupilicdad en la tabla de contactos
    public ArrayList<ContactList> getMemberMissing(String idschats) {
        String selectQuery = "SELECT   * FROM " +
                DatabaseChatHelper.TABLE_CONTACTS +
                " WHERE " + DatabaseChatHelper.ID_ALIAS + " NOT IN " + idschats;
        SQLiteDatabase db = dbHelper.getContextlist();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ContactList> FavList = new ArrayList<ContactList>();
        if (cursor.moveToFirst()) {
            do {
                FavList.add(new ContactList(
                        cursor.getInt(3),//Id alias
                        cursor.getString(2),//nombre
                        cursor.getString(1)//alias

                ));
            } while (cursor.moveToNext());
        }
        return FavList;
    }


}