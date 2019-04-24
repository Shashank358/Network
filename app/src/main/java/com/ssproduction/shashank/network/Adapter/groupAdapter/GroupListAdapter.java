package com.ssproduction.shashank.network.Adapter.groupAdapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.Adapter.AllPublishAdapter;
import com.ssproduction.shashank.network.GroupChatActivity;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.AddPeople;
import com.ssproduction.shashank.network.Utils.AllGroups;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {


    private List<AllGroups> mGroups;
    private Context mContext;

    private List<AddPeople> mPeople;
    private List<Users> mUsers;

    OnItemClick onItemClick;

    public GroupListAdapter(List<AllGroups> mGroups, Context mContext) {
        this.mGroups = mGroups;
        this.mContext = mContext;
    }

    public void setOnItemClick(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick{

        void getPosition(String admin_id, String group_id);
    }


    @NonNull
    @Override
    public GroupListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_single_chat_list, viewGroup, false);

        return new GroupListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupListAdapter.ViewHolder viewHolder, int i) {

        final AllGroups groups = mGroups.get(i);

        Picasso picasso = Picasso.get();
        picasso.load(groups.getGroup_cover()).placeholder(R.drawable.default_image).into(viewHolder.groupImage);
        picasso.setIndicatorsEnabled(false);

        viewHolder.groupName.setText(groups.getGroup_title());
        //viewHolder.userId.setText(groups.getGroup_tag());

        viewHolder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(mContext, GroupChatActivity.class);
                chatIntent.putExtra("group_id", groups.getGroup_id());
                mContext.startActivity(chatIntent);
            }
        });
        final DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference peopleDatabase = FirebaseDatabase.getInstance().getReference().child("AddPeople");

        mPeople = new ArrayList<>();
        //------------getting list of members-------------------
        peopleDatabase.child(groups.getGroup_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPeople.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AddPeople addPeople = snapshot.getValue(AddPeople.class);
                    mPeople.add(addPeople);
                }

                //-------------------getting list---------------------
                mUsers = new ArrayList<>();
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

                            GroupMembersAd adapter = new GroupMembersAd(mContext, mUsers);
                            viewHolder.memberList.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewHolder.moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String admin_id = groups.getGroup_admin();
                String group_id = groups.getGroup_id();
                onItemClick.getPosition(admin_id, group_id);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView groupName, userId;
        private ImageView groupImage;
        private RelativeLayout mainCard;
        private RecyclerView memberList;
        private ImageView moreView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupName= (TextView) itemView.findViewById(R.id.group_single_title_text);
            groupImage = (ImageView) itemView.findViewById(R.id.group_chat_image);
            mainCard = (RelativeLayout) itemView.findViewById(R.id.group_chat_rel);
            memberList = (RecyclerView) itemView.findViewById(R.id.group_members_list);
            moreView = (ImageView) itemView.findViewById(R.id.group_chat_list_more_view);


            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            memberList.setHasFixedSize(true);
            memberList.setLayoutManager(layoutManager);
        }



    }
}
