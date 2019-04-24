package com.ssproduction.shashank.network.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPeopleAdapter extends RecyclerView.Adapter<AddPeopleAdapter.ViewHolder> {


    private List<Users> mUsers;
    private Context mContext;

    OnItemClick onItemClick;


    public AddPeopleAdapter(List<Users> mUsers, Context mContext) {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    public void setOnItemClick(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick{

        void getPosition(String id);

    }

    @NonNull
    @Override
    public AddPeopleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.add_people_single_user_layout, viewGroup, false);

        return new AddPeopleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddPeopleAdapter.ViewHolder viewHolder, int i) {

        final Users users = mUsers.get(i);

        Picasso picasso = Picasso.get();
        picasso.load(users.getUser_thumbImage()).placeholder(R.drawable.avatar).into(viewHolder.userImage);
        picasso.setIndicatorsEnabled(false);

        viewHolder.userName.setText(users.getUser_name());
        viewHolder.userId.setText(users.getUser_id());

        final String click_id = users.getUserKey_id();

        viewHolder.selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.getPosition(click_id);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, userId;
        private CircleImageView userImage;
        private RadioButton selectBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.add_people_user_single_name);
            userId = (TextView) itemView.findViewById(R.id.add_people_user_single_id);
            userImage = (CircleImageView) itemView.findViewById(R.id.add_people_user_profile);
            selectBtn = (RadioButton) itemView.findViewById(R.id.add_people_radio_btn);
        }



    }
}
