package com.ssproduction.shashank.network;

import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.Fragments.AllUserPagesFrag;
import com.ssproduction.shashank.network.Fragments.PagesPublishFrag;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private CircleImageView userImage;
    private TextView userName, userId, userPageName, followersCount, followingCount, publishCount;
    private TextView  pagesPubList, allPagesList;

    private DatabaseReference mDatabase, followDatabase, publishDatabase;
    private String currentUser, otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        otherUser = getIntent().getStringExtra("user_id");

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.profile_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userImage = (CircleImageView) findViewById(R.id.activity_user_profile_image);
        userName = (TextView) findViewById(R.id.activity_user_profile_name);
        userId = (TextView) findViewById(R.id.activity_user_profile_id);
        userPageName = (TextView) findViewById(R.id.activity_user_profile_page_name);
        followersCount = (TextView) findViewById(R.id.activity_profile_followers_count);
        followingCount = (TextView) findViewById(R.id.activity_profile_following_count);
        publishCount = (TextView) findViewById(R.id.activity_profile_publish_count);
        pagesPubList = (TextView) findViewById(R.id.activity_profile_pages_publish_text);
        allPagesList = (TextView) findViewById(R.id.activity_profile_all_pages_text);


        mDatabase.child(otherUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String user_image = dataSnapshot.child("user_image").getValue().toString();
                String user_id = dataSnapshot.child("user_id").getValue().toString();
                String user_name = dataSnapshot.child("user_name").getValue().toString();
                String page_name = dataSnapshot.child("page_name").getValue().toString();

                Picasso picasso = Picasso.get();
                picasso.load(user_image).placeholder(R.drawable.avatar).into(userImage);
                picasso.setIndicatorsEnabled(false);
                userName.setText(user_name);
                userId.setText(user_id);
                userPageName.setText(page_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //----------------------cut following feature--------------
        followDatabase = FirebaseDatabase.getInstance().getReference().child("Following")
                .child("People").child(otherUser);
        followDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int followers = (int) dataSnapshot.getChildrenCount();
                String count = String.valueOf(followers);
                followingCount.setText(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //------------------------list of content features---------------
        pagesPubList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pagesPubList.setBackgroundResource(R.drawable.follow_page_background);
                pagesPubList.setTextColor(Color.WHITE);
                allPagesList.setBackgroundResource(android.R.color.transparent);
                allPagesList.setTextColor(Color.DKGRAY);

                PagesPublishFrag publishFrag = new PagesPublishFrag();
                Bundle argument = new Bundle();
                argument.putString("user_id", otherUser);
                publishFrag.setArguments(argument);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.replace(R.id.activity_profile_list_container, publishFrag).commit();
            }
        });

        allPagesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                allPagesList.setBackgroundResource(R.drawable.follow_page_background);
                allPagesList.setTextColor(Color.WHITE);
                pagesPubList.setBackgroundResource(android.R.color.transparent);
                pagesPubList.setTextColor(Color.DKGRAY);

                AllUserPagesFrag userPagesFrag = new AllUserPagesFrag();
                Bundle argument = new Bundle();
                argument.putString("user_id", otherUser);
                userPagesFrag.setArguments(argument);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.replace(R.id.activity_profile_list_container, userPagesFrag).commit();

            }
        });
    }
}
