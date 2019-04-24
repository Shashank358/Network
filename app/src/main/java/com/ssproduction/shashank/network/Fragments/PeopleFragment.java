package com.ssproduction.shashank.network.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.AllUsersListAdapter;
import com.ssproduction.shashank.network.Adapter.PeoplePagerAdapter;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.SearchPageActivity;
import com.ssproduction.shashank.network.SearchUsersActivity;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {


    private Toolbar mToolbar;

    private ViewPager pager;
    private TabLayout tabs;


    public PeopleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.people_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle("All Users");

        pager = (ViewPager) view.findViewById(R.id.people_pager);
        PeoplePagerAdapter adapter = new PeoplePagerAdapter(getFragmentManager());
        pager.setAdapter(adapter);


        tabs = (TabLayout) view.findViewById(R.id.people_frag_tabs);
        tabs.setupWithViewPager(pager);

        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.people_frag_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int currentItem = pager.getCurrentItem();

        if (item.getItemId() == R.id.people_frag_search_item){
            if (currentItem == 0){
                Intent searchIntent = new Intent(getActivity(), SearchUsersActivity.class);
                startActivity(searchIntent);
            }
            else if (currentItem == 1){
                Intent searchIntent = new Intent(getActivity(), SearchPageActivity.class);
                startActivity(searchIntent);
            }
        }

        if (item.getItemId() == R.id.people_frag_setting_item){
            Toast.makeText(getActivity(), "setting", Toast.LENGTH_SHORT).show();
        }
        return true;

    }
}
