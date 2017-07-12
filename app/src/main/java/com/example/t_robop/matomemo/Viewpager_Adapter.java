package com.example.t_robop.matomemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by user on 2017/06/20.
 */

public class Viewpager_Adapter extends FragmentPagerAdapter {
    int numberOfTabs;

    String tabTitles[];

    public Viewpager_Adapter(android.support.v4.app.FragmentManager supportFragmentManager, String[] tabTitles) {
        super(supportFragmentManager);
        this.tabTitles = tabTitles;
        numberOfTabs = tabTitles.length;    //「メモ」と「まとめ」の2つ
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new memoFragment();

            case 1:
                return new matomeFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return tabTitles[position];
    }
}
