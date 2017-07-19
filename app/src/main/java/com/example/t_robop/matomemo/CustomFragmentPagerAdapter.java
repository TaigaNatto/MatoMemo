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

public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {
    int numberOfTabs;

    String tabTitles[];

    Fragment memoFra;
    Fragment matoFra;

    public CustomFragmentPagerAdapter(android.support.v4.app.FragmentManager supportFragmentManager, String[] tabTitles) {
        super(supportFragmentManager);
        this.tabTitles = tabTitles;
        numberOfTabs = tabTitles.length;    //「メモ」と「まとめ」の2つ

        memoFra = memoFragment.newInstance();
        matoFra = matomeFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return memoFra;
                //return memoFragment.newInstance();

            case 1:
                return matoFra;
                //return new matomeFragment();
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
