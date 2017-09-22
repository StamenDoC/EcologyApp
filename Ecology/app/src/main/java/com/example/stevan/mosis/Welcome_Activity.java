package com.example.stevan.mosis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class Welcome_Activity extends AppCompatActivity {

    ImageView welcomeLogo, textLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();



        welcomeLogo = (ImageView) findViewById(R.id.welcomeLogo);
        textLogo = (ImageView) findViewById(R.id.welcomeText);

        final Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(3000);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                welcomeLogo.setVisibility(View.VISIBLE);
                welcomeLogo.startAnimation(fadeIn);
                textLogo.setVisibility(View.VISIBLE);
                textLogo.startAnimation(fadeIn);
            }
        }, 500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                Intent loginIntent = new Intent(Welcome_Activity.this, LoginActivity.class);
                startActivity(loginIntent);
                Welcome_Activity.this.finish();
            }
        }, 3000);
    }

}
