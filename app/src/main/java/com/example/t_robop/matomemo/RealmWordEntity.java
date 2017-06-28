package com.example.t_robop.matomemo;

import io.realm.RealmObject;

/**
 * Created by taiga on 2017/06/27.
 */

//単語保存用
public class RealmWordEntity extends RealmObject {

    private String wordName;//☆の場合はここに☆入れて
    private String color;//マーカーカラー
    private String tagName;//タグ付ける場合はこいつ使って

    public void setWordName(String wordName){
        this.wordName=wordName;
    }
    public String getWordName(){
        return this.wordName;
    }

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

}
