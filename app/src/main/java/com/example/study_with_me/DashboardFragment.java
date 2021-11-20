package com.example.study_with_me;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class DashboardFragment extends Fragment {

    SwipeRefreshLayout sp;

    TextView addTv,interest,add_interest;
    RecyclerView recyclerView;
    EditText etSearch;

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());
    DatabaseReference reference,likesref,likelist,ntref,db1,reference_user;

    LinearLayoutManager linearLayoutManager;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    String currentuid;
    TextView call;


    Boolean likechecker = false;
    public String interestResult="";
    AlluserMember userMember;
    NewMember newMember;

    String urluser,nameuser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        interest = getActivity().findViewById(R.id.tv_int_int);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        db1 = database.getReference("All images").child(currentuid);


        likesref = database.getReference("post likes");
        ntref = database.getReference("notification");
        documentReference = db.collection("user").document(currentuid);

        reference_user = database.getReference("All users").child(currentuid);

        add_interest = getActivity().findViewById(R.id.tv_edit_int_dash);
        etSearch = getActivity().findViewById(R.id.et_search_dash);
        call = getActivity().findViewById(R.id.tv_call_dash);

        userMember = new AlluserMember();
        newMember = new NewMember();

        recyclerView = getActivity().findViewById(R.id.rv_dash);
        sp = getActivity().findViewById(R.id.sr_dash);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        addTv = getActivity().findViewById(R.id.tv_add_dash);

        sp.setOnRefreshListener(() -> sp.setRefreshing(false));



    }

    private void goToInt(String inte) {
        Intent intent = new Intent(getActivity(),InputInterestActivity.class);
        intent.putExtra("i",inte);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();


        DocumentReference referenceuser;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        referenceuser = firestore.collection("user").document(currentuid);

        referenceuser.get()
                .addOnCompleteListener(task -> {
                    if(!task.getResult().exists()) {
                        Intent intent = new Intent(getActivity(), CreateProfile.class);
                        startActivity(intent);
                    }else{
                        interestResult = task.getResult().getString("interest").toUpperCase();
                        urluser = task.getResult().getString("url");
                        nameuser = task.getResult().getString("name");
                        if(interestResult!=null){
                            doActivity(interestResult,urluser,nameuser);
                        }

                    }
                });

    }

    private void doActivity(String interest_result,String url_user,String name_user) {


        reference = database.getReference("All post").child(interest_result);

        interest.setText(interest_result);

        interest.setOnClickListener(v -> goToInt(interest_result));

        addTv.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),CreatePost.class);
            intent.putExtra("i",interest_result);
            startActivity(intent);
        });

        call.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(),callActivity.class);
