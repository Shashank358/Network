package com.ssproduction.shashank.network.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.AllPublishAdapter;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Publish;
import com.ssproduction.shashank.network.Utils.PublishPrev;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PagesPublishFrag extends Fragment {

    private List<Publish> mPublish;
    private AllPublishAdapter adapter;
    private DatabaseReference publishDatabase;
    private RecyclerView publishList;


    public PagesPublishFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle argument = getArguments();
        final String otherUser = argument.getString("user_id");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pages_publish, container, false);

        publishList = (RecyclerView) view.findViewById(R.id.pages_publish_list_for_profile_activity);
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        publishList.setHasFixedSize(true);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        publishList.setLayoutManager(manager);
        mPublish = new ArrayList<>();

        publishDatabase = FirebaseDatabase.getInstance().getReference().child("Publish");

        publishDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPublish.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Publish publish = snapshot.getValue(Publish.class);
                    if (otherUser.equals(publish.getPublisher_id())){
                        mPublish.add(publish);

                    }
                }
                adapter = new AllPublishAdapter(getContext(), mPublish);
                adapter.notifyDataSetChanged();
                publishList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
