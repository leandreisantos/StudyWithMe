package com.example.study_with_me;

import android.app.AlertDialog;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class DashboardFragment extends Fragment {

    SwipeRefreshLayout sp;

    TextView addTv;
    RecyclerView recyclerView;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,likesref,likelist,ntref;

    LinearLayoutManager linearLayoutManager;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    String currentuid,interest_result;

    Boolean likechecker = false;

    AlluserMember userMember;
    NewMember newMember;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        reference = database.getReference("All post");
        likesref = database.getReference("post likes");
        ntref = database.getReference("notification").child(currentuid);
        documentReference = db.collection("user").document(currentuid);

        userMember = new AlluserMember();
        newMember = new NewMember();

        recyclerView = getActivity().findViewById(R.id.rv_dash);
        sp = getActivity().findViewById(R.id.sr_dash);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        addTv = getActivity().findViewById(R.id.tv_add_dash);
        addTv.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),CreatePost.class);
            startActivity(intent);
        });

        sp.setOnRefreshListener(() -> sp.setRefreshing(false));

    }

    @Override
    public void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(task -> {
                    if(task.getResult().exists()){
                        interest_result = task.getResult().getString("name");
                    }
                });


        FirebaseRecyclerOptions<PostMember> options =
                new FirebaseRecyclerOptions.Builder<PostMember>()
                        .setQuery(reference,PostMember.class)
                        .build();

        FirebaseRecyclerAdapter<PostMember,PostViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PostMember, PostViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostViewholder holder, int position, @NonNull PostMember model) {

                        final String postkey = getRef(position).getKey();

                        holder.SetPost(getActivity(),model.getId(),model.getUrl(),model.getPostUri(),model.getTime(),model.getDate(),model.getUid(),
                                model.getType(),model.getDesc(),model.getTitle(),model.getName());

                        String name = getItem(position).getName();
                        String url = getItem(position).getPostUri();
                        String time = getItem(position).getTime();
                        String type = getItem(position).getType();
                        String id = getItem(position).getId();
                        String userid = getItem(position).getUid();
                        String desc = getItem(position).getDesc();

                        holder.iv_post.setOnClickListener(v -> ShowPost(url,userid,postkey,name));

                        holder.likechecker(postkey);

                        holder.tv_more.setOnClickListener(v -> showDialog(type,id));

                        holder.tv_like.setOnClickListener(v -> {
                            likechecker = true;

                            likesref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if(likechecker.equals(true)){
                                        if(snapshot.child(postkey).hasChild(currentuid)){
                                            likesref.child(postkey).child(currentuid).removeValue();
                                            likelist = database.getReference("like list").child(postkey).child(currentuid);
                                            likelist.removeValue();
                                            //delete(time);

                                            ntref.child(currentuid+"l").removeValue();
                                            likechecker = false;
                                        }else{
                                            likesref.child(postkey).child(currentuid).setValue(true);
                                            likelist = database.getReference("like list").child(postkey);
                                            userMember.setName(name);
                                            userMember.setUid(currentuid);
                                            userMember.setUrl(url);

                                            likelist.child(currentuid).setValue(userMember);

//                                                newMember.setName(name_result);
//                                                newMember.setUid(currentUserid);
//                                                newMember.setUrl(url_result);
//                                                newMember.setSeen("no");
//                                                newMember.setText("Like your post");
//                                                newMember.setAction("L");
//
//                                                ntref.child(currentUserid+"l").setValue(newMember);
//                                                sendNotification(name_result,userid);

                                            likechecker = false;
                                        }
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        });

                        holder.iv.setOnClickListener(v -> {
                            if (currentuid.equals(userid)) {
//                                Intent intent = new Intent(getActivity(),MyProfileActivity.class);
//                                startActivity(intent);

                            }else {
                                Intent intent = new Intent(getActivity(),ShowUser.class);
                                intent.putExtra("n",name);
                                intent.putExtra("u",url);
                                intent.putExtra("uid",userid);
                                startActivity(intent);
                            }
                        });

                        holder.commentbtn.setOnClickListener(view -> {
                            Intent intent = new Intent(getActivity(),CommentsActivity.class);
                            intent.putExtra("postkey",postkey);
                            intent.putExtra("name",name);
                            intent.putExtra("url",url);
                            intent.putExtra("uid",userid);
                            intent.putExtra("d",desc);
                            startActivity(intent);
                        });



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

    private void ShowPost(String url,String uid,String postkey,String name) {
        Intent intent = new Intent(getActivity(),ViewImage.class);
        intent.putExtra("i",uid);
        intent.putExtra("iv",url);
        intent.putExtra("p",postkey);
        intent.putExtra("n",name);
        startActivity(intent);
    }

    private void showDialog(String type,String id) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.more_layout,null);
        TextView download = view.findViewById(R.id.download_tv_post);
        TextView edit = view.findViewById(R.id.edit_tv_post);
        TextView share = view.findViewById(R.id.share_tv_post);
        TextView delete = view.findViewById(R.id.delete_tv_post);
        TextView copyurl = view.findViewById(R.id.copyurl_tv_post);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();

        alertDialog.show();

        if(type.equals("text")){
            download.setVisibility(View.GONE);
        }else{
            download.setVisibility(View.VISIBLE);
        }

        if(!(id.equals(currentuid))){
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }else if(id.equals(currentuid)){
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
        }
    }
}
