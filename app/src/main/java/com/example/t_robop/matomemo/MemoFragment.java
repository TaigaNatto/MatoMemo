package com.example.t_robop.matomemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by user on 2017/06/20.
 */

public class MemoFragment extends Fragment {

    //ListView memoListView = null;   //メモのListView
    ArrayAdapter<String> adapterMemo = null;    //ListViewのAdapter

    Realm realm;

    //MemoFragmentのインスタンス化メソッド
    public static MemoFragment newInstance(String subjectName){
        // Bundleとかここに書く
        Bundle args = new Bundle();
        args.putString("SUBJECT",subjectName);  //StartListActivityでクリックされた教科名を受け取って保存
        MemoFragment fragment = new MemoFragment(); //Fragmentの初期化
        fragment.setArguments(args);
        return fragment;
    }

    //Fragmentで表示するViewを作成するメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        //Database初期化
        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        //メモのListViewのレイアウトをインフレート
        ListView memoListView = (ListView)inflater.inflate(R.layout.activity_memo_tab,container,false);

        //Adapterのインスタンスを作成
        adapterMemo = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        Bundle args = getArguments();
        String subject = args.getString("SUBJECT"); //初期表示の教科名を保存
        //ToDo 別画面で作成されてデータベースに保存されているメモのリストを呼び出す
        setMemoDataTest(subject);   //Debug用データベース設定    StartListActivityでタップした教科名のメモ一覧をデータベースにセット
        getMemoDataList(subject);   //StartListActivityでタップした教科名のメモ一覧をデータベースから取ってきて表示

        memoListView.setAdapter(adapterMemo);   //メモを画面に表示

        return memoListView;
    }

    //Debug用データベース設定    教科別メモセット
    //ToDo
    public void setMemoDataTest(String subjectName){
        //トランザクション開始
        realm.beginTransaction();
        //インスタンスを生成
        RealmMemoEntity testMemo = realm.createObject(RealmMemoEntity.class);

        //Debug用　Drawer内でタップした教科のメモをDebug用に作成したセット
        testMemo.setFolder(subjectName);
        testMemo.setMemo(subjectName+"メモ");

        //トランザクション終了 (データを書き込む)
        realm.commitTransaction();

    }

    //データベースから教科別メモ取得
    public void getMemoDataList(String subjectName){    //引数：　Drawer内でクリックした教科名
        //検索用のクエリ作成
        RealmQuery<RealmMemoEntity> memoQuery = realm.where(RealmMemoEntity.class);

        memoQuery = memoQuery.equalTo("folder",subjectName);  //引数で受け取った教科名のメモを取得

        RealmResults<RealmMemoEntity> memoResults = memoQuery.findAll();    //インスタンス生成し、その中に取得したすべてのデータを入れる

        adapterMemo.clear();    //一旦Listを全部削除

        for(int i=0; i<memoResults.size(); i++){
            adapterMemo.add(memoResults.get(i).getMemo());    //メモをListViewのAdapterに入れる
        }
    }


}
