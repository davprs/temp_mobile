package com.example.ilriccone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        JSONObject mJsonObject = null;
        if(getIntent().hasExtra("json")) {
            try {
                mJsonObject = new JSONObject(getIntent().getStringExtra("json"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (mJsonObject != null || Utility.readFromPreferences(this, "logged_status") == true) {
            Intent loginIntent = new Intent(getBaseContext(), SideDrawer.class);

            if (mJsonObject != null){
                Log.d("aaa","innn");
                loginIntent.putExtra("json", mJsonObject.toString());
            }

            Utility.writeOnPreferences(this, "logged_status", true);

            startActivity(loginIntent);
            finish();

            //Utility.insertFragment(this, new StartFragment(), "afterLogin");
        } else {


            Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
            Log.d("aaa", getBaseContext().toString());
            startActivity(loginIntent);
            finish();

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("aaa", "pre!_TOTMAINACT");
    }
}


