package com.example.ilriccone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Utility {

    public static final int MAIN_APPBAR_COLOR = 0;
    public static final int DIF_1_APPBAR_COLOR = 1;
    public static final int DIF_2_APPBAR_COLOR = 2;
    public static final int DIF_3_APPBAR_COLOR = 3;
    static final int ACTIVITY_ADD_TRIP = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    /*static void setUpToolbar(AppCompatActivity activity, String title) {
        Toolbar toolbar = activity.findViewById(R.id.app_bar);
        toolbar.setTitle(title);
        //Set a Toolbar to act as the ActionBar for the Activity
        activity.setSupportActionBar(toolbar);
    }*/

    static public void changeAppBarColor(AppCompatActivity activity, String difficulty){
        if(difficulty.equals(activity.getString(R.string.difficulty_1))){
            changeAppBarColor(activity, DIF_1_APPBAR_COLOR);
        } else if(difficulty.equals(activity.getString(R.string.difficulty_2))){
            changeAppBarColor(activity, DIF_2_APPBAR_COLOR);
        } else if(difficulty.equals(activity.getString(R.string.difficulty_3))){
            changeAppBarColor(activity, DIF_3_APPBAR_COLOR);
        }
    }

    static public void changeAppBarColor(AppCompatActivity activity, int color){
        ActionBar actionBar;
        actionBar = (activity).getSupportActionBar();
        ColorDrawable colorDrawable;

        switch (color){
            case MAIN_APPBAR_COLOR:
                colorDrawable
                        = new ColorDrawable(activity.getResources().getColor(R.color.colorDefaultAppBar));
                actionBar.setBackgroundDrawable(colorDrawable);
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                break;
            case DIF_1_APPBAR_COLOR:
                colorDrawable
                        = new ColorDrawable(activity.getResources().getColor(R.color.dif1));
                actionBar.setBackgroundDrawable(colorDrawable);
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.dif1_secondary));
                break;
            case DIF_2_APPBAR_COLOR:
                colorDrawable
                        = new ColorDrawable(activity.getResources().getColor(R.color.dif2));
                actionBar.setBackgroundDrawable(colorDrawable);
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.dif2_secondary));
                break;
            case DIF_3_APPBAR_COLOR:
                colorDrawable
                        = new ColorDrawable(activity.getResources().getColor(R.color.dif3));
                actionBar.setBackgroundDrawable(colorDrawable);
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.dif3_secondary));
                break;
        }
    }

    static public String getScoreString(Activity activity, String category, String difficulty){
        String cat_code = null;
        String dif_code = null;

        if (category.equals(activity.getString(R.string.category_1))){
            cat_code = "1";
        } else if (category.equals(activity.getString(R.string.category_2))){
            cat_code = "2";
        } else if (category.equals(activity.getString(R.string.category_3))){
            cat_code = "3";
        } else if (category.equals(activity.getString(R.string.category_4))){
            cat_code = "4";
        }

        if (difficulty.equals(activity.getString(R.string.difficulty_1))){
            dif_code = "1";
        } else if (difficulty.equals(activity.getString(R.string.difficulty_2))){
            dif_code = "2";
        } else if (difficulty.equals(activity.getString(R.string.difficulty_3))){
            dif_code = "3";
        }


        
        return cat_code + "_" + dif_code;
    }

    static public void setLocale(Activity activity, String lang) {
        if(lang != null) {
            Locale myLocale = new Locale(lang);
            Resources res = activity.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(activity, MainActivity.class);
            activity.finish();
            activity.startActivity(refresh);
        }
    }

    static public Boolean readFromPreferences(Activity activity, String name){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean(name, false);
    }

    static public String readFromPreferencesString(Activity activity, String name){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(name, "nulla");
    }

    static public Integer readFromPreferencesInt(Activity activity, String name){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(name, 0);
    }

    static public void writeOnPreferences(Activity activity, String name, Boolean status){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(name, status);
        editor.commit();
    }

    static public void writeOnPreferences(Activity activity, String name, int value){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    static public void writeOnPreferences(Activity activity, String name, String value){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(name, value);
        editor.commit();
    }

    static void insertFragment(AppCompatActivity activity, int container_id, Fragment fragment, String tag) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(container_id, fragment, tag)
                .commit();
    }

    public static void replaceFragment(AppCompatActivity activity, int container_id, Fragment fragment, String tag){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(container_id, fragment)
                .addToBackStack(null)
                .commit();
    }

    public static void replaceQuestionFragment(AppCompatActivity activity, int container_id, Fragment fragment, String tag){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(container_id, fragment)
                .commit();
    }



    public static Bitmap getImageBitmap(Activity activity, Uri currentPhotoUri){
        ContentResolver resolver = activity.getApplicationContext()
                .getContentResolver();
        try {
            InputStream stream = resolver.openInputStream(currentPhotoUri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            Objects.requireNonNull(stream).close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
