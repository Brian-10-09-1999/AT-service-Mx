package com.example.devolucionmaterial.activitys.Rack.Adapters;

import java.util.ArrayList;

/**
 * Created by gonet_cam on 25/08/17.
 */

public class itemModel {

    private String Subsala,id;
    private String description;
    private boolean prime;
    private String   rack,ubicacionRack,status;



    public String getSubsala() {
        return Subsala;
    }
    public void setSubsala(String sub) {
        this.Subsala = sub;
    }

    public String getIdSubsala(){return  id;}
    public void setIdSubsala(String ids ){ this.id=ids;}

    public String getStatus() {
        return status;
    }
    public void setStaus(String sub) {
        this.status = sub;
    }

    public String getRack() {
        return rack;
    }
    public void setRack(String r) {
        this.rack =r;
    }


    public String getUbicacionRack() {
        return ubicacionRack;
    }
    public void setUbicacionRack(String rac) {
        this.ubicacionRack =rac;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    public boolean isPrime() {
        return prime;
    }
    public void setPrime(boolean prime) {
        this.prime = prime;
    }




    public static ArrayList<itemModel> getData(String[] subs, String[] sta, String[] id,String[] rack,String[] ubicacion ) {


        ArrayList<itemModel> dataList = new ArrayList<>();
        String [] subsalas = subs;
        String [] status= sta;
        String [] ra= rack;
        String [] ubi= ubicacion;
        String [] ids= id;


        for (int i = 0; i < subsalas.length; i++) {

            itemModel item = new itemModel();
            item.setIdSubsala(ids[i]);
            item.setSubsala(subsalas[i]);
            item.setStaus(status[i]);
            item.setPrime(checkPrime(i));
            item.setRack(ra[i]);
            item.setUbicacionRack(ubi[i]);

            dataList.add(item);
        }

        return dataList;
    }

    private static boolean checkPrime(int position) {
        if (position == 0 || position == 1) return false;
        for (int i = 2; i <= position / 2; ++i) {
            if (position % i == 0) {
                return false;
            }
        }
        return true;
    }





}
