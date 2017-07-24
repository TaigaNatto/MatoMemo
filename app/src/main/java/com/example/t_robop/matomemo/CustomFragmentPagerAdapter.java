package com.example.t_robop.matomemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/06/20.
 */
//ViewPagerのAdapter
public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {
    int numberOfTabs;   //tabの数

    String tabTitles[]; //tab上のテキスト

    private List<Fragment> mFragments = new ArrayList<>();  //Fragmentを入れる変数

    //コンストラクタ
    public CustomFragmentPagerAdapter(android.support.v4.app.FragmentManager supportFragmentManager, String[] tabTitles) {
        super(supportFragmentManager);
        this.tabTitles = tabTitles;
        numberOfTabs = tabTitles.length;    //「メモ」と「まとめ」の2つ

    }

    //Fragmentを取得するメソッド
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    //Fragmentの数を返すメソッド
    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return tabTitles[position];
    }

    //Fragmentの追加メソッド
    public void addFragment(Fragment ft){
        mFragments.add(ft);
    }
}
