package com.example.t_robop.matomemo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by taiga on 2017/08/02.
 */

public class TagListItemAdapter extends ArrayAdapter<RealmWordEntity> {
    private int mResource;
    private List<RealmWordEntity> mItems;
    private LayoutInflater mInflater;

    /**
     * コンストラクタ
     * @param context コンテキスト
     * @param resource リソースID
     * @param items リストビューの要素
     */
    public TagListItemAdapter(Context context, int resource, List<RealmWordEntity> items) {
        super(context, resource, items);

        mResource = resource;
        mItems = items;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        }
        else {
            view = mInflater.inflate(mResource, null);
        }

        // リストビューに表示する要素を取得
        RealmWordEntity item = mItems.get(position);

        //tag名の設定
        TextView text = (TextView)view.findViewById(R.id.tag_item_text);
        text.setText(item.getTagName());
        //色の設定
        TextView color = (TextView)view.findViewById(R.id.tag_item_color);
        color.setBackgroundColor(Color.parseColor(item.getColor()));

        return view;
    }
}
