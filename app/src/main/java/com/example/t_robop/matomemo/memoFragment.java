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

/**
 * Created by user on 2017/06/20.
 */

public class memoFragment extends Fragment {

    ArrayAdapter<String> adapterMemo = null;
    ListView memoListView = null;

    Realm realm;


    public static memoFragment newInstance(){
        memoFragment fragment = new memoFragment();
        // Bundleとかここに書く
        return fragment;
    }


    //Fragmentで表示するViewを作成するメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();


        //メモリストのレイアウトをViewとして作成
        memoListView = (ListView)inflater.inflate(R.layout.activity_memo_tab,container,false);

        //Adapterのインスタンスを作って、追加
        adapterMemo = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);

        adapterMemo.add("メモ");
        adapterMemo.add("メモ");
        adapterMemo.add("メモ");
        adapterMemo.add("メモ");
        adapterMemo.add("メモ");
        adapterMemo.add("メモ");
        adapterMemo.add("メモ");
        adapterMemo.add("メモ");
        adapterMemo.add("メモ");
        adapterMemo.add("メモ");
        adapterMemo.add("メモ");
        adapterMemo.add("メモ");
        adapterMemo.add("メモ");


        memoListView.setAdapter(adapterMemo);

        return memoListView;
    }

    //Fragmentを呼び出すためのpublicなメソッド
    public void callFromOut(){
        Log.d("test","fragment処理");
    }

}
