package com.ara.amuseme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ara.amuseme.modelos.Deposito;
import com.ara.amuseme.modelos.Usuario;
import com.ara.amuseme.servicios.FCMSend;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class RegistrarDeposito extends AppCompatActivity {

    private EditText etxt_pago;
    private ImageView img_foto;
    private TextView txt_horayFecha;
    private Button btn_registrarDeposito;
    private Usuario usuario;
    private String ubicacion;
    private Uri photoUri;
    private Map<String, String> time;
    private ArrayList<String> tokensNotif;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_deposito);

        // Views
        etxt_pago = findViewById(R.id.etxt_pago);
        img_foto = findViewById(R.id.img_foto);
        txt_horayFecha = findViewById(R.id.txt_horayFecha);
        btn_registrarDeposito = findViewById(R.id.btn_registrarDeposito);

        ubicacion = "";
        photoUri = null;
        tokensNotif = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = getIntent().getExtras().getParcelable("usuario");
            ubicacion = getIntent().getExtras().getString("ubicacion");
            tokensNotif = (ArrayList<String>) extras.getSerializable("tokensNotif");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarDeposito.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(RegistrarDeposito.this, HomeEmpleado.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        time = Utils.getTime();
        String hf =
                "Fecha: "+time.get("fecha")+
                        "\nHora: "+time.get("hora")+
                        "\n\n"+ubicacion;

        setTitle(usuario.getNombre());
        etxt_pago.setHint("0.0");
        txt_horayFecha.setText(hf);
        img_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        btn_registrarDeposito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarDeposito();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private boolean validarDatos() {
        if (TextUtils.isEmpty(etxt_pago.getText().toString())) {
            etxt_pago.setError("Este campo debe ser registrado.");
            return false;
        }
        if (photoUri==null) {
            Dialog dialog = new Dialog(RegistrarDeposito.this);
            dialog.setContentView(R.layout.cardview_validacion_de_datos);
            // Editar texto
            TextView mensajeFinal = dialog.findViewById(R.id.mensaje);
            Button siButton = dialog.findViewById(R.id.si_button);
            Button noButton = dialog.findViewById(R.id.no_button);
            mensajeFinal.setText("Es necesario tomar foto al comprobante de pago.");
            siButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            siButton.setText("OK");
            noButton.setVisibility(View.GONE);

            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            return false;
        }
        return true;
    }

    private void registrarDeposito() {
        if (!validarDatos()){
            return;
        }
        Deposito deposito = new Deposito(
                time.get("hora"),
                time.get("fecha"),
                "",
                etxt_pago.getText().toString(),
                time.get("numSemana"),
                ubicacion,
                usuario.getId()
        );
        FirebaseFirestore.getInstance().collection("registros_depositos")
                .document().set(deposito);
        uploadImages();

        String mensaje = "Depósito registrado satisfactoriamente.";
        FCMSend.pushNotification(
                RegistrarDeposito.this,
                tokensNotif,
                usuario.getToken(),
                "AmuseMe Deposito",
                mensaje
                );
        mostrarMensajeFinal(mensaje);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("ERROR FOTO", "Error creando foto: " + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.ara.amuseme.FileProvider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            // no camera on this device
            Toast.makeText(this, "No hay cámara en este dispositivo o está desactivada.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + usuario.getContRegistro() + "_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    public void uploadImages() {
        try {
            String fecha = time.get("fecha").replaceAll("/","_");
            String hora = time.get("hora").replaceAll(":","_");
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference;

            storageReference = storage.getReference("fotos_depositos/" +
                    usuario.getId());
            final StorageReference ref = storageReference.child(usuario.getContRegistro()+"-"+fecha+"_"+hora);
            ref.putFile(photoUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("Upload", "Imagen deposito subida al servidor.");
                            } else {
                                Log.e("Error Upload", "No se pudo subir imagen al servidor.");
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e("ErrorUploadImages", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void mostrarMensajeFinal(String mensaje) {
        Dialog dialog = new Dialog(RegistrarDeposito.this);
        //se asigna el layout
        dialog.setContentView(R.layout.cardview_message);
        // Editar texto
        TextView mensajeFinal = dialog.findViewById(R.id.mensajeFinal);
        ImageView imgCloseDialog = dialog.findViewById(R.id.imgCloseDialog);
        Button sucDoneButton = dialog.findViewById(R.id.sucDoneButton);
        mensajeFinal.setText(mensaje);

        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(RegistrarDeposito.this, HomeEmpleado.class);
                startActivity(intent);
            }
        });
        sucDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(RegistrarDeposito.this, HomeEmpleado.class);
                startActivity(intent);
            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}