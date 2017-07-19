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

    //Fragmentの初期化  呼び出し元: CustomFragmentPagerAdapter
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

        //メモリストのレイアウトをViewとして作成
        memoListView = (ListView)inflater.inflate(R.layout.activity_memo_tab,container,false);

        //Adapterのインスタンスを作成
        adapterMemo = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);

        //ToDo StartListActivityからIntentされたときにタップした教科名のメモを表示させる
        //SetMemoDataTestとGetMemoDataTestを使いたいけど、MatoMemoListActivityのフィールドでnullで宣言しているsubjectNameを取ってきてしまう

        memoListView.setAdapter(adapterMemo);   //メモを画面に表示

        return memoListView;
    }

    //Debug用データベース設定    教科別メモセット
    //ToDo
    public void SetMemoDataTest(String subjectName){
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

    //データベース設定    教科別メモ取得
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
