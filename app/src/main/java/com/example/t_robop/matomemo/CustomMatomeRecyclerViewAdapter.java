package com.example.t_robop.matomemo;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;

import static android.support.v4.content.ContextCompat.startActivity;


public class CustomMatomeRecyclerViewAdapter extends RecyclerView.Adapter<CustomMatomeRecyclerViewAdapter.CustomMatomeRecyclerViewHolder> {

    private String[] mMatomeTitles;
    private String[] mMatomeStartDates;
    private String[] mMatomeEndDates;
    private String[] mMatomeTagNames;

    MatomeFragment matomeFragment;

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
    public CustomMatomeRecyclerViewAdapter(String[] matomeTitles, String[] matomeStartDate, String[] matomeEndDate, String[] matomeTagNames) {
        mMatomeTitles = matomeTitles;
        mMatomeStartDates = matomeStartDate;
        mMatomeEndDates = matomeEndDate;
        mMatomeTagNames = matomeTagNames;
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
    public void onBindViewHolder(CustomMatomeRecyclerViewHolder holder, final int position) {
        holder.matomeTitleText.setText(mMatomeTitles[holder.getLayoutPosition()]);
        holder.matomeStartDateText.setText(mMatomeStartDates[holder.getLayoutPosition()]);
        holder.matomeEndDateText.setText(mMatomeEndDates[holder.getLayoutPosition()]);
        holder.matomeTagNameText.setText(mMatomeTagNames[holder.getLayoutPosition()]);

        holder.matomeCardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MatomeActivity.class);     //MatomeActivityへIntent
                //idの指定
                intent.putExtra("ID", matomeFragment.idList.get(position));
                startActivity(view.getContext(),intent,null);
            }
        });

        holder.matomeCardView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                /*
                final String clickedMatomeItem = (String) view.getContext().getItemAtPosition(position);   //クリックしたpositionからItemを取得

                //消去確認のダイアログ
                AlertDialog.Builder alertDig = new AlertDialog.Builder(getActivity());
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
                        adapterMatome.remove(clickedMatomeItem);   //リストから削除

                        removeMatomeData(clickedMatomeItem);   //データベースから削除
                    }
                });

                alertDig.create().show();
                */
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMatomeTitles.length;
    }

    /*
    //選択されたItemをデータベースから削除
    public void removeMatomeData(String selectedItem) {

        //realm.beginTransaction();

        RealmQuery<RealmMatomeEntity> delQuery = realm.where(RealmMatomeEntity.class);
        //消したいデータを指定
        //delQuery.equalTo("folder", selectedItem);

        final RealmResults<RealmMatomeEntity> delR = delQuery.equalTo("matomeName", selectedItem).findAll();     //まとめフォルダの名前と一致したものを削除

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                delR.deleteAllFromRealm();
            }
        });

        //realm.commitTransaction();
    }
    */
}
