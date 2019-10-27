package com.example.linesdriver.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.linesdriver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(this,MainActivity.class));
        }
    }

    public void login(View view) {
        startActivity(new Intent(this,LoginActivity.class));
    }

    public void SignUp(View view) {
        startActivity(new Intent(this,SignUpActivity.class));
    }
}
