package com.example.t_robop.matomemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class StartListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> arrayList;

    String memo = "aaabbb";                                 //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
    String memo2 = "cccddd";                                //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
    String plusyo = "+";                                    //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
    String memowokakuyo = "memowokaku";                     //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです

    ArrayAdapter<String>arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_list);

        listView = (ListView)findViewById(R.id.list);
        arrayList = new ArrayList<>();
        arrayList.add(memo);                                //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
        arrayList.add(memo2);                               //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
        arrayList.add(plusyo);                              //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです
        arrayList.add(memowokakuyo);                        //ボタンを押したときの処理の確認用 ※実装とは直接関係ないです

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        arrayAdapter.add(arrayList.get(0));
        arrayAdapter.add(arrayList.get(1));

        listView.setAdapter(arrayAdapter);

    }
//↓↓↓↓↓↓↓↓ボタンを押したときの処理の確認用 ※実装とは直接関係ないです↓↓↓↓↓↓↓↓↓

    public void plus(View v) {
        arrayAdapter.add(arrayList.get(2));
        listView.setAdapter(arrayAdapter);
    }

    public void memowokaku(View v){
        arrayAdapter.add(arrayList.get(3));
        listView.setAdapter(arrayAdapter);
    }

}
