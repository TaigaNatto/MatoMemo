package com.example.t_robop.matomemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class FirstActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter arrayAdapter;
    ArrayList<Class> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        listView=(ListView)findViewById(R.id.list);
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        arrayList=new ArrayList<Class>();

        arrayList.add(FirstActivity.class);
    }
}
