package com.ara.amuseme.herramientas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ara.amuseme.R;
import com.ara.amuseme.administrador.InfoDeposito;
import com.ara.amuseme.modelos.Deposito;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DepositosAdapter extends RecyclerView.Adapter<DepositosViewHolder>{

    private Context context;
    private ArrayList<Deposito> depositos;
    private ArrayList<Deposito> depositosOriginal;
    private String filtro;
    private Usuario usuario_actual;

    public DepositosAdapter(Context context, ArrayList<Deposito> depositos,
                        String filtro, Usuario usuario_actual) {
        this.context = context;
        this.depositos = depositos;
        this.filtro = filtro;
        this.depositosOriginal = new ArrayList<>();
        this.depositosOriginal.addAll(depositos);
        this.usuario_actual = usuario_actual;
        this.cambiarUsuario(depositos);
        this.cambiarUsuario(depositosOriginal);
    }

    public void cambiarUsuario(ArrayList<Deposito> depositos) {
        for (Deposito d: depositos) {
            FirebaseFirestore.getInstance().collection("usuarios")
                    .whereEqualTo("id", d.getUsuario())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getDocuments().size() != 0) {
                                    String strUsuario = task.getResult().getDocuments().get(0)
                                            .getData().get("nombre").toString();
                                    d.setUsuario(strUsuario);
                                }
                            } else {
                                Toast.makeText(context, "Error obteniendo usuario.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @NonNull
    @Override
    public DepositosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DepositosViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.deposito_db, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DepositosViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.getTxtDeposito().setText(depositos.get(position).getFecha());
        switch (this.filtro.toLowerCase()){
            case "fecha":
                holder.getTxtValorFiltro().setText(depositos.get(position).getFecha());
                break;
            case "semana fiscal":
                holder.getTxtValorFiltro().setText(depositos.get(position).getSemanaFiscal());
                break;
            case "usuario":
                holder.getTxtValorFiltro().setText(depositos.get(position).getUsuario());
                break;
        }
        final int position1 = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goDeposito(depositos.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return depositos.size();
    }

    public void filtrado(String txtBuscar) {
        String filtro = this.filtro.toLowerCase();
        int len = txtBuscar.length();
        if (len == 0) {
            depositos.clear();
            depositos.addAll(depositosOriginal);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Deposito> coleccion = new ArrayList<>();
                switch (filtro){
                    case "fecha":
                        coleccion = depositos.stream().filter(i -> i.getFecha().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                    case "semana fiscal":
                        coleccion = depositos.stream().filter(i -> i.getSemanaFiscal().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                    case "usuario":
                        coleccion = depositos.stream().filter(i -> i.getUsuario().toLowerCase()
                                .contains(txtBuscar.toLowerCase())).collect(Collectors.toList());
                        break;
                }
                depositos.clear();
                depositos.addAll(coleccion);
            } else {
                for (Deposito d: depositosOriginal) {
                    if (d.getFecha().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        depositosOriginal.add(d);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void goDeposito(Deposito deposito) {
        Intent i = new Intent(context, InfoDeposito.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("deposito", deposito);
        i.putExtra("usuario_actual", usuario_actual);
        context.startActivity(i);
    }
}
