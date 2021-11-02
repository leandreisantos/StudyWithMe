package com.example.study_with_me;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    ImageView back;
    EditText username,pass,conf_pass;
    ImageButton signup;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        back = findViewById(R.id.btn_back_rg);
        username = findViewById(R.id.et_username_rg);
        pass = findViewById(R.id.et_password_rg);
        conf_pass = findViewById(R.id.et_password2_rg);
        signup = findViewById(R.id.sign_up_rg);
        pb = findViewById(R.id.pv_login);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
        });
        signup.setOnClickListener(v -> register());
    }

    private void register() {
        String email = username.getText().toString();
        String password = pass.getText().toString();
        String confirm_password = conf_pass.getText().toString();


        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)&& !TextUtils.isEmpty(confirm_password)){
            if(password.equals(confirm_password)){
                pb.setVisibility(View.VISIBLE);
                enabledElement(false);
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Successfully account created", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        pb.setVisibility(View.GONE);
                        enabledElement(true);
                        String error = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(RegisterActivity.this, "Error:"+error, Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                pb.setVisibility(View.GONE);
                enabledElement(true);
                Toast.makeText(RegisterActivity.this, "password and confirm password is not matching", Toast.LENGTH_SHORT).show();
            }
        }else{
            pb.setVisibility(View.GONE);
            enabledElement(true);
            Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

        }
    }

    private void enabledElement(Boolean x){
        username.setEnabled(x);
        pass.setEnabled(x);
        back.setEnabled(x);
        conf_pass.setEnabled(x);
        signup.setEnabled(x);

    }


}