package com.example.t_robop.matomemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.example.t_robop.matomemo.R.id.edit;
import static com.example.t_robop.matomemo.R.id.txtmemo;

public class WritingActivity extends AppCompatActivity {

    int id;
    //メモを新規作成する場合は０、編集するときは１としています。
    String folder;

    int folderId;

    int change = 0;

    TextView textView;
    EditText editText;
    ArrayAdapter<String> spinnerAdapter;

    MatoMemoListActivity matoMemoListActivity;

    Realm realm;

    //キーボード検知用のインスタンス
    DetectableSoftKeyLayout layout;

    LinearLayout linearLayout;
    LinearLayout buttonLayout;
    //
    Button markBtn;

    //markする色を保持する(デフォルトは白)
    String tempColor = "#ffffff";
    //markするタグを保持する
    String tempTagName = null;

    //タグ一覧を保持する
    String tags[];

    //設定されたタグデータの保持
    ArrayList<MatomeWord> mWordList;

    String memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();
        //todo idで管理します！(新規は-1,編集はidを飛ばしてください！！)
        id = intent.getIntExtra("MODE", -1);
        //todo intentでフォルダ名ほしい！
        folder = intent.getStringExtra("SUBJECT NAME");

        //関連付け
        textView = (TextView) findViewById(R.id.txt);
        editText = (EditText) findViewById(R.id.txtmemo);
        layout = (DetectableSoftKeyLayout) findViewById(R.id.liner_layout);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        buttonLayout = (LinearLayout) findViewById(R.id.button_layout);
        markBtn = (Button) findViewById(R.id.mark_button);
        //キーボード検知用のリスナーをセット
        layout.setListener(listner);

        mWordList = new ArrayList<>();
        matoMemoListActivity = new MatoMemoListActivity();

        // editText.addTextChangedListener(this);

