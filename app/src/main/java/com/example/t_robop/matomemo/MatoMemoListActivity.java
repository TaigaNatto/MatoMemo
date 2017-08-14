package com.example.t_robop.matomemo;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MatoMemoListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener {

    private CustomFragmentPagerAdapter customFragmentPagerAdapter;    //自作のViewPager用Adapter

    private ArrayAdapter<String> drawerArrayAdapter;

    private String nowSubjectName = "未分類";  //現在表示されている画面の教科名

    private Button matoMemoButton;  //WritingActivity、FolderCreateActivityに遷移するButton

    private Toolbar toolbar;    //ToolBar

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
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        matoMemoButton = (Button)findViewById(R.id.MatoMemoButton);
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        ListView drawerListView = (ListView)findViewById(R.id.left_drawer);
        String[] tabNames = getResources().getStringArray(R.array.tabs);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        ViewPager viewPager = (ViewPager)findViewById(R.id.pager);

        //Toolbar表示
        toolbar.setTitle(nowSubjectName);  //intent元でタップされた教科名を設定
        setSupportActionBar(toolbar);


        //DrawerToggleの表示
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Drawer内のList初期化
        drawerArrayAdapter =new ArrayAdapter<>(this,R.layout.drawer_list_item);
        drawerArrayAdapter.add("未分類");


        getFolderDataList(realm,drawerArrayAdapter);    //Databaseから教科(Folder)取得してdrawerArrayAdapterにセット    //ToDo データのgetとsetを分けてメソッド化する →　reloadいらなくなる


        drawerListView.setAdapter(drawerArrayAdapter);
        drawerListView.setOnItemClickListener(this);


        //Fragment操作準備
        customFragmentPagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(),tabNames);

        //newInstanceメソッドでAdapterにFragment追加
        customFragmentPagerAdapter.addFragment(MemoFragment.newInstance(nowSubjectName));
        customFragmentPagerAdapter.addFragment(MatomeFragment.newInstance(nowSubjectName));

        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(customFragmentPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    //ToDo GroupEditActivityで教科削除して戻ったときに、消された教科の情報が残っている
    //Activityが再度開始された時
    @Override
    public void onRestart(){
        super.onRestart();
        reloadFolderDataList(realm,drawerArrayAdapter);     //DrawerArrayAdapterの更新
        reloadFragmentData(nowSubjectName);     //fragmentのListViewを更新
    }

    //メニューバーの作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options,menu);  //res\menu\optionsのlayoutを読み込む
        return true;
    }

    //メニューが選択されたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        //addしたときのIDで識別
        switch(item.getItemId()) {
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

    //ToDo 非同期処理　(無くても動くけど...)
    //DrawerListViewのクリック処理
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //動的に追加された教科Listのクリック処理
        //drawerArrayAdapterに教科Listがある
        nowSubjectName = (String)adapterView.getItemAtPosition(position);   //クリックしたpositionからItem(教科名)を取得

        //教科クリックしたらToolBar.setTitleで教科名をセット
        toolbar.setTitle(nowSubjectName);

        reloadFragmentData(nowSubjectName);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        String buttonText = null;
        switch (position){
            case 0:
                buttonText = "メモを書く";
                break;

            case 1:
                buttonText = "まとめを作る";
                break;

            default:
                break;
        }
        matoMemoButton.setText(buttonText);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //データベースから教科取得
    public void getFolderDataList(Realm realm, ArrayAdapter<String> arrayAdapter){
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();

        if(folderResults.size() != 0){
            for(int i=0; i<folderResults.size(); i++){
                arrayAdapter.add(folderResults.get(i).getFolderName());    //全教科名をDrawerのAdapterに追加
            }
        }
    }

    //教科リストの更新
    public void reloadFolderDataList(Realm realm, ArrayAdapter<String> arrayAdapter){
        arrayAdapter.clear();
        arrayAdapter.add("未分類");
        getFolderDataList(realm,arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    //Fragmentの更新
    public void reloadFragmentData(String subjectName){
        int flag = 0;

        //Drawer内でタップされた教科名のメモリストを表示
        Fragment memoFragment = customFragmentPagerAdapter.getItem(0);    //CustomFragmentPagerAdapterのgetItemからfragment情報を取ってくる
        Fragment matomeFragment = customFragmentPagerAdapter.getItem(1);

        //GroupEditActivityで削除した教科が指定された場合、強制的に未分類フォルダに変える
        for(int i=0; i<drawerArrayAdapter.getCount(); i++){
            if(subjectName.equals(drawerArrayAdapter.getItem(i))){
                flag = 1;
            }
        }

        if(flag == 0){
            subjectName = "未分類";
            toolbar.setTitle("未分類");
        }


        if(memoFragment != null && memoFragment instanceof MemoFragment){
            ((MemoFragment)memoFragment).reloadMemoData(subjectName);       //メモリストの更新
        }

        if(matomeFragment != null && matomeFragment instanceof MatomeFragment){
            ((MatomeFragment)matomeFragment).reloadMatomeData(subjectName);     //まとめリストの更新
        }
    }

    //画面下のButton処理
    public void MatoMemoClick(View v){      //ToDo メソッド名リファクター
        String buttonText = (String) matoMemoButton.getText();  //ButtonのTextを取得
        Intent intent = null;
        String modeKEY = "MODE";
        String subjectKEY = "SUBJECT NAME";

        switch (buttonText){
            case "メモを書く":
                intent = new Intent(this,WritingActivity.class);    //WritingActivityにIntent
                intent.putExtra(modeKEY,-1);      //数値受け渡し　id: メモ確認　-1: 新規作成   //ここでは-1を送る
                intent.putExtra(subjectKEY,nowSubjectName);     //教科名受け渡し
                break;

            case "まとめを作る":
                intent = new Intent(this,FolderCreateActivity.class);    //FolderCreateActivityにIntent
                intent.putExtra(subjectKEY,nowSubjectName);
                break;

            default:
                break;
        }

        startActivity(intent);  //Intent!!!
    }

    //Drawer内のButtonクリック処理
    public void intentEditFolder(View v){
        Intent intent = new Intent(this,GroupEditActivity.class);   //GroupEditActivityにIntent
        startActivity(intent);
    }

}
