package com.ssproduction.shashank.network.Fragments.HomeFragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.AllPublishAdapter;
import com.ssproduction.shashank.network.Adapter.PagerAdapter.HomePagerAdapter;
import com.ssproduction.shashank.network.Adapter.PagesListAdapterFrag;
import com.ssproduction.shashank.network.PublishPrevActivity;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.AllPages;
import com.ssproduction.shashank.network.Utils.Publish;
import com.ssproduction.shashank.network.Utils.Users;
import com.ssproduction.shashank.network.YourPageActivity;

import java.util.ArrayList;
import java.util.List;

import im.ene.toro.widget.Container;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private String mCurrentUser;
    private Toolbar mToolbar;
    private FloatingActionButton addPublish;

    private ImageView cameraView;
    private ViewPager viewPager;
    private TabLayout homeTabs;
    private HomePagerAdapter pagerAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        AppCompatActivity activity = (AppCompatActivity)getActivity();
        mToolbar = (Toolbar) view.findViewById(R.id.home_toolbar);
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle("");

        addPublish = (FloatingActionButton) view.findViewById(R.id.add_publish_floating_btn);
        cameraView = (ImageView) view.findViewById(R.id.home_frag_camera_open_view);
        viewPager = (ViewPager) view.findViewById(R.id.all_publishes_container);
        homeTabs = (TabLayout) view.findViewById(R.id.all_publish_type_tabs);
        pagerAdapter = new HomePagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        homeTabs.setupWithViewPager(viewPager);


        addPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                final DatabaseReference pageDatabase = FirebaseDatabase.getInstance().getReference()
                        .child("AllPages");
                pageDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            AllPages pages = snapshot.getValue(AllPages.class);
                            if (pages.getAdmin_id().equals(mCurrentUser)){
                                String page_id = pages.getPush_id() ;

                                Intent pubIntent = new Intent(getActivity(), PublishPrevActivity.class);
                                pubIntent.putExtra("page_id", page_id);
                                startActivity(pubIntent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.home_frag_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.home_frag_add_post_item){


        }
        else if (item.getItemId() == R.id.home_frag_setting_menu){
            Toast.makeText(getActivity(), "setting", Toast.LENGTH_SHORT).show();
        }
        else if (item.getItemId() == R.id.home_frag_privacy_item){
            Toast.makeText(getActivity(), "privacy", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);

    }
}
