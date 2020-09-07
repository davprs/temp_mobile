package com.example.ilriccone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ilriccone.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SideDrawer extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private View header;
    private Activity activity = this;
    private String username;
    private Boolean hasImage = false;
    private Boolean imageLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setTheme(R.style.NightAppTheme);
        if(Utility.readFromPreferencesString(this, "theme").equals("light")){
            setTheme(R.style.AppTheme);
        } else if(Utility.readFromPreferencesString(this, "theme").equals("dark")) {
            setTheme(R.style.NightAppTheme);
        }

        setContentView(R.layout.activity_side_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_scores, R.id.nav_settings, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Log.d("aaa",  String.valueOf(Utility.readFromPreferencesInt(activity, "lang_is_setted")));

        header = navigationView.getHeaderView(0);

        if (Utility.readFromPreferencesInt(activity, "lang_is_setted") != 1) {
            String lang = Utility.readFromPreferencesString(activity, "lang");

            if (!lang.equals("en")) {
                //Log.d("aaa", lang);
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

        if(getIntent().hasExtra("img")) {
            hasImage = getIntent().getStringExtra("img").equals("1");
            storeImage(hasImage);
            //Log.d("eee", "qui");
            Intent intent = getIntent();
            intent.removeExtra("img");
            finish();
            startActivity(intent);
        }

        //Log.d("aaa", "lol " +  "   " + username);

        TextView in_game_username = header.findViewById(R.id.game_username);
        in_game_username.setText(Utility.readFromPreferencesString(activity, "username"));


        loadImageInGUI(Utility.readFromPreferencesString(activity, "image"));

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
        //setupButtons();
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.home_fragment);
        if (f instanceof QuestionFragment) {
            ((QuestionFragment)f).onBackPressed();
        } else if (f instanceof QuizEndFragment) {
            ((QuizEndFragment)f).onBackPressed();
        } else if (f instanceof HomeFragment) {
            //Log.d("aaa","1");
        } else if (f instanceof DifficultyFragment) {
            super.onBackPressed();
        } else {
            //super.onBackPressed();
            this.finishAffinity();

        }

    }

    private void storeImage(Boolean hasImage){
        //Log.d("ddd", "has image : " + hasImage.toString());
        if (hasImage) {
            RequestQueue queue = Volley.newRequestQueue(getBaseContext());
            final String command = getString(R.string.server_address) + "/" + getString(R.string.get_image_server)
                    + "username=" + username;
// Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, command,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            //Log.d("ddd", "command :" + command);
                            //Log.d("ddd", "writing :" + response);
                            loadImageInGUI(response);
                            Utility.writeOnPreferences(activity, "image", response);
                            Utility.reloadActivity(activity);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.d("aaa", error.toString());
                }

            });

// Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }

    private void loadImageInGUI(String image_64){
        //Log.d("ddd", "temp lenght>10 : " + (image_64.length() > 10) + "\n" + image_64);
        if (image_64.length() > 10) {
            //Log.d("ddd", "writooo");
            ImageView userImage = header.findViewById(R.id.profile_img);
            byte[] decodedString = Base64.decode(image_64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            userImage.setImageBitmap(decodedByte);
            imageLoaded = true;
        }
    }

    private void setupButtons(){

        if (! imageLoaded){
            //Log.d("ddd", "from here :)");
            loadImageInGUI(Utility.readFromPreferencesString(activity, "image"));
       }

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
        username = json.getString("username");
        Utility.writeOnPreferences(activity, "username", username);
        Utility.writeOnPreferences(activity, "password", json.getString("password"));

        String temp = Utility.getScoreString(activity, getString(R.string.category_1), getString(R.string.difficulty_1));
        //Log.d("aab", " ** :" + temp);
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_1), getString(R.string.difficulty_2));
        //Log.d("aab", temp);
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_1), getString(R.string.difficulty_3));
        //Log.d("aab", temp);
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_2), getString(R.string.difficulty_1));
        //Log.d("aab", temp);
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_2), getString(R.string.difficulty_2));
        //Log.d("aab", temp);
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_2), getString(R.string.difficulty_3));
        //Log.d("aab", temp);
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_3), getString(R.string.difficulty_1));
        //Log.d("aab", temp);
        Utility.writeOnPreferences(activity, temp, json.getInt("pts_" + temp));

        temp = Utility.getScoreString(activity, getString(R.string.category_3), getString(R.string.difficulty_2));
        //Log.d("aab", temp);
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
