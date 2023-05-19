package com.ara.amuseme;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Utils {

    public static final String ADMINISTRADORES = "ara.lj.uaa@gmail.com,diazserranoricardo1@gmail.com" +
            ",gencovending@gmail.com";
    public static final String[] ROLS = {"EMPLEADO", "ADMIN"};


    public static void generateDeviceToken(String userid) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {

                    @Override
                    public void onComplete(Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("ERROR", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Agregar token a Firestore
                        Map<String, Object> tokenData = new HashMap<>();
                        tokenData.put("deviceToken", token);
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection("usuarios").document(userid).update("token",token)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("OK", "Device Token agregado.");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.w("ERROR", "Error al agregar Device Token");
                                    }
                                });
                        Map<String, String> hmToken = new HashMap<>();
                        hmToken.put("token",token);
                        firestore.collection("DeviceTokens").document(token).set(hmToken)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("OK", "Device Token agregado.");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.w("ERROR", "Error al agregar Device Token");
                                    }
                                });

                    }
                });
    }

    public static Map<String, String> getTime() {
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
        Map<String, String> mapTime = new HashMap<>();
        mapTime.put("fecha", fecha);
        mapTime.put("hora", hora);
        mapTime.put("numSemana", numSemana+"");
        return mapTime;
    }
}
