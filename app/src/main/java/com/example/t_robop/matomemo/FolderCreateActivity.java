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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

    private TextView textViewfront;
    private TextView textViewrear;
    EditText edittext;
    ListView listView;
    DatePick newFragment;
    DatePick secondFragment;
    int flag=0;

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

        newFragment = new DatePick(2017,1,1);
        secondFragment=new DatePick(2017,1,1);


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



       // ((FolderCreateActivity)getActivity()).getSupportActionBar().

        /***realmの初期化***/
        /***これ必須だからみんな書いて***/
        Realm.init(this);
        realm = Realm.getDefaultInstance();


        wordlist=new ArrayList<>();


        arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_multiple_choice);

        //DatabaseからTagの一覧を取得
        /****単語のデータが欲しいとき！**/
        //検索用のクエリ作成
        RealmQuery<RealmWordEntity> wordQuery = realm.where(RealmWordEntity.class);
        //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
        RealmResults<RealmWordEntity> wordResults = wordQuery.findAll();
        //取ってきたデータ全部欲しい時はこんな感じに
        for(int i=0;i<wordResults.size();i++){

            wordlist.add(wordResults.get(i));
            //arrayAdapterにwordlistを入れる
            arrayAdapter.add(wordlist.get(i).getTagName());
            //arrayAdapterをlistViewに入れる
            listView.setAdapter(arrayAdapter);

        }

         Button btn = (Button)findViewById(R.id.createbutton);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(edittext.length() !=0) {

                    //トランザクション開始
                    realm.beginTransaction();
                    //インスタンスを生成
                    RealmFolderEntity model = realm.createObject(RealmFolderEntity.class);
                    //書き込みたいデータをインスタンスに入れる
                    model.setFolderName(edittext.getText().toString());
                    //トランザクション終了 (データを書き込む)
                    realm.commitTransaction();

                }else{
                    //トランザクション開始
                    realm.beginTransaction();
                    //インスタンスを生成
                    RealmFolderEntity model = realm.createObject(RealmFolderEntity.class);
                    //書き込みたいデータをインスタンスに入れる
                    model.setFolderName("タイトル未設定");
                    //トランザクション終了 (データを書き込む)
                    realm.commitTransaction();
                }


                //日付の保存
                //トランザクション開始
                realm.beginTransaction();
                //インスタンスを生成
                RealmMatomeEntity model = realm.createObject(RealmMatomeEntity.class);
                //書き込みたいデータをインスタンスに入れる
                model.setStartDate(frontDate);
                //トランザクション終了 (データを書き込む)
                realm.commitTransaction();


                //トランザクション開始
                realm.beginTransaction();
                //インスタンスを生成
                RealmMatomeEntity rearmodel = realm.createObject(RealmMatomeEntity.class);
                //書き込みたいデータをインスタンスに入れる
                rearmodel.setEndDate(rearDate);
                //トランザクション終了 (データを書き込む)
                realm.commitTransaction();

               finish();
            }
        });
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

    public void showDatePickerDialog(View v) {
        switch (v.getId()) {
            case R.id.button1:
                newFragment = new DatePick(frontYear,frontMonth,frontDay);
                newFragment.show(getSupportFragmentManager(), "datePicker");
                flag=1;
                break;

            case R.id.button2:
                secondFragment = new DatePick(rearYear,rearMonth,rearDay);
                secondFragment.show(getSupportFragmentManager(), "datePicker");
                flag=2;
                break;
        }
    }

    //メニューバーの作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options,menu);  //res\menu\optionsのlayoutを読み込む
        return true;
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

}
