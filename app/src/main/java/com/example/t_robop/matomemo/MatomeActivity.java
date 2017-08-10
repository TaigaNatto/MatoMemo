package com.example.t_robop.matomemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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

    //単語確認用のid保管リスト
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
        //保管用listの初期化
        idList = new ArrayList<>();

        //ツールバー表示
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        //todo idほしいです！！！
        int id = intent.getIntExtra("ID", 1);

        /***ダミーデータ用意 ***/
        /*
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

*/
        /***************************/


        //検索用のクエリ作成
        RealmQuery<RealmMatomeEntity> mwQuery = realm.where(RealmMatomeEntity.class);
        //インスタンス生成
        RealmResults<RealmMatomeEntity> mwResults = mwQuery.findAll();
        for (int j = 0; j < mwResults.size(); j++) {
            for (int i = 0; i < mwResults.get(j).getWords().size(); i++) {
                Log.d("SSS", String.valueOf(mwResults.get(j).getWords().get(i).getWord()));
            }
        }


        //Realmから「まとめ」のデータ取得
        //検索用のまとめクエリ作成
        RealmQuery<RealmMatomeEntity> matomeQuery = realm.where(RealmMatomeEntity.class);
        //もらってきたidで検索
        matomeQuery.equalTo("id", id);
        //インスタンス生成し、その中にすべてのデータを入れる(今回は一つだけのはず)
        RealmResults<RealmMatomeEntity> matomeResults = matomeQuery.findAll();
        RealmMatomeEntity matomeEntity = matomeResults.get(0);
        String folderName = matomeEntity.getFolder();
        int startDate = matomeEntity.getStartDate();
        int endDate = matomeEntity.getEndDate();

        //検索用のメモクエリ作成
        RealmQuery<RealmMemoEntity> memoQuery = realm.where(RealmMemoEntity.class);
        //フォルダ名取得
        memoQuery.equalTo("folder", folderName);
        memoQuery.between("date", startDate, endDate);
        //インスタンス生成し、検索されたメモを取得する
        RealmResults<RealmMemoEntity> memoResults = memoQuery.findAll();

        //その単語を保持してるメモをlistに保管
        //まとめに設定された単語の数だけ回す
        for (int i = 0; i < matomeResults.get(0).getWords().size(); i++) {
            //メモの数だけ回す
            for (int j = 0; j < memoResults.size(); j++) {
                //それぞれのメモに設定された単語の数だけ回す
                for (int k = 0; k < memoResults.get(j).getWords().size(); k++) {
                    String matomeTag = matomeResults.get(0).getWords().get(i).getTagName();
                    String memoTag = memoResults.get(j).getWords().get(k).getTagName();
                    //同じ単語があれば保持
                    if (matomeTag.equals(memoTag)) {
                        int memoId = memoResults.get(j).getId();
                        //既にメモを保持してないか確認
                        for (int l = 0; l < idList.size(); l++) {
                            if (idList.get(l) == memoId) {
                                break;
                            } else {
                                if (l == idList.size() - 1) {
                                    idList.add(memoId);
                                    memoQuery.equalTo("id", memoId);
                                }
                            }
                        }
                        break;//一個抜ける
                    }
                }
            }
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
            MatomeObject matomeObj = new MatomeObject();
            //メモセット
            matomeObj.setMemo(MResults.get(i).getMemo());
            //単語帳セット
            ArrayList<MatomeWord> wordList = new ArrayList<>();
            for (int j = 0; j < MResults.get(i).getWords().size(); j++) {
                MatomeWord mWord=new MatomeWord();
                mWord.setTagName(MResults.get(i).getWords().get(j).getTagName());
                mWord.setWord(MResults.get(i).getWords().get(j).getWord());
                wordList.add(mWord);
            }
            matomeObj.setMarckWords(wordList);
            //listに追加
            matomeObjects.add(matomeObj);
        }
        //listをadapterにセット
        matomeAdapter.setMatomeList(matomeObjects);
        //アダプターをセット
        listView.setAdapter(matomeAdapter);

    }

    //左上のバックボタン
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Activity終了
        finish();
        return true;
    }
}
