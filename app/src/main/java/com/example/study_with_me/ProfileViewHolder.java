package com.example.study_with_me;

import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileViewHolder extends RecyclerView.ViewHolder {
    
    public TextView nameuser,calluser;
    public ImageView dp;
    public ConstraintLayout cv;
    public String interestResult;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();


    
    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setProf(Application application,String uid,String name,String url,String email,String interest,String about){
        nameuser = itemView.findViewById(R.id.tv_name_nli);
        calluser = itemView.findViewById(R.id.tv_call_nli);
        dp = itemView.findViewById(R.id.iv_dp_uli);
        cv = itemView.findViewById(R.id.cl_nitem);

        Picasso.get().load(url).into(dp);
        nameuser.setText(name);


    }
}
