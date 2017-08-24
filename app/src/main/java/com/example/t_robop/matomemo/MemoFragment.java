package com.example.t_robop.matomemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MemoFragment extends Fragment {

    private String[] allMemoDate;
    private String[] allMemoTime;
    private String[] allMemoData;

    public static ArrayList<Integer> idList = null;  //id保持用のリスト

    public static String nowSubjectName;  //現在表示しているメモの教科名

    EmptyRecyclerView recyclerView;
    CustomMemoRecyclerViewAdapter customMemoRecyclerViewAdapter;

    private Realm realm;

    //MemoFragmentのインスタンス化メソッド
    public static MemoFragment newInstance(String subjectName) {
        Bundle args = new Bundle();
        args.putString("SUBJECT", subjectName);  //選択されている教科名を受け取って保存  @KEY SUBJECT
        MemoFragment fragment = new MemoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Fragmentで表示するViewを作成するメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.activity_memo_tab, container, false);

        recyclerView = (EmptyRecyclerView) view.findViewById(R.id.memo_recycler_view);
        recyclerView.setHasFixedSize(true);

        View emptyView = view.findViewById(R.id.memo_view_empty);
        recyclerView.setEmptyView(emptyView);

        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        //MatoMemoListActivityとの値の受け渡し
        Bundle args = getArguments();
        nowSubjectName = args.getString("SUBJECT");

        idList = new ArrayList<>();

        getMemoDataList(nowSubjectName);   //選択されている教科名のメモ一覧をデータベースから取ってきて表示

        customMemoRecyclerViewAdapter = new CustomMemoRecyclerViewAdapter(allMemoDate, allMemoTime, allMemoData);
        recyclerView.setAdapter(customMemoRecyclerViewAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    //データの初期化
    private void memoDataInitialize() {
        for (int i = 0; i < allMemoDate.length; i++) {
            allMemoDate[i] = "";
        }
    }

    //データベースから教科別メモ取得
    public void getMemoDataList(String subjectName) {    //引数：　Drawer内でクリックした教科名

        RealmQuery<RealmMemoEntity> memoQuery = realm.where(RealmMemoEntity.class);

        memoQuery = memoQuery.equalTo("folder", subjectName);  //引数で受け取った教科名のメモを取得

        RealmResults<RealmMemoEntity> memoResults = memoQuery.findAll();

        //Debeg用　全データ確認
        int year;
        int month;
        int day;
        int hour;
        int min;
        int sec;
        allMemoDate = new String[memoResults.size()];     //各CardViewに入れるメモの保存日付
        allMemoTime = new String[memoResults.size()];     //各CardViewに入れるメモの保存時間
        allMemoData = new String[memoResults.size()];     //各CardViewに入れるメモの内容


        //ToDo メモリストに何を表示するのか定めて、該当データ取得とadapterへの代入を行う
        //データ取得
        if (memoResults.size() != 0) {
            //CardViewに表示する要素の取得
            for (int i = 0; i < memoResults.size(); i++) {
                year = memoResults.get(i).getDate() / 10000;
                month = memoResults.get(i).getDate() / 100 % 100;
                day = memoResults.get(i).getDate() % 100;

                hour = memoResults.get(i).getTime() / 10000;
                min = memoResults.get(i).getTime() / 100 % 100;
                sec = memoResults.get(i).getTime() % 100;

                allMemoDate[i] = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day);
                allMemoTime[i] = String.valueOf(hour) + ":" + String.valueOf(min);
                allMemoData[i] = memoResults.get(i).getMemo();
                idList.add(memoResults.get(i).getId());
            }
        }
    }

    //Drawerクリック時のメモリスト更新
    public void reloadMemoData(String subject) {
        nowSubjectName = subject;   //Drawerでクリックされた教科名をフィールド変数に代入

        idList.clear();
        getMemoDataList(subject);    //データベースから教科別メモ取得

        customMemoRecyclerViewAdapter = new CustomMemoRecyclerViewAdapter(allMemoDate, allMemoTime, allMemoData);
        customMemoRecyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(customMemoRecyclerViewAdapter);
    }

}
