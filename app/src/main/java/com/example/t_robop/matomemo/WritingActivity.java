package com.example.t_robop.matomemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;

import static com.example.t_robop.matomemo.R.id.txt;
import static com.example.t_robop.matomemo.R.id.txtmemo;

public class WritingActivity extends ActionBarActivity implements TextWatcher {

    int kari = 0;
    //仮にメモを新規作成する場合は０、編集するときは１としています。

    int change = 0;

    TextView textView;
    EditText editText;

    Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        textView = (TextView) findViewById(R.id.txt);
        editText = (EditText) findViewById(R.id.txtmemo);

        editText.addTextChangedListener(this);

        if (kari == 0) {

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

                                    if (kari == 0) {
                                        //新規の場合は今日の日付を取得
                                        final Calendar c = Calendar.getInstance();
                                        int newYear = c.get(Calendar.YEAR);
                                        int newMonth = c.get(Calendar.MONTH) + 1;
                                        int newDay = c.get(Calendar.DAY_OF_MONTH);

                                        int Today = newYear * 10000 + newMonth * 100 + newDay;


                                        //トランザクション開始
                                        realm.beginTransaction();
                                        //インスタンスを生成
                                        RealmMemoEntity model = realm.createObject(RealmMemoEntity.class);
                                        //書き込みたいデータをインスタンスに入れる
                                        model.setDate(Today);
                                        //トランザクション終了 (データを書き込む)
                                        realm.commitTransaction();

                                    }
                                    //トランザクション開始
                                    realm.beginTransaction();
                                    //インスタンスを生成
                                    RealmMemoEntity model = realm.createObject(RealmMemoEntity.class);
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


}
