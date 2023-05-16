package com.ara.amuseme.herramientas;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ara.amuseme.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    private ImageView imgUsuario;
    private TextView txtUsuario;
    private TextView txtValorFiltro;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        imgUsuario = itemView.findViewById(R.id.img_item);
        txtUsuario = itemView.findViewById(R.id.txtUsuario);
        txtValorFiltro = itemView.findViewById(R.id.txtValorFiltro);
    }

    public ImageView getImgUsuario() {
        return imgUsuario;
    }

    public void setImgUsuario(ImageView imgUsuario) {
        this.imgUsuario = imgUsuario;
    }

    public TextView getTxtUsuario() {
        return txtUsuario;
    }

    public void setTxtUsuario(TextView txtUsuario) {
        this.txtUsuario = txtUsuario;
    }

    public TextView getTxtValorFiltro() {
        return txtValorFiltro;
    }
}
