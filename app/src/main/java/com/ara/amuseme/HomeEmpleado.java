package com.ara.amuseme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ara.amuseme.Servicios.QRCodeReader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class HomeEmpleado extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout btnRegistrarContadores;
    private ArrayList<String> maquinas;

    private static final int CODIGO_PERMISOS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_empleado);
        // Permisos
        checarPermisos();

        btnRegistrarContadores = findViewById(R.id.btnRegistrarContadores);
        btnRegistrarContadores.setOnClickListener(this);

        maquinas = new ArrayList<>();
        getDBdata();


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeEmpleado.this);
        builder.setMessage("¿Seguro que deseas cerrar sesión?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeEmpleado.this, LoginActivity.class));
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .setCancelable(false).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegistrarContadores:
//                Intent intent = new Intent(HomeEmpleado.this, RegistrarContadores.class);
//                startActivity(intent);
                Intent intent = new Intent(HomeEmpleado.this, QRCodeReader.class);
                intent.putExtra("maquinas", maquinas);
                intent.putExtra("actividad","contadores");
                startActivity(intent);
                break;
            case R.id.cerrarSesionUser:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeEmpleado.this, LoginActivity.class));
                break;
        }
    }

    public void checarPermisos() {
        int permisoCamara = ContextCompat
                .checkSelfPermission(HomeEmpleado.this, android.Manifest.permission.CAMERA);
        int permisoUbicacion = ContextCompat
                .checkSelfPermission(HomeEmpleado.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permisoUbicacionFine = ContextCompat
                .checkSelfPermission(HomeEmpleado.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permisoSMS = ContextCompat
                .checkSelfPermission(HomeEmpleado.this, android.Manifest.permission.SEND_SMS);
        int permisoNotif = ContextCompat
                .checkSelfPermission(HomeEmpleado.this, Manifest.permission.POST_NOTIFICATIONS);
        if (permisoCamara != PackageManager.PERMISSION_GRANTED ||
                permisoUbicacion != PackageManager.PERMISSION_GRANTED ||
                permisoUbicacionFine != PackageManager.PERMISSION_GRANTED ||
                permisoUbicacion != PackageManager.PERMISSION_GRANTED ||
                permisoSMS != PackageManager.PERMISSION_GRANTED ||
                permisoNotif != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeEmpleado.this,
                    new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.SEND_SMS,
                            Manifest.permission.POST_NOTIFICATIONS},
                    CODIGO_PERMISOS);
        }
    }

    public void getDBdata() {

        ProgressDialog progressDialog = new ProgressDialog(HomeEmpleado.this);
        progressDialog.setMessage("Obteniendo datos...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseDatabase dbref = FirebaseDatabase.getInstance();
        OnCompleteListener<DataSnapshot> listener = new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    for (DataSnapshot ds: task.getResult().getChildren()) {
                        maquinas.add(ds.getKey());
                    }
                    progressDialog.cancel();
                }
            }
        };
        dbref.getReference("maquinas").get().addOnCompleteListener(listener);
    }

}