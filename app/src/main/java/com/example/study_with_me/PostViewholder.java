package com.example.study_with_me;

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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class PostViewholder extends RecyclerView.ViewHolder{

    ImageView iv,iv_post;
    TextView tv_name,tv_title,tv_desc,tv_date,tv_more,tv_like,tv_like_c,commentbtn;
    CardView cv;
    int likescount,commentcount;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference likesref,commentref;


    public PostViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void SetPost(FragmentActivity activity, String id, String url, String postUri, String time,String date,String uid,
                        String type, String desc,String title,String name){

        iv = itemView.findViewById(R.id.iv_profile_pl);
        iv_post = itemView.findViewById(R.id.iv_post_lp);
        tv_name = itemView.findViewById(R.id.tv_name_pl);
        tv_title = itemView.findViewById(R.id.tv_title_pl);
        tv_desc = itemView.findViewById(R.id.tv_desc_pl);
        tv_like = itemView.findViewById(R.id.tv_like_pl);
        commentbtn = itemView.findViewById(R.id.tv_comment_pl);
        tv_date = itemView.findViewById(R.id.tv_date_pl);
        tv_more = itemView.findViewById(R.id.tv_more_pl);
        tv_like_c = itemView.findViewById(R.id.tv_likecount_pl);
        cv = itemView.findViewById(R.id.cv2_pl);

        if(type.equals("text")){
            cv.setVisibility(View.GONE);
            iv_post.setVisibility(View.GONE);
            Picasso.get().load(url).into(iv);
            tv_name.setText(name);
            tv_title.setText(title);
            tv_desc.setText(desc);
            tv_date.setText(date);
        }else if(type.equals("iv")){
            cv.setVisibility(View.VISIBLE);
            iv_post.setVisibility(View.VISIBLE);
            Picasso.get().load(url).into(iv);
            Picasso.get().load(postUri).into(iv_post);
            tv_name.setText(name);
            tv_title.setText(title);
            tv_desc.setText(desc);
            tv_date.setText(date);
        }else{
            Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
        }

    }

    public void likechecker(String postKey) {

        likesref = database.getReference("post likes");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();


        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(postKey).hasChild(uid)){
                    tv_like.setBackgroundResource(R.drawable.liked_icon);
                    likescount = (int)snapshot.child(postKey).getChildrenCount();
                    tv_like_c.setText(Integer.toString(likescount));

                }else{
                    tv_like.setBackgroundResource(R.drawable.like_icon);
                    likescount = (int)snapshot.child(postKey).getChildrenCount();
                    tv_like_c.setText(Integer.toString(likescount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
