package com.ssproduction.shashank.network.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Chats;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private String mCurrentUser;

    private Context mContext;
    private List<Chats> mChats;

    public MessageAdapter(Context mContext, List<Chats> mChats) {
        this.mContext = mContext;
        this.mChats = mChats;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.right_message_layout, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.left_message_layout, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder viewHolder, int i) {

        final Chats chats = mChats.get(i);

        if (chats.getType().equals("text")){
            viewHolder.message.setText(chats.getMessage());
            setAnimation(viewHolder.itemView, i);

            if (chats.getIsRead().equals("true")){
                viewHolder.readDelText.setText("R");
            }
            else {
                viewHolder.readDelText.setText("D");
            }

            viewHolder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.timeAgo.getVisibility() == View.GONE){
                        viewHolder.timeAgo.animate().alpha(1.0f);
                        viewHolder.timeAgo.setVisibility(View.VISIBLE);
                        viewHolder.timeAgo.setText(chats.getTime());
                    }else {
                        viewHolder.timeAgo.animate().alpha(0.0f);
                        viewHolder.timeAgo.setVisibility(View.GONE);
                    }

                }
            });
        }

        else if (chats.getType().equals("image")){
        }
    }

    protected int mLastPosition = -1;

    protected void setAnimation(View viewToAnimate, int position) {
        if (position > mLastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(200);//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            mLastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView message, timeAgo, readDelText;
        private ImageView messageImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message = (TextView) itemView.findViewById(R.id.text_message);
            timeAgo = (TextView) itemView.findViewById(R.id.time_ago);
            readDelText = (TextView) itemView.findViewById(R.id.read_or_delivered_text);
            messageImage = (ImageView) itemView.findViewById(R.id.message_image);
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
