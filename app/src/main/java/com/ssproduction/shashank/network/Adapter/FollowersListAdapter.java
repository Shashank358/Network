package com.ssproduction.shashank.network.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Following;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowersListAdapter extends RecyclerView.Adapter<FollowersListAdapter.ViewHolder> {

    private List<Users> mUsers;
    private Context mContext;

    public FollowersListAdapter(List<Users> mUsers, Context mContext) {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FollowersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.followers_images_single_layout, viewGroup, false);

        return new FollowersListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersListAdapter.ViewHolder viewHolder, int i) {

        Users users = mUsers.get(i);
        Picasso picasso = Picasso.get();
        picasso.load(users.getUser_thumbImage()).placeholder(R.drawable.avatar)
                .into(viewHolder.followerImage);
        picasso.setIndicatorsEnabled(false);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView followerImage;
        private TextView followerId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            followerImage = (CircleImageView) itemView.findViewById(R.id.followers_single_profile_image);
            followerId = (TextView) itemView.findViewById(R.id.follower_single_name);
        }
    }
}
