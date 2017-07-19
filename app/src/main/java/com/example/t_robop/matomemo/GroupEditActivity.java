package com.example.t_robop.matomemo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

import io.realm.Realm;
import io.realm.RealmFieldType;

import io.realm.RealmQuery;
import io.realm.RealmResults;




import static android.R.attr.alertDialogIcon;
import static android.R.attr.checked;
import static android.R.attr.dial;
import static android.R.attr.id;

public class GroupEditActivity extends AppCompatActivity {
    //ArrayListのString型でarrayListを作成
    ArrayList<String> arrayList;
    ArrayList<Integer> arrayNum;

    //ListViewでlistViewを作成
    ListView listView;
    //EditTextでeditViewを作成

    Cursor cursor = null;

    EditText editView;
    //arrayListに入れる用
    String groupName = " ";
    //listViewの要素の個数
    int itemNum = 0;
    //checkがついていないときtrue※
    boolean checking = false;
    //重複していないときtrue※
    boolean original = true;
    //ArrayAdapterのString型でarrayAdapterを作成
    ArrayAdapter<String> arrayAdapter;
    //TextViewで「textView」を作成
    TextView textView;

    /*** 神 ***/
    Realm realm;
    /*** ** ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);
        //レイアウトで作ったListViewをjavaで使えるようにする
        listView = (ListView) findViewById(R.id.list_item);
        //TextViewの関連付け
        textView = (TextView) findViewById(R.id.textView);
        //"arrayList"をArrayListでインスタンス化
        arrayList = new ArrayList<>();
        //arrayListに"memo"を追加
        arrayList.add(groupName);

     /*   //"arrayList"をArrayListでインスタンス化
        arrayNum = new ArrayList<>();
        //set
        arrayNum.add(0);
        arrayNum.add(1);
        arrayNum.add(2);
        arrayNum.add(3);
        arrayNum.add(4);
       */

        /***これ必須だからみんな書いて***/
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        /*******************/





