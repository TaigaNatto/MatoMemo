package com.example.t_robop.matomemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_list);

        //レイアウトで作ったListViewをjavaで使えるようにする
        listView = (ListView) findViewById(R.id.list);
        //"arrayList"をArrayListでインスタンス化
        arrayList = new ArrayList<>();
        //arrayListに"memo"を追加
        arrayList.add(mibunrui);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        /*arrayList.add(memo);                                //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
        arrayList.add(memo2);                               //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
        arrayList.add(plusyo);                              //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
        arrayList.add(memowokakuyo);                        //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
        */

        arrayAdapter.add(arrayList.get(0));
        //arrayAdapter.add(arrayList.get(1));

        listView.setAdapter(arrayAdapter);


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

}
