package com.example.ilriccone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class SignupFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 12345;
    private Activity activity;
    Fragment fragment = this;
    Button load_image;
    String filePath;
    private String username = null;
    private String password = null;
    Button signup;
    ImageView imageView;
    TextInputEditText username_f, password_f1, password_f2;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.signup_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        InternetUtilities.registerNetworkCallback(activity);
        load_image = getView().findViewById(R.id.captureButton);
        imageView = getView().findViewById(R.id.imageView);
        signup = getView().findViewById(R.id.signup_button1);
        username_f = getView().findViewById(R.id.signup_username);
        password_f1 = getView().findViewById(R.id.signup_password1);
        password_f2 = getView().findViewById(R.id.signup_password2);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSignupFormRight()) {
                    makeRequest(getString(R.string.server_address) + "/insertUser?username=" +
                            username_f.getText() + "&password=" + password_f1.getText());

                } else {
                    Log.d("aaa", "pre!_5");
                    InternetUtilities.getSnackbar().show();
                }
            }
        });

        load_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("aaa", "pre!_1");
                if (InternetUtilities.getIsNetworkConnected()) {
                    Log.d("aaa", "pre!_2");
                    if (ActivityCompat.checkSelfPermission(activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("aaa", "pre!_3");

                        requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                123);
                    } else {
                        Log.d("aaa", "pre!_4");
                        startGallery();
                    }
                }
            }
        });
    }
    /*

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("aaa", "pre!_TOT2");

        if (resultCode == RESULT_OK) {
            if (requestCode == 123) {
                Uri returnUri = data.getData();
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bitmapImage);
            }
        }
    }*/

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("aaa", "ONACT");
        if (resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }



    private void startGallery() {
        Log.d("aaa", "pre!_TOT");
        dispatchTakePictureIntent();
        Log.d("aaa", "pre!_TOT1/2");
    }

    private boolean isSignupFormRight() {
        return true;
    }


    private void makeRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(getContext());

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
                            Snackbar.make(getView(), R.string.user_already_in, Snackbar.LENGTH_SHORT).show();
                            username_f.setText("");
                            password_f1.setText("");
                            password_f2.setText("");
                        } else {
                            Intent mainIntent = new Intent(getContext(), MainActivity.class);
                            mainIntent.putExtra("json", response.toString());

                            Log.d("aaa", response.toString());
                            startActivity(mainIntent);
                        }

                        //Log.d("aaa", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");
                Log.d("aaa", error.toString());
                TextView textView = getView().findViewById(R.id.login_text);
                textView.setText("Response is: " + error.toString());

            }
        });
        Log.d("aaa", "check2");

        // Add the request to the RequestQueue.
        //jsonRequest.setTag(InternetUtilities.OSM_REQUEST_TAG);
        queue.add(jsonRequest);

        //Log.d("aaa", res);
    }

}
