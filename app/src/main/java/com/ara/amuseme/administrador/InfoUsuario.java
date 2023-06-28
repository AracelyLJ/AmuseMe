package com.ara.amuseme.administrador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ara.amuseme.HomeEmpleado;
import com.ara.amuseme.R;
import com.ara.amuseme.RegistrarDeposito;
import com.ara.amuseme.Utils;
import com.ara.amuseme.herramientas.ItemsAdapter;
import com.ara.amuseme.modelos.Sucursal;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfoUsuario extends AppCompatActivity implements View.OnClickListener {

    private Usuario usuario;
    private TextView txtId, txtUsuario;
    private EditText etxNombre, etxCorreo, etxTelefono, etxPorDepositar;
    private Button btnRegresar, btnEditar, btnBorrar, btnDone;
    private LinearLayout llsucAsignadas, llsucRegistradas, llmaqRegistradas;
    private Spinner spinRol;
    ArrayList<CheckBox> opcionesSucursales;
    ArrayList<CheckBox> opcionesMaquinas;
    String rol;
    ArrayList<Sucursal> sucursales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_usuario);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtId = findViewById(R.id.txtId);
        etxNombre = findViewById(R.id.etxtNombre);
        etxCorreo = findViewById(R.id.etxtCorreo);
        etxTelefono = findViewById(R.id.etxtTelefono);
        etxPorDepositar = findViewById(R.id.etxtPorDepositar);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnDone = findViewById(R.id.btnDone);
        llsucAsignadas = findViewById(R.id.llsucAsignadas);
        llsucRegistradas = findViewById(R.id.llsucRegistradas);
        llmaqRegistradas = findViewById(R.id.llmaqRegistradas);
        spinRol = findViewById(R.id.spinRol);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = getIntent().getExtras().getParcelable("usuario");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(InfoUsuario.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(InfoUsuario.this, HomeEmpleado.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        // Inicializaciones
        opcionesSucursales = new ArrayList<>();
        opcionesMaquinas = new ArrayList<>();
        sucursales = new ArrayList<>();

        rol = "";

        ponerDatos();

        getFirebaseData();
    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegresar:
                Intent intent = new Intent(InfoUsuario.this, Usuarios.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
                finish();
                break;
            case R.id.btnEditar:
                etxNombre.setEnabled(true);
//                etxCorreo.setEnabled(true);
                etxTelefono.setEnabled(true);
                spinRol.setEnabled(true);
                etxPorDepositar.setEnabled(true);
                for (CheckBox checkBox : opcionesSucursales) {
                    checkBox.setEnabled(true);
                }
                for (CheckBox checkBox : opcionesMaquinas) {
                    checkBox.setEnabled(true);
                }
                btnDone.setVisibility(View.VISIBLE);
                break;
            case R.id.btnBorrar:
                Toast.makeText(this, "Opción no disponible por el momento.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDone:
                etxNombre.setEnabled(false);
                etxCorreo.setEnabled(false);
                etxTelefono.setEnabled(false);
                spinRol.setEnabled(false);
                etxPorDepositar.setEnabled(false);
                for (CheckBox checkBox : opcionesSucursales) {
                    checkBox.setEnabled(false);
                }
                for (CheckBox checkBox : opcionesMaquinas) {
                    checkBox.setEnabled(false);
                }
                btnDone.setVisibility(View.GONE);
                submitCambios();
                break;
        }
    }

    public void ponerDatos() {
        txtUsuario.setText(usuario.getNombre());
        txtId.setText(usuario.getId());
        etxNombre.setText(usuario.getNombre());
        etxCorreo.setText(usuario.getCorreo());
        etxTelefono.setText(usuario.getTel());
        etxPorDepositar.setText(usuario.getPorDepositar());
        btnRegresar.setOnClickListener(this);
        btnEditar.setOnClickListener(this);
        btnBorrar.setOnClickListener(this);
        btnDone.setOnClickListener(this);

        // Obtener selección de rol
        int seleccion = 0;
        while (seleccion<Utils.ROLS.length && !Utils.ROLS[seleccion].equalsIgnoreCase(usuario.getRol())){
            seleccion++;
        }
            // Spinner rol
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils.ROLS);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinRol.setAdapter(adapter);
        spinRol.setSelection(seleccion);
        spinRol.setEnabled(false);
        spinRol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rol = Utils.ROLS[spinRol.getSelectedItemPosition()];
                usuario.setRol(rol.toLowerCase());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void getFirebaseData() {

        ProgressDialog progressDialog = new ProgressDialog(InfoUsuario.this);
        progressDialog.setMessage("Obteniendo datos...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OnCompleteListener<QuerySnapshot> listenerUsuario = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    sucursales = new ArrayList<>();
                    for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                        Sucursal suc = new Sucursal(
                                ds.get("clave").toString(),
                                ds.get("id").toString(),
                                ds.get("maquinas").toString(),
                                ds.get("nombre").toString(),
                                ds.get("ubicacion").toString()
                        );
                        sucursales.add(suc);
                    }
                    progressDialog.cancel();

                    // Sucursales asignadas y registradas
                    List<String> sucursalesAsignadas =
                            Arrays.asList(usuario.getSucursales().split(","));
                    List<String> sucursalesRegistradas =
                            Arrays.asList(usuario.getSucRegistradas().split(","));
                    for (Sucursal s : sucursales) {
                        if (sucursalesAsignadas.contains(s.getClave())) {
                            // Adding sucursales asignadas
                            TextView tv = new TextView(InfoUsuario.this);
                            tv.setText(s.getNombre());
                            tv.setTextColor(getResources().getColor(R.color.colorBlack));
                            llsucAsignadas.addView(tv);

                            // Adding sucursales asignadas
                            CheckBox opcionSucursales = new CheckBox(InfoUsuario.this);
                            opcionSucursales.setText(s.getNombre());
                            opcionSucursales.setTextColor(getResources().getColor(R.color.colorBlack));
                            opcionSucursales.setEnabled(false);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                opcionSucursales.setButtonTintList(ColorStateList.valueOf(getResources()
                                        .getColor(R.color.colorPrimaryDark)));
                            }
                            if (sucursalesRegistradas.contains(s.getClave())) {
                                opcionSucursales.setChecked(true);
                            }
                            opcionesSucursales.add(opcionSucursales);
                            llsucRegistradas.addView(opcionSucursales);

                            // Adding máquinas registradas
                            List<String> registrando = Arrays.asList(usuario.getMaqRegSuc().split(","));
                            String[] maqsEnSucursal = s.getMaquinas().split(",");
                            if (registrando.size() != 0 && usuario.getMaqRegSuc().length() > 0
                                    && s.getClave().equals(usuario.getMaqRegSuc().substring(0, 2))) {
                                for (String r : maqsEnSucursal) {
                                    CheckBox checkMaqs = new CheckBox(InfoUsuario.this);
                                    checkMaqs.setText(r);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        checkMaqs.setButtonTintList(ColorStateList.valueOf(getResources()
                                                .getColor(R.color.colorPrimaryDark)));
                                    }
                                    if (registrando.contains(r)) {
                                        checkMaqs.setChecked(true);
                                    }
                                    checkMaqs.setTextColor(getResources().getColor(R.color.colorBlack));
                                    checkMaqs.setEnabled(false);
                                    opcionesMaquinas.add(checkMaqs);
                                    llmaqRegistradas.addView(checkMaqs);
                                }
                            }
                        }
                    }
                }
            }
        };
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("sucursal").get()
                .addOnCompleteListener(listenerUsuario);
    }

    public void submitCambios() {
        usuario.setNombre(etxNombre.getText().toString());
        usuario.setCorreo(etxCorreo.getText().toString());
        usuario.setTel(etxTelefono.getText().toString());
        usuario.setRol(rol.toLowerCase());
        usuario.setPorDepositar(etxPorDepositar.getText().toString());
        // TODO: Editar sucursales asignadas
        StringBuilder strSucReg = new StringBuilder();
        for (CheckBox checkBox : opcionesSucursales) {
            for (Sucursal s : sucursales) {
                if (checkBox.getText().toString().equals(s.getNombre()) && checkBox.isChecked()) {
                    strSucReg.append(s.getClave()).append(",");
                    break;
                }
            }
        }
        if (strSucReg.length() > 0) {
            strSucReg.deleteCharAt(strSucReg.length() - 1);
        }
        usuario.setSucRegistradas(strSucReg.toString());

        StringBuilder strMaqReg = new StringBuilder();
        List<String> listMaqReg = new ArrayList<>();

        for (Sucursal s : sucursales) {
            for (CheckBox checkBox : opcionesMaquinas) {
                if (checkBox.isChecked() &&
                        s.getClave().equals(checkBox.getText().toString().substring(0,2))) {
                    strMaqReg.append(checkBox.getText().toString()).append(",");
                    listMaqReg.add(checkBox.getText().toString());
                }
                if (listMaqReg.size() == s.getMaquinas().split(",").length) {
                    strMaqReg = new StringBuilder();
                    if (usuario.getSucRegistradas().equals("")){
                        usuario.setSucRegistradas(s.getClave());
                    } else {
                        usuario.setSucRegistradas(usuario.getSucRegistradas() + "," + s.getClave());
                    }
                    break;
                }
            }
        }
        if (strMaqReg.length() > 0) strMaqReg.deleteCharAt(strMaqReg.length() - 1);
        usuario.setMaqRegSuc(strMaqReg.toString());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(usuario.getId()).set(usuario);
    }


}