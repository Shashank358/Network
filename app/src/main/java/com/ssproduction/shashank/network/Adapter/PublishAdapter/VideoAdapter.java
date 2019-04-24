package com.ssproduction.shashank.network.Adapter.PublishAdapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import com.ssproduction.shashank.network.Utils.PublishVideo;
import com.ssproduction.shashank.network.VideoClipActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.SimpleExoPlayerViewHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private List<PublishVideo> mVideo;
    private Context mContext;

    public VideoAdapter(List<PublishVideo> mVideo, Context mContext) {
        this.mVideo = mVideo;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.timeline_single_post_layout, viewGroup, false);

        return new VideoAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder viewHolder, int i) {

        PublishVideo video =  mVideo.get(i);
        Picasso picasso = Picasso.get();
        picasso.setIndicatorsEnabled(false);

        viewHolder.playerView.setVisibility(View.VISIBLE);
        viewHolder.publishImage.setVisibility(View.GONE);
        viewHolder.pollLayout.setVisibility(View.GONE);
        viewHolder.questionRel.setVisibility(View.GONE);

        viewHolder.bind(Uri.parse(video.getPublish()));

        viewHolder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent clipIntent = new Intent(mContext, VideoClipActivity.class);
                mContext.startActivity(clipIntent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mVideo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ToroPlayer {

        private ImageView publishImage, feedMenu, commentImage, likeView, unlikeView;
        private CircleImageView userImage;
        private SimpleExoPlayerView playerView;

        private TextView userName, userPageName, publishText, commentCount, likeCount;
        private RelativeLayout feedInfoRel;
        private RecyclerView commentList;

        private ImageView pollFirst, pollSecond;
        private TextView firstText, secondText;
        private ConstraintLayout pollLayout;
        private RelativeLayout mainCard;
        private LinearLayoutManager layoutManager;

        private TextView askQuestion;
        private ImageView questionBack;
        private RelativeLayout questionRel;
        Uri mediaUri;
        SimpleExoPlayerViewHelper helper;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mainCard = (RelativeLayout) itemView.findViewById(R.id.timeline_card);
            publishImage = (ImageView) itemView.findViewById(R.id.feed_user_single_post);
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

            questionRel = (RelativeLayout) itemView.findViewById(R.id.timeline_asked_question_relative);
            askQuestion = (TextView) itemView.findViewById(R.id.timeline_asked_question_text);

            firstText = (TextView) itemView.findViewById(R.id.single_first_poll_text_view);
            secondText = (TextView) itemView.findViewById(R.id.single_second_poll_text_view);
            pollFirst = (ImageView) itemView.findViewById(R.id.single_first_poll_image_view);
            pollSecond = (ImageView) itemView.findViewById(R.id.single_second_poll_image_view);
            pollLayout = (ConstraintLayout) itemView.findViewById(R.id.timeline_poll_layout);

            commentList.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            commentList.setLayoutManager(layoutManager);

            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(commentList);

            playerView = (SimpleExoPlayerView) itemView.findViewById(R.id.feed_user_single_video);

        }

        @NonNull
        @Override
        public View getPlayerView() {
            return playerView;
        }

        @NonNull
        @Override
        public PlaybackInfo getCurrentPlaybackInfo() {
            return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
        }

        @Override
        public void initialize(@NonNull Container container, @Nullable PlaybackInfo playbackInfo) {

            if (helper == null) {
                helper = new SimpleExoPlayerViewHelper(container, this, mediaUri);
            }
            helper.initialize(playbackInfo);
        }

        @Override
        public void play() {
            if (helper != null) helper.play();
        }

        @Override
        public void pause() {
            if (helper != null) helper.pause();

        }

        @Override
        public boolean isPlaying() {

            return helper != null && helper.isPlaying();
        }

        @Override
        public void release() {
            if (helper != null) {
                helper.release();
                helper = null;
            }
        }

        @Override
        public boolean wantsToPlay() {
            return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.85;
        }

        @Override
        public int getPlayerOrder() {
            return getAdapterPosition();
        }

        void bind(Uri media) {
            this.mediaUri = media;
        }

    }

}
