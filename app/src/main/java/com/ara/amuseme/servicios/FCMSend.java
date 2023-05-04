package com.ara.amuseme.servicios;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FCMSend {
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY = "key=AAAA4oqpl7Y:APA91bFX936OVFETu4WooaKaXT1i-p1cmU7Uek9-5ogF-BoKMldHc9rWZwpVWLvpxN2NgKEN6H7Oe7VOky4xQO6k-m_mB1qXRG_S0i9nC8QA8NAQgSvoGCFju5PWULLcsQedQjGcdZxg";

    public static void pushNotification(Context context, ArrayList<String> tokens, String exceptToken,
                                        String title, String message) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);

        if (tokens.isEmpty()) return;
        tokens.remove(exceptToken);

        for (String token: tokens) {
            try{
                JSONObject json = new JSONObject();
                json.put("to", token);
                JSONObject notification = new JSONObject();
                notification.put("title",title);
                notification.put("body", message);
                json.put("notification",notification);
//                JSONObject data = new JSONObject();
//                data.put("nick","nick");
//                data.put("room","room");
//                json.put("data",data);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        BASE_URL, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println("FCM response:  "+response);
                                Log.d("FCM", response.toString());
                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("ERROR",error.toString());
                                System.out.println(error.getMessage());
                            }
                        })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", SERVER_KEY);
                        return params;
                    }
                };

                queue.add(jsonObjectRequest);
//                Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}