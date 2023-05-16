package com.ara.amuseme.herramientas;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ara.amuseme.R;
import com.ara.amuseme.modelos.Usuario;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    Context context;
    ArrayList<Usuario> usuarios;
    ArrayList<Usuario> usuariosOriginal;
    String filtro;

    public ItemsAdapter(Context context, ArrayList<Usuario> usuarios, String filtro) {
        this.context = context;
        this.usuarios = usuarios;
        this.filtro = filtro;
        usuariosOriginal = new ArrayList<>();
        usuariosOriginal.addAll(usuarios);
        Toast.makeText(context, this.filtro, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_db, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.getTxtUsuario().setText(usuarios.get(position).getNombre());
        switch (this.filtro.toLowerCase()){
            case "nombre":
                holder.getTxtValorFiltro().setText(usuarios.get(position).getNombre());
                break;
            case "correo":
                holder.getTxtValorFiltro().setText(usuarios.get(position).getCorreo());
                break;
            case "id":
                holder.getTxtValorFiltro().setText(usuarios.get(position).getId());
                break;
            case "rol":
                holder.getTxtValorFiltro().setText(usuarios.get(position).getRol());
                break;
            case "status":
                holder.getTxtValorFiltro().setText(usuarios.get(position).getStatus());
                break;
            case "telefono":
                holder.getTxtValorFiltro().setText(usuarios.get(position).getTel());
                break;
        }
        final int position1 = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, nombres.get(position1), Toast.LENGTH_SHORT).show();
//                Toast.makeText(context, filtro.get(position1), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public void filtrado(String txtBuscar) {
        String filtro = this.filtro.toLowerCase();
        int len = txtBuscar.length();
        if (len == 0) {
            usuarios.clear();
            usuarios.addAll(usuariosOriginal);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Usuario> coleccion = new ArrayList<>();
                switch (filtro){
                    case "nombre":
                        coleccion = usuarios.stream().filter(i -> i.getNombre().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                    case "correo":
                        coleccion = usuarios.stream().filter(i -> i.getCorreo().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                    case "id":
                        coleccion = usuarios.stream().filter(i -> i.getId().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                    case "rol":
                        coleccion = usuarios.stream().filter(i -> i.getRol().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                    case "status":
                        coleccion = usuarios.stream().filter(i -> i.getStatus().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                    case "telefono":
                        coleccion = usuarios.stream().filter(i -> i.getTel().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                }
                usuarios.clear();
                usuarios.addAll(coleccion);
            } else {
                for (Usuario u: usuariosOriginal) {
                    if (u.getNombre().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        usuarios.add(u);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}
