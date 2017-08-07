package com.example.t_robop.matomemo;

import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

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

    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        super.destroyItem(container, position, object);

        if(position <= getCount()){
            FragmentManager manager = ((Fragment)object).getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove((Fragment)object);
            transaction.commit();
        }
    }

    //Fragmentの追加メソッド
    public List<Fragment> addFragment(Fragment ft){
        mFragments.add(ft);
        return null;
    }

    public void destroyAllItem(ViewPager pager){
        for(int i=0; i<getCount() - 1; i++){
            try{
                Object object = this.instantiateItem(pager,i);
                if(object != null){
                    destroyItem(pager,i,object);
                }
            }catch (Exception e){
                Log.d("destroyAllItem",e.toString());
            }
        }
    }
}
