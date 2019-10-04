package com.example.linesdriver.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.linesdriver.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    public void login(View view) {
        startActivity(new Intent(this,LoginActivity.class));
    }

    public void SignUp(View view) {
        startActivity(new Intent(this,SignUpActivity.class));
    }
}
