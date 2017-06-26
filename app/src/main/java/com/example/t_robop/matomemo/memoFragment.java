package com.example.t_robop.matomemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by user on 2017/06/20.
 */

public class memoFragment extends Fragment {

    ListView memoListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        memoListView = (ListView)inflater.inflate(R.layout.activity_memo_tab,container,false);

        ArrayList<String> memoList = new ArrayList<>();
        memoList.add("メモ");

        ArrayAdapter<String> adapterMemo = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_list_item_1, memoList);
        memoListView.setAdapter(adapterMemo);

        return memoListView;
    }

}
