package com.example.ilriccone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.invoke.ConstantCallSite;

public class StartFragment extends Fragment {

    Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("aaa", "ciaone");
        View view =  inflater.inflate(R.layout.startup_fragment, container, false);

        //if there was a configuration change, i retrieve the image if it was taken
        if (savedInstanceState != null){
            /*ImageView imageView = view.findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);*/
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = getView().findViewById(R.id.button1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "bottone premuto", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(getContext(), SideDrawer.class);
                Log.d("aaa", getContext().toString());
                startActivity(loginIntent);
            }
        });
    }


}
