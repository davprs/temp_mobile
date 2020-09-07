package com.example.ilriccone;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.LayoutInflaterCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.zip.Inflater;

public class DifficultyFragment extends Fragment {
    private Activity activity;
    private String category;
    private LayoutInflater inflater;
    private ConstraintLayout loading_layout;


    public DifficultyFragment(String category){
        this.category = category;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_difficulty, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        //Log.d("aaa", category);
        loading_layout = (ConstraintLayout)getView().findViewById(R.id.loading_constraint_layout);
        loading_layout.setVisibility(View.GONE);
        setupButtons();
    }

    @Override
    public void onStart() {
        super.onStart();
        //InternetUtilities.makeSnackbar(getActivity(), R.id.fragment_difficulty);
        //InternetUtilities.registerNetworkCallback(getActivity());
    }

    private void setupButtons(){
        Button b1 = getView().findViewById(R.id.buttonDif1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InternetUtilities.getIsNetworkConnected()) {
                    InternetUtilities.getSnackbar().dismiss();
                    makeRequest(category, getString(R.string.difficulty_1));
                } else {
                    InternetUtilities.getSnackbar().show();
                }
            }
        });

        Button b2 = getView().findViewById(R.id.buttonDif2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InternetUtilities.getIsNetworkConnected()) {
                    InternetUtilities.getSnackbar().dismiss();
                    makeRequest(category, getString(R.string.difficulty_2));
                } else {
                    InternetUtilities.getSnackbar().show();
                }
            }
        });


        Button b3 = getView().findViewById(R.id.buttonDif3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InternetUtilities.getIsNetworkConnected()) {
                    InternetUtilities.getSnackbar().dismiss();
                    makeRequest(category, getString(R.string.difficulty_3));
                } else {
                    InternetUtilities.getSnackbar().show();
                }
            }
        });

    }

    private void makeRequest(final String category, String difficulty){
        loading_layout.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String requestCategory = "23";
        String temp = category.toLowerCase();
        if (temp.equals(getString(R.string.category_1)))
            requestCategory = "23";
        else if (temp.equals(getString(R.string.category_2)))
            requestCategory = "22";
        else if (temp.equals(getString(R.string.category_3)))
            requestCategory = "18";
        else if (temp.equals(getString(R.string.category_4)))
            requestCategory = "28";

        temp = difficulty.toLowerCase();
        if (temp.equals(getString(R.string.difficulty_1)))
            difficulty = "easy";
        else if (temp.equals(getString(R.string.difficulty_2)))
            difficulty = "medium";
        else if (temp.equals(getString(R.string.difficulty_3)))
            difficulty = "hard";

        String url ="https://opentdb.com/api.php?amount=10&category=" + requestCategory
                + "&difficulty=" + difficulty + "&type=multiple";
        //Log.d("aaa", "query: " + url);

        // Request a string response from the provided URL.
        JSONObject jo = null;
        final String finalDifficulty = temp;
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //textView.setText("Response is: "+ response);
                        try {
                            saveQuestions(activity, response);

                            Utility.changeAppBarColor((AppCompatActivity)activity, finalDifficulty);
                            Utility.replaceFragment((AppCompatActivity)activity, R.id.home_fragment,
                                    new QuestionFragment(1, category, finalDifficulty), "aaa");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Log.d("aaa", response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("aaa", error.toString());

            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    private void saveQuestions(Activity activity, JSONObject json) throws JSONException {
        JSONArray questions = new JSONArray(json.getString("results"));

        questions.length();

        //Log.d("aaa", "len:" + String.valueOf(questions.length()));

        for (int i = 0; i < questions.length(); i++){
            Utility.writeOnPreferences(activity, "question_" + String.valueOf(i + 1), questions.get(i).toString());
        }

    }

}
