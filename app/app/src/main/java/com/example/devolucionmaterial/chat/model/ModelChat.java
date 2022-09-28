package com.example.devolucionmaterial.chat.model;

/**
 * Created by Administrador on 26/12/2016.
 */

public class ModelChat  {
    private long id;
    private int id_chat;
    private String message;
    private int type;
    private String user;
    private String file;
    private String timeStamp;
    private int status;

    //el id es el numero de la fila en la bd
    public ModelChat(long id, String mensaje, String timeStamp, String user, int type, int status) {
        this.id = id;
        this.message = mensaje;
        this.timeStamp = timeStamp;
        this.user = user;
        this.type = type;
        this.status = status;
    }


    public int getId_chat() {
        return id_chat;
    }

    public void setId_chat(int id_chat) {
        this.id_chat = id_chat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
