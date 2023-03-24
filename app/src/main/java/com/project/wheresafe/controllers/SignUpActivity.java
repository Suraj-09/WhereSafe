package com.project.wheresafe.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.project.wheresafe.R;
import com.project.wheresafe.models.FirestoreHelper;
import com.project.wheresafe.models.SharedPreferenceHelper;

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

        email = findViewById(R.id.edtEmailSignUp);
        password = findViewById(R.id.edtPasswordSignUp);
        name = findViewById(R.id.edtNameSignUp);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnGoToSignIn = findViewById(R.id.btnGoSignIn);
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
                                    setUserProfile(user, user_name);

                                    SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(getApplicationContext());
                                    sharedPreferenceHelper.saveUser(user);

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

    private void setUserProfile(FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }
}