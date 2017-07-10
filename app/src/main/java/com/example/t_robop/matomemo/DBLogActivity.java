package com.example.t_robop.matomemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class DBLogActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter arrayAdapter;

    /*** 神 ***/
    Realm realm;
    /*** ** ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dblog);

        /***realmの初期化***/
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        /*******************/

        listView=(ListView)findViewById(R.id.log_list);
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1);

        loadRealm("memo");

    }

    public void memo_v(View v){
        loadRealm("memo");
    }

    public void folder_v(View v){
        loadRealm("folder");
    }

    public void word_v(View v){
        loadRealm("word");
    }

    public void loadRealm(String name){

        arrayAdapter.clear();

        switch (name) {
            case "memo":
                //検索用のクエリ作成
                RealmQuery<RealmMemoEntity> memoQuery = realm.where(RealmMemoEntity.class);
                //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
                RealmResults<RealmMemoEntity> memoR = memoQuery.findAll();
                for (RealmMemoEntity test:memoR){
                    arrayAdapter.add(test.getMemo());
                }
            case "folder":
                //検索用のクエリ作成
                RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
                //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
                RealmResults<RealmFolderEntity> folderR = folderQuery.findAll();
                for (RealmFolderEntity test:folderR){
                    arrayAdapter.add(test.getFolderName());
                }
            case "word":
                //検索用のクエリ作成
                RealmQuery<RealmWordEntity> wordQuery = realm.where(RealmWordEntity.class);
                //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
                RealmResults<RealmWordEntity> wordR = wordQuery.findAll();
                for (RealmWordEntity test:wordR){
                    arrayAdapter.add(test.getWordName());
                }
        }
        listView.setAdapter(arrayAdapter);

    }

    public void add_v(View v){
        /***データを書き込みたいとき！***/
        //トランザクション開始
        realm.beginTransaction();
        //インスタンスを生成
        RealmMemoEntity model = realm.createObject(RealmMemoEntity.class);
        //書き込みたいデータをインスタンスに入れる
        model.setMemo("test");
        //トランザクション終了 (データを書き込む)
        realm.commitTransaction();
        /******************************/

        loadRealm("memo");
    }

    //削除
    public void del_v(View v){
        // クエリを発行
        RealmQuery<RealmMemoEntity> query  = realm.where(RealmMemoEntity.class);
        //消したいデータを指定 (以下の場合はmemoデータの「memo」が「test」のものを指定)
        query.equalTo("memo","test");
        //指定されたデータを持つデータのみに絞り込む
        final RealmResults<RealmMemoEntity> results = query.findAll();
        // 変更操作はトランザクションの中で実行する必要あり
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // すべてのオブジェクトを削除
                results.deleteAllFromRealm();
            }
        });

        loadRealm("memo");
    }
}
