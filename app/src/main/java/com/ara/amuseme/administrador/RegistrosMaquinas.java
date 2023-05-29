package com.ara.amuseme.administrador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.ara.amuseme.LoginActivity;
import com.ara.amuseme.R;
import com.ara.amuseme.herramientas.DepositosAdapter;
import com.ara.amuseme.herramientas.ItemsAdapter;
import com.ara.amuseme.herramientas.SpinnerAdapter;
import com.ara.amuseme.modelos.Deposito;
import com.ara.amuseme.modelos.RegistroMaquina;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RegistrosMaquinas extends AppCompatActivity implements androidx.appcompat.widget.SearchView.OnQueryTextListener{

    private RecyclerView rv_usuarios;
    private androidx.appcompat.widget.SearchView search_usuario;
    private Usuario usuario_actual;
    private ArrayList<Usuario> usuarios;
    private Spinner spin_filter;
    private ItemsAdapter usuariosAdapter;
    private String filtrarPor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registos_maquinas);

        rv_usuarios = findViewById(R.id.rv_registros);
        search_usuario = findViewById(R.id.search_registro);
        spin_filter = findViewById(R.id.spin_filter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario_actual = getIntent().getExtras().getParcelable("usuario");
            setTitle(usuario_actual.getNombre());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrosMaquinas.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(RegistrosMaquinas.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        usuarios = new ArrayList<>();
        search_usuario.setOnQueryTextListener(this);

        getRegistros();

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
//                registrarSucursal();
                return true;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(RegistrosMaquinas.this, LoginActivity.class));
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
        return false;
    }

    public void getRegistros() {

        ProgressDialog progressDialog = new ProgressDialog(RegistrosMaquinas.this);
        progressDialog.setMessage("Obteniendo usuarios...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OnCompleteListener<QuerySnapshot> listenerUsuario = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    for (DocumentSnapshot ds: task.getResult().getDocuments()){
                        String contRegistro = ds.get("contRegistro").toString();
                        String correo = ds.get("correo").toString();
                        String id = ds.get("id").toString();
                        String maqRegSuc = ds.get("maqRegSuc").toString();
                        String nombre = ds.get("nombre").toString();
                        String porDepositar = ds.get("porDepositar").toString();
                        String pw = ds.get("pw").toString();
                        String rol = ds.get("rol").toString();
                        String status = ds.get("status").toString();
                        String sucRegistradas = ds.get("sucRegistradas").toString();
                        String sucursales = ds.get("sucursales").toString();
                        String tel = ds.get("tel").toString();
                        String token = ds.get("token").toString();
                        Usuario usuario = new Usuario(contRegistro, correo, id, maqRegSuc, nombre, porDepositar,
                                pw, rol, status, sucRegistradas, sucursales, tel, token);
                        usuarios.add(usuario);
                    }
                    crearLista();
                    progressDialog.cancel();
                }
            }
        };
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").orderBy("nombre").get()
                .addOnCompleteListener(listenerUsuario);
    }

    public void crearLista() {
        // Select filter
        ArrayList<String> filtro = new ArrayList<>();
        filtro.add("Nombre");
        filtro.add("Correo");
        filtro.add("Id");
        filtro.add("Rol");
        filtro.add("Status");
        filtro.add("Telefono");

        spin_filter.setAdapter(new SpinnerAdapter(this, R.layout.spin_value, filtro));
        spin_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filtrarPor = filtro.get(spin_filter.getSelectedItemPosition());
                usuariosAdapter = new ItemsAdapter(getApplicationContext(), usuarios, filtrarPor,"registro");
                rv_usuarios.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv_usuarios.setAdapter(usuariosAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}