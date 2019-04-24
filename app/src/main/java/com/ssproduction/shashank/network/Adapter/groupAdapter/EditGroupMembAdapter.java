package com.ssproduction.shashank.network.Adapter.groupAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditGroupMembAdapter extends RecyclerView.Adapter<EditGroupMembAdapter.ViewHolder> {

    private List<Users> mUsers;
    private Context mContext;

    public EditGroupMembAdapter(List<Users> mUsers, Context mContext) {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public EditGroupMembAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_single_layout, viewGroup, false);

        return new  EditGroupMembAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final EditGroupMembAdapter.ViewHolder viewHolder, int i) {

        final Users users = mUsers.get(i);

        viewHolder.userName.setText(users.getUser_name());
        viewHolder.userId.setText(users.getUser_id());
        viewHolder.followText.setVisibility(View.GONE);
        viewHolder.followText.setEnabled(false);

        if (!users.getPage_name().equals("default")){
            viewHolder.userPageName.setVisibility(View.VISIBLE);
            viewHolder.userPageName.setText(users.getPage_name());
        }else {
            viewHolder.userPageName.setVisibility(View.INVISIBLE);
        }

        final Picasso picasso = Picasso.get();
        picasso.setIndicatorsEnabled(false);
        picasso.load(users.getUser_thumbImage()).placeholder(R.drawable.avatar).networkPolicy(NetworkPolicy.OFFLINE)
                .into(viewHolder.userImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        picasso.load(users.getUser_thumbImage()).placeholder(R.drawable.avatar)
                                .into(viewHolder.userImage);
                    }
                });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, userPageName, userId, followText;
        private CircleImageView userImage;
        private RelativeLayout userRelative;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.user_single_name);
            userId = (TextView) itemView.findViewById(R.id.user_single_user_id);
            userPageName = (TextView) itemView.findViewById(R.id.user_single_page_name);
            userImage = (CircleImageView) itemView.findViewById(R.id.user_single_image);
            followText = (TextView) itemView.findViewById(R.id.user_single_follow_text);
            userRelative = (RelativeLayout) itemView.findViewById(R.id.user_single_main_relative);

        }
    }
}
