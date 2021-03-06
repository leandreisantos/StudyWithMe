package com.example.study_with_me;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsViewholder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nameTv,timeTv,ansTv,tv_likes,delete;
    TextView likebutton;
    int likescount ;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userid = user.getUid();
    
    public CommentsViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setComment(Application application, String comment, String time, String url, String username, String uid,String postkey){

        imageView = itemView.findViewById(R.id.imageView_comment_item);
        nameTv = itemView.findViewById(R.id.tv_name_comment_item);
        timeTv = itemView.findViewById(R.id.tv_time_comment_item);
        ansTv = itemView.findViewById(R.id.tv_comment_item);
        delete = itemView.findViewById(R.id.del_comment);


        nameTv.setText(username);
        timeTv.setText(time);
        ansTv.setText(comment);
        Picasso.get().load(url).into(imageView);

        if(uid.equals(userid)) delete.setVisibility(View.VISIBLE);
        else delete.setVisibility(View.GONE);


    }

    public void LikeChecker(String postkey) {


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("comment likes");
        likebutton = itemView.findViewById(R.id.likebutton_comment_item);

        tv_likes = itemView.findViewById(R.id.tv_like_comment_item);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postkey).hasChild(currentUserId)){
                    likescount = (int)snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likescount));
                    likebutton.setBackgroundResource(R.drawable.liked_icon);


                }else {
                    likescount = (int)snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likescount)+"Likes");
                    likebutton.setBackgroundResource(R.drawable.like_icon);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}
