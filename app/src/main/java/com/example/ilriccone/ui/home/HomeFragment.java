package com.example.ilriccone.ui.home;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.ilriccone.DifficultyFragment;
import com.example.ilriccone.R;
import com.example.ilriccone.Utility;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Activity activity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        Utility.changeAppBarColor((AppCompatActivity)activity, Utility.MAIN_APPBAR_COLOR);

        setupButtons();
    }

    private void setupButtons(){
        Button b1 = getView().findViewById(R.id.buttonCat1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //InternetUtilities.makeSnackbar((AppCompatActivity)activity, R.id.nav_view);
                Utility.writeOnPreferences((AppCompatActivity)activity, "category", getString(R.string.category_1));
                Utility.replaceFragment((AppCompatActivity) activity, R.id.home_fragment,
                        new DifficultyFragment(getString(R.string.category_1)), "aaa");
            }
        });

        Button b2 = getView().findViewById(R.id.buttonCat2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //InternetUtilities.makeSnackbar((AppCompatActivity)activity, R.id.nav_view);
                Utility.writeOnPreferences((AppCompatActivity)activity, "category", getString(R.string.category_2));
                Utility.replaceFragment((AppCompatActivity) activity, R.id.home_fragment,
                        new DifficultyFragment(getString(R.string.category_2)), "aaa");
            }
        });

        Button b3 = getView().findViewById(R.id.buttonCat3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //InternetUtilities.makeSnackbar((AppCompatActivity)activity, R.id.nav_view);
                Utility.writeOnPreferences((AppCompatActivity)activity, "category", getString(R.string.category_3));
                Utility.replaceFragment((AppCompatActivity) activity, R.id.home_fragment,
                        new DifficultyFragment(getString(R.string.category_3)), "aaa");
            }
        });

        Button b4 = getView().findViewById(R.id.buttonCat4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //InternetUtilities.makeSnackbar((AppCompatActivity)activity, R.id.nav_view);
                Utility.writeOnPreferences((AppCompatActivity)activity, "category", getString(R.string.category_4));
                Utility.replaceQuestionFragment((AppCompatActivity) activity, R.id.home_fragment,
                        new DifficultyFragment(getString(R.string.category_4)), "aaa");
            }
        });

    }
}
