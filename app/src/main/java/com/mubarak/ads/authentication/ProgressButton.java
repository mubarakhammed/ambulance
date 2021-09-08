package com.mubarak.ads.authentication;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mubarak.ads.R;


public class ProgressButton {
    private CardView cardView;
    private ConstraintLayout layout;
    private ProgressBar progressBar;
    private TextView textView;


    Animation fade_in;

    public ProgressButton(Context ct, View view){


        fade_in = AnimationUtils.loadAnimation(ct, R.anim.fade_in);
        cardView = view.findViewById(R.id.cardview);
        layout = view.findViewById(R.id.constraint_layout);
        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.logintext);


    }

    public void buttonActivated(){
        progressBar.setAnimation(fade_in);
        progressBar.setVisibility(View.VISIBLE);
        textView.setAnimation(fade_in);
        textView.setText("LOGGING IN... ");
    }

    public void buttonFinished(){
        // layout.setBackgroundColor(cardView.getResources().getColor(R.color.green));
        progressBar.setVisibility(View.GONE);
        textView.setText("LOGIN");
    }

    public void  buttonError(){
        // layout.setBackgroundColor(cardView.getResources().getColor(R.color.red));
        progressBar.setVisibility(View.GONE);
        textView.setText("TAP TO RETRY!");
    }


}

