package com.example.t_robop.matomemo;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by taiga on 2017/07/12.
 */

public class MatomeAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<MatomeObject> matomeList;

    Realm realm;

    //コンテキスト
    public MatomeAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Realmの初期化
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    public void setMatomeList(ArrayList<MatomeObject> matomeList) {
        this.matomeList = matomeList;
    }

    @Override
    public int getCount() {
        return matomeList.size();
    }

    @Override
    public Object getItem(int position) {
        return matomeList.get(position);
    }

    //クソ
    @Override
    public long getItemId(int position) {
        return position;
    }

    //どうやらこいつが全てを司ってるらしい
    //スクロールの度に呼ばれて表示するviewを決定してるんだとか
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //レイアウトの読み込み
        convertView = layoutInflater.inflate(R.layout.matome_text,parent,false);

        //メモ全文の取得
        String txtStr =this.matomeList.get(position).getMemo();
        //メモに登録されてる単語の数だけ回す
        for(int i=0;i<matomeList.get(position).getMarckWords().size();i++) {
            //単語取得
            String word=this.matomeList.get(position).getMarckWords().get(i).getWord();
            //タグ取得
            String tagName=this.matomeList.get(position).getMarckWords().get(i).getTagName();
            //色変える時はこの辺に書いて
            //検索用のクエリ作成
            RealmQuery<RealmWordEntity> wordQuery = realm.where(RealmWordEntity.class);
            wordQuery.equalTo("tagName",tagName);
            //インスタンス生成し、その中にすべてのデータを入れる 今回なら全てのデータ
            RealmResults<RealmWordEntity> wordResults = wordQuery.findAll();
            //取得した単語の部分にマーカーを付与
            WritingActivity wa=new WritingActivity();
            txtStr = wa.giveMark(txtStr,word,wordResults.get(0).getColor());
        }

        //表示するレイアウトの決定
        ((TextView)convertView.findViewById(R.id.memo_text)).setText(HtmlCompat.fromHtml(txtStr));

        return convertView;
    }



}
