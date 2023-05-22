package com.ara.amuseme.administrador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ara.amuseme.LoginActivity;
import com.ara.amuseme.R;
import com.ara.amuseme.herramientas.SpinnerAdapter;
import com.ara.amuseme.herramientas.SucursalesAdapter;
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

public class Sucursales extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView rv_tipos;
    private Spinner spin_filter;
    private androidx.appcompat.widget.SearchView searchTipo;
    private Usuario usuario_actual;
    private ArrayList<Sucursal> sucursales;
    private ArrayList<String> sucursalesExistentes;
    private String filtrarPor;
    private SucursalesAdapter sucursalesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucursales);

        rv_tipos = findViewById(R.id.rv_items);
        spin_filter = findViewById(R.id.spin_filter);
        searchTipo = findViewById(R.id.search_tipo);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario_actual = getIntent().getExtras().getParcelable("usuario");
            setTitle(usuario_actual.getNombre());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Sucursales.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Sucursales.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        sucursales = new ArrayList<>();
        sucursalesExistentes = new ArrayList<>();

        searchTipo.setOnQueryTextListener(this);

        getSucursales();

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
                registrarSucursal();
                return true;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Sucursales.this, LoginActivity.class));
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
        sucursalesAdapter.filtrado(newText);
        return false;
    }

    public void getSucursales() {
        ProgressDialog progressDialog = new ProgressDialog(Sucursales.this);
        progressDialog.setMessage("Obteniendo sucursales...");
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
                        String id = ds.get("id").toString();
                        String maquinas = ds.get("maquinas").toString();
                        String nombre = ds.get("nombre").toString();
                        String ubicacion = ds.get("ubicacion").toString();

                        Sucursal sucursal = new Sucursal(clave, id, maquinas, nombre, ubicacion);
                        sucursales.add(sucursal);
                        sucursalesExistentes.add(clave);
                    }
                    crearLista();
                    progressDialog.cancel();
                }
            }
        };
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("sucursal").orderBy("clave").get()
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
                sucursalesAdapter = new SucursalesAdapter(getApplicationContext(), sucursales, filtrarPor, usuario_actual);
                rv_tipos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv_tipos.setAdapter(sucursalesAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void registrarSucursal() {
        Dialog dialog = new Dialog(Sucursales.this);
        //se asigna el layout
        dialog.setContentView(R.layout.cardview_add_sucursal);

        EditText etxtNombre = dialog.findViewById(R.id.etxtAddNombre);
        EditText etxtClave = dialog.findViewById(R.id.etxtClave);
        Button btnAddDone = dialog.findViewById(R.id.btnAddDone);
        ImageView imgCloseDialog = dialog.findViewById(R.id.imgCloseDialog);


        btnAddDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validar datos
                String errorMsg = "Este campo no puede estar vacío.";
                if (etxtNombre.getText().toString().equals("")) etxtNombre.setError(errorMsg); else
                if (etxtClave.getText().toString().equals("")) etxtClave.setError(errorMsg); else
                if (etxtClave.getText().toString().length() != 2) etxtClave.setError("La clave de la máquina debe tener dos caracteres."); else
                if (sucursalesExistentes.contains(etxtClave.getText().toString().toUpperCase())) etxtClave.setError("Esta clave ya existe en la base de datos.");
                else {
                    String clave = etxtClave.getText().toString();
                    String id = generateNewId();
                    String nombre = etxtNombre.getText().toString();
                    String maquinas = "";
                    String ubicacion = "";
                    Sucursal nueva_sucursal = new Sucursal(clave, id, maquinas, nombre, ubicacion);
                    createTipoMaquina(nueva_sucursal);

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

    public void createTipoMaquina(Sucursal sucursal) {
        FirebaseFirestore.getInstance().collection("sucursal")
                .document(sucursal.getId()).set(sucursal).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                            Toast.makeText(Sucursales.this, "Error al agregar nuevo usuario.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Sucursales.this, "Nuevo tipo de máquina agregado.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public String generateNewId() {
        Random rand = new Random();

        String newId = "12345678901234567890";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            newId = rand.ints(48, 123)
                    .filter(num -> (num<58 || num>64) && (num<91 || num>96))
                    .limit(20)
                    .mapToObj(c -> (char)c).collect(StringBuffer::new, StringBuffer::append,
                            StringBuffer::append)
                    .toString();
        }
        return newId;
    }
}