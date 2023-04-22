package com.ara.amuseme.modelos;

import java.util.HashMap;
import java.util.Map;

public class RegistroMaquina {

    private String alias;
    private String contRegistro;
    private String fecha;
    private String hora;
    private String nombre;
    private String semanaFiscal;
    private String sucursal;
    private String tipoMaquina;
    private String ubicacion;
    private String usuario;
    private HashMap<String, String> contadores;

    public RegistroMaquina() {
        this.alias = "0";
        this.contRegistro = "0";
        this.fecha = "0";
        this.hora = "0";
        this.nombre = "0";
        this.semanaFiscal = "0";
        this.sucursal = "0";
        this.tipoMaquina = "0";
        this.ubicacion = "0";
        this.usuario = "0";
        this.contadores = new HashMap<>();
    }

    public RegistroMaquina(String alias, String contRegistro, String fecha, String hora,
                           String nombre, String semanaFiscal, String sucursal, String tipoMaquina,
                           String ubicacion, String usuario, HashMap<String, String> contadores) {
        this.alias = alias;
        this.contRegistro = contRegistro;
        this.fecha = fecha;
        this.hora = hora;
        this.nombre = nombre;
        this.semanaFiscal = semanaFiscal;
        this.sucursal = sucursal;
        this.tipoMaquina = tipoMaquina;
        this.ubicacion = ubicacion;
        this.usuario = usuario;
        this.contadores = contadores;
    }

    public HashMap<String, String> getContadores() {
        return contadores;
    }

    public void setContadores(HashMap<String, String> contadores) {
        this.contadores = contadores;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getContRegistro() {
        return contRegistro;
    }

    public void setContRegistro(String contRegistro) {
        this.contRegistro = contRegistro;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSemanaFiscal() {
        return semanaFiscal;
    }

    public void setSemanaFiscal(String semanaFiscal) {
        this.semanaFiscal = semanaFiscal;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getTipoMaquina() {
        return tipoMaquina;
    }

    public void setTipoMaquina(String tipoMaquina) {
        this.tipoMaquina = tipoMaquina;
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

    @Override
    public String toString() {
        return "RegistroMaquina{" +
                "alias='" + alias + '\'' +
                ", contRegistro='" + contRegistro + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", nombre='" + nombre + '\'' +
                ", semanaFiscal='" + semanaFiscal + '\'' +
                ", sucursal='" + sucursal + '\'' +
                ", tipoMaquina='" + tipoMaquina + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", usuario='" + usuario + '\'' +
                ", contadores=" + contadores +
                '}';
    }

    public String strDatosPrincipales() {
        String info = "Máquina: "+ alias;
        for (Map.Entry<String, String> contador : contadores.entrySet()) {
            info += "\n" + contador.getKey() + ": " + contador.getValue();
        }
        return info;
    }

    public String strDatosSecundarios() {
        return "Nombre máquina: "+ nombre +
                "\nFecha: " + fecha +
                "\nHora: " + hora ;
    }

}
