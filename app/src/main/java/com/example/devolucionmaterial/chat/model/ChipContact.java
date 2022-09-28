package com.example.devolucionmaterial.chat.model;

/**
 * Created by EDGAR ARANA on 21/04/2017.
 */

public class ChipContact {
    int position ;
    String name ;

    public ChipContact(int position, String name){
        this.position=position;
        this.name=name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
