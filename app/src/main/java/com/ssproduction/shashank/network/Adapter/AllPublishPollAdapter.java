package com.ssproduction.shashank.network.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Publish;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllPublishPollAdapter extends RecyclerView.Adapter<AllPublishPollAdapter.ViewHolder> {


    private List<Publish> mPublish;
    private Context mContext;

    public AllPublishPollAdapter(List<Publish> mPublish, Context mContext) {
        this.mPublish = mPublish;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AllPublishPollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.timeline_single_poll_layout, viewGroup, false);

        return new AllPublishPollAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllPublishPollAdapter.ViewHolder viewHolder, int i) {


        Publish publish = mPublish.get(i);

            Picasso picasso = Picasso.get();
            picasso.setIndicatorsEnabled(false);
            picasso.load(publish.getFirstPoll()).into(viewHolder.pollFirst);
            picasso.load(publish.getSecondPoll()).into(viewHolder.pollSecond);
            viewHolder.firstText.setText(publish.getFirstText());
            viewHolder.secondText.setText(publish.getSecondText());

    }

    @Override
    public int getItemCount() {
        return mPublish.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView feedMenu, commentImage, likeView, unlikeView;
        private CircleImageView userImage;
        private TextView userName, userPageName, publishText, commentCount, likeCount;
        private RelativeLayout feedInfoRel;
        private RecyclerView commentList;

        private ImageView pollFirst, pollSecond;
        private TextView firstText, secondText;
        private ConstraintLayout pollLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.feed_user_name);
            feedMenu = (ImageView) itemView.findViewById(R.id.feed_user_menu);
            userImage = (CircleImageView) itemView.findViewById(R.id.feed_user_image);
            userName = (TextView) itemView.findViewById(R.id.feed_user_name);
            userPageName = (TextView) itemView.findViewById(R.id.feed_user_pageName);
            publishText = (TextView) itemView.findViewById(R.id.feed_user_texted_post);
            feedInfoRel = (RelativeLayout) itemView.findViewById(R.id.feed_info_relative);
            commentImage = (ImageView) itemView.findViewById(R.id.feed_comment_view);
            commentCount = (TextView) itemView.findViewById(R.id.feed_user_comments_count);
            likeView = (ImageView) itemView.findViewById(R.id.feed_like_color_view);
            unlikeView = (ImageView) itemView.findViewById(R.id.feed_like_view);
            likeCount = (TextView) itemView.findViewById(R.id.feed_posts_likes_count);
            commentList = (RecyclerView) itemView.findViewById(R.id.feed_post_user_comments_list);

            firstText = (TextView) itemView.findViewById(R.id.single_first_poll_text_view);
            secondText = (TextView) itemView.findViewById(R.id.single_second_poll_text_view);
            pollFirst = (ImageView) itemView.findViewById(R.id.single_first_poll_image_view);
            pollSecond = (ImageView) itemView.findViewById(R.id.single_second_poll_image_view);
            pollLayout = (ConstraintLayout) itemView.findViewById(R.id.timeline_poll_layout);

            final LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            commentList.setLayoutManager(manager);
            commentList.setHasFixedSize(true);

            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(commentList);


        }
    }
}
