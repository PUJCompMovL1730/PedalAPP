package com.example.usuario.mybd;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by Nicolás Restrepo on 18/11/2017.
 */

public class RutaProgramada  implements Serializable{

    private Ruta r;
    private String titulo;
    private String descrip;

    public RutaProgramada(Ruta r, String titulo, String descrip) {
        this.r = r;
        this.titulo = titulo;
        this.descrip = descrip;
    }

    public RutaProgramada() {

    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public Ruta getR() {
        return r;
    }

    public void setR(Ruta r) {
        this.r = r;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public String toString() {
        SimpleDateFormat formatFecha=new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatHora=new SimpleDateFormat("hh:mm:ss");
        return " "+formatFecha.format(getR().getFecha())+" - "+formatHora.format(getR().getFecha());
    }
}

