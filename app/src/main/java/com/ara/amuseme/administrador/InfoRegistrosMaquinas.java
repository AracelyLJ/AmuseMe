package com.ara.amuseme.administrador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ara.amuseme.HomeEmpleado;
import com.ara.amuseme.R;
import com.ara.amuseme.modelos.RegistroMaquina;
import com.ara.amuseme.modelos.Usuario;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InfoRegistrosMaquinas extends AppCompatActivity implements View.OnClickListener{

    private RegistroMaquina registro;
    private Usuario usuario_seleccionado;
    private TextView txtUsuario, txtAlias, txtNombre, txtFecha, txtHora;
    private LinearLayout llContadores;
    private HashMap<String, EditText> editTexts;

    private Button btnDone, btnRegresar, btnEditar, btnBorrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros_maquinas_info);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtNombre = findViewById(R.id.txtNombre);
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);
        txtAlias = findViewById(R.id.txtAlias);
        llContadores = findViewById(R.id.llContadores);
        btnDone = findViewById(R.id.btnDone);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);
        editTexts = new HashMap<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            registro = getIntent().getExtras().getParcelable("registro");
            usuario_seleccionado = getIntent().getExtras().getParcelable("usuario_seleccionado");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(InfoRegistrosMaquinas.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(InfoRegistrosMaquinas.this, HomeEmpleado.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        cargarInfo();

        btnDone.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);
        btnEditar.setOnClickListener(this);
        btnBorrar.setOnClickListener(this);

    }

    public void cargarInfo() {
        txtUsuario.setText(usuario_seleccionado.getNombre());
        txtAlias.setText(registro.getAlias());
        txtNombre.setText(registro.getNombre());
        txtFecha.setText(registro.getFecha());
        txtHora.setText(registro.getHora());

        for (Map.Entry<String, String> entry: registro.getContadores().entrySet()) {
            String fotoPath = "/fotos_contadores/" + registro.getUsuario() +
                    "/"+registro.getContRegistro()+"/"+registro.getSucursal() +
                    "/" + registro.getAlias() + "_" + registro.getNombre() +
                    "_" + entry.getKey();

            TextView txtNombreContador = new TextView(this);
            EditText etxContador = new EditText(this);
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0,0,0,20);
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference(fotoPath);

            imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setMinimumWidth(1000);
            imageView.setMinimumHeight(1000);

            txtNombreContador.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            txtNombreContador.setText(entry.getKey());
            txtNombreContador.setTextColor(getResources().getColor(R.color.colorBlack));
            txtNombreContador.setTextSize(15);

            etxContador.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            etxContador.setText(entry.getValue());
            etxContador.setLayoutParams(params);
            etxContador.setEnabled(false);
            etxContador.setTextColor(getResources().getColor(R.color.colorBlack));


            try {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(InfoRegistrosMaquinas.this).load(uri).into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InfoRegistrosMaquinas.this, "No se encontraron las fotos de uno o mas registros.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            editTexts.put(entry.getKey(), etxContador);
            llContadores.addView(txtNombreContador);
            llContadores.addView(imageView);
            llContadores.addView(etxContador);
        }


    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnRegresar:
                onBackPressed();
                finish();
                break;
            case R.id.btnEditar:
                for (Map.Entry<String, EditText> et: editTexts.entrySet()) {
                    et.getValue().setEnabled(true);
                }
                btnDone.setVisibility(View.VISIBLE);
                break;
            case R.id.btnBorrar:
                Toast.makeText(this, "Opción no disponible por el momento.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDone:

                for (Map.Entry<String, EditText> et: editTexts.entrySet()) {
                    et.getValue().setEnabled(false);
                }
                btnDone.setVisibility(View.GONE);
                submitCambios();
                break;
        }
    }
    
    public void submitCambios() {
        for (Map.Entry<String, EditText> entry: editTexts.entrySet()) {
            Toast.makeText(this, entry.getKey(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, entry.getValue().getText().toString(), Toast.LENGTH_SHORT).show();
            registro.getContadores().put(entry.getKey(),entry.getValue().getText().toString());
        }

        // Actualizar registro
            // Realtime database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("registros_maquinas/" + registro.getUsuario() + "/"
                + (registro.getContRegistro())).child(registro.getNombre()).setValue(registro);
            // Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("registros_maquinas").document(usuario_seleccionado.getId()).set(registro);
        db.collection("registros_maquinas").document(registro.getUsuario()).
                collection(registro.getContRegistro()+"").document(registro.getAlias()).set(registro);
        // TODO: Registrar contadores actuales en máquina
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("contadoresActuales",contadores);
//        db.collection("maquinas").document(maquina.getId()).update(map);
    }

}