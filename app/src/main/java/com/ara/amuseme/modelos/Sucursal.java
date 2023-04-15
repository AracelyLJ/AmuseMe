package com.ara.amuseme.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Sucursal implements Parcelable {

    private String clave;
    private String maquinas;
    private String nombre;
    private String ubicacion;

    public Sucursal(String clave, String maquinas, String nombre, String ubicacion) {
        this.clave = clave;
        this.maquinas = maquinas;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
    }

    protected Sucursal(Parcel in) {
        clave = in.readString();
        maquinas = in.readString();
        nombre = in.readString();
        ubicacion = in.readString();
    }

    public static final Creator<Sucursal> CREATOR = new Creator<Sucursal>() {
        @Override
        public Sucursal createFromParcel(Parcel in) {
            return new Sucursal(in);
        }

        @Override
        public Sucursal[] newArray(int size) {
            return new Sucursal[size];
        }
    };

    public String getClave() {
        return clave;
    }
    public void setClave(String clave) {
        this.clave = clave;
    }
    public String getMaquinas() {
        return maquinas;
    }
    public void setMaquinas(String maquinas) {
        this.maquinas = maquinas;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getUbicacion() {
        return ubicacion;
    }
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(clave);
        parcel.writeString(maquinas);
        parcel.writeString(nombre);
        parcel.writeString(ubicacion);
    }

    @Override
    public String toString() {
        return "Sucursal{" +
                "clave='" + clave + '\'' +
                ", maquinas='" + maquinas + '\'' +
                ", nombre='" + nombre + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                '}';
    }
}
