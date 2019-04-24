package com.ssproduction.shashank.network;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssproduction.shashank.network.Fragments.ChatsContainerFrag;
import com.ssproduction.shashank.network.Fragments.HomeFragments.HomeFragment;
import com.ssproduction.shashank.network.Fragments.NotificationFragment;
import com.ssproduction.shashank.network.Fragments.PeopleFragment;
import com.ssproduction.shashank.network.Fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;

    private int[] tabIcons = {R.drawable.ic_event_note_black_24dp, R.drawable.ic_chat_black_24dp
            , R.drawable.ic_people_black_24dp,R.drawable.ic_chat_black_24dp, R.drawable.ic_person_black_24dp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //-----------------------home page selection on create---------
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.main_container, new HomeFragment()).commit();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.main_navigation);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch (menuItem.getItemId()){
                    case R.id.main_timeline_item:
                        transaction.replace(R.id.main_container, new HomeFragment()).commit();
                        return true;

                    case R.id.main_chat_item:
                        transaction.replace(R.id.main_container, new ChatsContainerFrag()).commit();
                        return true;

                    case R.id.main_all_users_item:
                        transaction.replace(R.id.main_container, new PeopleFragment()).commit();
                        return true;

                    case R.id.main_notification_item:
                        transaction.replace(R.id.main_container, new NotificationFragment()).commit();
                        return true;

                    case R.id.main_profile_item:
                        transaction.replace(R.id.main_container, new ProfileFragment()).commit();
                        return true;

                }

                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {

        if (currentUser == null){
            Intent regIntent = new Intent(MainActivity.this, RegisterActivity.class);
            regIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(regIntent);
        }
    }
}
