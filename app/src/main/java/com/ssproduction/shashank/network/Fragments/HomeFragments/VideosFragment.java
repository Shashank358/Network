package com.ssproduction.shashank.network.Fragments.HomeFragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.AllPublishAdapter;
import com.ssproduction.shashank.network.Adapter.PublishAdapter.VideoAdapter;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Publish;
import com.ssproduction.shashank.network.Utils.PublishVideo;

import java.util.ArrayList;
import java.util.List;

import im.ene.toro.widget.Container;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideosFragment extends Fragment {

    private Container list;
    private VideoAdapter adapter;
    private List<PublishVideo> mVideo;

    private DatabaseReference pageDatabase;
    private DatabaseReference videoDatabase;
    private String mCurrentUser;

    private SwipeRefreshLayout refreshLayout;
    private static final int LIMIT_TO_FIRST = 10;
    private int currentPage = 1;
    private int LAST_ITEM = 5;

    public VideosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        pageDatabase = FirebaseDatabase.getInstance().getReference().child("AllPages");
        pageDatabase.keepSynced(true);
        videoDatabase = FirebaseDatabase.getInstance().getReference().child("PublishVideo");
        videoDatabase.keepSynced(true);

        list = (Container) view.findViewById(R.id.video_frag_publish_list);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.video_frag_pull_to_refresh);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setStackFromEnd(true);
        manager.setSmoothScrollbarEnabled(true);
        manager.setReverseLayout(true);
        list.setLayoutManager(manager);
        list.setHasFixedSize(true);

        mVideo = new ArrayList<>();
        readPublish();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refreshData();

                currentPage++;

                mVideo.clear();

                readPublish();


            }
        });

        return view;
    }

    private void readPublish() {

        Query publishQuery = videoDatabase.limitToFirst(currentPage * LIMIT_TO_FIRST);

        publishQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mVideo.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PublishVideo video = snapshot.getValue(PublishVideo.class);

                    mVideo.add(video);
                    VideoAdapter adapter = new VideoAdapter(mVideo, getContext());
                    list.setAdapter(adapter);

                    refreshLayout.setRefreshing(false);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
