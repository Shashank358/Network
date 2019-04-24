package com.ssproduction.shashank.network.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ssproduction.shashank.network.Adapter.AllUsersListAdapter;
import com.ssproduction.shashank.network.Adapter.ChatListAdapter;
import com.ssproduction.shashank.network.Notification.Token;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.SearchChatListActivity;
import com.ssproduction.shashank.network.Utils.ChatList;
import com.ssproduction.shashank.network.Utils.Chats;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private DatabaseReference chatListDatabase;
    private RecyclerView userList;
    private String currentUser;

    private List<ChatList> mChatList;
    private ChatListAdapter chatListAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userList = (RecyclerView) view.findViewById(R.id.chat_frag_users_list);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        userList.setHasFixedSize(true);
        userList.setLayoutManager(manager);
        mChatList = new ArrayList<>();

        chatListDatabase = FirebaseDatabase.getInstance().getReference().child("ChatList");

        chatListDatabase.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mChatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    mChatList.add(chatList);
                }
                chatListAdapter = new ChatListAdapter(getContext(), mChatList);
                userList.setAdapter(chatListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(currentUser).setValue(token1);
    }


}
