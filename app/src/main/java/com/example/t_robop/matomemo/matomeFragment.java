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

    ListView matomeListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        matomeListView = (ListView)inflater.inflate(R.layout.activity_matome_tab,container,false);

        ArrayList<String> matomeList = new ArrayList<>();
        matomeList.add("まとめ");

        ArrayAdapter<String> adapterMatome = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_list_item_1, matomeList);
        matomeListView.setAdapter(adapterMatome);

        return matomeListView;
    }

}
