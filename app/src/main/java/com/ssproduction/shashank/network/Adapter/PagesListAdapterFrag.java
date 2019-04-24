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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.YourPageActivity;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.AllPages;

import java.util.List;

public class PagesListAdapterFrag extends RecyclerView.Adapter<PagesListAdapterFrag.ViewHolder> {

    private List<AllPages> mPages;
    private Context mContext;
    private String currentUser;
    private DatabaseReference followDatabase;

    public PagesListAdapterFrag(List<AllPages> mPages, Context mContext) {
        this.mPages = mPages;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PagesListAdapterFrag.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.my_page_single_layout, viewGroup, false);

        return new PagesListAdapterFrag.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PagesListAdapterFrag.ViewHolder viewHolder, int i) {

        final AllPages pages = mPages.get(i);
        viewHolder.pageTitle.setText(pages.getTitle());
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        followDatabase = FirebaseDatabase.getInstance().getReference().child("Following");

        Picasso picasso = Picasso.get();
        picasso.setIndicatorsEnabled(false);
        picasso.load(pages.getPage_cover()).placeholder(R.drawable.attach_dialog_background)
                .into(viewHolder.pageCover);

        viewHolder.myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String page_pushId = pages.getPush_id();
                Intent pageIntent = new Intent(mContext, YourPageActivity.class);
                pageIntent.putExtra("pushId", page_pushId);
                pageIntent.putExtra("page_click_by", pages.getAdmin_id());
                mContext.startActivity(pageIntent);
            }
        });

        followDatabase.child(pages.getPush_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int followers = (int) dataSnapshot.getChildrenCount();
                String count = String.valueOf(followers);
                if (count.equals("1")){
                    viewHolder.pageFollowers.setText(count + " follower");
                }else if (count.equals("0")){
                   viewHolder.pageFollowers.setText("No followers");
                }
                else {
                    viewHolder.pageFollowers.setText(count + " followers");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView pageTitle, pageFollowers, followPage, joinPage;
        private ImageView pageCover;
        private CardView myPage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pageTitle = (TextView) itemView.findViewById(R.id.my_page_single_name);
            pageFollowers = (TextView) itemView.findViewById(R.id.my_page_followers);
            followPage = (TextView) itemView.findViewById(R.id.my_page_follow_single_text);
            joinPage = (TextView) itemView.findViewById(R.id.my_page_join_single_text);
            pageCover = (ImageView) itemView.findViewById(R.id.my_page_single_image);
            myPage = (CardView) itemView.findViewById(R.id.my_page_card);
        }
    }
}
