package com.example.study_with_me;


import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ShowUser extends AppCompatActivity {


    ImageView iv;
    TextView name,prof,email,interest,about;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        Bundle extras = getIntent().getExtras();

        if(extras != null) id = extras.getString("uid");
        else Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();

        name = findViewById(R.id.tv_username_profile);
        prof = findViewById(R.id.tv_prof);
        email = findViewById(R.id.tv_email);
        interest = findViewById(R.id.tv_interest);
        about = findViewById(R.id.tv_about);
        iv = findViewById(R.id.iv_profile);


    }

    @Override
    protected void onStart() {
        super.onStart();


        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("user").document(id);

        reference.get()
                .addOnCompleteListener(task -> {
                    if(task.getResult().exists()){

                        String nameResult = task.getResult().getString("name");
                        String bioResult = task.getResult().getString("about");
                        String emailResult = task.getResult().getString("email");
                        String interestResult = task.getResult().getString("interest");
                        String url = task.getResult().getString("url");
                        String profResult = task.getResult().getString("prof");

                        Picasso.get().load(url).into(iv);
                        name.setText(nameResult);
                        prof.setText(profResult);
                        email.setText(emailResult);
                        interest.setText(interestResult);
                        about.setText(bioResult);

                    }else{
                        Toast.makeText(ShowUser.this, "No User", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}