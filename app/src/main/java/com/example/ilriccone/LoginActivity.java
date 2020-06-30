package com.example.ilriccone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.ilriccone.ui.home.HomeFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Il riccone";
    private static final String FRAGMENT_TAG = "signupFragment";
    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 123;


    private Uri currentPhotoUri;
    private EditText username, password;
    private Boolean checked = false;
    final private Activity activity = this;
    private Object Dexter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_fragment_container);

        //Utility.insertFragment(this, R.id.login_fragment_container, new LoginFragment(),  FRAGMENT_TAG);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("aaa", "SLKDLSkdl");

    }

    @Override
    protected void onStart() {
        super.onStart();
        InternetUtilities.registerNetworkCallback(this);
    }

    JSONObject res_array = new JSONObject();
    RequestQueue queue = null;

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            //cancel the request to osm
            queue.cancelAll(InternetUtilities.OSM_REQUEST_TAG);
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            //unregistered the callback
            connectivityManager.unregisterNetworkCallback(InternetUtilities.getNetworkCallback());
        }
    }
}
