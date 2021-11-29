package com.example.study_with_me;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNav);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new DashboardFragment()).commit();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        Toast.makeText(MainActivity.this, "Token is missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    databaseReference dbr = new databaseReference();
                    FirebaseDatabase.getInstance(dbr.keyDb()).getReference("Token").child(uid).child("token").setValue(token);

                    // Log and toast
                    //String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });


    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNav = item -> {
        Fragment selected = null;
        switch(item.getItemId()){
            case R.id.dashboard_bottom:
                selected = new DashboardFragment();
                break;
            case R.id.Profile_bottom:
                selected = new ProfileFragment();
                break;
            case R.id.settings_bottom:
                selected = new SettingsFragment();
                break;
            case R.id.notification_bottom:
                selected = new NotificationsFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,selected).commit();
        return true;
    };


    @Override
    public void onClick(View v) {

    }
}