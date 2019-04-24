package com.ssproduction.shashank.network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.ChatListAdapter;
import com.ssproduction.shashank.network.Utils.ChatList;

import java.util.ArrayList;
import java.util.List;

public class SearchChatListActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private EditText searchUser;
    private List<ChatList> mChatList;
    private RecyclerView chatUserList;
    private ChatListAdapter adapter;
    private String currentUser;
    private DatabaseReference chatListDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_chat_list);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.chat_list_search_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        searchUser = (EditText) findViewById(R.id.chat_list_search_edittext);
        chatUserList = (RecyclerView) findViewById(R.id.chat_list_searched_users_list);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatUserList.setLayoutManager(manager);
        chatUserList.setHasFixedSize(true);

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                SearchChatList(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mChatList = new ArrayList<>();
        readChatUsers();

    }

    private void readChatUsers() {

        chatListDatabase = FirebaseDatabase.getInstance().getReference().child("ChatList");

        chatListDatabase.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mChatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    mChatList.add(chatList);
                }
                adapter = new ChatListAdapter(getApplicationContext(), mChatList);
                chatUserList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void SearchChatList(String s) {

        Query query = FirebaseDatabase.getInstance().getReference().child("ChatList").child(currentUser)
                .orderByChild("name").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChatList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatList chatList = snapshot.getValue(ChatList.class);

                    mChatList.add(chatList);
                }

                adapter = new ChatListAdapter(getApplicationContext(), mChatList);
                adapter.notifyDataSetChanged();
                chatUserList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
