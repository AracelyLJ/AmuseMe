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

import com.ara.amuseme.Servicios.QRCodeReader;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class HomeEmpleado extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout btnRegistrarContadores;
    private ArrayList<String> maquinas;
    private FirebaseUser user;
    private Usuario usuario;

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
        user = FirebaseAuth.getInstance().getCurrentUser();
        usuario = new Usuario();
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
                Intent intent = new Intent(HomeEmpleado.this, QRCodeReader.class);
                intent.putExtra("maquinas", maquinas);
                intent.putExtra("actividad","contadores");
                intent.putExtra("usuario", usuario);
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
                }
                progressDialog.cancel();
            }
        };
        dbref.getReference("maquinas1").get().addOnCompleteListener(listener);
        OnCompleteListener<DocumentSnapshot> listenerUsuario = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String correo = task.getResult().getData().get("correo").toString();
                    String maqRegSuc = task.getResult().getData().get("maqRegSuc").toString();
                    String nombre = task.getResult().getData().get("nombre").toString();
                    String pw = task.getResult().getData().get("pw").toString();
                    String porDepositar = task.getResult().getData().get("porDepositar").toString();
                    String status = task.getResult().getData().get("status").toString();
                    String sucRegistradas = task.getResult().getData().get("sucRegistradas").toString();
                    String sucursales = task.getResult().getData().get("sucursales").toString();
                    String tel = task.getResult().getData().get("tel").toString();
                    usuario = new Usuario(correo, maqRegSuc, nombre, pw, porDepositar, status,
                            sucRegistradas, sucursales, tel);
                    setTitle(nombre);
                }
            }
        };
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(user.getUid())
                .get()
                .addOnCompleteListener(listenerUsuario);


    }


}