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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.project.wheresafe.R;
import com.project.wheresafe.models.FirestoreHelper;

public class SignUpActivity extends AppCompatActivity {
    final private String TAG = "SignUpActivity";
    EditText email, password, name;
    Button btnSignUp, btnGoToSignIn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_sign_up);
        password = findViewById(R.id.password_sign_up);
        name = findViewById(R.id.name_sign_up);

        btnSignUp = findViewById(R.id.sign_up_button);
        btnGoToSignIn = findViewById(R.id.go_to_sign_in);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        btnGoToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotToSignIn();
            }
        });

    }

    private void gotToSignIn() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void signUp() {
        String user_name = name.getText().toString();
        String user_email = email.getText().toString();
        String user_password = password.getText().toString();

        if (user_name.isEmpty()) {
            name.setError("Name cannot be empty");
            name.requestFocus();
        } else if (user_email.isEmpty()) {
            email.setError("Email cannot be empty");
            email.requestFocus();
        } else if (user_password.isEmpty()) {
            password.setError("Password cannot be empty");
            password.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(user_email, user_password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    FirestoreHelper firestoreHelper = new FirestoreHelper();
                                    firestoreHelper.addUser(user, user_name);
                                    goToMainActivity();
                                }
                            } else {
                                // handle the errors from invalid credentials
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    password.setError(task.getException().getMessage());
                                    password.requestFocus();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    email.setError(task.getException().getMessage());
                                    email.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    email.setError(task.getException().getMessage());
                                    email.requestFocus();
                                } catch (Exception e) {
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
        finish();
    }
}