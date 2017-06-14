package com.example.t_robop.matomemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class memo_tab extends AppCompatActivity {

    String[] texts = { "aaa", "bbb"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_tab);



        listSet();

    }

    void listSet(){
        ListView listView = (ListView)findViewById(R.id.memo);
        setContentView(listView);

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.activity_memo_tab, texts);

        listView.setAdapter(arrayAdapter);
    }

}
