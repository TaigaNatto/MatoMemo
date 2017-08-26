package com.example.t_robop.matomemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MatomeFragment extends Fragment {

    private ArrayList<CardMatomeData> matomeDatas;

    private List<Integer> idList;

    public static String nowSubjectName;

    EmptyRecyclerView recyclerView;
    CustomMatomeRecyclerViewAdapter customMatomeRecyclerViewAdapter;

    private Realm realm;

    //MatomeFragmentのインスタンス化メソッド
    public static MatomeFragment newInstance(String subjectName) {
        Bundle args = new Bundle();
        args.putString("SUBJECT", subjectName);  //StartListActivityでクリックされた教科名を受け取って保存  @KEY SUBJECT
        MatomeFragment fragment = new MatomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Fragmentで表示するViewを作成するメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_matome_tab, container, false);

        recyclerView = (EmptyRecyclerView) view.findViewById(R.id.matome_recycler_view);
        recyclerView.setHasFixedSize(true);

        View emptyView = view.findViewById(R.id.matome_view_empty);
        recyclerView.setEmptyView(emptyView);

        //Database初期化
        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        //値の受け渡し
        Bundle args = getArguments();
        nowSubjectName = args.getString("SUBJECT"); //初期表示の教科名を保存

        matomeDatas = new ArrayList<>();
        idList = new ArrayList<>();

        getMatomeDataList(nowSubjectName);   //StartListActivityでタップした教科名のメモ一覧をデータベースから取ってきて表示

        customMatomeRecyclerViewAdapter = new CustomMatomeRecyclerViewAdapter(matomeDatas,idList);
        recyclerView.setAdapter(customMatomeRecyclerViewAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    //データベースから教科別まとめ取得してAdapterにセット
    public void getMatomeDataList(String subjectName) {

        RealmQuery<RealmMatomeEntity> matomeQuery = realm.where(RealmMatomeEntity.class);
        if(subjectName.equals("未分類")){
            matomeQuery = matomeQuery.equalTo("folder", subjectName);
        }
        else {
            RealmQuery<RealmFolderEntity> folQuery = realm.where(RealmFolderEntity.class);
            folQuery = folQuery.equalTo("folderName", subjectName);
            RealmResults<RealmFolderEntity> folResults = folQuery.findAll();

            matomeQuery = matomeQuery.equalTo("folderId", folResults.get(0).getId());
        }
        RealmResults<RealmMatomeEntity> matomeResults = matomeQuery.findAll();

        //Debug用全データ確認
        int startYear;
        int startMonth;
        int startDay;

        int endYear;
        int endMonth;
        int endDay;

        String matomeTitle;
        String matomeStartDate;
        String matomeEndDate;
        String matomeTagName;

        if (matomeResults.size() != 0) {
            //データ取得

            for (int i = 0; i < matomeResults.size(); i++) {
                startYear = matomeResults.get(i).getStartDate() / 10000;
                startMonth = matomeResults.get(i).getStartDate() / 100 % 100;
                startDay = matomeResults.get(i).getStartDate() % 100;

                endYear = matomeResults.get(i).getEndDate() / 10000;
                endMonth = matomeResults.get(i).getEndDate() / 100 % 100;
                endDay = matomeResults.get(i).getEndDate() % 100;

                if(startMonth == 0 && startDay == 0){
                    matomeStartDate = "未設定";
                }else{
                    matomeStartDate = String.valueOf(startMonth) + "/" + String.valueOf(startDay);
                }

                if(endMonth == 99 && endDay == 99){
                    matomeEndDate = "未設定";
                }else{
                    matomeEndDate = String.valueOf(endMonth) + "/" + String.valueOf(endDay);
                }
                if(matomeResults.get(i).getMatomeName() == null){
                    matomeTitle = "タイトル未設定";
                }else{
                    matomeTitle = matomeResults.get(i).getMatomeName();
                }
                matomeTagName="";
                for(int k=0;k<matomeResults.get(i).getWords().size();k++) {
                    matomeTagName = matomeTagName+" "+ matomeResults.get(i).getWords().get(k).getTagName();
                }
                matomeDatas.add(new CardMatomeData(matomeTitle,matomeStartDate,matomeEndDate,matomeTagName));
                idList.add(matomeResults.get(i).getId());
            }
        }
    }

    //ToDO もう一つ同じメモが作成されてしまう
    //Drawerクリック時のまとめリスト更新
    public void reloadMatomeData(String subject) {
        nowSubjectName = subject;

        idList.clear();
        getMatomeDataList(subject);

        customMatomeRecyclerViewAdapter = new CustomMatomeRecyclerViewAdapter(matomeDatas,idList);
        customMatomeRecyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(customMatomeRecyclerViewAdapter);
    }

}
