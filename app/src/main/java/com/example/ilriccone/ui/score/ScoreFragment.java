package com.example.ilriccone.ui.score;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.ilriccone.R;
import com.example.ilriccone.Utility;

import java.util.Arrays;
import java.util.List;

public class ScoreFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private TextView label1, label2, label3, label4;
    private TextView value1, value2,value3, value4;
    private Button diff1, diff2, diff3;

    private List<Button> difficulties;
    private List<TextView> labels;
    private List<TextView> vals;
    private List<String> categories;

    private Activity activity;
    private String visualizedScore;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_scores, container, false);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        visualizedScore = getString(R.string.difficulty_1);
        setupButtonsAndLabels();
        changeDifficulty(visualizedScore);
    }

    private void setupButtonsAndLabels(){
        label1 = getView().findViewById(R.id.score_category_label_1);
        label2 = getView().findViewById(R.id.score_category_label_2);
        label3 = getView().findViewById(R.id.score_category_label_3);
        label4 = getView().findViewById(R.id.score_category_label_4);

        value1 = getView().findViewById(R.id.score_category_1);
        value2 = getView().findViewById(R.id.score_category_2);
        value3 = getView().findViewById(R.id.score_category_3);
        value4 = getView().findViewById(R.id.score_category_4);

        diff1 = getView().findViewById(R.id.score_btn_1);
        diff2 = getView().findViewById(R.id.score_btn_2);
        diff3 = getView().findViewById(R.id.score_btn_3);

        difficulties = Arrays.asList(diff1, diff2, diff3);
        labels = Arrays.asList(label1, label2, label3, label4);
        vals = Arrays.asList(value1, value2, value3, value4);
        categories = Arrays.asList(getString(R.string.category_1), getString(R.string.category_2),
                getString(R.string.category_3), getString(R.string.category_4));

        for (final TextView label : labels){
            label.setText(categories.get(labels.indexOf(label)));
        }

        for (final Button diff : difficulties){
            diff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeDifficulty(String.valueOf(diff.getText()));
                }
            });
        }
    }

    private void changeDifficulty(String newDiff){
        int style = 0;
        Utility.changeAppBarColor((AppCompatActivity)activity, newDiff);

        if(newDiff.equals(getString(R.string.difficulty_1))){
            style = R.drawable.rounded_corners_color_1;
        } else if(newDiff.equals(getString(R.string.difficulty_2))){
            style = R.drawable.rounded_corners_color_2;
        } else if(newDiff.equals(getString(R.string.difficulty_3))){
            style = R.drawable.rounded_corners_color_3;
        }

        for(TextView value : vals){
            value.setBackground(getResources().getDrawable(style));
            value.setPadding(0, 20, 0, 20);
        }

        for(TextView value : vals){
            Log.d("aaa", Utility.getScoreString(activity, categories.get(vals.indexOf(value)), newDiff));
            value.setText(String.valueOf(
                    Utility.readFromPreferencesInt( activity, Utility.getScoreString(
                                                                activity,
                                                                categories.get( vals.indexOf(value)), newDiff))) + getString(R.string.pts));
        }

    }

}
