package com.example.t_robop.matomemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmTestActivity extends AppCompatActivity {

    //xml
    ListView listView;
    EditText editText;

    //アダプター。わかるやろ？
    ArrayAdapter arrayAdapter;

    //神
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm_test);

        //関連付け
        listView=(ListView)findViewById(R.id.list);
        editText=(EditText)findViewById(R.id.edit);

        //realmの初期化
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //arrayAdapterの初期化
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1);

        //Realmデータの出力
        loadRealm();
    }

    public void click(View v){

        //editTextの内容を抽出
        String text=editText.getText().toString();

        //文字数が0でないとき(何か入ってるとき)
        if(text.length()!=0) {

            //トランザクション開始
            realm.beginTransaction();
            //インスタンスを生成
            RealmEntity model = realm.createObject(RealmEntity.class);

            //書き込みたいデータをインスタンスに入れる
            model.setMemo(text);

            //トランザクション終了 (データを書き込む)
            realm.commitTransaction();

            //Realmデータの出力
            loadRealm();
        }

    }

    public void loadRealm(){
        //検索用のクエリ作成
        RealmQuery<RealmEntity> query = realm.where(RealmEntity.class);

//        query.equalTo("name", "test");
//        query.or().equalTo("id", 2);
//        query.or().equalTo("id", 3);

        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmEntity> results = query.findAll();

        //すべての値を出力
        for (RealmEntity test:results){
            arrayAdapter.add(test.getMemo());
        }
        listView.setAdapter(arrayAdapter);
    }
}
