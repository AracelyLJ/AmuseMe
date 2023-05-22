package com.ara.amuseme.administrador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ara.amuseme.HomeEmpleado;
import com.ara.amuseme.R;
import com.ara.amuseme.modelos.TipoMaquina;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoTipoMaquina extends AppCompatActivity implements View.OnClickListener {

    private TipoMaquina tipoMaquina;
    private EditText etxtNombre, etxtClave, etxtObservaciones;
    private Button btnDone, btnRegresar, btnEditar, btnBorrar;
    private TextView txtTipo;
    private Usuario usuario_actual;
    private TableLayout table;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tipo_maquina);

        etxtNombre = findViewById(R.id.etxtNombre);
        etxtClave = findViewById(R.id.etxtClave);
        etxtObservaciones = findViewById(R.id.etxtObservaciones);
        btnDone = findViewById(R.id.btnDone);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);
        txtTipo = findViewById(R.id.txtTipo);
        table = findViewById(R.id.table);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tipoMaquina = getIntent().getExtras().getParcelable("tipoMaquina");
            usuario_actual = getIntent().getExtras().getParcelable("usuario_actual");
            setTitle(usuario_actual.getNombre());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(InfoTipoMaquina.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(InfoTipoMaquina.this, HomeEmpleado.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        ponerDatos();

    }

    public void ponerDatos() {
        txtTipo.setText(tipoMaquina.getNombre());
        etxtNombre.setText(tipoMaquina.getNombre());
        etxtClave.setText(tipoMaquina.getClave());
        etxtObservaciones.setText(tipoMaquina.getObservaciones());

        String conts[] = tipoMaquina.getContadores().split(",");
        for (int c = 0; c < conts.length; c+=3) {
            TableRow tr = new TableRow(this);
            TextView tv = new TextView(this);
            TextView tv2 = new TextView(this);
            tv.setGravity(Gravity.CENTER);
            tv2.setGravity(Gravity.CENTER);
            tv.setTypeface(null, Typeface.BOLD);
            tv2.setTypeface(null, Typeface.BOLD);
            tv.setTextColor(getResources().getColor(R.color.colorBlack));
            tv2.setTextColor(getResources().getColor(R.color.colorBlack));
            tv.setText(conts[c].replace("*","").toUpperCase());
            tv2.setText(conts[c+1].toUpperCase());
            tr.addView(tv);
            tr.addView(tv2);
            table.addView(tr);
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
                Intent intent = new Intent(InfoTipoMaquina.this, TiposMaquinas.class);
                intent.putExtra("usuario", usuario_actual);
                startActivity(intent);
                finish();
                break;
            case R.id.btnEditar:
                etxtClave.setEnabled(true);
                etxtNombre.setEnabled(true);
                etxtObservaciones.setEnabled(true);
                btnDone.setVisibility(View.VISIBLE);
                break;
            case R.id.btnBorrar:
                Toast.makeText(this, "Opción no disponible por el momento.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDone:
                etxtClave.setEnabled(false);
                etxtNombre.setEnabled(false);
                etxtObservaciones.setEnabled(false);
                btnDone.setVisibility(View.GONE);
                submitCambios();
                break;
        }
    }

    public void submitCambios(){
        String errorMsg = "Este campo no debe estar vacío";
        if (etxtClave.getText().toString().equals("")) etxtClave.setError(errorMsg); else
        if (etxtNombre.getText().toString().equals("")) etxtClave.setError(errorMsg); else
        {
            tipoMaquina.setClave(etxtClave.getText().toString());
            tipoMaquina.setNombre(etxtNombre.getText().toString());
            tipoMaquina.setObservaciones(etxtObservaciones.getText().toString());
            FirebaseFirestore.getInstance().collection("tipoMaquina")
                    .document(tipoMaquina.getId()).set(tipoMaquina);
        }
    }

}