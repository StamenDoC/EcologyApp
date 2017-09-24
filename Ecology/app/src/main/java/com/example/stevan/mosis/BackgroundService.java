package com.example.stevan.mosis;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Stevan Zivkovic on 22-Sep-17.
 */

public class BackgroundService extends Service {

    Integer userId;
    TimerTask timerTask;
    Timer timer;
    SharedPreferences prefs;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("SERVICE started!", "TRUE");
        prefs = getSharedPreferences("Logging info", MODE_PRIVATE);
        userId = prefs.getInt("userId", 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("Pokrece se", "DA");

        timerTask = new TimerTask() {
            @Override
            public void run() {
                //CALL YOUR ASSYNC TASK HERE.
                String longitude = prefs.getString("currentLon", "");
                String latitude = prefs.getString("currentLat", "");
                if(!longitude.equals("") && !latitude.equals(""))
                {
                    new CheckFriendsOrObjects().execute(String.valueOf(userId), "friends", longitude, latitude);
                    new CheckFriendsOrObjects().execute(String.valueOf(userId), "object", longitude, latitude);
                    new SetPlayerCordsTask().execute(String.valueOf(userId), longitude, latitude);
                }

            }
        };

        timer = new Timer();
        Integer timePeriod = 30*1000;
        timer.schedule(timerTask, timePeriod, timePeriod);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
    }

    public class SetPlayerCordsTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {

            String urlAddress = "http://qosstamen.esy.es/Server/set_player_cords.php";

            final String userId = strings[0];
            final String longitude = strings[1];
            final String latitude = strings[2];

            try {

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest request = new StringRequest(StringRequest.Method.POST, urlAddress, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Set cords response", response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();

                        hashMap.put("id", userId);
                        hashMap.put("lon", longitude);
                        hashMap.put("lat", latitude);

                        return hashMap;
                    }
                };

                requestQueue.add(request);

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String aVoid) {

        }
    }

    public class CheckFriendsOrObjects extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {

            String urlAddress = "http://qosstamen.esy.es/Server/check_friends_or_object.php";

            final String userId = strings[0];
            final String type = strings[1];
            final String longitude = strings[2];
            final String latitude = strings[3];

            try {

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest request = new StringRequest(StringRequest.Method.POST, urlAddress, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Friend or obj response", response);

                        if(type.equals("friends") && response.equals("1"))
                        {
                            Intent intent = new Intent();
                            intent.putExtra("notificationId", 1); // pretraga prijatelja
                            intent.setAction("com.example.stevan.mosis.FRIEND_OR_OBJECT_CLOSE");
                            sendBroadcast(intent);
                        }
                        else if(type.equals("object") && response.equals("1"))
                        {
                            Intent intent = new Intent();
                            intent.putExtra("notificationId", 2); // pretraga objekata
                            intent.setAction("com.example.stevan.mosis.FRIEND_OR_OBJECT_CLOSE");
                            sendBroadcast(intent);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();

                        hashMap.put("id", userId);
                        hashMap.put("type_search", type);
                        hashMap.put("lon", longitude);
                        hashMap.put("lat", latitude);

                        return hashMap;
                    }
                };

                requestQueue.add(request);

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String aVoid) {

        }
    }
}
