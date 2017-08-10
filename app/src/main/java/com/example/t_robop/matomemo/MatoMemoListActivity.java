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

    private String nowSubjectName = "未分類";  //現在表示されている画面の教科名

    //画面下のButton
    Button matoMemoButton;

    //ToolBar
    Toolbar toolbar;

    //Realm
    Realm realm;

    //StartListActivityから受け取った教科名
    //String subjectName = null;

    //ToDo 処理種類によってonCreate内の順序を変更
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

        /*
        //ToDo intent処理なので別メソッド化
        //StartListActivityからのIntent受取
        Intent intent = getIntent();
        nowSubjectName = intent.getStringExtra("folder");   //ToDo 教科名空欄の場合の例外処理
        */

        //Toolbar表示
        toolbar.setTitle(nowSubjectName);  //intent元でタップされた教科名を設定
        setSupportActionBar(toolbar);


        //DrawerToggleの表示
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        drawerArrayAdapter =new ArrayAdapter<>(this,R.layout.drawer_list_item);

        //Drawer内のList初期表示
        drawerArrayAdapter.add("未分類");

        viewPager.addOnPageChangeListener(this);


        getFolderDataList(realm,drawerArrayAdapter);    //Databaseから教科(Folder)取得してdrawerArrayAdapterにセット    //ToDo データのgetとsetを分けてメソッド化する →　reloadいらなくなる


        drawerListView.setAdapter(drawerArrayAdapter);

        drawerListView.setOnItemClickListener(this);


        //Fragment操作準備
        matomemoFragmentPagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(),tabNames);

        //newInstanceメソッドでAdapterにFragment追加
        matomemoFragmentPagerAdapter.addFragment(MemoFragment.newInstance(nowSubjectName));
        matomemoFragmentPagerAdapter.addFragment(MatomeFragment.newInstance(nowSubjectName));


        viewPager.setAdapter(matomemoFragmentPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    //Activityが再度開始された時
    @Override
    public void onRestart(){
        super.onRestart();
        reloadDrawerList(realm,drawerArrayAdapter);     //DrawerArrayAdapterの更新
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
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //動的に追加された教科Listのクリック処理
        //drawerArrayAdapterに教科Listがある
        String item = (String)adapterView.getItemAtPosition(position);   //クリックしたpositionからItem(教科名)を取得     //ToDo 変数itemをリファクター

        nowSubjectName = item;

        //教科クリックしたらToolBar.setTitleで教科名をセット
        toolbar.setTitle(item);

        reloadFragmentData(item);
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


        //ToDo arrayListに入れて返す
    }

    //Drawer内の教科リストの更新
    public void reloadDrawerList(Realm realm, ArrayAdapter<String> arrayAdapter){
        arrayAdapter.clear();
        arrayAdapter.add("未分類");
        getFolderDataList(realm,arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    public void reloadFragmentData(String subjectName){
        //Drawer内でタップされた教科名のメモリストを表示
        Fragment fragmentPage0 = matomemoFragmentPagerAdapter.getItem(0);    //CustomFragmentPagerAdapterのgetItemからfragment情報を取ってくる     //ToDo 変数fragmentPageをリファクター
        Fragment fragmentPage1 = matomemoFragmentPagerAdapter.getItem(1);

        if(fragmentPage0 != null && fragmentPage0 instanceof MemoFragment){
            ((MemoFragment)fragmentPage0).reloadMemoData(subjectName);
        }

        if(fragmentPage1 != null && fragmentPage1 instanceof MatomeFragment){
            ((MatomeFragment)fragmentPage1).reloadMatomeData(subjectName);
        }
    }

    //画面下のButton処理
    public void MatoMemoClick(View v){      //ToDo メソッド名リファクター
        String buttonText = (String) matoMemoButton.getText();  //ButtonのTextを取得    //ToDo 変数名リファクター
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
        }

        startActivity(intent);  //Intent!!!
    }

    //Drawer内のButtonクリック処理
    public void intentEditFolder(View v){     //ToDo メソッド名リファクター

        Intent intent = new Intent(this,GroupEditActivity.class);   //GroupEditActivityにIntent
        startActivity(intent);
    }

}
