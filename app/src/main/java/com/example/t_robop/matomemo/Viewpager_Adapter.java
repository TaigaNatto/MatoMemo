package com.example.t_robop.matomemo;

import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by user on 2017/06/20.
 */

public class Viewpager_Adapter extends FragmentPagerAdapter {
    int numberOfTabs;

    String tabTitles[];

    public Viewpager_Adapter(android.support.v4.app.FragmentManager supportFragmentManager, String[] tabTitles) {
        super(supportFragmentManager);
        this.tabTitles = tabTitles;
        numberOfTabs = tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
