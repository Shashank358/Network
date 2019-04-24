package com.ssproduction.shashank.network.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ssproduction.shashank.network.Pagers.ChatPager;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.SearchChatListActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsContainerFrag extends Fragment {

    private ViewPager viewPager;
    private ChatPager chatPager;
    private TabLayout tabLayout;
    private Toolbar mToolbar;




    public ChatsContainerFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats_container, container, false);

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        mToolbar = (Toolbar) view.findViewById(R.id.chat_frag_toolbar);
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle("Chats");

        viewPager = (ViewPager) view.findViewById(R.id.chats_container_pager);
        chatPager = new ChatPager(getFragmentManager());
        viewPager.setAdapter(chatPager);

        tabLayout = (TabLayout) view.findViewById(R.id.chat_frag_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.people_frag_search_item){
            Toast.makeText(getActivity(), "search", Toast.LENGTH_SHORT).show();
            Intent searchIntent = new Intent(getActivity(), SearchChatListActivity.class);
            startActivity(searchIntent);

        }else if (item.getItemId() == R.id.people_frag_setting_item){
            Toast.makeText(getActivity(), "setting", Toast.LENGTH_SHORT).show();
        }
        return true;

    }
}
