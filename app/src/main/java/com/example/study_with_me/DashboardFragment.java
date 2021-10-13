package com.example.study_with_me;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DashboardFragment extends Fragment {

    TextView addTv;
    RecyclerView recyclerView;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,likesref,db1,db2,db3,storyRef,likelist,referenceDel,ntref;

    LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        reference = database.getReference("All post");

        recyclerView = getActivity().findViewById(R.id.rv_dash);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        addTv = getActivity().findViewById(R.id.tv_add_dash);
        addTv.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),CreatePost.class);
            startActivity(intent);
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<PostMember> options =
                new FirebaseRecyclerOptions.Builder<PostMember>()
                        .setQuery(reference,PostMember.class)
                        .build();

        FirebaseRecyclerAdapter<PostMember,PostViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PostMember, PostViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostViewholder holder, int position, @NonNull PostMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserid = user.getUid();

                        final String postkey = getRef(position).getKey();

                        holder.SetPost(getActivity(),model.getId(),model.getUrl(),model.getPostUri(),model.getTime(),model.getDate(),model.getUid(),
                                model.getType(),model.getDesc(),model.getTitle(),model.getName());

                    }

                    @NonNull
                    @Override
                    public PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_layout,parent,false);

                        return new PostViewholder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);



    }
}
