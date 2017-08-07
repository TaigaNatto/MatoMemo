package com.example.t_robop.matomemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.example.t_robop.matomemo.R.id.txtmemo;

public class WritingActivity extends ActionBarActivity implements TextWatcher {

    Intent intent=new Intent();
    int mode = intent.getIntExtra("MODE",0);
    //メモを新規作成する場合は０、編集するときは１としています。

    int change = 0;

    TextView textView;
    EditText editText;

    Realm realm;

    //キーボード検知用のインスタンス
    DetectableSoftKeyLayout layout;
    //
    Button markBtn;

    //markする色を保持する(デフォルトは白)
    String tempColor="#ffffff";
    //markするタグを保持する
    String tempTagName=null;

    //タグ一覧を保持する
    String tags[];

    //設定されたタグデータの保持
    ArrayList<MatomeWord> mWordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //関連付け
        textView = (TextView) findViewById(R.id.txt);
        editText = (EditText) findViewById(R.id.txtmemo);
        layout=(DetectableSoftKeyLayout)findViewById(R.id.liner_layout);
        markBtn=(Button) findViewById(R.id.mark_button);
        //キーボード検知用のリスナーをセット
        layout.setListener(listner);

        mWordList=new ArrayList<>();

        editText.addTextChangedListener(this);

