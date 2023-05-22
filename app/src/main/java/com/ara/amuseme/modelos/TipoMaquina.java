package com.ara.amuseme.modelos;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TipoMaquina implements Parcelable {

    private String clave;
    private String contadores;
    private String id;
    private String nombre;
    private String observaciones;

    public TipoMaquina() {
        this.clave = "";
        this.contadores = "";
        this.nombre = "";
        this.observaciones = "";
    }

    public TipoMaquina(String clave, String contadores, String id, String nombre, String observaciones) {
        this.clave = clave;
        this.contadores = contadores;
        this.id = id;
        this.nombre = nombre;
        this.observaciones = observaciones;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getContadores() {
        return contadores;
    }

    public void setContadores(String contadores) {
        this.contadores = contadores;
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

    protected  TipoMaquina(Parcel in) {
        this.clave = in.readString();
        this.contadores = in.readString();
        this.id = in.readString();
        this.nombre = in.readString();
        this.observaciones = in.readString();
    }

    public static final Creator<TipoMaquina> CREATOR = new Creator<TipoMaquina>() {
        @Override
        public TipoMaquina createFromParcel(Parcel in) {
            return new TipoMaquina(in);
        }

        @Override
        public TipoMaquina[] newArray(int size) {
            return new TipoMaquina[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(clave);
        parcel.writeString(contadores);
        parcel.writeString(id);
        parcel.writeString(nombre);
        parcel.writeString(observaciones);
    }

    @Override
    public String toString() {
        return "TipoMaquina{" +
                "clave='" + clave + '\'' +
                ", contadores='" + contadores + '\'' +
                ", id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