//            intent.putExtra("i",interest_result);
//            startActivity(intent);
            Toast.makeText(getActivity(), "Call function not available today", Toast.LENGTH_SHORT).show();
        });

        add_interest.setOnClickListener(v -> goToInt(interest_result));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                showRec("s",interest_result,url_user,name_user);
            }
        });

        showRec("n",interest_result,url_user,name_user);

    }

    private void showRec(String keys,String interest,String urlUser,String nameUser) {
        FirebaseRecyclerOptions<PostMember> options= null;

        if(keys.equals("n")){
                   options =
                    new FirebaseRecyclerOptions.Builder<PostMember>()
                            .setQuery(reference,PostMember.class)
                            .build();
        }else if(keys.equals("s")){
            String query = etSearch.getText().toString();
            Query search = reference.orderByChild("title").startAt(query).endAt(query+"\uf0ff");
            options =
                    new FirebaseRecyclerOptions.Builder<PostMember>()
                            .setQuery(search,PostMember.class)
                            .build();
        }

        FirebaseRecyclerAdapter<PostMember,PostViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PostMember, PostViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostViewholder holder, int position, @NonNull PostMember model) {

                        final String postkey = getRef(position).getKey();

                        holder.SetPost(getActivity(),model.getId(),model.getUrl(),model.getPostUri(),model.getTime(),model.getDate(),model.getUid(),
                                model.getType(),model.getDesc(),model.getTitle(),model.getName(),model.getPostkey());

                        String name = getItem(position).getName();
                        String url = getItem(position).getPostUri();
                        String time = getItem(position).getTime();
                        String type = getItem(position).getType();
                        String id = getItem(position).getId();
                        String userid = getItem(position).getUid();
                        String desc = getItem(position).getDesc();
                        String post_key = getItem(position).getPostkey();
                        String title = getItem(position).getTitle();
                        String desc_p = getItem(position).getDesc();
                        String url1 = getItem(position).getUrl();

                        holder.iv_post.setOnClickListener(v -> ShowPost(url,userid,postkey,name));

                        holder.likechecker(postkey);
                        holder.commentchecker(postkey);

                        holder.tv_more.setOnClickListener(v -> showDialog(type,id,name,url,time,post_key,title,desc_p));

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

                                            ntref.child(userid).child(post_key).removeValue();
                                            likechecker = false;
                                        }else{
                                            likesref.child(postkey).child(currentuid).setValue(true);
                                            likelist = database.getReference("like list").child(postkey);
                                            userMember.setName(name);
                                            userMember.setUid(currentuid);
                                            userMember.setUrl(url);

                                            likelist.child(currentuid).setValue(userMember);

                                            Calendar cdate = Calendar.getInstance();
                                            SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyy");
                                            final String savedate = currentdate.format(cdate.getTime());

                                            Calendar ctime = Calendar.getInstance();
                                            SimpleDateFormat currenttime =new SimpleDateFormat("HH-mm-ss");
                                            final String savetime = currenttime.format(ctime.getTime());

                                            String id1 = ntref.push().getKey();

                                            newMember.setUrl(urlUser);
                                            newMember.setName(nameUser);
                                            newMember.setText(name + "Likes your post");
                                            newMember.setUidsender(userid);
                                            newMember.setAction("L");
                                            newMember.setUiduser(currentuid);
                                            newMember.setTime(savetime);
                                            newMember.setDate(savedate);
                                            newMember.setInterest(interest);
                                            newMember.setTitle(title);
                                            newMember.setIdPost(post_key);
                                            newMember.setPostkey(id1);

                                            ntref.child(userid).child(post_key).setValue(newMember);
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

                            intent.putExtra("url1",urlUser);
                            intent.putExtra("name1",nameUser);
                            intent.putExtra("uidsender",userid);
                            intent.putExtra("interest",interest);
                            intent.putExtra("title",title);
                            intent.putExtra("idPost",post_key);
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

    private void showDialog(String type,String id,String name,String url,String time,String post_key,String title,String desc_p) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.more_layout,null);
        TextView download = view.findViewById(R.id.download_tv_post);
        TextView edit = view.findViewById(R.id.edit_tv_post);
        TextView share = view.findViewById(R.id.share_tv_post);
        TextView delete = view.findViewById(R.id.delete_tv_post);
        TextView copyurl = view.findViewById(R.id.copyurl_tv_post);
        TextView report = view.findViewById(R.id.report_tv_post);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();

        alertDialog.show();

        if(type.equals("text")) download.setVisibility(View.GONE);
        else download.setVisibility(View.VISIBLE);

        if(!(id.equals(currentuid))){
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            report.setVisibility(View.VISIBLE);
        }else if(id.equals(currentuid)){
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            report.setVisibility(View.GONE);
        }

        share.setOnClickListener(v -> {
            String sharetext = name + "\n" + "\n" + url;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT,sharetext);
            intent.setType("text/plain");
            startActivity(intent.createChooser(intent,"share via"));

            alertDialog.dismiss();
        });

        delete.setOnClickListener(v -> {

            Query query = db1.orderByChild("time").equalTo(time);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
                        dataSnapshot1.getRef().removeValue();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Query query3 = reference.orderByChild("time").equalTo(time);
            query3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
                        dataSnapshot1.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if(type.equals("text")) Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
            else{
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                reference.delete().addOnSuccessListener(aVoid -> {
                });
            }

            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });

        report.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),ReportPost.class);
            intent.putExtra("id_post",post_key);
            intent.putExtra("id_report",currentuid);
            intent.putExtra("t",title);
            intent.putExtra("d",desc_p);
            startActivity(intent);
        });



    }
}
