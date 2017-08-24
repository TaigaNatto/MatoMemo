package com.example.t_robop.matomemo;

import android.content.DialogInterface;
import android.content.Intent;
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

public class GroupEditActivity extends AppCompatActivity {

    //listViewに入れる配列
    ArrayList<String> groupNameArrayList;
    ListView simpleListView;
    ListView multiChoiceListView;

    ArrayList<String> memoDetaList;
    ///////
    //ダイアログ用のEditText
    EditText dialogGroupNameEditText;
    EditText dialogGroupNameMakeText;
    //ダイアログ内のeditTextデータを一次保存
    String groupName;

    //編集画面なら真、削除画面なら偽
    boolean groupEditMode;

    //ダイアログのなかでの変数宣言がうまくいかなかったため
    boolean kesu;

    //ArrayAdapterのString型でarrayAdapterを作成
    ArrayAdapter<String> groupNameArrayAdapter;
    ArrayAdapter<String> groupNameArrayAdapterChoice;

    TextView folderNotExistText;
    //listViewをタップしたときpositionの文字列

    /*** 神 ***/
    Realm realm;
    /*** ** ***/

    //Dialogレイアウト取得用のView
    View dialogViewGroupEdit;
    View dialogViewGroupMake;

    //ダイアログ　上から編集、新規作成、削除用ダイアログ
    AlertDialog dialogBuilderGroupEdit;
    AlertDialog dialogBuilderGroupMake;
    AlertDialog dialogBuilderGroupRemove;

    //タップしたポジションを一時保存
    int listPosition;
    //削除用ListViewのチェック状態を保存
    ArrayList checking;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        LayoutInflater factoryEdit = LayoutInflater.from(this);
        LayoutInflater factoryMake = LayoutInflater.from(this);


        dialogViewGroupEdit = factoryEdit.inflate(R.layout.dialog_group_edit,null);
        dialogViewGroupMake = factoryMake.inflate(R.layout.dialog_group_make,null);
        dialogGroupNameEditText = (EditText) dialogViewGroupEdit.findViewById(R.id.groupNameEdit);
        dialogGroupNameMakeText = (EditText) dialogViewGroupMake.findViewById(R.id.groupNameMake);
        simpleListView = (ListView) findViewById(R.id.list);
        multiChoiceListView = (ListView) findViewById(R.id.list_item);
        folderNotExistText = (TextView) findViewById(R.id.textView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        groupNameArrayList = new ArrayList<>();
        groupNameArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        groupNameArrayAdapterChoice = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_multiple_choice);
        memoDetaList = new ArrayList<>();
        groupEditMode = true;

        //group削除用のlistViewを消す
        multiChoiceListView.setVisibility(View.GONE);

        ////データベース用
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle( "フォルダの編集" );

        //dateBaseからデータを取得
        dateBaseReference();
        adapterUpdate();
        simpleListView.setAdapter(groupNameArrayAdapter);
        setTextViewDisply();

