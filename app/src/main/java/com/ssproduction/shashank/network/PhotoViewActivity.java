package com.ssproduction.shashank.network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewActivity extends AppCompatActivity {

    PhotoViewAttacher pAttacher;
    PhotoView post;

    android.support.v7.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        String imagePost = getIntent().getStringExtra("image");

        post = (PhotoView) findViewById(R.id.post_show_image_view);
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.post_show_image_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        Picasso picasso = Picasso.get();
        picasso.load(imagePost).into(post);
        pAttacher = new PhotoViewAttacher(post);
        pAttacher.update();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_view_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.photo_view_photo_download_item){

            StorageReference reference = FirebaseStorage.getInstance().getReference().child("");
        }

        else if (item.getItemId() == R.id.photo_view_setting_item){

        }

        return true;
    }
}
