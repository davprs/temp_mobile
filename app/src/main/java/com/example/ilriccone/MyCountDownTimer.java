package com.example.ilriccone;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;

public class MyCountDownTimer extends CountDownTimer {
    private QuestionFragment questionFragment;
    private int i;
    private ProgressBar progressBar;

    public MyCountDownTimer(long millisInFuture, long countDownInterval, ProgressBar progressBar, QuestionFragment questionFragment, int i) {
        super(millisInFuture, countDownInterval);
        this.questionFragment = questionFragment;
        this.i = i;
        this.progressBar = progressBar;
    }

    @Override
    public void onTick(long millisUntilFinished) {

        int progress = (int) (millisUntilFinished/1000);

        progressBar.setProgress(progress);
    }

    @Override
    public void onFinish() {
        if(questionFragment.visible)
            questionFragment.wrongAnswer(i);
    }


}
