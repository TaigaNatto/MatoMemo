package com.example.t_robop.matomemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by user on 2017/06/20.
 */

public class memoFragment extends Fragment {

    ArrayAdapter<String> adapterMemo = null;
    ListView memoListView = null;

    Realm realm;

    //
    public static memoFragment newInstance(){
        memoFragment fragment = new memoFragment();
        // Bundleとかここに書く
        return fragment;
    }


    //Fragmentで表示するViewを作成するメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        //Database初期化
        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        SetMemoDataTest();  //Debug用データセット

        //メモリストのレイアウトをViewとして作成
        memoListView = (ListView)inflater.inflate(R.layout.activity_memo_tab,container,false);

        //Adapterのインスタンスを作成
        adapterMemo = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);

        adapterMemo.add("ただのメモ");

        memoListView.setAdapter(adapterMemo);

        return memoListView;
    }

    //Debug用データベース設定    教科別メモセット
    //ToDo  
    public void SetMemoDataTest(){
        //トランザクション開始
        realm.beginTransaction();
        //インスタンスを生成
        RealmMemoEntity testMemo = realm.createObject(RealmMemoEntity.class);

        //ToDo
        //未分類フォルダに未分類メモ1を入れる
        //数学フォルダに数学メモ1を入れる
        //書き込みたいデータをインスタンスに入れる
        testMemo.setFolder("未分類");
        testMemo.setMemo("未分類メモ");


        //トランザクション終了 (データを書き込む)
        realm.commitTransaction();

        //ToDo
        //setFolderNameだと逐一上書きされてしまうので、ArrayListでSetするとかする必要あり
    }

    //Debug用データベース設定    教科別メモ取得
    public void GetMemoDataTest(String subjectName){    //引数：　Drawer内でクリックした教科名
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
