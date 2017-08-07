package com.example.t_robop.matomemo;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import io.realm.Realm;

import io.realm.RealmQuery;
import io.realm.RealmResults;


import static android.R.attr.alertDialogIcon;
import static android.R.attr.checked;
import static android.R.attr.dial;
import static android.R.attr.id;

public class GroupEditActivity extends AppCompatActivity {

    //listViewに入れる配列
    ArrayList<String> arrayList;
    ListView listView;
    //ダイアログ用のEditText
    EditText editView;

    String groupName = " ";
    //listViewの要素の個数
    int itemNum = 0;
    //重複していないときtrue※
    boolean original = true;
    //ArrayAdapterのString型でarrayAdapterを作成
    ArrayAdapter<String> arrayAdapter;
    //TextViewで「textView」を作成
    TextView textView;
    //
    int listPosition;
    //listViewをタップしたときpositionの文字列
    String listPositionText;

    /*** 神 ***/
    Realm realm;
    /*** ** ***/



    //dialogの作成
    AlertDialog.Builder dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_edit);
        listView = (ListView) findViewById(R.id.list);
        textView = (TextView) findViewById(R.id.textView);

        arrayList = new ArrayList<>();
        arrayList.add("領域確保");

        //"arrayAdapter"を
        arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1);

        /***これ必須だからみんな書いて***/
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        /*******************/


        ////Toolbarの処理////

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        dialog = new AlertDialog.Builder(this);


        //textView.setText(" ");
        //dateBaseからデータを取得し、listViewに入れる
        dateBaseReference();

        ////ListViewがタップされたとき////
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        listPosition = position;
                        dialogBuild();
                        dialog.show();
                    }
                }
        );
    }

    ////dialogの中身////

    void dialogBuild() {

        editView = new EditText(getApplicationContext());
        //タップした場所を取得
        listPositionText = (String)listView.getItemAtPosition(listPosition);
        editView.setText(listPositionText);
        dialog.setView(editView);

        dialog.setTitle("編集");
        dialog.create();

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                //groupNameをeditViewのデータで初期化
                groupName = editView.getText().toString();

                ////入力データにダブってないか判定※被っているならoriginalをfalseに////
                original = true;
                //ListViewの要素分繰り返す
                for (int i = 0; i < itemNum; i++) {
                    //groupNameがarrayAdapterのi番目の要素と同じか、"未分類"なら実行
                    if (groupName.equals(arrayAdapter.getItem(i)) || groupName.equals("未分類")) {
                        original = false;
                         Toast.makeText(getApplicationContext(), "同名のファイルがすでに存在します", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                //入力データが空白でなく、ダブってないなら実行
                if (groupName.equals("") == false && original) {

                    if(itemNum == 0){
                        arrayList.set(0,groupName);
                    }else {
                        arrayList.add(groupName);
                    }
                    //arrayListのデータをadapterに反映
                    adapterUpdate();
                    listView.setAdapter(arrayAdapter);
                    ////データベースにデータを追加(String groupNameを追加)
                    dateBaseAdd();
                }
                if (itemNum != 0) {
                    textView.setText("");
                }

            }

        });
        dialog.setNeutralButton("削除", new DialogInterface.OnClickListener() {
            @Override
            //ダイアログのcancelが押されたら実行
            public void onClick(DialogInterface dialog, int whichButton) {

                ////リストから削除////
                arrayList.remove(listPosition);
                //arrayListのデータをadapterに反映
                adapterUpdate();



                listView.setAdapter(arrayAdapter);

                dateBaseUpdate();

                //元に戻す
                editView.setText("");
            }
        });
        dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            //ダイアログのcancelが押されたら実行
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        //元に戻す
        editView.setText("");
    }

    //adapterをarrayListで更新
    void adapterUpdate(){
        arrayAdapter.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            arrayAdapter.add(arrayList.get(i));
            //listViewの要素数を追加
            itemNum = i + 1;
        }
    }

    //////データベースからデータを取り出しlistViewにset////
    void dateBaseReference(){

        String text;
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();
        /***使い方は↑のメモと同じ***/

        if(folderResults.size() != 0) {
            text = folderResults.get(0).getFolderName();
            arrayList.set(0, text);
            for (int i = 1; i < folderResults.size(); i++) {
                text = folderResults.get(i).getFolderName();
                arrayList.add(text);
            }
            //adapterの中身を更新
            adapterUpdate();
            listView.setAdapter(arrayAdapter);
        }
    }
    ////データベースにデータを追加(String groupNameを追加)
    void dateBaseAdd(){

        //トランザクション開始
        realm.beginTransaction();
        //インスタンスを生成
        RealmFolderEntity model = realm.createObject(RealmFolderEntity.class);
        //書き込みたいデータをインスタンスに入れる
        model.setFolderName(groupName);
        //トランザクション終了 (データを書き込む)
        realm.commitTransaction();
    }
    ////データベースのデータを削除
    void dateBaseRemove(){

        ////指定したフォルダにはいっているメモデータを削除////

        RealmQuery<RealmMemoEntity> queryMemo = realm.where(RealmMemoEntity.class);
        //消したいデータを指定 (以下の場合はmemoデータの「memo」が「test」のものを指定)
        queryMemo.equalTo("folder", listPositionText);
        //指定されたデータを持つデータのみに絞り込む
        final RealmResults<RealmMemoEntity> resultMemos = queryMemo.findAll();
        // 変更操作はトランザクションの中で実行する必要あり
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // すべてのオブジェクトを削除
                resultMemos.deleteAllFromRealm();
            }
        });

        ////フォルダをすべて削除////

        RealmQuery<RealmFolderEntity> query = realm.where(RealmFolderEntity.class);
        //指定されたデータを持つデータのみに絞り込む
        final RealmResults<RealmFolderEntity> results = query.findAll();
        // 変更操作はトランザクションの中で実行する必要あり
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // すべてのオブジェクトを削除
                results.deleteAllFromRealm();
            }
        });
    }
    ////データベースのデータを更新////
    void dateBaseUpdate(){
        //データベースのデータを削除
        dateBaseRemove();

        //データベースにlistViewの要素をすべて追加
        for (int i = 0; i < itemNum; i++) {

            String temp = (String) listView.getItemAtPosition(i);

            realm.beginTransaction();
            //インスタンスを生成
            RealmFolderEntity model = realm.createObject(RealmFolderEntity.class);
            //書き込みたいデータをインスタンスに入れる
            model.setFolderName(temp);
            //トランザクション終了 (データを書き込む)
            realm.commitTransaction();
        }
    }


    //フローティングアクションボタンがタップされた際に実行
    public void plus(View v) {
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //checkが1つもついていないとき実行
        listPosition = -1;
        dialogBuild();
        dialog.show();
    }

    /*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            //
            // なんらかの処理
            int activityNum = 0;
            finish();
            Intent intent = getIntent();
            activityNum = intent.getIntExtra("Date",0);

            intent.putExtra("ID",0);

            String packageName = getCallingActivity().getPackageName();

            if(activityNum == 1) {
                finish();
                Intent intents = new Intent(this, MatoMemoListActivity.class);
                startActivity(intents);
            }else {
                //Intent intents = new Intent(this, ListActivity.class);
                //intent = new Intent(this,GroupEditActivity.class);  //GroupEditActivityへIntent
                //break;
                finish();
                Intent intents = new Intent(this, StartListActivity.class);
                startActivity(intents);
            }


            int activityNum = 0;
            Intent intent = getIntent();
            activityNum = intent.getIntExtra("Date",0);

            if(activityNum == 1) {
                finish();
                Intent intents = new Intent(this, MatoMemoListActivity.class);
                intents.putExtra("Date",1);
                startActivity(intents);
            }else {
                finish();
                Intent intents = new Intent(this, StartListActivity.class);
                intents.putExtra("Date",1);
                startActivity(intents);
            }

            return true;
        }
        return false;
    }
/*/
    //メニューバーの作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);  //res\menu\optionsのlayoutを読み込む
        return true;
    }

    //ToDo Intent先の作成とIntent処理の追加
    //メニューが選択されたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        //addしたときのIDで識別
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.tag_settings:
                Log.d("menu", "タグ設定へ");  //TagEditActivityへIntent
                break;

            case R.id.important_setting:
                Log.d("menu", "重要度設定へ");     //ImportantEditActivityへIntent
                break;

            case R.id.editFolder:
                intent = new Intent(this, GroupEditActivity.class);  //GroupEditActivityへIntent
                startActivity(intent);
                break;
        }
        //startActivity(intent);

        return true;
    }


}