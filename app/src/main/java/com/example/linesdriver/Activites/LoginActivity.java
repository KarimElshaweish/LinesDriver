package com.example.linesdriver.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.linesdriver.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void finish(View view) {
        finish();
    }

    public void submit(View view) {
        startActivity(new Intent(this,VertificationCode.class));
    }
}
