package com.ara.amuseme.administrador;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ara.amuseme.HomeEmpleado;
import com.ara.amuseme.R;
import com.ara.amuseme.modelos.Deposito;
import com.ara.amuseme.modelos.Usuario;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class InfoDeposito extends AppCompatActivity implements View.OnClickListener{

    private EditText etxtMonto, etxtUsuario, etxtFecha, etxtHora, etxtSemana, etxtUbicacion;
    private ImageView imgDeposito;
    private Button btnRegresar, btnEditar, btnBorrar, btnDone;
    private Deposito deposito;
    private Usuario usuario_actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_deposito);

        etxtMonto = findViewById(R.id.etxtMonto);
        etxtUsuario = findViewById(R.id.etxtUsuario);
        etxtFecha = findViewById(R.id.etxtFecha);
        etxtHora = findViewById(R.id.etxtHora);
        etxtSemana = findViewById(R.id.etxtSemana);
        etxtUbicacion = findViewById(R.id.etxtUbicacion);
        imgDeposito = findViewById(R.id.imgDeposito);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnDone = findViewById(R.id.btnDone);
        btnRegresar.setOnClickListener(this);
        btnEditar.setOnClickListener(this);
        btnBorrar.setOnClickListener(this);
        btnDone.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            deposito = getIntent().getExtras().getParcelable("deposito");
            usuario_actual = getIntent().getExtras().getParcelable("usuario_actual");
            setTitle(usuario_actual.getNombre());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(InfoDeposito.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(InfoDeposito.this, HomeEmpleado.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        ponerDatos();

    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnRegresar:
                Intent intent = new Intent(InfoDeposito.this, Depositos.class);
                intent.putExtra("usuario", usuario_actual);
                startActivity(intent);
                finish();
                break;
            case R.id.btnEditar:
                etxtMonto.setEnabled(true);
                etxtUsuario.setEnabled(true);
                etxtFecha.setEnabled(true);
                etxtHora.setEnabled(true);
                etxtSemana.setEnabled(true);
                etxtUbicacion.setEnabled(true);
                btnDone.setVisibility(View.VISIBLE);
                break;
            case R.id.btnBorrar:
                Toast.makeText(this, "Opción no disponible por el momento.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDone:
                etxtMonto.setEnabled(false);
                etxtUsuario.setEnabled(false);
                etxtFecha.setEnabled(false);
                etxtHora.setEnabled(false);
                etxtSemana.setEnabled(false);
                etxtUbicacion.setEnabled(false);
                btnDone.setVisibility(View.GONE);
                submitCambios();
                break;
        }
    }

    public void ponerDatos() {
        etxtMonto.setText(deposito.getMonto());
        etxtUsuario.setText(deposito.getUsuario());
        etxtFecha.setText(deposito.getFecha());
        etxtHora.setText(deposito.getHora());
        etxtSemana.setText(deposito.getSemanaFiscal());
        etxtUbicacion.setText(deposito.getUbicacion());

        Glide.with(InfoDeposito.this)
                .load(deposito.getFoto())
                .into(imgDeposito);
    }

    public void submitCambios() {
        deposito.setMonto(etxtMonto.getText().toString());
        deposito.setUsuario(etxtUsuario.getText().toString());
        deposito.setFecha(etxtFecha.getText().toString());
        deposito.setSemanaFiscal(etxtSemana.getText().toString());
        deposito.setUbicacion(etxtUbicacion.getText().toString());
        deposito.setHora(etxtHora.getText().toString());
        deposito.setUsuario(usuario_actual.getId());
        FirebaseFirestore.getInstance().collection("registros_depositos")
                .document(deposito.getId()).set(deposito);

    }

}