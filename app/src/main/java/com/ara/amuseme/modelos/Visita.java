package com.ara.amuseme.modelos;

public class Visita {

    private String descripcion;
    private String fecha;
    private String hora;
    private String semanaFiscal;
    private String ubicacion;
    private String usuario;

    public Visita() {
        this.descripcion = "";
        this.fecha = "";
        this.hora = "";
        this.semanaFiscal = "";
        this.ubicacion = "";
        this.usuario = "";
    }

    public Visita(String descripcion, String fecha, String hora, String semanaFiscal, String ubicacion, String usuario) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.hora = hora;
        this.semanaFiscal = semanaFiscal;
        this.ubicacion = ubicacion;
        this.usuario = usuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getSemanaFiscal() {
        return semanaFiscal;
    }

    public void setSemanaFiscal(String semanaFiscal) {
        this.semanaFiscal = semanaFiscal;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
