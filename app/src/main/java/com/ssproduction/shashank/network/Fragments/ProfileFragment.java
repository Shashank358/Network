package com.ssproduction.shashank.network.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprylab.android.widget.TextureVideoView;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.LoginActivity;
import com.ssproduction.shashank.network.EditProfileActivity;
import com.ssproduction.shashank.network.PrivacyActivity;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Publish;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private android.support.v7.widget.Toolbar mToolbar;
    private LinearLayout moreLinear, profileLinear, privacyLinear;

    private CircleImageView profileImage;
    private TextView userName, userId, userCollegeName;
    private TextView followersCount, followingCount, publishCount;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, followDatabase, publishDatabase;
    private String mCurrentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        followDatabase = FirebaseDatabase.getInstance().getReference().child("Following").child("People");

        mDatabase.keepSynced(true);
        followDatabase.keepSynced(true);

        mToolbar = (android.support.v7.widget.Toolbar) view.findViewById(R.id.profile_frag_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle("User Profile");

        profileImage = (CircleImageView) view.findViewById(R.id.user_profile_image);
        userName = (TextView) view.findViewById(R.id.user_profile_name);
        userId = (TextView) view.findViewById(R.id.user_profile_id);
        userCollegeName = (TextView) view.findViewById(R.id.user_profile_college_name);
        moreLinear = (LinearLayout) view.findViewById(R.id.more_linear);
        profileLinear = (LinearLayout) view.findViewById(R.id.edit_profile_linear);
        privacyLinear = (LinearLayout) view.findViewById(R.id.privacy_linear);
        followingCount = (TextView) view.findViewById(R.id.profile_following_count);
        publishCount = (TextView) view.findViewById(R.id.profile_publish_count);

        final TextureVideoView videoView = (TextureVideoView) view.findViewById(R.id.videoView);
        moreLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
            }
        });

        profileLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProfileBottomSheet bottomSheet = new ProfileBottomSheet();
                bottomSheet.show(getFragmentManager(), bottomSheet.getTag());

            }
        });

        privacyLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacyIntent = new Intent(getActivity(), PrivacyActivity.class);
                startActivity(privacyIntent);
            }
        });

        //------------------publish count feature-----------------
        publishDatabase = FirebaseDatabase.getInstance().getReference().child("Publish");
        publishDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //-------------following count feature---------------

        followDatabase.child(mCurrentUser).addValueEventListener(new ValueEventListener() {
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


        mDatabase.child(mCurrentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("user_thumbImage").getValue().toString();
                String name = dataSnapshot.child("user_name").getValue().toString();
                String id = dataSnapshot.child("user_id").getValue().toString();
                String college = dataSnapshot.child("page_name").getValue().toString();

                userName.setText(name);
                userId.setText(id);
                userCollegeName.setText(college);
                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(image).placeholder(R.drawable.avatar).into(profileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Publish");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Publish publish = snapshot.getValue(Publish.class);
                    videoView.setVideoURI(Uri.parse(publish.getPublish()));
                    videoView.requestFocus();
                    videoView.start();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
