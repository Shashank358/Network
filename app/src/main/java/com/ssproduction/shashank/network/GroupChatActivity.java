package com.ssproduction.shashank.network;

import android.content.Intent;
import android.service.autofill.Dataset;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.Adapter.AddPeopleAdapter;
import com.ssproduction.shashank.network.Adapter.groupAdapter.GMessageAdapter;
import com.ssproduction.shashank.network.Adapter.groupAdapter.GroupMembersAd;
import com.ssproduction.shashank.network.Utils.AddPeople;
import com.ssproduction.shashank.network.Utils.GroupChats;
import com.ssproduction.shashank.network.Utils.Users;

import java.security.acl.Group;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private String group_id;

    private DatabaseReference addDatabase, groupDatabase, mDatabase;
    private String currentUser;

    private List<AddPeople> mPeople;
    private GroupMembersAd adapter;

    private List<Users> mUsers;
    private List<GroupChats> mChats;
    private GMessageAdapter mAdapter;

    private RecyclerView userList, chatList;
    private ImageView groupCover, sendMessage;
    private CircleImageView userImage;
    private TextView groupTitle;
    private EditText messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        group_id = getIntent().getStringExtra("group_id");

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        addDatabase = FirebaseDatabase.getInstance().getReference().child("AddPeople");
        groupDatabase = FirebaseDatabase.getInstance().getReference().child("AllGroups");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        userList = (RecyclerView) findViewById(R.id.group_chat_users_list);
        groupCover = (ImageView) findViewById(R.id.group_chat_activity_group_cover);
        groupTitle = (TextView) findViewById(R.id.group_chat_activity_group_title);
        userImage = (CircleImageView) findViewById(R.id.group_chat_activity_user_image);
        messageText = (EditText) findViewById(R.id.group_chat_activity_message_edittext);
        sendMessage = (ImageView) findViewById(R.id.group_chat_activity_send_message_view);
        chatList = (RecyclerView) findViewById(R.id.all_group_chats_list);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatList.setHasFixedSize(true);
        manager.setStackFromEnd(true);
        chatList.setLayoutManager(manager);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        userList.setHasFixedSize(true);
        userList.setLayoutManager(layoutManager);

        mToolbar = (Toolbar) findViewById(R.id.group_chat_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //-----------------reading messages----------------
        readMessages();
        mChats = new ArrayList<>();

        //---------------getting user info--------------------------
        mDatabase.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String user_profile = dataSnapshot.child("user_thumbImage").getValue().toString();
                final String user_id = dataSnapshot.child("user_id").getValue().toString();

                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(user_profile).placeholder(R.drawable.avatar).into(userImage);

                sendMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message_text = messageText.getText().toString();
                        sendingMessage(message_text, user_profile, user_id);
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //--------------------getting group info-----------------//
        groupDatabase.child(group_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String group_title = dataSnapshot.child("group_title").getValue().toString();
                String group_cover = dataSnapshot.child("group_cover").getValue().toString();

                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(group_cover).placeholder(R.drawable.default_image).into(groupCover);
                groupTitle.setText(group_title);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPeople = new ArrayList<>();

        addDatabase.child(group_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPeople.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AddPeople addPeople = snapshot.getValue(AddPeople.class);
                    mPeople.add(addPeople);
                }

                readMembers();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void readMessages() {

        DatabaseReference messageDatabase = FirebaseDatabase.getInstance().getReference().child("GroupChats");
        messageDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChats.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupChats chats = snapshot.getValue(GroupChats.class);
                    mChats.add(chats);
                }
                mAdapter = new GMessageAdapter(mChats, getApplicationContext());
                mAdapter.notifyDataSetChanged();
                chatList.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendingMessage(String message_text, String user_profile, String user_id) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("GroupChats");
        String chat_push = reference.push().getKey();
        messageText.setText("");

        DateFormat df = new SimpleDateFormat("h:mm a");
        String sendTime = df.format(Calendar.getInstance().getTime());

        Map messageMap = new HashMap();
        messageMap.put("isRead", "false");
        messageMap.put("message", message_text);
        messageMap.put("sender_id", currentUser);
        messageMap.put("time", sendTime);
        messageMap.put("type", "text");
        messageMap.put("receiver_id", "default");
        messageMap.put("sender_profile", user_profile);
        messageMap.put("sender_userId", user_id);

        reference.child(chat_push).updateChildren(messageMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });

    }

    private void readMembers() {
        mUsers = new ArrayList<>();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users users = snapshot.getValue(Users.class);
                    for (AddPeople addPeople : mPeople){
                        if (users.getUserKey_id().equals(addPeople.getUser_id())){
                            mUsers.add(users);

                        }
                    }
                    adapter = new GroupMembersAd(getApplicationContext(), mUsers);
                    userList.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_chat_items, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.group_chat_add_members_item) {
            Intent addIntent = new Intent(GroupChatActivity.this, AddPeopleActivity.class);
            addIntent.putExtra("group_id", group_id);
            startActivity(addIntent);
        }
        else if (item.getItemId() == R.id.group_chat_edit_item){
            Intent editIntent = new Intent(GroupChatActivity.this, EditGroupActivity.class);
            editIntent.putExtra("group_id", group_id);
            startActivity(editIntent);
        }

        return true;
    }
}
