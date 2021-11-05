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

public class VideoCallOutgoing extends AppCompatActivity {

    ImageView iv;
    TextView tvname,tvint;
    TextView close;
    String receiver_url,receiver_int,receiver_name,receiver_token,receiver_uid;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,reference_response;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String sender_uid = user.getUid();

    VcModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_outgoing);
        model = new VcModel();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            receiver_uid = bundle.getString("uid");
        }else Toast.makeText(this, "data missing", Toast.LENGTH_SHORT).show();

        iv = findViewById(R.id.iv_dp_outgoing);
        tvname = findViewById(R.id.tv_name_og);
        tvint = findViewById(R.id.tv_int_og);
        close = findViewById(R.id.close_og);

        reference = database.getReference("All users").child(receiver_uid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    receiver_name = snapshot.child("name").getValue().toString();
                    receiver_url = snapshot.child("url").getValue().toString();
                    receiver_int = snapshot.child("interest").getValue().toString();

                    tvname.setText(receiver_name);
                    Picasso.get().load(receiver_url).into(iv);
                    tvint.setText(receiver_int);
                }else{
                    Toast.makeText(VideoCallOutgoing.this, "Cannot make a call", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendCallInvitation();

        checkResponse();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelVC();
            }
        });
    }

    private void cancelVC() {

        DatabaseReference cancelRef;
        cancelRef = database.getInstance().getReference("cancel");

        model.setResponse("no");
        cancelRef.child(sender_uid).setValue(model);
        Toast.makeText(this, "Call ended", Toast.LENGTH_SHORT).show();
        onBackPressed();
        finish();



    }

    private void checkResponse() {
        reference_response = database.getReference("vcref").child(sender_uid).child(receiver_uid);
        reference_response.child("res").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String key = snapshot.child("key").getValue().toString();
                    String response = snapshot.child("response").getValue().toString();

                    if(response.equals("yes")){
                        joinmeeting(key);
                        Toast.makeText(VideoCallOutgoing.this, "Call accepted", Toast.LENGTH_SHORT).show();
                    }else if(response.equals("no")){
                        Toast.makeText(VideoCallOutgoing.this, "Call denied", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }else{
                    Toast.makeText(VideoCallOutgoing.this, "Not responding", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void joinmeeting(String key) {
        try{
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(key)
                    .setWelcomePageEnabled(false)
                    .build();
            JitsiMeetActivity.launch(VideoCallOutgoing.this,options);
            finish();
        }catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void sendCallInvitation() {

        databaseReference dbr = new databaseReference();
        FirebaseDatabase.getInstance(dbr.keyDb()).getReference("Token").child(receiver_uid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                receiver_token = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            FcmNotificationsSender notificationsSender =
                    new FcmNotificationsSender(receiver_token,"v",sender_uid,
                            getApplicationContext(),VideoCallOutgoing.this);

            notificationsSender.SendNotifications();

        },1000);



    }

}