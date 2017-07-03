package com.example.t_robop.matomemo;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MathTestActivity extends AppCompatActivity {

    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayMainAdapter;
    ListView mainList;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_test);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mainList=(ListView)findViewById(R.id.list);

        arrayAdapter=new ArrayAdapter<String>(this,R.layout.drawer_list_item);
        arrayMainAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        arrayAdapter.add("aka");
        arrayAdapter.add("kiiro");
        arrayAdapter.add("midori");

        // Set the adapter for the list view
        mDrawerList.setAdapter(arrayAdapter);
        mainList.setAdapter(arrayMainAdapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                arrayMainAdapter.add(arrayAdapter.getItem(position));
                mainList.setAdapter(arrayMainAdapter);
            }
        });

    }

    public void add(View v){
        arrayAdapter.add("huetayo");
        mDrawerList.setAdapter(arrayAdapter);
    }
}
