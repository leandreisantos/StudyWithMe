package com.example.study_with_me;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextView register;
    EditText username,pass;
    ImageButton signIn;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register = findViewById(R.id.tv_register_la);
        username = findViewById(R.id.inputEmail);
        pass = findViewById(R.id.inputPassword);
        signIn = findViewById(R.id.buttonSignIn);
        pb = findViewById(R.id.pv_login);

        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        });
        signIn.setOnClickListener(v -> login());

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showlogout();
    }

    private void login() {
        String email = username.getText().toString();
        String password = pass.getText().toString();

        if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){
            pb.setVisibility(View.VISIBLE);
            enabledElement(false);

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    pb.setVisibility(View.GONE);
                    String error = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(LoginActivity.this, "Error:"+error, Toast.LENGTH_SHORT).show();
                    enabledElement(true);
                }
            });
        }else Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

    }

    private void showlogout() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.exit_item_layout,null);
        TextView logout_tv = view.findViewById(R.id.logout_tv_ll);
        TextView cancel_tv = view.findViewById(R.id.cancel_tv_ll);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        alertDialog.show();
        logout_tv.setOnClickListener(v -> {
            finish();
        });
        cancel_tv.setOnClickListener((View v) -> {
            alertDialog.dismiss();
        });
    }

    private void enabledElement(Boolean x){
        username.setEnabled(x);
        pass.setEnabled(x);
        register.setEnabled(x);
        signIn.setEnabled(x);

    }
}