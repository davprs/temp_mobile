package com.example.ilriccone;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;

public class MyCountDownTimer extends CountDownTimer {
    private ProgressBar progressBar;

    public MyCountDownTimer(long millisInFuture, long countDownInterval, ProgressBar progressBar) {
        super(millisInFuture, countDownInterval);
        this.progressBar = progressBar;
    }

    @Override
    public void onTick(long millisUntilFinished) {

        int progress = (int) (millisUntilFinished/1000);

        progressBar.setProgress(progress);
    }

    @Override
    public void onFinish() {
        Log.d("aaa", "Finito");
    }
}
