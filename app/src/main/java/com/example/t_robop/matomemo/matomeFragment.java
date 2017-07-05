package com.example.t_robop.matomemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by user on 2017/06/20.
 */

public class matomeFragment extends Fragment {

    ArrayAdapter<String> adapterMatome;
    ListView matomeListView;

    //Fragmentで表示するViewを作成するメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //まとめリストのレイアウトをViewとして作成
        matomeListView = (ListView)inflater.inflate(R.layout.activity_matome_tab,container,false);

        //Adapterのインスタンスを作って、追加
        adapterMatome = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);

        adapterMatome.add("まとめ");


        matomeListView.setAdapter(adapterMatome);

        return matomeListView;
    }

}
