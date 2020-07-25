package com.example.ilriccone;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class QuestionFragment extends Fragment {

    private int numberOfQuestions = 10;
    private Activity activity;
    private String category;
    private JSONObject json = null;
    private Button b1, b2, b3, b4;
    private Integer questionNumber;
    private String difficulty;
    private ProgressBar progressBar;

    public QuestionFragment(int questionNumber, String category, String difficulty) {
        this.questionNumber = questionNumber;
        this.difficulty = difficulty;
        this.category = category;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        return inflater.inflate(R.layout.question_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
    }

    public void onBackPressed() {
        FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
        Log.d("aaa", String.valueOf(fragmentManager.getBackStackEntryCount()));
        Utility.changeAppBarColor((AppCompatActivity)activity, Utility.MAIN_APPBAR_COLOR);
        fragmentManager.popBackStack(fragmentManager.
                        getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - questionNumber - 1).
                        getId()
                , FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onStart() {
        super.onStart();
        InternetUtilities.makeSnackbar(getActivity(), R.id.question_fragment);
        InternetUtilities.registerNetworkCallback(getActivity());
        try {
            setupQuestionFragment(questionNumber);
        } catch (JSONException e) {
            Log.d("aaa", "ERRORE JSON!!");
        }
    }

    private void setupQuestionFragment(final int i) throws JSONException {
        String res = Utility.readFromPreferencesString(activity, "question_" + i);
        json = new JSONObject(res);
        TextView q_counter = getView().findViewById(R.id.text_view_question_count);
        q_counter.setText(getString(R.string.question_counter_pre, i));
        TextView tw = getView().findViewById(R.id.text_view_question);
        String temp = json.getString("question");
        temp = temp.replace("&#039;", "'")
                .replace("&amp;", "&");
        tw.setText(temp);
        progressBar=(ProgressBar)getView().findViewById(R.id.progressBar);
        MyCountDownTimer countDownTimer = new MyCountDownTimer(10000,
                1000, progressBar);
        countDownTimer.start();


        setupButtons(i);
    }

    private void setupButtons(final int i) throws JSONException {
        b1 = getView().findViewById(R.id.question_choise_1);
        b2 = getView().findViewById(R.id.question_choise_2);
        b3 = getView().findViewById(R.id.question_choise_3);
        b4 = getView().findViewById(R.id.question_choise_4);

        setupButtonsBackground();

        Stack<Button> buttons = new Stack<>();
        buttons.add(b1);
        buttons.add(b2);
        buttons.add(b3);
        buttons.add(b4);

        Stack<String> incorrectAnswers = new Stack<>();
        incorrectAnswers.addAll(Arrays.asList(json.getString("incorrect_answers")
                .replace("[\"", "")
                .replace("\"]", "")
                .replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .split("\",\""))
        );

        Collections.shuffle(buttons);
        Button correct = buttons.pop();
        correct.setText(json.getString("correct_answer")
                .replace("&#039;", "'")
                .replace("&quot;", "\"")
                .replace("&amp;", "&"));
        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.writeOnPreferences(activity, "question_" + i + "_answer_status", true);
                jumpToNextQuestion(i);
            }
        });

        for (Button button : buttons) {
            button.setText(incorrectAnswers.pop());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.writeOnPreferences(activity, "question_" + i + "_answer_status", false);
                    jumpToNextQuestion(i);
                }
            });
        }
    }

    private void setupButtonsBackground(){
        List<Button> buttonList = new LinkedList(Arrays.asList(b1, b2, b3, b4));
        Integer style = 0;

        Log.d("aaa", difficulty + " " + getString(R.string.difficulty_1));

        if(difficulty.equals(getString(R.string.difficulty_1))){
            style = R.drawable.refined_answer_diff_1_button;
        } else if(difficulty.equals(getString(R.string.difficulty_2))){
            style = R.drawable.refined_answer_diff_2_button;
        } else if(difficulty.equals(getString(R.string.difficulty_3))){
            style = R.drawable.refined_answer_diff_3_button;
        }


            for(Button button : buttonList){
                button.setBackground(getResources().getDrawable(style));
        }
    }

    private void jumpToNextQuestion(int i) {
        if (i < numberOfQuestions) {
            Utility.replaceFragment((AppCompatActivity) activity, R.id.home_fragment, new QuestionFragment(i + 1, category, difficulty), "aaa");
        } else {
            Utility.replaceFragment((AppCompatActivity) activity, R.id.home_fragment, new QuizEndFragment(category, difficulty), "aaa");
        }
    }
}
