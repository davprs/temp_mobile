package com.example.ilriccone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SignupActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 12345;
    private Activity activity;
    Button load_image;
    String filePath;
    private String username = null;
    private String password = null;
    Button signup;
    ImageView imageView;
    TextInputEditText username_f, password_f1, password_f2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_fragment);


        activity = this;
        InternetUtilities.makeSnackbar(activity, R.id.signup_fragment);
        InternetUtilities.registerNetworkCallback(activity);
        load_image = findViewById(R.id.captureButton);
        imageView = findViewById(R.id.imageView);
        signup = findViewById(R.id.signup_button1);
        username_f = findViewById(R.id.signup_username);
        password_f1 = findViewById(R.id.signup_password1);
        password_f2 = findViewById(R.id.signup_password2);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSignupFormRight()) {
                    makeRequest(getString(R.string.server_address) + "/insertUser?username=" +
                            username_f.getText() + "&password=" + password_f1.getText());

                } else {
                    //Log.d("aaa", "pre!_5");
                    InternetUtilities.getSnackbar().show();
                }
            }
        });

        load_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("aaa", "pre!_1");
                if (InternetUtilities.getIsNetworkConnected()) {
                    //Log.d("aaa", "pre!_2");
                    if (ActivityCompat.checkSelfPermission(activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        //Log.d("aaa", "pre!_3");

                    } else {
                        //Log.d("aaa", "pre!_4");
                        startGallery();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,        // prefix
                ".jpg",         // suffix
                storageDir            // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),"com.example.ilriccone",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }



    private void startGallery() {
        //Log.d("aaa", "pre!_TOT");
        dispatchTakePictureIntent();
        //Log.d("aaa", "pre!_TOT1/2");
    }

    private boolean isSignupFormRight() {
        return true;
    }


    private void makeRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(activity);

        // Request a string response from the provided URL.
        JSONObject jo = null;
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String res = "";
                        try {
                            res = response.getString("2");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (res.equals("already in")){
                            //Snackbar.make(), R.string.user_already_in, Snackbar.LENGTH_SHORT).show();
                            username_f.setText("");
                            password_f1.setText("");
                            password_f2.setText("");
                        } else {
                            Intent mainIntent = new Intent(activity, MainActivity.class);
                            mainIntent.putExtra("json", response.toString());

                            //Log.d("aaa", response.toString());
                            startActivity(mainIntent);
                        }

                        //Log.d("aaa", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");
                //Log.d("aaa", error.toString());
                TextView textView = findViewById(R.id.login_text);
                textView.setText("Response is: " + error.toString());

            }
        });
        //Log.d("aaa", "check2");

        // Add the request to the RequestQueue.
        //jsonRequest.setTag(InternetUtilities.OSM_REQUEST_TAG);
        queue.add(jsonRequest);

        //Log.d("aaa", res);
    }
}
