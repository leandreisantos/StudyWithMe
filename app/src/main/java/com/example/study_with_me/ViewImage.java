package com.example.study_with_me;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ViewImage extends AppCompatActivity {

    String id,url,postkey,name;
    ImageView iv_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            id = extras.getString("i");
            url = extras.getString("iv");
            postkey = extras.getString("p");
            name = extras.getString("n");
        }

        iv_main = findViewById(R.id.iv_vm);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Picasso.get().load(url).into(iv_main);
    }
}