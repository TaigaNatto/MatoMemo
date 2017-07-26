package com.example.t_robop.matomemo;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import io.realm.Realm;

import static com.example.t_robop.matomemo.R.id.txt;
import static com.example.t_robop.matomemo.R.id.txtmemo;

public class WritingActivity extends ActionBarActivity {

    int kari = 0;
    //仮にメモを新規作成する場合は０、編集するときは１としています。

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        boolean result = true;

        /*
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        */


            switch (id) {
                case android.R.id.home:

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


                                 /*       if (kari == 0) {
                                            //新規の場合は今日の日付を取得
                                            final Calendar c = Calendar.getInstance();
                                            int newYear = c.get(Calendar.YEAR);
                                            int newMonth = c.get(Calendar.MONTH);
                                            int newDay = c.get(Calendar.DAY_OF_MONTH);
                                            int newdate = newYear + newMonth + newDay;

                                            //トランザクション開始
                                            realm.beginTransaction();
                                            //インスタンスを生成
                                            RealmMemoEntity model = realm.createObject(RealmMemoEntity.class);
                                            //書き込みたいデータをインスタンスに入れる
                                            model.setDate(newdate);
                                            //トランザクション終了 (データを書き込む)
                                            realm.commitTransaction();
                                        }*/
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

                    break;
                default:
                    result = super.onOptionsItemSelected(item);
            }

        return result;
    }
}
