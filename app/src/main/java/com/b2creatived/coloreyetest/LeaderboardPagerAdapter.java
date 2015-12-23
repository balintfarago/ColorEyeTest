package com.b2creatived.coloreyetest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class LeaderboardPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public LeaderboardPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                LeaderboardFragment1 tab1 = new LeaderboardFragment1();
                return tab1;
            case 1:
                LeaderboardFragment2 tab2 = new LeaderboardFragment2();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
