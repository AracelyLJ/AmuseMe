package com.ara.amuseme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ara.amuseme.modelos.Maquina;
import com.ara.amuseme.modelos.Usuario;
import com.ara.amuseme.modelos.Visita;
import com.ara.amuseme.servicios.FCMSend;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import io.grpc.okhttp.internal.Util;

public class RegistrarVisita extends AppCompatActivity {

    private TextView txtIdMaquina;
    private EditText etxt_descripcion;
    private Button btnRegistrarVisita;
    private Usuario usuario;
    private ArrayList<String> tokensNotif;
    private Maquina maquina;
    private String cveSucursal;
    private String cveTipo;
    private Visita visita;
    private String ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_visita);

        txtIdMaquina = findViewById(R.id.txtIdMaquina);
        etxt_descripcion = findViewById(R.id.etxt_descripcion);
        btnRegistrarVisita = findViewById(R.id.btnRegistrarVisita);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Intent intent = getIntent();
            usuario = intent.getExtras().getParcelable("usuario");
            tokensNotif = (ArrayList<String>) intent.getExtras().getSerializable("tokensNotif");
            maquina = intent.getExtras().getParcelable("maquina");
            cveSucursal = maquina.getAlias().charAt(0) + "" + maquina.getAlias().charAt(1);
            cveTipo = maquina.getAlias().charAt(2) + "" + maquina.getAlias().charAt(3);
            ubicacion = getIntent().getExtras().getString("ubicacion");
        }else
            {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarVisita.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(RegistrarVisita.this, HomeEmpleado.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        setTitle(usuario.getNombre());
        validarDatos("inicio");
        txtIdMaquina.setText(maquina.getAlias());

        visita = new Visita();

        btnRegistrarVisita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarVisita();
            }
        });

    }

    public boolean validarDatos(String accion) {
        String mensaje = "";
        String botonSi = "SI";
        boolean regresar = false;
        Intent intent = new Intent(RegistrarVisita.this, HomeEmpleado.class);
        intent.putExtra("usuario", usuario);

        if (accion.equals("inicio")){
            String sucsAsignadas = usuario.getSucursales();
            // Validar si la máquina está asignada al usuario
            if (!sucsAsignadas.contains(cveSucursal)) {
                mensaje = "No estás asignado para registrar esta sucursal. Contacta a tu administrador.";
                botonSi = "OK";
                regresar = true;
                usuario.setMaqRegSuc(usuario.getMaqRegSuc().replaceAll(cveSucursal, ""));
            }
        } else {
            if (TextUtils.isEmpty(etxt_descripcion.getText().toString())) {
                etxt_descripcion.setError("Este campo debe ser registrado.");
                return false;
            }
        }

        final boolean valRegresar = regresar;
        if (!mensaje.equals("")) {
            Dialog dialog = new Dialog(RegistrarVisita.this);
            dialog.setContentView(R.layout.cardview_validacion_de_datos);
            // Editar texto
            TextView mensajeFinal = dialog.findViewById(R.id.mensaje);
            Button siButton = dialog.findViewById(R.id.si_button);
            Button noButton = dialog.findViewById(R.id.no_button);
            mensajeFinal.setText(mensaje);
            siButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (valRegresar) startActivity(intent);
                }
            });
            if (botonSi == "OK") {
                siButton.setText("OK");
                noButton.setVisibility(View.GONE);
            } else {
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(intent);
                        dialog.dismiss();
                        if (valRegresar) startActivity(intent);
                    }
                });
            }

            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            return false;
        }
        return true;
    }

    public void registrarVisita() {
        validarDatos("final");

        Map<String, String> tiempo = Utils.getTime();
        visita = new Visita(
                etxt_descripcion.getText().toString(),
                tiempo.get("fecha"),
                tiempo.get("hora"),
                tiempo.get("numSemana"),
                ubicacion,
                usuario.getId()
        );
        FirebaseFirestore.getInstance().collection("registros_visitas")
                .document().set(visita);
        String mensaje = "Se registró visita de la máquina: "+maquina.getAlias();
        FCMSend.pushNotification(
                RegistrarVisita.this,
                tokensNotif,
                usuario.getToken(),
                "AmuseMe Visita",
                mensaje
        );
        mostrarMensajeFinal(mensaje);
    }

    public void mostrarMensajeFinal(String mensaje) {
        Dialog dialog = new Dialog(RegistrarVisita.this);
        //se asigna el layout
        dialog.setContentView(R.layout.cardview_message);
        // Editar texto
        TextView mensajeFinal = dialog.findViewById(R.id.mensajeFinal);
        ImageView imgCloseDialog = dialog.findViewById(R.id.imgCloseDialog);
        Button sucDoneButton = dialog.findViewById(R.id.sucDoneButton);
        mensajeFinal.setText(mensaje);
        Intent intent = new Intent(RegistrarVisita.this, HomeEmpleado.class);
        intent.putExtra("usuario", usuario);

        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(intent);
            }
        });
        sucDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(intent);
            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

}