package com.ssproduction.shashank.network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.AllUsersListAdapter;
import com.ssproduction.shashank.network.Adapter.ChatListAdapter;
import com.ssproduction.shashank.network.Utils.ChatList;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.ArrayList;
import java.util.List;

public class SearchUsersActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private EditText searchUser;
    private List<Users> mUsers;
    private RecyclerView allUsersList;
    private AllUsersListAdapter adapter;
    private String currentUser;
    private DatabaseReference userDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (Toolbar) findViewById(R.id.all_users_search_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchUser = (EditText) findViewById(R.id.all_users_search_edittext);
        allUsersList = (RecyclerView) findViewById(R.id.all_users_searched_users_list);
        LinearLayoutManager manager = new LinearLayoutManager(this) ;
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(manager);

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                SearchAllUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mUsers = new ArrayList<>();
        readUsers();

    }

    private void readUsers() {

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users users = snapshot.getValue(Users.class);
                    mUsers.add(users);
                }
                adapter = new AllUsersListAdapter(mUsers, getApplicationContext());
                adapter.notifyDataSetChanged();
                allUsersList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SearchAllUsers(String s) {

        Query query = userDatabase.orderByChild("search").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users users = snapshot.getValue(Users.class);
                    mUsers.add(users);
                }
                adapter = new AllUsersListAdapter(mUsers, getApplicationContext());
                adapter.notifyDataSetChanged();
                allUsersList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
