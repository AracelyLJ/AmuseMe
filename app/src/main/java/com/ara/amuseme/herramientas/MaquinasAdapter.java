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
import com.ara.amuseme.administrador.InfoMaquina;
import com.ara.amuseme.modelos.Maquina;
import com.ara.amuseme.modelos.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MaquinasAdapter extends RecyclerView.Adapter<TiposViewHolder>{

    private Context context;
    private ArrayList<Maquina> maquinas;
    private ArrayList<Maquina> maquinasOriginal;
    private String filtro;
    private Usuario usuario_actual;

    public MaquinasAdapter(Context context, ArrayList<Maquina> maquinas,
                           String filtro, Usuario usuario_actual) {
        this.context = context;
        this.maquinas = maquinas;
        this.filtro = filtro;
        this.maquinasOriginal = new ArrayList<>();
        this.maquinasOriginal.addAll(maquinas);
        this.usuario_actual = usuario_actual;
    }

    @NonNull
    @Override
    public TiposViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TiposViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.maquina_db, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TiposViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.getTxtTipo().setText(maquinas.get(position).getAlias());
        switch (this.filtro.toLowerCase()){
            case "alias":
                holder.getTxtValorFiltro().setText(maquinas.get(position).getAlias());
                break;
            case "nombre":
                holder.getTxtValorFiltro().setText(maquinas.get(position).getNombre());
                break;
        }
        final int position1 = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goMaquinas(maquinas.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return maquinas.size();
    }

    public void filtrado(String txtBuscar) {
        String filtro = this.filtro.toLowerCase();
        int len = txtBuscar.length();
        if (len == 0) {
            maquinas.clear();
            maquinas.addAll(maquinasOriginal);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Maquina> coleccion = new ArrayList<>();
                switch (filtro){
                    case "alias":
                        coleccion = maquinas.stream().filter(i -> i.getAlias().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                    case "nombre":
                        coleccion = maquinas.stream().filter(i -> i.getNombre().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                }
                maquinas.clear();
                maquinas.addAll(coleccion);
            } else {
                for (Maquina tm: maquinasOriginal) {
                    if (tm.getNombre().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        maquinas.add(tm);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void goMaquinas(Maquina maquina) {
        Intent i = new Intent(context, InfoMaquina.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("maquina", maquina);
        i.putExtra("usuario_actual", usuario_actual);
        context.startActivity(i);
    }
}
