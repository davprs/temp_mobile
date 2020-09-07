package com.example.ilriccone.ui.profile_image;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Base64;
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
import androidx.appcompat.app.AppCompatActivity;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ilriccone.InternetUtilities;
import com.example.ilriccone.MainActivity;
import com.example.ilriccone.R;
import com.example.ilriccone.Utility;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileImageFragment extends Fragment {

    static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private String username;
    private Button load_image, signup;
    private TextInputEditText username_f, password_f1, password_f2;
    private Image images;
    private Bitmap imageBitmap;
    private Uri currentPhotoUri;
    private Activity activity;
    private String encodedImage = null;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_image, container, false);
    }

    //scegli immagine galleria
    public static Intent showFileChooser() {
        final Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        pickImageIntent.putExtra("aspectX", 1);
        pickImageIntent.putExtra("aspectY", 1);
        pickImageIntent.putExtra("scale", true);
        pickImageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        return pickImageIntent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        username = Utility.readFromPreferencesString(activity, "username");
        //InternetUtilities.makeSnackbar(getActivity(), R.id.upload_image_layout);
        load_image = getView().findViewById(R.id.captureButton_upload);
        imageView = getView().findViewById(R.id.profile_img_upload);
        String user_image = Utility.readFromPreferencesString(activity, "image");
        if(user_image.length() > 10){
            byte[] decodedString = Base64.decode(user_image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        }

        Utility.changeAppBarColor((AppCompatActivity)activity, Utility.MAIN_APPBAR_COLOR);


        load_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(showFileChooser(), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void uploadImage(){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        final String command = getString(R.string.server_address) + getString(R.string.upload_image_to_server);
// Request a string response from the provided URL.
        //Log.d("aac", command);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, command,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Log.d("aaa", "Server response : " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("aaa", error.toString());
            }

        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("username", username);
                params.put("image", encodedImage);

                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("aaa", "urlo");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {

            currentPhotoUri = data.getData();

            //Log.d("aab", String.valueOf(currentPhotoUri));
            // Load a specific media item, and show it in the ImageView
            Bitmap bitmap = Utility.getImageBitmap(activity, currentPhotoUri);
            if (bitmap != null) {
                ImageView imageView = getView().findViewById(R.id.profile_img_upload);
                imageView.setImageBitmap(bitmap);
                convertToBase64(bitmap);
                storeImage();
                uploadImage();
                Utility.reloadActivity(activity);
            }
        }
    }

    private void storeImage(){
// Request a string response from the provided URL.
        Utility.writeOnPreferences(activity, "image", encodedImage);

    }

    private void convertToBase64(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
        byte[] b = baos.toByteArray();

        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
    }
}
