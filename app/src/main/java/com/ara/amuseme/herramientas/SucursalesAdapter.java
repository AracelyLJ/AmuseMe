package com.ara.amuseme.herramientas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ara.amuseme.R;
import com.ara.amuseme.administrador.InfoSucursales;
import com.ara.amuseme.modelos.Sucursal;
import com.ara.amuseme.modelos.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SucursalesAdapter extends RecyclerView.Adapter<TiposViewHolder>{

    private Context context;
    private ArrayList<Sucursal> sucursales;
    private ArrayList<Sucursal> sucursalesOriginal;
    private String filtro;
    private Usuario usuario_actual;

    public SucursalesAdapter(Context context, ArrayList<Sucursal> sucursales,
                             String filtro, Usuario usuario_actual) {
        this.context = context;
        this.sucursales = sucursales;
        this.filtro = filtro;
        this.sucursalesOriginal = new ArrayList<>();
        this.sucursalesOriginal.addAll(sucursales);
        this.usuario_actual = usuario_actual;
    }

    @NonNull
    @Override
    public TiposViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TiposViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.sucursal_db, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TiposViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.getTxtTipo().setText(sucursales.get(position).getNombre());
        switch (this.filtro.toLowerCase()){
            case "clave":
                holder.getTxtValorFiltro().setText(sucursales.get(position).getClave());
                break;
            case "nombre":
                holder.getTxtValorFiltro().setText(sucursales.get(position).getNombre());
                break;
        }
        final int position1 = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goSucursales(sucursales.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return sucursales.size();
    }

    public void filtrado(String txtBuscar) {
        String filtro = this.filtro.toLowerCase();
        int len = txtBuscar.length();
        if (len == 0) {
            sucursales.clear();
            sucursales.addAll(sucursalesOriginal);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Sucursal> coleccion = new ArrayList<>();
                switch (filtro){
                    case "clave":
                        coleccion = sucursales.stream().filter(i -> i.getClave().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                    case "nombre":
                        coleccion = sucursales.stream().filter(i -> i.getNombre().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                }
                sucursales.clear();
                sucursales.addAll(coleccion);
            } else {
                for (Sucursal tm: sucursalesOriginal) {
                    if (tm.getNombre().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        sucursales.add(tm);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
    public void goSucursales(Sucursal sucursal) {
        Intent i = new Intent(context, InfoSucursales.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("sucursal", sucursal);
        i.putExtra("usuario_actual", usuario_actual);
        context.startActivity(i);
    }
}
