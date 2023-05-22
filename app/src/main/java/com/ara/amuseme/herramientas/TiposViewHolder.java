package com.ara.amuseme.herramientas;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ara.amuseme.R;

public class TiposViewHolder extends RecyclerView.ViewHolder{

    private ImageView img_tipo;
    private TextView txtTipo;
    private TextView txtValorFiltro;

    public TiposViewHolder(@NonNull View itemView) {
        super(itemView);
        img_tipo = itemView.findViewById(R.id.img_tipo);
        txtTipo = itemView.findViewById(R.id.txtTipo);
        txtValorFiltro = itemView.findViewById(R.id.txtValorFiltro);
    }

    public ImageView getImg_tipo() {
        return img_tipo;
    }

    public void setImg_tipo(ImageView img_tipo) {
        this.img_tipo = img_tipo;
    }

    public TextView getTxtTipo() {
        return txtTipo;
    }

    public void setTxtTipo(TextView txtTipo) {
        this.txtTipo = txtTipo;
    }

    public TextView getTxtValorFiltro() {
        return txtValorFiltro;
    }

    public void setTxtValorFiltro(TextView txtValorFiltro) {
        this.txtValorFiltro = txtValorFiltro;
    }
}
