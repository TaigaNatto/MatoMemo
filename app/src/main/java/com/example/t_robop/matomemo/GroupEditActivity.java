package com.example.t_robop.matomemo;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import static android.R.attr.checked;

public class GroupEditActivity extends AppCompatActivity {
    //ArrayListのString型でarrayListを作成
    ArrayList<String> arrayList;
    //ListViewでlistViewを作成
    ListView listView;
    //EditTextでeditViewを作成
    EditText editView;
    //arrayListに入れる用
    String groupName = " ";
    //listViewの要素の個数
    int itemNum = 0;
    //checkがついていないときtrue※
    boolean checking = false;
    //ArrayAdapterのString型でarrayAdapterを作成
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);
        //レイアウトで作ったListViewをjavaで使えるようにする
        listView = (ListView) findViewById(R.id.list_item);
        //"arrayList"をArrayListでインスタンス化
        arrayList = new ArrayList<>();
        //arrayListに"memo"を追加
        arrayList.add(groupName);
        //"arrayAdapter"をcheckBox付きのArrayAdapterでインスタンス化
        arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_multiple_choice);
        //listViewがタップされた事を取れるようにする
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    //listViewがタップされたときに実行
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //selを0で初期化して作成
                        int sel = 0;
                        //listViewの要素数分繰り返す
                        for(int x = itemNum - 1;x >= 0;x--){
                            //checkがついているなら実行
                            if (listView.isItemChecked(x)){
                                //selを1で初期化
                                sel = 1;
                            }
                        }

                        if (sel == 1){
                            //削除のアイコンに変更
                            checking = true;
                        }else{
                            //プラスのアイコンに変更
                            checking = false;
                        }
                    }
                }
        );
    }
    //フローティングアクションボタンがタップされた際に実行
    public void plus(View v) {
            //checkが1つもついていないとき実行
        if(checking) {
            //listViewの要素数分だけ繰り返す
            for(int x = itemNum - 1;x >= 0;x--){
                //もしチェックボックスにチェックがついているなら実行
                if (listView.isItemChecked(x)){
                    //チェックがついてる行を削除
                    arrayAdapter.remove(arrayAdapter.getItem(x));
                    //listViewの要素数を1減らす
                    itemNum--;
                    //checkの状態を元にもどす
                    checking = false;
                }
            }
            //checkBoxの中身を初期化
            listView.setAdapter(arrayAdapter);
        }else {
            //"editView"を使用可能に
            editView = new EditText(this);
            //ダイアログを使用可能に
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            //ダイアログの題名を"グループの名前"で追加
            dialog.setTitle("グループの名前");
            //ダイアログにeditView(editText)を追加
            dialog.setView(editView);
            //ダイアログの中に"OK"ボタンを追加
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                //ダイアログのokが押されたら実行
                public void onClick(DialogInterface dialog, int whichButton) {
                    //memoをeditViewのデータで初期化
                    groupName = editView.getText().toString();
                    //arrayListをmemoで初期化
                    arrayList.set(0, groupName);
                    //arrayListをarrayAdapterに追加する
                    arrayAdapter.add(arrayList.get(0));
                    //arrayAdapterをlistViewに入れる
                    listView.setAdapter(arrayAdapter);
                    //listViewの要素数を追加
                    itemNum++;
                }
            });
            //ダイアログを表示
            dialog.show();
        }
    }
}