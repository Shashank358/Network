package com.ssproduction.shashank.network.Adapter;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.Adapter.PublishAdapter.CommentsAdapter;
import com.ssproduction.shashank.network.FeedCommentActivity;
import com.ssproduction.shashank.network.PhotoViewActivity;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Comments;
import com.ssproduction.shashank.network.Utils.Publish;
import com.ssproduction.shashank.network.VideoClipActivity;
import com.ssproduction.shashank.network.YourPageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.SimpleExoPlayerViewHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

public class AllPublishAdapter extends RecyclerView.Adapter<AllPublishAdapter.ViewHolder> {

    private static Context mContext;
    private List<Publish> mPublish;

    private DatabaseReference publishDatabase, commentDatabase, likeDatabase;
    OnItemClick onItemClick;
    private String currentUser;

    private List<Comments> mComments;
    private CommentsAdapter adapter;

    private final static int FADE_DURATION = 500; //FADE_DURATION in milliseconds



    public AllPublishAdapter(Context context, List<Publish> mPublish) {
        this.mContext = context;
        this.mPublish = mPublish;
    }

    public void setOnItemClick(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick{

        void getPosition(String id);

    }

    @NonNull
    @Override
    public AllPublishAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.timeline_single_post_layout, viewGroup, false);

        return new AllPublishAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final AllPublishAdapter.ViewHolder viewHolder, final int i) {

        final Publish publish = mPublish.get(i);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference pageDatabase = FirebaseDatabase.getInstance().getReference().child("AllPages");
        commentDatabase = FirebaseDatabase.getInstance().getReference().child("Comments");
        likeDatabase = FirebaseDatabase.getInstance().getReference().child("Likes");
        commentDatabase.keepSynced(true);
        likeDatabase.keepSynced(true);
        pageDatabase.keepSynced(true);
        mDatabase.keepSynced(true);
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mComments = new ArrayList<>();


        commentDatabase.child(publish.getPublish_pushId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mComments.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comments comments = snapshot.getValue(Comments.class);
                    mComments.add(comments);
                }

                adapter = new CommentsAdapter(mComments, mContext);
                adapter.notifyDataSetChanged();
                viewHolder.commentList.setAdapter(adapter);

                /*final int duration = 20;
                final int pixelsToMove = 30;
                final Handler mHandler = new Handler(Looper.getMainLooper());
                final Runnable SCROLLING_RUNNABLE = new Runnable() {

                    @Override
                    public void run() {
                        viewHolder.commentList.smoothScrollBy(pixelsToMove, 0);
                        mHandler.postDelayed(this, duration);
                    }
                };


                viewHolder.commentList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                        if(lastItem == layoutManager.getItemCount()-1){
                            mHandler.removeCallbacks(SCROLLING_RUNNABLE);
                            Handler postHandler = new Handler();
                            postHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.commentList.setAdapter(null);
                                    viewHolder.commentList.setAdapter(adapter);
                                    mHandler.postDelayed(SCROLLING_RUNNABLE, 2000);
                                }
                            }, 2000);
                        }
                    }
                });
                mHandler.postDelayed(SCROLLING_RUNNABLE, 2000);
                */

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ViewHolder.AnotherClass anotherClass = new ViewHolder.AnotherClass();
        anotherClass.update(mPublish, i);

        if (publish.getPublish_type().equals("Photo")){
            viewHolder.publishImage.setVisibility(View.VISIBLE);
            viewHolder.playerView.setVisibility(View.GONE);
            viewHolder.pollLayout.setVisibility(View.GONE);
            viewHolder.questionRel.setVisibility(View.GONE);

            final Picasso picasso = Picasso.get();
            picasso.setIndicatorsEnabled(false);
            picasso.load(publish.getPublish_thumb()).placeholder(R.drawable.publish_prev_menu_back)
                    .networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.publishImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    picasso.load(publish.getPublish_thumb()).placeholder(R.drawable.publish_prev_menu_back)
                            .into(viewHolder.publishImage);
                }
            });

            if (!publish.getText_info().equals("")){
                viewHolder.feedInfoRel.setVisibility(View.VISIBLE);
                viewHolder.publishText.setText(publish.getText_info());

            }
            else{
                viewHolder.feedInfoRel.setVisibility(View.GONE);

            }

            //----------------photo view-----------------
            viewHolder.publishImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String image = publish.getPublish_thumb();
                    Intent prevIntent = new Intent(mContext, PhotoViewActivity.class);
                    prevIntent.putExtra("image", image);
                    mContext.startActivity(prevIntent);
                }
            });

            //---------------------comments count----------------
            commentDatabase.child(publish.getPublish_pushId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    int followers = (int) dataSnapshot.getChildrenCount();
                    String count = String.valueOf(followers);
                    viewHolder.commentCount.setText(count);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //-------------------getting page of publish-----------------
            viewHolder.userPageName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pageIntent = new Intent(mContext, YourPageActivity.class);
                    pageIntent.putExtra("pushId", publish.getPublisher_page());
                    pageIntent.putExtra("page_click_by", currentUser);
                    mContext.startActivity(pageIntent);
                }
            });


            //---------------------like feature-------------------
            viewHolder.unlikeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Animation expandIn = AnimationUtils.loadAnimation(mContext, R.anim.expand_in);
                    viewHolder.unlikeView.setVisibility(View.GONE);
                    viewHolder.likeView.setVisibility(View.VISIBLE);
                    viewHolder.likeView.startAnimation(expandIn);

                    Map likeMap = new HashMap();
                    likeMap.put(currentUser, "like");

                    likeDatabase.child(publish.getPublish_pushId()).child(currentUser).updateChildren(likeMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                        }
                    });

                }
            });

            //--------------unlike feature---------------------
            viewHolder.likeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation expandIn = AnimationUtils.loadAnimation(mContext, R.anim.expand_in);
                    viewHolder.likeView.setVisibility(View.GONE);
                    viewHolder.unlikeView.setVisibility(View.VISIBLE);
                    viewHolder.unlikeView.startAnimation(expandIn);

                    Map likeMap = new HashMap();
                    likeMap.put(currentUser, null);

                    likeDatabase.child(publish.getPublish_pushId()).child(currentUser).updateChildren(likeMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                        }
                    });

                }
            });

            //-----------------------like/ unlike count feature------------------
            likeDatabase.child(publish.getPublish_pushId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    int followers = (int) dataSnapshot.getChildrenCount();
                    String count = String.valueOf(followers);
                    viewHolder.likeCount.setText(count);

                    if (dataSnapshot.hasChild(currentUser)){
                        viewHolder.likeView.setVisibility(View.VISIBLE);
                        viewHolder.unlikeView.setVisibility(View.GONE);
                    }else {
                        viewHolder.likeView.setVisibility(View.GONE);
                        viewHolder.unlikeView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            pageDatabase.child(publish.getPublisher_page()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String page_name = dataSnapshot.child("title").getValue().toString();
                    viewHolder.userPageName.setText(page_name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mDatabase.child(publish.getPublisher_id()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final String publisher_image = dataSnapshot.child("user_thumbImage").getValue().toString();
                    String user_id = dataSnapshot.child("user_id").getValue().toString();
                    final String  user_name = dataSnapshot.child("user_name").getValue().toString();

                    viewHolder.userName.setText(user_id);
                    Picasso picasso1 = Picasso.get();
                    picasso1.load(publisher_image).placeholder(R.drawable.avatar).networkPolicy(NetworkPolicy.OFFLINE)
                            .into(viewHolder.userImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {

                                    picasso.load(publisher_image).placeholder(R.drawable.avatar).into(viewHolder.userImage);
                                }
                            });

                    //---------------comment feature--------------------//

                    viewHolder.commentImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String imagePrev = publish.getPublish_thumb();
                            String imageInfo = publish.getText_info();
                            String publisher_pageName = viewHolder.userPageName.getText().toString();

                            Intent commentIntent = new Intent(mContext, FeedCommentActivity.class);
                            commentIntent.putExtra("preview_image", imagePrev);
                            commentIntent.putExtra("imageInfo", imageInfo);
                            commentIntent.putExtra("publisher_image", publisher_image);
                            commentIntent.putExtra("page_name", publisher_pageName);
                            commentIntent.putExtra("userName", user_name);
                            commentIntent.putExtra("publish_pushId", publish.getPublish_pushId());
                            commentIntent.putExtra("publisher_id", publish.getPublisher_id());
                            mContext.startActivity(commentIntent);
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }
        else if (publish.getPublish_type().equals("Video")){

            viewHolder.playerView.setVisibility(View.VISIBLE);
            viewHolder.publishImage.setVisibility(View.GONE);
            viewHolder.pollLayout.setVisibility(View.GONE);
            viewHolder.questionRel.setVisibility(View.GONE);

            viewHolder.bind(Uri.parse(publish.getPublish()));

            viewHolder.mainCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent clipIntent = new Intent(mContext, VideoClipActivity.class);
                    mContext.startActivity(clipIntent);
                }
            });

        }
        else if (publish.getPublish_type().equals("Poll")){

            viewHolder.pollLayout.setVisibility(View.VISIBLE);
            viewHolder.playerView.setVisibility(View.GONE);
            viewHolder.publishImage.setVisibility(View.GONE);
            viewHolder.questionRel.setVisibility(View.GONE);

            Picasso picasso = Picasso.get();
            picasso.setIndicatorsEnabled(false);
            picasso.load(publish.getFirstPoll()).into(viewHolder.pollFirst);
            picasso.load(publish.getSecondPoll()).into(viewHolder.pollSecond);
            viewHolder.firstText.setText(publish.getFirstText());
            viewHolder.secondText.setText(publish.getSecondText());

        }
        else if (publish.getPublish_type().equals("Question")){

            viewHolder.pollLayout.setVisibility(View.GONE);
            viewHolder.playerView.setVisibility(View.GONE);
            viewHolder.publishImage.setVisibility(View.GONE);
            viewHolder.questionRel.setVisibility(View.VISIBLE);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(publish.getPublisher_id()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final String name = dataSnapshot.child("user_id").getValue().toString();
                    final String profile = dataSnapshot.child("user_thumbImage").getValue().toString();

                    Picasso picasso = Picasso.get();
                    picasso.setIndicatorsEnabled(false);
                    picasso.load(profile).into(viewHolder.userImage);
                    viewHolder.userName.setText(name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            pageDatabase.child(publish.getPublisher_page()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String page_name = dataSnapshot.child("title").getValue().toString();
                    viewHolder.userPageName.setText(page_name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            viewHolder.askQuestion.setText(publish.getQuestion());


        }



        final String id = publish.getPublish_pushId();

        publishDatabase = FirebaseDatabase.getInstance().getReference().child("Publish");
        publishDatabase.keepSynced(true);
        viewHolder.feedMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onItemClick.getPosition(id);
            }
        });

        //setScaleAnimation(viewHolder.itemView);
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.85f, 1.0f, 0.85f, 1.0f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return mPublish.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ToroPlayer{

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

        public static class AnotherClass{

            public void update(List<Publish> mPublish, int i) {

                Publish publish = mPublish.get(i);

            }
        }

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
