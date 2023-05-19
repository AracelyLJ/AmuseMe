package com.ara.amuseme;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        FirebaseAuth.getInstance().signOut();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        usuario = new Usuario();
        if (currentUser!= null) {
            Utils.generateDeviceToken(currentUser.getUid());
            goHome(currentUser.getUid());
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void goHome(String user_id) {
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
                    Intent intent;
                    if (usuario.getRol().equals("empleado")) {
                        intent = new Intent(SplashActivity.this, HomeEmpleado.class);
                    } else {
                        intent = new Intent(SplashActivity.this, HomeAdmin.class);
                    }
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                    finish();
                }
            }
        };
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(user_id).get()
                .addOnCompleteListener(listenerUsuario);
    }
}