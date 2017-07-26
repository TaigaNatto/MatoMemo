package com.example.t_robop.matomemo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class FirstActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter arrayAdapter;
    ArrayList<Class> arrayList;

    Button button;

    /**********************************************/
    Class[] MatomemoClass={FolderCreateActivity.class,MatoMemoListActivity.class,GroupEditActivity.class,StartListActivity.class,RealmTestActivity.class,DBLogActivity.class,MatomeActivity.class,WritingActivity.class};
    /***　　　　　　　　　　　 ∩＿
     　　　　　　　　 　　　〈〈〈 ヽ
     　　 　　　　　　　　　 〈⊃ 　}
        　　　 /ﾆＹﾆヽ　　    |　　 |
     　　　 /（ ﾟ )( ﾟ ）ヽ　 !　　 !
     　　／::::⌒｀´⌒::::＼ |　　/    <ここにActivityを追加してね！
     　 |　,-）＿＿＿（-，|　／　　
     　　､　　|-┬-|　　／
      　/　＿　`ー'´　/
      　(＿＿＿）　　/
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        button = (Button) findViewById(R.id.button_first);
        button.setText("最新情報をGet!");

        listView = (ListView) findViewById(R.id.list);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        arrayList=new ArrayList<>();

        for (int i=0;i<MatomemoClass.length;i++){
            arrayList.add(MatomemoClass[i]);
            arrayAdapter.add(MatomemoClass[i].getName().toString());
        }
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(view.getContext(),arrayList.get(position));
                startActivity(intent);
            }
        });

    }

    public void intent_infomation(View v){
        Uri uri = Uri.parse("https://cleweb.tsc.u-tokai.ac.jp/campusweb/top.do;jsessionid=FAE25D92502F819EB34B87B4970C6A24");
        Intent i = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(i);
    }

}