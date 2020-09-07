package com.example.ilriccone.ui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.ilriccone.R;
import com.example.ilriccone.Utility;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private Activity activity;
    private Button language, theme;
    private final String[] OPTIONS = {"Italiano", "English"};
    private String light = "Light", dark = "Dark";
    private String[] THEMES = {light, dark};


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
        Utility.changeAppBarColor((AppCompatActivity)activity, Utility.MAIN_APPBAR_COLOR);
        setupButtons();
    }

    private void setupButtons(){
        language = getView().findViewById(R.id.language_button_settings);
        theme = getView().findViewById(R.id.theme_button_settings);
        String lang = "";
        String cur_theme = "";

        if (Utility.readFromPreferencesString(activity, "lang").equals("it")){
            lang = OPTIONS[0];
        } else if (Utility.readFromPreferencesString(activity, "lang").equals("en")) {
            lang = OPTIONS[1];
        } else {
            lang = "DEFAULT";
        }

        if (Utility.readFromPreferencesString(activity, "theme").equals("light")){
            cur_theme = THEMES[0];
        } else if (Utility.readFromPreferencesString(activity, "theme").equals("dark")) {
            cur_theme = THEMES[1];
        } else {
            cur_theme = "DEFAULT";
        }
        //Log.d("theme", Utility.readFromPreferencesString(activity, "theme"));

        language.setText(lang);
        theme.setText(cur_theme);

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseLenguage();
            }
        });
        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTheme();
            }
        });

    }

    private void chooseLenguage(){
        AlertDialog.Builder window = new AlertDialog.Builder(activity);
        window.setTitle(getString(R.string.change_language));
        window.setItems(OPTIONS, new DialogInterface.OnClickListener() {
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

    private void chooseTheme(){
        AlertDialog.Builder window = new AlertDialog.Builder(activity);
        window.setTitle(getString(R.string.change_theme));
        window.setItems(THEMES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    //first option clicked, do this...
                    Utility.writeOnPreferences(activity, "theme", "light");
                    activity.setTheme(R.style.AppTheme);
                    Utility.reloadActivity(activity);



                }else if(which == 1){
                    //second option clicked, do this...
                    Utility.writeOnPreferences(activity, "theme", "dark");
                    activity.setTheme(R.style.NightAppTheme);
                    Utility.reloadActivity(activity);


                }else{
                    //theres an error in what was selected
                    Toast.makeText(getContext(), "Hmmm I messed up. I detected that you clicked on : " + which + "?", Toast.LENGTH_LONG).show();
                }
                //activity.recreate();

            }
        });

        window.show();
    }
}
