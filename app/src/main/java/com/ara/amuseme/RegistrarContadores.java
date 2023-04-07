package com.ara.amuseme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private int contRegActual;
    private String idUsuario;
    private ArrayList<String> nombresMaquinas;
    private ArrayList<String> maquinasRegistradas;

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

        // Initializations
        maquina = new Maquina();
        camposContadores = new ArrayList<>();
        textContadores = new ArrayList<>();
        camposFotos = new ArrayList<>();
        contRegActual = 0;
        idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        maquinasRegistradas = new ArrayList<>();

        // Get extras
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Intent intent = getIntent();
            nombresMaquinas = (ArrayList<String>) intent.getExtras().getSerializable("nombresMaquinas");
            String maq = intent.getExtras().getString("nombre");
            maquina.setNombre(maq);
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

        // Set views
        btnRegistrarMaquina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarContadores();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegistrarContadores.this, HomeEmpleado.class);
        startActivity(intent);
        finish();
    }

    public void getFirebaseData() {
        ProgressDialog progressDialog = new ProgressDialog(RegistrarContadores.this);
        progressDialog.setMessage("Obteniendo maquinas...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Maquina
        OnCompleteListener<QuerySnapshot> listenerMaq = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String alias = task.getResult().getDocuments().get(0).get("alias").toString();
                    maquina.setAlias(alias);
                    txtNombreMaquina.setText("Máquina:  " + maquina.getAlias());
                    progressDialog.cancel();
                    setContadores(alias);
                }
            }
        };
        db.collection("maquinas")
                .whereEqualTo("nombre", maquina.getNombre())
                .get()
                .addOnCompleteListener(listenerMaq);



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
                                Toast.makeText(RegistrarContadores.this, "Take photo", Toast.LENGTH_SHORT).show();
                            }
                        });

                        textView.setText(sconts[i].toUpperCase());
                        editTexts.add(editText);
                        camposContadores.add(editText);
                        textContadores.add(sconts[i]);
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
        // Confirmar datos necesarios
        for (EditText e: camposContadores){
            if (TextUtils.isEmpty(e.getText().toString())){
                e.setError("Este campo debe ser registrado.");
                return;
            }
        }
        // Metadata
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
        nvoRegistro.put("sucursal",maquina.getAlias().charAt(0)+""+maquina.getAlias().charAt(1));
        nvoRegistro.put("tipoMaquina",maquina.getAlias().charAt(2)+""+maquina.getAlias().charAt(3));
        for (int i=0; i<textContadores.size(); i++){
            nvoRegistro.put(textContadores.get(i), camposContadores.get(i).getText().toString());
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("registros_maquinas/"
                + idUsuario + "/" + (contRegActual));
        dbRef.child(maquina.getNombre()).setValue(nvoRegistro);

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarContadores.this);
        String mensaje = "Máquina "+maquina.getAlias()+" registrada.";
        if(maquinasRegistradas.size() == nombresMaquinas.size()){
            mensaje+="\n Se terminaron de registrar todas las máquinas";
        }
        builder.setMessage(mensaje)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(RegistrarContadores.this, HomeEmpleado.class));
                        finish();
                    }
                })
                .setCancelable(false).show();


    }

}