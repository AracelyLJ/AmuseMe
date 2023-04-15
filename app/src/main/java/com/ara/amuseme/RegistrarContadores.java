package com.ara.amuseme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ara.amuseme.modelos.Maquina;
import com.ara.amuseme.modelos.Sucursal;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class RegistrarContadores extends AppCompatActivity {

    private TextView txtNombreMaquina;
    private TextView txtPrizeCounter;
    private EditText etxtPrizes;
    private ImageView btnCamPrizes;
    private Button btnRegistrarMaquina;
    private TextView txtDatosAnteriores;
    private Maquina maquina;
    private ArrayList<EditText> camposContadores;
    private ArrayList<ImageView> camposFotos;
    private ArrayList<String> textContadores;
    private HashMap<String, Uri> mapFotos;
    private HashMap<String, String> mapUrlFotos;
    private int contRegActual;
    private String idUsuario;
    private ArrayList<String> nombresMaquinas;
    private ArrayList<String> maquinasRegistradas;
    private Uri photoUri;
    private String cveSucursal;
    private Usuario usuario;
    private Sucursal sucursal;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_contadores);

        // Initialize views
        txtNombreMaquina = findViewById(R.id.nombreMaquina);
        txtPrizeCounter = findViewById(R.id.prizeCounter);
        etxtPrizes = findViewById(R.id.prizes);
        btnCamPrizes = findViewById(R.id.camPrizes);
        btnRegistrarMaquina = findViewById(R.id.registrarMaquina);
        txtDatosAnteriores = findViewById(R.id.datosAnteriores);
        btnCamPrizes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent("*prizes");
            }
        });

        // Initializations
        maquina = new Maquina();
        camposContadores = new ArrayList<>();
        textContadores = new ArrayList<>();
        camposFotos = new ArrayList<>();
        contRegActual = 0;
        idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        maquinasRegistradas = new ArrayList<>();
        mapFotos = new HashMap<>();
        mapUrlFotos = new HashMap<>();
        usuario = new Usuario();


        // Get extras
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Intent intent = getIntent();
            nombresMaquinas = (ArrayList<String>) intent.getExtras().getSerializable("nombresMaquinas");
            usuario = getIntent().getExtras().getParcelable("usuario");
            maquina = intent.getExtras().getParcelable("maquina");
            cveSucursal = maquina.getAlias().charAt(0) + "" + maquina.getAlias().charAt(1);
        } else
            {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarContadores.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(RegistrarContadores.this, HomeEmpleado.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        // Get data from database
        getFirebaseData();
        getRegistroAnterior();
        setContadores(maquina.getAlias());

        // Set views
        txtNombreMaquina.setText("Máquina:  " + maquina.getAlias());
        btnRegistrarMaquina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarContadores();
            }
        });

        // Validaciones
        validarDatos("inicio");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegistrarContadores.this, HomeEmpleado.class);
        startActivity(intent);
        finish();
    }

    public void getFirebaseData() {
        ProgressDialog progressDialog = new ProgressDialog(RegistrarContadores.this);
        progressDialog.setMessage("Obteniendo información...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Sucursal
        OnCompleteListener<QuerySnapshot> listenerSucursal = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot  > task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String clave = task.getResult().getDocuments().get(0).get("clave").toString();
                    String maquinas = task.getResult().getDocuments().get(0).get("maquinas").toString();
                    String nombre = task.getResult().getDocuments().get(0).get("nombre").toString();
                    String ubicacion = task.getResult().getDocuments().get(0).get("ubicacion").toString();
                    sucursal = new Sucursal(clave, maquinas, nombre, ubicacion);
                    progressDialog.cancel();
                }
            }
        };
        db.collection("sucursal")
                .whereEqualTo("clave", cveSucursal)
                .get()
                .addOnCompleteListener(listenerSucursal);

    }

    public boolean validarDatos(String accion) {

        String mensaje = "";
        String botonSi = "SI";
        boolean regresar = false;

        if (accion.equals("inicio")){
            String sucsReg = usuario.getSucRegistradas();
            String maqsReg = usuario.getMaqRegSuc();
            String sucsAsignadas = usuario.getSucursales();
            String maqRegSuc = usuario.getMaqRegSuc(); // Checar que no empieze a registrar otra
            if (!sucsReg.equals("") && sucsReg.contains(cveSucursal)) { // Checa si ya se registró esta sucursal
                mensaje = "Esta sucursal ya fué registrada. ¿Deseas reiniciar el registo?";
                usuario.setSucRegistradas(
                        usuario.getSucRegistradas().replaceAll(cveSucursal,""));

            } else
            if (maqsReg.contains(maquina.getAlias())) { // Checa si ya se regisró esta máquina
                mensaje = "Esta máquina ya fué registrada. ¿Deseas reiniciar el registro?";
            } else
            if (!sucsAsignadas.contains(cveSucursal)) { // Checa si el user tiene esta suc asignada
                mensaje = "No estás asignado para registrar esta sucursal. Contacta a tu administrador.";
                botonSi = "OK";
                regresar = true;
                usuario.setMaqRegSuc(
                        usuario.getMaqRegSuc().replaceAll(cveSucursal,""));
            } else
            if (!maqRegSuc.equals("")) {               // sucursal si no ha terminado la actual
                String suc = usuario.getMaqRegSuc().substring(0,2);
                if (!suc.equals(cveSucursal)){
                    mensaje = "No has terminado de registrar la sucursal: "+suc;
                    botonSi = "OK";
                    regresar = true;
                }
            }
        } else {
            for (EditText e: camposContadores){ // Checar que se llenaron todos los campos
                if (TextUtils.isEmpty(e.getText().toString())){
                    e.setError("Este campo debe ser registrado.");
                    return false;
                }
            }
            if (mapFotos.size()!=textContadores.size()) { // Checar que se tomaron todas las fotos
                mensaje = "Es necesario tomar foto de todos los contadores.";
                botonSi = "OK";
            }
            // Todo: Checar los valores de contadores no sean menores al anterior
        }
        final boolean valRegresar = regresar;
        if (!mensaje.equals("")) {
            Dialog dialog = new Dialog(RegistrarContadores.this);
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
                    if (valRegresar) startActivity(new Intent(RegistrarContadores.this,
                            HomeEmpleado.class));
                }
            });
            if (botonSi == "OK") {
                siButton.setText("OK");
                noButton.setVisibility(View.GONE);
            } else{
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(RegistrarContadores.this,
                                HomeEmpleado.class));
                        dialog.dismiss();
                        if (valRegresar) startActivity(new Intent(RegistrarContadores.this,
                                HomeEmpleado.class));
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

    public void setContadores(String alias) {
        String cveMaq = ""+alias.charAt(2)+alias.charAt(3);

        // Agregar prize counter
        camposContadores.add(etxtPrizes);
        camposFotos.add(btnCamPrizes);
        textContadores.add("*prizes");

        // Tipo
        ProgressDialog progressDialogt = new ProgressDialog(RegistrarContadores.this);
        progressDialogt.setMessage("Obteniendo contadores...");
        progressDialogt.setCancelable(false);
        progressDialogt.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        OnCompleteListener<QuerySnapshot> listenerTipo = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String contadores = task.getResult().getDocuments().get(0).get("contadores").toString();
                    String sconts[] = contadores.split(",");
                    int i=0;
                    do {
                        ArrayList<EditText> editTexts = new ArrayList<>();
                        String contador = sconts[i];

                        // Layout
                        ViewGroup layout = findViewById(R.id.content);
                        LayoutInflater inflater = LayoutInflater.from(RegistrarContadores.this);
                        int id = R.layout.campocontador;
                        LinearLayout linearLayout = (LinearLayout) inflater.inflate(id, null, false);

                        int id_new_view = View.generateViewId();
                        TextView textView = linearLayout.findViewById(R.id.txtViewCounter);
                        EditText editText = linearLayout.findViewById(R.id.editTxtCounter);
                        ImageView imageView = linearLayout.findViewById(R.id.camCounter);

                        editText.setId(id_new_view);
                        imageView.setId(id_new_view);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dispatchTakePictureIntent(contador);
                            }
                        });

                        textView.setText(contador.toUpperCase());
                        editTexts.add(editText);
                        camposContadores.add(editText);
                        textContadores.add(contador);
                        camposFotos.add(imageView);

                        layout.addView(linearLayout);
                        i+=3;
                    } while (i<sconts.length);
                    progressDialogt.cancel();
                }
            }
        };
        db.collection("tipoMaquina")
                .whereEqualTo("clave", cveMaq)
                .get()
                .addOnCompleteListener(listenerTipo);
    }

    public void getRegistroAnterior() {
        ProgressDialog progressDialog = new ProgressDialog(RegistrarContadores.this);
        progressDialog.setMessage("Obteniendo maquinas...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        // Registro anterior
        FirebaseDatabase dbref = FirebaseDatabase.getInstance();
        OnCompleteListener<DataSnapshot> listenerRegistro = new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    for (DataSnapshot ds: task.getResult().getChildren()) {
                        int key = Integer.parseInt(ds.getKey());
                        if(contRegActual < key) contRegActual = key;
                    }
                    for(DataSnapshot ds1: task.getResult().child(contRegActual+"").getChildren()) {
                        maquinasRegistradas.add(ds1.getKey());
                    }
                }
                if (maquinasRegistradas.size() == nombresMaquinas.size()) contRegActual++;
                progressDialog.cancel();
            }
        };
        try {
            dbref.getReference("registros_maquinas/"+idUsuario).get().addOnCompleteListener(listenerRegistro);
        }catch (Exception e) {
            Log.e("Error", "Error obteniendo registro anterior. Iniciando contador...");
            contRegActual = 0;
        }
    }

    public void registrarContadores() {
        if (!validarDatos("final")) return;

        // Fecha y hora
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        SimpleDateFormat formatFecha = new SimpleDateFormat("yyyy/MM/dd", new Locale("es_MX"));
        DateFormat formatHora = new SimpleDateFormat("HH:mm", new Locale("es_MX"));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ZoneId zoneIdMx = ZoneId.of("America/Mexico_City");
            formatFecha.setTimeZone(TimeZone.getTimeZone(zoneIdMx));
            formatHora.setTimeZone(TimeZone.getTimeZone(zoneIdMx));
        }
        int numSemana = calendar.get(Calendar.WEEK_OF_YEAR);
        String fecha = formatFecha.format(date);
        String hora = formatHora.format(date);

        // Registro
        HashMap<String, String> nvoRegistro = new HashMap<>();
        nvoRegistro.put("nombre",maquina.getNombre());
        nvoRegistro.put("alias",maquina.getAlias());
        nvoRegistro.put("fecha",fecha);
        nvoRegistro.put("hora",hora);
        nvoRegistro.put("ubicacion","");
        nvoRegistro.put("semanaFiscal",String.valueOf(numSemana));
        nvoRegistro.put("usuario", idUsuario);
        nvoRegistro.put("contRegistro",String.valueOf(contRegActual));
        nvoRegistro.put("sucursal",cveSucursal);
        nvoRegistro.put("tipoMaquina",maquina.getAlias().charAt(2)+""+maquina.getAlias().charAt(3));
        for (int i=0; i<textContadores.size(); i++){
            nvoRegistro.put(textContadores.get(i), camposContadores.get(i).getText().toString());
        }
        // Subir fotos de contadores registrados
        uploadImages();

        // Subir registro
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("registros_maquinas/" + idUsuario + "/"
                + (contRegActual)).child(maquina.getNombre()).setValue(nvoRegistro);

        // Actualizar usuario
        String alias = maquina.getAlias();
        String auxMaqReg = usuario.getMaqRegSuc();
        String auxSucReg = usuario.getSucRegistradas();
        ArrayList<String> maquinasRegistradas = new ArrayList<>();
        ArrayList<String> maquinasPorRegistrar = new ArrayList<>
                (Arrays.asList(sucursal.getMaquinas().replaceAll(" ","").split(",")));
        ArrayList<String> sucursalesRegistradas = new ArrayList<>();
        ArrayList<String> sucursalesPorRegistrar = new ArrayList<>
                (Arrays.asList(usuario.getSucursales().replaceAll(" ","").split(",")));
        Boolean regresarSinDialog = true;
        String mensajeFinal="";

        if (!auxMaqReg.equals("")) {
            maquinasRegistradas = new ArrayList<>(Arrays.asList(usuario.getMaqRegSuc()
                    .replaceAll(" ","").split(",")));
        }
        if (!auxSucReg.equals("")) {
            sucursalesRegistradas = new ArrayList<>(Arrays.asList(usuario.getSucRegistradas()
                    .replaceAll(" ","").split(",")));
        }

        if (!maquinasRegistradas.contains(alias)) {
            maquinasRegistradas.add(alias);
        }
        Collections.sort(maquinasPorRegistrar);
        Collections.sort(maquinasRegistradas);
        if (maquinasPorRegistrar.equals(maquinasRegistradas)){
            mensajeFinal = "Se terminaron de registrar las máquinas de la sucursal: "+sucursal.getNombre();
            maquinasRegistradas=new ArrayList<>();
            sucursalesRegistradas.add(sucursal.getClave());
            usuario.setSucRegistradas(sucursalesRegistradas.toString()
                    .replace("[","").replace("]",""));
            regresarSinDialog = false;
        }
        Collections.sort(sucursalesPorRegistrar);
        Collections.sort(sucursalesRegistradas);
        if (sucursalesPorRegistrar.equals(sucursalesRegistradas)){
            mensajeFinal = "Se terminaron de registrar todas las maquinas de las sucursales " +
                    "asignadas al usuario: " + usuario.getNombre();
            usuario.setSucRegistradas("");
            regresarSinDialog = false;
        }
        usuario.setMaqRegSuc(maquinasRegistradas.toString()
                .replace("[","").replace("]",""));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(idUsuario).set(usuario);
        if (regresarSinDialog) {
            Intent intent = new Intent(RegistrarContadores.this, HomeEmpleado.class);
            startActivity(intent);
        } else {
            mostrarMensajeFinal(mensajeFinal);
        }

    }

    private void dispatchTakePictureIntent(String contador) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera

            File photoFile = null;
            try {
                photoFile = createImageFile(contador);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("ERROR FOTO", "Error creando foto: "+ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.ara.amuseme.FileProvider",
                        photoFile);
                mapFotos.put(contador, photoUri);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            // no camera on this device
            Toast.makeText(this, "No hay cámara en este dispositivo o está desactivada.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile(String contador) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_"+maquina.getAlias()+"_"+contador+"_"+timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    public void uploadImages(){
        try {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference;
            storageReference = storage.getReference("fotos_contadores/" +
                    idUsuario + "/" + contRegActual + "/" + cveSucursal);
            for (Map.Entry entry : mapFotos.entrySet()) {
                final StorageReference ref = storageReference.child(maquina.getAlias() + "_" +
                        maquina.getNombre() + "_" + entry.getKey());
                ref.putFile((Uri) entry.getValue()) // mapFotos.get(entry.getKey())
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    Log.d("Upload", "Imagen subida al servidor.");
                                }else{
                                    Log.e("Error Upload", "No se pudo subir imagen al servidor.");
                                }
                            }
                        });
            }
        }catch (Exception e){
            Log.e("ErrorUploadImages", Objects.requireNonNull(e.getMessage()));
        }


    }

    public void mostrarMensajeFinal(String mensaje ) {
        Dialog dialog = new Dialog(RegistrarContadores.this);
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
                Intent intent = new Intent(RegistrarContadores.this, HomeEmpleado.class);
                startActivity(intent);
            }
        });
        sucDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(RegistrarContadores.this, HomeEmpleado.class);
                startActivity(intent);
            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

}