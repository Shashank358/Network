package com.ssproduction.shashank.network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.VideoClipAdapter;
import com.ssproduction.shashank.network.Utils.Publish;

import java.util.ArrayList;
import java.util.List;

public class VideoClipActivity extends AppCompatActivity {

    private List<Publish> mPublish;
    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_clip);

        recyclerView = (RecyclerView) findViewById(R.id.all_videos_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.setHasFixedSize(true);

        mPublish = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Publish");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mPublish.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Publish publish = snapshot.getValue(Publish.class);
                    if (publish.getPublish_type().equals("Video")){
                        mPublish.add(publish);
                    }

                    VideoClipAdapter adapter = new VideoClipAdapter(getApplicationContext(), mPublish);
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
