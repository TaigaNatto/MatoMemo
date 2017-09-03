package com.example.t_robop.matomemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.app.DatePickerDialog.OnDateSetListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class FolderCreateActivity extends AppCompatActivity implements OnDateSetListener {

    private TextView dateTextView[] = new TextView[2]; //まとめ期間のTextView
    EditText edittext;  //まとめタイトル
    ListView listView;  //タグ表示
    DatePick dateFragment[];
    int flag = 0;     // 0:まとめ期間開始　1: まとめ期間終了

    Realm realm;

    //今日の日付の取得
    final Calendar c = Calendar.getInstance();

    //Datepickerで取得した日付
    int frontYear = c.get(Calendar.YEAR);
    int frontMonth = c.get(Calendar.MONTH);
    int frontDay = c.get(Calendar.DAY_OF_MONTH);
    int frontDate = 0;

    int rearYear = c.get(Calendar.YEAR);
    int rearMonth = c.get(Calendar.MONTH);
    int rearDay = c.get(Calendar.DAY_OF_MONTH);
    int rearDate = 99999999;

    //実際に使うのはこいつら
    int yearPear[] = {frontYear, rearYear};
    int monthPear[] = {frontMonth, rearMonth};
    int dayPear[] = {frontDay, rearDay};
    int datePear[] = {frontDate, rearDate};

    //adapterに表示するアイテム用のArrayList
    ArrayList<RealmWordEntity> wordlist;

    // ArrayList<String> wordlist;

    //ArrayAdapterのString型でarrayAdapterを作成
    ArrayAdapter<String> arrayAdapter;

    //チェックが入っているTagをDatabaseに保存するための配列
    SparseBooleanArray checkarray;

    //まとめの教科名
    String folder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_create);

        dateTextView[0] = (TextView) findViewById(R.id.button1);
        dateTextView[1] = (TextView) findViewById(R.id.button2);
        edittext = (EditText) findViewById(R.id.txtmatome);
        listView = (ListView) findViewById(R.id.tagList);
        listView.setEmptyView(findViewById(R.id.txtempty));
        dateFragment = new DatePick[]{new DatePick(2017, 1, 1), new DatePick(2017, 1, 1)};

        //ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("まとめ作成");
        toolbar.setBackgroundColor(Color.parseColor("#c84f5d"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        wordlist = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_multiple_choice);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //教科名取得
        Intent intent = getIntent();
        folder = intent.getStringExtra("SUBJECT NAME");

        //DatabaseからTagの一覧を取得
        getTagDataList();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        getTagDataList();
    }

    public void showDatePickerDialog(View v) {
        //タグをint型に変換して取得
        int tag = Integer.parseInt(v.getTag().toString());
        dateFragment[tag] = new DatePick(yearPear[tag], monthPear[tag], dayPear[tag]);
        dateFragment[tag].show(getSupportFragmentManager(), "datePicker");
        flag = tag;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.d("date", String.valueOf(year));
        Log.d("date", String.valueOf(monthOfYear));
        Log.d("date", String.valueOf(dayOfMonth));
        yearPear[flag] = year;
        monthPear[flag] = monthOfYear;
        dayPear[flag] = dayOfMonth;
        datePear[flag] = yearPear[flag] * 10000 + (monthPear[flag] + 1) * 100 + dayPear[flag];

        if (datePear[0] <= datePear[1]) {
            dateTextView[flag].setText(
                            String.valueOf(yearPear[flag]) + "/ " +
                            String.valueOf(monthPear[flag] + 1) + "/ " +
                            String.valueOf(dayPear[flag]));
        } else {
            Toast.makeText(this, "誤った日付が設定されています。", Toast.LENGTH_LONG).show();
        }
    }

    //保存ボタン処理
    public void matomecreateClick(View view) {
        //保存可能か判定
        if (getSaveJudge()) {
            //まとめをデータベースに保存
            saveMatome();
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //保存可能か判定
            if (getSaveJudge()) {
                //確認ダイアログ表示
                saveMemoCheckedDialog();
            } else {
                finish();
            }
            return true;
        }
        return false;
    }

    //DatabaseからTagの一覧を取得
    public void getTagDataList() {
        RealmQuery<RealmWordEntity> wordQuery = realm.where(RealmWordEntity.class);

        RealmResults<RealmWordEntity> wordResults = wordQuery.findAll();
        //取ってきたデータ全部欲しい時はこんな感じに
        wordlist.clear();
        arrayAdapter.clear();
        for (int i = 0; i < wordResults.size(); i++) {

            wordlist.add(wordResults.get(i));
            //arrayAdapterにwordlistを入れる
            arrayAdapter.add(wordlist.get(i).getTagName());
            //arrayAdapterをlistViewに入れる
            listView.setAdapter(arrayAdapter);

        }
    }

    //todo Realmに保存するならまとめなきゃ
    //まとめを保存する
    public void saveMatome() {
        //トランザクション開始
        realm.beginTransaction();
        RealmMatomeEntity model = realm.createObject(RealmMatomeEntity.class);

        if (edittext.length() != 0) {
            model.setMatomeName(edittext.getText().toString());

        } else {
            model.setMatomeName("タイトル未設定");
        }

        model.setStartDate(datePear[0]);
        model.setEndDate(datePear[1]);

        //ListViewチェックボックスで選択されているものを配列に代入
        checkarray = listView.getCheckedItemPositions();
        for (int i = 0; i < checkarray.size(); i++) {
            int at = checkarray.keyAt(i);
            if (checkarray.get(at)) {
                Log.d("example", "選択されている項目:" + listView.getItemAtPosition(at).toString()); //選択されているListの文字列を取得
                Log.d("example", "そのキー" + at);  //選択されているListの要素数を取得
                MatomeWord mWord = realm.createObject(MatomeWord.class);
                mWord.setTagName(listView.getItemAtPosition(at).toString());
                //書き込みたいデータをインスタンスに入れる
                model.words.add(mWord);
            }
        }

        model.setFolder(folder);

        RealmQuery<RealmFolderEntity> folQuery = realm.where(RealmFolderEntity.class);
        folQuery = folQuery.equalTo("folderName", folder);
        RealmResults<RealmFolderEntity> folResults = folQuery.findAll();

        if (folResults.size() != 0) {
            model.setFolderId(folResults.get(0).getId());
        } else {
            model.setFolderId(-1);
        }

        model.setId(getNewId());

        //トランザクション終了
        realm.commitTransaction();

        Toast.makeText(this, "まとめを作成しました", Toast.LENGTH_LONG).show();
    }


    private void saveMemoCheckedDialog() {

        // 確認ダイアログの生成
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
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
                        saveMatome();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();

        Intent intent;

        boolean result = true;

        switch (item.getItemId()) {
            //戻るボタンを押したときの処理
            case android.R.id.home:
                if (getSaveJudge()) {
                    saveMemoCheckedDialog();
                } else {
                    finish();
                }
                break;
            //メニューが選択されたときの処理
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

    //既存のidの最大値+1した数値を返してくれるメソッド
    public int getNewId() {
        //検索用のクエリ作成
        RealmQuery<RealmMatomeEntity> matomeQuery = realm.where(RealmMatomeEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmMatomeEntity> matomeResults = matomeQuery.findAll();
        int maxId = 0;
        if (matomeResults.size() > 0) {
            for (int i = 0; i < matomeResults.size(); i++) {
                int nowId = matomeResults.get(i).getId();
                if (nowId > maxId) {
                    maxId = nowId;
                }
            }
        }
        return maxId + 1;
    }

    //保存可能か判定してくれるメソッド
    public Boolean getSaveJudge() {
        checkarray = listView.getCheckedItemPositions();
        if (!(checkarray.size() == 0 && datePear[0] == 0 && datePear[1] == 99999999 && edittext.length() == 0)) {
            //まとめをデータベースに保存可能
            return true;
        }
        return false;
    }

}
