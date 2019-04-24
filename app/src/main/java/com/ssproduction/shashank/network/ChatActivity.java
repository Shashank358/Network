package com.ssproduction.shashank.network;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.Adapter.MessageAdapter;
import com.ssproduction.shashank.network.Fragments.ChatsOptionFragment;
import com.ssproduction.shashank.network.Notification.APIService;
import com.ssproduction.shashank.network.Notification.Client;
import com.ssproduction.shashank.network.Notification.Data;
import com.ssproduction.shashank.network.Notification.MyResponse;
import com.ssproduction.shashank.network.Notification.Sender;
import com.ssproduction.shashank.network.Notification.Token;
import com.ssproduction.shashank.network.Utils.ChatList;
import com.ssproduction.shashank.network.Utils.Chats;
import com.ssproduction.shashank.network.Utils.Users;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.connection.RealConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private String otherUser, currentUSer, chatPushId;
    private CircleImageView userImage;
    private TextView userName;
    private Toolbar mToolbar;
    private EditText textMessage;
    private ImageView sendMessage, sendAudio, emojiExpand, messageOptExpand, messageOptCollapse, cameraImage;
    private RecyclerView chatList;
    private RelativeLayout optionContainer;

    boolean notify = false;
    APIService apiService;


    private DatabaseReference userDatabase, chatDatabase;

    private List<Chats> mChats;
    private MessageAdapter adapter;

    ValueEventListener ReadListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = getIntent().getStringExtra("user_id");
        currentUSer = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        chatDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");

        mToolbar = (Toolbar) findViewById(R.id.chat_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        userImage = (CircleImageView) findViewById(R.id.chat_activity_user_profile_image);
        userName = (TextView) findViewById(R.id.chat_activity_user_name);
        sendMessage = (ImageView) findViewById(R.id.chat_activity_send_message_image);
        sendAudio = (ImageView) findViewById(R.id.chat_activity_mic_image);
        emojiExpand = (ImageView) findViewById(R.id.chat_activity_emoji_image);
        messageOptExpand = (ImageView) findViewById(R.id.chat_activity_message_option_expand_image);
        textMessage = (EditText) findViewById(R.id.chat_activity_type_message_editText);
        cameraImage = (ImageView) findViewById(R.id.chat_activity_camera_image);
        chatList = (RecyclerView) findViewById(R.id.chat_activity_message_list);
        messageOptCollapse = (ImageView) findViewById(R.id.chat_activity_message_option_collapse_image);
        optionContainer = (RelativeLayout) findViewById(R.id.chat_options_container);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        chatList.setLayoutManager(manager);
        chatList.setHasFixedSize(true);

        userDatabase.child(otherUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String user_image = dataSnapshot.child("user_image").getValue().toString();
                String user_id = dataSnapshot.child("user_id").getValue().toString();

                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(user_image).placeholder(R.drawable.avatar).into(userImage);
                userName.setText(user_id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //---------------chat option expand----------------------

        messageOptExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation3 = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.slide_up);

                messageOptCollapse.setVisibility(View.VISIBLE);
                messageOptExpand.setVisibility(View.GONE);
                optionContainer.setVisibility(View.VISIBLE);

                ChatsOptionFragment chatsOptionFragment = new ChatsOptionFragment();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                transaction.setCustomAnimations(R.anim.slide_up, R.anim.no_animation);
                transaction.replace(R.id.chat_options_container, chatsOptionFragment).commit();
                optionContainer.startAnimation(animation3);
            }
        });

        messageOptCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                messageOptCollapse.setVisibility(View.GONE);
                messageOptExpand.setVisibility(View.VISIBLE);
                optionContainer.setVisibility(View.GONE);
            }
        });

        //---------------------sending message feature--------------------
        textMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cameraImage.setVisibility(View.VISIBLE);
                sendAudio.setVisibility(View.VISIBLE);
                sendMessage.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!textMessage.getText().toString().equals("")){
                    cameraImage.setVisibility(View.GONE);
                    sendAudio.setVisibility(View.INVISIBLE);
                    sendMessage.setVisibility(View.VISIBLE);
                    sendAudio.setEnabled(false);
                    sendMessage.setEnabled(true);
                }
                else {
                    cameraImage.setVisibility(View.VISIBLE);
                    sendAudio.setVisibility(View.VISIBLE);
                    sendMessage.setVisibility(View.INVISIBLE);
                    sendAudio.setEnabled(true);
                    sendMessage.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //--------------read message---------------------
        readMessage();

        //----------------is read message---------------------
        isRead(otherUser);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notify = true;

                final String text_message = textMessage.getText().toString();
                textMessage.setText("");

                final DatabaseReference chatListDatabase =  FirebaseDatabase.getInstance().getReference()
                        .child("ChatList");

                chatPushId = chatDatabase.push().getKey();

                DateFormat df = new SimpleDateFormat("h:mm a");
                String sendTime = df.format(Calendar.getInstance().getTime());

                Map chatMap = new HashMap();
                chatMap.put("sender_id", currentUSer);
                chatMap.put("receiver_id", otherUser);
                chatMap.put("message", text_message);
                chatMap.put("isRead", "false");
                chatMap.put("time", sendTime);
                chatMap.put("type", "text");

                DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(currentUSer);
                userDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Users users = dataSnapshot.getValue(Users.class);
                        if (notify){
                            sendNotification(otherUser, users.getUser_id(), text_message);
                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                chatDatabase.child(chatPushId).updateChildren(chatMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null){

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    String sender_image = dataSnapshot.child(currentUSer).child("user_thumbImage").getValue().toString();
                                    String sender_name = dataSnapshot.child(currentUSer).child("user_id").getValue().toString();
                                    String receiver_image = dataSnapshot.child(otherUser).child("user_thumbImage").getValue().toString();
                                    String receiver_name = dataSnapshot.child(otherUser).child("user_id").getValue().toString();

                                    Map senderMap = new HashMap();
                                    senderMap.put("image", receiver_image);
                                    senderMap.put("name", receiver_name);
                                    senderMap.put("message", text_message);
                                    senderMap.put("type", "sender");
                                    senderMap.put("key", otherUser);

                                    Map receiverMap = new HashMap();
                                    receiverMap.put("image", sender_image);
                                    receiverMap.put("name", sender_name);
                                    receiverMap.put("message", text_message);
                                    receiverMap.put("type", "receiver");
                                    receiverMap.put("key", currentUSer);

                                    final Map listMap1 = new HashMap();
                                    listMap1.put(currentUSer + "/" + otherUser + "/", senderMap);
                                    listMap1.put(otherUser + "/" + currentUSer + "/" , receiverMap);


                                    chatListDatabase.updateChildren(listMap1, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                });


            }
        });

    }

    private void sendNotification(String otherUser, final String user_id, final String text_message) {

        final DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child("Tokens");
        Query query = tokens.orderByKey().equalTo(otherUser);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(currentUSer, R.mipmap.ic_launcher, user_id+": "+text_message, "New Message",
                            user_id);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isRead(final String otherUser) {

        //----------------------is read feature-----------------------

        ReadListener = chatDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    if (chats.getReceiver_id().equals(currentUSer) && chats.getSender_id().equals(otherUser)){
                        Map readMap = new HashMap();
                        readMap.put("isRead", "true");

                        snapshot.getRef().updateChildren(readMap);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void readMessage() {
        mChats = new ArrayList<>();

        chatDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChats.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    if ((chats.getSender_id().equals(currentUSer) && chats.getReceiver_id().equals(otherUser))
                            || (chats.getReceiver_id().equals(currentUSer) && chats.getSender_id().equals(otherUser)))
                    {
                        mChats.add(chats);
                    }
                }
                adapter = new MessageAdapter(getApplicationContext(), mChats);
                adapter.notifyDataSetChanged();
                chatList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.chat_view_profile_item){
            Intent profileIntent = new Intent(ChatActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        }
        else if (item.getItemId() == R.id.chat_voice_call_item){
            Toast.makeText(this, "Voice Call", Toast.LENGTH_SHORT).show();
        }
        else if (item.getItemId() == R.id.chat_video_call_item){
            Toast.makeText(this, "video Call", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatDatabase.removeEventListener(ReadListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
