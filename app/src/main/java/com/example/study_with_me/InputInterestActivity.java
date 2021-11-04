package com.example.study_with_me;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

public class InputInterestActivity extends AppCompatActivity {

    ImageButton btn;
    EditText et;
    TextView back;
    String bundle_int;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    DocumentReference referenceuser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_interest);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            bundle_int = extras.getString("i");
        }

        reference = database.getReference("user interest").child(currentuid);

        btn = findViewById(R.id.ib_submit_ii);
        et = findViewById(R.id.et_interest_ii);
        back = findViewById(R.id.tv_back_interest);

        btn.setOnClickListener(v -> goToMain());

        back.setOnClickListener(v -> onBackPressed());

    }

    private void goToMain() {

        String interest = et.getText().toString();

        final DocumentReference sDoc = db.collection("user").document(currentuid);
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // DocumentSnapshot snapshot = transaction.get(sfDocRef);

            transaction.update(sDoc, "interest",interest);

            // Success
            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(InputInterestActivity.this, "Updated", Toast.LENGTH_SHORT).show();
            onBackPressed();
        })
                .addOnFailureListener(e -> Toast.makeText(InputInterestActivity.this, "failed", Toast.LENGTH_SHORT).show());


    }

    @Override
    protected void onStart() {
        super.onStart();


        referenceuser = db.collection("user").document(currentuid);

        referenceuser.get()
                .addOnCompleteListener(task -> {

                        String interestResult = task.getResult().getString("interest");
                        et.setText(interestResult);

                });

    }
}