package com.example.study_with_me;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CommentsActivity extends AppCompatActivity {

    String url,name,post_key,userid,bundleuid;
    String name_result,age_result,Url,uid,bio_result,web_result,email_result,usertoken,desc;
    RecyclerView recyclerView;
    TextView commentsBtn;
    EditText commentsEdittext;

    DatabaseReference Commentref,userCommentref,likesref,ntref;
    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());

    Boolean likeChecker = false;

    NewMember newMember;
    CommentsMember commentsMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentsMember = new CommentsMember();

        newMember = new NewMember();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();



        Bundle extras = getIntent().getExtras();

        if (extras != null){
            url = extras.getString("url");
            name = extras.getString("name");
            post_key = extras.getString("postkey");
            bundleuid = extras.getString("uid");
            desc = extras.getString("d");
        }else {
            Toast.makeText(this, "No Data pass", Toast.LENGTH_SHORT).show();
        }

        commentsBtn = findViewById(R.id.btn_comments);
        commentsEdittext = findViewById(R.id.et_comments);


        recyclerView = findViewById(R.id.recycler_view_comments);

        recyclerView.setHasFixedSize(true);
        //   MediaController mediaController;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Commentref = database.getReference("All posts").child(post_key).child("Comments");

        likesref = database.getReference("comment likes");
        userCommentref = database.getReference("User Posts").child(userid);

        ntref = database.getReference("notification").child(bundleuid);

        commentsBtn.setOnClickListener(view -> comment());


    }

    private void comment() {
        GetCurrentTime gc = new GetCurrentTime();
        String time = gc.ctime();
        String comment = commentsEdittext.getText().toString();
        if (comment != null){

            commentsMember.setComment(comment);
            commentsMember.setUsername(name_result);
            commentsMember.setUid(uid);
            commentsMember.setTime(time);
            commentsMember.setUrl(Url);

            String pushkey = Commentref.push().getKey();
            Commentref.child(pushkey).setValue(commentsMember);

            commentsEdittext.setText("");


            newMember.setName(name);
            newMember.setUid(userid);
            newMember.setUrl(Url);
            newMember.setSeen("no");
            newMember.setText("Commented on your post: " + comment);
            newMember.setAction("C");

            String key = ntref.push().getKey();
            ntref.child(key).setValue(newMember);
            sendNotification(bundleuid,name_result,comment);

            Toast.makeText(this, "Commented", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Please write comment", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(String bundleuid, String name_result, String comment) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(userid);

        documentReference.get()
                .addOnCompleteListener(task -> {
                    if (task.getResult().exists()) {
                        name_result = task.getResult().getString("name");
                        age_result = task.getResult().getString("age");
                        bio_result = task.getResult().getString("bio");
                        email_result = task.getResult().getString("email");
                        web_result = task.getResult().getString("website");
                        Url = task.getResult().getString("url");
                        uid = task.getResult().getString("uid");
                    }
                });

        FirebaseRecyclerOptions<CommentsMember> options =
                new FirebaseRecyclerOptions.Builder<CommentsMember>()
                        .setQuery(Commentref,CommentsMember.class)
                        .build();

        FirebaseRecyclerAdapter<CommentsMember,CommentsViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<CommentsMember, CommentsViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CommentsViewholder holder, int position, @NonNull CommentsMember model) {


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserId = user.getUid();
                        final String postkey = getRef(position).getKey();
                        String time = getItem(position).getTime();

                        holder.setComment(getApplication(),model.getComment(),model.getTime(),model.getUrl(),model.getUsername(),model.getUid());

                        holder.LikeChecker(postkey);

                        holder.delete.setOnClickListener(view -> {
                            Query query = Commentref.orderByChild("time").equalTo(time);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                        dataSnapshot1.getRef().removeValue();

                                        Toast.makeText(CommentsActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                        });
                        holder.likebutton.setOnClickListener(view -> {

                            likeChecker = true;

                            likesref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (likeChecker.equals(true)){
                                        if (snapshot.child(postkey).hasChild(currentUserId)){
                                            likesref.child(postkey).child(currentUserId).removeValue();
                                            likeChecker = false;

                                        }else {
                                            likesref.child(postkey).child(currentUserId).setValue(true);
                                            likeChecker = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        });
                    }

                    @NonNull
                    @Override
                    public CommentsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.comments_layout_item,parent,false);

                        return new CommentsViewholder(view);
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }
}