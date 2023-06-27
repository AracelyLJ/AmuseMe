package com.ara.amuseme.herramientas;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ara.amuseme.R;

public class RegistrosMaquinasViewHolder extends RecyclerView.ViewHolder{

    private ImageView img_registro;
    private TextView txtRegistro;
    private TextView txtValorFiltro;
    public RegistrosMaquinasViewHolder(@NonNull View itemView) {
        super(itemView);
        img_registro = itemView.findViewById(R.id.img_registro);
        txtRegistro = itemView.findViewById(R.id.txtRegistro);
        txtValorFiltro = itemView.findViewById(R.id.txtValorFiltro);
    }

    public ImageView getImg_registro() {
        return img_registro;
    }

    public void setImg_registro(ImageView img_registro) {
        this.img_registro = img_registro;
    }

    public TextView getTxtRegistro() {
        return txtRegistro;
    }

    public void setTxtRegistro(TextView txtRegistro) {
        this.txtRegistro = txtRegistro;
    }

    public TextView getTxtValorFiltro() {
        return txtValorFiltro;
    }

    public void setTxtValorFiltro(TextView txtValorFiltro) {
        this.txtValorFiltro = txtValorFiltro;
    }
}
