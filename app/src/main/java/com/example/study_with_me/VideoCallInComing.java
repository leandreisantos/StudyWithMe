package com.example.study_with_me;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URL;

public class VideoCallInComing extends AppCompatActivity {

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    String sender_url,sender_prof,sender_name,sender_uid,receiver_uid;
    VcModel model;
    ImageView dp;
    TextView accept,decline,nameuser,interestuser;
    DatabaseReference referencecaller,referenceVc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_in_coming);


        dp = findViewById(R.id.iv_dp_outgoing);
        accept = findViewById(R.id.accept_og);
        decline = findViewById(R.id.close_og);
        nameuser = findViewById(R.id.tv_name_og);
        interestuser = findViewById(R.id.tv_int_og);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        receiver_uid = user.getUid();

        Bundle bundle = getIntent().getExtras();
        if (bundle!= null){
            sender_uid = bundle.getString("uid");

        }else {
            Toast.makeText(this, "Data missing", Toast.LENGTH_SHORT).show();
        }

        model = new VcModel();
        checkCallstatus();
        referencecaller = database.getReference("All Users").child(sender_uid);

        referencecaller.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    sender_name = snapshot.child("name").getValue().toString();
                    sender_url = snapshot.child("url").getValue().toString();
                    sender_prof = snapshot.child("interest").getValue().toString();

                    nameuser.setText(sender_name);
                    Picasso.get().load(sender_url).into(dp);
                    interestuser.setText(sender_prof);

                }else {
                    Toast.makeText(VideoCallInComing.this, "Cannot make call", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        referenceVc = database.getReference("vcref").child(sender_uid).child(receiver_uid);

        accept.setOnClickListener(view -> {

            String response = "yes";
            sendResponse(response);
        });

        decline.setOnClickListener(view -> {

            String response = "no";
            sendResponse(response);
            onBackPressed();
            finish();
        });

    }

    private void checkCallstatus() {

        DatabaseReference cancelRef;
        cancelRef = database.getInstance().getReference("cancel");


        cancelRef.child(sender_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String response = snapshot.child("response").getValue().toString();

                    if (response.equals("no")){

                        Intent intent = new Intent(VideoCallInComing.this,MainActivity.class);
                        startActivity(intent);
                        finish();


                    }else{

                    }

                }else {

                    // Toast.makeText(VideoCallOutgoing.this, "Not responding", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendResponse(String response) {

        if (response.equals("yes")){

            model.setKey(sender_name+receiver_uid);
            model.setResponse(response);
            referenceVc.child("res").setValue(model);
            joinmeeting();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    referenceVc.removeValue();
                }
            },3000);


        }else if (response.equals("no")){

            model.setKey(sender_name+receiver_uid);
            model.setResponse(response);
            referenceVc.child("res").setValue(model);
            joinmeeting();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    referenceVc.removeValue();
                }
            },3000);

        }


    }

    private void joinmeeting() {


        try {

            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(sender_name+receiver_uid)
                    .setWelcomePageEnabled(false)
                    .build();
            JitsiMeetActivity.launch(VideoCallInComing.this,options);
            finish();

        }catch (Exception e){

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
}