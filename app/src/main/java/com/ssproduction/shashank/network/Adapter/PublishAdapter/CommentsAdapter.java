package com.ssproduction.shashank.network.Adapter.PublishAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Comments;
import com.ssproduction.shashank.network.Utils.ReplyComment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comments> mComments;
    private Context mContext;
    private String currentUser;

    OnItemClick onItemClick;



    public CommentsAdapter(List<Comments> mComments, Context mContext) {
        this.mComments = mComments;
        this.mContext = mContext;
    }

    public void setOnItemClick(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick{

        void getPosition(String commentId);

    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_comment_single_layout, viewGroup, false);

        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsAdapter.ViewHolder viewHolder, int i) {

        final Comments comments = mComments.get(i);
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        viewHolder.commenterName.setText(comments.getCommenter_id());
        viewHolder.comment.setText(comments.getComment());

        Picasso picasso = Picasso.get();
        picasso.load(comments.getCommenter_image()).into(viewHolder.commenterImage);

        final DatabaseReference likeDatabase = FirebaseDatabase.getInstance().getReference()
                .child("CommentLikes");

        //-----------------reply comment feature---------------------
        viewHolder.unlikeCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.getPosition(comments.getComment_pushId());

                Animation expandIn = AnimationUtils.loadAnimation(mContext, R.anim.expand_in);
                viewHolder.unlikeCmt.setVisibility(View.GONE);
                viewHolder.likeCmt.setVisibility(View.VISIBLE);
                viewHolder.likeCmt.startAnimation(expandIn);

                Map likeMap = new HashMap();
                likeMap.put(currentUser, "like");


                likeDatabase.child(comments.getComment_pushId()).child(currentUser).updateChildren(likeMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    }
                });

            }
        });

        viewHolder.likeCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.getPosition(comments.getComment_pushId());

                Animation expandIn = AnimationUtils.loadAnimation(mContext, R.anim.expand_in);
                viewHolder.likeCmt.setVisibility(View.GONE);
                viewHolder.unlikeCmt.setVisibility(View.VISIBLE);
                viewHolder.unlikeCmt.startAnimation(expandIn);

                Map likeMap = new HashMap();
                likeMap.put(currentUser, null);

                DatabaseReference likeDatabase = FirebaseDatabase.getInstance().getReference()
                        .child("CommentLikes");
                likeDatabase.child(comments.getComment_pushId()).child(currentUser).updateChildren(likeMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    }
                });
            }
        });

        likeDatabase.child(comments.getComment_pushId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int followers = (int) dataSnapshot.getChildrenCount();
                String count = String.valueOf(followers);
                viewHolder.likeCount.setText(count);

                if (dataSnapshot.hasChild(currentUser)){
                    viewHolder.likeCmt.setVisibility(View.VISIBLE);
                    viewHolder.unlikeCmt.setVisibility(View.GONE);
                }else {
                    viewHolder.likeCmt.setVisibility(View.GONE);
                    viewHolder.unlikeCmt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView commenterName, comment, sendComment, likeCount;
        private CircleImageView commenterImage;
        private ImageView unlikeCmt, likeCmt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            comment = (TextView) itemView.findViewById(R.id.commenter_user_comment);
            commenterName = (TextView) itemView.findViewById(R.id.commenter_user_name);
            commenterImage = (CircleImageView) itemView.findViewById(R.id.commenter_profile_image);
            unlikeCmt = (ImageView) itemView.findViewById(R.id.commenter_user_comment_unlike_view);
            likeCmt = (ImageView) itemView.findViewById(R.id.commenter_user_comment_like_view);
            sendComment = (TextView) itemView.findViewById(R.id.comment_activity_send_comment_view);
            likeCount = (TextView) itemView.findViewById(R.id.commenter_user_comment_likes_count);
        }
    }
}
