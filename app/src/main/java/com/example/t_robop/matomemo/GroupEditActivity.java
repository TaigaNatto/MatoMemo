package com.example.t_robop.matomemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class GroupEditActivity extends AppCompatActivity {

    ArrayList<String> mibunruiList;
    ArrayList<String> arrayList;
    ListView listView;
    ListView mListView;

    String mibunrui = "未分類";
    String memo1 = "数学";
    String memo2 = "英語";

    ArrayAdapter<String>mibunruiAdapter;
    ArrayAdapter<String>arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        mListView = (ListView)findViewById(R.id.list);
        listView = (ListView)findViewById(R.id.list_item);

        mibunruiList = new ArrayList<>();
        mibunruiList.add(mibunrui);

        arrayList = new ArrayList<>();
        arrayList.add(memo1);
        arrayList.add(memo2);

        mibunruiAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_multiple_choice);

        mibunruiAdapter.add(mibunruiList.get(0));
        mListView.setAdapter(mibunruiAdapter);

        arrayAdapter.add(arrayList.get(0));
        listView.setAdapter(arrayAdapter);

    }

    public void plus(View v){
        arrayAdapter.add(arrayList.get(1));
        listView.setAdapter(arrayAdapter);

    }


}



