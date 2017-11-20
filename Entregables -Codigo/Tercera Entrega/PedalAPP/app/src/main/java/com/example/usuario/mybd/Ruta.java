package com.example.usuario.mybd;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by USUARIO on 29/10/2017.
 */

public class Ruta implements Serializable {
    Date fecha;
    Float kilometros;
    int horas;
    int minutos;
    double latitudIncial;
    double longitudInicial;
    double latitudFinal;
    double longitudFinal;
    String clima;
    public double getLatitudIncial() {
        return latitudIncial;
    }

    public void setLatitudIncial(double latitudIncial) {
        this.latitudIncial = latitudIncial;
    }

    public double getLongitudInicial() {
        return longitudInicial;
    }

    public void setLongitudInicial(double longitudInicial) {
        this.longitudInicial = longitudInicial;
    }

    public double getLatitudFinal() {
        return latitudFinal;
    }

    public void setLatitudFinal(double latitudFinal) {
        this.latitudFinal = latitudFinal;
    }

    public double getLongitudFinal() {
        return longitudFinal;
    }

    public void setLongitudFinal(double longitudFinal) {
        this.longitudFinal = longitudFinal;
    }



    public String getClima() {
        return clima;
    }

    public void setClima(String clima) {
        this.clima = clima;
    }

    public Ruta(){
    }
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Float getKilometros() {
        return kilometros;
    }

    public void setKilometros(Float kilometros) {
        this.kilometros = kilometros;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    @Override
    public String toString() {
        SimpleDateFormat formatFecha=new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatHora=new SimpleDateFormat("hh:mm:ss");
        return " "+formatFecha.format(getFecha())+" - "+formatHora.format(getFecha());
    }
}