        if (mode == 0) {

            textView.setVisibility(View.GONE);

            //キーボードを出現させる
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else {

            editText.setVisibility(View.GONE);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    textView.setVisibility(View.GONE);
                    editText.setVisibility(View.VISIBLE);
                    editText.requestFocus();

                    //キーボードを出現させる
                    InputMethodManager manager =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.toggleSoftInput(1, InputMethodManager.SHOW_IMPLICIT);
                }
            });

        }

        //タグ一覧の取得
        //検索用のクエリ作成
        RealmQuery<RealmWordEntity> wordQuery = realm.where(RealmWordEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmWordEntity> wordResults = wordQuery.findAll();
        //タグ一覧保持の容量をセット
        tags=new String[wordResults.size()];
        //タグ一覧に同期
        for (int i=0;i<wordResults.size();i++){
            tags[i]=wordResults.get(i).getTagName();
        }

        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                //menu.removeItem(android.R.id.paste);
                //menu.removeItem(android.R.id.cut);
                //menu.removeItem(android.R.id.copy);

                MenuItem item = menu.add(Menu.NONE, R.id.mark, Menu.NONE, "Mark");
                item.setTitle("マーク");

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();
                switch(id) {
                    case R.id.mark:
                        // 独自の処理
                        if(tempTagName!=null) {
                            //マーカー付け
                            wordMark();
                            Log.d("SSSS", "マークされたよ！");
                        }
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //操作前のEditTextの状態を取得する
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //操作中のEditTextの状態を取得する
    }

    @Override
    public void afterTextChanged(Editable s) {
        //操作後のEditTextの状態を取得する
        // テキスト変更後に変更されたテキストを取り出す
        String inputStr = s.toString();

// 文字長をカウントして
        if (inputStr.length() != 0) {
            change = 1;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("", "ACTION_DOWN");
                EditText txtTitle = (EditText) findViewById(txtmemo);
                txtTitle.setFocusable(true);
                txtTitle.setFocusableInTouchMode(true);
                txtTitle.setEnabled(true);

                break;
        }
        return false;
    }

    //ToDo Intent先の作成とIntent処理の追加
    //メニューが選択されたときの処理
    //戻るボタンを押したときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;

        boolean result = true;

        /*
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        */


        switch (id) {
            case android.R.id.home:

                if (change == 1) {
                    // 確認ダイアログの生成
                    AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
                    alertDlg.setTitle("");
                    alertDlg.setMessage("メモの内容を保存しますか？");
                    alertDlg.setPositiveButton(
                            "キャンセル",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // キャンセル ボタンクリック処理

                                }
                            });
                    alertDlg.setNeutralButton(
                            "保存する",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 保存する ボタンクリック処理

                                    //トランザクション開始
                                    realm.beginTransaction();
                                    //インスタンスを生成
                                    RealmMemoEntity model = realm.createObject(RealmMemoEntity.class);

                                    if (mode == 0) {
                                        //新規の場合は今日の日付を取得
                                        final Calendar c = Calendar.getInstance();
                                        int newYear = c.get(Calendar.YEAR);
                                        int newMonth = c.get(Calendar.MONTH) + 1;
                                        int newDay = c.get(Calendar.DAY_OF_MONTH);

                                        int Today = newYear * 10000 + newMonth * 100 + newDay;

                                        //書き込みたいデータをインスタンスに入れる
                                        model.setDate(Today);

                                        //タグが１つでもセットされてれば
                                        if(mWordList.size()>0){
                                            for (int i = 0; i< mWordList.size(); i++){
                                                MatomeWord mWord= realm.createObject(MatomeWord.class);
                                                mWord.setWord(mWordList.get(i).getWord());
                                                mWord.setTagName(mWordList.get(i).getTagName());
                                                model.words.add(mWord);
                                            }
                                        }
                                    }

                                    //書き込みたいデータをインスタンスに入れる
                                    model.setMemo(editText.getText().toString());
                                    //トランザクション終了 (データを書き込む)
                                    realm.commitTransaction();

                                    finish();
                                }
                            });
                    alertDlg.setNegativeButton(
                            "保存しないで戻る",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 保存しないで戻る ボタンクリック処理
                                    finish();
                                }
                            });

                    // 表示
                    alertDlg.create().show();

                } else {
                    finish();
                }
                break;

            case R.id.tag_settings:
                Log.d("menu", "タグ設定へ");  //TagEditActivityへIntent
                intent = new Intent(this, TagEditActivity.class);
                startActivity(intent);
                break;

            /*
            case R.id.important_setting:
                Log.d("menu","重要度設定へ");     //ImportantEditActivityへIntent
                break;
             */

            case R.id.editFolder:
                intent = new Intent(this, GroupEditActivity.class);  //GroupEditActivityへIntent
                startActivity(intent);
                break;

            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;
    }

    //メニューバーの作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);  //res\menu\optionsのlayoutを読み込む
        return true;
    }

    //キーボード検知用のリスナー
    DetectableSoftKeyLayout.OnSoftKeyShownListener listner = new DetectableSoftKeyLayout.OnSoftKeyShownListener() {
        @Override
        public void onSoftKeyShown(boolean isShown) {
            if (isShown) {
                // ソフトキーボードが表示されている場合
                // ボタンを表示する
                markBtn.setVisibility(View.VISIBLE);
            } else {
                // ソフトキーボードが表示されてなければ、非表示にする
                markBtn.setVisibility(View.GONE);
            }
        }
    };

    //色選択のためのDialogを表示
    public void marker_mode(View v){
        int defaultItem = 0; // デフォルトでチェックされているアイテム
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);
        new AlertDialog.Builder(this)
                .setTitle("マーカーを選択")
                .setSingleChoiceItems(tags, defaultItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItems.clear();
                        checkedItems.add(which);
                    }
                })
                .setPositiveButton("変更", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!checkedItems.isEmpty()) {
                            //検索用のクエリ作成
                            RealmQuery<RealmWordEntity> wordQuery = realm.where(RealmWordEntity.class);
                            //選択されたタグのもののみ取得
                            wordQuery.equalTo("tagName",tags[checkedItems.get(0)]);
                            //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
                            RealmResults<RealmWordEntity> wordResults = wordQuery.findAll();
                            //設定する色を変更
                            tempColor=wordResults.get(0).getColor();
                            //設定するタグを変更
                            tempTagName=wordResults.get(0).getTagName();
                        }
                    }
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }

    //選択した文字列にmakerを付けてくれるうえに保存までしてくれるメソッド
    public void wordMark(){
        //選択の始めの位置を取得
        int start=editText.getSelectionStart();
        //選択の終わりの位置を取得
        int end=editText.getSelectionEnd();
        //選択部分の文字列の取得
        String text=editText.getText().toString().substring(start,end);
        //色を変えてEditTextにセット
        editText.setText(Html.fromHtml(giveMark(editText.getText().toString(),text,tempColor)));
        //一時保存
        MatomeWord mWord = new MatomeWord();
        mWord.setTagName(tempTagName);
        mWord.setWord(text);
        mWordList.add(mWord);
    }

    //単語にマーカー付けてくれる最高のメソッド
    public String giveMark(String memo,String word,String color){
        //htmlで背景色変え
        String text=memo.replaceAll(word,"<span style=background-color:"+color+">"+word+"</span>");
        //変更後の文字列を返す
        return text;
    }


}
