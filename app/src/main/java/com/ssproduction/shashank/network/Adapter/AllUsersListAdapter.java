package com.ssproduction.shashank.network.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.ChatActivity;
import com.ssproduction.shashank.network.ProfileActivity;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersListAdapter extends RecyclerView.Adapter<AllUsersListAdapter.ViewHolder> {

    private List<Users> mUsers;
    private Context mContext;
    private FirebaseAuth mAuth;
    private String currentUser;
    private DatabaseReference followDatabase;

    public AllUsersListAdapter(List<Users> mUsers, Context mContext) {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AllUsersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_single_layout, viewGroup, false);
        return new AllUsersListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AllUsersListAdapter.ViewHolder viewHolder, int i) {

        final Users users = mUsers.get(i);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        followDatabase = FirebaseDatabase.getInstance().getReference().child("Following").child("People");
        String userKey = users.getUserKey_id();

        viewHolder.userName.setText(users.getUser_name());
        viewHolder.userId.setText(users.getUser_id());
        if (!users.getPage_name().equals("default")){
            viewHolder.userPageName.setVisibility(View.VISIBLE);
            viewHolder.userPageName.setText(users.getPage_name());
        }else {
            viewHolder.userPageName.setVisibility(View.INVISIBLE);
        }

        Picasso picasso = Picasso.get();
        picasso.load(users.getUser_thumbImage()).placeholder(R.drawable.avatar).into(viewHolder.userImage);

        //------------------my follow/following feature--------------------
        followDatabase.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(users.getUserKey_id())){

                    String follow_type = dataSnapshot.child(users.getUserKey_id()).child("follow_type")
                            .getValue().toString();

                    if (follow_type.equals("following")){
                        viewHolder.current_state = "following";

                        viewHolder.followText.setText("Following");
                        viewHolder.followText.setTextColor(Color.BLACK);
                        viewHolder.followText.setBackground(mContext.getResources().getDrawable(R.drawable.following_page_background));

                    }
                }else {

                    viewHolder.current_state = "not_following";

                    viewHolder.followText.setText("Follow");
                    viewHolder.followText.setTextColor(Color.WHITE);
                    viewHolder.followText.setBackground(mContext.getResources().getDrawable(R.drawable.follow_page_background));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //----------------------other follow following feature----------




        viewHolder.followText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------------not following state-------------------
                if (viewHolder.current_state.equals("not_following")){
                    viewHolder.followText.setText("Following");
                    viewHolder.followText.setTextColor(Color.BLACK);
                    viewHolder.followText.setBackground(mContext.getResources().getDrawable(R.drawable.following_page_background));

                    Map followMap = new HashMap();
                    followMap.put("follow_type", "following");
                    followMap.put("followerId", currentUser);
                    followMap.put("getFollowed_id", users.getUserKey_id());

                    followDatabase.child(currentUser).child(users.getUserKey_id()).updateChildren(followMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){

                            }
                        }
                    });
                }

                //----------------------following state-------------------
                if (viewHolder.current_state.equals("following")){
                    viewHolder.followText.setText("Follow");
                    viewHolder.followText.setTextColor(Color.WHITE);
                    viewHolder.followText.setBackground(mContext.getResources().getDrawable(R.drawable.follow_page_background));


                    followDatabase.child(currentUser).child(users.getUserKey_id()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                            }
                        }
                    });
                }
            }
        });

        //----------------user click profile feature---------------
        viewHolder.userRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                profileIntent.putExtra("user_id", users.getUserKey_id());
                mContext.startActivity(profileIntent);

            }
        });

        viewHolder.userRelative.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final String[] items = {"View Profile", "Send Message", "Block"};
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setTitle("Select Options");
                alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if ("View Profile".equals(items[which])){
                            Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                            profileIntent.putExtra("user_id", users.getUserKey_id());
                            mContext.startActivity(profileIntent);

                        }else if ("Send Message".equals(items[which])){

                            Intent chatIntent = new Intent(mContext, ChatActivity.class);
                            chatIntent.putExtra("user_id", users.getUserKey_id());
                            mContext.startActivity(chatIntent);
                        }
                        else if ("Block".equals(items[which])){

                        }

                    }
                });
                alertDialog.show();


                return true;
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
        private String current_state = "not_following";
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
