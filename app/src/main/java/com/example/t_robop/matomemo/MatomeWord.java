package com.example.t_robop.matomemo;

import io.realm.RealmObject;

/**
 * Created by taiga on 2017/07/17.
 */

public class MatomeWord extends RealmObject {

    private String word;
    private String tagName;

    public String getWord(){
        return this.word;
    }
    public void setWord(String word){
        this.word=word;
    }

    public String getTagName(){
        return this.tagName;
    }
    public void setTagName(String tagName){
        this.tagName=tagName;
    }

}
