package com.example.usuario.mybd;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by Nicolás Restrepo on 18/11/2017.
 */

public class MarcadorEmpresarial {

    private Double longitude;
    private Double latitude;
    private Date deadline;
    private String title;
    private String info;


    public MarcadorEmpresarial() {

    }

    public MarcadorEmpresarial(Double longitude, Double latitude, Date deadline, String title, String info) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.deadline = deadline;
        this.title = title;
        this.info = info;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

