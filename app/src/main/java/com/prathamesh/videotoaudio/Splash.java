package com.prathamesh.videotoaudio;

import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class Splash extends AppCompatActivity {

    ImageView splashBack;

    @SuppressLint("MissingInflatedId")
=======
import android.os.Bundle;

public class Splash extends AppCompatActivity {

>>>>>>> 1f02d215357fbecd577598349e1f92c072207ea8
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
<<<<<<< HEAD

        Objects.requireNonNull(getSupportActionBar()).hide();

        splashBack = findViewById(R.id.IV_Splash);
        Glide.with(this).load(R.drawable.splash_background).into(splashBack);

=======
>>>>>>> 1f02d215357fbecd577598349e1f92c072207ea8
    }
}