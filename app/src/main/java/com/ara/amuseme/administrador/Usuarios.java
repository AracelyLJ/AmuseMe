package com.ara.amuseme.administrador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ara.amuseme.HomeEmpleado;
import com.ara.amuseme.LoginActivity;
import com.ara.amuseme.R;
import com.ara.amuseme.herramientas.ItemsAdapter;
import com.ara.amuseme.herramientas.SpinnerAdapter;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Usuarios extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private Usuario usuario_actual;
    private ArrayList<Usuario> usuarios;
    private RecyclerView rv_items;
//    private EditText etxt_busqueda;
    private Spinner spin_filter;
    private String filtrarPor;
    private SearchView searchUsuario;
    private ItemsAdapter usuariosAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        rv_items = findViewById(R.id.rv_items);
        spin_filter = findViewById(R.id.spin_filter);
        searchUsuario = findViewById(R.id.search_suario);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario_actual = getIntent().getExtras().getParcelable("usuario");
            setTitle(usuario_actual.getNombre());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Usuarios.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Usuarios.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        usuarios = new ArrayList<>();

        searchUsuario.setOnQueryTextListener(this);
        get_usuarios();
    }

    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.btnUsuarios:
                break;
        }

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
                Toast.makeText(this, "Agregar item no implementado", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Usuarios.this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void get_usuarios() {
        ProgressDialog progressDialog = new ProgressDialog(Usuarios.this);
        progressDialog.setMessage("Obteniendo datos...");
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

        Toast.makeText(this, "Crear lista", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(Usuarios.this, filtrarPor, Toast.LENGTH_SHORT).show();
                usuariosAdapter = new ItemsAdapter(getApplicationContext(), usuarios, filtrarPor);
                rv_items.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv_items.setAdapter(usuariosAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public ArrayList<String> listUsuarioBy(String campo) {
        ArrayList<String> lista = new ArrayList<>();

        switch (campo) {
            case "Nombre":
                for (Usuario u: usuarios) { lista.add(u.getNombre()); }
                break;
            case "Correo":
                for (Usuario u: usuarios) { lista.add(u.getCorreo()); }
                break;
            case "Id":
                for (Usuario u: usuarios) { lista.add(u.getId()); }
                break;
            case "Rol":
                for (Usuario u: usuarios) { lista.add(u.getRol()); }
                break;
            case "Status":
                for (Usuario u: usuarios) { lista.add(u.getStatus()); }
                break;
            case "Teléfono":
                for (Usuario u: usuarios) { lista.add(u.getTel()); }
                break;
            default:
                break;
        }
        return lista;
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        usuariosAdapter.filtrado(s);
        return false;
    }
}