package com.example.t_robop.matomemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static android.support.v4.content.ContextCompat.startActivity;


public class CustomMatomeRecyclerViewAdapter extends RecyclerView.Adapter<CustomMatomeRecyclerViewAdapter.CustomMatomeRecyclerViewHolder> {

    private List<CardMatomeData> matomeDatas;
    private List<Integer> IdLists;

    //MatomeFragment matomeFragment;

    Realm realm;

    public static class CustomMatomeRecyclerViewHolder extends RecyclerView.ViewHolder {

        public CardView matomeCardView;
        public TextView matomeTitleText;
        public TextView matomeStartDateText;
        public TextView matomeEndDateText;
        public TextView matomeTagNameText;

        public CustomMatomeRecyclerViewHolder(View view) {
            super(view);

            matomeCardView = (CardView)view.findViewById(R.id.matome_card_view);
            matomeTitleText = (TextView)view.findViewById(R.id.matome_title);
            matomeStartDateText = (TextView)view.findViewById(R.id.matome_start_date);
            matomeEndDateText = (TextView)view.findViewById(R.id.matome_end_date);
            matomeTagNameText = (TextView)view.findViewById(R.id.matome_tag_name);
        }
    }

    //まとめ用コンストラクタ
    public CustomMatomeRecyclerViewAdapter(List<CardMatomeData> matomeDatas, List<Integer> idLists) {
        this.matomeDatas = matomeDatas;
        this.IdLists = idLists;
    }

    @Override
    public CustomMatomeRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.matome_cardview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        CustomMatomeRecyclerViewHolder customMatomeRecyclerViewHolder = new CustomMatomeRecyclerViewHolder(view);
        return customMatomeRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomMatomeRecyclerViewHolder holder, final int position) {
        holder.matomeTitleText.setText(matomeDatas.get(position).getMatomeTitle());
        holder.matomeStartDateText.setText(matomeDatas.get(position).getMatomeTagName());
        holder.matomeEndDateText.setText(matomeDatas.get(position).getMatomeEndData());
        holder.matomeTagNameText.setText(matomeDatas.get(position).getMatomeTagName());

        holder.matomeCardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MatomeActivity.class);     //MatomeActivityへIntent
                //idの指定
                intent.putExtra("ID", IdLists.get(position));
                startActivity(view.getContext(),intent,null);
            }
        });


        holder.matomeCardView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(final View view) {

                Realm.init(view.getContext());
                realm = Realm.getDefaultInstance();

                //消去確認のダイアログ
                AlertDialog.Builder alertDig = new AlertDialog.Builder(view.getContext());
                alertDig.setTitle("");
                alertDig.setMessage("まとめを消去しますか？");

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
                        matomeDatas.remove(position);   //カードリストから削除
                        notifyItemRemoved(position);

                        removeMatomeData(IdLists.get(position));   //データベースから削除

                    }
                });

                alertDig.create().show();

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null !=matomeDatas?matomeDatas.size():0);
    }


    //選択されたItemをデータベースから削除
    public void removeMatomeData(int selectedItemId) {

        RealmQuery<RealmMatomeEntity> delQuery = realm.where(RealmMatomeEntity.class);
        //消したいデータを指定
        delQuery.equalTo("id", selectedItemId);

        final RealmResults<RealmMatomeEntity> delR = delQuery.findAll();     //まとめフォルダの名前と一致したものを削除

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                delR.deleteAllFromRealm();
            }
        });

    }

}
