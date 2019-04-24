package com.ssproduction.shashank.network.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ssproduction.shashank.network.Fragments.AllUsersFragment;
import com.ssproduction.shashank.network.Fragments.IntrestsFragment;
import com.ssproduction.shashank.network.Fragments.YourPageFragment;

public class PeoplePagerAdapter extends FragmentStatePagerAdapter {

    public PeoplePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new AllUsersFragment();

            case 1:
                return new IntrestsFragment();

            case 2:
                return new YourPageFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Users";

            case 1:
                return "Channels";

            case 2:
                return "My Channel";

                default:
                    return null;
        }
    }
}
