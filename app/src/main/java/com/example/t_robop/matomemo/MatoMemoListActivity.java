package com.example.t_robop.matomemo;

import android.content.Intent;
import android.support.design.widget.TabLayout;
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
import android.widget.Toast;

import io.realm.Realm;


public class MatoMemoListActivity extends AppCompatActivity {

    //Tab
    TabLayout tabLayout;
    ViewPager viewPager;
    Viewpager_Adapter viewPagerAdapter;
    String[] tabs_names;

    //NavigationDrawer内
    DrawerLayout drawerLayout;
    ListView drawerList;
    ArrayAdapter<String> arrayAdapter;

    Button matoMemoButton;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matomemo_list);

        //Database初期化
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //Toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(" ");  //教科名をセット
        setSupportActionBar(toolbar);

        //Drawerのid
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);

        //DrawerToggleの表示
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Drawer内のListViewのid
        drawerList = (ListView)findViewById(R.id.left_drawer);

        //Drawer内のArrayAdapterのインスタンス生成
        arrayAdapter=new ArrayAdapter<String>(this,R.layout.drawer_list_item);

        //Debug用Drawer内のList表示
        arrayAdapter.add("未分類");
        arrayAdapter.add("数学");
        arrayAdapter.add("国語");
        arrayAdapter.add("英語");

        //AdapterをListViewにセット
        drawerList.setAdapter(arrayAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //Drawer内のItemのクリック処理
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Debug用Drawer内のItem名をToast表示
                ListView list = (ListView)parent;
                String msg = "ItemClick : " + (String)list.getItemAtPosition(position);
                Toast toast = Toast.makeText(MatoMemoListActivity.this,msg,Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //画面下のButtonのid
        matoMemoButton = (Button)findViewById(R.id.MatoMemoButton);

        //tabLayoutとViewPager
        tabs_names = getResources().getStringArray(R.array.tabs);  //string.xmlに書いてあるxmlファイルから配列取得
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
