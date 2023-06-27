package com.ara.amuseme.herramientas;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ara.amuseme.HomeAdmin;
import com.ara.amuseme.R;
import com.ara.amuseme.administrador.InfoDeposito;
import com.ara.amuseme.administrador.Maquinas;
import com.ara.amuseme.administrador.RegistrosMaquinasID;
import com.ara.amuseme.administrador.RegistrosMaquinasInfo;
import com.ara.amuseme.modelos.Deposito;
import com.ara.amuseme.modelos.RegistroMaquina;
import com.ara.amuseme.modelos.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RegistrosMaquinasAdapter extends RecyclerView.Adapter<RegistrosMaquinasViewHolder>{

    private Context context;
    private ArrayList<RegistroMaquina> registros;
    private ArrayList<RegistroMaquina> registrosOriginal;
    private String filtro;
    private Usuario usuario_actual;

    public RegistrosMaquinasAdapter(Context context,
                                    ArrayList<RegistroMaquina> registros,
                                    String filtro, Usuario usuario_actual) {
        this.context = context;
        this.registros = registros;
        this.filtro = filtro;
        this.registrosOriginal = new ArrayList<>();
        this.registrosOriginal.addAll(registros);
        this.usuario_actual = usuario_actual;
        Collections.reverse(this.registros);
        Collections.reverse(this.registrosOriginal);
    }

    @NonNull
    @Override
    public RegistrosMaquinasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RegistrosMaquinasViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.registromaquina_db, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrosMaquinasViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.getTxtRegistro().setText(registros.get(position).getFecha());

        switch (this.filtro.toLowerCase()){
            case "fecha":
                holder.getTxtValorFiltro().setText(registros.get(position).getFecha());
                break;
            case "alias":
                holder.getTxtValorFiltro().setText(registros.get(position).getAlias());
                break;
            case "id contador":
                holder.getTxtValorFiltro().setText(registros.get(position).getContRegistro());
                break;
            case "semana fiscal":
                holder.getTxtValorFiltro().setText(registros.get(position).getSemanaFiscal());
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RegistrosMaquinasInfo.class);
                intent.putExtra("registro", registros.get(position));
                intent.putExtra("usuario_seleccionado", usuario_actual);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    public void filtrado(String txtBuscar) {
        String filtro = this.filtro.toLowerCase();
        int len = txtBuscar.length();
        if (len == 0) {
            registros.clear();
            registros.addAll(registrosOriginal);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<RegistroMaquina> coleccion = new ArrayList<>();
                try{
                    switch (filtro){
                        case "fecha":
                            coleccion = registros.stream().filter(i -> i.getFecha()
                                    .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                            break;
                        case "alias":
                            coleccion = registros.stream().filter(i -> i.getAlias().toLowerCase()
                                    .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                            break;
                        case "id contador":
                            coleccion = registros.stream().filter(i -> i.getContRegistro().toLowerCase()
                                    .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                            break;
                        case "semana fiscal":
                            coleccion = registros.stream().filter(i -> i.getSemanaFiscal().toLowerCase()
                                    .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                            break;
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error al generar la lista de filtros.", Toast.LENGTH_SHORT).show();
                }
                registros.clear();
                registros.addAll(coleccion);
            } else {
                for (RegistroMaquina d: registros) {
                    if (d.getFecha().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        registros.add(d);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

}
