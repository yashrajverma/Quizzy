package com.yashraj.quizzy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private EditText email, pass;
    private String email_str = "", pass_str = "";
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email_editText);
        pass = findViewById(R.id.password_editText);

        loginButton = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    Log.d("User loggin in status..............", "User logged in!!");
                    Toast.makeText(MainActivity.this, "Verified User", Toast.LENGTH_SHORT).show();
                    sendUserToMainActivity();
                }
            }
        };
        mUser = mAuth.getCurrentUser();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString() == null && pass.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "Enter all Credentials", Toast.LENGTH_LONG).show();
                } else {
                    email_str = email.getText().toString();
                    pass_str = pass.getText().toString();
                    loginUser(email_str, pass_str);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Verified User", Toast.LENGTH_SHORT).show();
                    sendUserToMainActivity();

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(MainActivity.this, "Error:" + error, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth != null) {
            mAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}