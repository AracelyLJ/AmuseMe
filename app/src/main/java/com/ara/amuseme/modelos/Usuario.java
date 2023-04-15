package com.ara.amuseme.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Usuario implements Parcelable {

    private String correo;
    private String maqRegSuc;
    private String nombre;
    private String pw;
    private String porDepositar;
    private String status;
    private String sucRegistradas;
    private String sucursales;
    private String tel;

    public Usuario(){
        correo="";
        maqRegSuc="";
        nombre="";
        pw="";
        porDepositar="";
        status="";
        sucRegistradas="";
        sucursales="";
        tel="";
    }

    public Usuario(String correo, String maqRegSuc, String nombre, String pw, String porDepositar, String status, String sucRegistradas, String sucursales, String tel) {
        this.correo = correo;
        this.maqRegSuc = maqRegSuc;
        this.nombre = nombre;
        this.pw = pw;
        this.porDepositar = porDepositar;
        this.status = status;
        this.sucRegistradas = sucRegistradas;
        this.sucursales = sucursales;
        this.tel = tel;
    }

    protected Usuario(Parcel in) {
        correo = in.readString();
        maqRegSuc = in.readString();
        nombre = in.readString();
        pw = in.readString();
        porDepositar = in.readString();
        status = in.readString();
        sucRegistradas = in.readString();
        sucursales = in.readString();
        tel = in.readString();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getMaqRegSuc() {
        return maqRegSuc;
    }

    public void setMaqRegSuc(String maqRegSuc) {
        this.maqRegSuc = maqRegSuc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getpw() {
        return pw;
    }

    public void setpw(String pw) {
        this.pw = pw;
    }

    public String getPorDepositar() {
        return porDepositar;
    }

    public void setPorDepositar(String porDepositar) {
        this.porDepositar = porDepositar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSucRegistradas() {
        return sucRegistradas;
    }

    public void setSucRegistradas(String sucRegistradas) {
        this.sucRegistradas = sucRegistradas;
    }

    public String getSucursales() {
        return sucursales;
    }

    public void setSucursales(String sucursales) {
        this.sucursales = sucursales;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(correo);
        parcel.writeString(maqRegSuc);
        parcel.writeString(nombre);
        parcel.writeString(pw);
        parcel.writeString(porDepositar);
        parcel.writeString(status);
        parcel.writeString(sucRegistradas);
        parcel.writeString(sucursales);
        parcel.writeString(tel);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "correo='" + correo + '\'' +
                ", maqRegSuc='" + maqRegSuc + '\'' +
                ", nombre='" + nombre + '\'' +
                ", pw='" + pw + '\'' +
                ", porDepositar='" + porDepositar + '\'' +
                ", status='" + status + '\'' +
                ", sucRegistradas='" + sucRegistradas + '\'' +
                ", sucursales='" + sucursales + '\'' +
                ", tel='" + tel + '\'' +
                '}';
    }
}
