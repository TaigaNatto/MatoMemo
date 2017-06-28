package com.example.t_robop.matomemo;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MatoMemoListActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    Viewpager_Adapter viewPagerAdapter;
    Button matoMemoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matomemo_list);

        //Toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //DrawerToggle
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //NavigationView Listener
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            //NavigationDrawer内のメニュー選択時
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getGroupId()){
                    case R.id.menu_item1:
                        Log.d("Navigation_test","Item 1 Selected");
                        break;
                }

                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawerLayout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        //画面下のButton
        matoMemoButton = (Button)findViewById(R.id.MatoMemoButton);

        //tabLayoutとViewPager
        String[] tabs_names = getResources().getStringArray(R.array.tabs);  //string.xmlに書いてあるxmlファイルから配列取得
        tabLayout = (TabLayout)findViewById(R.id.tabs); //tabLayoutのid取得
        viewPager = (ViewPager)findViewById(R.id.pager);    //viewPagerのid取得

        viewPagerAdapter = new Viewpager_Adapter(getSupportFragmentManager(),tabs_names);   //作成したfragmentとviewPagerのadapterを作成
        viewPager.setAdapter(viewPagerAdapter); //viewPagerにfragmentをセット
        //viewPager.addOnAdapterChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));   //tabLayoutでも移動できるようにする  //よくわかんないCastしてるけどコメントアウトしたらできた

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {  //ページ切り替え時に呼び出されるメソッド

            }

            @Override
            public void onPageSelected(int position) {  //ページがpositionの番号になったとき呼ばれるメソッド
                switch (position){
                    case 0:
                        Log.d("position","onPageSelected"+ String.valueOf(position));
                        matoMemoButton.setText("メモを書く");
                        break;

                    case 1:
                        Log.d("position","onPageSelected"+ String.valueOf(position));
                        matoMemoButton.setText("まとめを作る");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {   //ページスクロール時に呼ばれるメソッド

            }

        });

        tabLayout.setupWithViewPager(viewPager);    //tabLayoutとviewPagerの連携

    }
}
