package com.example.t_robop.matomemo;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.LayoutInflater;
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
    ArrayList<String> subjectArrayList;
    ArrayList<String> classPlaceArrayList;
    ListView listView;
    //ダイアログ用のEditText
    //////////
    EditText dialogGroupNameEditText;
    EditText dialogClassPlaceEditText;



    String groupName;
    //////////
    //listViewの要素の個数
    int itemNum = 0;
    //////////
    //重複していないときtrue※
    boolean original = true;
    //ArrayAdapterのString型でarrayAdapterを作成
    ArrayAdapter<String> groupNameArrayAdapter;
    //TextViewで「textView」を作成
    TextView textView;
    //listViewをタップしたときpositionの文字列
    String listPositionText;

    /*** 神 ***/
    Realm realm;
    /*** ** ***/

    //Dialogレイアウト取得用のView
    View inputView;


    boolean shinki = true;
    boolean henko = true;
    AlertDialog dialogNewMakeGroup;
    AlertDialog dialogReferenceGroup;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        LayoutInflater factory = LayoutInflater.from(this);

        //etContentView(R.layout.dialog_group_edit);
        inputView = factory.inflate(R.layout.dialog_group_edit, null);

        dialogGroupNameEditText = (EditText) inputView.findViewById(R.id.groupName);
        dialogClassPlaceEditText = (EditText) inputView.findViewById(R.id.classPlace);


        listView = (ListView) findViewById(R.id.list);
        textView = (TextView) findViewById(R.id.textView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        subjectArrayList = new ArrayList<>();
        classPlaceArrayList = new ArrayList<>();
        groupNameArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        subjectArrayList.add("領域確保");

        ////データベース用
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //dateBaseからデータを取得
        dateBaseReference();
        listView.setAdapter(groupNameArrayAdapter);




        ////ListViewがタップされたとき////
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        setDialogReferenceGroup(position);
                    }
                }
        );
    }

    ////dialogの中身////

    AlertDialog dialogBuild(AlertDialog dialog) {

        // dialogGroupNameEditText = new EditText(getApplicationContext());
        // dialogClassPlaceEditText = new EditText(getApplicationContext());
        dialog.setView(inputView);
        dialog.create();
        return dialog;
    }

    //adapterをarrayListで更新
    void adapterUpdate() {

        groupNameArrayAdapter.clear();

        for (int i = 0; i < subjectArrayList.size(); i++) {
            groupNameArrayAdapter.add(subjectArrayList.get(i));
        }
    }

    //////データベースからデータを取り出しlistViewにset////
    void dateBaseReference() {

        String text;
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();
        /***使い方は↑のメモと同じ***/

        if (folderResults.size() != 0) {
            text = folderResults.get(0).getFolderName();
            subjectArrayList.set(0, text);
            for (int i = 1; i < folderResults.size(); i++) {
                text = folderResults.get(i).getFolderName();
                subjectArrayList.add(text);
            }
            //adapterの中身を更新
            adapterUpdate();
        }
    }

    ////データベースにデータを追加(String groupNameを追加)
    void dateBaseAdd() {

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
    void dateBaseRemove() {

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
    void dateBaseUpdate() {
        //データベースのデータを削除
        dateBaseRemove();

        //データベースにlistViewの要素をすべて追加
        for (int i = 0; i < subjectArrayList.size(); i++) {

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

    //グループを新規作成するボタンをダイアログに追加
    void dialogGroupAddButtonMake(AlertDialog.Builder dialog) {

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                //groupNameをeditViewのデータで初期化

                groupName = dialogGroupNameEditText.getText().toString();

                ////入力データにダブってないか判定※被っているならoriginalをfalseに////
                original = true;
                //ListViewの要素分繰り返す
                for (int i = 0; i < itemNum; i++) {
                    //groupNameがarrayAdapterのi番目の要素と同じか、"未分類"なら実行
                    if (groupName.equals(groupNameArrayAdapter.getItem(i)) || groupName.equals("未分類")) {
                        original = false;
                        Toast.makeText(getApplicationContext(), "同名のファイルがすでに存在します", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                //入力データが空白でなく、ダブってないなら実行
                if (!groupName.equals("") && original) {

                    if (itemNum == 0) {
                        subjectArrayList.set(0, groupName);
                    } else {
                        subjectArrayList.add(groupName);
                    }

                    ///////引数を書く

                    //arrayListのデータをadapterに反映
                    adapterUpdate();
                    listView.setAdapter(groupNameArrayAdapter);
                    ////データベースにデータを追加(String groupNameを追加)

                    dateBaseAdd();
                }
            }
        });
    }

    //グループを削除するボタンをダイアログに追加
    void dialogGroupRemoveButtonMake(final int listPosition, AlertDialog.Builder dialog) {

        dialog.setNeutralButton("削除", new DialogInterface.OnClickListener() {
            @Override
            //ダイアログのcancelが押されたら実行
            public void onClick(DialogInterface dialog, int whichButton) {

                ////リストから削除////
                subjectArrayList.remove(listPosition);
                //arrayListのデータをadapterに反映
                adapterUpdate();


                listView.setAdapter(groupNameArrayAdapter);

                dateBaseUpdate();

                //元に戻す
                dialogGroupNameEditText.setText("");
            }
        });
    }

    //
    void dialogCancelButtonMake(AlertDialog.Builder dialog) {
        dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            //ダイアログのcancelが押されたら実行
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
    }

    //
    void dialogGroupReferenceButtonMake(final int listPosition, AlertDialog.Builder dialog) {

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                //groupNameをeditViewのデータで初期化
                groupName = dialogGroupNameEditText.getText().toString();

                ////入力データにダブってないか判定※被っているならoriginalをfalseに////
                original = true;
                //ListViewの要素分繰り返す
                for (int i = 0; i < itemNum; i++) {
                    //groupNameがarrayAdapterのi番目の要素と同じか、"未分類"なら実行
                    if (groupName.equals(groupNameArrayAdapter.getItem(i)) || groupName.equals("未分類")) {
                        original = false;
                        Toast.makeText(getApplicationContext(), "同名のファイルがすでに存在します", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                //入力データが空白でなく、ダブってないなら実行
                if (!groupName.equals("") && original) {

                    subjectArrayList.set(listPosition, groupName);

                    //引数の記入

                    //arrayListのデータをadapterに反映
                    adapterUpdate();
                    listView.setAdapter(groupNameArrayAdapter);

                    //データベースを更新
                    dateBaseUpdate();
                }
                if (itemNum != 0) {
                    textView.setText("");
                }

            }

        });
    }
    //フローティングアクションボタンがタップされた際に実行


    public void plus(View v) {

        if (dialogNewMakeGroup == null) {
            dialogNewMakeGroup = new AlertDialog.Builder(this)
                    .setView(inputView)
                    .setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    groupName = dialogGroupNameEditText.getText().toString();
                                    if (!groupName.equals("") && kuraberuKUN(groupName, subjectArrayList)) {

                                        if (subjectArrayList.size() == 0) {
                                            subjectArrayList.set(0, groupName);
                                        } else {
                                            subjectArrayList.add(groupName);
                                        }

                                        //arrayListのデータをadapterに反映
                                        adapterUpdate();
                                        listView.setAdapter(groupNameArrayAdapter);
                                        ////データベースにデータを追加(String groupNameを追加)
                                        dateBaseAdd();
                                    }

                                }
                            }
                    )
                    .setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }
                    )
                .create();
        }
        dialogNewMakeGroup.show();
    }

       boolean kuraberuKUN (String moziretu,ArrayList list){
           for (int i = 0; i < itemNum; i++) {
               //groupNameがarrayAdapterのi番目の要素と同じか、"未分類"なら実行
               if (groupName.equals(groupNameArrayAdapter.getItem(i)) || groupName.equals("未分類")) {
                   original = false;
                   Toast.makeText(getApplicationContext(), "同名のファイルがすでに存在します", Toast.LENGTH_SHORT).show();
                   return false;

               }
           }
           return true;
       }
