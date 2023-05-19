package com.ara.amuseme.modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Usuario implements Parcelable {

    private String contRegistro;
    private String correo;
    private String id;
    private String maqRegSuc;
    private String nombre;
    private String pw;
    private String porDepositar;
    private String rol;
    private String status;
    private String sucRegistradas;
    private String sucursales;
    private String tel;
    private String token;

    public Usuario(){
        contRegistro="";
        correo="";
        id = "";
        maqRegSuc="";
        nombre="";
        pw="";
        porDepositar="";
        rol = "";
        status="";
        sucRegistradas="";
        sucursales="";
        tel="";
        token = "";
    }

    public Usuario(String contRegistro, String correo, String id, String maqRegSuc, String nombre,
                   String porDepositar, String pw, String rol, String status, String sucRegistradas,
                   String sucursales, String tel, String token) {
        this.contRegistro = contRegistro;
        this.correo = correo;
        this.id = id;
        this.maqRegSuc = maqRegSuc;
        this.nombre = nombre;
        this.porDepositar = porDepositar;
        this.pw = pw;
        this.rol = rol;
        this.status = status;
        this.sucRegistradas = sucRegistradas;
        this.sucursales = sucursales;
        this.tel = tel;
        this.token = token;
    }

    protected Usuario(Parcel in) {
        contRegistro = in.readString();
        correo = in.readString();
        id = in.readString();
        maqRegSuc = in.readString();
        nombre = in.readString();
        porDepositar = in.readString();
        pw = in.readString();
        rol = in.readString();
        status = in.readString();
        sucRegistradas = in.readString();
        sucursales = in.readString();
        tel = in.readString();
        token = in.readString();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPorDepositar() {
        return porDepositar;
    }

    public void setPorDepositar(String porDepositar) {
        this.porDepositar = porDepositar;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
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

    public String getContRegistro() {
        return contRegistro;
    }

    public void setContRegistro(String contRegistro) {
        this.contRegistro = contRegistro;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contRegistro);
        parcel.writeString(correo);
        parcel.writeString(id);
        parcel.writeString(maqRegSuc);
        parcel.writeString(nombre);
        parcel.writeString(porDepositar);
        parcel.writeString(pw);
        parcel.writeString(rol);
        parcel.writeString(status);
        parcel.writeString(sucRegistradas);
        parcel.writeString(sucursales);
        parcel.writeString(tel);
        parcel.writeString(token);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "contRegistro='" + contRegistro + '\'' +
                ", correo='" + correo + '\'' +
                ", id='" + id + '\'' +
                ", maqRegSuc='" + maqRegSuc + '\'' +
                ", nombre='" + nombre + '\'' +
                ", porDepositar='" + porDepositar + '\'' +
                ", pw='" + pw + '\'' +
                ", status='" + status + '\'' +
                ", sucRegistradas='" + sucRegistradas + '\'' +
                ", sucursales='" + sucursales + '\'' +
                ", tel='" + tel + '\'' +
                ", token='" + token + '\'' +
                '}';
    }


}
