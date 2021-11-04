package com.example.study_with_me;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile extends AppCompatActivity {

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference databaseReference;
    DocumentReference documentReference;
    AlluserMember member;
    ProgressBar pb;

    Uri imageUri;
    UploadTask uploadTask;
    StorageReference storageReference;
    private static final int PICK_IMAGE=1;
    String currentUserId;

    ImageView dp;
    EditText etname,etInterest,etProfession,etEmail,etAbout;
    ImageButton start;

    private static final int PICK_FILE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        documentReference = db.collection("user").document(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");
        databaseReference = database.getReference("All users");

        member= new AlluserMember();

        dp = findViewById(R.id.iv_cp);
        etname = findViewById(R.id.et_name_cp);
        etProfession = findViewById(R.id.et_profession_cp);
        etEmail = findViewById(R.id.et_email_cp);
        etInterest = findViewById(R.id.et_interest_cp);
        etAbout = findViewById(R.id.et_about_cp);
        start = findViewById(R.id.start_cp);
        pb = findViewById(R.id.pv_cp);


        start.setOnClickListener(v -> uploadData());

        dp.setOnClickListener(v -> {

            chooseImage();
        });

    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == PICK_FILE && resultCode == RESULT_OK && data!= null && data.getData() != null){
                imageUri = data.getData();
                Picasso.get().load(imageUri).into(dp);
            }

    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }


    private void uploadData() {
        pb.setVisibility(View.GONE);
        String name = etname.getText().toString();
        String prof = etProfession.getText().toString();
        String email = etEmail.getText().toString();
        String interest = etInterest.getText().toString();
        String about = etAbout.getText().toString();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(prof) && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(interest) && !TextUtils.isEmpty(about) && imageUri != null){

            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));
            uploadTask = reference.putFile(imageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {

                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();

                    Map<String,String> profile = new HashMap<>();
                    profile.put("name",name);
                    profile.put("prof",prof);
                    profile.put("url",downloadUri.toString());
                    profile.put("email",email);
                    profile.put("interest",interest);
                    profile.put("about",about);
                    profile.put("uid",currentUserId);


                    member.setName(name.toUpperCase());
                    member.setProf(prof.toUpperCase());
                    member.setUid(currentUserId);
                    member.setUrl(downloadUri.toString());
                    member.setInterest(interest.toUpperCase());
                    member.setEmail(email);
                    member.setAbout(about);

                    databaseReference.child(currentUserId).setValue(member);

                    documentReference.set(profile)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                    Intent intent = new Intent(CreateProfile.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                },2000);
                            });
                }
            });

        }else{
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
        }
    }
}