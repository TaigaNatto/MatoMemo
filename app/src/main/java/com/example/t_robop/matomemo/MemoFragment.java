package com.example.t_robop.matomemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by user on 2017/06/20.
 */

public class MemoFragment extends ListFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    //ListView memoListView = null;   //メモのListView
    ArrayAdapter<String> adapterMemo = null;    //ListViewのAdapter

    private MatoMemoListActivity matoMemoListActivity;

    Realm realm;

    //MemoFragmentのインスタンス化メソッド
    public static MemoFragment newInstance(String subjectName){
        // Bundleとかここに書く
        Bundle args = new Bundle();
        args.putString("SUBJECT",subjectName);  //StartListActivityでクリックされた教科名を受け取って保存  @KEY SUBJECT
        MemoFragment fragment = new MemoFragment(); //Fragmentの初期化
        fragment.setArguments(args);
        return fragment;
    }

    //Fragmentで表示するViewを作成するメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        //メモのListViewのレイアウトをインフレート
        ListView memoListView = (ListView)inflater.inflate(R.layout.activity_memo_tab,container,false);

        //Database初期化
        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        //Adapterのインスタンスを作成
        adapterMemo = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        //値の受け渡し
        Bundle args = getArguments();
        String subject = args.getString("SUBJECT"); //初期表示の教科名を保存
        //ToDo 別画面で作成されてデータベースに保存されているメモのリストを呼び出す
        setMemoDataTest(subject);   //Debug用データベース設定    StartListActivityでタップした教科名のメモ一覧をデータベースにセット
        getMemoDataList(subject);   //StartListActivityでタップした教科名のメモ一覧をデータベースから取ってきて表示

        //メモリストのItemタップ時の処理     WritingActivityへのIntent
        memoListView.setOnItemClickListener(this);

        //メモリストのItem長押し時の処理     選択メモの削除
        memoListView.setOnItemLongClickListener(this);

        memoListView.setAdapter(adapterMemo);   //メモを画面に表示

        return memoListView;
    }

    //MatoMemoListActivityとの連携  横線あるけど気にしないで
    @Override
    public void onAttach(Activity activity){
        matoMemoListActivity = (MatoMemoListActivity)activity;
        super.onAttach(matoMemoListActivity);
    }

    //Debug用データベース設定    教科別メモセット
    //ToDo
    public void setMemoDataTest(String subjectName){
        //トランザクション開始
        realm.beginTransaction();
        //インスタンスを生成
        RealmMemoEntity testMemo = realm.createObject(RealmMemoEntity.class);

        //Debug用　Drawer内でタップした教科のメモをDebug用に作成したセット
        testMemo.setFolder(subjectName);
        testMemo.setMemo(subjectName+"メモ");

        //トランザクション終了 (データを書き込む)
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
        adapterMemo.clear();
        getMemoDataList(subject);
        adapterMemo.notifyDataSetChanged();
    }

    //選択されたItemをデータベースから削除
    //ToDo このままだとメモタイトル同名のものが全てデータベースから消えてしまう
    public void removeMemoData(String selectedItem){
        // クエリを発行
        RealmQuery<RealmMemoEntity> delQuery  = realm.where(RealmMemoEntity.class);
        //消したいデータを指定 (以下の場合はmemoデータの「memo」が「test」のものを指定)
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

    //ToDo Intent処理できてない
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //ToDo メモリストのItemをタップしたときに、・日付　・時間　・メモ内容　・教科名　のデータを持ってWritingActivityにIntent
        matoMemoListActivity.move();    //WritingActivityへのIntentメソッド   //処理内容はMatoMemoListActivityにある
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

        String item = (String)adapterView.getItemAtPosition(position);   //クリックしたpositionからItemを取得
        adapterMemo.remove(item);   //リストから削除

        removeMemoData(item);   //データベースから削除    //ToDo ダイアログ表示して消去確認メッセ出そう
        return false;
    }
}
