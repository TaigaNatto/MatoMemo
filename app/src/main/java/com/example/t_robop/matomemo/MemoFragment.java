package com.example.t_robop.matomemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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


public class MemoFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

    //ListView memoListView = null;   //メモのListView
    ArrayAdapter<String> adapterMemo = null;    //ListViewのAdapter

    private String nowSubjectName;  //現在表示しているメモの教科名

    Realm realm;

    //MemoFragmentのインスタンス化メソッド
    public static MemoFragment newInstance(String subjectName){
        // Bundleとかここに書く
        Bundle args = new Bundle();
        args.putString("SUBJECT",subjectName);  //StartListActivityでクリックされた教科名を受け取って保存  @KEY SUBJECT
        MemoFragment fragment = new MemoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Fragmentで表示するViewを作成するメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        //ToDo メモが無い場合はTextViewで「メモなし」を表示 (優先度低)
        ListView memoListView = (ListView)inflater.inflate(R.layout.activity_memo_tab,container,false);

        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        adapterMemo = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        //MatomemoListActivityとの値の受け渡し
        Bundle args = getArguments();
        nowSubjectName = args.getString("SUBJECT"); //初期表示の教科名を保存

        //ToDo 別画面で作成されてデータベースに保存されているメモのリストを呼び出す
        getMemoDataList(nowSubjectName);   //StartListActivityでタップした教科名のメモ一覧をデータベースから取ってきて表示

        memoListView.setOnItemClickListener(this);

        memoListView.setOnItemLongClickListener(this);

        memoListView.setAdapter(adapterMemo);

        return memoListView;
    }

    //Debug用データベース設定    教科別メモセット
    //ToDo
    public void setMemoDataTest(String subjectName){

        realm.beginTransaction();

        RealmMemoEntity testMemo = realm.createObject(RealmMemoEntity.class);

        //Debug用　Drawer内でタップした教科のメモをDebug用に作成したセット
        testMemo.setFolder(subjectName);
        testMemo.setMemo(subjectName+"メモ");

        realm.commitTransaction();

    }

    //データベースから教科別メモ取得
    public void getMemoDataList(String subjectName){    //引数：　Drawer内でクリックした教科名
        //検索用のクエリ作成
        RealmQuery<RealmMemoEntity> memoQuery = realm.where(RealmMemoEntity.class);

        memoQuery = memoQuery.equalTo("folder",subjectName);  //引数で受け取った教科名のメモを取得

        RealmResults<RealmMemoEntity> memoResults = memoQuery.findAll();    //インスタンス生成し、その中に取得したすべてのデータを入れる

        adapterMemo.clear();    //一旦Listを全部削除

        for(int i=0; i<memoResults.size(); i++){
            adapterMemo.add(memoResults.get(i).getMemo());    //メモをListViewのAdapterに入れる
        }
    }

    //Drawerクリック時のメモリスト更新
    public void reloadMemoData(String subject){
        nowSubjectName = subject;   //Drawerでクリックされた教科名をフィールド変数に代入

        adapterMemo.clear();
        getMemoDataList(subject);    //データベースから教科別メモ取得
        adapterMemo.notifyDataSetChanged();
    }

    //選択されたItemをデータベースから削除
    //ToDo このままだとメモタイトル同名のものが全てデータベースから消えてしまう
    public void removeMemoData(String selectedItem){
        // クエリを発行
        RealmQuery<RealmMemoEntity> delQuery  = realm.where(RealmMemoEntity.class);
        //消したいデータを指定
        delQuery.equalTo("memo",selectedItem);
        //指定されたデータを持つデータのみに絞り込む
        final RealmResults<RealmMemoEntity> delR = delQuery.findAll();
        // 変更操作はトランザクションの中で実行する必要あり
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // すべてのオブジェクトを削除
                delR.deleteAllFromRealm();
            }
        });
    }

    //メモリストのクリック処理
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //ToDo メモリストのItemをタップしたときに、教科名　のデータを持ってWritingActivityにIntent
        Intent intent = new Intent(getActivity(),WritingActivity.class);
        intent.putExtra("MODE",1);    //数値受け渡し　1: メモ確認　0: 新規作成   //ここでは1を送る
        intent.putExtra("SUBJECT NAME",nowSubjectName);     //教科名受け渡し
        startActivity(intent);
    }

    //メモリストのロングクリック処理
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        Log.d("test","onLongItemClick");
        final String item = (String)adapterView.getItemAtPosition(position);   //クリックしたpositionからItemを取得

        //消去確認のダイアログ
        AlertDialog.Builder alertDig = new AlertDialog.Builder(getActivity());
        alertDig.setTitle("");
        alertDig.setMessage("メモを消去しますか？");

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
                adapterMemo.remove(item);   //リストから削除

                removeMemoData(item);   //データベースから削除
            }
        });

        alertDig.create().show();

        return true;    //ここtrueじゃないと通常クリックも呼ばれてしまう
    }
}
