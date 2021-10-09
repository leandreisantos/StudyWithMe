package com.example.study_with_me;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextView register;
    EditText username,pass;
    ImageButton signIn;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register = findViewById(R.id.tv_register_la);
        username = findViewById(R.id.et_username_la);
        pass = findViewById(R.id.et_password_la);
        signIn = findViewById(R.id.sign_in_la);

        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        });
        signIn.setOnClickListener(v -> login());

    }

    private void login() {
        String email = username.getText().toString();
        String password = pass.getText().toString();

        if(!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password)){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    String error = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(LoginActivity.this, "Error:"+error, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}