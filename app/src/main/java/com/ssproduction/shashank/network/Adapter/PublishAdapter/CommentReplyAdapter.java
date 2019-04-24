package com.ssproduction.shashank.network.Adapter.PublishAdapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Comments;
import com.ssproduction.shashank.network.Utils.ReplyComment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentReplyAdapter extends RecyclerView.Adapter<CommentReplyAdapter.ViewHolder> {

    private List<ReplyComment> mReply;
    private Context mContext;

    public CommentReplyAdapter(List<ReplyComment> mReply, Context mContext) {
        this.mReply = mReply;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CommentReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view = LayoutInflater.from(mContext).inflate(R.layout.user_comment_single_layout, viewGroup, false);

       return new CommentReplyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentReplyAdapter.ViewHolder viewHolder, int i) {

        ReplyComment replyComment = mReply.get(i);
        viewHolder.list.setVisibility(View.GONE);
        viewHolder.replyView.setVisibility(View.GONE);

        Picasso picasso = Picasso.get();
        picasso.load(replyComment.getReplier_image()).into(viewHolder.replyerImage);
        viewHolder.replyerName.setText(replyComment.getReplier_id());
        viewHolder.replyerComment.setText(replyComment.getReply());


    }

    @Override
    public int getItemCount() {
        return mReply.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView replyerImage;
        private ImageView replyView;
        private TextView replyerName, replyerComment;
        private RecyclerView list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            replyerComment = (TextView) itemView.findViewById(R.id.commenter_user_comment);
            replyerName = (TextView) itemView.findViewById(R.id.commenter_user_name);
            replyerImage = (CircleImageView) itemView.findViewById(R.id.commenter_profile_image);
            replyView = (ImageView) itemView.findViewById(R.id.commenter_user_comment_unlike_view);

        }
    }
}
