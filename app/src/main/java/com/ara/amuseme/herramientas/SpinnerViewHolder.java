package com.ara.amuseme.herramientas;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ara.amuseme.R;

public class SpinnerViewHolder extends RecyclerView.ViewHolder {

    private TextView txtOpcion;

    public SpinnerViewHolder(@NonNull View itemView) {
        super(itemView);
        txtOpcion = itemView.findViewById(R.id.txtOpcion);
    }

    public TextView getTxtOpcion() {
        return txtOpcion;
    }
}
