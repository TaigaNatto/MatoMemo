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
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MatomeFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

    private ArrayAdapter<String> adapterMatome = null;

    private ArrayList<Integer> idList;

    private String nowSubjectName;

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
        View view = inflater.inflate(R.layout.activity_matome_tab,container,false);

        //Database初期化
        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        //値の受け渡し
        Bundle args = getArguments();
        nowSubjectName = args.getString("SUBJECT"); //初期表示の教科名を保存

        ListView matomeListView = (ListView)view.findViewById(R.id.matome_list);
        TextView emptyMatomeText = (TextView)view.findViewById(R.id.emptyMatomeView);
        matomeListView.setEmptyView(emptyMatomeText);

        idList=new ArrayList<>();

        adapterMatome = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        getMatomeDataList(nowSubjectName);   //StartListActivityでタップした教科名のメモ一覧をデータベースから取ってきて表示

        matomeListView.setOnItemClickListener(this);
        matomeListView.setOnItemLongClickListener(this);

        matomeListView.setAdapter(adapterMatome);

        return view;
    }

    //データベースから教科別まとめ取得してAdapterにセット
    public void getMatomeDataList(String subjectName) {

        RealmQuery<RealmMatomeEntity> matomeQuery = realm.where(RealmMatomeEntity.class);

        matomeQuery = matomeQuery.equalTo("folder", subjectName);

        RealmResults<RealmMatomeEntity> matomeResults = matomeQuery.findAll();

        //Debug用全データ確認
        String allMatomeData;
        String[] allWordsData = new String[100];   //マーカーが引かれている箇所の単語

        if(matomeResults.size() != 0){
            //データ取得
            /*
            for(int w=0; w<matomeResults.get(w).getWords().size(); w++){
                allWordsData[w] = matomeResults.get(w).getWords().get(w).getWord();
            }

            //ToDo まとめ機関未設定のときに、開始日が0または終了日が999999999のときは「未設定」を表示、マーカーが動かないので下一つが未検証、idはなんのために存在？　
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
            for (int i=0;i<matomeResults.size();i++){
                adapterMatome.add(matomeResults.get(i).getMatomeName());
                idList.add(matomeResults.get(i).getId());
            }
        }

        /*
        for (int i = 0; i < matomeResults.size(); i++) {
            adapterMatome.add(matomeResults.get(i).getMatomeName());
        }
        */
    }

    //Drawerクリック時のまとめリスト更新
    public void reloadMatomeData(String subject) {
        nowSubjectName = subject;

        adapterMatome.clear();
        idList.clear();
        getMatomeDataList(subject);
        adapterMatome.notifyDataSetChanged();
    }

    //選択されたItemをデータベースから削除
    public void removeMatomeData(String selectedItem) {

        RealmQuery<RealmMatomeEntity> delQuery = realm.where(RealmMatomeEntity.class);
        //消したいデータを指定
        delQuery.equalTo("folder", selectedItem);

        final RealmResults<RealmMatomeEntity> delR = delQuery.findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                delR.deleteAllFromRealm();
            }
        });
    }

    //まとめ内容へIntent
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        Intent intent = new Intent(getActivity(), MatomeActivity.class);     //MatomeActivityへIntent
        //idの指定
        intent.putExtra("ID",idList.get(pos));
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

                removeMatomeData(clickedMatomeItem);   //データベースから削除     //ToDo 削除してから画面遷移して戻ってくるとListに復活しているので、データベース上で削除できていない？
            }
        });

        alertDig.create().show();

        return true;
    }
}
