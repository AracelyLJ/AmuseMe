package com.ara.amuseme.administrador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ara.amuseme.LoginActivity;
import com.ara.amuseme.R;
import com.ara.amuseme.herramientas.DepositosAdapter;
import com.ara.amuseme.herramientas.ItemsAdapter;
import com.ara.amuseme.herramientas.RegistrosMaquinasAdapter;
import com.ara.amuseme.herramientas.SpinnerAdapter;
import com.ara.amuseme.modelos.RegistroMaquina;
import com.ara.amuseme.modelos.Usuario;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class RegistrosMaquinasID extends AppCompatActivity implements androidx.appcompat.widget.SearchView.OnQueryTextListener{

    private RecyclerView rv_cont_ids;
    private androidx.appcompat.widget.SearchView search_cont_id;
    private Usuario usuario_seleccionado;
    private Spinner spin_filter;
    private ItemsAdapter usuariosAdapter;
    private String filtrarPor;

    ArrayList<RegistroMaquina> registros;
    private RegistrosMaquinasAdapter registrosAdapter;
    private RecyclerView rv_registros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros_maquinas_id);

        rv_registros = findViewById(R.id.rv_registros);
        search_cont_id = findViewById(R.id.search_registro);
        spin_filter = findViewById(R.id.spin_filter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario_seleccionado = getIntent().getExtras().getParcelable("usuarioSeleccionado");
            setTitle(usuario_seleccionado.getNombre());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrosMaquinasID.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(RegistrosMaquinasID.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

         registros = new ArrayList<>();
        registrosAdapter = new RegistrosMaquinasAdapter(getApplicationContext(), registros, filtrarPor, usuario_seleccionado);
        search_cont_id.setOnQueryTextListener(this);

        getRegistros();

    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.btn_add:
//                registrarSucursal();
                return true;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(RegistrosMaquinasID.this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        registrosAdapter.filtrado(s);
        return false;
    }

    public void getRegistros() {
        ProgressDialog progressDialog = new ProgressDialog(RegistrosMaquinasID.this);
        progressDialog.setMessage("Obteniendo datos...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OnCompleteListener<DataSnapshot> listenerUsuario = new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {

                    for (DataSnapshot ds: task.getResult().getChildren()) {
                        for (DataSnapshot ds1: ds.getChildren()) {
                            RegistroMaquina registroMaquina = new RegistroMaquina();
                            HashMap<String, String> contadores = new HashMap<>();
                            for (DataSnapshot ds2: ds1.getChildren()) {
                                if (ds2.getKey().startsWith("*")) {
                                    contadores.put(ds2.getKey(), ds2.getValue().toString());
                                }
                                try {
                                    switch (ds2.getKey()) {
                                        case "alias":
                                            registroMaquina.setAlias(ds2.getValue().toString());
                                            break;
                                        case "contRegistro":
                                            registroMaquina.setContRegistro(ds2.getValue().toString());
                                            break;
                                        case "fecha":
                                            registroMaquina.setFecha(ds2.getValue().toString());
                                            break;
                                        case "hora":
                                            registroMaquina.setHora(ds2.getValue().toString());
                                            break;
                                        case "nombre":
                                            registroMaquina.setNombre(ds2.getValue().toString());
                                            break;
                                        case "semanaFiscal":
                                            registroMaquina.setSemanaFiscal(ds2.getValue().toString());
                                            break;
                                        case "sucursal":
                                            registroMaquina.setSucursal(ds2.getValue().toString());
                                            break;
                                        case "tipoMaquina":
                                            registroMaquina.setTipoMaquina(ds2.getValue().toString());
                                            break;
                                        case "ubicacion":
                                            registroMaquina.setUbicacion(ds2.getValue().toString());
                                            break;
                                        case "usuario":
                                            registroMaquina.setUsuario(ds2.getValue().toString());
                                            break;
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(RegistrosMaquinasID.this," Error obteniendo registros", Toast.LENGTH_SHORT).show();
                                }
                            }
                            registroMaquina.setContadores(contadores);
                            registros.add(registroMaquina);
                        }
                    }
                    crearLista();
                    progressDialog.cancel();
                }
            }
        };

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference("registros_maquinas/"+usuario_seleccionado.getId())
                        .get().addOnCompleteListener(listenerUsuario);
    }

    public void crearLista() {
        // Select filter
        ArrayList<String> filtro = new ArrayList<>();
        filtro.add("Fecha");
        filtro.add("Alias");
        filtro.add("ID Contador");
        filtro.add("Semana Fiscal");

        spin_filter.setAdapter(new SpinnerAdapter(this, R.layout.spin_value, filtro));
        spin_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filtrarPor = filtro.get(spin_filter.getSelectedItemPosition());
                registrosAdapter = new RegistrosMaquinasAdapter(getApplicationContext(), registros, filtrarPor, usuario_seleccionado);
                rv_registros.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv_registros.setAdapter(registrosAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}