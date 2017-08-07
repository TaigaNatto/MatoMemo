package com.example.t_robop.matomemo;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MatoMemoListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener {

    //Tab
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CustomFragmentPagerAdapter matomemoFragmentPagerAdapter;    //自作のViewPager用Adapter
    String[] tabNames;    //tabの名前 /value/string.xml/string-arrayの"tabs"を参照

    //NavigationDrawer内
    DrawerLayout drawerLayout;
    ListView drawerListView;
    ArrayAdapter<String> drawerArrayAdapter;

    //画面下のButton
    Button matoMemoButton;

    //ToolBar
    Toolbar toolbar;

    //Realm
    Realm realm;

    //StartListActivityから受け取った教科名
    //String subjectName = null;

    //Acitivityの初回起動時
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matomemo_list);

        //Database初期化
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //UI部品の取得
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        drawerListView = (ListView)findViewById(R.id.left_drawer);
        matoMemoButton = (Button)findViewById(R.id.MatoMemoButton);
        tabNames = getResources().getStringArray(R.array.tabs);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        viewPager = (ViewPager)findViewById(R.id.pager);

        //StartListActivityからのIntent受取
        Intent intent = getIntent();
        String subjectName = intent.getStringExtra("folder");   //ToDo 教科名空欄の場合の例外処理


        //Toolbar表示
        toolbar.setTitle(subjectName);  //intent元でタップされた教科名を設定
        setSupportActionBar(toolbar);


        //DrawerToggleの表示
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        drawerArrayAdapter =new ArrayAdapter<>(this,R.layout.drawer_list_item);

        //Drawer内のList初期表示
        drawerArrayAdapter.add("未分類");

        viewPager.addOnPageChangeListener(this);


        getFolderDataList();    //Databaseから教科(Folder)取得してdrawerArrayAdapterにセット


        drawerListView.setAdapter(drawerArrayAdapter);

        drawerListView.setOnItemClickListener(this);


        //Fragment操作準備
        matomemoFragmentPagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(),tabNames);

        //newInstanceメソッドでAdapterにFragment追加
        matomemoFragmentPagerAdapter.addFragment(MemoFragment.newInstance(subjectName));
        matomemoFragmentPagerAdapter.addFragment(MatomeFragment.newInstance(subjectName));


        viewPager.setAdapter(matomemoFragmentPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    //Activityが再度開始された時
    @Override
    public void onRestart(){
        super.onRestart();
        reloadDrawerList();     //DrawerArrayAdapterの更新
    }

    //メニューバーの作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options,menu);  //res\menu\optionsのlayoutを読み込む
        return true;
    }

    //ToDo Intent先の作成とIntent処理の追加
    //メニューが選択されたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        //addしたときのIDで識別
        switch(item.getItemId()) {
            case R.id.tag_settings:
                intent = new Intent(this, TagEditActivity.class);   //TagEditActivityへIntent
                break;

            /*
            case R.id.important_setting:
                Log.d("menu","重要度設定へ");     //ImportantEditActivityへIntent
                break;
             */

            case R.id.editFolder:
                intent = new Intent(this, GroupEditActivity.class);  //GroupEditActivityへIntent
                break;

            default:
                break;

        }
        startActivity(intent);

        return true;
    }

    //ToDo 非同期処理　(無くても動くけど...)
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //動的に追加された教科Listのクリック処理
        //drawerArrayAdapterに教科Listがある
        String item = (String)adapterView.getItemAtPosition(position);   //クリックしたpositionからItem(教科名)を取得

        //教科クリックしたらToolBar.setTitleで教科名をセット
        toolbar.setTitle(item);


        //Drawer内でタップされた教科名のメモを表示
        Fragment fragmentPage0 = matomemoFragmentPagerAdapter.getItem(0);    //CustomFragmentPagerAdapterのgetItemからfragment情報を取ってくる
        Fragment fragmentPage1 = matomemoFragmentPagerAdapter.getItem(1);

        if(fragmentPage0 != null && fragmentPage0 instanceof MemoFragment){
            ((MemoFragment)fragmentPage0).reloadMemoData(item);
        }

        if(fragmentPage1 != null && fragmentPage1 instanceof MatomeFragment){
            //((MatomeFragment)fragmentPage1).reloadMatomeData(item);           //ToDo 落ちる
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position){
            case 0:
                matoMemoButton.setText(R.string.WriteMemo); //"メモを書く"をButtonのTextにセット
                break;

            case 1:
                matoMemoButton.setText(R.string.MakeMatome);    //"まとめを作る"をButtonのTextにセット
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //データベースから教科取得
    public void getFolderDataList(){
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();

        for(int i=0; i<folderResults.size(); i++){
            drawerArrayAdapter.add(folderResults.get(i).getFolderName());    //全教科名をDrawerのAdapterに追加
        }
        //ToDo arrayListに入れて返す
    }

    //Drawer内の教科リストの更新
    private void reloadDrawerList(){
        drawerArrayAdapter.clear();
        drawerArrayAdapter.add("未分類");
        getFolderDataList();
        drawerArrayAdapter.notifyDataSetChanged();
    }

    //画面下のButton処理
    public void MatoMemoClick(View v){
        String buttonText = (String) matoMemoButton.getText();  //ButtonのTextを取得
        Intent intent = null;

        switch (buttonText){
            case "メモを書く":
                intent = new Intent(this,WritingActivity.class);    //WritingActivityにIntent
                break;

            case "まとめを作る":
                intent = new Intent(this,FolderCreateActivity.class);    //FolderCreateActivityにIntent
                break;
        }

        startActivity(intent);  //Intent!!!

    }

    //Drawer内のButtonクリック処理
    public void editFolder(View v){
        Intent intent = new Intent(this,GroupEditActivity.class);   //GroupEditActivityにIntent
        intent.putExtra("Writing Status",0);    //数値受け渡し　1: メモ確認　0: 新規作成   //ここでは0を送る
        startActivity(intent);
    }
}
