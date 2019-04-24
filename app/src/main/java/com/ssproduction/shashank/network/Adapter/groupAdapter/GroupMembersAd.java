package com.ssproduction.shashank.network.Adapter.groupAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMembersAd extends RecyclerView.Adapter<GroupMembersAd.ViewHolder> {

    private Context mContext;
    private List<Users> mUsers;

    public GroupMembersAd(Context mContext, List<Users> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public GroupMembersAd.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.group_members_single_layout, viewGroup, false);

        return new GroupMembersAd.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull GroupMembersAd.ViewHolder viewHolder, int i) {

        Users users = mUsers.get(i);
        Picasso picasso = Picasso.get();
        picasso.setIndicatorsEnabled(false);
        picasso.load(users.getUser_thumbImage()).placeholder(R.drawable.avatar).into(viewHolder.userImage);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = (CircleImageView) itemView.findViewById(R.id.group_member_single_image);

        }
    }
}
