package com.example.t_robop.matomemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class GroupEditActivity extends AppCompatActivity {

    ListView listView[];

    //ダイアログ用のEditText
    EditText dialogEditText;
    //ArrayAdapterのString型でarrayAdapterを作成
    ArrayAdapter<String> groupNameArrayAdapter[];
    //TextViewで「textView」を作成
    TextView textView;
    /*** 神 ***/
    Realm realm;
    /*** ** ***/
    //Dialogレイアウト取得用のView
    View dialogViewGroup;
    //dialog
    AlertDialog editDialog;
    //チェックされたアイテムのidを保持
    ArrayList<Integer> checkItemId;
    //listviewの状態を保持(0:通常,1:選択モード)
    int modeList = 0;
    //dialogの管理(0:新規作成,1:編集,2:削除)
    int modeDlg = 0;
    //タップされたアイテムのpositionを保持
    int tapPos = -1;
    //アイテムの数
    int itemSize = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        LayoutInflater factoryEdit = LayoutInflater.from(this);

        dialogViewGroup = factoryEdit.inflate(R.layout.dialog_group_edit, null);
        dialogEditText = (EditText) dialogViewGroup.findViewById(R.id.groupNameEdit);

        listView = new ListView[2];
        groupNameArrayAdapter = new ArrayAdapter[2];

        listView[0] = (ListView) findViewById(R.id.list);
        listView[1] = (ListView) findViewById(R.id.list_item);
        textView = (TextView) findViewById(R.id.textView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("教科編集");

        checkItemId = new ArrayList<>();
        //0なら通常のアダプター、1なら選択可能
        groupNameArrayAdapter[0] = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        groupNameArrayAdapter[1] = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_multiple_choice);

        //group削除用のlistViewを消す
        listView[1].setVisibility(View.GONE);

        ////データベース用
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        modeList = 0;
        modeDlg = 0;

        ////group編集用のListViewがタップされたとき////
        listView[0].setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //ListViewでタップされた場所のテキストをdialog内のEditTextにsetする
                        dialogEditText.setText(groupNameArrayAdapter[0].getItem(position));
                        //EditTextのカーソルを右寄せ
                        dialogEditText.setSelection(dialogEditText.getText().length());
                        //EditTextを出現
                        dialogEditText.setVisibility(View.VISIBLE);
                        //dialogモードの変更
                        modeDlg = 1;
                        //tapIdの更新
                        tapPos = position;
                        //ダイアログの表示
                        showEditDialog();
                    }
                }
        );

        ////group編集用のListViewがロングタップされたとき////
        listView[0].setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        //編集画面から削除画面への切り替え
                        listView[0].setVisibility(View.GONE);
                        listView[1].setVisibility(View.VISIBLE);
                        //モード切り替え
                        modeList = 1;
                        //ロングタップされた所のみチェック
                        listView[1].setItemChecked(position, true);
                        //ゴミ箱アイコンに変更
                        ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.gomi);

                        return true;
                    }
                }
        );

        //教科データを読み込んで表示
        loadList();

    }

    //floatingActionButton押した時
    public void plus(View v) {
        switch (modeList) {
            case 0://新規作成
                //空白化
                dialogEditText.setText("");
                //新規作成モードに
                modeDlg = 0;
                break;
            case 1://一括削除
                //チェックされたアイテムを保持するリストを初期化
                checkItemId.clear();
                //チェックされたアイテムのidのみを取得
                for (int i = 0; i < itemSize; i++) {
                    //チェックされてれば
                    if (listView[1].isItemChecked(i)) {
                        //idに変えて保持
                        checkItemId.add(getItemId(groupNameArrayAdapter[1].getItem(i)));
                    }
                }
                //削除モードに
                modeDlg = 2;
                //EditTextを非表示に
                dialogEditText.setVisibility(View.GONE);
                break;
        }
        //dialog表示
        showEditDialog();
    }

    //教科データを取得してリストを更新してくれるメソッド
    public void loadList() {
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();
        //ソート
        folderResults = folderResults.sort("id");
        for (int adpterType = 0; adpterType < 2; adpterType++) {
            //アダプターの初期化
            groupNameArrayAdapter[adpterType].clear();
            //アダプターに追加
            for (int i = 0; i < folderResults.size(); i++) {
                groupNameArrayAdapter[adpterType].add(folderResults.get(i).getFolderName());
            }
            listView[adpterType].setAdapter(groupNameArrayAdapter[adpterType]);
        }
        //数を取得
        itemSize = folderResults.size();
    }

    //dialog表示
    public void showEditDialog() {
        //dialogがまだ作られていなければ
        if (editDialog == null) {
            editDialog = new AlertDialog.Builder(this)
                    .setView(dialogViewGroup)
                    .setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBuilderGroupEdit, int which) {

                                    //editTextの文字列を取得
                                    String groupName = dialogEditText.getText().toString();

                                    switch (modeDlg) {
                                        case 0://新規作成
                                            //トランザクション開始
                                            realm.beginTransaction();
                                            //インスタンスを生成
                                            RealmFolderEntity model = realm.createObject(RealmFolderEntity.class);
                                            //書き込みたいデータをインスタンスに入れる
                                            model.setFolderName(groupName);
                                            model.setId(getNewId());
                                            //トランザクション終了 (データを書き込む)
                                            realm.commitTransaction();
                                            break;
                                        case 1://編集
                                            //何か入力されていれば
                                            if (!groupName.equals("")) {
                                                //データ更新
                                                //検索用のクエリ作成
                                                RealmQuery<RealmFolderEntity> query = realm.where(RealmFolderEntity.class);
                                                //2016/6/28で検索
                                                query.equalTo("id", getItemId(groupNameArrayAdapter[0].getItem(tapPos)));
                                                //インスタンス生成し、その中にすべてのデータを入れる
                                                RealmResults<RealmFolderEntity> results = query.findAll();
                                                //トランザクション開始
                                                realm.beginTransaction();
                                                //最初に作られたデータなのでposition0で指定
                                                RealmFolderEntity editModel = results.get(0);
                                                //書き込みたいデータをインスタンスに入れる
                                                editModel.setFolderName(groupName);
                                                //トランザクション終了 (データを書き込む)
                                                realm.commitTransaction();
                                            }
                                            break;
                                        case 2://削除
                                            deleteItems(checkItemId);
                                            loadList();
                                            //削除画面から編集画面へ切り替え
                                            listView[1].setVisibility(View.GONE);
                                            listView[0].setVisibility(View.VISIBLE);
                                            modeList = 0;
                                            //plusアイコンへ
                                            ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.plusmark);
                                            break;
                                    }
                                    //データを読み込んでリストに反映
                                    loadList();
                                }
                            }
                    )
                    .setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBuilderGroupEdit, int witch) {
                                }
                            }
                    )
                    .setNeutralButton(
                            "削除",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogBuilderGroupEdit, int witch) {
                                    switch (modeDlg) {
                                        case 0://新規
                                            //編集画面から削除画面への切り替え
                                            listView[0].setVisibility(View.GONE);
                                            listView[1].setVisibility(View.VISIBLE);
                                            modeList = 1;
                                            //ゴミ箱アイコンに変更
                                            ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.gomi);
                                            break;
                                        case 1://編集
                                            //編集画面から削除画面への切り替え
                                            listView[0].setVisibility(View.GONE);
                                            listView[1].setVisibility(View.VISIBLE);
                                            modeList = 1;
                                            listView[1].setItemChecked(tapPos, true);
                                            //ゴミ箱アイコンに変更
                                            ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.gomi);
                                            break;
                                        case 2://削除
                                            deleteItems(checkItemId);
                                            loadList();
                                            //削除画面から編集画面へ切り替え
                                            listView[1].setVisibility(View.GONE);
                                            listView[0].setVisibility(View.VISIBLE);
                                            modeList = 0;
                                            //plusアイコンへ
                                            ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.plusmark);
                                            break;
                                    }
                                }
                            }
                    )
                    .create();
        }
        String dialogTitles[] = {"新規作成", "編集", "削除しますか"};
        editDialog.setTitle(dialogTitles[modeDlg]);
        editDialog.show();
    }

    //指定したアイテムをRealmから削除してくれるメソッド
    public void deleteItems(ArrayList<Integer> items) {
        // クエリを発行
        RealmQuery<RealmFolderEntity> delQuery = realm.where(RealmFolderEntity.class);
        //消したいデータを指定
        for (int i = 0; i < items.size(); i++) {
            delQuery.or().equalTo("id", items.get(i));

        }
        //指定されたデータを持つデータのみに絞り込む
        final RealmResults<RealmFolderEntity> delR = delQuery.findAll();
        // 変更操作はトランザクションの中で実行する必要あり
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // すべてのオブジェクトを削除
                delR.deleteAllFromRealm();
            }
        });
    }

    //指定したアイテムのidを取得するメソッド
    public int getItemId(String itemName) {
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        folderQuery.equalTo("folderName", itemName);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();
        return folderResults.get(0).getId();
    }

    //既存のidの最大値+1した数値を返してくれるメソッド
    public int getNewId() {
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();
        int maxId = 0;
        if (folderResults.size() > 0) {
            for (int i = 0; i < folderResults.size(); i++) {
                int nowId = folderResults.get(i).getId();
                if (nowId > maxId) {
                    maxId = nowId;
                }
            }
        }
        return maxId + 1;
    }

    //メニューバーの作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);  //res\menu\optionsのlayoutを読み込む
        return true;
    }

    //ToDo Intent先の作成とIntent処理の追加
    //メニューが選択されたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        //addしたときのIDで識別
        switch (item.getItemId()) {
            case android.R.id.home:
                //削除モードであれば戻す
                if (modeList == 1) {
                    //削除画面から編集画面へ切り替え
                    listView[1].setVisibility(View.GONE);
                    listView[0].setVisibility(View.VISIBLE);
                    modeList = 0;
                }
                //通常モードであれば終了
                else if (modeList == 0) {
                    finish();
                }
                break;
            case R.id.tag_settings:
                Log.d("menu", "タグ設定へ");  //TagEditActivityへIntent
                break;

            /*
            case R.id.important_setting:
                Log.d("menu", "重要度設定へ");     //ImportantEditActivityへIntent
                break;
                */

            case R.id.editFolder:
                intent = new Intent(this, GroupEditActivity.class);  //GroupEditActivityへIntent
                startActivity(intent);
                break;
        }
        //startActivity(intent);

        return true;
    }


}