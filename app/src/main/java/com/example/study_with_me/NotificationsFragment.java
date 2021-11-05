package com.example.study_with_me;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationsFragment extends Fragment {

    RecyclerView recyclerView;
    ImageView search;
    EditText search_et;
    TextView lbl;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference ntRef,df;
    String userid;
    LinearLayoutManager linearLayoutManager;
    String image;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notifications,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        df = FirebaseDatabase.getInstance().getReference("notification");

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = getActivity().findViewById(R.id.rv_new);
//        search= findViewById(R.id.search_btn_an);
//        search_et = findViewById(R.id.search_et_an);
//        lbl = findViewById(R.id.tv_lbl);


        ntRef = database.getReference("notification").child(userid);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        ntRef.keepSynced(true);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<NewMember> options =
                new FirebaseRecyclerOptions.Builder<NewMember>()
                        .setQuery(ntRef,NewMember.class)
                        .build();

        FirebaseRecyclerAdapter<NewMember,newViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<NewMember, newViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull newViewholder holder, int position, @NonNull NewMember model) {

                        holder.setNotification(getActivity(),model.getUrl(),model.getName(),model.getText(),model.getUidsender(),model.getAction()
                        ,model.getUiduser(),model.getDate(),model.getTime(),model.getInterest(),model.getTitle(),model.getIdPost(),model.getPostkey());

                        String post_key = getItem(position).getIdPost();

                        holder.moretv.setOnClickListener(v -> {
                            Toast.makeText(getActivity(), "deleted", Toast.LENGTH_SHORT).show();
                            ntRef.child(post_key).removeValue();
                        });


                    }

                    @NonNull
                    @Override
                    public newViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.notification_layout_item,parent,false);

                        return new newViewholder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);




    }
}
