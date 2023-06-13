package com.ara.amuseme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
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
import com.ara.amuseme.modelos.RegistroMaquina;
import com.ara.amuseme.modelos.Sucursal;
import com.ara.amuseme.modelos.Usuario;
import com.ara.amuseme.servicios.FCMSend;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistrarContadores extends AppCompatActivity {

    private TextView txtNombreMaquina;
    private EditText etxtPrizes;
    private ImageView btnCamPrizes;
    private Button btnRegistrarMaquina;
    private TextView txtDatosAnteriores1;
    private TextView txtDatosAnteriores2;
    private Maquina maquina;
    private HashMap<String, EditText> camposContadores;
    private ArrayList<ImageView> camposFotos;
    private ArrayList<String> textContadores;
    private HashMap<String, Uri> mapFotos;
    private int contRegActual;
    private int contRegAnterior;
    private RegistroMaquina registroActual;
    private RegistroMaquina registroAnterior;
    private String idUsuario;
    private Uri photoUri;
    private String cveSucursal;
    private String cveTipo;
    private Usuario usuario;
    private Sucursal sucursal;
    private String ubicacion;
    private ArrayList<String> tokensNotif;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_contadores);

        // Initialize views
        txtNombreMaquina = findViewById(R.id.nombreMaquina);
        etxtPrizes = findViewById(R.id.prizes);
        btnCamPrizes = findViewById(R.id.camPrizes);
        btnRegistrarMaquina = findViewById(R.id.registrarMaquina);
        txtDatosAnteriores1 = findViewById(R.id.datosAnteriores1);
        txtDatosAnteriores2 = findViewById(R.id.datosAnteriores2);
        btnCamPrizes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent("*prizes");
            }
        });

        // Initializations
        maquina = new Maquina();
        camposContadores = new HashMap<>();
        textContadores = new ArrayList<>();
        camposFotos = new ArrayList<>();
        contRegActual = 0;
        contRegAnterior = 0;
        registroActual = new RegistroMaquina();
        registroAnterior = new RegistroMaquina();
        idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mapFotos = new HashMap<>();
        usuario = new Usuario();
        ubicacion = "";
        tokensNotif = new ArrayList<>();

        // Get extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Intent intent = getIntent();
            usuario = getIntent().getExtras().getParcelable("usuario");
            maquina = intent.getExtras().getParcelable("maquina");
            cveSucursal = maquina.getAlias().charAt(0) + "" + maquina.getAlias().charAt(1);
            cveTipo = maquina.getAlias().charAt(2) + "" + maquina.getAlias().charAt(3);
            tokensNotif = (ArrayList<String>) intent.getExtras().getSerializable("tokensNotif");
        } else {
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

        // Get datos necesarios
        setContadores(maquina.getAlias());

        // Set views
        txtNombreMaquina.setText("Máquina:  " + maquina.getAlias());
        btnRegistrarMaquina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validarDatos("final")) {
                    registrarContadores();
                }
            }
        });

        // Validaciones
        validarDatos("inicio");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        getFirebaseData();
        getRegistroAnterior();
