package com.example.t_robop.matomemo;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class GroupEditActivity extends AppCompatActivity {

    ArrayList<String> mibunruiList;
    ArrayList<String> arrayList;
    ListView listView;
    ListView mListView;

    String mibunrui = "未分類";
    String memo = "数学";
    EditText editView;

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
        arrayList.add(memo);

        mibunruiAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_multiple_choice);


        mibunruiAdapter.add(mibunruiList.get(0));
        mListView.setAdapter(mibunruiAdapter);

   /*     Button button = (Button)findViewById(R.id.button1);                 //id指定が分からない動的にボタンを増やした場合のid
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListView listView = (ListView)findViewById(R.id.list_item);
                SparseBooleanArray checked = listView.getCheckedItemPositions();

                StringBuilder sb = new StringBuilder();
                for(int i = 0;i<checked.size();i++){
                    if(checked.valueAt(i)) {

                        BIND_ALLOW_OOM_MANAGEMENT]
                    }


                }
            }
        });
*/
        checkCheckBox();

    }

    public void plus(View v){

        editView = new EditText(this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("グループの名前");
        dialog.setView(editView);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
//                memo2 = editView.getText();
         //       CharSequence str = editView.getText();

                memo = editView.getText().toString();
                arrayList.set(0, memo);

                arrayAdapter.add(arrayList.get(0));
                listView.setAdapter(arrayAdapter);

            }
        });
        dialog.show();
    }

    void checkCheckBox(){




    }
}



