package com.ara.amuseme.modelos;

public class Maquina {

    private String alias;
    private String imagen;
    private String nombre;
    private String observaciones;
    private String renta;

    public Maquina() {
        alias = "";
        imagen = "";
        nombre = "";
        observaciones = "";
        renta = "";
    }

    public Maquina(String alias, String nombre) {
        this.alias = alias;
        this.nombre = nombre;
    }

    public Maquina(String alias, String imagen, String nombre, String observaciones, String renta) {
        this.alias = alias;
        this.imagen = imagen;
        this.nombre = nombre;
        this.observaciones = observaciones;
        this.renta = renta;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getRenta() {
        return renta;
    }

    public void setRenta(String renta) {
        this.renta = renta;
    }
}
