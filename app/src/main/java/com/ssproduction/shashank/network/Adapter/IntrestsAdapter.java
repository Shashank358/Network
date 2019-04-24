package com.ssproduction.shashank.network.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.AllPages;
import com.ssproduction.shashank.network.YourPageActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntrestsAdapter extends RecyclerView.Adapter<IntrestsAdapter.ViewHolder> {

    private Context mContext;
    private List<AllPages> mPages;

    private String currentUser;
    private DatabaseReference followDatabase;

    public IntrestsAdapter(Context mContext, List<AllPages> mPages) {
        this.mContext = mContext;
        this.mPages = mPages;
    }

    @NonNull
    @Override
    public IntrestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.page_single_layout, viewGroup, false);

        return new IntrestsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final IntrestsAdapter.ViewHolder viewHolder, final int i) {

        final AllPages allPages = mPages.get(i);
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        followDatabase = FirebaseDatabase.getInstance().getReference().child("Following");
        followDatabase.keepSynced(true);

        viewHolder.pageName.setText(allPages.getTitle());
        viewHolder.followerCount.setText(allPages.getFollowers_count());
        Picasso picasso = Picasso.get();
        picasso.load(allPages.getPage_cover()).placeholder(R.drawable.publish_prev_menu_back)
        .into(viewHolder.pageCover);

        //--------------------Follow/Following Feature---------------------//
        followDatabase.child(allPages.getPush_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int followers = (int) dataSnapshot.getChildrenCount();
                String count = String.valueOf(followers);
                if (count.equals("1")){
                    viewHolder.followerCount.setText(count + " follower");
                }else if (count.equals("0")){
                    viewHolder.followerCount.setText("No followers");
                }
                else {
                    viewHolder.followerCount.setText(count + " followers");
                }

                if (dataSnapshot.hasChild(currentUser)){
                    String followType = dataSnapshot.child(currentUser).child("follow_type").getValue().toString();

                    if (followType.equals("following")){

                        viewHolder.followPage.setText("Following");
                        viewHolder.current_state.equals("following");

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewHolder.followPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //--------------not following state-------------
                if (viewHolder.current_state.equals("not_following")){
                    viewHolder.followPage.setText("Following");

                    Map followMap = new HashMap();
                    followMap.put("follow_type", "following");
                    followMap.put("follower_id", currentUser);
                    followMap.put("page_id", allPages.getPush_id());

                    followDatabase.child(allPages.getPush_id()).child(currentUser).updateChildren(followMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful()){
                                viewHolder.current_state = "following";
                                viewHolder.followPage.setText("Following");
                            }
                        }
                    });
                }

                //------------------following state-------------------
                if (viewHolder.current_state.equals("following")){
                    viewHolder.followPage.setText("Follow");

                    followDatabase.child(allPages.getPush_id()).child(currentUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                viewHolder.current_state = "not_following";
                                viewHolder.followPage.setText("Follow");
                            }
                        }
                    });

                }
            }
        });


        //--------------------page selection --------------
        viewHolder.pageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pageIntent = new Intent(mContext, YourPageActivity.class);
                pageIntent.putExtra("pushId", allPages.getPush_id());
                pageIntent.putExtra("page_click_by", allPages.getAdmin_id());
                mContext.startActivity(pageIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView pageCover;
        private TextView pageName, followerCount, followPage, joinPage;
        private String current_state = "not_following";
        private CardView pageCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pageCover = (ImageView) itemView.findViewById(R.id.other_page_single_image);
            pageName = (TextView) itemView.findViewById(R.id.other_page_single_name);
            followerCount = (TextView) itemView.findViewById(R.id.other_page_followers_count);
            followPage = (TextView) itemView.findViewById(R.id.other_page_follow_single_text);
            joinPage = (TextView) itemView.findViewById(R.id.other_page_join_single_text);
            pageCard = (CardView) itemView.findViewById(R.id.all_page_single_card);
        }
    }
}