        //"arrayAdapter"をcheckBox付きのArrayAdapterでインスタンス化
        arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_multiple_choice);



        ///フォルダのデータが欲しいとき！///
        //検索用のクエリ作成
        RealmQuery<RealmFolderEntity> folderQuery = realm.where(RealmFolderEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmFolderEntity> folderResults = folderQuery.findAll();
        /***使い方は↑のメモと同じ***/
        for(int i=0;i<folderResults.size();i++){
            String text = folderResults.get(i).getFolderName();
            //arrayListをtextで初期化
            arrayList.set(0, text);
            //グループがすでにあるとき、textViewを空白で更新
            textView.setText(" ");
            ////データベースで実装
            // arrayListをarrayAdapterに追加する
             arrayAdapter.add(arrayList.get(0));
            //listViewの要素数を追加
            itemNum++;
            //arrayAdapterをlistViewに入れる
            listView.setAdapter(arrayAdapter);
        }

        /*///
        //listViewの要素数分だけ繰り返す
        for(int i = 0;i < itemNum;i++) {
            //listViewの要素数分だけ繰り返す
            for (int j = 0; j < itemNum; j++) {
                //arrayNum(j)とiが同じなら実行
                if (arrayNum.get(j) == i) {

                    //arrayListをarrayAdapterに追加する
                    arrayAdapter.add(arrayList.get(j));
                    //arrayAdapterをlistViewに入れる
                    listView.setAdapter(arrayAdapter);
                    break;
                }
            }
        }*/


        //listViewがタップされた事を取れるようにする
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    //listViewがタップされたときに実行
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //selを0で初期化して作成
                        checking = false;
                        //listViewの要素数分繰り返す
                        for(int x = itemNum - 1;x >= 0;x--){
                            //checkがついているなら実行
                            if (listView.isItemChecked(x)){
                                //checkingをtrueに
                                checking = true;
                            }
                        }
                        //checkingがtrueなら削除のアイコン、違うならプラスのアイコン
                        if (checking){
                            //削除のアイコンに変更
                            ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.gomi);

                            //listView.setBackgroundColor(Color.rgb(255, 255, 255));
                        }else{
                            //プラスのアイコンに変更
                            ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.plusmark);
                            //listView.setBackgroundColor(Color.rgb(127, 127, 255));
                        }
                    }
                }
        );
    }
    //フローティングアクションボタンがタップされた際に実行
    public void plus(View v) {
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

            //checkが1つもついていないとき実行
        if(checking) {
            //listViewの要素数分だけ繰り返す
            for (int x = itemNum - 1; x >= 0; x--) {
                //もしチェックボックスにチェックがついているなら実行
                if (listView.isItemChecked(x)) {

                    String temp = (String)listView.getItemAtPosition(x);

                    //チェックがついてる行を削除
                    arrayAdapter.remove(arrayAdapter.getItem(x));

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //arrayNum.remove(arrayNum.get(x));

                    //for(int i = 1;i<=itemNum;i++){
                    //    arrayNum.set(i,i);
                    //}
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    ////String temp = arrayAdapter.getContext(x);

                    RealmQuery<RealmFolderEntity> query  = realm.where(RealmFolderEntity.class);
                    //消したいデータを指定 (以下の場合はmemoデータの「memo」が「test」のものを指定)
                    query.equalTo("folderName",temp);
                    //指定されたデータを持つデータのみに絞り込む
                    final RealmResults<RealmFolderEntity> results = query.findAll();
                    // 変更操作はトランザクションの中で実行する必要あり
                    realm.executeTransaction(new Realm.Transaction() {
             @Override
             public void execute(Realm realm) {
                 // すべてのオブジェクトを削除
                 results.deleteAllFromRealm();
             }
         });




                    //listViewの要素数を1減らす
                    itemNum--;
                    //checkの状態を元にもどす
                    checking = false;
                    //画像をplusに変更
                    ((android.support.design.widget.FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.plusmark);
                }
            }
            //checkBoxの中身を初期化
            listView.setAdapter(arrayAdapter);

            //TextViewに「フォルダを作成してください」を代入
            if (itemNum == 0) {
                textView.setText("フォルダを作成してください");
            }
        }else {
            //"editView"を使用可能に
            editView = new EditText(this);
            //ダイアログを使用可能に
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            //ダイアログにeditView(editText)を追加
            dialog.setView(editView);
            //ダイアログの題名を"グループの名前"で追加
            dialog.setTitle("グループの名前");
            dialog.create();

            /*
            editView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });
            */


            //ダイアログのokが押されたら実行
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override

                //ダイアログのokが押されたら実行
                public void onClick(DialogInterface dialog, int whichButton) {
                    //groupNameをeditViewのデータで初期化
                    groupName = editView.getText().toString();

                    //////////////////////////////////////////
                    //重複してない状態
                    original = true;
                    //ListViewの要素分繰り返す
                    for (int i = 0; i < itemNum; i++) {
                        //もし重複しているなら実行
                        //groupNameがarrayAdapterのi番目の要素と同じか、"未分類"なら実行
                        if (groupName .equals(arrayAdapter.getItem(i)) || groupName .equals("未分類")) {
                            //重複している状態に更新
                            original = false;
                            // 第3引数は、表示期間（LENGTH_SHORT、または、LENGTH_LONG）
                            //トーストを表示
                            Toast.makeText(GroupEditActivity.this, "同名のファイルがすでに存在します", Toast.LENGTH_SHORT).show();
                            ///***///トーストを中央に表示///***///
                            //Toast toast = Toast.makeText(GroupEditActivity.this, "同名のファイルがすでに存在します", Toast.LENGTH_SHORT);
                            //toast.setGravity(Gravity.CENTER, 0, 0);
                            //toast.show();

                            break;
                        }
                    }

                    //editViewの中身がnullでないなら実行
                    if(groupName .equals("") == false && original) {
                 //
                 //arrayListをgroupNameで初期化
                        arrayList.set(0, groupName);
                        //arrayListをarrayAdapterに追加する
                        arrayAdapter.add(arrayList.get(0));
                        //arrayAdapterをlistViewに入れる
                        listView.setAdapter(arrayAdapter);

                        //実装時は別の処理////////////////////////////////////////////////////////////////////////////////////
                        //
                        //arrayNum.add(itemNum);


                        //listViewの要素数を追加
                        itemNum++;
                 //

                        /////////
                        /***データを書き込みたいとき！***/
                         //トランザクション開始
                              realm.beginTransaction();
                         //インスタンスを生成
                              RealmFolderEntity model = realm.createObject(RealmFolderEntity.class);
                         //書き込みたいデータをインスタンスに入れる
                              model.setFolderName(groupName);
                         //トランザクション終了 (データを書き込む)
                              realm.commitTransaction();
                        /******************************/

                    }
                    //TextViewに「」を代入
                    if (itemNum != 0){
                        textView.setText(" ");
                    }
                }
            });
            dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                //ダイアログのokが押されたら実行
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            //ダイアログを表示
            dialog.show();

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            // なんらかの処理

            Intent intent=new Intent(this,StartListActivity.class);

            //intent.putExtra("ID",arrayNum);

            startActivity(intent);

            return true;
        }
        return false;
    }

}