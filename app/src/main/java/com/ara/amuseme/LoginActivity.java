package com.ara.amuseme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.ara.amuseme.administrador.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private CardView loginButton;
    private FirebaseUser currentUser;
//    private ProgressDialog progressDialog;
    private ArrayList<Usuario> usuarios;
    private String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);

        usuarios = new ArrayList<Usuario>();

        ProgressDialog progressDialog;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Ingresando...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        // Iniciar
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference();
        userRef.child("usuarios").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if(!task.isSuccessful()) {
                    Log.e("firebase", "Error obteniendo usuario.", task.getException());
                }else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });

    }
}