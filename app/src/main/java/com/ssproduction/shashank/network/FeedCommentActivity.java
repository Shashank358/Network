package com.ssproduction.shashank.network;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.ssproduction.shashank.network.Adapter.PublishAdapter.CommentsAdapter;
import com.ssproduction.shashank.network.Utils.Comments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedCommentActivity extends AppCompatActivity {

    private ImageView previewImage, likeView, unLikeView, commentView, shareView;
    private TextView imageInfo, userName, pageName, sendComment;
    private TextView commentsCount, likesCount;
    private EditText textComment;
    private CircleImageView userThumb;
    private RecyclerView commentList;
    private RelativeLayout infoRelative;

    private Toolbar mToolbar;

    private DatabaseReference commentDatabase, mDatabase, likeDatabase;
    private String currentUser;

    private List<Comments> mComments;
    private CommentsAdapter commentsAdapter;
    private String commentPushId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_comment);

        overridePendingTransition(R.anim.slide_up, R.anim.no_animation);

        String preview_image = getIntent().getStringExtra("preview_image");
        String image_info = getIntent().getStringExtra("imageInfo");
        String page_name = getIntent().getStringExtra("page_name");
        String user_name = getIntent().getStringExtra("userName");
        String user_image = getIntent().getStringExtra("publisher_image");
        final String publisher_id = getIntent().getStringExtra("publisher_id");
        final String publish_pushId = getIntent().getStringExtra("publish_pushId");

        mToolbar = (Toolbar) findViewById(R.id.comment_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        commentDatabase = FirebaseDatabase.getInstance().getReference().child("Comments");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        previewImage = (ImageView) findViewById(R.id.comment_activity_publish_preview);
        imageInfo = (TextView) findViewById(R.id.comment_activity_feed_comment_textview);
        userName = (TextView) findViewById(R.id.comment_activity_user_name);
        pageName = (TextView) findViewById(R.id.comment_activity_page_name);
        userThumb = (CircleImageView) findViewById(R.id.comment_activity_user_profile_image);
        sendComment = (TextView) findViewById(R.id.comment_activity_send_comment_view);
        textComment = (EditText) findViewById(R.id.comment_activity_comment_edittext);
        commentList = (RecyclerView) findViewById(R.id.comment_activity_all_comments_list);
        infoRelative = (RelativeLayout) findViewById(R.id.comment_activity_feed_text_relative);
        likeView = (ImageView) findViewById(R.id.comment_activity_publish_like_color);
        unLikeView = (ImageView) findViewById(R.id.comment_activity_publish_like);
        commentView = (ImageView) findViewById(R.id.comment_activity_comment_image);
        shareView = (ImageView) findViewById(R.id.comment_activity_publish_share);
        commentsCount = (TextView) findViewById(R.id.comment_activity_comments_count);
        likesCount = (TextView) findViewById(R.id.comment_activity_like_count);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        commentList.setHasFixedSize(true);
        commentList.setLayoutManager(manager);

        Picasso picasso = Picasso.get();
        picasso.load(preview_image).into(previewImage);
        picasso.load(user_image).into(userThumb);
        picasso.setIndicatorsEnabled(false);
        userName.setText(user_name);
        pageName.setText(page_name);

        //-------------image info exist checking------------
        if (!image_info.equals("")){
            infoRelative.setVisibility(View.VISIBLE);
            imageInfo.setText(image_info);
        }else {
            infoRelative.setVisibility(View.GONE);
        }

        //----------------send comment---------------------
        if (sendComment.getText().toString().equals("Send")) {
            sendComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String text_comment = textComment.getText().toString();
                    textComment.setText("");

                    if (!text_comment.equals("")) {

                        mDatabase.child(currentUser).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String user_id = dataSnapshot.child("user_id").getValue().toString();
                                String user_image = dataSnapshot.child("user_thumbImage").getValue().toString();

                                commentPushId = commentDatabase.child(publish_pushId).push().getKey();

                                Map commentMap = new HashMap();
                                commentMap.put("commenter_pushId", currentUser);
                                commentMap.put("publisher_id", publisher_id);
                                commentMap.put("comment", text_comment);
                                commentMap.put("commenter_id", user_id);
                                commentMap.put("commenter_image", user_image);
                                commentMap.put("publish_pushId", publish_pushId);
                                commentMap.put("comment_pushId", commentPushId);


                                commentDatabase.child(publish_pushId).child(commentPushId).updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            textComment.setText("");

                                        } else {
                                            Toast.makeText(FeedCommentActivity.this, "error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else {
                        Toast.makeText(FeedCommentActivity.this, "empty comment", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }


        //-------------------------------read comments------------------
        mComments = new ArrayList<>();

        commentDatabase.child(publish_pushId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mComments.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comments comments = snapshot.getValue(Comments.class);
                    if (comments.getPublish_pushId().equals(publish_pushId)){
                        mComments.add(comments);

                    }
                }
                commentsAdapter = new CommentsAdapter(mComments, getApplicationContext());

                commentList.setAdapter(commentsAdapter);
                commentList.scrollToPosition(mComments.size() - 1);

                commentsAdapter.setOnItemClick(new CommentsAdapter.OnItemClick() {
                    @Override
                    public void getPosition(final String commentId) {

                    }
                });

                //------------------comments count-----------------//
                int comments = (int) dataSnapshot.getChildrenCount();
                String count = String.valueOf(comments);
                if (count.equals("1")){
                    commentsCount.setText(count + " Comment");
                }else if (count.equals("0")){
                    commentsCount.setText("No Comments");
                }
                else {
                    commentsCount.setText(count + " Comments");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //---------------------likes count---------------------
        likeDatabase = FirebaseDatabase.getInstance().getReference().child("Likes");
        likeDatabase.child(publish_pushId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int likes = (int) dataSnapshot.getChildrenCount();
                String count = String.valueOf(likes);
                if (count.equals("1")){
                    likesCount.setText(count + " Like");
                }else if (count.equals("0")){
                    likesCount.setText("No Likes");
                }
                else {
                    likesCount.setText(count + " Likes");
                }

                //--------------------is like or not state----------------
                if (dataSnapshot.hasChild(currentUser)){
                    likeView.setVisibility(View.VISIBLE);
                    unLikeView.setVisibility(View.GONE);
                }else {
                    likeView.setVisibility(View.GONE);
                    unLikeView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //----------------like unlike feature-------------------------

        unLikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation expandIn = AnimationUtils.loadAnimation(FeedCommentActivity.this, R.anim.expand_in);
                unLikeView.setVisibility(View.GONE);
                likeView.setVisibility(View.VISIBLE);
                likeView.startAnimation(expandIn);

                Map likeMap = new HashMap();
                likeMap.put(currentUser, "like");

                likeDatabase.child(publish_pushId).child(currentUser).updateChildren(likeMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                    }
                });

            }
        });

        //--------------unlike feature---------------------
        likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation expandIn = AnimationUtils.loadAnimation(FeedCommentActivity.this, R.anim.expand_in);
                likeView.setVisibility(View.GONE);
                unLikeView.setVisibility(View.VISIBLE);
                unLikeView.startAnimation(expandIn);

                Map likeMap = new HashMap();
                likeMap.put(currentUser, null);

                likeDatabase.child(publish_pushId).child(currentUser).updateChildren(likeMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                    }
                });

            }
        });



    }
}
