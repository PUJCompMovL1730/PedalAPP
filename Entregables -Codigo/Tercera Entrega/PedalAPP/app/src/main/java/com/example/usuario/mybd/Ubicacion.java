package com.example.usuario.mybd;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by USUARIO on 23/10/2017.
 */

public class Ubicacion {
    Double longitude;
    Double latitude;
    String usuario;
    Boolean active;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    Ubicacion(){

        longitude = null;
        latitude = null;

    }

    public Double getLatitude(){
        return latitude;
    }
    public Double getLongitude(){
        return longitude;
    }
    public void setLatitude(Double  l){
        latitude=l;
    }
    public void setLongitude(Double l){
        longitude=l;
    }

}
