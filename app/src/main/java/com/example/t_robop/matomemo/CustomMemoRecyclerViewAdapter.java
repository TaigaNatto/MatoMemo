package com.example.t_robop.matomemo;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.t_robop.matomemo.Main.MemoFragment;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static android.support.v4.content.ContextCompat.startActivity;


public class CustomMemoRecyclerViewAdapter extends RecyclerView.Adapter<CustomMemoRecyclerViewAdapter.CustomMemoRecyclerViewHolder> {

    private String[] mMemoDates;
    private String[] mMemoTimes;
    private String[] mMemoTexts;

    MemoFragment memoFragment;

    Realm realm;

    public static class CustomMemoRecyclerViewHolder extends RecyclerView.ViewHolder {
        public CardView memoCardView;
        public TextView memoDateText;
        public TextView memoTimeText;
        public TextView memoText;

        public CustomMemoRecyclerViewHolder(View view) {
            super(view);

            memoCardView = (CardView) view.findViewById(R.id.memo_card_view);
            memoDateText = (TextView) view.findViewById(R.id.memo_date);
            memoTimeText = (TextView) view.findViewById(R.id.memo_time);
            memoText = (TextView) view.findViewById(R.id.memo_text);
        }
    }

    //メモ用コンストラクタ
    public CustomMemoRecyclerViewAdapter(String[] memoDates, String[] memoTimes, String[] memoTexts) {
        mMemoDates = memoDates;
        mMemoTimes = memoTimes;
        mMemoTexts = memoTexts;
    }

    public CustomMemoRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_cardview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        CustomMemoRecyclerViewHolder customMemoRecyclerViewHolder = new CustomMemoRecyclerViewHolder(view);
        return customMemoRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomMemoRecyclerViewHolder holder, final int position) {
        holder.memoDateText.setText(mMemoDates[position]);
        holder.memoTimeText.setText(mMemoTimes[position]);
        holder.memoText.setText(mMemoTexts[position]);

        holder.memoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WritingActivity.class);
                intent.putExtra("MODE", memoFragment.idList.get(position));    //メモリストのposition値渡し
                intent.putExtra("SUBJECT NAME", memoFragment.nowSubjectName);     //教科名受け渡し
                startActivity(view.getContext(), intent, null);
            }
        });

        /*
        holder.memoCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                Realm.init(view.getContext());
                realm = Realm.getDefaultInstance();

                //final String clickedMemoListItem = (String) view.getContext().getItemAtPosition(position);   //クリックしたpositionから文字列を取得

                //消去確認のダイアログ
                AlertDialog.Builder alertDig = new AlertDialog.Builder(view.getContext());
                alertDig.setTitle("");
                alertDig.setMessage("メモを消去しますか？");

                alertDig.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //No処理
                    }
                });

                alertDig.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //YES処理
                        //adapterMemo.remove(clickedMemoListItem);   //カードリストから削除
                        holder.memoCardView.removeView(view);
                        notifyItemRemoved(position);

                        removeMemoData(mMemoTexts[position]);   //データベースから削除
                    }
                });

                alertDig.create().show();

                return true;
            }
        });
        */
    }

    @Override
    public int getItemCount() {
        return mMemoTexts.length;
    }

    //選択されたItemをデータベースから削除
    //ToDo このままだとメモタイトル同名のものが全てデータベースから消えてしまう
    public void removeMemoData(String selectedItem) {
        // クエリを発行
        RealmQuery<RealmMemoEntity> delQuery = realm.where(RealmMemoEntity.class);
        //消したいデータを指定
        delQuery.equalTo("memo", selectedItem);      //メモ内容とListの文字列を比較      //ToDo List内の表示を決めないとequalToでヒットしないので、レイアウト決めよう

        //指定されたデータを持つデータのみに絞り込む
        final RealmResults<RealmMemoEntity> delR = delQuery.findAll();
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
