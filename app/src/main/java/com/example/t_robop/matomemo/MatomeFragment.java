package com.example.t_robop.matomemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.realm.Realm;

/**
 * Created by user on 2017/06/20.
 */
//ToDo まとめListの設計確認
public class MatomeFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

    //ListView matomeListView = null;
    ArrayAdapter<String> adapterMatome = null;

    Realm realm;

    public static MatomeFragment newInstance(){
        // Bundleとかここに書く
        MatomeFragment fragment = new MatomeFragment();
        return fragment;
    }

    //Fragmentで表示するViewを作成するメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //まとめのListViewのレイアウトをインフレート
        ListView matomeListView = (ListView)inflater.inflate(R.layout.activity_matome_tab,container,false);

        //Database初期化
        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        //Adapterのインスタンスを作って、追加
        adapterMatome = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        adapterMatome.add("まとめ");

        //まとめリストのItemタップ時の処理
        matomeListView.setOnItemClickListener(this);

        //まとめリストのItem長押し時の処理
        matomeListView.setOnItemLongClickListener(this);

        matomeListView.setAdapter(adapterMatome);   //adapterをlistViewにセット

        return matomeListView;
    }

    //データベースから教科別まとめ取得
    public void getMatomeDataList(String subjectName){

    }

    //Drawerクリック時のまとめリスト更新
    public void reloadMatomeData(String subject){
        adapterMatome.clear();
        getMatomeDataList(subject);
        adapterMatome.notifyDataSetChanged();
    }

    //選択されたItemをデータベースから削除
    public void removeMatomeData(String selectedItem){

    }

    //まとめ内容へIntent
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(),MatomeActivity.class);     //MatomeActivityへIntent
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        final String item = (String)adapterView.getItemAtPosition(position);   //クリックしたpositionからItemを取得

        //消去確認のダイアログ
        AlertDialog.Builder alertDig = new AlertDialog.Builder(getActivity());
        alertDig.setTitle("");
        alertDig.setMessage("まとめを消去しますか？");

        alertDig.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //No処理
            }
        });

        alertDig.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //YES処理
                adapterMatome.remove(item);   //リストから削除

                removeMatomeData(item);   //データベースから削除
            }
        });

        alertDig.create().show();

        return true;
    }
}
