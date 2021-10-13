package com.example.study_with_me;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class PostViewholder extends RecyclerView.ViewHolder{

    ImageView iv,iv_post;
    TextView tv_name,tv_title,tv_desc;
    CardView cv;


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
        cv = itemView.findViewById(R.id.cv2_pl);

        if(type.equals("text")){
            cv.setVisibility(View.GONE);
            iv_post.setVisibility(View.GONE);
            Picasso.get().load(url).into(iv);
            tv_name.setText(name);
            tv_title.setText(title);
            tv_desc.setText(desc);
        }else if(type.equals("iv")){
            cv.setVisibility(View.VISIBLE);
            iv_post.setVisibility(View.VISIBLE);
            Picasso.get().load(url).into(iv);
            Picasso.get().load(postUri).into(iv_post);
            tv_name.setText(name);
            tv_title.setText(title);
            tv_desc.setText(desc);
        }else{
            Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
        }

    }
}
