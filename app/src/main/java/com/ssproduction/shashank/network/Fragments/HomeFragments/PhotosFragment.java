package com.ssproduction.shashank.network.Fragments.HomeFragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.AllPublishAdapter;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Publish;

import java.util.ArrayList;
import java.util.List;

import im.ene.toro.widget.Container;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosFragment extends Fragment {

    private Container list;
    private AllPublishAdapter adapter;
    private List<Publish> mPublish;

    private DatabaseReference pageDatabase;
    private DatabaseReference publishDatabase;
    private String mCurrentUser;

    private SwipeRefreshLayout refreshLayout;

    private static final int LIMIT_TO_FIRST = 10;
    private int currentPage = 1;
    private int LAST_ITEM = 5;

    public PhotosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        pageDatabase = FirebaseDatabase.getInstance().getReference().child("AllPages");
        pageDatabase.keepSynced(true);
        publishDatabase = FirebaseDatabase.getInstance().getReference().child("Publish");
        publishDatabase.keepSynced(true);

        list = (Container) view.findViewById(R.id.photo_frag_publish_list);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.photo_frag_pull_to_refresh);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setStackFromEnd(true);
        manager.setSmoothScrollbarEnabled(true);
        manager.setReverseLayout(true);
        list.setLayoutManager(manager);
        list.setHasFixedSize(true);

        mPublish = new ArrayList<>();
        readPublish();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refreshData();

                currentPage++;

                mPublish.clear();

                readPublish();


            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    private void readPublish() {

        Query publishQuery = publishDatabase.limitToFirst(currentPage * LIMIT_TO_FIRST);

        publishQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPublish.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Publish publish = snapshot.getValue(Publish.class);

                    mPublish.add(publish);
                    AllPublishAdapter adapter = new AllPublishAdapter(getContext(), mPublish);
                    list.setAdapter(adapter);

                    refreshLayout.setRefreshing(false);

                    adapter.setOnItemClick(new AllPublishAdapter.OnItemClick() {
                        @Override
                        public void getPosition(final String id) {

                        /*EditPostBottomSheet bottomSheet = new EditPostBottomSheet();
                        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag();
                        */
                            final String[] items = {"Edit Post", "Delete Post"};
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                            alertDialog.setTitle("Select Options");
                            alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if ("Edit Post".equals(items[which])){
                                        Toast.makeText(getActivity(), "Edit post is not available yet", Toast.LENGTH_SHORT).show();

                                    }else if ("Delete Post".equals(items[which])){

                                        publishDatabase.child(id).removeValue();
                                    }

                                }
                            });
                            alertDialog.show();


                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
