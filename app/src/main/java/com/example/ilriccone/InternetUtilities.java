package com.example.ilriccone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class InternetUtilities {

    private static Boolean isNetworkConnected = false;
    private static Snackbar snackbar;
    public final static String OSM_REQUEST_TAG = "OSM_REQUEST";
    private static Activity activ;

    //callback that keep monitored the internet connection
    private static ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            isNetworkConnected = true;
            final String temp = Utility.readFromPreferencesString(activ, "scores_update");
            Log.d("aaa", "wth : " + temp);
            final String[] writable = {""};
            String[] updates = temp.replace("nulla", "").split(",");

            for (final String update : updates){
                RequestQueue queue = Volley.newRequestQueue(activ.getBaseContext());
                Log.d("aaa", "here : " + update);

// Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, update,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                Log.d("aaa", "Server response : " + response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        writable[0] += update + ",";
                        Log.d("aaa", "Server request : Something went wrong!");
                    }

                });

// Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
            //Log.d("aaa", "Lol1 ... " + writable[0]);
            //Utility.writeOnPreferences(activ, "scores_update", writable[0]);

            Log.d("aaa", "Lol2 ... " + temp);
            snackbar.dismiss();
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            isNetworkConnected = false;
            snackbar.show();
        }
    };

    public static void registerNetworkCallback(Activity activity) {
        Log.d("LAB","registerNetworkCallback");
        activ = activity;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            //api 24, android 7
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                //Class deprecated since API 29 (android 10) but used for android 5 and 6
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                isNetworkConnected = networkInfo != null && networkInfo.isConnected();
            }
        } else {
            isNetworkConnected = false;
        }
    }

    public static Boolean getIsNetworkConnected() {
        return isNetworkConnected;
    }

    public static void makeSnackbar(final Activity activity, final Integer layoutId){
        snackbar = Snackbar.make(
                activity.findViewById(layoutId),
                R.string.no_intern_available,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        setSettingsIntent(activity);
                    }
                });
    }

    private static void setSettingsIntent(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    public static ConnectivityManager.NetworkCallback getNetworkCallback(){
        return networkCallback;
    }

    public static Snackbar getSnackbar(){
        return snackbar;
    }
}

