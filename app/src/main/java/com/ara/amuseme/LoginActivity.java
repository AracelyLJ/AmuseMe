package com.ara.amuseme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private CardView loginButton;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);

        usuario = new Usuario();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSesion();
            }
        });

    }

    public void iniciarSesion() {
        if(TextUtils.isEmpty(emailField.getText().toString())) {
            emailField.setError("Campo vacío.");
            return;
        }
        if(TextUtils.isEmpty(passwordField.getText().toString())) {
            passwordField.setError("Campo vacío.");
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Iniciando...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
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
                                        Utils.generateDeviceToken(usuario.getId());
                                        Intent intent;
                                       if (usuario.getRol().equals("empleado")) {
                                            intent = new Intent(LoginActivity.this, HomeEmpleado.class);
                                        } else {
                                            intent = new Intent(LoginActivity.this, HomeAdmin.class);
                                        }
                                        intent.putExtra("usuario", usuario);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            };
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            db.collection("usuarios").document(auth.getCurrentUser().getUid()).get()
                                    .addOnCompleteListener(listenerUsuario);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage("Usuario y/o contraseña incorrectos")
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            return;
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {

    }
}