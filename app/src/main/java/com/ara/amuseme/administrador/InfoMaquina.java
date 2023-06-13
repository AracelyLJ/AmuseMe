package com.ara.amuseme.administrador;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ara.amuseme.HomeEmpleado;
import com.ara.amuseme.R;
import com.ara.amuseme.modelos.Maquina;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class InfoMaquina extends AppCompatActivity implements View.OnClickListener{

    private Maquina maquina;
    private Usuario usuario_actual;
    private TextView txtMaquina;
    private EditText etxtAlias, etxtNombre, etxtObservaciones;
    private LinearLayout llContActuales;
    private HashMap<String, EditText> contadoresActuales;
    private Button btnDone, btnRegresar, btnEditar, btnBorrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_maquina);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            maquina = getIntent().getExtras().getParcelable("maquina");
            usuario_actual = getIntent().getExtras().getParcelable("usuario_actual");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(InfoMaquina.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(InfoMaquina.this, HomeEmpleado.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        setTitle(usuario_actual.getNombre());

        contadoresActuales = new HashMap<>();

        ponerDatos();
    }

    public void ponerDatos() {

        txtMaquina = findViewById(R.id.txtMaquina);
        etxtAlias = findViewById(R.id.etxtAlias);
        etxtNombre = findViewById(R.id.etxtNombre);
        etxtObservaciones = findViewById(R.id.etxtObservaciones);
        llContActuales = findViewById(R.id.llContActuales);
        btnDone = findViewById(R.id.btnDone);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);

        txtMaquina.setText(maquina.getAlias());
        etxtAlias.setText(maquina.getAlias());
        etxtNombre.setText(maquina.getNombre());
        etxtObservaciones.setText(maquina.getObservaciones());

        agregarContadoresActuales();


        btnDone.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);
        btnEditar.setOnClickListener(this);
        btnBorrar.setOnClickListener(this);

    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnRegresar:
                Intent intent = new Intent(InfoMaquina.this, Maquinas.class);
                intent.putExtra("usuario", usuario_actual);
                startActivity(intent);
                finish();
                break;
            case R.id.btnEditar:
                etxtObservaciones.setEnabled(true);
                for (Map.Entry<String, EditText> entry: contadoresActuales.entrySet()) {
                    entry.getValue().setEnabled(true);
                }
                btnDone.setVisibility(View.VISIBLE);
                break;
            case R.id.btnBorrar:
                Toast.makeText(this, "Opción no disponible por el momento.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDone:
                etxtObservaciones.setEnabled(false);
                for (Map.Entry<String, EditText> entry: contadoresActuales.entrySet()) {
                    entry.getValue().setEnabled(false);
                }
                btnDone.setVisibility(View.GONE);
                submitCambios();
                break;
        }
    }


    public void agregarContadoresActuales() {
        OnCompleteListener<DocumentSnapshot> listenerContadoresActuales = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String contadores = task.getResult().getData().get("contadoresActuales")
                            .toString().replace("{","").replace("}","");
                    String [] contVal = contadores.split(",");
                    for (String cont: contVal) {
                        String [] c = cont.split("=");
                        TextView txt1 = new TextView(InfoMaquina.this);
                        txt1.setTextSize(15);
                        txt1.setText(c[0]);

                        EditText etxt2 = new EditText(InfoMaquina.this);
                        etxt2.setText(c[1]);
                        etxt2.setTextColor(getResources().getColor(R.color.colorBlack));
                        etxt2.setEnabled(false);
                        etxt2.setInputType(InputType.TYPE_CLASS_NUMBER);
                        etxt2.setGravity(Gravity.CENTER);
                        etxt2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));

                        contadoresActuales.put(c[0], etxt2);
                        llContActuales.addView(txt1);
                        llContActuales.addView(etxt2);
                    }
                }
            }
        };
        FirebaseFirestore.getInstance().collection("maquinas")
                .document(maquina.getId()).get()
                .addOnCompleteListener(listenerContadoresActuales);
    }

    public void submitCambios() {
        String errorMsg = "Este campo no debe estar vacío";
        if (etxtNombre.getText().toString().equals("")) etxtNombre.setError(errorMsg); else
        {
            maquina.setAlias(etxtAlias.getText().toString());
            maquina.setNombre(etxtNombre.getText().toString());
            maquina.setObservaciones(etxtObservaciones.getText().toString());
            FirebaseFirestore.getInstance().collection("maquinas")
                    .document(maquina.getId()).set(maquina);

            Map<String, Object> conts = new HashMap<>();
            for (Map.Entry<String, EditText> entry: contadoresActuales.entrySet()) {
                conts.put(entry.getKey(), entry.getValue().getText().toString());
            }
            Map<String, Object> nvosContadores = new HashMap<>();
            nvosContadores.put("contadoresActuales", conts);
            FirebaseFirestore.getInstance().collection("maquinas")
                    .document(maquina.getId()).update(nvosContadores);
        }
    }

}