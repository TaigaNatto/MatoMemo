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

import android.view.ViewGroup;
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
    ArrayList<String> groupNameArrayList;
    ArrayList<String> classPlaceArrayList;
    ListView simpleListView;
    ListView multiChoiceListView;

    ArrayList<String> memoDetaList;
    ///////

    //multinihennko
    //まずはもう一つのlistViewを消す

    //////

    //ダイアログ用のEditText
    //////////
    EditText dialogGroupNameEditText;
    EditText dialogClassPlaceEditText;

    EditText dialogGroupNameMakeText;
    EditText dialogClassPlaceMakeText;

    String groupName;
    //////////
    //listViewの要素の個数
    int itemNum = 0;
    //////////

    boolean groupEditMode;

    //ArrayAdapterのString型でarrayAdapterを作成
    ArrayAdapter<String> groupNameArrayAdapter;

    ArrayAdapter<String> groupNameArrayAdapterChoice;
    //TextViewで「textView」を作成
    TextView textView;
    //listViewをタップしたときpositionの文字列

    /*** 神 ***/
    Realm realm;
    /*** ** ***/

    //Dialogレイアウト取得用のView
    View dialogViewGroupEdit;
    View dialogViewGroupMake;


    boolean shinki = true;
    boolean henko = true;
    AlertDialog dialogBuilderGroupEdit;
    AlertDialog dialogBuilderGroupMake;
    AlertDialog dialogBuilderGroupRemove;

    int listPosition;
    ArrayList checking;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        LayoutInflater factoryEdit = LayoutInflater.from(this);
        LayoutInflater factoryMake = LayoutInflater.from(this);

        //etContentView(R.layout.dialog_group_edit);
        dialogViewGroupEdit = factoryEdit.inflate(R.layout.dialog_group_edit,null);
        dialogViewGroupMake = factoryMake.inflate(R.layout.dialog_group_make,null);


        dialogGroupNameEditText = (EditText) dialogViewGroupEdit.findViewById(R.id.groupNameEdit);

        dialogGroupNameMakeText = (EditText) dialogViewGroupMake.findViewById(R.id.groupNameMake);

        simpleListView = (ListView) findViewById(R.id.list);
        multiChoiceListView = (ListView) findViewById(R.id.list_item);
        textView = (TextView) findViewById(R.id.textView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        groupNameArrayList = new ArrayList<>();
        classPlaceArrayList = new ArrayList<>();
        groupNameArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        groupNameArrayAdapterChoice = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_multiple_choice);

        memoDetaList = new ArrayList<>();

        //groupNameArrayList.add("領域確保");
        multiChoiceListView.setVisibility(View.GONE);
        groupEditMode = true;

        ////データベース用
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //dateBaseからデータを取得
        dateBaseReference();
        simpleListView.setAdapter(groupNameArrayAdapter);

        setTextViewDisply();

        ////ListViewがタップされたとき////
        simpleListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        listPosition = position;
                        dialogGroupNameEditText.setText(groupNameArrayList.get(position));
                        dialogGroupNameEditText.setSelection(dialogGroupNameEditText.getText().length());
                        setDialogBuilderGroupEdit();
                    }
                }
        );

        simpleListView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener(){
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        simpleListView.setVisibility(View.GONE);
                        multiChoiceListView.setVisibility(View.VISIBLE);

                        groupEditMode = false;

                        adapterUpdate();
                        multiChoiceListView.setAdapter(groupNameArrayAdapterChoice);

                        ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.gomi);

                        return true;
                    }
                }
        );

        multiChoiceListView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener(){
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,View view, int position, long id){

                        simpleListView.setVisibility(View.VISIBLE);
                        multiChoiceListView.setVisibility(View.GONE);

                        groupEditMode = true;

                        adapterUpdate();
                        simpleListView.setAdapter(groupNameArrayAdapter);

                        ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.plusmark);

                        return true;
                    }
                }
        );
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

        for (int i = 0; i < folderResults.size(); i++) {
            text = folderResults.get(i).getFolderName();
            groupNameArrayList.add(text);
        }
        //adapterの中身を更新
        adapterUpdate();
    }

    void dateBaseMemoDateRemove(){

        ////指定したフォルダにはいっているメモデータを削除////

        RealmQuery<RealmMemoEntity> queryMemo = realm.where(RealmMemoEntity.class);
        //消したいデータを指定 (以下の場合はmemoデータの「memo」が「test」のものを指定)
        queryMemo.equalTo("folder", groupNameArrayList.get(listPosition));
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

    }

    ////データベースのデータを削除
    void detaBaseRemove() {

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
    void detaBaseUpdate() {
        //データベースのデータを削除
        detaBaseRemove();

        //データベースにlistViewの要素をすべて追加
        for (int i = 0; i < groupNameArrayList.size(); i++) {

            String temp = groupNameArrayList.get(i);

            realm.beginTransaction();
            //インスタンスを生成
            RealmFolderEntity model = realm.createObject(RealmFolderEntity.class);
            //書き込みたいデータをインスタンスに入れる
            model.setFolderName(temp);
            //トランザクション終了 (データを書き込む)
            realm.commitTransaction();
        }
    }

    boolean GroupNameInTheList(){

        //ListViewの要素分繰り返す
        for (int i = 0; i < groupNameArrayList.size(); i++) {
            //groupNameがarrayAdapterのi番目の要素と同じか、"未分類"なら実行
            if (groupName.equals(groupNameArrayList.get(i)) || groupName.equals("未分類")) {
                Toast.makeText(getApplicationContext(), "同名のファイルがすでに存在します", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    void setDialogBuilderGroupEdit(){

        if(dialogBuilderGroupEdit == null) {
            dialogBuilderGroupEdit = new AlertDialog.Builder(this)
                    .setView(dialogViewGroupEdit)
                    .setTitle("編集")
                    .setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBuilderGroupEdit, int which) {

                                    groupName = dialogGroupNameEditText.getText().toString();

                                    if (!groupName.equals("") && GroupNameInTheList()) {

                                        detaBaseReferenceMemo();
                                        dateBaseMemoDateRemove();

                                        groupNameArrayList.set(listPosition, groupName);
                                        adapterUpdate();

                                        detaBaseUpdate();
                                        detaBaseAddMemo();

                                        simpleListView.setAdapter(groupNameArrayAdapter);

                                    }
                                }
                            }
                    )
                    .setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialogBuilderGroupEdit,int witch){
                                }
                            }
                    )
                    .setNeutralButton(
                            "削除",
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogBuilderGroupEdit,int witch){

                                    dateBaseMemoDateRemove();

                                    groupNameArrayList.remove(listPosition);

                                    adapterUpdate();
                                    simpleListView.setAdapter(groupNameArrayAdapter);

                                    detaBaseUpdate();

                                    dialogGroupNameEditText.setText("");
                                    setTextViewDisply();
                                }
                            }
                    )
                    .create();
        }
        dialogBuilderGroupEdit.show();

    }


    public void plus(View v) {

        if(groupEditMode) {

            dialogGroupNameMakeText.setText("");

            if (dialogBuilderGroupMake == null) {
                dialogBuilderGroupMake = new AlertDialog.Builder(this)
                        .setView(dialogViewGroupMake)
                        .setTitle("新規作成")
                        .setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        groupName = dialogGroupNameMakeText.getText().toString();

                                        if (!groupName.equals("") && GroupNameInTheList()) {

                                            groupNameArrayList.add(groupName);
                                            //arrayListのデータをadapterに反映
                                            adapterUpdate();
                                            simpleListView.setAdapter(groupNameArrayAdapter);
                                            ////データベースにデータを追加(String groupNameを追加)
                                            detaBaseAdd();
                                            setTextViewDisply();
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
            dialogBuilderGroupMake.show();
            setTextViewDisply();
        }else {

            checking = new ArrayList<>();

            for(int i = 0; i < groupNameArrayList.size(); i++){
                checking.add("");
                if(multiChoiceListView.isItemChecked(i)){
                    checking.set(i,"true");
                }
            }

            if (dialogBuilderGroupRemove == null){

                dialogBuilderGroupRemove = new AlertDialog.Builder(this)
                        .setTitle("削除しますか？")
                        .setPositiveButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBuilderGroupEdit, int which) {

                                        for(int i = groupNameArrayList.size() - 1; 0 <= i; i--){

                                            if(checking.get(i) == "true"){

                                                listPosition = i;
                                                dateBaseMemoDateRemove();
                                                groupNameArrayList.remove(i);
                                            }
                                            checking.remove(i);
                                        }
                                        adapterUpdate();
                                        multiChoiceListView.setAdapter(groupNameArrayAdapterChoice);

                                        detaBaseUpdate();

                                        simpleListView.setVisibility(View.VISIBLE);
                                        multiChoiceListView.setVisibility(View.GONE);

                                        groupEditMode = true;

                                        simpleListView.setAdapter(groupNameArrayAdapter);

                                        ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.plusmark);
                                    }
                                }
                        )
                        .setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialogBuilderGroupEdit,int witch){
                                    }
                                }
                        )
                        .create();
            }
            dialogBuilderGroupRemove.show();

   /*         for(int i = groupNameArrayList.size() - 1; 0 <= i; i--){

                if(multiChoiceListView.isItemChecked(i)){

                    listPosition = i;
                    dateBaseMemoDateRemove();
                    groupNameArrayList.remove(i);
                }
            }
     */
            if(groupNameArrayList.size() == 0){
                simpleListView.setVisibility(View.VISIBLE);
                multiChoiceListView.setVisibility(View.GONE);

                groupEditMode = true;

                adapterUpdate();
                simpleListView.setAdapter(groupNameArrayAdapter);

                ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.plusmark);
            }

            setTextViewDisply();
        }
    }

    void detaBaseAdd() {

        //トランザクション開始
        realm.beginTransaction();
        //インスタンスを生成
        RealmFolderEntity model = realm.createObject(RealmFolderEntity.class);
        //書き込みたいデータをインスタンスに入れる
        model.setFolderName(groupName);
        //トランザクション終了 (データを書き込む)
        realm.commitTransaction();
    }

    void setTextViewDisply(){
        if(groupNameArrayList.size() == 0){
            textView.setVisibility(View.VISIBLE);
        }else {
            textView.setVisibility(View.INVISIBLE);
        }
    }

    void detaBaseReferenceMemo(){

        realm = Realm.getDefaultInstance();
        //検索用のクエリ作成
        RealmQuery<RealmMemoEntity> memoQuery = realm.where(RealmMemoEntity.class);

        memoQuery.equalTo("folder",groupNameArrayList.get(listPosition));

        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmMemoEntity> memoResults = memoQuery.findAll();

        String text;

        for (int i = 0; i < memoResults.size(); i++) {

            text = memoResults.get(i).getMemo();
            memoDetaList.add(text);
        }
    }

    void detaBaseAddMemo(){

        RealmQuery<RealmMemoEntity> queryMemo = realm.where(RealmMemoEntity.class);


        //消したいデータを指定 (以下の場合はmemoデータの「memo」が「test」のものを指定)
        queryMemo.equalTo("folder", groupNameArrayList.get(listPosition));



        for (int i = 0; i < memoDetaList.size(); i++) {

            realm.beginTransaction();
            //インスタンスを生成
            RealmMemoEntity model = realm.createObject(RealmMemoEntity.class);
            //書き込みたいデータをインスタンスに入れる
            model.setMemo(memoDetaList.get(i));
            model.setFolder(groupNameArrayList.get(listPosition));
            //トランザクション終了 (データを書き込む)
            realm.commitTransaction();
        }
        for(int i = memoDetaList.size() - 1; i >= 0 ;i--) {
            memoDetaList.remove(i);
        }
    }



    void dialogGroupReferenceButtonMake(final int listPosition, AlertDialog.Builder dialog) {

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                //groupNameをeditViewのデータで初期化
                groupName = dialogGroupNameEditText.getText().toString();



                //入力データが空白でなく、ダブってないなら実行
                if (!groupName.equals("") && GroupNameInTheList()) {

                    groupNameArrayList.set(listPosition, groupName);

                    //引数の記入

                    //arrayListのデータをadapterに反映
                    adapterUpdate();
                    simpleListView.setAdapter(groupNameArrayAdapter);

                    //データベースを更新
                    detaBaseUpdate();
                }
                if (itemNum != 0) {
                    textView.setText("");
                }

            }

        });
    }
    //adapterをarrayListで更新
    void adapterUpdate() {

        groupNameArrayAdapter.clear();
        groupNameArrayAdapterChoice.clear();


        for (int i = 0; i < groupNameArrayList.size(); i++) {
            groupNameArrayAdapter.add(groupNameArrayList.get(i));
            groupNameArrayAdapterChoice.add(groupNameArrayList.get(i));
        }
    }

    //アラートダイアログを使用可能に
    void alertDialogBuild(AlertDialog.Builder alertDialog){
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.create();
    }
    //アラートダイアログにviewをset

    //グループを削除するボタンをダイアログに追加
    void dialogGroupRemoveButtonMake(final int listPosition, AlertDialog.Builder dialog) {

        groupName = dialogGroupNameEditText.getText().toString();

        dialog.setNeutralButton("削除", new DialogInterface.OnClickListener() {
            @Override
            //ダイアログの削除が押されたら実行
            public void onClick(DialogInterface dialog, int whichButton) {

                ////リストから削除////
                groupNameArrayList.remove(listPosition);
                //arrayListのデータをadapterに反映
                adapterUpdate();

                simpleListView.setAdapter(groupNameArrayAdapter);

                detaBaseUpdate();

                //元に戻す
                dialogGroupNameEditText.setText("");
            }
        });
    }

    //グループを新規作成するボタンをダイアログに追加
    void dialogGroupAddButtonMake(AlertDialog.Builder dialog) {

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                //groupNameをeditViewのデータで初期化

                groupName = dialogGroupNameEditText.getText().toString();



                //入力データが空白でなく、ダブってないなら実行
                if (!groupName.equals("") && GroupNameInTheList()) {

                    if (groupNameArrayList.size() == 0) {
                        groupNameArrayList.set(0, groupName);
                    } else {
                        groupNameArrayList.add(groupName);
                    }

                    ///////引数を書く

                    //arrayListのデータをadapterに反映
                    adapterUpdate();
                    simpleListView.setAdapter(groupNameArrayAdapter);
                    ////データベースにデータを追加(String groupNameを追加)

                    //   dateBaseAdd();
                }
            }
        });
    }


    ////////////////////////////////////////////////////

    /*////dialogの中身////

    AlertDialog dialogBuild(AlertDialog dialog) {

        // dialogGroupNameEditText = new EditText(getApplicationContext());
        // dialogClassPlaceEditText = new EditText(getApplicationContext());
        dialog.setView(inputView);
        dialog.create();
        return dialog;
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
  */      /***使い方は↑のメモと同じ***/

 /*       if (folderResults.size() != 0) {
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



        dialogReferenceGroup.show();
    }
*/
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

    //既存のidの最大値+1した数値を返してくれるメソッド
    public int getNewId() {
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();
        int maxId = 0;
        if (folderResults.size() > 0) {
            for (int i = 0; i < folderResults.size(); i++) {
                int nowId = folderResults.get(i).getId();
                if (nowId > maxId) {
                    maxId = nowId;
                }
            }
        }
        return maxId + 1;
    }


}