package com.ssproduction.shashank.network;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.Adapter.AllPublishAdapter;
import com.ssproduction.shashank.network.Adapter.AllUsersListAdapter;
import com.ssproduction.shashank.network.Adapter.FollowersListAdapter;
import com.ssproduction.shashank.network.Adapter.PublishShowAdapter;
import com.ssproduction.shashank.network.Fragments.EditPostBottomSheet;
import com.ssproduction.shashank.network.Fragments.ProfileBottomSheet;
import com.ssproduction.shashank.network.Models.ImagePicker;
import com.ssproduction.shashank.network.Utils.Following;
import com.ssproduction.shashank.network.Utils.Publish;
import com.ssproduction.shashank.network.Utils.Users;
import com.ssproduction.shashank.network.Utils.multiPublish;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import im.ene.toro.widget.Container;

public class YourPageActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private ImageView pageImage, addPageImage;
    private TextView pageTitle, pageFollowers, editPage, pageTag, pageAbout, otherDetail, followPage;
    private CardView publishSomething;
    private ImageView publishImage;
    private RecyclerView publishList;
    private RecyclerView followersList;

    private List<Publish> mPublish;
    private AllPublishAdapter publishAdapter;
    private List<multiPublish> mMultiPublish;

    private List<Following> mFollowing;
    private List<Users> mUsers;

    private static final int PICK = 1;
    private ProgressDialog dialog;

    private DatabaseReference pageDatabase, followDatabase, mDatabase;
    private DatabaseReference publishDatabase, multiDatabase;
    private StorageReference mImageStorage;
    private FirebaseAuth mAuth;
    private String currentUser, page_pushId;
    private String current_state = "not_following";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_page);

        page_pushId = getIntent().getStringExtra("pushId");
        String page_administrator = getIntent().getStringExtra("page_click_by");

        dialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        pageDatabase = FirebaseDatabase.getInstance().getReference().child("AllPages");
        publishDatabase = FirebaseDatabase.getInstance().getReference().child("Publish");
        mImageStorage = FirebaseStorage.getInstance().getReference();
        multiDatabase = FirebaseDatabase.getInstance().getReference().child("multiPublish");
        followDatabase = FirebaseDatabase.getInstance().getReference().child("Following");

        pageDatabase.keepSynced(true);
        publishDatabase.keepSynced(true);
        followDatabase.keepSynced(true);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your Page");

        pageImage = (ImageView) findViewById(R.id.page_cover_image);
        addPageImage = (ImageView) findViewById(R.id.change_page_cover_imageview);
        pageTitle = (TextView) findViewById(R.id.create_page_page_title);
        pageFollowers = (TextView) findViewById(R.id.create_page_page_followers_count);
        publishSomething = (CardView) findViewById(R.id.publish_something_in_page_card);
        publishImage = (ImageView) findViewById(R.id.create_page_publish_image);
        publishList = (RecyclerView) findViewById(R.id.create_page_publish_list);
        editPage = (TextView) findViewById(R.id.create_page_edit_page_detail);
        followPage = (TextView) findViewById(R.id.my_page_follow_text);
        pageAbout = (TextView) findViewById(R.id.your_page_page_about);
        pageTag = (TextView) findViewById(R.id.your_page_page_tag);
        followersList = (RecyclerView) findViewById(R.id.my_page_followers_list);
        otherDetail = (TextView) findViewById(R.id.my_page_other_details);


        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        manager.setSmoothScrollbarEnabled(true);
        publishList.setLayoutManager(manager);
        publishList.setHasFixedSize(true);
        publishList.setItemAnimator(new DefaultItemAnimator());
        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.my_page_nested_scrollview);
        scrollView.getParent().requestChildFocus(scrollView, scrollView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        followersList.setLayoutManager(layoutManager);
        followersList.setHasFixedSize(true);

        mPublish = new ArrayList<>();
        mFollowing = new ArrayList<>();

        //--------------page click by other------------------
        if (!page_administrator.equals(currentUser)){

            editPage.setVisibility(View.GONE);
            editPage.setEnabled(false);
            followPage.setVisibility(View.VISIBLE);
            followPage.setEnabled(true);
            publishSomething.setVisibility(View.GONE);

        }

        if (page_administrator.equals(currentUser)){

            editPage.setVisibility(View.VISIBLE);
            editPage.setEnabled(true);
            followPage.setVisibility(View.GONE);
            followPage.setEnabled(false);
        }

        //-----------------------page following by people feature-----------------
        //----------------------read followers feature---------------------
        followingFeature();


        editPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editPageIntent = new Intent(YourPageActivity.this, EditPageActivity.class);
                editPageIntent.putExtra("push_id", page_pushId);
                startActivity(editPageIntent);
            }
        });


        publishSomething.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent(YourPageActivity.this, PublishPrevActivity.class);
                viewIntent.putExtra("page_id", page_pushId);
                startActivity(viewIntent);
            }
        });

        pageDatabase.child(page_pushId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String page_title = dataSnapshot.child("title").getValue().toString();
                String page_tagLine = dataSnapshot.child("page_tagLine").getValue().toString();
                String page_about = dataSnapshot.child("page_about").getValue().toString();
                String page_cover = dataSnapshot.child("page_cover").getValue().toString();
                String page_followers = dataSnapshot.child("followers_count").getValue().toString();
                String other_links = dataSnapshot.child("other_links").getValue().toString();

                pageTitle.setText(page_title);
                pageTag.setText(page_tagLine);
                pageAbout.setText(page_about);
                otherDetail.setText(other_links);

                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(page_cover).placeholder(R.drawable.attach_dialog_background)
                        .into(pageImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        publishImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), PICK);

            }
        });

    }

    private void followingFeature() {

        followDatabase.child(page_pushId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //-------------------showing followers------------
                mFollowing.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Following following = snapshot.getValue(Following.class);
                    mFollowing.add(following);
                }
                readFollowers();
                readPublish();

                //---------------------showing followers count-------------------
                int followers = (int) dataSnapshot.getChildrenCount();
                String count = String.valueOf(followers);
                if (count.equals("1")){
                    pageFollowers.setText(count + "  follower");
                }else if (count.equals("0")){
                    pageFollowers.setText("No  followers");
                }else {
                    pageFollowers.setText(count + "  followers");
                }

                //-----------------follow/following feature----------------
                if (dataSnapshot.hasChild(currentUser)){
                    String followType = dataSnapshot.child(currentUser).child("follow_type").getValue().toString();

                    if (followType.equals("following")){

                        followPage.setText("Following");
                        current_state.equals("following");

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        followPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //--------------not_following state-----------------
                if (current_state.equals("not_following")){
                    followPage.setText("Following");

                    Map followMap = new HashMap();
                    followMap.put("follow_type", "following");
                    followMap.put("follower_id", currentUser);
                    followMap.put("page_id", page_pushId);

                    followDatabase.child(page_pushId).child(currentUser).updateChildren(followMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful()){

                                followPage.setText("Following");
                                current_state = "following";
                            }
                        }
                    });

                }

                //--------------following state------------------------
                if (current_state.equals("following")){
                    followPage.setText("Follow");

                    followDatabase.child(page_pushId).child(currentUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                current_state = "not_following";
                                followPage.setText("Follow");
                            }
                        }
                    });

                }

            }
        });

    }

    private void readFollowers() {
        mUsers = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users users = snapshot.getValue(Users.class);
                    for (Following following : mFollowing){
                        if (users.getUserKey_id().equals(following.getFollower_id())){
                            mUsers.add(users);

                        }
                    }
                }
                FollowersListAdapter adapter = new FollowersListAdapter(mUsers, getApplicationContext());
                followersList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readPublish() {

        publishDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPublish.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final Publish publish = snapshot.getValue(Publish.class);
                    for (Following following : mFollowing){
                        if (publish.getPublisher_page().equals(page_pushId)){
                            if (following.getFollower_id().equals(currentUser)){
                                mPublish.add(publish);

                            }
                        }
                    }
                }

                AllPublishAdapter adapter = new AllPublishAdapter(getApplicationContext(), mPublish);
                adapter.notifyDataSetChanged();
                publishList.setAdapter(adapter);
                adapter.setOnItemClick(new AllPublishAdapter.OnItemClick() {
                    @Override
                    public void getPosition(final String id) {

                        /*EditPostBottomSheet bottomSheet = new EditPostBottomSheet();
                        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag();

                        */

                        final String[] items = {"Edit Post", "Delete Post"};
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(YourPageActivity.this);
                        alertDialog.setTitle("Select Options");
                        alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if ("Edit Post".equals(items[which])){
                                    Toast.makeText(YourPageActivity.this, "Edit post is not available yet", Toast.LENGTH_SHORT).show();

                                }else if ("Delete Post".equals(items[which])){

                                    publishDatabase.child(id).removeValue();
                                }

                            }
                        });
                        alertDialog.show();


                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK && resultCode == RESULT_OK){

            dialog.setMessage("Publishing");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            Uri resultUri = data.getData();

            final File thumb_filePath = new File(resultUri.getPath());

            final Bitmap bitmap = ImagePicker.getImageFromResult(getApplicationContext(), resultCode, data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            final byte[] thumb_byte = baos.toByteArray();


            StorageReference filepath = mImageStorage.child("Posts").child(currentUser).child(random() + ".jpg");
            final StorageReference thumb_filepath = mImageStorage.child("Posts").child(currentUser).child("thumb")
                    .child(random() + "jpg");

            filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){

                        final String download_url = task.getResult().getDownloadUrl().toString();

                        UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbTask) {

                                String thumb_downloadUrl = thumbTask.getResult().getDownloadUrl().toString();

                                if (thumbTask.isSuccessful()){

                                    String pushId = publishDatabase.push().getKey();

                                    Map publishMap = new HashMap();
                                    publishMap.put("publish_pushId", pushId);
                                    publishMap.put("publisher_id", currentUser);
                                    publishMap.put("publisher_page", "default");
                                    publishMap.put("publish", download_url);
                                    publishMap.put("publish_type", "Photo");
                                    publishMap.put("publish_thumb", thumb_downloadUrl);


                                    publishDatabase.child(pushId).updateChildren(publishMap).addOnCompleteListener
                                            (new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {

                                                    if (task.isSuccessful()){
                                                        dialog.dismiss();
                                                        Toast.makeText(YourPageActivity.this, "success", Toast.LENGTH_SHORT).show();

                                                    }

                                                }
                                            });
                                }
                                else {
                                    dialog.hide();
                                    Toast.makeText(YourPageActivity.this, "error uploading", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.edit_page_item){
            Intent editIntent = new Intent(YourPageActivity.this, EditPageActivity.class);
            editIntent.putExtra("push_id", page_pushId);
            startActivity(editIntent);

        }else if (item.getItemId() == R.id.delete_page_item){
            pageDatabase.child(page_pushId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent mainIntent = new Intent(YourPageActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    Toast.makeText(YourPageActivity.this, "deleted successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }


        return true;
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();

    }

}
