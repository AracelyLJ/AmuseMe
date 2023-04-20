package com.ara.amuseme.servicios;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import com.ara.amuseme.HomeEmpleado;
import com.ara.amuseme.R;
import com.ara.amuseme.RegistrarContadores;
import com.ara.amuseme.modelos.Maquina;
import com.ara.amuseme.modelos.Usuario;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

// source: https://github.com/yuriy-budiyev/code-scanner
public class QRCodeReader extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private ArrayList<String> maquinasExistentes;
    private String actividad;
    private Usuario usuario;
    private String result;
    private Maquina maquina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code_reader);

        maquinasExistentes = new ArrayList<>();
        usuario = new Usuario();
        maquina = new Maquina();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Intent intent = getIntent();
            maquinasExistentes = (ArrayList<String>) intent.getExtras().getSerializable("maquinas");
            actividad = intent.getExtras().getString("activity");
            usuario = intent.getExtras().getParcelable("usuario");
            result = "";
        }

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(com.google.zxing.Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String r = result.getText();
                        if(maquinasExistentes.contains(r)) {
                            pasaraRegistro(r);
                        } else {
                            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeReader.this);
                            builder.setMessage("La máquina que intentas registrar no existe.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(QRCodeReader.this, HomeEmpleado.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).setCancelable(false).show();
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void pasaraRegistro(String r) {
        result = r;

        // Sucursal
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        OnCompleteListener<QuerySnapshot> listenerSucursal = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot  > task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String alias = task.getResult().getDocuments().get(0).get("alias").toString();
                    String imagen = task.getResult().getDocuments().get(0).get("imagen").toString();
                    String nombre = task.getResult().getDocuments().get(0).get("nombre").toString();
                    String observaciones = task.getResult().getDocuments().get(0).get("observaciones").toString();
                    String renta = task.getResult().getDocuments().get(0).get("renta").toString();
                    maquina = new Maquina(alias, imagen, nombre, observaciones, renta);
                    Intent i = new Intent(QRCodeReader.this, RegistrarContadores.class);
                    i.putExtra("nombresMaquinas",maquinasExistentes);
                    i.putExtra("usuario", usuario);
                    i.putExtra("maquina",maquina);
                    startActivity(i);
                }
            }
        };
        db.collection("maquinas")
                .whereEqualTo("nombre", result)
                .get()
                .addOnCompleteListener(listenerSucursal);



    }

}