package com.ssproduction.shashank.network.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.IntrestsAdapter;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.AllPages;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntrestsFragment extends Fragment {

    private DatabaseReference pageDatabase;
    private List<AllPages> mPages;
    private IntrestsAdapter adapter;


    public IntrestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intrests, container, false);

        mPages = new ArrayList<>();
        pageDatabase = FirebaseDatabase.getInstance().getReference().child("AllPages");
        pageDatabase.keepSynced(true);

        final RecyclerView list = (RecyclerView) view.findViewById(R.id.universities_grid_list);
        list.setLayoutManager(new GridLayoutManager(getActivity(),2));
        list.setHasFixedSize(true);

        pageDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mPages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AllPages allPages = snapshot.getValue(AllPages.class);
                    mPages.add(allPages);
                }

                IntrestsAdapter adapter = new IntrestsAdapter(getContext(), mPages);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
