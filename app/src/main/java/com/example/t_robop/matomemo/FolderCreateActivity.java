package com.example.t_robop.matomemo;

import android.content.DialogInterface;
import android.content.Intent;
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


public class FolderCreateActivity extends AppCompatActivity implements OnDateSetListener, View.OnClickListener {

    private TextView textViewfront; //まとめ期間開始日のTextView
    private TextView textViewrear;  //まとめ期間終了日のTextView
    EditText edittext;  //まとめタイトル
    ListView listView;  //タグ表示
    DatePick newFragment;
    DatePick secondFragment;
    int flag=0;     // 1:まとめ期間開始　2: まとめ期間終了

    Realm realm;

    //今日の日付の取得
    final Calendar c = Calendar.getInstance();

    //Datepickerで取得した前半の日付
    int frontYear = c.get(Calendar.YEAR);
    int frontMonth = c.get(Calendar.MONTH);
    int frontDay = c.get(Calendar.DAY_OF_MONTH);
    int frontDate = 99999999;

    int rearYear = c.get(Calendar.YEAR);
    int rearMonth = c.get(Calendar.MONTH);
    int rearDay = c.get(Calendar.DAY_OF_MONTH);
    int rearDate = 99999999;

    int yearV[]= new int[2];

    //adapterに表示するアイテム用のArrayList
    ArrayList<RealmWordEntity> wordlist;

   // ArrayList<String> wordlist;

    //ArrayAdapterのString型でarrayAdapterを作成
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_create);

        textViewfront = (TextView) findViewById(R.id.button1);
        textViewrear = (TextView) findViewById(R.id.button2);

        edittext = (EditText) findViewById(R.id.txtmatome);

        listView = (ListView) findViewById(R.id.tagList);

        Button btn = (Button)findViewById(R.id.createbutton);

        newFragment = new DatePick(2017,1,1);
        secondFragment=new DatePick(2017,1,1);


        //ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


       // ((FolderCreateActivity)getActivity()).getSupportActionBar().


        Realm.init(this);
        realm = Realm.getDefaultInstance();


        wordlist=new ArrayList<>();


        arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_multiple_choice);

        listView.setItemsCanFocus(false);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //DatabaseからTagの一覧を取得
        getTagDataList();


        btn.setOnClickListener(this);

    }


    public void showDatePickerDialog(View v) {
        switch (v.getId()) {
            //まとめ期間開始日
            case R.id.button1:
                newFragment = new DatePick(frontYear,frontMonth,frontDay);
                newFragment.show(getSupportFragmentManager(), "datePicker");
                flag=1;
                break;

            //まとめ期間終了日
            case R.id.button2:
                secondFragment = new DatePick(rearYear,rearMonth,rearDay);
                secondFragment.show(getSupportFragmentManager(), "datePicker");
                flag=2;
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        switch (flag) {
            case 1:
                Log.d("date",String.valueOf(year));
                Log.d("date",String.valueOf(monthOfYear));
                Log.d("date",String.valueOf(dayOfMonth));
                frontYear = year;
                frontMonth = monthOfYear+1;
                frontDay = dayOfMonth;
                frontDate = frontYear*10000 + frontMonth*100 + frontDay;

                if(frontDate<rearDate){
                    textViewfront.setText(String.valueOf(frontYear) + "/ " + String.valueOf(frontMonth) + "/ " + String.valueOf(frontDay));
                }else{
                    Toast.makeText(this, "誤った日付が設定されています。", Toast.LENGTH_LONG).show();
                }

                break;
            case 2:
                Log.d("date",String.valueOf(year));
                Log.d("date",String.valueOf(monthOfYear));
                Log.d("date",String.valueOf(dayOfMonth));
                rearYear = year;
                rearMonth = monthOfYear+1;
                rearDay = dayOfMonth;
                rearDate = rearYear*10000 + rearMonth*100 + rearDay;

                if(frontDate<rearDate){
                    textViewrear.setText(String.valueOf(rearYear) + "/ " + String.valueOf(rearMonth) + "/ " + String.valueOf(rearDay));
                }else{
                    Toast.makeText(this, "誤った日付が設定されています。", Toast.LENGTH_LONG).show();
                }

                break;
        }

        //yearV[flag]=year;
    }

    //ボタン処理
    @Override
    public void onClick(View view) {

        //まとめタイトルをDatabaseに保存
        saveMatomeTitle();

        //まとめ期間をDatabaseに保存
        saveMatomeTime();

        //ListViewチェックボックスで選択されているものを配列に代入
        SparseBooleanArray array = listView.getCheckedItemPositions();
        for(int i = 0; i<array.size(); i++){
            int at = array.keyAt(i);
            if (array.get(at)) {
                Log.d("example", "選択されている項目:" + listView.getItemAtPosition(at).toString()); //選択されているListの文字列を取得
                Log.d("example", "そのキー" + at);  //選択されているListの要素数を取得
            }
        }

        finish();
    }

    //DatabaseからTagの一覧を取得
    public void getTagDataList(){
        RealmQuery<RealmWordEntity> wordQuery = realm.where(RealmWordEntity.class);

        RealmResults<RealmWordEntity> wordResults = wordQuery.findAll();
        //取ってきたデータ全部欲しい時はこんな感じに
        for(int i=0;i<wordResults.size();i++){

            wordlist.add(wordResults.get(i));
            //arrayAdapterにwordlistを入れる
            arrayAdapter.add(wordlist.get(i).getTagName());
            //arrayAdapterをlistViewに入れる
            listView.setAdapter(arrayAdapter);

        }
    }

    //まとめタイトルをDatabaseに保存
    public void saveMatomeTitle(){

        realm.beginTransaction();
        RealmMatomeEntity model = realm.createObject(RealmMatomeEntity.class);

        if(edittext.length() !=0) {
            model.setMatomeName(edittext.getText().toString());

        }else{
            model.setMatomeName("タイトル未設定");
        }

        realm.commitTransaction();
    }

    //まとめ期間開始日をDatabaseに保存
    public void saveMatomeTime(){

        realm.beginTransaction();

        //まとめ期間開始日をDatabaseに保存
        RealmMatomeEntity frontModel = realm.createObject(RealmMatomeEntity.class);
        frontModel.setStartDate(frontDate);

        //まとめ期間終了日をDatabaseに保存
        RealmMatomeEntity rearModel = realm.createObject(RealmMatomeEntity.class);
        rearModel.setEndDate(rearDate);

        realm.commitTransaction();
    }

    private void saveMemoCheckedDialog(){

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
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options,menu);  //res\menu\optionsのlayoutを読み込む
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

                saveMemoCheckedDialog();
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

}
