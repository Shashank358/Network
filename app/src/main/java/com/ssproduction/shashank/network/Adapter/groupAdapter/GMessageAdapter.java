package com.ssproduction.shashank.network.Adapter.groupAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.Adapter.MessageAdapter;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Chats;
import com.ssproduction.shashank.network.Utils.GroupChats;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GMessageAdapter extends RecyclerView.Adapter<GMessageAdapter.ViewHolder> {

    private String mCurrentUser;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


    private List<GroupChats> mChats;
    private Context mContext;

    public GMessageAdapter(List<GroupChats> mChats, Context mContext) {
        this.mChats = mChats;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public GMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_single_right_message_layout, viewGroup, false);
            return new GMessageAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_single_left_message_layout, viewGroup, false);
            return new GMessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GMessageAdapter.ViewHolder viewHolder, int i) {

        GroupChats chats = mChats.get(i);
        Picasso picasso = Picasso.get();
        picasso.setIndicatorsEnabled(false);
        picasso.load(chats.getSender_profile()).placeholder(R.drawable.avatar).into(viewHolder.profile);

        viewHolder.message.setText(chats.getMessage());
        viewHolder.userId.setText(chats.getSender_userId());

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profile;
        private TextView message, userId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = (CircleImageView) itemView.findViewById(R.id.group_chat_message_user_profile);
            userId = (TextView) itemView.findViewById(R.id.group_chat_message_user_id);
            message = (TextView) itemView.findViewById(R.id.group_chat_message_text);
        }
    }

    @Override
    public int getItemViewType(int position) {

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (mChats.get(position).getSender_id().equals(mCurrentUser)){

            return MSG_TYPE_RIGHT;
        }
        else {

            return MSG_TYPE_LEFT;
        }

    }
}
