package com.example.study_with_me;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class callActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView back;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference callref,cancelRef;

    LinearLayoutManager linearLayoutManager;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userid = user.getUid();
    String inte;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        Bundle extras = getIntent().getExtras();
        if(extras!=null) inte = extras.getString("i");
        else Toast.makeText(this, "No Interest data found", Toast.LENGTH_SHORT).show();

        callref = (DatabaseReference) database.getReference("All users");
        query = callref.orderByChild("interest").equalTo(inte);


        back = findViewById(R.id.tv_back_call);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.rv_list_call);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        callref.keepSynced(true);

        back.setOnClickListener(v -> onBackPressed());

    }

    @Override
    protected void onStart() {
        super.onStart();

        cancelRef = database.getInstance().getReference("cancel");
        cancelRef.removeValue();


        FirebaseRecyclerOptions<AlluserMember> options =
                new FirebaseRecyclerOptions.Builder<AlluserMember>()
                        .setQuery(query,AlluserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AlluserMember,ProfileViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<AlluserMember,ProfileViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull AlluserMember model) {

                        holder.setProf(getApplication(), model.getUid(),model.getName(),model.getUrl(),model.getEmail(),model.getInterest(),
                                model.getAbout());

                        String receiverId = getItem(position).getUid();

                        holder.calluser.setOnClickListener(v -> {
                            Intent intent = new Intent(callActivity.this,VideoCallOutgoing.class);
                            intent.putExtra("uid",receiverId);
                            startActivity(intent);
                        });

                    }

                    @NonNull
                    @Override
                    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.user_layout_item,parent,false);

                        return new ProfileViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}