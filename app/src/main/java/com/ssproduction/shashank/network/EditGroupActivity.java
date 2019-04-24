package com.ssproduction.shashank.network;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.Adapter.groupAdapter.EditGroupMembAdapter;
import com.ssproduction.shashank.network.Utils.AddPeople;
import com.ssproduction.shashank.network.Utils.Users;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class EditGroupActivity extends AppCompatActivity {

    private EditText groupTitle;
    private TextView editDone;
    private RecyclerView membersList;
    private ImageView addCover, groupCover;

    private static final int SELECT_IMAGE = 1;

    private DatabaseReference groupDatabase, membDatabase;
    private StorageReference coverStorage, thumbCoverStorage;
    private String group_id;

    private Toolbar mToolbar;

    private List<Users> mUsers;
    private List<AddPeople> mPeople;
    private EditGroupMembAdapter adapter;

    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        group_id = getIntent().getStringExtra("group_id");

        dialog = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.edit_group_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groupTitle = (EditText) findViewById(R.id.edit_group_name_edittext);
        editDone = (TextView) findViewById(R.id.edit_group_activity_done_text);
        membersList = (RecyclerView) findViewById(R.id.edit_group_all_members_list);
        addCover = (ImageView) findViewById(R.id.edit_group_add_group_cover_image);
        groupCover = (ImageView) findViewById(R.id.edit_group_group_cover_imageview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        membersList.setHasFixedSize(true);
        membersList.setLayoutManager(layoutManager);

        groupDatabase = FirebaseDatabase.getInstance().getReference().child("AllGroups");

        editDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String group_title = groupTitle.getText().toString();

                Map groupMap = new HashMap();
                groupMap.put("group_title", group_title);

                groupDatabase.child(group_id).updateChildren(groupMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if (databaseError == null){
                            Intent groupIntent = new Intent(EditGroupActivity.this, GroupChatActivity.class);
                            groupIntent.putExtra("group_id", group_id);
                            groupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(groupIntent);
                        }
                    }
                });
            }
        });

        //----------------getting group info-------------
        groupDatabase.child(group_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String group_cover = dataSnapshot.child("group_cover").getValue().toString();
                String group_title = dataSnapshot.child("group_title").getValue().toString();

                groupTitle.setText(group_title);
                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(group_cover).placeholder(R.drawable.default_image).into(groupCover);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //--------------adding group cover image---------------------
        addCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_IMAGE);
            }
        });



        mPeople = new ArrayList<>();
        membDatabase = FirebaseDatabase.getInstance().getReference().child("AddPeople");
        membDatabase.child(group_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPeople.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AddPeople people = snapshot.getValue(AddPeople.class);
                    mPeople.add(people);
                }

                readMembers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void readMembers() {
        mUsers = new ArrayList<>();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users users = snapshot.getValue(Users.class);
                    for (AddPeople people : mPeople){
                        if (users.getUserKey_id().equals(people.getUser_id())){
                            mUsers.add(users);

                        }
                        adapter = new EditGroupMembAdapter(mUsers, getApplicationContext());
                        membersList.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(3, 2)
                    .start(EditGroupActivity.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){

                dialog.setMessage("Updating page cover");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                Uri resultUri = result.getUri();

                final File thumb_filePath = new File(resultUri.getPath());

                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(480)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                final byte[] thumb_byte = baos.toByteArray();

                coverStorage = FirebaseStorage.getInstance().getReference().child("GroupCover")
                        .child(group_id).child(group_id + ".jpg");
                thumbCoverStorage = FirebaseStorage.getInstance().getReference().child("GroupCover")
                        .child("thumb").child(group_id).child(group_id + ".jpg");

                coverStorage.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        String cover_downloadUrl = task.getResult().getDownloadUrl().toString();

                        Map groupMap = new HashMap();
                        groupMap.put("group_cover", cover_downloadUrl);

                        groupDatabase.child(group_id).updateChildren(groupMap)
                                .addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful()){
                                            dialog.dismiss();
                                            Toast.makeText(EditGroupActivity.this, "updated successfully", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });
                    }
                });
            }else {
                Toast.makeText(this, "something is error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
