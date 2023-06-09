package com.ara.amuseme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ara.amuseme.servicios.QRCodeReader;
import com.ara.amuseme.modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class HomeEmpleado extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout btnRegistrarContadores;
    private LinearLayout btnRegistrarDeposito;
    private LinearLayout btnRegistrarVisita;
    private ArrayList<String> maquinas;
    private FirebaseUser user;
    private Usuario usuario;
    private String ubicacion;
    private ArrayList<String> tokensToNotif;

    private static final int CODIGO_PERMISOS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_empleado);
        // Permisos
        checarPermisos();

        btnRegistrarContadores = findViewById(R.id.btnRegistrarContadores);
        btnRegistrarDeposito = findViewById(R.id.btnRegistrarDeposito);
        btnRegistrarVisita = findViewById(R.id.btnRegistrarVisita);
        btnRegistrarContadores.setOnClickListener(this);
        btnRegistrarDeposito.setOnClickListener(this);
        btnRegistrarVisita.setOnClickListener(this);

        maquinas = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        usuario = new Usuario();
        ubicacion = "";
        tokensToNotif = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = getIntent().getExtras().getParcelable("usuario");
            setTitle(usuario.getNombre());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeEmpleado.this);
            builder.setMessage("Error obteniendo datos. Contacte al administrador.")
                    .setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(HomeEmpleado.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .setCancelable(false).show();
        }

        getDBdata();
        getUbicacion();
        getTokensToNotif();

        getTestMaquinas();

    }

    public void getTestMaquinas() {
        ProgressDialog progressDialog = new ProgressDialog(HomeEmpleado.this);
        progressDialog.setMessage("Obteniendo tipos de máquinas...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OnCompleteListener<QuerySnapshot> listenerUsuario = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    for (DocumentSnapshot ds: task.getResult().getDocuments()){
                        if (ds.get("alias").toString().equals("APGT01")) {
//                            Toast.makeText(HomeEmpleado.this, ds.get("alias").toString(), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(HomeEmpleado.this, ds.get("contadoresActuales").toString(), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(HomeEmpleado.this, ds.get("contadoresActuales").getClass().toString(), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(HomeEmpleado.this, (HashMap) ds.get("contadoresActuales").get("*coins"), Toast.LENGTH_SHORT).show();
                            HashMap<String, String> test = (HashMap<String, String>) ds.get("contadoresActuales");
//                            Toast.makeText(HomeEmpleado.this, test.toString(), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(HomeEmpleado.this, test.get("*coins"), Toast.LENGTH_SHORT).show();
                        }

                    }
                    progressDialog.cancel();
                }
            }
        };
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("maquinas").orderBy("alias").get()
                .addOnCompleteListener(listenerUsuario);
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
                intent.putExtra("ubicacion", ubicacion);
                intent.putExtra("tokensNotif", tokensToNotif);
                startActivity(intent);
                break;
            case R.id.btnRegistrarDeposito:
                intent = new Intent(HomeEmpleado.this, RegistrarDeposito.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("ubicacion", ubicacion);
                intent.putExtra("tokensNotif", tokensToNotif);
                startActivity(intent);
                break;
            case R.id.btnRegistrarVisita:
                intent = new Intent(HomeEmpleado.this, QRCodeReader.class);
                intent.putExtra("maquinas", maquinas);
                intent.putExtra("actividad","visita");
                intent.putExtra("usuario", usuario);
                intent.putExtra("ubicacion", ubicacion);
                intent.putExtra("tokensNotif", tokensToNotif);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeEmpleado.this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void scripParaRealizarCalculos() {
        OnCompleteListener<QuerySnapshot> listenerUsuario = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    HashMap<String, HashMap<String, String>> maquina = new HashMap<>();
                    for (DocumentSnapshot d: task.getResult().getDocuments()) {
                        String maq = d.getData().get("contadores").toString().replaceAll(" ","")
                                .replace("{","").replace("}","");
                        String conts[] = maq.split(",");
                        HashMap<String, String> contador = new HashMap<>();
                        for (String valor: conts){
                            String v[] = valor.split("=");
                            contador.put(v[0],v[1]);
                        }
                        maquina.put(d.getId(),contador);
                    }

                    HashMap<String, HashMap<String, HashMap<String,String>>> porSucursal = new HashMap<>();
                    HashMap<String, HashMap<String, String>> porMaquina = new HashMap<>();
                    for (Map.Entry<String,HashMap<String, String>> maq: maquina.entrySet()) {
                        String suc = maq.getKey().substring(0,2);
                        if (porSucursal.containsKey(suc)) {
                            porMaquina.put(maq.getKey(),maq.getValue());
                        } else {
                            porMaquina = new HashMap<>();
                            porMaquina.put(maq.getKey(), maq.getValue());
                            porSucursal.put(suc,porMaquina);
                        }
                    }
                    for (Map.Entry<String,HashMap<String, HashMap<String, String >>> sucursal: porSucursal.entrySet()) {
                        Toast.makeText(HomeEmpleado.this, sucursal.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.collection("registros_maquinas").document(user.getUid()).collection("17").get()
                    .addOnCompleteListener(listenerUsuario);
        }catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

        OnCompleteListener<QuerySnapshot> listenerMaquina = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    for (DocumentSnapshot ds: task.getResult().getDocuments()) {
                        maquinas.add(ds.get("nombre").toString());
                        progressDialog.dismiss();
                    }
                }
            }
        };
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.collection("maquinas").get()
                    .addOnCompleteListener(listenerMaquina);
        }catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void getUbicacion() {
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.d("Ubicacion", "No se tienen los permisos para la ubicacion.");
            return;
        }
        final LocationListener listenerUbicacion = new LocationListener() {


            @Override
            public void onLocationChanged(Location location) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent()) {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("ERROR", "Error obteniendo ubicacion");
                    }
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        ubicacion = address.getAddressLine(0);
                        Log.d("Ubicación: ", ubicacion);
                    }
                }
            }

            @Override
            public void onLocationChanged(List<Location> locations) {
            }

            @Override
            public void onFlushComplete(int requestCode) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10000,
                10,
                listenerUbicacion);
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

}