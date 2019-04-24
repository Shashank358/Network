package com.ssproduction.shashank.network;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.Adapter.AllPublishAdapter;
import com.ssproduction.shashank.network.Adapter.GreetingAdapter;
import com.ssproduction.shashank.network.Adapter.PublishPrevAdapter;
import com.ssproduction.shashank.network.Adapter.PublishShowAdapter;
import com.ssproduction.shashank.network.Models.ImagePicker;
import com.ssproduction.shashank.network.Utils.Greetings;
import com.ssproduction.shashank.network.Utils.Publish;
import com.ssproduction.shashank.network.Utils.PublishPrev;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.media.ThumbnailUtils.createVideoThumbnail;

public class PublishPrevActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText publishText, firstPollText, secondPollText, askQuestion;
    private LinearLayout publishImage, publishClip, publishGreeting, publishPoll, publishQuestion;
    private RecyclerView imagePrevList;
    private TextView publishDone, pollOptionalText, pageName;
    private ImageView publishPrev, firstPollAdd, secondPollAdd, firstPollPrev, secondPollPrev;
    private ConstraintLayout pollLayout;
    private TextView userName;
    private ImageView questionBack;
    private CircleImageView profile;
    private RelativeLayout questionLayout;

    private VideoView videoView;
    private MediaController mediaC;
    private Uri firstUri, secondUri;
    private ArrayList<String> imagesPathList;


    ArrayList<String> filePath = new ArrayList<String>();
    private LinearLayoutManager layoutManager;
    private static final int SELECT_VIDEO = 1;
    private static final int SELECT_IMAGE = 2;

    private static final int POLL_FIRST = 3;
    private static final int POLL_SECOND = 4;

    private static GreetingAdapter adapter;

    private DatabaseReference publishDatabase, mDatabase;
    private StorageReference mImageStorage;
    private String currentUser, page_id;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_prev);

        overridePendingTransition(R.anim.slide_up, R.anim.no_animation);

        page_id = getIntent().getStringExtra("page_id");

        mToolbar = (Toolbar) findViewById(R.id.publish_preview_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        publishDatabase = FirebaseDatabase.getInstance().getReference().child("Publish");
        publishDatabase.keepSynced(true);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        profile = (CircleImageView) findViewById(R.id.publish_prev_user_profile);
        userName = (TextView) findViewById(R.id.publish_prev_user_name);
        publishText = (EditText) findViewById(R.id.publish_prev_text_something);
        publishImage = (LinearLayout) findViewById(R.id.publish_prev_publish_image_linear);
        publishClip = (LinearLayout) findViewById(R.id.publish_prev_publish_video_linear);
        publishGreeting = (LinearLayout) findViewById(R.id.publish_prev_publish_greeting_linear);
        publishPoll = (LinearLayout) findViewById(R.id.publish_prev_publish_poll_linear);
        publishQuestion = (LinearLayout) findViewById(R.id.publish_prev_publish_question_linear);
        imagePrevList = (RecyclerView) findViewById(R.id.publish_prev_image_list);
        publishDone = (TextView) findViewById(R.id.publish_preview_poublish_done);
        videoView = (VideoView) findViewById(R.id.publish_prev_video_preview);
        publishPrev = (ImageView) findViewById(R.id.publish_prev_image_prev);
        pageName = (TextView) findViewById(R.id.publish_prev_page_name);
        askQuestion = (EditText) findViewById(R.id.publish_prev_ask_question_edittext);
        questionLayout = (RelativeLayout) findViewById(R.id.publish_prev_ask_question_relative);
        pollLayout = (ConstraintLayout) findViewById(R.id.publish_prev_poll_constraint);
        firstPollAdd = (ImageView) findViewById(R.id.first_poll_add_image);
        secondPollAdd = (ImageView) findViewById(R.id.second_poll_add_image);
        firstPollPrev = (ImageView) findViewById(R.id.first_poll_image_view);
        secondPollPrev = (ImageView) findViewById(R.id.second_poll_image_view);
        firstPollText = (EditText) findViewById(R.id.first_poll_edittext);
        secondPollText = (EditText) findViewById(R.id.second_poll_edittext);
        pollOptionalText = (TextView) findViewById(R.id.poll_optional_text);
        publishGreeting = (LinearLayout) findViewById(R.id.publish_prev_publish_greeting_linear);
        RecyclerView greetList = (RecyclerView) findViewById(R.id.all_greetings_list);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imagePrevList.setLayoutManager(layoutManager);
        imagePrevList.setHasFixedSize(true);

        GreetingAdapter adapter = new GreetingAdapter(getApplicationContext());
        greetList.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        greetList.setLayoutManager(manager);
        greetList.setHasFixedSize(true);


        //---------------getting page name-----------------------
        DatabaseReference pageDatabase = FirebaseDatabase.getInstance().getReference().child("AllPages");
        pageDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(page_id)){
                    String page_name = dataSnapshot.child(page_id).child("title").getValue().toString();
                    pageName.setText(page_name + " -");
                    pageName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent selectIntent = new Intent(PublishPrevActivity.this, SelectPageActivity.class);
                            startActivity(selectIntent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //-------------------getting user info----------------
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("user_id").getValue().toString();
                String image = dataSnapshot.child("user_thumbImage").getValue().toString();

                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(image).placeholder(R.drawable.avatar).into(profile);
                userName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //----------------publishing greetings----------------
        publishGreeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //----------------publishing images--------------
        publishImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(i, SELECT_IMAGE);


            }
        });

        //---------------publishing question------------------
        publishQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (questionLayout.getVisibility() == View.GONE){

                    questionLayout.setVisibility(View.VISIBLE);
                }
                else {
                    questionLayout.setVisibility(View.GONE);

                }
            }
        });


            publishDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String pushId = publishDatabase.push().getKey();

                    dialog.setMessage("Publishing question");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    String question = askQuestion.getText().toString();
                    String publish_info = publishText.getText().toString();

                    final HashMap publishMap = new HashMap();
                    publishMap.put("publish_pushId", pushId);
                    publishMap.put("publisher_id", currentUser);
                    publishMap.put("publisher_page", page_id);
                    publishMap.put("publish", "default");
                    publishMap.put("publish_type", "Question");
                    publishMap.put("publish_thumb", "default");
                    publishMap.put("firstPoll", "default");
                    publishMap.put("secondPoll", "default");
                    publishMap.put("firstText", "default");
                    publishMap.put("secondText", "default");
                    publishMap.put("text_info", publish_info);
                    publishMap.put("question", question);



                    publishDatabase.child(pushId).updateChildren(publishMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                dialog.dismiss();

                                Intent pageIntent = new Intent(PublishPrevActivity.this, YourPageActivity.class);
                                pageIntent.putExtra("pushId", page_id);
                                pageIntent.putExtra("page_click_by", currentUser);
                                pageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(pageIntent);

                            }
                        }
                    });
                }
            });

        //----------------------publishing clips---------------
        publishClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_VIDEO);

            }
        });

        //----------------publishing poll---------------
        publishPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pollLayout.getVisibility() == View.GONE){
                    pollLayout.animate().alpha(1.0f);
                    pollLayout.setVisibility(View.VISIBLE);
                }


                else {
                    pollLayout.animate().alpha(0.0f);
                    pollLayout.setVisibility(View.GONE);

                }
            }
        });

        firstPollAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, POLL_FIRST);

            }
        });

        secondPollAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, POLL_SECOND);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //------------------pick photo -----------------------------//

        if (requestCode == SELECT_IMAGE && data != null){

            publishPrev.setVisibility(View.VISIBLE);

            final Uri resultUri = data.getData();
            Picasso picasso = Picasso.get();
            picasso.setIndicatorsEnabled(false);
            picasso.load(resultUri).into(publishPrev);

            final File thumb_filePath = new File(resultUri.getPath());

            final Bitmap bitmap = ImagePicker.getImageFromResult(getApplicationContext(), resultCode, data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            final byte[] thumb_byte = baos.toByteArray();

            publishDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.setMessage("Publishing Photo");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    final String text_in_publish = publishText.getText().toString();


                    final String pushId = publishDatabase.push().getKey();
                    final String publishId = publishDatabase.child(pushId).child("publish_thumb").push().getKey();

                    StorageReference postRef = mImageStorage.child("Posts").child(currentUser).child(random() + ".jpg");
                    final StorageReference thumb_postRef = mImageStorage.child("Posts").child(currentUser).child("thumb")
                            .child(random() + ".jpg");

                    postRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()){
                                final String download_url = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = thumb_postRef.putBytes(thumb_byte);

                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbTask) {

                                        final String thumb_downloadUrl = thumbTask.getResult().getDownloadUrl().toString();

                                        if (thumbTask.isSuccessful()){

                                            final HashMap publishMap = new HashMap();
                                            publishMap.put("publish_pushId", pushId);
                                            publishMap.put("publisher_id", currentUser);
                                            publishMap.put("publisher_page", page_id);
                                            publishMap.put("publish", download_url);
                                            publishMap.put("publish_type", "Photo");
                                            publishMap.put("publish_thumb", thumb_downloadUrl);
                                            publishMap.put("firstPoll", "default");
                                            publishMap.put("secondPoll", "default");
                                            publishMap.put("firstText", "default");
                                            publishMap.put("secondText", "default");
                                            publishMap.put("text_info", text_in_publish);
                                            publishMap.put("question", "default");


                                            publishDatabase.child(pushId).updateChildren(publishMap).addOnCompleteListener
                                                    (new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {

                                                            if (task.isSuccessful()){
                                                                dialog.dismiss();

                                                                Intent pageIntent = new Intent(PublishPrevActivity.this, YourPageActivity.class);
                                                                pageIntent.putExtra("pushId", page_id);
                                                                pageIntent.putExtra("page_click_by", currentUser);
                                                                pageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(pageIntent);


                                                                            /*DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("multiPublish");
                                                                            String postThumb_id = reference.child(publishId).push().getKey();

                                                                            Map map1 = new HashMap();
                                                                            map1.put("publish_thumb", thumb_downloadUrl);
                                                                            map1.put("thumb_key", postThumb_id);
                                                                            map1.put("publishThumbId", publishId);
                                                                            map1.put("publishId", pushId);

                                                                            reference.child(postThumb_id).updateChildren(map1);

                                                                            */
                                                                Toast.makeText(PublishPrevActivity.this, "success", Toast.LENGTH_SHORT).show();

                                                            }

                                                        }
                                                    });
                                        }
                                        else {
                                            dialog.hide();
                                            Toast.makeText(PublishPrevActivity.this, "error uploading", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    });

                }
            });

        }

        //----------------------pick video clip-------------------------//
        if (requestCode == SELECT_VIDEO && data != null){

            videoView.setVisibility(View.VISIBLE);
            final Uri resultUri = data.getData();

            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            videoView.setVideoURI(resultUri);
            videoView.requestFocus();
            videoView.start();

            publishDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.setMessage("Publishing clip");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    InputFilter filter = new InputFilter() {
                        public CharSequence filter(CharSequence source, int start, int end,
                                                   Spanned dest, int dstart, int dend) {
                            for (int i = start; i < end; i++) {
                                if (!Character.isLetterOrDigit(source.charAt(i))) {
                                    return "";
                                }
                            }
                            return null;
                        }
                    };
                    publishText.setFilters(new InputFilter[]{filter});
                    final String text_in_publish = publishText.getText().toString();

                    final String pushId = publishDatabase.push().getKey();

                    final StorageReference videoStorage = mImageStorage.child("video").child(currentUser).child(random() + ".mp4");

                    videoStorage.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()){
                                String download_vidUrl = task.getResult().getDownloadUrl().toString();

                                final HashMap publishMap = new HashMap();
                                publishMap.put("publish_pushId", pushId);
                                publishMap.put("publisher_id", currentUser);
                                publishMap.put("publisher_page", "default");
                                publishMap.put("publish", download_vidUrl);
                                publishMap.put("publish_type", "Video");
                                publishMap.put("publish_thumb", "default");
                                publishMap.put("text_info", text_in_publish);


                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("PublishVideo");

                                reference.child(pushId).updateChildren(publishMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful()){

                                            Intent pageIntent = new Intent(PublishPrevActivity.this, YourPageActivity.class);
                                            pageIntent.putExtra("pushId", page_id);
                                            pageIntent.putExtra("page_click_by", currentUser);
                                            pageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(pageIntent);

                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });

        }

        //--------------------picking images for poll------------------------
        //----------------first poll-------------------------
        if (requestCode == POLL_FIRST && data != null){

            firstPollPrev.setVisibility(View.VISIBLE);
            firstPollAdd.setVisibility(View.INVISIBLE);
            firstUri = data.getData();
            Picasso picasso = Picasso.get();
            picasso.setIndicatorsEnabled(false);
            picasso.load(firstUri).into(firstPollPrev);

        }

        //--------------------second poll-------------------
        if (requestCode == POLL_SECOND && data != null){

            secondPollPrev.setVisibility(View.VISIBLE);
            secondPollAdd.setVisibility(View.INVISIBLE);
            secondUri = data.getData();
            Picasso picasso = Picasso.get();
            picasso.setIndicatorsEnabled(false);
            picasso.load(secondUri).into(secondPollPrev);
            pollOptionalText.setText("Vs");
        }

        if (firstPollPrev.getVisibility() == View.VISIBLE && secondPollPrev.getVisibility() == View.VISIBLE){
            publishDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PublishPrevActivity.this, "well", Toast.LENGTH_SHORT).show();

                    dialog.setMessage("Publishing Photo");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    Drawable drawable = firstPollPrev.getDrawable();

                    BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                    Bitmap bitmap = bitmapDrawable .getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
                    byte[] imageInByte = stream.toByteArray();

                    final String first = firstPollText.getText().toString();
                    final String second = secondPollText.getText().toString();

                    final String pushId = publishDatabase.push().getKey();

                    final StorageReference pollRef1 = mImageStorage.child("Posts").child(currentUser).child("Poll").child("first").child(random() + ".jpg");
                    final StorageReference pollRef2 = mImageStorage.child("Posts").child(currentUser).child("Poll").child("second").child(random() + ".jpg");


                    final UploadTask uploadTask = pollRef1.putBytes(imageInByte);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()){
                                final String firstDownloadUrl = task.getResult().getDownloadUrl().toString();

                                Drawable drawable = secondPollPrev.getDrawable();

                                BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                                Bitmap bitmap = bitmapDrawable .getBitmap();
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
                                final byte[] imageInByte1 = stream.toByteArray();

                                UploadTask uploadTask1 = pollRef2.putBytes(imageInByte1);
                                uploadTask1.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                        if (task.isSuccessful()){

                                            String secondDownloadUrl = task.getResult().getDownloadUrl().toString();

                                            final HashMap publishMap = new HashMap();
                                            publishMap.put("publish_pushId", pushId);
                                            publishMap.put("publisher_id", currentUser);
                                            publishMap.put("publisher_page", page_id);
                                            publishMap.put("publish", "default");
                                            publishMap.put("publish_type", "Poll");
                                            publishMap.put("publish_thumb", "default");
                                            publishMap.put("firstPoll", firstDownloadUrl);
                                            publishMap.put("secondPoll", secondDownloadUrl);
                                            publishMap.put("firstText", first);
                                            publishMap.put("secondText", second);
                                            publishMap.put("text_info", "default");
                                            publishMap.put("question", "default");


                                            publishDatabase.child(pushId).updateChildren(publishMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()){

                                                        dialog.dismiss();

                                                        Intent pageIntent = new Intent(PublishPrevActivity.this, YourPageActivity.class);
                                                        pageIntent.putExtra("pushId", page_id);
                                                        pageIntent.putExtra("page_click_by", currentUser);
                                                        pageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(pageIntent);

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                        }
                    });

                }
            });
        }


    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
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
