package com.ara.amuseme.herramientas;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ara.amuseme.R;

public class DepositosViewHolder extends RecyclerView.ViewHolder{

    private ImageView img_deposito;
    private TextView txtDeposito;
    private TextView txtValorFiltro;
    public DepositosViewHolder(@NonNull View itemView) {
        super(itemView);
        img_deposito = itemView.findViewById(R.id.img_deposito);
        txtDeposito = itemView.findViewById(R.id.txtDeposito);
        txtValorFiltro = itemView.findViewById(R.id.txtValorFiltro);
    }

    public ImageView getImg_deposito() {
        return img_deposito;
    }

    public void setImg_deposito(ImageView img_deposito) {
        this.img_deposito = img_deposito;
    }

    public TextView getTxtDeposito() {
        return txtDeposito;
    }

    public void setTxtDeposito(TextView txtDeposito) {
        this.txtDeposito = txtDeposito;
    }

    public TextView getTxtValorFiltro() {
        return txtValorFiltro;
    }

    public void setTxtValorFiltro(TextView txtValorFiltro) {
        this.txtValorFiltro = txtValorFiltro;
    }
}