//
//        boolean nai = true;
//
    //       for (int i = 0; i < list.size(); i++) {
//            //groupNameがarrayAdapterのi番目の要素と同じか、"未分類"なら実行
//            if (moziretu == list.get(i) || groupName.equals("未分類")) {
//                nai = false;
//                Toast.makeText(getApplicationContext(), "同名のファイルがすでに存在します", Toast.LENGTH_SHORT).show();
//                break;
//            }
//            Toast.makeText(getApplicationContext(), i + "個目", Toast.LENGTH_SHORT).show();
//        }
//        return nai;

//}

    void setDialogReferenceGroup(final int position) {

        if (dialogReferenceGroup == null) {
            dialogReferenceGroup = new AlertDialog.Builder(this)
                    .setView(inputView)
                    .setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    groupName = dialogGroupNameEditText.getText().toString();
                                    if (!groupName.equals("") && kuraberuKUN(groupName, subjectArrayList)) {

                                        subjectArrayList.set(position,groupName);
                                        adapterUpdate();

                                        ////データベースにデータを追加(String groupNameを追加)
                                        dateBaseReference();

                                        listView.setAdapter(groupNameArrayAdapter);
                                    }

                                }
                            }
                    )
                    .setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }
                    )
                    .setNeutralButton("削除", new DialogInterface.OnClickListener() {
                                @Override
                                //ダイアログのcancelが押されたら実行
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    ////リストから削除////
                                    subjectArrayList.remove(position);
                                    //arrayListのデータをadapterに反映
                                    adapterUpdate();
                                    listView.setAdapter(groupNameArrayAdapter);

                                    dateBaseUpdate();

                                    //元に戻す
                                    dialogGroupNameEditText.setText("");
                                }
                            }
                    )
                    .create();
        }
        dialogReferenceGroup.show();
    }

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

            /*
            case R.id.important_setting:
                Log.d("menu", "重要度設定へ");     //ImportantEditActivityへIntent
                break;
                */

            case R.id.editFolder:
                intent = new Intent(this, GroupEditActivity.class);  //GroupEditActivityへIntent
                startActivity(intent);
                break;
        }
        //startActivity(intent);

        return true;
    }


}