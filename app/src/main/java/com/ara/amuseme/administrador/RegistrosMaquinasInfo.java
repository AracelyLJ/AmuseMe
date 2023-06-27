package com.ara.amuseme.administrador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class RegistrosMaquinasInfo extends AppCompatActivity {

    private RegistroMaquina registro;
    private Usuario usuario_seleccionado;
    private TextView txtUsuario, txtAlias, txtNombre, txtFecha, txtHora;
    private LinearLayout llContadores;

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            registro = getIntent().getExtras().getParcelable("registro");
            usuario_seleccionado = getIntent().getExtras().getParcelable("usuario_seleccionado");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrosMaquinasInfo.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(RegistrosMaquinasInfo.this, HomeEmpleado.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        cargarInfo();

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

            imageView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));

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
                        Glide.with(RegistrosMaquinasInfo.this).load(uri).into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrosMaquinasInfo.this, "No se encontraron las fotos de uno o mas registros.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            llContadores.addView(txtNombreContador);
            llContadores.addView(imageView);
            llContadores.addView(etxContador);
        }


    }
}