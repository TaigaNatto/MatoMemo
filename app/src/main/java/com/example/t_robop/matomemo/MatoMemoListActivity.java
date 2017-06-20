package com.example.t_robop.matomemo;

import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MatoMemoListActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    TabLayout tabLayout;
    ViewPager viewPager;
    Viewpager_Adapter viewpagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matomemo_list);

        String[] tabs_names = getResources().getStringArray(R.array.tabs);  //string.xmlに書いてあるxmlファイルから配列取得
        tabLayout = (TabLayout)findViewById(R.id.tabs); //tabLayoutのid取得
        viewPager = (ViewPager)findViewById(R.id.pager);    //viewPagerのid取得
        viewpagerAdapter = new Viewpager_Adapter(getSupportFragmentManager(),tabs_names);   //作成したfragmentとviewPagerのadapterを作成
        viewPager.setAdapter(viewpagerAdapter); //viewPagerにfragmentをセット
        //viewPager.addOnAdapterChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));   //tabLayoutでも移動できるようにする  //よくわかんないCastしてるけどコメントアウトしたらできた
        tabLayout.setupWithViewPager(viewPager);    //tabLayoutとviewPagerの連携

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
