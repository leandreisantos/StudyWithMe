package com.example.study_with_me;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class newViewholder extends RecyclerView.ViewHolder{

    public TextView nametv,datetv,actiontv,titletv,interesttv,moretv;
    public ImageView dpsender;



    public newViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setNotification(FragmentActivity activity,String url, String name, String text, String uidsender, String action, String uiduser, String date, String time,
                                String interest, String title, String idPost, String postkey){

        nametv = itemView.findViewById(R.id.tv_name_nli);
        datetv = itemView.findViewById(R.id.tv_date_nli);
        actiontv = itemView.findViewById(R.id.tv_action_nli);
        titletv = itemView.findViewById(R.id.tv_title_nli);
        interesttv = itemView.findViewById(R.id.tv_int_nli);
        moretv = itemView.findViewById(R.id.tv_more_nli);
        dpsender = itemView.findViewById(R.id.iv_dp_nli);

        
        nametv.setText(name);
        datetv.setText(date);
        actiontv.setText(text);
        titletv.setText(title);
        interesttv.setText(interest);
        Picasso.get().load(url).into(dpsender);

    }
}