        ////group編集用のListViewがタップされたとき////
        simpleListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //タップされた場所を記録
                        listPosition = position;
                        //ListViewでタップされた場所のテキストをdialog内のEditTextにsetする
                        dialogGroupNameEditText.setText(groupNameArrayList.get(position));
                        //EditTextのカーソルを右寄せ
                        dialogGroupNameEditText.setSelection(dialogGroupNameEditText.getText().length());
                        //ダイアログの作成
                        setDialogBuilderGroupEdit();
                    }
                }
        );

        ////group編集用のListViewがロングタップされたとき////
        simpleListView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener(){
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        //編集画面から削除画面への切り替え
                        simpleListView.setVisibility(View.GONE);
                        multiChoiceListView.setVisibility(View.VISIBLE);
                        groupEditMode = false;
                        //adapterの中身を更新
                        adapterUpdate();
                        //削除用のListViewにデータをset
                        multiChoiceListView.setAdapter(groupNameArrayAdapterChoice);
                        ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.gomi);

                        return true;
                    }
                }
        );

        ////group削除用のListViewがロングタップされたとき////
        multiChoiceListView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener(){
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,View view, int position, long id){

                        //削除画面から編集画面への切り替え
                        simpleListView.setVisibility(View.VISIBLE);
                        multiChoiceListView.setVisibility(View.GONE);
                        groupEditMode = true;
                        //adapterの中身を更新
                        adapterUpdate();
                        //編集用のListViewにデータをセット
                        simpleListView.setAdapter(groupNameArrayAdapter);
                        ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.plusmark);

                        return true;
                    }
                }
        );
    }

    ////フローティングアクションボタンがタップされたときの処理////
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

                                        if (!groupName.equals("") && notExistList()) {

                                            groupNameArrayList.add(groupName);
                                            //adapterの中身を更新
                                            adapterUpdate();
                                            simpleListView.setAdapter(groupNameArrayAdapter);
                                            //データベースに追加
                                            detaBaseAdd();
                                            //フォルダがあるか判定して、フォルダがありませんと表示させる
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
            //フォルダがあるか判定して、フォルダが1つもない場合「フォルダがありません」と表示させる
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
                                                //指定したフォルダにはいっているメモデータを削除
                                                dateBaseMemoDateRemove();
                                                groupNameArrayList.remove(i);
                                            }
                                            checking.remove(i);
                                        }
                                        //adapterを更新
                                        adapterUpdate();
                                        multiChoiceListView.setAdapter(groupNameArrayAdapterChoice);
                                        //データベースを更新
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

    ////編集用のlistViewがタップされたときに出るdialogの作成////
    void setDialogBuilderGroupEdit(){

        //dialogがまだ作られていなければ
        if(dialogBuilderGroupEdit == null) {
            dialogBuilderGroupEdit = new AlertDialog.Builder(this)
                    .setView(dialogViewGroupEdit)
                    .setTitle("編集")
                    .setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBuilderGroupEdit, int which) {

                                    groupName = dialogGroupNameEditText.getText().toString();

                                    //
                                    if (!groupName.equals("") && notExistList()) {
                                        detaKoshin();

                                        groupNameArrayList.set(listPosition, groupName);

                                        //adapterを更新
                                        adapterUpdate();
                                        //データベースのデータを更新
                                        //   detaBaseUpdate();
                                        //   detaBaseUpDateMemo();


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
                                    kesukadoka();
                                }
                            }
                    )
                    .create();
        }
        dialogBuilderGroupEdit.show();
    }

    ////adapterをarrayListで更新////
    void adapterUpdate() {

        groupNameArrayAdapter.clear();
        groupNameArrayAdapterChoice.clear();

        for (int i = 0; i < groupNameArrayList.size(); i++) {
            groupNameArrayAdapter.add(groupNameArrayList.get(i));
            groupNameArrayAdapterChoice.add(groupNameArrayList.get(i));
        }
    }

    ////データベースのデータを更新する
    void detaKoshin() {

        /****フォルダのデータが欲しいとき！**/
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);

        folderQuery.equalTo("folderName",groupNameArrayList.get(listPosition));
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();
        //トランザクション開始
        realm.beginTransaction();
        //最初に作られたデータなのでposition0で指定
        RealmFolderEntity editModel=folderResults.get(0);

        //トランザクション終了 (データを書き込む)
        realm.commitTransaction();

        int ID = editModel.getId();

        folderQuery.equalTo("id",ID);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        folderResults = folderQuery.findAll();
        //トランザクション開始
        realm.beginTransaction();
        //最初に作られたデータなのでposition0で指定
        editModel=folderResults.get(0);
        editModel.setFolderName(groupName);
        //トランザクション終了 (データを書き込む)
        realm.commitTransaction();
    }

    ////データベースからデータを取り出しgroupNameArrayListに保存////
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

    ////データベースにデータを追加////
    void detaBaseAdd() {

        realm.beginTransaction();
        RealmFolderEntity model = realm.createObject(RealmFolderEntity.class);
        model.setFolderName(groupName);
        model.setId(getNewId());
        realm.commitTransaction();
    }

    ////指定したフォルダにはいっているメモデータを削除////
    void dateBaseMemoDateRemove(){

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

    ////データベースのデータを削除////
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

    ////データベースのデータを書き直す////
    void detaBaseUpdate() {

        //データベースのデータを削除
        detaBaseRemove();
        //データベースにlistViewの要素をすべて追加
        for (int i = 0; i < groupNameArrayList.size(); i++) {

            String temp = groupNameArrayList.get(i);
            realm.beginTransaction();
            RealmFolderEntity model = realm.createObject(RealmFolderEntity.class);
            model.setFolderName(temp);
            model.setId(getNewId());
            realm.commitTransaction();
        }
    }

    ////groupNameがgroupNameArrayListの中にあるかどうかを判定
    boolean notExistList(){

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

    ////フォルダがあるか判定して、フォルダが1つもない場合「フォルダがありません」と表示させる////
    void setTextViewDisply(){
        if(groupNameArrayList.size() == 0){
            folderNotExistText.setVisibility(View.VISIBLE);
        }else {
            folderNotExistText.setVisibility(View.INVISIBLE);
        }
    }

    ////削除するか確認数ダイアログ////
    boolean kesukadoka(){

        final AlertDialog.Builder kesuDialog = new AlertDialog.Builder(this);
        kesuDialog.setTitle("削除しますか？");
        kesuDialog.create();

        kesuDialog.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int whichButton){

                dateBaseMemoDateRemove();

                groupNameArrayList.remove(listPosition);

                adapterUpdate();
                simpleListView.setAdapter(groupNameArrayAdapter);

                detaBaseUpdate();

                dialogGroupNameEditText.setText("");
                setTextViewDisply();
            }
        });

        kesuDialog.setNegativeButton("cancel",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int whichButton){
                kesu = false;
            }
        });
        kesuDialog.show();

        return kesu;
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