//        getUbicacion();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegistrarContadores.this, HomeEmpleado.class);
        intent.putExtra("usuario",usuario);
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
            public void onComplete(Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String clave = task.getResult().getDocuments().get(0).get("clave").toString();
                    String id = task.getResult().getDocuments().get(0).get("id").toString();
                    String maquinas = task.getResult().getDocuments().get(0).get("maquinas").toString();
                    String nombre = task.getResult().getDocuments().get(0).get("nombre").toString();
                    String ubicacion = task.getResult().getDocuments().get(0).get("ubicacion").toString();
                    sucursal = new Sucursal(clave, id, maquinas, nombre, ubicacion);
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
        boolean regresarSi = false;
        boolean regresarNo = false;
        boolean posibleError = false;
        Intent intent = new Intent(RegistrarContadores.this, HomeEmpleado.class);
        intent.putExtra("usuario", usuario);

        if (accion.equals("inicio")) {
            String sucsReg = usuario.getSucRegistradas();
            String maqsReg = usuario.getMaqRegSuc();
            String sucsAsignadas = usuario.getSucursales();
            String maqRegSuc = usuario.getMaqRegSuc(); // Checar que no empieze a registrar otra
            if (!sucsReg.equals("") && sucsReg.contains(cveSucursal)) { // Checa si ya se registró esta sucursal
                mensaje = "Esta sucursal ya fué registrada. ¿Deseas reiniciar el registo?";
                regresarNo = true;

            } else if (maqsReg.contains(maquina.getAlias())) { // Checa si ya se regisró esta máquina
                mensaje = "Esta máquina ya fué registrada. ¿Deseas reiniciar el registro?";
                regresarNo = true;
            } else if (!sucsAsignadas.contains(cveSucursal)) { // Checa si el user tiene esta suc asignada
                mensaje = "No estás asignado para registrar esta sucursal. Contacta a tu administrador.";
                botonSi = "OK";
                regresarSi = true;
            } else if (!maqRegSuc.equals("")) {               // sucursal si no ha terminado la actual
                String suc = usuario.getMaqRegSuc().substring(0, 2);
                if (!suc.equals(cveSucursal)) {
                    mensaje = "No has terminado de registrar la sucursal: " + suc;
                    botonSi = "OK";
                    regresarSi = true;
                }
            }
        } else {
            for (Map.Entry<String, EditText> entry: camposContadores.entrySet()) {
                String valorActual = entry.getValue().getText().toString();
                String valorAnterior = registroAnterior.getContadores().get(entry.getKey());
                if (valorAnterior == null) {
                    valorAnterior = "0";
                }
                if (TextUtils.isEmpty(valorActual)){ // Validar que no hay campos vacíos
                    entry.getValue().setError("Este campo debe ser registrado.");
                    return false;
                } else if(Integer.parseInt(valorActual) < Integer.parseInt(valorAnterior)) {
                    // Validar que los datos no sean menores al registro anterior
                    entry.getValue().setError("El valor del contador: " + entry.getKey() +
                            " debe ser mayor al registro anterior.");
                    return false;
                } else if ((Integer.parseInt(valorActual) - Integer.parseInt(valorAnterior)) > 5000) {
                    // Revisar si el valor es mucho mas grande que el anterior
                    mensaje = "El valor del contador: " + entry.getKey() +
                            " podría ser incorrecto. ¿Deseas continuar?";
                    posibleError = true;
                }
            }
        }
        final boolean valRegresarSi = regresarSi;
        final boolean valRegresarNo = regresarNo;
        if (!mensaje.equals("") ) {
            Dialog dialog = new Dialog(RegistrarContadores.this);
            dialog.setContentView(R.layout.cardview_validacion_de_datos);
            // Editar texto
            TextView mensajeFinal = dialog.findViewById(R.id.mensaje);
            Button siButton = dialog.findViewById(R.id.si_button);
            Button noButton = dialog.findViewById(R.id.no_button);
            mensajeFinal.setText(mensaje);
            boolean finalPosibleError = posibleError;
            siButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (valRegresarSi) startActivity(intent);
                    if (finalPosibleError) registrarContadores();
                }
            });
            if (botonSi.equals("OK")) {
                siButton.setText("OK");
                noButton.setVisibility(View.GONE);
            } else {
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if (valRegresarNo) startActivity(intent);
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
        String cveMaq = "" + alias.charAt(2) + alias.charAt(3);

        // Agregar prize counter
        camposContadores.put("*prizes", etxtPrizes);
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
                    int i = 0;
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

                        textView.setText(contador.toUpperCase() + " COUNTER");
                        editTexts.add(editText);
                        camposContadores.put(contador, editText);
                        textContadores.add(contador);
                        camposFotos.add(imageView);

                        layout.addView(linearLayout);
                        i += 3;
                    } while (i < sconts.length);
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

        contRegActual = Integer.parseInt(usuario.getContRegistro());
        contRegAnterior = contRegActual - 1;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        OnCompleteListener<DocumentSnapshot> listenerRegAnterior = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    try {
                        String alias = task.getResult().get("alias").toString();
                        String contRegistro = task.getResult().get("contRegistro").toString();
                        String fecha = task.getResult().get("fecha").toString();
                        String hora = task.getResult().get("hora").toString();
                        String nombre = task.getResult().get("nombre").toString();
                        String semanaFiscal = task.getResult().get("semanaFiscal").toString();
                        String sucursal = task.getResult().get("sucursal").toString();
                        String tipoMaquina = task.getResult().get("tipoMaquina").toString();
                        String ubicacion = task.getResult().get("ubicacion").toString();
                        String usuario = task.getResult().get("usuario").toString();
                        HashMap<String, String> contadores = (HashMap) task.getResult().get("contadores");
                        registroAnterior = new RegistroMaquina(alias,contRegistro,fecha,hora,nombre,semanaFiscal,
                                sucursal,tipoMaquina,ubicacion,usuario,contadores);
                        txtDatosAnteriores1.setText(registroAnterior.strDatosPrincipales());
                        txtDatosAnteriores2.setText(registroAnterior.strDatosSecundarios());
                    } catch (Exception e) {
                        Log.d("ERROR", "No existe registro anterior registrado.");
                    }
                }
            }
        };
        db.collection("registros_maquinas")
                .document(usuario.getId())
                .collection(contRegAnterior+"")
                .document(maquina.getAlias())
                .get()
                .addOnCompleteListener(listenerRegAnterior);
        progressDialog.dismiss();
    }

    public void registrarContadores() {

        // Fecha y hora
        Map<String, String> mapTime = Utils.getTime();
        String fecha = mapTime.get("fecha");
        String hora = mapTime.get("hora");
        String numSemana = mapTime.get("numSemana");

        // Registro
        HashMap<String, String> nvoRegistro = new HashMap<>();
        nvoRegistro.put("nombre", maquina.getNombre());
        nvoRegistro.put("alias", maquina.getAlias());
        nvoRegistro.put("fecha", fecha);
        nvoRegistro.put("hora", hora);
        nvoRegistro.put("ubicacion", ubicacion);
        nvoRegistro.put("semanaFiscal", numSemana);
        nvoRegistro.put("usuario", idUsuario);
        nvoRegistro.put("contRegistro", String.valueOf(contRegActual));
        nvoRegistro.put("sucursal", cveSucursal);
        nvoRegistro.put("tipoMaquina", cveTipo);
        HashMap<String, String> contadores = new HashMap<>();
        for (Map.Entry<String, EditText> entry: camposContadores.entrySet()) {
            nvoRegistro.put(entry.getKey(), entry.getValue().getText().toString());
            contadores.put(entry.getKey(), entry.getValue().getText().toString());
        }

        registroActual = new RegistroMaquina(maquina.getAlias(),
                usuario.getContRegistro(), fecha, hora, maquina.getNombre(),
                String.valueOf(numSemana), cveSucursal, cveTipo, ubicacion,idUsuario,contadores);
        maquina.setContadoresActuales(contadores.toString());

        // Subir fotos de contadores registrados
        uploadImages();


        // Actualizar usuario
        String alias = maquina.getAlias();
        String auxMaqReg = usuario.getMaqRegSuc();
        String auxSucReg = usuario.getSucRegistradas();
        ArrayList<String> maquinasRegistradas = new ArrayList<>();
        ArrayList<String> maquinasPorRegistrar = new ArrayList<>
                (Arrays.asList(sucursal.getMaquinas().replaceAll(" ", "").split(",")));
        ArrayList<String> sucursalesRegistradas = new ArrayList<>();
        ArrayList<String> sucursalesPorRegistrar = new ArrayList<>
                (Arrays.asList(usuario.getSucursales().replaceAll(" ", "").split(",")));
        Boolean regresarSinDialog = true;
        String mensajeFinal = "";

        if (!auxMaqReg.equals("")) {
            maquinasRegistradas = new ArrayList<>(Arrays.asList(usuario.getMaqRegSuc()
                    .replaceAll(" ", "").split(",")));
        }
        if (!auxSucReg.equals("")) {
            sucursalesRegistradas = new ArrayList<>(Arrays.asList(usuario.getSucRegistradas()
                    .replaceAll(" ", "").split(",")));
        }

        if (!maquinasRegistradas.contains(alias)) {
            maquinasRegistradas.add(alias);
        }
        Collections.sort(maquinasPorRegistrar);
        Collections.sort(maquinasRegistradas);
        if (maquinasPorRegistrar.equals(maquinasRegistradas)) {
            mensajeFinal = "Se terminaron de registrar las máquinas de la sucursal: " + sucursal.getNombre();
            maquinasRegistradas = new ArrayList<>();
            sucursalesRegistradas.add(sucursal.getClave());
            usuario.setSucRegistradas(sucursalesRegistradas.toString()
                    .replace("[", "").replace("]", ""));
            regresarSinDialog = false;
        }
        Collections.sort(sucursalesPorRegistrar);
        Collections.sort(sucursalesRegistradas);
        if (sucursalesPorRegistrar.equals(sucursalesRegistradas)) {
            mensajeFinal = "Se terminaron de registrar todas las maquinas de las sucursales " +
                    "asignadas al usuario: " + usuario.getNombre();
            usuario.setSucRegistradas("");
            usuario.setContRegistro(String.valueOf(contRegActual+1));
            regresarSinDialog = false;
//            enviarCalculos();
        }
        usuario.setMaqRegSuc(maquinasRegistradas.toString()
                .replace("[", "").replace("]", ""));


        // Subir registro
            // Realtime database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("registros_maquinas/" + idUsuario + "/"
                + (contRegActual)).child(maquina.getNombre()).setValue(nvoRegistro);
            // Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(idUsuario).set(usuario);
        Map<String, Object> reg = new HashMap<>();
        reg.put(usuario.getContRegistro(), registroActual);
        db.collection("registros_maquinas").document(idUsuario).set(reg);
                db.collection("registros_maquinas").document(idUsuario).
                collection(contRegActual+"").document(alias).set(registroActual);
        // Registrar contadores actuales en máquina
        HashMap<String, Object> map = new HashMap<>();
        map.put("contadoresActuales",contadores);
        db.collection("maquinas").document(maquina.getId()).update(map);

        registrarCalculo();

        if (regresarSinDialog) {
            Intent intent = new Intent(RegistrarContadores.this, HomeEmpleado.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
        } else {
            /*FCMSend.pushNotification(
                    RegistrarContadores.this,
                    tokensNotif,
                    usuario.getToken(),
                    "AmuseMe Registros",
                    mensajeFinal);*/
            mostrarMensajeFinal(mensajeFinal);
        }

    }

    public void registrarCalculo() {

        HashMap<String, String> restasContador = new HashMap<>();
        for (String contador: textContadores){
            int valRegActual = 0;
            int valRegAnterior = 0;
            if (registroActual.getContadores().get(contador)!=null)
                valRegActual = Integer.parseInt(registroActual.getContadores().get(contador));
            if (registroAnterior.getContadores().get(contador)!=null)
                valRegAnterior = Integer.parseInt(registroAnterior.getContadores().get(contador));
            restasContador.put(contador,String.valueOf((valRegActual-valRegAnterior)));
        }
        FirebaseDatabase.getInstance().getReference("calculos/" + usuario.getId())
                .child(contRegActual+"").child(cveSucursal).child(maquina.getAlias())
                .setValue(restasContador);
        FirebaseDatabase.getInstance().getReference("calculos/"+usuario.getId()+"/"+contRegActual)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    HashMap<String, String> totalSemana = new HashMap<>();
                    HashMap<String, HashMap<String,String>> totalSucursales = new HashMap<>();

                    for (DataSnapshot ds: task.getResult().getChildren()) {
                        HashMap<String, String> totalSucursal = new HashMap<>();
                        for (DataSnapshot ds1: ds.getChildren()){ // maquinas, key=SRBR02
                            if (!ds1.getKey().equals("total")){
                                for (DataSnapshot ds2: ds1.getChildren()){ // contadores, key=*coins
                                    int val = Integer.parseInt(ds2.getValue().toString());
                                    if (totalSucursal.containsKey(ds2.getKey())){
                                        int sumaAcumulada = Integer.parseInt(totalSucursal.get(ds2.getKey()));
                                        totalSucursal.put(ds2.getKey(),String.valueOf(sumaAcumulada+val));
                                    } else {
                                        totalSucursal.put(ds2.getKey(),ds2.getValue().toString());
                                    }
                                    if (totalSemana.containsKey(ds2.getKey())) {
                                        int sumaAcumulada = Integer.parseInt(totalSemana.get(ds2.getKey()));
                                        totalSemana.put(ds2.getKey(),String.valueOf(sumaAcumulada+val));
                                    } else{
                                        totalSemana.put(ds2.getKey(), ds2.getValue().toString());
                                    }
                                }
                            }
                            FirebaseDatabase.getInstance().getReference().child("calculos").child(usuario.getId())
                                    .child(contRegActual+"").child(ds.getKey()).child("total").setValue(totalSucursal);
                        }
                    }
                    FirebaseDatabase.getInstance().getReference().child("calculos").child(usuario.getId())
                            .child(contRegActual+"").child("total").setValue(totalSemana);
                }
            }
        });
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
                Log.e("ERROR FOTO", "Error creando foto: " + ex.getMessage());
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
        String imageFileName = "JPEG_" + timeStamp + "_" + maquina.getAlias() + "_" + contador + "_" + timeStamp;
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
                                if (task.isSuccessful()) {
                                    Log.d("Upload", "Imagen subida al servidor.");
                                } else {
                                    Log.e("Error Upload", "No se pudo subir imagen al servidor.");
                                }
                            }
                        });
            }
        } catch (Exception e) {
            Log.e("ErrorUploadImages", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void mostrarMensajeFinal(String mensaje) {
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
                intent.putExtra("usuario", usuario);
                startActivity(intent);
            }
        });
        sucDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(RegistrarContadores.this, HomeEmpleado.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void enviarCalculos() {

        FirebaseDatabase.getInstance().getReference("calculos/"+usuario.getId()+"/"+contRegActual)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String strCalculos = "El usuario: "+usuario.getNombre()+" terminó de registrar " +
                            "las máquinas de las sucursales registradas.<br><br>";
                    for (DataSnapshot ds: task.getResult().getChildren()) {
                        strCalculos += "<br><br>&nbsp&nbsp"+ds.getKey();
                        for (DataSnapshot ds1: ds.getChildren()) {
                            if (!ds.getKey().equals("total")) {
                                strCalculos += "<br>&nbsp&nbsp&nbsp&nbsp"+ds1.getKey();
                                for (DataSnapshot ds2: ds1.getChildren()) {
                                    String contadores = ds2.getKey()+": "+ ds2.getValue().toString();
                                    strCalculos+= "<br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp" + contadores;
                                }
                            }else{
                                strCalculos += "<br>&nbsp&nbsp&nbsp&nbsp"+
                                        ds1.getKey()+": "+ ds1.getValue().toString();
                            }
                        }
                    }
                    Map<String, String> mensaje = new HashMap<>();
                    Map<String, Object> mail = new HashMap<>();
                    mensaje.put("subject","Amuseme. Máquinas registradas");
                    mensaje.put("texto", strCalculos);
                    mensaje.put("html", strCalculos);
                    mail.put("to",Utils.ADMINISTRADORES);
                    mail.put("message",mensaje);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("mail").document().set(mail);
                }
            }
        });
    }

}