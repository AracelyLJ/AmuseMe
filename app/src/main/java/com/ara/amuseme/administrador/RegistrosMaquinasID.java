package com.ara.amuseme.administrador;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.ara.amuseme.LoginActivity;
import com.ara.amuseme.R;
import com.ara.amuseme.herramientas.ItemsAdapter;
import com.ara.amuseme.modelos.RegistroMaquina;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class RegistrosMaquinasID extends AppCompatActivity implements androidx.appcompat.widget.SearchView.OnQueryTextListener{

    private RecyclerView rv_cont_ids;
    private androidx.appcompat.widget.SearchView search_cont_id;
    private Usuario usuario_seleccionado;
    private Spinner spin_filter;
    private ItemsAdapter usuariosAdapter;
    private String filtrarPor;
    private ArrayList<RegistroMaquina> registros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros_maquinas_id);

        rv_cont_ids = findViewById(R.id.rv_registros);
        search_cont_id = findViewById(R.id.search_registro);
        spin_filter = findViewById(R.id.spin_filter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario_seleccionado = getIntent().getExtras().getParcelable("usuario");
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
                startActivity(new Intent(RegistrosMaquinasID.this, LoginActivity.class));
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
//        usuariosAdapter.filtrado(s);
        return false;
    }

    public void getRegistros() {
        Toast.makeText(this, usuario_seleccionado.getId(), Toast.LENGTH_SHORT).show();
        ProgressDialog progressDialog = new ProgressDialog(RegistrosMaquinasID.this);
        progressDialog.setMessage("Obteniendo usuarios...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OnCompleteListener<DocumentSnapshot> listenerUsuario = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("DOCUMENTO", task.getResult().getData().toString());
                    Toast.makeText(RegistrosMaquinasID.this, task.getResult().getData().toString(), Toast.LENGTH_SHORT).show();
//                    crearLista();
                    progressDialog.cancel();
                }
            }
        };
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("registros_maquinas")
                .document(usuario_seleccionado.getId()).get()
                .addOnCompleteListener(listenerUsuario);
    }
}