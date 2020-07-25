package com.example.ilriccone;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.example.ilriccone.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class SideDrawer extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_scores, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Log.d("aaa",  String.valueOf(Utility.readFromPreferencesInt(activity, "lang_is_setted")));

        if (Utility.readFromPreferencesInt(activity, "lang_is_setted") != 1) {
            String lang = Utility.readFromPreferencesString(activity, "lang");

            if (!lang.equals("en")) {
                Log.d("aaa", lang);
                Utility.setLocale(activity, lang);
            }
            Utility.writeOnPreferences(activity, "lang_is_setted", 1);
        }

        JSONObject json = null;

        if (getIntent().hasExtra("json")) {
            try {
                json = new JSONObject(getIntent().getStringExtra("json"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (json != null) {
                try {
                    savePersonalInfo(this, json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        String username = Utility.readFromPreferencesString(this, "username");
        Log.d("aaa", "lol " +  "   " + username);

        View header = navigationView.getHeaderView(0);
        TextView in_game_username = header.findViewById(R.id.game_username);
        in_game_username.setText(username);

        Utility.replaceFragment((AppCompatActivity) activity, R.id.home_fragment,
                new HomeFragment(), "aaa");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.writeOnPreferences(activity, "lang_is_setted", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.side_drawer, menu);
        //navigationView.getHeaderView(0).findViewById(R.id.nav_scores);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupButtons();

    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.home_fragment);
        if (f instanceof QuestionFragment) {
            ((QuestionFragment)f).onBackPressed();
        } else if (f instanceof QuizEndFragment) {
            ((QuizEndFragment)f).onBackPressed();
        } else if (f instanceof HomeFragment) {
            Log.d("aaa","1");
        } else if (f instanceof DifficultyFragment) {
            super.onBackPressed();
        } else {
            //super.onBackPressed();
            this.finishAffinity();

        }

    }

    private void setupButtons(){


        Button b1 = findViewById(R.id.buttonCat1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //InternetUtilities.makeSnackbar((AppCompatActivity)activity, R.id.nav_view);
                Utility.replaceFragment((AppCompatActivity) activity, R.id.home_fragment,
                        new DifficultyFragment(getString(R.string.category_1)), "aaa");
            }
        });

        Button b2 = findViewById(R.id.buttonCat2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //InternetUtilities.makeSnackbar((AppCompatActivity)activity, R.id.nav_view);
                Utility.replaceFragment((AppCompatActivity) activity, R.id.home_fragment,
                        new DifficultyFragment(getString(R.string.category_2)), "aaa");
            }
        });

        Button b3 = findViewById(R.id.buttonCat3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //InternetUtilities.makeSnackbar((AppCompatActivity)activity, R.id.nav_view);
                Utility.replaceFragment((AppCompatActivity) activity, R.id.home_fragment,
                        new DifficultyFragment(getString(R.string.category_3)), "aaa");
            }
        });

        Button b4 = findViewById(R.id.buttonCat4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //InternetUtilities.makeSnackbar((AppCompatActivity)activity, R.id.nav_view);
                Utility.replaceFragment((AppCompatActivity) activity, R.id.home_fragment,
                        new DifficultyFragment(getString(R.string.category_3)), "aaa");
            }
        });
    }

    private void savePersonalInfo(Activity activity, JSONObject json) throws JSONException {
        Utility.writeOnPreferences(activity, "username", json.getString("username"));
        Utility.writeOnPreferences(activity, "password", json.getString("password"));

        String temp = Utility.getScoreString(activity, getString(R.string.category_1), getString(R.string.difficulty_1));
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_1), getString(R.string.difficulty_2));
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_1), getString(R.string.difficulty_3));
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_2), getString(R.string.difficulty_1));
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_2), getString(R.string.difficulty_2));
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_2), getString(R.string.difficulty_3));
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_3), getString(R.string.difficulty_1));
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_3), getString(R.string.difficulty_2));
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_3), getString(R.string.difficulty_3));
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_4), getString(R.string.difficulty_1));
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_4), getString(R.string.difficulty_2));
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_4), getString(R.string.difficulty_3));
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

    }
}
