package com.example.t_robop.matomemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmTestActivity extends AppCompatActivity {

    //xml
    ListView listView;
    EditText editText;

    //アダプター。わかるやろ？
    ArrayAdapter arrayAdapter;

    /*** 神 ***/
    Realm realm;
    /*** ** ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm_test);

        /***realmの初期化***/
        /***これ必須だからみんな書いて***/
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        /*******************/

        /***メモのデータが欲しいとき！***/
        //検索用のクエリ作成
        RealmQuery<RealmMemoEntity> memoQuery = realm.where(RealmMemoEntity.class);

        /***

            //特定のデータだけ欲しい時はこんな感じに(この場合は「フォルダ名」が「数学」の物のみ取得)
            memoQuery.equalTo("folder","数学");

        ***/

        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmMemoEntity> memoResults = memoQuery.findAll();
        /***
            //取ってきたデータの中の0番目のメモ本文が欲しい時はこんな感じに
            String text = results.get(0).getMemo();

            //取ってきたデータ全部欲しい時はこんな感じに
            ArrayList<String> array=new ArrayList<>();
            for(int i=0;i<results.size();i++){
                array.add(results.get(i));
            }
        ***/
        /******************/

        /****フォルダのデータが欲しいとき！**/
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();
        /***使い方は↑のメモと同じ***/
        /****************************/

        /****単語のデータが欲しいとき！**/
        //検索用のクエリ作成
        RealmQuery<RealmWordEntity> wordQuery = realm.where(RealmWordEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmWordEntity> wordResults = wordQuery.findAll();
        /***使い方は(以下略***/
        /****************************/

        /***データを書き込みたいとき！***/
        //トランザクション開始
        realm.beginTransaction();
        //インスタンスを生成
        RealmMemoEntity model = realm.createObject(RealmMemoEntity.class);
        //書き込みたいデータをインスタンスに入れる
        model.setMemo("書き込みたいメモ");
        //トランザクション終了 (データを書き込む)
        realm.commitTransaction();
        /******************************/

        /***データ更新したいとき！***/
        //例えば2017年6月28日に最初に作られたメモを編集したいとき
        //まず更新したいデータを取得します
        //検索用のクエリ作成
        RealmQuery<RealmMemoEntity> query = realm.where(RealmMemoEntity.class);
        //2016/6/28で検索
        query.equalTo("date",20160628);
        //インスタンス生成し、その中にすべてのデータを入れる
        RealmResults<RealmMemoEntity> results = query.findAll();
        //トランザクション開始
        realm.beginTransaction();
        //最初に作られたデータなのでposition0で指定
        RealmMemoEntity editModel=results.get(0);
        //書き込みたいデータをインスタンスに入れる
        editModel.setMemo("この授業は取る価値無し");
        //トランザクション終了 (データを書き込む)
        realm.commitTransaction();
        /*************************/

        /***データ消したいとき！***/
        // クエリを発行
        RealmQuery<RealmMemoEntity> delQuery  = realm.where(RealmMemoEntity.class);
        //消したいデータを指定 (以下の場合はmemoデータの「memo」が「test」のものを指定)
        query.equalTo("memo","test");
        //指定されたデータを持つデータのみに絞り込む
        final RealmResults<RealmMemoEntity> delR = delQuery.findAll();
        // 変更操作はトランザクションの中で実行する必要あり
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // すべてのオブジェクトを削除
                delR.deleteAllFromRealm();
            }
        });
        /***********************/

        //関連付け
        listView=(ListView)findViewById(R.id.list);
        editText=(EditText)findViewById(R.id.edit);
        //arrayAdapterの初期化
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        //Realmデータの出力
        loadRealm();
    }

    public void click(View v){

        //editTextの内容を抽出
        final String text=editText.getText().toString();

        //文字数が0でないとき(何か入ってるとき)
        if(text.length()!=0) {
            //トランザクション開始
            realm.beginTransaction();
            //インスタンスを生成
            RealmMemoEntity model = realm.createObject(RealmMemoEntity.class);
            //書き込みたいデータをインスタンスに入れる
            model.setMemo(text);
            //トランザクション終了 (データを書き込む)
            realm.commitTransaction();

            //Realmデータの出力
            loadRealm();
        }

    }

    public void loadRealm(){

        arrayAdapter.clear();

        //検索用のクエリ作成
        RealmQuery<RealmMemoEntity> query = realm.where(RealmMemoEntity.class);

//        query.equalTo("name", "test");
//        query.or().equalTo("id", 2);
//        query.or().equalTo("id", 3);

        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmMemoEntity> results = query.findAll();

        //すべての値を出力
        for (RealmMemoEntity test:results){
            arrayAdapter.add(test.getMemo());
        }
        listView.setAdapter(arrayAdapter);
    }
}
