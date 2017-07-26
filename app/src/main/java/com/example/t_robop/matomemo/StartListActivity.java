package com.example.t_robop.matomemo;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class StartListActivity extends AppCompatActivity {

    //ArrayListのString型でarrayListを作成
    ArrayList<String> arrayList;
    //ListViewでlistViewを作成
    ListView listView;
    //ArrayAdapterのString型でarrayAdapterを作成
    ArrayAdapter<String> arrayAdapter;

    String text = "未分類";
    String memo2 = "cccddd";                                //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
    String plusyo = "+";                                    //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
    String memowokakuyo = "memowokaku";                     //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
    //ArrayList<Integer> arrayNum;

    /*** 神 ***/
    Realm realm;
    /*** ** ***/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_list);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);


        //レイアウトで作ったListViewをjavaで使えるようにする
        listView = (ListView) findViewById(R.id.list);
        //"arrayList"をArrayListでインスタンス化
        arrayList = new ArrayList<>();

        arrayList.add("groupName");
        //arrayAdapterをインスタンス化
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        arrayAdapter.add("未分類");
        //arrayAdapterをlistViewに入れる
        listView.setAdapter(arrayAdapter);

        /***これ必須だからみんな書いて***/
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        /*******************/
        ///フォルダのデータが欲しいとき！///
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();
        /***使い方は↑のメモと同じ***/
        for(int i=0;i<folderResults.size();i++){
            String text = folderResults.get(i).getFolderName();
            //arrayListをtextで初期化
            arrayList.set(0, text);
            //arrayListをarrayAdapterに追加する
            arrayAdapter.add(arrayList.get(0));
            //arrayAdapterをlistViewに入れる
            listView.setAdapter(arrayAdapter);
        }

        arrayList.set(0,"Listをタップして下さい");

        /*arrayList.add(memo);                                //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
        arrayList.add(memo2);                               //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
        arrayList.add(plusyo);                              //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
        arrayList.add(memowokakuyo);                        //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
        */


        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    //listViewがタップされたときに実行
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        text = (String)listView.getItemAtPosition(position);

                        Intent intent=new Intent(getApplicationContext(),MatoMemoListActivity.class);
                        intent.putExtra("folder",text);

                        startActivity(intent);
                    }


                });

        Intent intent = new Intent(this.getApplicationContext(), GroupEditActivity.class);

    }

//↓↓↓↓↓↓↓↓ボタンを押したときの処理の確認用 ※実装とは直接関係ないです↓↓↓↓↓↓↓↓↓

    public void Intent(View v) {
        Intent intent=new Intent(this,GroupEditActivity.class);

        startActivity(intent);
    }

    public void plus(View v) {
        arrayAdapter.add(arrayList.get(0));
        listView.setAdapter(arrayAdapter);
    }

    public void memowokaku(View v){
        arrayAdapter.add(arrayList.get(0));
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            // なんらかの処理
            //Class activityName = getCallingActivity().getClass();

            //Intent intent=new Intent(getApplicationContext(),GroupEditActivity.class);
            //startActivity(intent);


            int activityNum = 0;
            Intent intent = getIntent();
            activityNum = intent.getIntExtra("Date",0);

            if(activityNum == 1) {
                Intent intents = new Intent(this, GroupEditActivity.class);
                startActivity(intents);
            }else {
                Intent intents = new Intent(this, FirstActivity.class);
                startActivity(intents);
            }


            return true;
        }
        return false;
    }

}
