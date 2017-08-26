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

    private ArrayList<CardMemoData> memoDatas;

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

        memoDatas = new ArrayList<>();
        idList = new ArrayList<>();

        getMemoDataList(nowSubjectName);   //選択されている教科名のメモ一覧をデータベースから取ってきて表示

        customMemoRecyclerViewAdapter = new CustomMemoRecyclerViewAdapter(memoDatas,idList);
        recyclerView.setAdapter(customMemoRecyclerViewAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    //データベースから教科別メモ取得
    public void getMemoDataList(String subjectName) {    //引数：　Drawer内でクリックした教科名

        RealmQuery<RealmMemoEntity> memoQuery = realm.where(RealmMemoEntity.class);
        if(subjectName.equals("未分類")){
            memoQuery = memoQuery.equalTo("folder", subjectName);  //引数で受け取った教科名のメモを取得
        }
        else {
            RealmQuery<RealmFolderEntity> folQuery = realm.where(RealmFolderEntity.class);
            folQuery = folQuery.equalTo("folderName", subjectName);  //引数で受け取った教科名のfolderを取得
            RealmResults<RealmFolderEntity> folResults = folQuery.findAll();
            memoQuery = memoQuery.equalTo("folderId", folResults.get(0).getId());  //引数で受け取った教科名のメモを取得
        }
        RealmResults<RealmMemoEntity> memoResults = memoQuery.findAll();

        //Debeg用　全データ確認
        int year;
        int month;
        int day;
        int hour;
        int min;
        int sec;

        String memoDate;
        String memoTime;
        String memoText;
        String textCountResult;

        //ToDo cardViewのレイアウト修正
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

                if(memoResults.get(i).getMemo().length() > 10){
                    textCountResult = memoResults.get(i).getMemo().substring(0,10) + "...";
                }else{
                    textCountResult = memoResults.get(i).getMemo();
                }
                memoDate = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day);
                memoTime = String.valueOf(hour) + ":" + String.valueOf(min);
                memoText = textCountResult;
                memoDatas.add(new CardMemoData(memoText,memoTime,memoDate));
                idList.add(memoResults.get(i).getId());
            }
        }
    }

    //ToDO もう一つおなじメモが作成されてしまう
    //Drawerクリック時のメモリスト更新
    public void reloadMemoData(String subject) {
        nowSubjectName = subject;   //Drawerでクリックされた教科名をフィールド変数に代入

        idList.clear();
        getMemoDataList(subject);    //データベースから教科別メモ取得

        customMemoRecyclerViewAdapter = new CustomMemoRecyclerViewAdapter(memoDatas,idList);
        customMemoRecyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(customMemoRecyclerViewAdapter);
    }

}
