package com.example.study_with_me;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    databaseReference dbr = new databaseReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance(dbr.keyDb());

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentid = user.getUid();

    ImageView userdp;
    TextView username,prof,email,interest,about;
    ImageButton edit;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userdp = getActivity().findViewById(R.id.iv_profile);
        username = getActivity().findViewById(R.id.tv_username_profile);
        prof = getActivity().findViewById(R.id.tv_prof);
        email = getActivity().findViewById(R.id.tv_email);
        interest = getActivity().findViewById(R.id.tv_interest);
        about= getActivity().findViewById(R.id.tv_about);

    }

    @Override
    public void onStart() {
        super.onStart();

        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("user").document(currentid);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task){
                        if(task.getResult().exists()){

                            String nameResult = task.getResult().getString("name");
                            String bioResult = task.getResult().getString("about");
                            String emailResult = task.getResult().getString("email");
                            String interestResult = task.getResult().getString("interest");
                            String url = task.getResult().getString("url");
                            String profResult = task.getResult().getString("prof");

                            Picasso.get().load(url).into(userdp);
                            username.setText(nameResult);
                            prof.setText(profResult);
                            email.setText(emailResult);
                            interest.setText(interestResult);
                            about.setText(bioResult);

                        }else{
                            Intent intent = new Intent(getActivity(),CreateProfile.class);
                            startActivity(intent);
                        }
                    }
                });


    }
}
