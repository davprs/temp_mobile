package com.example.ilriccone.ui.slideshow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.ilriccone.MainActivity;
import com.example.ilriccone.R;
import com.example.ilriccone.Utility;
import com.example.ilriccone.ui.home.HomeFragment;

import java.util.Locale;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private Activity activity;
    private Button language, theme;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        setupButtons();
    }

    private void setupButtons(){
        language = getView().findViewById(R.id.language_button_settings);
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseLenguage();
            }
        });
    }

    private void chooseLenguage(){
        final String[] Options = {"Italiano", "English"};
        AlertDialog.Builder window = new AlertDialog.Builder(activity);
        window.setTitle(getString(R.string.change_language));
        window.setItems(Options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    //first option clicked, do this...
                    Utility.writeOnPreferences(activity, "lang", "it");
                    Utility.setLocale(activity, "it");

                }else if(which == 1){
                    //second option clicked, do this...
                    Utility.writeOnPreferences(activity, "lang", "en");
                    Utility.setLocale(activity, "en");


                }else{
                    //theres an error in what was selected
                    Toast.makeText(getContext(), "Hmmm I messed up. I detected that you clicked on : " + which + "?", Toast.LENGTH_LONG).show();
                }
            }
        });

        window.show();
    }
}
