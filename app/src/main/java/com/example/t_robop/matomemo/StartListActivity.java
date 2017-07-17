package com.example.t_robop.matomemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    String mibunrui = "未分類";
    String memo = "aaabbb";                                 //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
    String memo2 = "cccddd";                                //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
    String plusyo = "+";                                    //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
    String memowokakuyo = "memowokaku";                     //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです

    /*** 神 ***/
    Realm realm;
    /*** ** ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_list);

        //レイアウトで作ったListViewをjavaで使えるようにする
        listView = (ListView) findViewById(R.id.list);
        //"arrayList"をArrayListでインスタンス化
        arrayList = new ArrayList<>();

        arrayList.add("groupName");
        //arrayAdapterをインスタンス化
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

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

                        mibunrui = position + "番目のアイテムがクリックされました";
                        arrayList.set(0,mibunrui);
                    }


                });
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

            Intent intent=new Intent(this,FirstActivity.class);
            startActivity(intent);

            return true;
        }
        return false;
    }
}
