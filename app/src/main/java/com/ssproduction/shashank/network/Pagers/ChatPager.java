package com.ssproduction.shashank.network.Pagers;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ssproduction.shashank.network.Fragments.ChatFragment;
import com.ssproduction.shashank.network.Fragments.GroupChatFrag;

public class ChatPager extends FragmentStatePagerAdapter {

    public ChatPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new ChatFragment();

            case 1:
                return new GroupChatFrag();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Personal";

            case 1:
                return "Group";

            default:
                return null;
        }
    }
}
