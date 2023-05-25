package com.ara.amuseme.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Deposito implements Parcelable {

    private String hora;
    private String fecha;
    private String foto;
    private String id;
    private String monto;
    private String semanaFiscal;
    private String ubicacion;
    private String usuario;

    public Deposito() {
        this.hora="";
        this.fecha="";
        this.foto="";
        this.id="";
        this.monto="";
        this.semanaFiscal="";
        this.ubicacion="";
        this.usuario="";
    }

    public Deposito(String hora, String fecha, String foto, String id, String monto,
                    String semanaFiscal, String ubicacion, String usuario) {
        this.hora = hora;
        this.fecha = fecha;
        this.foto = foto;
        this.id = id;
        this.monto = monto;
        this.semanaFiscal = semanaFiscal;
        this.ubicacion = ubicacion;
        this.usuario = usuario;
    }

    protected  Deposito(Parcel in) {
        this.hora = in.readString();
        this.fecha = in.readString();
        this.foto = in.readString();
        this.id = in.readString();
        this.monto = in.readString();
        this.semanaFiscal = in.readString();
        this.ubicacion = in.readString();
        this.usuario = in.readString();
    }

    public static final Creator<Deposito> CREATOR = new Creator<Deposito>() {
        @Override
        public Deposito createFromParcel(Parcel in) {
            return new Deposito(in);
        }

        @Override
        public Deposito[] newArray(int size) {
            return new Deposito[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(hora);
        parcel.writeString(fecha);
        parcel.writeString(foto);
        parcel.writeString(id);
        parcel.writeString(monto);
        parcel.writeString(semanaFiscal);
        parcel.writeString(ubicacion);
        parcel.writeString(usuario);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Deposito{" +
                "hora='" + hora + '\'' +
                ", fecha='" + fecha + '\'' +
                ", foto='" + foto + '\'' +
                ", id='" + id + '\'' +
                ", monto='" + monto + '\'' +
                ", semanaFiscal='" + semanaFiscal + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", usuario='" + usuario + '\'' +
                '}';
    }
}
