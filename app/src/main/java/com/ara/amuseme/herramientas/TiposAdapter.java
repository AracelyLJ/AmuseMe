package com.ara.amuseme.herramientas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ara.amuseme.R;
import com.ara.amuseme.administrador.InfoTipoMaquina;
import com.ara.amuseme.administrador.InfoUsuario;
import com.ara.amuseme.modelos.TipoMaquina;
import com.ara.amuseme.modelos.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TiposAdapter extends RecyclerView.Adapter<TiposViewHolder>{

    private Context context;
    private ArrayList<TipoMaquina> tiposMaquinas;
    private ArrayList<TipoMaquina> tiposMaquinasOriginal;
    private String filtro;
    private Usuario usuario_actual;

    public TiposAdapter(Context context, ArrayList<TipoMaquina> tiposMaquinas,
                        String filtro, Usuario usuario_actual) {
        this.context = context;
        this.tiposMaquinas = tiposMaquinas;
        this.filtro = filtro;
        this.tiposMaquinasOriginal = new ArrayList<>();
        this.tiposMaquinasOriginal.addAll(tiposMaquinas);
        this.usuario_actual = usuario_actual;
    }

    @NonNull
    @Override
    public TiposViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TiposViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.tipo_maq_db, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TiposViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.getTxtTipo().setText(tiposMaquinas.get(position).getNombre());
        switch (this.filtro.toLowerCase()){
            case "clave":
                holder.getTxtValorFiltro().setText(tiposMaquinas.get(position).getClave());
                break;
            case "nombre":
                holder.getTxtValorFiltro().setText(tiposMaquinas.get(position).getNombre());
                break;
        }
        final int position1 = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTipoMaquina(tiposMaquinas.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return tiposMaquinas.size();
    }

    public void filtrado(String txtBuscar) {
        String filtro = this.filtro.toLowerCase();
        int len = txtBuscar.length();
        if (len == 0) {
            tiposMaquinas.clear();
            tiposMaquinas.addAll(tiposMaquinasOriginal);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<TipoMaquina> coleccion = new ArrayList<>();
                switch (filtro){
                    case "clave":
                        coleccion = tiposMaquinas.stream().filter(i -> i.getClave().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                    case "nombre":
                        coleccion = tiposMaquinas.stream().filter(i -> i.getNombre().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                }
                tiposMaquinas.clear();
                tiposMaquinas.addAll(coleccion);
            } else {
                for (TipoMaquina tm: tiposMaquinasOriginal) {
                    if (tm.getNombre().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        tiposMaquinas.add(tm);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
    public void goTipoMaquina(TipoMaquina tipoMaqiuna) {
        Intent i = new Intent(context, InfoTipoMaquina.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("tipoMaquina", tipoMaqiuna);
        i.putExtra("usuario_actual", usuario_actual);
        context.startActivity(i);
    }
}
