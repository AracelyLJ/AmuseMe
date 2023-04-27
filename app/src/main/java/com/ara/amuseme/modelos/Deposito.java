package com.ara.amuseme.modelos;

public class Deposito {

    private String hora;
    private String fecha;
    private String foto;
    private String monto;
    private String semanaFiscal;
    private String ubicacion;
    private String usuario;

    public Deposito() {
        this.hora="";
        this.fecha="";
        this.foto="";
        this.monto="";
        this.semanaFiscal="";
        this.ubicacion="";
        this.usuario="";
    }

    public Deposito(String hora, String fecha, String foto, String monto, String semanaFiscal, String ubicacion, String usuario) {
        this.hora = hora;
        this.fecha = fecha;
        this.foto = foto;
        this.monto = monto;
        this.semanaFiscal = semanaFiscal;
        this.ubicacion = ubicacion;
        this.usuario = usuario;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
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
