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
import com.ara.amuseme.herramientas.SpinnerAdapter;
import com.ara.amuseme.modelos.Deposito;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Depositos extends AppCompatActivity implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private RecyclerView rv_depositos;
    private Spinner spin_filter;
    private androidx.appcompat.widget.SearchView searchDeposito;
    private DepositosAdapter depositosAdapter;
    private Usuario usuario_actual;
    private ArrayList<Deposito> depositos;
    private String filtrarPor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despositos);

        rv_depositos = findViewById(R.id.rv_depositos);
        spin_filter = findViewById(R.id.spin_filter);
        searchDeposito = findViewById(R.id.search_deposito);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario_actual = getIntent().getExtras().getParcelable("usuario");
            setTitle(usuario_actual.getNombre());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Depositos.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Depositos.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        depositos = new ArrayList<>();
        searchDeposito.setOnQueryTextListener(this);

        getDepositos();

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
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Depositos.this, LoginActivity.class));
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
        depositosAdapter.filtrado(newText);
        return false;
    }

    public void getDepositos() {
        ProgressDialog progressDialog = new ProgressDialog(Depositos.this);
        progressDialog.setMessage("Obteniendo depositos...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OnCompleteListener<QuerySnapshot> listenerUsuario = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    for (DocumentSnapshot ds: task.getResult().getDocuments()){
                        String hora = ds.get("hora").toString();
                        String fecha = ds.get("fecha").toString();
                        String foto = ds.get("foto").toString();
                        String id = ds.get("id").toString();
                        String monto = ds.get("monto").toString();
                        String semanaFiscal = ds.get("semanaFiscal").toString();
                        String ubicacion = ds.get("ubicacion").toString();
                        String usuario = ds.get("usuario").toString();
                        Deposito deposito = new Deposito(hora, fecha, foto, id,
                                monto, semanaFiscal, ubicacion, usuario);
                        depositos.add(deposito);
                    }
                    crearLista();
                    progressDialog.cancel();
                }
            }
        };
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("registros_depositos")
                .orderBy("fecha", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(listenerUsuario);

    }

    public void crearLista() {

        // Select filter
        ArrayList<String> filtro = new ArrayList<>();
        filtro.add("Fecha");
        filtro.add("Semana fiscal");
        filtro.add("Usuario");

        spin_filter.setAdapter(new SpinnerAdapter(this, R.layout.spin_value, filtro));
        spin_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filtrarPor = filtro.get(spin_filter.getSelectedItemPosition());
                depositosAdapter = new DepositosAdapter(getApplicationContext(), depositos, filtrarPor, usuario_actual);
                rv_depositos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv_depositos.setAdapter(depositosAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}