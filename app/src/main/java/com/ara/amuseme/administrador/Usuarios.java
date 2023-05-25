package com.ara.amuseme.administrador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ara.amuseme.HomeAdmin;
import com.ara.amuseme.HomeEmpleado;
import com.ara.amuseme.LoginActivity;
import com.ara.amuseme.R;
import com.ara.amuseme.RegistrarContadores;
import com.ara.amuseme.RegistrarDeposito;
import com.ara.amuseme.Utils;
import com.ara.amuseme.herramientas.ItemsAdapter;
import com.ara.amuseme.herramientas.SpinnerAdapter;
import com.ara.amuseme.modelos.Sucursal;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Usuarios extends AppCompatActivity implements androidx.appcompat.widget.SearchView.OnQueryTextListener{

    private Usuario usuario_actual;
    private ArrayList<Usuario> usuarios;
    private ArrayList<Sucursal> sucursales;
    private ArrayList<CheckBox> opcionesSucursales;
    private RecyclerView rv_items;
//    private EditText etxt_busqueda;
    private Spinner spin_filter;
    private String filtrarPor;
    private androidx.appcompat.widget.SearchView searchUsuario;
    private ItemsAdapter usuariosAdapter;
    private Usuario nuevoUsuario;
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
        nuevoUsuario = new Usuario();
        sucursales = new ArrayList<>();
        opcionesSucursales = new ArrayList<>();

        searchUsuario.setOnQueryTextListener(this);
        get_usuarios();
        get_sucursales();
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
                registrarUsuario();
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

    public  void get_sucursales() {
        ProgressDialog progressDialog = new ProgressDialog(Usuarios.this);
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
                usuariosAdapter = new ItemsAdapter(getApplicationContext(), usuarios, filtrarPor);
                rv_items.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv_items.setAdapter(usuariosAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

    public void registrarUsuario() {
        Dialog dialog = new Dialog(Usuarios.this);
        //se asigna el layout
        dialog.setContentView(R.layout.cardview_add_user);
        // Editar texto
        EditText etxtAddNombre = dialog.findViewById(R.id.etxtAddNombre);
        EditText etxtAddCorreo = dialog.findViewById(R.id.etxtAddCorreo);
        EditText etxtAddContrasena = dialog.findViewById(R.id.etxtAddContrasena);
        EditText etxtAddTelefono = dialog.findViewById(R.id.etxtAddTelefono);
        Spinner spinAddRol = dialog.findViewById(R.id.spinAddRol);
        LinearLayout llAddSucAsignadas = dialog.findViewById(R.id.llAddSucAsignadas);
        Button btnAddDone = dialog.findViewById(R.id.btnAddDone);
        ImageView imgCloseDialog = dialog.findViewById(R.id.imgCloseDialog);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils.ROLS);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinAddRol.setAdapter(adapter);
        spinAddRol.setSelection(0);
        spinAddRol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        for (Sucursal s: sucursales) {
            CheckBox checkMaqs = new CheckBox(Usuarios.this);
            checkMaqs.setText(s.getNombre());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkMaqs.setButtonTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.colorPrimaryDark)));
            }
            checkMaqs.setTextColor(getResources().getColor(R.color.colorBlack));
            opcionesSucursales.add(checkMaqs);
            llAddSucAsignadas.addView(checkMaqs);
        }
        btnAddDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validar datos
                String errorMsg = "Este campo no puede estar vacío.";
                if (etxtAddNombre.getText().toString().equals("")) etxtAddNombre.setError(errorMsg); else
                if (etxtAddCorreo.getText().toString().equals("")) etxtAddCorreo.setError(errorMsg); else
                if (etxtAddContrasena.getText().toString().equals("")) etxtAddContrasena.setError(errorMsg); else
                if (etxtAddTelefono.getText().toString().equals("")) etxtAddNombre.setError(errorMsg);
                else {
                    nuevoUsuario.setContRegistro("0");
                    nuevoUsuario.setNombre(etxtAddNombre.getText().toString());
                    nuevoUsuario.setCorreo(etxtAddCorreo.getText().toString());
                    nuevoUsuario.setPw(etxtAddContrasena.getText().toString());
                    nuevoUsuario.setTel(etxtAddTelefono.getText().toString());
                    nuevoUsuario.setRol(Utils.ROLS[spinAddRol.getSelectedItemPosition()]);
                    nuevoUsuario.setPorDepositar("0");
                    StringBuilder sucAsignadas = new StringBuilder();
                    for (CheckBox checkBox: opcionesSucursales) {
                        if (checkBox.isChecked()) {
                            for (Sucursal s: sucursales) {
                                if (s.getNombre().equals(checkBox.getText().toString())) {
                                    sucAsignadas.append(s.getClave()).append(",");
                                }
                            }
                        }
                    }
                    if (sucAsignadas.length() > 0) sucAsignadas.deleteCharAt(sucAsignadas.length() - 1);
                    nuevoUsuario.setSucursales(sucAsignadas.toString());

                    createUser(nuevoUsuario.getCorreo(), nuevoUsuario.getPw());

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


    public void createUser(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = task.getResult().getUser().getUid();
                            nuevoUsuario.setId(id);
                            resetSession(usuario_actual.getCorreo(), usuario_actual.getPw());
                            mensajeFinal(nuevoUsuario);
                            Log.d("Usuario", "Usuario creado con éxito.");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Usuario", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Usuarios.this, "No se pudo agregar el usuario.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void resetSession(String correo, String pw) {
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, pw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            OnCompleteListener<DocumentSnapshot> listenerUsuario = new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(Task<DocumentSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    }
                                }
                            };
                        } else {
                            // Error al resetear sesion
                            Toast.makeText(Usuarios.this, "Error al resetear sesión",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void mensajeFinal(Usuario u) {

        Dialog dialog = new Dialog(Usuarios.this);
        //se asigna el layout
        dialog.setContentView(R.layout.cardview_message);
        // Editar texto
        TextView mensajeFinal = dialog.findViewById(R.id.mensajeFinal);
        ImageView imgCloseDialog = dialog.findViewById(R.id.imgCloseDialog);
        Button sucDoneButton = dialog.findViewById(R.id.sucDoneButton);
        mensajeFinal.setText("El usuario "+u.getNombre()+" se a agregado a la base de datos con éxito.");
        sucDoneButton.setText("OK");
        imgCloseDialog.setVisibility(View.GONE);
        sucDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore.getInstance().collection("usuarios")
                        .document(u.getId()).set(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                    Toast.makeText(Usuarios.this, "Error al agregar nuevo usuario.", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.cancel();
                                    Intent intent = new Intent(Usuarios.this, HomeAdmin.class);
                                    intent.putExtra("usuario", usuario_actual);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    
}