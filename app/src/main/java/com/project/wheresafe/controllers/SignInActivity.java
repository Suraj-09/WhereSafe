package com.project.wheresafe.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.project.wheresafe.R;

public class SignInActivity extends AppCompatActivity {
    final private String TAG = "SignInActivity";
    EditText email, password;

    Button btnSignIn, btnGoToSignUp;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_sign_in);
        password = findViewById(R.id.password_sign_in);

        btnSignIn = findViewById(R.id.sign_in_button);
        btnGoToSignUp = findViewById(R.id.go_to_sign_up);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

        btnGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });
    }

    private void goToSignUp() {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }

    private void SignIn() {
        String user_email = email.getText().toString();
        String user_password = password.getText().toString();

        if (user_email.isEmpty()) {
            email.setError("Email cannot be empty");
            email.requestFocus();
        } else if (user_password.isEmpty()) {
            password.setError("Password cannot be empty");
            password.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(user_email, user_password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                goToMainActivity();
                            } else {
                                // handle the errors from invalid credentials
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    password.setError("Invalid password");
                                    password.requestFocus();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    email.setError("Invalid email.");
                                    email.requestFocus();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }
                    });
        }

    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}