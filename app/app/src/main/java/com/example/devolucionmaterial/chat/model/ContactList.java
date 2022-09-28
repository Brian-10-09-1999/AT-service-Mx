package com.example.devolucionmaterial.chat.model;

import java.io.Serializable;

/**
 * Created by Administrador on 22/12/2016.
 */

public class ContactList implements Serializable {
    String alias;
    int id_alias;
    String name;
    String email;
    String token;
    boolean isSelected;
    int image;

    public ContactList(String alias, String nombre, int id_alias, String email, String token) {
        this.alias = alias;
        this.name = nombre;
        this.id_alias = id_alias;
        this.email = email;
        this.token = token;
    }

    public ContactList(int id_alias, String name, String alias) {
        this.id_alias = id_alias;
        this.name = name;
        this.alias = alias;
    }


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getId_alias() {
        return id_alias;
    }

    public void setId_alias(int id_alias) {
        this.id_alias = id_alias;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
