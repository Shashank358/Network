package com.ssproduction.shashank.network;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.UpdateLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class EditPageActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private ImageView pageCover, selectCover;
    private EditText pageTitle, pageTagLine, pageAbout, emailWeb;
    private Switch askToFollow;
    private TextView editDone;

    private static final int SELECT_IMAGE = 1;
    private ProgressDialog dialog;

    private DatabaseReference pageDatabase;
    private StorageReference coverStorage, thumbCoverStorage;
    private FirebaseAuth mAuth;
    private String currentUser, page_pushId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);

        page_pushId = getIntent().getStringExtra("push_id");

        dialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();


        pageDatabase = FirebaseDatabase.getInstance().getReference().child("AllPages");
        pageDatabase.keepSynced(true);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.edit_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Page");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pageCover = (ImageView) findViewById(R.id.edit_page_page_cover_image);
        pageTitle = (EditText) findViewById(R.id.edit_page_page_title);
        pageTagLine = (EditText) findViewById(R.id.edit_page_page_tagline);
        pageAbout = (EditText) findViewById(R.id.edit_page_about_page);
        askToFollow = (Switch) findViewById(R.id.edit_page_ask_for_follow_switch);
        editDone = (TextView) findViewById(R.id.edit_page_task_done_text);
        selectCover = (ImageView) findViewById(R.id.change_page_cover_imageview);
        emailWeb = (EditText) findViewById(R.id.edit_page_email_webLink);

        pageDatabase.child(page_pushId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String page_title = dataSnapshot.child("title").getValue().toString();
                String page_tag = dataSnapshot.child("page_tagLine").getValue().toString();
                String page_about = dataSnapshot.child("page_about").getValue().toString();
                String page_cover = dataSnapshot.child("page_cover").getValue().toString();
                String other_links = dataSnapshot.child("other_links").getValue().toString();

                pageTitle.setText(page_title);
                pageTagLine.setText(page_tag);
                pageAbout.setText(page_about);
                emailWeb.setText(other_links);


                Glide.with(getApplicationContext()).load(page_cover).into(pageCover);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String page_title = pageTitle.getText().toString();
                String page_tagLine = pageTagLine.getText().toString();
                String page_about = pageAbout.getText().toString();
                String other_links = emailWeb.getText().toString();
                String search_title = pageTitle.getText().toString().toLowerCase();

                Map newMap = new HashMap();
                newMap.put("title", page_title);
                newMap.put("page_tagLine", page_tagLine);
                newMap.put("page_about", page_about);
                newMap.put("ask_to_follow", false);
                newMap.put("other_links", other_links);
                newMap.put("search", search_title);

                final Map userMap = new HashMap();
                userMap.put("page_name", page_title);

                pageDatabase.child(page_pushId).updateChildren(newMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(EditPageActivity.this, "Success", Toast.LENGTH_SHORT).show();

                        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                        userDatabase.child(currentUser).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Intent pageIntent = new Intent(EditPageActivity.this, YourPageActivity.class);
                                pageIntent.putExtra("pushId", page_pushId);
                                pageIntent.putExtra("page_click_by", currentUser);
                                startActivity(pageIntent);
                            }
                        });
                    }
                });

            }
        });

        selectCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_IMAGE);
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
                    .start(EditPageActivity.this);
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

                coverStorage = FirebaseStorage.getInstance().getReference().child("PageCover")
                        .child(page_pushId).child(page_pushId + ".jpg");
                thumbCoverStorage = FirebaseStorage.getInstance().getReference().child("PageCover")
                        .child("thumb").child(page_pushId).child(page_pushId + ".jpg");

                coverStorage.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        String cover_downloadUrl = task.getResult().getDownloadUrl().toString();

                        Map pageMap = new HashMap();
                        pageMap.put("page_cover", cover_downloadUrl);

                        pageDatabase.child(page_pushId).updateChildren(pageMap)
                                .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if (task.isSuccessful()){
                                    dialog.dismiss();
                                    Toast.makeText(EditPageActivity.this, "updated successfully", Toast.LENGTH_SHORT).show();

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
