package com.prathamesh.videotoaudio;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class Splash extends AppCompatActivity {

    ImageView splashBack;

    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Objects.requireNonNull(getSupportActionBar()).hide();

        splashBack = findViewById(R.id.IV_Splash);
        Glide.with(this).load(R.drawable.splash_background).into(splashBack);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this,Home.class);
                startActivity(intent);
                finish();
            }
        },Constants.SPLASH);

    }
}