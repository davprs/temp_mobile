package com.example.ilriccone;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ilriccone.ui.home.HomeFragment;

public class QuizEndFragment extends Fragment {

    private final String category, difficulty;
    private Activity activity;
    private int difficultyMultiplier = 1;
    private Integer quitzScore = 0;

    public QuizEndFragment(String category, String difficulty){
        this.category = category;
        this.difficulty = difficulty;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quiz_end_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupQuitzEndFragment();
    }

    private void setupQuitzEndFragment(){
        TextView category = getView().findViewById(R.id.quiz_result_category);
        TextView difficulty = getView().findViewById(R.id.quiz_result_difficulty);
        TextView score = getView().findViewById(R.id.quiz_result_score);
        TextView bestScore = getView().findViewById(R.id.quiz_result_best_score);
        Button goHome = getView().findViewById(R.id.quiz_result_goto_home);

        category.setText(getString(R.string.quiz_result_category, this.category));
        difficulty.setText(getString(R.string.quiz_result_difficulty, this.difficulty));
        score.setText(getString(R.string.quiz_result_score, String.valueOf(getQuitzScore())));
        bestScore.setText(getString(R.string.quiz_result_best_score, String.valueOf(getBestScoreAndUpdate(quitzScore))));

        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.replaceQuestionFragment((AppCompatActivity)activity, R.id.home_fragment, new HomeFragment(), "aaa");
            }
        });
    }

    public Integer getQuitzScore() {
        if (difficulty.equals(getString(R.string.difficulty_1))) {
            difficultyMultiplier = 1;
        } else if (difficulty.equals(getString(R.string.difficulty_2))) {
            difficultyMultiplier = 2;
        } else {
            difficultyMultiplier = 3;
        }
        int temp = 0;
        for (int i = 1; i <= 10; i++){
            if (Utility.readFromPreferences(activity, "question_" + i + "_answer_status")) {
                temp += 1*difficultyMultiplier;
            }
        }

        quitzScore = temp;
        return quitzScore;
    }

    private Integer getBestScoreAndUpdate(int quitzScore){
        if (quitzScore == 0){
            quitzScore = getQuitzScore();
        }
        int oldBestScore = Utility.readFromPreferencesInt((AppCompatActivity)activity, Utility.getScoreString(activity, category, difficulty));
        if (oldBestScore < quitzScore){
            updateLocal(quitzScore);
            updateOnServer(quitzScore);
            return  quitzScore;
        } else {
            return oldBestScore;
        }
    }

    private void updateLocal(int quitzScore){
        Utility.writeOnPreferences((AppCompatActivity)activity, Utility.getScoreString(activity, category, difficulty),
                quitzScore);
    }

    private void updateOnServer(int bestScore){
        String urlCategory = "0", urlDifficulty="0";
        String temp = category.toLowerCase();
        if (temp.equals(getString(R.string.category_1)))
            urlCategory = getString(R.string.category_1_code);
        else if (temp.equals(getString(R.string.category_2)))
            urlCategory = getString(R.string.category_2_code);
        else if (temp.equals(getString(R.string.category_3)))
            urlCategory = getString(R.string.category_3_code);
        else if (temp.equals(getString(R.string.category_4)))
            urlCategory = getString(R.string.category_4_code);

        temp = difficulty.toLowerCase();
        if (temp.equals(getString(R.string.difficulty_1)))
            urlDifficulty = "easy";
        else if (temp.equals(getString(R.string.difficulty_2)))
            urlDifficulty = "medium";
        else if (temp.equals(getString(R.string.difficulty_3)))
            urlDifficulty = "hard";
        String command = "pts_" + urlCategory + "_" + urlDifficulty + "&pts=" + bestScore;

        String url = getString(R.string.server_address) + "/updateScore.php?username=" +
                Utility.readFromPreferencesString(activity, "username")
                + "&category=" + command;
        Log.d("aaa", url);

        sendServerUpdate(url);
    }

    private void sendServerUpdate(final String url){

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("aaa", "Server response : " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String temp = Utility.readFromPreferencesString(activity, "scores_update");
                //temp = temp.equals("nulla") ? "" : temp;
                Utility.writeOnPreferences(activity, "scores_update", temp + url + ",");
                Log.d("aaa", temp + url + ",");
                Log.d("aaa", "Server request : Something went wrong!");
            }

        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    public void onBackPressed() {
        Utility.replaceQuestionFragment((AppCompatActivity)activity, R.id.home_fragment,
                new HomeFragment(), "aaa");
    }
}
