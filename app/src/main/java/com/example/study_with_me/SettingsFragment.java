package com.example.study_with_me;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    
    TextView logout,settings,report;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        
        logout = getActivity().findViewById(R.id.tv_logout_st);
        settings = getActivity().findViewById(R.id.tv_about_st);
        report = getActivity().findViewById(R.id.tv_report_st);

        logout.setOnClickListener(v -> showlogout());
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),OnBoardingScreen.class);
            startActivity(intent);
        });
        report.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),ReportIssue.class);
            startActivity(intent);
        });
        

    }

    private void showlogout() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.logout_item_layout,null);
        TextView logout_tv = view.findViewById(R.id.logout_tv_ll);
        TextView cancel_tv = view.findViewById(R.id.cancel_tv_ll);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        alertDialog.show();
        logout_tv.setOnClickListener(v -> {
             mAuth.signOut();
//            FirebaseDatabase.getInstance(dbr.keyDb()).getReference("Token").child(uid).child("token").removeValue();
            startActivity(new Intent(getActivity(),LoginActivity.class));
        });
        cancel_tv.setOnClickListener((View v) -> {
            alertDialog.dismiss();
        });
    }
}
