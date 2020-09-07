package com.example.ilriccone;

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

public class SignupFragment extends Fragment {

    static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
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
        return inflater.inflate(R.layout.signup_fragment, container, false);
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
        InternetUtilities.makeSnackbar(getActivity(), R.id.signup_fragment);
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
                    makeRequest(getString(R.string.server_address) + "/insertUser.php?username=" +
                            username_f.getText() + "&password=" + password_f1.getText());

                } else {
                    Snackbar.make(getView(), R.string.unsuccessful_login, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        load_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(showFileChooser(), PICK_IMAGE_REQUEST);
            }
        });
    }

    private boolean isSignupFormRight() {
        return (password_f1.getText().toString().equals(password_f2.getText().toString()) && username_f.getText().toString().length() > 0);
    }


    private void makeRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(activity);

        // Request a string response from the provided URL.
        JSONObject jo = null;
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (encodedImage != null)
                            uploadImage();

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
                            if (encodedImage != null){
                                mainIntent.putExtra("img", "1");
                            }

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
            }
        });
        //Log.d("aaa", "check2");

        // Add the request to the RequestQueue.
        //jsonRequest.setTag(InternetUtilities.OSM_REQUEST_TAG);
        queue.add(jsonRequest);

        //Log.d("aaa", res);
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
                params.put("username", username_f.getText().toString());
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
           // String imagePath = currentPhotoUri.getPath();

            /*Bundle extras = data.getExtras();
            if (extras != null) {
                imageBitmap = (Bitmap) extras.get("data");
                try {
                    if (imageBitmap != null) {
                        //method to save the image in the gallery of the device
                        saveImage(imageBitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/

            //Log.d("aab", String.valueOf(currentPhotoUri));
            // Load a specific media item, and show it in the ImageView
            Bitmap bitmap = Utility.getImageBitmap(activity, currentPhotoUri);
            if (bitmap != null) {
                ImageView imageView = getView().findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
                convertToBase64(bitmap);
            }
        }
    }

    private void convertToBase64(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
        byte[] b = baos.toByteArray();

        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
    }

    private void saveImage(Bitmap bitmap) throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(new Date());
        String name = "JPEG_" + timeStamp + "_.png";

        ContentResolver resolver = activity.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        currentPhotoUri = imageUri;
        OutputStream fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));

        //for the jpeg quality, it goes from 0 to 100
        //for the png one, the quality is ignored
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        if (fos != null) {
            fos.close();
        }
    }

}
