package com.project.wheresafe.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.project.wheresafe.R;

public class SignUpActivity extends AppCompatActivity {
    EditText email, password;
    Button sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.email_sign_up);
        password = findViewById(R.id.password_sign_up);


    }
}