        if (id == -1) {

            textView.setVisibility(View.GONE);

            memo = "";

            //キーボードを出現させる
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else {

            //idからメモの取得
            //検索用のクエリ作成
            RealmQuery<RealmMemoEntity> query = realm.where(RealmMemoEntity.class);
            query.equalTo("id", id);
            //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
            RealmResults<RealmMemoEntity> results = query.findAll();
            //１つしか入ってないはずなので0
            memo = results.get(0).getMemo();

            //Viewに持ってきたメモのセット
            textView.setText(memo);
            editText.setText(memo);

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
        tags = new String[wordResults.size()];
        //タグ一覧に同期
        for (int i = 0; i < wordResults.size(); i++) {
            tags[i] = wordResults.get(i).getTagName();
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
                switch (id) {
                    case R.id.mark:
                        // 独自の処理
                        if (tempTagName != null) {
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
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // アイテムを追加します
        spinnerAdapter.add("未分類");
        matoMemoListActivity.getFolderDataList(realm, spinnerAdapter);
        Spinner spinner = (Spinner) findViewById(R.id.Spinner);
        // アダプターを設定します
        spinner.setAdapter(spinnerAdapter);

        int index = 0;
        for (int i = 0; i < spinnerAdapter.getCount(); i++) {
            if (spinnerAdapter.getItem(i).equals(folder)) {
                index = i;
                break;
            }
        }
        spinner.setSelection(index);

        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得します
                folder = (String) spinner.getSelectedItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        matoMemoListActivity.reloadFolderDataList(realm, spinnerAdapter);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            String newmemo = editText.getText().toString();
            if (memo.equals(newmemo)) {
                finish();
            } else {
                saveMemoDialog();
            }
            return true;
        }
        return false;
    }


    //ToDo Intent先の作成とIntent処理の追加
//メニューが選択されたときの処理
//戻るボタンを押したときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        Intent intent;

        boolean result = true;

        /*
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        */


        switch (id) {
            case android.R.id.home:


                String newmemo = editText.getText().toString();
                if (memo.equals(newmemo)) {
                    finish();
                } else {
                    saveMemoDialog();
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

    public void saveMemoDialog() {
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

                        //新規作成時
                        if (WritingActivity.this.id == -1) {
                            //インスタンスを生成
                            RealmMemoEntity model = realm.createObject(RealmMemoEntity.class);//新規の場合は今日の日付を取得
                            final Calendar c = Calendar.getInstance();
                            int newYear = c.get(Calendar.YEAR);
                            int newMonth = c.get(Calendar.MONTH) + 1;
                            int newDay = c.get(Calendar.DAY_OF_MONTH);
                            int newHour = c.get(Calendar.HOUR_OF_DAY);
                            int newMinute = c.get(Calendar.MINUTE);
                            int newSecond = c.get(Calendar.SECOND);

                            int Today = newYear * 10000 + newMonth * 100 + newDay;
                            int nowTime = newHour * 10000 + newMinute * 100 + newSecond;

                            //書き込みたいデータをインスタンスに入れる
                            model.setDate(Today);
                            model.setTime(nowTime);

                            //タグが１つでもセットされてれば
                            if (mWordList.size() > 0) {
                                for (int i = 0; i < mWordList.size(); i++) {
                                    MatomeWord mWord = realm.createObject(MatomeWord.class);
                                    mWord.setWord(mWordList.get(i).getWord());
                                    mWord.setTagName(mWordList.get(i).getTagName());
                                    model.words.add(mWord);
                                }
                            }

                            //書き込みたいデータをインスタンスに入れる
                            model.setMemo(editText.getText().toString());

                            //フォルダ名をセット
                            model.setFolder(folder);

                            RealmQuery<RealmFolderEntity> folQuery = realm.where(RealmFolderEntity.class);
                            folQuery = folQuery.equalTo("folderName", folder);
                            RealmResults<RealmFolderEntity> folResults = folQuery.findAll();

                            model.setFolderId(folResults.get(0).getId());

                            //新規idをセット
                            model.setId(getNewId());
                        }
                        //更新時
                        else {
                            //検索用のクエリ作成
                            RealmQuery<RealmMemoEntity> query = realm.where(RealmMemoEntity.class);
                            //持ってきたidで検索
                            query.equalTo("id", WritingActivity.this.id);
                            //インスタンス生成し、その中にすべてのデータを入れる
                            RealmResults<RealmMemoEntity> results = query.findAll();
                            //１つしかないはずなのでposition0で指定
                            RealmMemoEntity editModel = results.get(0);
                            //書き込みたいデータをインスタンスに入れる
                            editModel.setMemo(editText.getText().toString());
                            //タグが１つでもセットされてれば
                            if (mWordList.size() > 0) {
                                //一度中身を初期化
                                editModel.words.clear();
                                //タグを追加
                                for (int i = 0; i < mWordList.size(); i++) {
                                    MatomeWord mWord = realm.createObject(MatomeWord.class);
                                    mWord.setWord(mWordList.get(i).getWord());
                                    mWord.setTagName(mWordList.get(i).getTagName());
                                    editModel.words.add(mWord);
                                }
                            }
                        }

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

    }

    //メニューバーの作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);  //res\menu\optionsのlayoutを読み込む
        return true;
    }
    /*
    public void move(View v) {
        int topMargin = 100;
        layout.layout(0, topMargin, layout.getWidth(), topMargin + layout.getHeight());
    }
    */
    //キーボード検知用のリスナー
    DetectableSoftKeyLayout.OnSoftKeyShownListener listner = new DetectableSoftKeyLayout.OnSoftKeyShownListener() {
        @Override
        public void onSoftKeyShown(boolean isShown) {
            if (isShown) {
                // ソフトキーボードが表示されている場合
                // ボタンを表示する
                // linearLayout.setScaleY (30);
                // buttonLayout.setScaleY (5);
               //  buttonLayout.setTranslationY(10);
                //move(layout);
            } else {
                // ソフトキーボードが表示されてなければ、非表示にする
                markBtn.setVisibility(View.GONE);
            }
        }
    };

    //色選択のためのDialogを表示
    public void marker_mode(View v) {
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
                            wordQuery.equalTo("tagName", tags[checkedItems.get(0)]);
                            //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
                            RealmResults<RealmWordEntity> wordResults = wordQuery.findAll();
                            //設定する色を変更
                            tempColor = wordResults.get(0).getColor();
                            //設定するタグを変更
                            tempTagName = wordResults.get(0).getTagName();
                        }
                    }
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }

    //選択した文字列にmakerを付けてくれるうえに保存までしてくれるメソッド
    public void wordMark() {
        //選択の始めの位置を取得
        int start = editText.getSelectionStart();
        //選択の終わりの位置を取得
        int end = editText.getSelectionEnd();
        //選択部分の文字列の取得
        String text = editText.getText().toString().substring(start, end);
        //色を変えてEditTextにセット
        /*
        CharSequence str = text;
        SpannableString spannableString = new SpannableString(str);
        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.parseColor(tempColor));
        spannableString.setSpan(backgroundColorSpan,0,spannableString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        String setText = editText.getText().toString().replaceAll(text,spannableString.toString());
        editText.setText(setText);
        */
        editText.setText(HtmlCompat.fromHtml(giveMark(editText.getText().toString(), text, tempColor)));
        //一時保存
        MatomeWord mWord = new MatomeWord();
        mWord.setTagName(tempTagName);
        mWord.setWord(text);
        mWordList.add(mWord);
    }

    //単語にマーカー付けてくれる最高のメソッド
    public String giveMark(String memo, String word, String color) {
        //htmlで背景色変え
        //ToDo replaceAllは文字列に対して指定したパターンに一致する部分の全てに適用されるので、issue#102が発生する
        String text = memo.replaceAll(word, "<font color=" + color + ">" + word + "</font>");
        //変更後の文字列を返す
        return text;
    }

    //既存のidの最大値+1した数値を返してくれるメソッド
    public int getNewId() {
        //検索用のクエリ作成
        RealmQuery<RealmMemoEntity> memoQuery = realm.where(RealmMemoEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmMemoEntity> memoResults = memoQuery.findAll();
        int maxId = 0;
        if (memoResults.size() > 0) {
            for (int i = 0; i < memoResults.size(); i++) {
                int nowId = memoResults.get(i).getId();
                if (nowId > maxId) {
                    maxId = nowId;
                }
            }
        }
        return maxId + 1;
    }
}
