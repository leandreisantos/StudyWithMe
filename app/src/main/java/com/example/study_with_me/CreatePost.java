package com.example.study_with_me;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreatePost extends AppCompatActivity {

    ImageButton post;
    TextView back,upload;
    ImageView iv;

    EditText title,desc;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    DatabaseReference db1,db3;

    private static final int PICK_FILE = 1;
    private Uri selectedUri;
    UploadTask uploadTask;
    StorageReference storageReference;

    String type,name,url;
    PostMember postMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postMember = new PostMember();

        db3 = database.getReference("All post");
        db1 = database.getReference("All images").child(currentuid);
        storageReference = FirebaseStorage.getInstance().getReference("User posts");

        post = findViewById(R.id.edit_profile);
        back = findViewById(R.id.tv_back_cp);
        upload = findViewById(R.id.tv_upload);

        title = findViewById(R.id.et_title_cp);
        desc = findViewById(R.id.et_desc_cp);
        iv = findViewById(R.id.iv_cp);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(CreatePost.this,MainActivity.class);
            startActivity(intent);
        });

        upload.setOnClickListener(v -> chooseImage());
        post.setOnClickListener(v -> Dopost());


    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_FILE || resultCode == RESULT_OK || data!= null || data.getData() != null){

                selectedUri = data.getData();
                Picasso.get().load(selectedUri).into(iv);
                iv.setVisibility(View.VISIBLE);
                upload.setVisibility(View.GONE);
                type = "iv";
        }

    }
    private String getFileExt(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private void Dopost() {
        String t = title.getText().toString();
        String d = desc.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());


        if(!(TextUtils.isEmpty(d) && TextUtils.isEmpty(d))&&selectedUri == null){

            String id1 = db3.push().getKey();

            postMember.setId(currentuid);
            postMember.setUrl(url);
            postMember.setTime(savetime);
            postMember.setDate(savedate);
            postMember.setUid(currentuid);
            postMember.setType("text");
            postMember.setDesc(d);
            postMember.setTitle(t);
            postMember.setName(name);
            postMember.setPostkey(id1);

            db3.child(id1).setValue(postMember);

            Toast.makeText(this, "post upload!", Toast.LENGTH_SHORT).show();
            Toast.makeText(CreatePost.this, "post uploaded", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CreatePost.this,MainActivity.class);
            startActivity(intent);

        }else if(TextUtils.isEmpty(d) || TextUtils.isEmpty(d)){
            Toast.makeText(this, "input fields!", Toast.LENGTH_SHORT).show();
        }else if(!(TextUtils.isEmpty(d) && TextUtils.isEmpty(d))&&selectedUri != null){

            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(selectedUri));
            uploadTask = reference.putFile(selectedUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()){
                    throw task.getException();

                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();

                    if(type.equals("iv")) {
                        String id1 = db3.push().getKey();

                        postMember.setId(currentuid);
                        postMember.setUrl(url);
                        postMember.setPostUri(downloadUri.toString());
                        postMember.setTime(savetime);
                        postMember.setDate(savedate);
                        postMember.setUid(currentuid);
                        postMember.setType("iv");
                        postMember.setDesc(d);
                        postMember.setTitle(t);
                        postMember.setName(name);
                        postMember.setPostkey(id1);

                        //for image
                        String id = db1.push().getKey();
                        db1.child(id).setValue(postMember);
                        //for both
                        db3.child(id1).setValue(postMember);


                        Toast.makeText(CreatePost.this, "post uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreatePost.this,MainActivity.class);
                        startActivity(intent);

                     }else{
                        Toast.makeText(CreatePost.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(currentuid);


        documentReference.get()
                .addOnCompleteListener(task -> {

                    if(task.getResult().exists()){
                        name = task.getResult().getString("name");
                        url = task.getResult().getString("url");

                    }else{
                        Toast.makeText(CreatePost.this, "Error", Toast.LENGTH_SHORT).show();
                    }


                });
    }
}