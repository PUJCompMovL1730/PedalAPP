package com.example.usuario.mybd;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by USUARIO on 23/10/2017.
 */

public class MyUser {
    private String nombres;
    private String apellidos;
    private int edad;
    private float altura;
    private float peso;
    private String ciudad;
    private String sexo;
    private String correo;
    private List<String> amigos;
    private Map<String,String> buzonEntrada;
    private Map<String,String> buzonSalida;

    private Date fechaNacimiento;


    public MyUser(){

    }


    public List<String> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<String> amigos) {
        this.amigos = amigos;
    }

    public Map<String, String> getBuzonEntrada() {
        return buzonEntrada;
    }

    public void setBuzonEntrada(Map<String, String> buzonEntrada) {
        this.buzonEntrada = buzonEntrada;
    }

    public Map<String, String> getBuzonSalida() {
        return buzonSalida;
    }

    public void setBuzonSalida(Map<String, String> buzonSalida) {
        this.buzonSalida = buzonSalida;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }


    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public float getAltura() {
        return altura;
    }

    public void setAltura(float altura) {
        this.altura = altura;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }
}
