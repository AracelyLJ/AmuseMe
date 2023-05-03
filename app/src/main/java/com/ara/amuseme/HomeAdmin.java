package com.ara.amuseme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ara.amuseme.administrador.Usuarios;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeAdmin extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> tokensToNotif;
    private FirebaseUser user;
    private Usuario usuario;
    private CardView btnUsuarios, btnSucursales, btnDepositos, btnRegistros, btnMaquinas, btnTipos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        // Views
        btnUsuarios = findViewById(R.id.btnUsuarios);
        btnSucursales = findViewById(R.id.btnSucursales);
        btnDepositos = findViewById(R.id.btnDepositos);
        btnRegistros = findViewById(R.id.btnRegistros);
        btnMaquinas = findViewById(R.id.btnMaquinas);
        btnTipos = findViewById(R.id.btnTipos);

        // Inicializaciones
        user = FirebaseAuth.getInstance().getCurrentUser();
        tokensToNotif = new ArrayList<>();
        usuario = new Usuario();

        btnUsuarios.setOnClickListener(this);
        btnSucursales.setOnClickListener(this);
        btnDepositos.setOnClickListener(this);
        btnRegistros.setOnClickListener(this);
        btnMaquinas.setOnClickListener(this);
        btnTipos.setOnClickListener(this);

        // Obtención de datos
        getTokensToNotif();
        getFirebaseData();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeAdmin.this);
        builder.setMessage("¿Seguro que deseas cerrar sesión?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeAdmin.this, LoginActivity.class));
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
        Intent i;
        switch (view.getId()){
            case R.id.btnUsuarios:
                i = new Intent(HomeAdmin.this, Usuarios.class);
                startActivity(i);
                break;
        }

    }
    public void getTokensToNotif() {
        OnCompleteListener<QuerySnapshot> listenerTokens = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    for (DocumentSnapshot ds: task.getResult().getDocuments()){
                        tokensToNotif.add(ds.get("token").toString());
                    }
                }
            }
        };
        FirebaseFirestore.getInstance().collection("DeviceTokens").get()
                .addOnCompleteListener(listenerTokens);
    }
    public void getFirebaseData() {
        OnCompleteListener<DocumentSnapshot> listenerUsuario = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String contRegistro = task.getResult().getData().get("contRegistro").toString();
                    String correo = task.getResult().getData().get("correo").toString();
                    String id = task.getResult().getData().get("id").toString();
                    String maqRegSuc = task.getResult().getData().get("maqRegSuc").toString();
                    String nombre = task.getResult().getData().get("nombre").toString();
                    String porDepositar = task.getResult().getData().get("porDepositar").toString();
                    String pw = task.getResult().getData().get("pw").toString();
                    String rol = task.getResult().getData().get("rol").toString();
                    String status = task.getResult().getData().get("status").toString();
                    String sucRegistradas = task.getResult().getData().get("sucRegistradas").toString();
                    String sucursales = task.getResult().getData().get("sucursales").toString();
                    String tel = task.getResult().getData().get("tel").toString();
                    String token = task.getResult().getData().get("token").toString();
                    usuario = new Usuario(contRegistro, correo, id, maqRegSuc, nombre, porDepositar,
                            pw, rol, status, sucRegistradas, sucursales, tel, token);
                    setTitle(nombre);
                }
            }
        };
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(user.getUid()).get()
                .addOnCompleteListener(listenerUsuario);
    }


}