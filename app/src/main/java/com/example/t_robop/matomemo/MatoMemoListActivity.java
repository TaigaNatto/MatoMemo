package com.example.t_robop.matomemo;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MatoMemoListActivity extends AppCompatActivity {

    //Tab
    TabLayout tabLayout;
    ViewPager viewPager;
    CustomFragmentPagerAdapter matomemoFragmentPagerAdapter;    //自作のFragment用
    String[] tabs_names;    //tabの名前 /value/string.xml/string-arrayの"tabs"を参照

    //NavigationDrawer内
    DrawerLayout drawerLayout;
    ListView drawerListView;
    ArrayAdapter<String> drawerArrayAdapter;

    //Fragment
    FragmentManager manager;

    Button matoMemoButton;

    Toolbar toolbar;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matomemo_list);

        //Database初期化
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //Debug用Database設定
        SetFolderDataTest();

        //Toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("未分類");  //教科名をセット
        setSupportActionBar(toolbar);

        //Drawerのid
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);

        //DrawerToggleの表示
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Drawer内のListViewのid
        drawerListView = (ListView)findViewById(R.id.left_drawer);

        //Drawer内のArrayAdapterのインスタンス生成
        drawerArrayAdapter =new ArrayAdapter<String>(this,R.layout.drawer_list_item);

        //Debug用Drawer内のList表示
        drawerArrayAdapter.add("未分類");

        //Databaseから教科(Folder)取得
        ArrayList<String> SubjectName = this.GetFolderDataTest();
        for(int i = 0; i<SubjectName.size(); i++){
            drawerArrayAdapter.add(SubjectName.get(i)); //DatabaseからGetしてきた教科名をDrawerにセット
        }

        //AdapterをListViewにセット
        drawerListView.setAdapter(drawerArrayAdapter);

        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //Drawer内のItemのクリック処理
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //ToDo
                //動的に追加された教科Listのクリック処理
                //drawerArrayAdapterに教科Listがある
                //処理内容：　教科クリックしたらToolBar.setTitleで教科名をセット

                //CustomFragmentPagerAdapterのgetItemからfragment情報を取ってきて、memoFragmentのcallFromOutメソッドを呼び出す
                Fragment fragment = matomemoFragmentPagerAdapter.getItem(0);
                if(fragment != null && fragment instanceof memoFragment){
                    ((memoFragment)fragment).callFromOut();
                }
            }
        });

        //画面下のButtonのid
        matoMemoButton = (Button)findViewById(R.id.MatoMemoButton);

        //tabLayoutとViewPager
        tabs_names = getResources().getStringArray(R.array.tabs);  //string.xmlに書いてあるxmlファイルから配列取得
        tabLayout = (TabLayout)findViewById(R.id.tabs); //tabLayoutのid取得
        viewPager = (ViewPager)findViewById(R.id.pager);    //viewPagerのid取得

        manager = getSupportFragmentManager();  //Fragmentの取得
        matomemoFragmentPagerAdapter = new CustomFragmentPagerAdapter(manager,tabs_names);  //自作のFragment用Adapterにmanagerを入れる
        viewPager.setAdapter(matomemoFragmentPagerAdapter); //Fragment用AdapterをviewPagerに入れる

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {  //ページ切り替え時に呼び出されるメソッド

            }

            @Override
            public void onPageSelected(int position) {  //ページがpositionの番号になったとき呼ばれるメソッド
                switch (position){
                    case 0:
                        Log.d("position","onPageSelected"+ String.valueOf(position));
                        matoMemoButton.setText(R.string.WriteMemo); //"メモを書く"をButtonのTextにセット
                        break;

                    case 1:
                        Log.d("position","onPageSelected"+ String.valueOf(position));
                        matoMemoButton.setText(R.string.MakeMatome);    //"まとめを作る"をButtonのTextにセット
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {   //ページスクロール時に呼ばれるメソッド

            }

        });

        tabLayout.setupWithViewPager(viewPager);    //tabLayoutとviewPagerの連携

    }

    //Debug用データベース設定    教科セット
    public void SetFolderDataTest(){
        realm.beginTransaction();   //transaction開始
        RealmFolderEntity testFolder = realm.createObject(RealmFolderEntity.class); //Instance作成

        //追加する教科
        testFolder.setFolderName("数学");

        //ToDo
        //setFolderNameだと逐一上書きされてしまうので、ArrayListでSetするとかする必要あり

        realm.commitTransaction();
    }

    //Debug用データベース設定　教科取得
    public ArrayList GetFolderDataTest(){
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();

        ArrayList<String> arrayList = new ArrayList<>();
        for(int i=0; i<folderResults.size(); i++){
            arrayList.add(folderResults.get(i).getFolderName());    //全教科名をarrayListに追加
        }

        return arrayList;   //arrayListを返す
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
        startActivity(intent);
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
        switch(item.getItemId())
        {
            case R.id.tag_settings:
                Log.d("menu","タグ設定へ");  //TagEditActivityへIntent
                break;

            case R.id.important_setting:
                Log.d("menu","重要度設定へ");     //ImportantEditActivityへIntent
                break;

            case R.id.editFolder:
                intent = new Intent(this,GroupEditActivity.class);  //GroupEditActivityへIntent
                break;

        }
        startActivity(intent);

        return true;
    }
}
