package com.example.t_robop.matomemo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

public class MatoMemoListActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {

    TabLayout tabLayout;
    ViewPager viewPager;
    Viewpager_Adapter viewpagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matomemo_list);

        association();
        tabViewSetting();

    }

    //xmlとの関連付けや初期化
    void association(){
        String[] tabs_names = getResources().getStringArray(R.array.tabs);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewpagerAdapter = new Viewpager_Adapter(getSupportFragmentManager(),tabs_names);
    }

    void tabViewSetting(){
        //Fragmentの管理
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return PageFragment.newInstance(position + 1);
            }

            @Override
            public CharSequence getPageTitle(int position){
                switch (position){
                    case 0:
                        return getString(R.string.tab_title1);

                    case 1:
                        return getString(R.string.tab_title2);

                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        viewPager.setAdapter(adapter);
        viewPager.addOnAdapterChangeListener(this);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {      //ページスクロール中に呼び出されるメソッド

    }

    @Override
    public void onPageSelected(int position) {      //ページが切り替わった時に呼び出されるメソッド

    }

    @Override
    public void onPageScrollStateChanged(int state) {   //画面スクロール状態が変化した時に呼び出されるメソッド

    }

    public static class PageFragment extends Fragment {

        ListView listView = null;

        //コンストラクタ
        public PageFragment(){
            //Fragmentを継承したクラスのコンストラクタは空にする
        }

        //fragmentの内容
        public static PageFragment newInstance(int page){
            Bundle args = new Bundle();
            args.putInt("page",page);
            PageFragment fragment = new PageFragment();
            fragment.setArguments(args);

            return fragment;
        }
    }

}
