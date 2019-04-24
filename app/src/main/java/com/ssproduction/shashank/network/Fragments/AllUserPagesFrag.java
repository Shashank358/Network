package com.ssproduction.shashank.network.Fragments;

import android.content.Context;
import android.net.Uri;
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
import com.ssproduction.shashank.network.Adapter.PagesListAdapterFrag;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.AllPages;

import java.util.ArrayList;
import java.util.List;

public class AllUserPagesFrag extends Fragment {

    private List<AllPages> mPages;
    private PagesListAdapterFrag adapterFrag;
    private DatabaseReference pagesDatabase;
    private RecyclerView allPagesList;


    public AllUserPagesFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle argument = getArguments();
        final String otherUser = argument.getString("user_id");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_user_pages, container, false);

        allPagesList = (RecyclerView) view.findViewById(R.id.user_all_pages_list_for_profile);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setStackFromEnd(true);
        allPagesList.setLayoutManager(manager);
        allPagesList.setHasFixedSize(true);

        mPages =  new ArrayList<>();
        pagesDatabase = FirebaseDatabase.getInstance().getReference().child("AllPages");

        pagesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPages.clear();;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AllPages pages = snapshot.getValue(AllPages.class);
                    if (otherUser.equals(pages.getAdmin_id())){
                        mPages.add(pages);

                    }
                }
                adapterFrag = new PagesListAdapterFrag(mPages, getContext());
                adapterFrag.notifyDataSetChanged();
                allPagesList.setAdapter(adapterFrag);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;

    }

}
