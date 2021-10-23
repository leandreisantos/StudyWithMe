package com.example.study_with_me;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReportPost extends AppCompatActivity {

    String id_post,title,desc,id_report;
    TextView title_tv,desc_tv;
    EditText report;
    ImageButton reportPost;

    ReportMember member;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    DatabaseReference db1,db3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_post);

        member = new ReportMember();

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            id_post = extras.getString("id_post");
            id_report = extras.getString("id_report");
            title = extras.getString("t");
            desc = extras.getString("d");
        }else{
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }

        db3 = database.getReference("All report").child(id_post);

        title_tv = findViewById(R.id.tv_title1_report);
        desc_tv = findViewById(R.id.tv_desc_report);
        report = findViewById(R.id.et_issue_report);
        reportPost = findViewById(R.id.reportP);

        reportPost.setOnClickListener(v -> DoReport());


    }

    private void DoReport() {
        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
        final String savetime = currenttime.format(ctime.getTime());

        String problem = report.getText().toString();

        if(!TextUtils.isEmpty(problem)){
            member.setPost_key(id_post);
            member.setId_reporter(id_report);
            member.setDate(savedate);
            member.setTime(savetime);

            db3.child(id_report).setValue(member);
            Toast.makeText(this, "Post Reported we examine this post Thank you!", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(this, "Input Problem", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        title_tv.setText(title);
        desc_tv.setText(desc);

    }
}