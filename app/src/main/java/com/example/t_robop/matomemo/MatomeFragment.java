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
import io.realm.RealmQuery;
import io.realm.RealmResults;


//ToDo まとめListの設計確認
public class MatomeFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

    //ListView matomeListView = null;
    ArrayAdapter<String> adapterMatome = null;

    Realm realm;

    //MatomeFragmentのインスタンス化メソッド
    public static MatomeFragment newInstance(String subjectName){
        Bundle args = new Bundle();
        args.putString("SUBJECT",subjectName);  //StartListActivityでクリックされた教科名を受け取って保存  @KEY SUBJECT
        MatomeFragment fragment = new MatomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Fragmentで表示するViewを作成するメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ListView matomeListView = (ListView)inflater.inflate(R.layout.activity_matome_tab,container,false);

        //Database初期化
        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        adapterMatome = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        //値の受け渡し
        Bundle args = getArguments();
        String subject = args.getString("SUBJECT"); //初期表示の教科名を保存

        //ToDo 別画面で作成されてデータベースに保存されているメモのリストを呼び出す
        //getMatomeDataList(subject);   //StartListActivityでタップした教科名のメモ一覧をデータベースから取ってきて表示

        matomeListView.setOnItemClickListener(this);

        matomeListView.setOnItemLongClickListener(this);

        matomeListView.setAdapter(adapterMatome);

        return matomeListView;
    }

    //データベースから教科別まとめ取得してAdapterにセット
    public void getMatomeDataList(String subjectName){

        RealmQuery<RealmMatomeEntity> matomeQuery = realm.where(RealmMatomeEntity.class);

        matomeQuery = matomeQuery.equalTo("matome",subjectName);

        RealmResults<RealmMatomeEntity> matomeResults = matomeQuery.findAll();

        adapterMatome.clear();

        for(int i=0; i<matomeResults.size(); i++){
            adapterMatome.add(matomeResults.get(i).getMatomeName());
        }
    }

    //Drawerクリック時のまとめリスト更新
    public void reloadMatomeData(String subject){
        adapterMatome.clear();
        getMatomeDataList(subject);
        adapterMatome.notifyDataSetChanged();
    }

    //選択されたItemをデータベースから削除
    public void removeMatomeData(String selectedItem){

        RealmQuery<RealmMatomeEntity> delQuery  = realm.where(RealmMatomeEntity.class);
        //消したいデータを指定
        delQuery.equalTo("matome",selectedItem);

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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(),MatomeActivity.class);     //MatomeActivityへIntent
        startActivity(intent);
    }

    //ダイアログ表示してまとめの消去
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
