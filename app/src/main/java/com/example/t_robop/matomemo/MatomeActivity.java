package com.example.t_robop.matomemo;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MatomeActivity extends AppCompatActivity {

    //まとめ用のListView
    ListView listView;

    //まとめ用のArrayAdapter
    MatomeAdapter matomeAdapter;

    //表示する要素のリスト
    ArrayList<MatomeObject> matomeObjects;

    //単語確認用id保管リスト
    ArrayList<Integer> idList;

    //神
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matome);

        //Realmの初期化
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //関連付け
        listView = (ListView) findViewById(R.id.list_matome);

        idList=new ArrayList<>();

        //intentのときに「まとめのid」をもらってくるものとする
        int id = 0;
        //フォルダ名ももらってくるものとする
        String folderName="数学";

        /***ダミーデータ用意 ***/
        //トランザクション開始
        realm.beginTransaction();
        MatomeWord mWord= realm.createObject(MatomeWord.class);
        mWord.setWord("テスト");
        //メモインスタンスを生成
        RealmMemoEntity model = realm.createObject(RealmMemoEntity.class);
        //書き込みたいデータをインスタンスに入れる
        model.setMemo("数学テスト");
        model.setFolder("数学");
        model.words.add(mWord);
        //まとめインスタンスを生成
        RealmMatomeEntity matomeModel = realm.createObject(RealmMatomeEntity.class);
        //書き込みたいデータをインスタンスに入れる
        matomeModel.setId(0);
        matomeModel.words.add(mWord);

        MatomeWord mWord2= realm.createObject(MatomeWord.class);
        mWord2.setWord("テスト");
        //メモインスタンスを生成
        RealmMemoEntity model2 = realm.createObject(RealmMemoEntity.class);
        //書き込みたいデータをインスタンスに入れる
        model2.setMemo("今日は数学のテストがありました。しっかり公式覚えようと思いました。");
        model2.setFolder("数学");
        model2.words.add(mWord2);
        //まとめインスタンスを生成
        matomeModel.words.add(mWord2);

        MatomeWord mWord3= realm.createObject(MatomeWord.class);
        mWord3.setWord("公式");
        //メモインスタンスを生成
        RealmMemoEntity model3 = realm.createObject(RealmMemoEntity.class);
        //書き込みたいデータをインスタンスに入れる
        model3.setMemo("今日は数学のテストがありました。しっかり公式覚えようと思いました。");
        model3.setFolder("数学");
        model3.words.add(mWord3);
        //まとめインスタンスを生成
        matomeModel.words.add(mWord3);

        MatomeWord mWord4= realm.createObject(MatomeWord.class);
        mWord4.setWord("公式");
        MatomeWord mWord4_2= realm.createObject(MatomeWord.class);
        mWord4_2.setWord("今日");
        //メモインスタンスを生成
        RealmMemoEntity model4 = realm.createObject(RealmMemoEntity.class);
        //書き込みたいデータをインスタンスに入れる
        model4.setMemo("今日は数学のテストがありました。しっかり公式覚えようと思いました。");
        model4.setFolder("数学");
        model4.words.add(mWord4);
        model4.words.add(mWord4_2);
        //まとめインスタンスを生成
        matomeModel.words.add(mWord4);
        matomeModel.words.add(mWord4_2);

        //トランザクション終了 (データを書き込む)
        realm.commitTransaction();


        //検索用のクエリ作成
        RealmQuery<RealmMatomeEntity> mwQuery = realm.where(RealmMatomeEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmMatomeEntity> mwResults = mwQuery.findAll();
        for(int j=0;j<mwResults.size();j++) {
            for (int i = 0; i < mwResults.get(j).getWords().size(); i++) {
                Log.d("SSS", String.valueOf(mwResults.get(j).getWords().get(i).getWord()));
            }
        }


        //Realmから「まとめ」のデータ取得
        //検索用のまとめクエリ作成
        RealmQuery<RealmMatomeEntity> matomeQuery = realm.where(RealmMatomeEntity.class);
        //もらってきたidで検索
        //matomeQuery.equalTo("id", id);
        //インスタンス生成し、その中にすべてのデータを入れる(今回は一つだけのはず)
        RealmResults<RealmMatomeEntity> matomeResults = matomeQuery.findAll();

        //検索用のメモクエリ作成
        RealmQuery<RealmMemoEntity> memoQuery = realm.where(RealmMemoEntity.class);
        //フォルダ名取得
        memoQuery.equalTo("folder",folderName);
        //インスタンス生成し、検索されたメモを取得する
        RealmResults<RealmMemoEntity> memoResults = memoQuery.findAll();

        //同じidのまとめが複数存在することはありえない設計なので常に一つ目のみ取得する
        //まとめの開始時間と終了時間のどちらかが-1(未設定)でないなら
//        if (matomeResults.get(0).getStartDate() != -1 || matomeResults.get(0).getEndDate() != -1) {
//
//            //ここで時間で検索する処理
//
//        }
        //単語が指定されてる場合
        //if(matomeResults.get(0).getWords()!=null){
            //その単語を保持してるメモをlistに保管
            //まとめに設定された単語の数だけ回す
            for(int i=0;i<matomeResults.get(0).getWords().size();i++){
                //メモの数だけ回す
                for (int j=0;j<memoResults.size();j++){
                    //それぞれのメモに設定された単語の数だけ回す
                    for(int k=0;k<memoResults.get(j).getWords().size();k++){
                        String matomeWord=matomeResults.get(0).getWords().get(i).getWord();
                        String memoWord=memoResults.get(j).getWords().get(k).getWord();
                        //同じ単語があれば保持
                        if(matomeWord.equals(memoWord)){
                            idList.add(memoResults.get(j).getId());
                            break;//一個抜ける
                        }
                    }
                }
            }
        //}

        //最終的なメモ検索結果のクエリ作成
        for (int i=0;i<idList.size();i++) {
            //idが同じものを総取り
            memoQuery.equalTo("id", idList.get(i));
        }
        //インスタンス生成し、検索されたメモを取得する
        RealmResults<RealmMemoEntity> MResults = memoQuery.findAll();

        //アダプターの初期化
        matomeAdapter = new MatomeAdapter(this);
        //まとめリストの初期化
        matomeObjects = new ArrayList<MatomeObject>();
        //最終検索結果の数だけ回す
        for (int i = 0; i < MResults.size(); i++) {
            //list表示用のオブジェクトを初期化
            MatomeObject matomeObj=new MatomeObject();
            //メモセット
            matomeObj.setMemo(MResults.get(i).getMemo());
            //単語帳セット
            ArrayList<String> wordList=new ArrayList<>();
            for(int j=0;j<MResults.get(i).getWords().size();j++) {
                wordList.add(MResults.get(i).getWords().get(j).getWord());
            }
            matomeObj.setMarckWords(wordList);
            //listに追加
            matomeObjects.add(matomeObj);
        }
        //listをadapterにセット
        matomeAdapter.setMatomeList(matomeObjects);
        //アダプターをセット
        listView.setAdapter(matomeAdapter);


        // FloatingActionButton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.marker_change);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //マーカー表示非表示切り替え
            }
        });
    }
}
