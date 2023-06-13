package com.ara.amuseme.administrador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ara.amuseme.HomeAdmin;
import com.ara.amuseme.LoginActivity;
import com.ara.amuseme.R;
import com.ara.amuseme.Utils;
import com.ara.amuseme.herramientas.ItemsAdapter;
import com.ara.amuseme.herramientas.SpinnerAdapter;
import com.ara.amuseme.herramientas.TiposAdapter;
import com.ara.amuseme.modelos.Sucursal;
import com.ara.amuseme.modelos.TipoMaquina;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

public class TiposMaquinas extends AppCompatActivity implements androidx.appcompat.widget.SearchView.OnQueryTextListener{


    private RecyclerView rv_tipos;
    private Spinner spin_filter;
    private androidx.appcompat.widget.SearchView searchTipo;
    private TiposAdapter tiposAdapter;
    private Usuario usuario_actual;
    private ArrayList<TipoMaquina> tipos;
    private String filtrarPor;
    private ArrayList<String> clavesExistentes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipos_maquinas);

        rv_tipos = findViewById(R.id.rv_items);
        spin_filter = findViewById(R.id.spin_filter);
        searchTipo = findViewById(R.id.search_tipo);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario_actual = getIntent().getExtras().getParcelable("usuario");
            setTitle(usuario_actual.getNombre());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(TiposMaquinas.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(TiposMaquinas.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        tipos = new ArrayList<>();
        clavesExistentes = new ArrayList<>();
        searchTipo.setOnQueryTextListener(this);

        getTipos();

    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.btn_add:
                registrarTipoMaquina();
                return true;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(TiposMaquinas.this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        tiposAdapter.filtrado(newText);
        return false;
    }

    public void getTipos() {
        ProgressDialog progressDialog = new ProgressDialog(TiposMaquinas.this);
        progressDialog.setMessage("Obteniendo tipos de máquinas...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OnCompleteListener<QuerySnapshot> listenerUsuario = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    for (DocumentSnapshot ds: task.getResult().getDocuments()){
                        String clave = ds.get("clave").toString();
                        String contadores = ds.get("contadores").toString();
                        String id = ds.get("id").toString();
                        String nombre = ds.get("nombre").toString();
                        String observaciones = ds.get("observaciones").toString();
                        TipoMaquina tipoMaquina = new TipoMaquina(clave, contadores, id, nombre, observaciones);
                        clavesExistentes.add(clave);
                        tipos.add(tipoMaquina);
                    }
                    crearLista();
                    progressDialog.cancel();
                }
            }
        };
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tipoMaquina").orderBy("clave").get()
                .addOnCompleteListener(listenerUsuario);
    }

    public void crearLista() {

        // Select filter
        ArrayList<String> filtro = new ArrayList<>();
        filtro.add("Nombre");
        filtro.add("Clave");

        spin_filter.setAdapter(new SpinnerAdapter(this, R.layout.spin_value, filtro));
        spin_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filtrarPor = filtro.get(spin_filter.getSelectedItemPosition());
                tiposAdapter = new TiposAdapter(getApplicationContext(), tipos, filtrarPor, usuario_actual);
                rv_tipos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv_tipos.setAdapter(tiposAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void registrarTipoMaquina() {
        Dialog dialog = new Dialog(TiposMaquinas.this);
        //se asigna el layout
        dialog.setContentView(R.layout.cardview_add_tipo_maquina);

        EditText etxtNombre = dialog.findViewById(R.id.etxtNombre);
        EditText etxtClave = dialog.findViewById(R.id.etxtClave);
        EditText etxtObservaciones = dialog.findViewById(R.id.etxtObservaciones);
        EditText etxtContMultiplicador = dialog.findViewById(R.id.etxtContMultiplicador);
        Button btnAddDone = dialog.findViewById(R.id.btnAddDone);
        ImageView imgCloseDialog = dialog.findViewById(R.id.imgCloseDialog);


        btnAddDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validar datos
                String errorMsg = "Este campo no puede estar vacío.";
                if (etxtNombre.getText().toString().equals("")) etxtNombre.setError(errorMsg); else
                if (etxtClave.getText().toString().equals("")) etxtClave.setError(errorMsg); else
                if (etxtObservaciones.getText().toString().equals("")) etxtObservaciones.setError(errorMsg); else
                if (etxtContMultiplicador.getText().toString().equals("")) etxtContMultiplicador.setError(errorMsg); else
                if (etxtClave.getText().toString().length() != 2) etxtClave.setError("La clave de la máquina debe tener dos caracteres."); else
                if (clavesExistentes.contains(etxtClave.getText().toString().toUpperCase())) etxtClave.setError("Esta clave ya existe en la base de datos.");
                else {
                    String contadores = "*COINS,"+etxtContMultiplicador.getText().toString()+",1";
                    TipoMaquina nuevoTipoMaquina = new TipoMaquina();
                    nuevoTipoMaquina.setNombre(etxtNombre.getText().toString());
                    nuevoTipoMaquina.setClave(etxtClave.getText().toString().toUpperCase());
                    nuevoTipoMaquina.setObservaciones(etxtObservaciones.getText().toString());
                    nuevoTipoMaquina.setContadores(contadores);
                    nuevoTipoMaquina.setId(Utils.generateNewId(20));
                    createTipoMaquina(nuevoTipoMaquina);

                    dialog.cancel();
                }
            }
        });
        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void createTipoMaquina(TipoMaquina nuevoTipoMaquina) {

        FirebaseFirestore.getInstance().collection("tipoMaquina")
                .document(nuevoTipoMaquina.getId()).set(nuevoTipoMaquina).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                            Toast.makeText(TiposMaquinas.this, "Error al agregar nuevo usuario.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TiposMaquinas.this, "Nuevo tipo de máquina agregado.", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(TiposMaquinas.this, HomeAdmin.class);
//                            intent.putExtra("usuario", usuario_actual);
//                            startActivity(intent);
//                            finish();
                        }
                    }
                });

    }
}