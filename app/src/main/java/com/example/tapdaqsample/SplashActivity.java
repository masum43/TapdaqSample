package com.example.tapdaqsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        AdsConfig adsConfig = new AdsConfig(this);
        adsConfig.init("com.example.tapdaqsample.MainActivity");
    }
}