package com.ara.amuseme.administrador;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ara.amuseme.HomeAdmin;
import com.ara.amuseme.HomeEmpleado;
import com.ara.amuseme.LoginActivity;
import com.ara.amuseme.R;
import com.ara.amuseme.RegistrarContadores;
import com.ara.amuseme.RegistrarVisita;
import com.ara.amuseme.Utils;
import com.ara.amuseme.herramientas.MaquinasAdapter;
import com.ara.amuseme.herramientas.SpinnerAdapter;
import com.ara.amuseme.herramientas.SucursalesAdapter;
import com.ara.amuseme.modelos.Maquina;
import com.ara.amuseme.modelos.Sucursal;
import com.ara.amuseme.modelos.TipoMaquina;
import com.ara.amuseme.modelos.Usuario;
import com.ara.amuseme.servicios.QRCodeReader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Maquinas extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private RecyclerView rv_maquinas;
    private Spinner spin_filter;
    private androidx.appcompat.widget.SearchView searchTipo;
    private Usuario usuario_actual;
    private ArrayList<Maquina> maquinas;
    private String filtrarPor;
    private MaquinasAdapter maquinasAdapter;
    private ArrayList<Sucursal> sucursales;
    private ArrayList<TipoMaquina> tiposMaquinas;
    private Sucursal sucSeleccionada;
    private TipoMaquina tipoMaqSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maquinas);

        rv_maquinas = findViewById(R.id.rv_maquinas);
        spin_filter = findViewById(R.id.spin_filter);
        searchTipo = findViewById(R.id.search_tipo);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario_actual = getIntent().getExtras().getParcelable("usuario");
            setTitle(usuario_actual.getNombre());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Maquinas.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Maquinas.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        maquinas = new ArrayList<>();
        sucursales = new ArrayList<>();
        tiposMaquinas = new ArrayList<>();

        searchTipo.setOnQueryTextListener(this);

        getMaquinas();

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
                registrarMaquina();
                return true;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Maquinas.this, LoginActivity.class));
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
        maquinasAdapter.filtrado(newText);
        return false;
    }


    public void getMaquinas() {
        ProgressDialog progressDialog = new ProgressDialog(Maquinas.this);
        progressDialog.setMessage("Obteniendo sucursales...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        OnCompleteListener<QuerySnapshot> listenerMaquina = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot  > task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    for (DocumentSnapshot ds: task.getResult().getDocuments()) {

                        String contadoresActuales = "";
                        String alias = "";
                        String id = "";
                        String imagen = "";
                        String nombre = "";
                        String observaciones = "";
                        String renta = "";
                        try {
                            alias = ds.get("alias").toString();
                            id = ds.get("id").toString();
                            imagen = ds.get("imagen").toString();
                            nombre = ds.get("nombre").toString();
                            observaciones = ds.get("observaciones").toString();
                            renta = ds.get("renta").toString();
                            contadoresActuales = ds.get("contadoresActuales").toString();
                        } catch (Exception e) {

                        }
                        Maquina maquina = new Maquina(alias, id, imagen, nombre, observaciones, renta, contadoresActuales);
                        maquinas.add(maquina);
                    }
                    crearLista();
                }
            }
        };
        db.collection("maquinas").orderBy("alias")
                .get()
                .addOnCompleteListener(listenerMaquina);
        OnCompleteListener<QuerySnapshot> listenerSucursal = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot  > task) {
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
                    progressDialog.cancel();
                }
            }
        };
        db.collection("sucursal").orderBy("clave")
                .get()
                .addOnCompleteListener(listenerSucursal);
        OnCompleteListener<QuerySnapshot> listenerTipos = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot  > task) {
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
                        tiposMaquinas.add(tipoMaquina);
                    }
                    progressDialog.cancel();
                }
            }
        };
        db.collection("tipoMaquina").orderBy("clave")
                .get()
                .addOnCompleteListener(listenerTipos);
    }

    public void registrarMaquina() {
        Dialog dialog = new Dialog(Maquinas.this);
        //se asigna el layout
        dialog.setContentView(R.layout.cardview_add_maquina);

        EditText etxtAddNombre = dialog.findViewById(R.id.etxtAddNombre);
        EditText etxtAddRenta = dialog.findViewById(R.id.etxtAddRenta);
        EditText etxtAddObservaciones = dialog.findViewById(R.id.etxtAddObservaciones);
        Spinner spin_sucursal = dialog.findViewById(R.id.spin_sucursal);
        Spinner spin_tipo_maquina = dialog.findViewById(R.id.spin_tipo_maquina);
        Button btnAddDone = dialog.findViewById(R.id.btnAddDone);
        ImageView imgCloseDialog = dialog.findViewById(R.id.imgCloseDialog);

        ArrayList<String> strSucursales = new ArrayList<>();
        ArrayList<String> strTipos = new ArrayList<>();
        for (Sucursal s: sucursales) strSucursales.add(s.getClave() + " - " + s.getNombre());
        for (TipoMaquina t: tiposMaquinas) strTipos.add(t.getClave() + " - " + t.getNombre());
        etxtAddNombre.setText(Utils.generateNewId(8).toUpperCase());
        etxtAddNombre.setEnabled(false);

        spin_sucursal.setAdapter(new SpinnerAdapter(this, android.R.layout.simple_list_item_1, strSucursales));
        spin_sucursal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sucSeleccionada = sucursales.get(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spin_tipo_maquina.setAdapter(new SpinnerAdapter(this, android.R.layout.simple_list_item_1, strTipos));
        spin_tipo_maquina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tipoMaqSeleccionada = tiposMaquinas.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnAddDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validar datos
                String errorMsg = "Este campo no puede estar vacío.";
                String errorMsg2 = "El nombre de la nueva máquina debe tener exactamente 8 caracteres.";
                if (etxtAddNombre.getText().toString().equals("")) etxtAddNombre.setError(errorMsg); else
                if (etxtAddRenta.getText().toString().equals("")) etxtAddRenta.setError(errorMsg); else
                if (etxtAddNombre.getText().toString().length() != 8) etxtAddNombre.setError(errorMsg2);
                else {
                    // Agregar nombre a la nueva máquina
                    String nuevaMaquina = sucSeleccionada.getClave()+tipoMaqSeleccionada.getClave();
                    ArrayList<String> numsExistentes = new ArrayList<>();
                    for (Maquina m: maquinas) {
                        if (m.getAlias().substring(0,4).equals(nuevaMaquina)) {
                            numsExistentes.add(m.getAlias().substring(4,6));
                        }
                    }
                    String stri = "";
                    for (int i = 1; i<=99; i++){
                        if (i<10) stri = "0" + i;
                        else stri = String.valueOf(i);
                        if (!numsExistentes.contains(stri)) break;
                    }
                    nuevaMaquina += stri;

                    // Agregar contadores
                    int cont = 0;
                    String [] contadores = tipoMaqSeleccionada.getContadores().split(",");



                    mostrarMensajeFinal("Máquina: " + nuevaMaquina + " agregada.");

                    tiposMaquinas.get(0).getContadores();
                    String nombre = etxtAddNombre.getText().toString();
                    String id = Utils.generateNewId(20);
                    Maquina maquina = new Maquina(nuevaMaquina, id, "", nombre,
                            etxtAddObservaciones.getText().toString(), etxtAddRenta.getText().toString(), "");

                    String sucMaquinas = sucSeleccionada.getMaquinas() + "," + nuevaMaquina;
                    Map<String, Object> mapMaquinas = new HashMap<>();
                    mapMaquinas.put("maquinas",sucMaquinas);

                    FirebaseFirestore.getInstance().collection("maquinas").document(maquina.getId()).set(maquina);
                    FirebaseFirestore.getInstance().collection("sucursal")
                            .document(sucSeleccionada.getId()).update(mapMaquinas);
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

    public void crearLista() {

        // Select filter
        ArrayList<String> filtro = new ArrayList<>();
        filtro.add("Alias");
        filtro.add("Nombre");

        spin_filter.setAdapter(new SpinnerAdapter(this, R.layout.spin_value, filtro));
        spin_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filtrarPor = filtro.get(spin_filter.getSelectedItemPosition());
                maquinasAdapter = new MaquinasAdapter(getApplicationContext(), maquinas, filtrarPor, usuario_actual);
                rv_maquinas.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv_maquinas.setAdapter(maquinasAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void mostrarMensajeFinal(String mensaje) {
        Dialog dialog = new Dialog(Maquinas.this);
        //se asigna el layout
        dialog.setContentView(R.layout.cardview_message);
        // Editar texto
        TextView mensajeFinal = dialog.findViewById(R.id.mensajeFinal);
        ImageView imgCloseDialog = dialog.findViewById(R.id.imgCloseDialog);
        Button sucDoneButton = dialog.findViewById(R.id.sucDoneButton);
        sucDoneButton.setText("OK");
        mensajeFinal.setText(mensaje);

        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        sucDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(Maquinas.this, HomeAdmin.class);
                intent.putExtra("usuario", usuario_actual);
                startActivity(intent);
            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}