package com.ssproduction.shashank.network.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.PagesListAdapterFrag;
import com.ssproduction.shashank.network.YourPageActivity;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.AllPages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourPageFragment extends Fragment {

    private RelativeLayout creatPage;
    private FirebaseAuth mAuth;
    private String currentUser;
    private DatabaseReference pageDatabase, userDatabase;

    private RecyclerView myPageList;
    private List<AllPages> mPages;
    private PagesListAdapterFrag mAdapter;

    public YourPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_your_page, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        pageDatabase = FirebaseDatabase.getInstance().getReference().child("AllPages");
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabase.keepSynced(true);
        pageDatabase.keepSynced(true);

        creatPage = (RelativeLayout) view.findViewById(R.id.create_page_card);
        myPageList = (RecyclerView) view.findViewById(R.id.my_page_list);
        myPageList.setLayoutManager(new LinearLayoutManager(getActivity()));
        myPageList.setHasFixedSize(true);

        creatPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"Public", "Private"};
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Type Of Page");
                alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if ("Public".equals(items[which])){
                            InitiatePageCreating(currentUser);

                        }else if ("Private".equals(items[which])){
                            Toast.makeText(getActivity(), "Private not available yet", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                alertDialog.show();


            }
        });

        mPages = new ArrayList<>();
        readPages();

        return view;
    }

    private void readPages() {

        pageDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mPages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AllPages pages = snapshot.getValue(AllPages.class);
                    if (pages.getAdmin_id().equals(currentUser)){
                        mPages.add(pages);

                    }
                }
                PagesListAdapterFrag adapterFrag = new PagesListAdapterFrag(mPages, getContext());
                adapterFrag.notifyDataSetChanged();
                myPageList.setAdapter(adapterFrag);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void InitiatePageCreating(final String currentUser) {
        final String pushId = pageDatabase.push().getKey();

        Map pageMap = new HashMap();
        pageMap.put("push_id", pushId);
        pageMap.put("admin_id", currentUser);
        pageMap.put("title", "Page Title");
        pageMap.put("page_tagLine", "Page Tag");
        pageMap.put("page_about", "Page Description");
        pageMap.put("followers_count", "No Followers");
        pageMap.put("page_cover", "default");
        pageMap.put("ask_to_follow", false);
        pageMap.put("other_links", "Email/Website");
        pageMap.put("search", "default");

        pageDatabase.child(pushId).setValue(pageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(), "page created", Toast.LENGTH_SHORT).show();

                Intent createIntent = new Intent(getActivity(), YourPageActivity.class);
                createIntent.putExtra("pushId", pushId);
                createIntent.putExtra("page_click_by", currentUser);
                startActivity(createIntent);
            }
        });

    }

}
