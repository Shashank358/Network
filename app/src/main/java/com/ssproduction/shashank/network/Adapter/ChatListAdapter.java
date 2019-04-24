package com.ssproduction.shashank.network.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.ChatActivity;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.ChatList;
import com.ssproduction.shashank.network.Utils.Chats;

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context mContext;
    private List<ChatList> mChatList;

    public ChatListAdapter(Context mContext, List<ChatList> mChatList) {
        this.mContext = mContext;
        this.mChatList = mChatList;
    }

    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_chat_list_single_layout, viewGroup, false);

        return new ChatListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListAdapter.ViewHolder viewHolder, int i) {

        final ChatList chatList = mChatList.get(i);

        Picasso picasso = Picasso.get();
        picasso.load(chatList.getImage()).placeholder(R.drawable.avatar).into(viewHolder.userImage);
        picasso.setIndicatorsEnabled(false);
        viewHolder.userName.setText(chatList.getName());
        viewHolder.lastMessage.setText(chatList.getMessage());



        //--------------------------user clicked on-------------------------
        viewHolder.chatUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chatIntent = new Intent(mContext, ChatActivity.class);
                chatIntent.putExtra("user_id", chatList.getKey());
                mContext.startActivity(chatIntent);
            }
        });

        //-------------------unread messages count---------------
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    if (chats.getIsRead().equals("false")){
                       /* viewHolder.unreadCount.setVisibility(View.VISIBLE);
                        int followers = (int) snapshot.getChildrenCount();
                        String count = String.valueOf(followers);
                        viewHolder.unreadCount.setText(count);
                        */
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView userImage;
        public TextView userName, lastMessage, unreadCount;
        public RelativeLayout chatUserLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = (CircleImageView) itemView.findViewById(R.id.chat_list_user_single_image);
            userName = (TextView) itemView.findViewById(R.id.chat_list_user_single_name);
            lastMessage = (TextView) itemView.findViewById(R.id.chat_list_user_single_last_message);
            chatUserLayout = (RelativeLayout) itemView.findViewById(R.id.chat_list_user_rel_layout);
            unreadCount = (TextView) itemView.findViewById(R.id.chat_list_single_unread_message);
        }
    }
}
