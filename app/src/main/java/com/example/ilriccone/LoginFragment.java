package com.example.ilriccone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {
    TextInputEditText username, password;
    Boolean checked = false;
    TextInputEditText username_f, password_f;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button login_button = getView().findViewById(R.id.login_button);
        Button signup_button = getView().findViewById(R.id.signup_button);

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getActivity().getBaseContext(), SignupActivity.class);
                startActivity(intent);*/
                Utility.replaceFragment((AppCompatActivity) getActivity(), R.id.login_fragment_container, new SignupFragment(), "aaa");
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            /* PER RICHIEDERE DOMANDE ***************************************************
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(getBaseContext());
            String url ="https://opentdb.com/api.php?amount=10&category=9&difficulty=easy&type=multiple";

            // Request a string response from the provided URL.
            JSONObject jo = null;
            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //textView.setText("Response is: "+ response);
                            //Log.d("aaa", response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    textView.setText("That didn't work!");
                    //Log.d("aaa", error.toString());

                }
            });

            // Add the request to the RequestQueue.
            queue.add(jsonRequest);*/

                if (isLoginRight()){
                    if (InternetUtilities.getIsNetworkConnected()) {
                        try {
                            JSONObject user = makeRequest(new String(getString(R.string.server_address) + "/checkUser.php?username=" + username.getText()
                                    + "&password=" + password.getText()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        InternetUtilities.getSnackbar().show();
                    }
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        InternetUtilities.registerNetworkCallback(getActivity());
    }

    JSONObject res_array = new JSONObject();
    RequestQueue queue = null;

    @Override
    public void onStop() {
        super.onStop();
        if (queue != null) {
            //cancel the request to osm
            queue.cancelAll(InternetUtilities.OSM_REQUEST_TAG);
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            //unregistered the callback
            connectivityManager.unregisterNetworkCallback(InternetUtilities.getNetworkCallback());
        }
    }

    private JSONObject makeRequest(String url) throws JSONException {
        JSONObject res;
        queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        JSONObject jo = null;
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            response.getString("1");
                            TextView textView = getView().findViewById(R.id.login_text);
                            Snackbar.make(getView(), R.string.unsuccessful_login, Snackbar.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            Intent mainIntent = new Intent(getContext(), MainActivity.class);
                            //Log.d("ddd", "lol1  : " + response.toString());

                            if(response.remove("user_image").toString().length()>10) {
                                //Log.d("ddd", "putting extra img = 1");
                                mainIntent.putExtra("img", "1");
                            }
                            mainIntent.putExtra("json", response.toString());
                            //Log.d("ddd", "lol2  : " + response.toString());

                            //Log.d("aaa", response.toString());
                            startActivity(mainIntent);

                            res_array = response;
                        }

                        //Log.d("aaa", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");
                //Log.d("aaa", error.toString());
                TextView textView = getView().findViewById(R.id.login_text);
                textView.setText("Response is: " + error.toString());

            }
        });
        //Log.d("aaa", "check2");

        // Add the request to the RequestQueue.
        //jsonRequest.setTag(InternetUtilities.OSM_REQUEST_TAG);
        queue.add(jsonRequest);
        res = res_array;

        //Log.d("aaa", res);
        return res;
    }

    private Boolean isLoginRight() {
        final int MAX_USER = 20, MAX_PASS = 25;

        username = getView().findViewById(R.id.login_username);
        password = getView().findViewById(R.id.login_password);

        if (username.length() <= MAX_USER) {
            if (password.length() <= MAX_PASS){
                return true;
            } else {
                Snackbar.make(getView(), R.string.password_too_long, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(getView(), R.string.username_too_long, Snackbar.LENGTH_SHORT).show();

        }
        return false;
    }
}