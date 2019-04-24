package com.ssproduction.shashank.network;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.AddPeopleAdapter;
import com.ssproduction.shashank.network.Adapter.AllUsersListAdapter;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPeopleActivity extends AppCompatActivity {

    private RecyclerView addList;
    private DatabaseReference userDatanase, addDatabase;
    private FirebaseUser firebaseUser;
    private String group_id;

    private LinearLayoutManager layoutManager;
    private Toolbar mToolbar;
    private TextView addText;

    List<Users> mUsers;
    private AddPeopleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);

        group_id = getIntent().getStringExtra("group_id");

        mToolbar = (Toolbar) findViewById(R.id.add_people_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addText = (TextView) findViewById(R.id.add_people_done_text);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userDatanase = FirebaseDatabase.getInstance().getReference().child("Users");
        addDatabase = FirebaseDatabase.getInstance().getReference().child("AddPeople");

        addList = (RecyclerView) findViewById(R.id.add_people_in_group_chat_list);

        layoutManager = new LinearLayoutManager(this);
        addList.setHasFixedSize(true);
        addList.setLayoutManager(layoutManager);

        mUsers = new ArrayList<>();

        userDatanase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users users  = snapshot.getValue(Users.class);

                    assert users != null;
                    assert firebaseUser != null;

                    if (!users.getUserKey_id().equals(firebaseUser.getUid())){
                        mUsers.add(users);

                    }
                }
                adapter = new AddPeopleAdapter(mUsers, getApplicationContext());
                adapter.notifyDataSetChanged();
                addList.setAdapter(adapter);

                adapter.setOnItemClick(new AddPeopleAdapter.OnItemClick() {
                    @Override
                    public void getPosition(final String id) {

                        Map addMap = new HashMap();
                        addMap.put("user_id", id);

                        addDatabase.child(group_id).child(id).updateChildren(addMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError == null){
                                    addText.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chatIntent = new Intent(AddPeopleActivity.this, GroupChatActivity.class);
                chatIntent.putExtra("group_id", group_id);
                startActivity(chatIntent);
            }
        });

    }
}
