package com.ara.amuseme.administrador;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ara.amuseme.HomeEmpleado;
import com.ara.amuseme.R;
import com.ara.amuseme.modelos.Sucursal;
import com.ara.amuseme.modelos.Usuario;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoSucursales extends AppCompatActivity implements View.OnClickListener {

    private Sucursal sucursal;
    private TextView txtSucursal;
    private EditText etxtNombre, etxtClave;
    private Button btnDone, btnRegresar, btnEditar, btnBorrar;
    private Usuario usuario_actual;
    private LinearLayout llMaquinas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_sucursales);

        txtSucursal = findViewById(R.id.txtSucursal);
        etxtNombre = findViewById(R.id.etxtNombre);
        etxtClave = findViewById(R.id.etxtClave);
        llMaquinas = findViewById(R.id.llMaquinas);
        btnDone = findViewById(R.id.btnDone);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sucursal = getIntent().getExtras().getParcelable("sucursal");
            usuario_actual = getIntent().getExtras().getParcelable("usuario_actual");
            setTitle(usuario_actual.getNombre());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(InfoSucursales.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(InfoSucursales.this, HomeEmpleado.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }


        ponerDatos();

    }

    public void ponerDatos() {
        txtSucursal.setText(sucursal.getNombre());
        etxtNombre.setText(sucursal.getNombre());
        etxtClave.setText(sucursal.getClave());

        String maqs[] = sucursal.getMaquinas().split(",");
        for (String m: maqs) {

        }

        String sucs[] = sucursal.getMaquinas().split(",");
        for (String s: sucs) {
            TextView tv = new TextView(this);
            tv.setText(s);
            llMaquinas.addView(tv);
        }
        btnDone.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);
        btnEditar.setOnClickListener(this);
        btnBorrar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnRegresar:
                Intent intent = new Intent(InfoSucursales.this, Sucursales.class);
                intent.putExtra("usuario", usuario_actual);
                startActivity(intent);
                finish();
                break;
            case R.id.btnEditar:
                etxtNombre.setEnabled(true);
                btnDone.setVisibility(View.VISIBLE);
                break;
            case R.id.btnBorrar:
                Toast.makeText(this, "Opción no disponible por el momento.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDone:
                etxtClave.setEnabled(false);
                etxtNombre.setEnabled(false);
                btnDone.setVisibility(View.GONE);
                submitCambios();
                break;
        }
    }

    public void submitCambios(){
        String errorMsg = "Este campo no debe estar vacío";
        if (etxtClave.getText().toString().equals("")) etxtClave.setError(errorMsg); else
        if (etxtNombre.getText().toString().equals("")) etxtNombre.setError(errorMsg); else
        {
            sucursal.setClave(etxtClave.getText().toString());
            sucursal.setNombre(etxtNombre.getText().toString());
            FirebaseFirestore.getInstance().collection("sucursal")
                    .document(sucursal.getId()).set(sucursal);
        }
    }

}