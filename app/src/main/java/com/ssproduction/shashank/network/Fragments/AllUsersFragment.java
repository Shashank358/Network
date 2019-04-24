package com.ssproduction.shashank.network.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.AllUsersListAdapter;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllUsersFragment extends Fragment {

    private RecyclerView allUsersList;
    private List<Users> mUsers;

    private AllUsersListAdapter usersListAdapter;
    private DatabaseReference mDatabase;


    public AllUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_users, container, false);

        allUsersList = (RecyclerView) view.findViewById(R.id.all_user_list);
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mUsers = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);

        readUsers();

        return view;
    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users users = snapshot.getValue(Users.class);

                    assert users != null;
                    assert firebaseUser != null;

                    if (!users.getUserKey_id().equals(firebaseUser.getUid())){
                        mUsers.add(users);
                    }
                    usersListAdapter = new AllUsersListAdapter(mUsers, getContext());
                    usersListAdapter.notifyDataSetChanged();
                    allUsersList.setAdapter(usersListAdapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
