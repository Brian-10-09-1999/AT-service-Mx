package com.example.devolucionmaterial.chat.model;

/**
 * Created by Administrador on 20/12/2016.
 */

public class ChatList {

    // TODO: 23/03/2017 id_chat es el id_alias de los usuarios
    int id_chat;
    String title;
    String desc;
    int image;
    String users;
    int id_alias;
    String nombre;
    String alias;
    int status;
    long timeStamp;
    int type;

    public ChatList(String title, String desc, int image) {
        this.title = title;
        this.desc = desc;
        this.image = image;
    }

    public ChatList(int id_alias, String nombre, String alias) {
        this.id_alias = id_alias;
        this.nombre = nombre;
        this.alias = alias;
    }


    public ChatList(int chat_id, String title, String desc, Integer image, String users,int status,long timeStamp,  int type) {
        this.id_chat = chat_id;
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.users = users;
        this.status = status;
        this.timeStamp = timeStamp;
        this.type = type;
    }


    public int getId_chat() {
        return id_chat;
    }

    public void setId_chat(int id_chat) {
        this.id_chat = id_chat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }



    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public int getId_alias() {
        return id_alias;
    }

    public void setId_alias(int id_alias) {
        this.id_alias = id_alias;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
