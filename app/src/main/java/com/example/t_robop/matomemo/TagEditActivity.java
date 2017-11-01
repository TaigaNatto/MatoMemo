package com.example.t_robop.matomemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class TagEditActivity extends AppCompatActivity {

    //タグと色用のlist
    ListView tagList;
    //adapterに表示するアイテム用のArrayList
    ArrayList<RealmWordEntity> wordList;

    //ダイアログ
    AlertDialog alertDlg;

    /**
     * ダイアログレイアウト関連
     **/
    //ダイアログのレイアウトを取得するView
    View inputView;
    EditText dialogEdit;
    TextView dialogColor;
    SeekBar dialogSeek;

    //一時的に色を保管するための変数。初期値は白
    String tempColor = "#ffffff";

    int dialogModeFlag = -1;    //1: 新規作成　2: 編集
    int editTagPosition = 0;    //List内のItemでクリックされたpositionを入れる変数
    String beforeEditTagName = null;

    Toolbar toolbar;

    /*** 神 ***/
    Realm realm;

    /*** ** ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_edit);

        /*realmの初期化*/
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //関連付け
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tagList = (ListView) findViewById(R.id.tag_list);
        TextView emptyTagText = (TextView) findViewById(R.id.emptyTagView);
        tagList.setEmptyView(emptyTagText);

        //arrayList初期化
        wordList = new ArrayList<>();

        toolbar.setTitle("タグ設定");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /*ダイアログレイアウトの呼び出し*/
        //ダイアログレイアウトの読み込み
        LayoutInflater factory = LayoutInflater.from(this);
        inputView = factory.inflate(R.layout.colorpicker_dialog, null);
        //ダイアログ内の関連付け
        dialogEdit = (EditText) inputView.findViewById(R.id.tag_edit);
        dialogColor = (TextView) inputView.findViewById(R.id.tag_color);
        dialogSeek = (SeekBar) inputView.findViewById(R.id.tag_bar);
        //シークバーのリスナー
        dialogSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //シークバーが動く度に色が変わる
                dialogColor.setBackgroundColor(Color.parseColor(colorConverter(tempColor, (100 - progress) * 2)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //指を離したタイミングでデータを保持
                tempColor = colorConverter(tempColor, (100 - seekBar.getProgress()) * 2);
            }
        });
        //ダイアログのセット
        setDialog();

        //ListViewのListener登録
        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editTagPosition = position;
                //wordオブジェクトの生成
                RealmWordEntity wordObj = wordList.get(position);
                //タグ名を取得してセット
                dialogEdit.setText(wordObj.getTagName());
                beforeEditTagName = dialogEdit.getText().toString();
                //色を取得してセット
                dialogColor.setBackgroundColor(Color.parseColor(wordObj.getColor()));
                //シークバーを初期化
                dialogSeek.setProgress(100);
                dialogModeFlag = 2;
                //dialogを表示
                alertDlg.show();
                alertDlg.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                alertDlg.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
                alertDlg.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
            }
        });

        /*tag一覧のデータが欲しい！*/
        //検索用のクエリ作成
        RealmQuery<RealmWordEntity> tagQuery = realm.where(RealmWordEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる
        RealmResults<RealmWordEntity> tagResults = tagQuery.findAll();
        //取得したデータ数だけ追加
        for (int i = 0; i < tagResults.size(); i++) {
            //アドォッ！！
            wordList.add(tagResults.get(i));
        }
        //リストにセット
        setListItem(this);

    }

    //+ボタン押された時
    public void plus(View v) {
        //editText内を初期化
        dialogEdit.setText(HtmlCompat.fromHtml("<span style=background-color:" + "#ff00ff" + ">" + "</span>"));
        //色を初期化（白に）
        dialogColor.setBackgroundColor(Color.parseColor("#ffffff"));
        //seekbarを初期値に
        dialogSeek.setProgress(100);
        dialogModeFlag = 1;
        //dialog召喚
        alertDlg.show();
        alertDlg.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        alertDlg.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
        alertDlg.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
    }

    //dialogセットするメソッド
    public void setDialog() {
        if (alertDlg == null) {
            alertDlg = new AlertDialog.Builder(TagEditActivity.this)
                    .setView(inputView)
                    .setPositiveButton(
                            "作成",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // OK ボタンクリック処理
                                    //タグ名が入力されていれば
                                    if (dialogEdit.getText().toString().length() != 0) {
                                        //既に同じ名前のタグが存在してるか確認
                                        int checkList = checkListItem(dialogEdit.getText().toString());
                                        //存在してない場合は追加
                                        if (checkList == -1) {
                                            //保存処理
                                            //wordのオブジェクトの作成
                                            RealmWordEntity wordObj = new RealmWordEntity();
                                            wordObj.setTagName(dialogEdit.getText().toString());
                                            wordObj.setColor(tempColor);
                                            if (dialogModeFlag == 1) {  //tag新規作成時
                                                //wordListに追加
                                                wordList.add(wordObj);
                                            } else if (dialogModeFlag == 2) {   //tag編集時
                                                wordList.set(editTagPosition, wordObj);  //List内でタップされたpositionのtag名を更新
                                            }

                                            //listviewにセット
                                            setListItem(getApplicationContext());

                                            //tag新規作成時
                                            if(dialogModeFlag == 1){
                                                //Realmに追加保存
                                                //トランザクション開始
                                                realm.beginTransaction();
                                                //インスタンスを生成
                                                RealmWordEntity model = realm.createObject(RealmWordEntity.class);
                                                //書き込みたいデータをインスタンスに入れる
                                                model.setTagName(dialogEdit.getText().toString());
                                                model.setColor(tempColor);

                                                //トランザクション終了 (データを書き込む)
                                                realm.commitTransaction();
                                            }
                                            //tag名編集時
                                            else if(dialogModeFlag == 2){
                                                //編集前のdialogEditのテキストとデータベース上のtagNameを比較
                                                RealmResults<RealmWordEntity> editTag = realm.where(RealmWordEntity.class).equalTo("tagName",beforeEditTagName).findAll();
                                                realm.beginTransaction();
                                                RealmWordEntity model = editTag.get(0);
                                                model.setTagName(dialogEdit.getText().toString());  //データベース上の編集するtagNameを新しいテキストでセットする
                                                realm.commitTransaction();
                                            }



                                        }
                                        //存在してれば更新
                                        else {
                                            //トランザクション開始
                                            realm.beginTransaction();
                                            //既に存在してるタグ名であれば色のみ変更
                                            //wordのオブジェクトの作成
                                            RealmWordEntity wordObj = wordList.get(checkList);
                                            //色だけ変える
                                            wordObj.setColor(tempColor);
                                            //上書き
                                            wordList.set(checkList, wordObj);
                                            //listViewに反映
                                            setListItem(getApplicationContext());

                                            //Realm更新
                                            //検索用のクエリ作成
                                            RealmQuery<RealmWordEntity> query = realm.where(RealmWordEntity.class);
                                            //上書きするタグ名で検索
                                            query.equalTo("tagName", dialogEdit.getText().toString());
                                            //インスタンス生成し、その中にすべてのデータを入れる
                                            RealmResults<RealmWordEntity> results = query.findAll();

                                            //１つしか検索に引っかからないっはずなのでpositionは0
                                            RealmWordEntity editModel = results.get(0);
                                            //colorを更新
                                            editModel.setColor(tempColor);
                                            //トランザクション終了 (データを書き込む)
                                            realm.commitTransaction();
                                        }
                                    }
                                    //タグ名に何も書いてなければメッセージ
                                    else {
                                        Toast.makeText(getApplicationContext(), "タグ名が入力されてません", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                    .setNeutralButton("削除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Later button pressed
                            //リスト内に存在するか確認
                            int checkList = checkListItem(dialogEdit.getText().toString());
                            //存在してなければメッセージ
                            if (checkList == -1) {
                                Toast.makeText(getApplicationContext(), "しっかりしてくださいよ", Toast.LENGTH_SHORT).show();
                            }
                            //してたら削除
                            else {
                                //削除
                                wordList.remove(checkList);
                                //リストにセット
                                setListItem(getApplicationContext());

                                //Realmのデータの削除
                                // クエリを発行
                                RealmQuery<RealmWordEntity> delQuery = realm.where(RealmWordEntity.class);
                                //消したいデータを指定
                                delQuery.equalTo("tagName", dialogEdit.getText().toString());
                                //指定されたデータを持つデータのみに絞り込む
                                final RealmResults<RealmWordEntity> delR = delQuery.findAll();
                                // 変更操作はトランザクションの中で実行する必要あり
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        // すべてのオブジェクトを削除
                                        delR.deleteAllFromRealm();
                                    }
                                });
                            }
                        }
                    })
                    .setNegativeButton(
                            "キャンセル",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Cancel ボタンクリック処理
                                }
                            })

                    // 表示
                    .create();
        }
    }

    //dialog内の色ボタン押された時
    public void color_change(View v) {
        //ボタンに設定されてるタグから色を取得
        String color = "#" + v.getTag().toString();
        //colorの一時保存
        tempColor = color;
        //dialog内のTextViewに反映
        dialogColor.setBackgroundColor(Color.parseColor(color));
        //dialog内のseekbarを初期位置へ
        dialogSeek.setProgress(dialogSeek.getMax());
    }

    //16進数の色コードと減少値を入れると計算後の16進数の色コードを返してくれるメソッド
    public String colorConverter(String color, int value) {
        //r部分の取得
        String r = color.substring(1, 3);
        //g部分の取得
        String g = color.substring(3, 5);
        //b部分の取得
        String b = color.substring(5, 7);
        Log.d("SOS", r + g + b);
        //それぞれ10進数に変換
        int rI = Integer.decode("0x" + r);
        int gI = Integer.decode("0x" + g);
        int bI = Integer.decode("0x" + b);
        //引数の減少値分の余裕があればそれぞれの値を変える
        if (rI + value <= 255) {
            rI = rI + value;
        }
        if (gI + value <= 255) {
            gI = gI + value;
        }
        if (bI + value <= 255) {
            bI = bI + value;
        }
        //それぞれ16進数に変換
        r = Integer.toHexString(rI);
        g = Integer.toHexString(gI);
        b = Integer.toHexString(bI);
        //変換して、もしそれが一桁のときは0をつけてあげる
        if (r.length() == 1) {
            r = "0" + r;
        }
        if (g.length() == 1) {
            g = "0" + g;
        }
        if (b.length() == 1) {
            b = "0" + b;
        }
        Log.d("SOS", r + g + b);
        //色コードになるように合体して返す
        return "#" + r + g + b;
    }

    //現在のデータからadapterを再生成してlistviewに反映するメソッド
    public void setListItem(Context context) {
        //adapterの再生成
        TagListItemAdapter adapter = new TagListItemAdapter(context, R.layout.tag_list_item, wordList);
        //listviewに反映
        tagList.setAdapter(adapter);
    }

    //同じ名前があるか確認して、あれば要素のpositionを、無ければ-1を返すメソッド
    public int checkListItem(String word) {
        for (int i = 0; i < wordList.size(); i++) {
            if (wordList.get(i).getTagName().equals(word)) {
                //見つかりました！
                return i;
            }
        }
        //見つかりませんでした！！！
        return -1;
    }

    /*
    //メニューバーの作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);  //res\menu\optionsのlayoutを読み込む
        return true;
    }
    */
    //メニューが選択されたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Intent intent;

        //addしたときのIDで識別
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            /*
            case R.id.editFolder:
                intent = new Intent(this, GroupEditActivity.class);  //GroupEditActivityへIntent
                startActivity(intent);
                break;
            */

        }
        return true;
    }

}
