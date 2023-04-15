package com.ara.amuseme.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Maquina implements Parcelable {

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

    protected Maquina(Parcel in) {
        alias = in.readString();
        imagen = in.readString();
        nombre = in.readString();
        observaciones = in.readString();
        renta = in.readString();
    }

    public static final Creator<Maquina> CREATOR = new Creator<Maquina>() {
        @Override
        public Maquina createFromParcel(Parcel in) {
            return new Maquina(in);
        }

        @Override
        public Maquina[] newArray(int size) {
            return new Maquina[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(alias);
        parcel.writeString(imagen);
        parcel.writeString(nombre);
        parcel.writeString(observaciones);
        parcel.writeString(renta);
    }

    @Override
    public String toString() {
        return "Maquina{" +
                "alias='" + alias + '\'' +
                ", imagen='" + imagen + '\'' +
                ", nombre='" + nombre + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", renta='" + renta + '\'' +
                '}';
    }
}
