package com.example.t_robop.matomemo;

import io.realm.RealmObject;

/**
 * Created by taiga on 2017/06/27.
 */

//単語保存用
public class RealmWordEntity extends RealmObject {

    private String color;//マーカーカラー
    private String tagName;//タグ付ける場合はこいつ使って
    private int tagNum;

    public void setColor(String color){
        this.color=color;
    }
    public String getColor(){
        return this.color;
    }

    public void setTagName(String tagName){
        this.tagName=tagName;
    }
    public String getTagName(){
        return this.tagName;
    }

    public void setTagNum(int num){
        this.tagNum=num;
    }
    public int getTagNum(){
        return this.tagNum;
    }

}
