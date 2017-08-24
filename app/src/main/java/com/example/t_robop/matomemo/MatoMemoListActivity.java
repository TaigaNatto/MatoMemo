package com.example.t_robop.matomemo;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.RecoverySystem;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MatoMemoListActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener{

    private CustomFragmentPagerAdapter customFragmentPagerAdapter;    //自作のViewPager用Adapter

    private ArrayAdapter<String> drawerArrayAdapter;

    private String nowSubjectName = "未分類";  //現在表示されている画面の教科名

    private Button matoMemoButton;  //WritingActivity、FolderCreateActivityに遷移するButton

    private Toolbar toolbar;    //ToolBar

    DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Menu menu;
    private SubMenu subjectGroupMenu;
    private SubMenu optionGroupMenu;

    ViewPager viewPager;
    TabLayout tabLayout;

    private Realm realm;    //Realm

    //Activityの初回起動時
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matomemo_list);

        //Database初期化
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //UI部品の取得
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        matoMemoButton = (Button) findViewById(R.id.MatoMemoButton);
        navigationView = (NavigationView)findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        //ListView drawerListView = (ListView) findViewById(R.id.left_drawer);
        String[] tabNames = getResources().getStringArray(R.array.tabs);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Toolbar表示
        toolbar.setTitle(nowSubjectName);  //intent元でタップされた教科名を設定
        setSupportActionBar(toolbar);

        //DrawerToggleの表示
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Drawer内のList初期化
        drawerArrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item);

        getFolderDataList(realm, drawerArrayAdapter);    //Databaseから教科(Folder)取得してdrawerArrayAdapterにセット    //ToDo データのgetとsetを分けてメソッド化する →　reloadいらなくなる

        customSubjectItemAddMenu(drawerArrayAdapter);
        navigationView.setNavigationItemSelectedListener(this);

        //Fragment操作準備
        customFragmentPagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(), tabNames);

        //newInstanceメソッドでAdapterにFragment追加
        customFragmentPagerAdapter.addFragment(MemoFragment.newInstance(nowSubjectName));
        customFragmentPagerAdapter.addFragment(MatomeFragment.newInstance(nowSubjectName));

        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(customFragmentPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.setBackgroundColor(Color.parseColor("#eeeeef"));
        tabLayout.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    //Activityが再度開始された時に呼ばれるコールバックメソッド
    @Override
    public void onRestart() {
        super.onRestart();

        reloadFolderDataList(realm, drawerArrayAdapter);     //DrawerArrayAdapterの更新
        reloadFragmentData(nowSubjectName);     //fragmentのListViewを更新
    }

    //メニューバーの作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);  //res\menu\optionsのlayoutを読み込む
        return true;
    }

    //メニューが選択されたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        //addしたときのIDで識別
        switch (item.getItemId()) {
            case R.id.tag_settings:
                intent = new Intent(this, TagEditActivity.class);   //TagEditActivityへIntent
                break;

            case R.id.editFolder:
                intent = new Intent(this, GroupEditActivity.class);  //GroupEditActivityへIntent
                break;

            default:
                break;

        }
        startActivity(intent);

        return true;
    }

    //NavigationDrawer内のItemクリック処理
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String clickedItem = item.toString();
        if(clickedItem.equals("教科編集")){
            Intent intent = new Intent(this, GroupEditActivity.class);   //GroupEditActivityにIntent
            startActivity(intent);
        }
        else if(clickedItem.equals("タグ編集")){
            Intent intent = new Intent(this, TagEditActivity.class);   //GroupEditActivityにIntent
            startActivity(intent);
        }
        else{

            nowSubjectName = clickedItem;

            toolbar.setTitle(nowSubjectName);

            reloadFragmentData(nowSubjectName);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        String buttonText = null;
        switch (position) {
            case 0:
                viewPager.setBackgroundColor(Color.parseColor("#eeeeef"));
                tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#d902ce7f"));
                buttonText = "メモる";
                matoMemoButton.setBackgroundColor(Color.parseColor("#7f9fff"));
                View view1 = this.matoMemoButton; // フェードイン・アウトさせたいViewを取得
                view1.setAlpha(0.0f);
                ObjectAnimator animation1 = ObjectAnimator.ofFloat(view1, "alpha", 1.0f);
                animation1.start();
                break;

            case 1:
                viewPager.setBackgroundColor(Color.parseColor("#efeeee"));
                tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ff9999"));
                buttonText = "マトメる";
                matoMemoButton.setBackgroundColor(Color.rgb(200,79,93));
                View view2 = this.matoMemoButton; // フェードイン・アウトさせたいViewを取得
                view2.setAlpha(0.0f);
                ObjectAnimator animation2 = ObjectAnimator.ofFloat(view2, "alpha", 1.0f);
                animation2.start();
                break;

            default:
                break;
        }
        matoMemoButton.setText(buttonText);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d("ASD",String.valueOf(state));
    }

    //データベースから教科取得
    public void getFolderDataList(Realm realm, ArrayAdapter<String> arrayAdapter) {
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();

        if (folderResults.size() != 0) {
            for (int i = 0; i < folderResults.size(); i++) {
                arrayAdapter.add(folderResults.get(i).getFolderName());    //全教科名をDrawerのAdapterに追加
            }
        }
    }

    //教科リストの更新
    public void reloadFolderDataList(Realm realm, ArrayAdapter<String> arrayAdapter) {
        arrayAdapter.clear();
        getFolderDataList(realm, arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        menu.clear();
        customSubjectItemAddMenu(arrayAdapter);
    }

    //Fragmentの更新
    public void reloadFragmentData(String subjectName) {
        int flag = 0;

        //Drawer内でタップされた教科名のメモリストを表示
        Fragment memoFragment = customFragmentPagerAdapter.getItem(0);    //CustomFragmentPagerAdapterのgetItemからfragment情報を取ってくる
        Fragment matomeFragment = customFragmentPagerAdapter.getItem(1);

        //GroupEditActivityで削除した教科が指定された場合、強制的に未分類フォルダに変える
        for (int i = 0; i < drawerArrayAdapter.getCount(); i++) {
            if (subjectName.equals(drawerArrayAdapter.getItem(i))) {
                flag = 1;
            }
        }

        if (flag == 0) {
            subjectName = "未分類";
            toolbar.setTitle("未分類");
        }


        if (memoFragment != null && memoFragment instanceof MemoFragment) {
            ((MemoFragment) memoFragment).reloadMemoData(subjectName);       //メモリストの更新
        }

        if (matomeFragment != null && matomeFragment instanceof MatomeFragment) {
            ((MatomeFragment) matomeFragment).reloadMatomeData(subjectName);     //まとめリストの更新
        }
    }

    private void customSubjectItemAddMenu(ArrayAdapter arrayAdapter){

        menu = navigationView.getMenu();
        subjectGroupMenu = menu.addSubMenu(0,0,0,"Subject");
        subjectGroupMenu.add(0,0,0,"未分類");

        optionGroupMenu = menu.addSubMenu(1,1,1,"Option");
        optionGroupMenu.add(1,0,0,"教科編集");
        optionGroupMenu.add(1,1,0,"タグ編集");

        for(int i=0; i<arrayAdapter.getCount(); i++){
            subjectGroupMenu.add(i+1,i+1,i+1,arrayAdapter.getItem(i).toString());
        }
    }

    //画面下のButton処理
    public void MatoMemoClick(View v) {      //ToDo メソッド名リファクター
        String buttonText = matoMemoButton.getText().toString();  //ButtonのTextを取得
        Intent intent = null;
        String modeKEY = "MODE";
        String subjectKEY = "SUBJECT NAME";

        switch (buttonText) {
            case "メモを書く":
                intent = new Intent(this, WritingActivity.class);    //WritingActivityにIntent
                intent.putExtra(modeKEY, -1);      //-1 : メモ新規作成
                intent.putExtra(subjectKEY, nowSubjectName);     //教科名受け渡し
                break;

            case "まとめを作る":
                intent = new Intent(this, FolderCreateActivity.class);    //FolderCreateActivityにIntent
                intent.putExtra(subjectKEY, nowSubjectName);
                break;

            default:
                break;
        }

        startActivity(intent);  //Intent!!!
    }

}
