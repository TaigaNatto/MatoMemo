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


public class MatomeFragment extends Fragment {

    private String[] allMatomeTitles = null;
    private String[] allMatomeStartDates = null;
    private String[] allMatomeEndDates = null;
    private String[] allMatomeTagNames = null;

    public static ArrayList<Integer> idList = null;

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

        idList = new ArrayList<>();

        getMatomeDataList(nowSubjectName);   //StartListActivityでタップした教科名のメモ一覧をデータベースから取ってきて表示

        customMatomeRecyclerViewAdapter = new CustomMatomeRecyclerViewAdapter(allMatomeTitles,allMatomeStartDates,allMatomeEndDates,allMatomeTagNames);
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


        allMatomeTitles = new String[matomeResults.size()];
        allMatomeStartDates = new String[matomeResults.size()];
        allMatomeEndDates = new String[matomeResults.size()];
        allMatomeTagNames = new String[matomeResults.size()];

        if (matomeResults.size() != 0) {
            //データ取得
            /*
            for(int w=0; w<matomeResults.get(w).getWords().size(); w++){
                allWordsData[w] = matomeResults.get(w).getWords().get(w).getWord();
            }

            //ToDo まとめ機関未設定のときに、開始日が0または終了日が999999999のときは「未設定」を表示、マーカーが動かないので下一つが未検証　
            for(int i=0; i<matomeResults.size(); i++){
                allMatomeData = "id: " + matomeResults.get(i).getId() + "\n" +
                        "まとめ名前: " + matomeResults.get(i).getMatomeName() + "\n" +
                        "まとめ期間開始日: " + matomeResults.get(i).getStartDate() + "\n" +     //ToDo 0のとき、String型で「未設定」を表示
                        "まとめ期間終了日: " + matomeResults.get(i).getEndDate() + "\n" +       //ToDo 999999999のとき、String型で「未設定」を表示
                        "教科名: " + matomeResults.get(i).getFolder() + "\n" +
                        "マーカー単語: " + allWordsData[i];

                adapterMatome.add(allMatomeData);
            }
            */
            for (int i = 0; i < matomeResults.size(); i++) {
                startYear = matomeResults.get(i).getStartDate() / 10000;
                startMonth = matomeResults.get(i).getStartDate() / 100 % 100;
                startDay = matomeResults.get(i).getStartDate() % 100;

                endYear = matomeResults.get(i).getEndDate() / 10000;
                endMonth = matomeResults.get(i).getEndDate() / 100 % 100;
                endDay = matomeResults.get(i).getEndDate() % 100;

                if(startMonth == 0 && startDay == 0){
                    allMatomeStartDates[i] = "未設定";
                }else{
                    allMatomeStartDates[i] = String.valueOf(startMonth) + "/" + String.valueOf(startDay);
                }

                if(endMonth == 99 && endDay == 99){
                    allMatomeEndDates[i] = "未設定";
                }else{
                    allMatomeEndDates[i] = String.valueOf(endMonth) + "/" + String.valueOf(endDay);
                }
                if(matomeResults.get(i).getMatomeName() == null){
                    allMatomeTitles[i] = "タイトル未設定";
                }else{
                    allMatomeTitles[i] = matomeResults.get(i).getMatomeName();
                }
                allMatomeTagNames[i]="";
                for(int k=0;k<matomeResults.get(i).getWords().size();k++) {
                    allMatomeTagNames[i] = allMatomeTagNames[i]+" "+ matomeResults.get(i).getWords().get(k).getTagName();
                }
                idList.add(matomeResults.get(i).getId());
            }
        }
    }

    //Drawerクリック時のまとめリスト更新
    public void reloadMatomeData(String subject) {
        nowSubjectName = subject;

        idList.clear();
        getMatomeDataList(subject);

        customMatomeRecyclerViewAdapter = new CustomMatomeRecyclerViewAdapter(allMatomeTitles,allMatomeStartDates,allMatomeEndDates,allMatomeTagNames);
        customMatomeRecyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(customMatomeRecyclerViewAdapter);
    }

    /*
    //選択されたItemをデータベースから削除
    public void removeMatomeData(String selectedItem) {

        //realm.beginTransaction();

        RealmQuery<RealmMatomeEntity> delQuery = realm.where(RealmMatomeEntity.class);
        //消したいデータを指定
        //delQuery.equalTo("folder", selectedItem);

        final RealmResults<RealmMatomeEntity> delR = delQuery.equalTo("matomeName", selectedItem).findAll();     //まとめフォルダの名前と一致したものを削除

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                delR.deleteAllFromRealm();
            }
        });

        //realm.commitTransaction();
    }

    //まとめ内容へIntent
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        Intent intent = new Intent(getActivity(), MatomeActivity.class);     //MatomeActivityへIntent
        //idの指定
        intent.putExtra("ID", idList.get(pos));
        startActivity(intent);
    }

    //ダイアログ表示してまとめの消去
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        final String clickedMatomeItem = (String) adapterView.getItemAtPosition(position);   //クリックしたpositionからItemを取得

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
                adapterMatome.remove(clickedMatomeItem);   //リストから削除

                removeMatomeData(clickedMatomeItem);   //データベースから削除
            }
        });

        alertDig.create().show();

        return true;
    }
    */
}
