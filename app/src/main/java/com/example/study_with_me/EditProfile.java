package com.example.study_with_me;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

public class EditProfile extends AppCompatActivity {

    EditText name,prof,email,interest,about;
    ImageView dp;
    TextView close;
    ImageButton save;

    //database stuff
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        documentReference=db.collection("user").document(currentuid);

        name = findViewById(R.id.et_username_profile);
        prof = findViewById(R.id.et_prof);
        email = findViewById(R.id.et_email);
        interest = findViewById(R.id.et_interest);
        about = findViewById(R.id.et_about);
        close = findViewById(R.id.tv_close_edit);

        dp = findViewById(R.id.iv_profile);
        save = findViewById(R.id.btn_profile);


        save.setOnClickListener(view -> Dosave());
        close.setOnClickListener(view -> onBackPressed());

    }

    private void Dosave() {
        String name_et = name.getText().toString();
        String prof_et = prof.getText().toString();
        String email_et = email.getText().toString();
        String interest_et = interest.getText().toString();
        String about_et = about.getText().toString();

        final DocumentReference sDoc = db.collection("user").document(currentuid);
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // DocumentSnapshot snapshot = transaction.get(sfDocRef);

            transaction.update(sDoc, "name",name_et);
            transaction.update(sDoc, "prof",prof_et);
            transaction.update(sDoc, "email",email_et);
            transaction.update(sDoc, "interest",interest_et);
            transaction.update(sDoc, "about",about_et);


            // Success
            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(EditProfile.this, "Updated", Toast.LENGTH_SHORT).show();
            onBackPressed();
        })
                .addOnFailureListener(e -> Toast.makeText(EditProfile.this, "failed", Toast.LENGTH_SHORT).show());

    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(task -> {

                    if(task.getResult().exists()){

                        String nameResult = task.getResult().getString("name");
                        String profResult = task.getResult().getString("prof");
                        String emailResult = task.getResult().getString("email");
                        String interestResult = task.getResult().getString("interest");
                        String url = task.getResult().getString("url");
                        String aboutResult = task.getResult().getString("about");

                        name.setText(nameResult);
                        prof.setText(profResult);
                        email.setText(emailResult);
                        interest.setText(interestResult);
                        about.setText(profResult);
                        Picasso.get().load(url).into(dp);

                    }else{
                        Toast.makeText(EditProfile.this, "No Profile Exist", Toast.LENGTH_SHORT).show();
                    }


                });

    }
}