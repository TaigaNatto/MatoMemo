package com.example.t_robop.matomemo;

import io.realm.RealmObject;

/**
 * Created by taiga on 2017/07/17.
 */

public class MatomeWord extends RealmObject {

    private String word;

    public String getWord(){
        return this.word;
    }
    public void setWord(String word){
        this.word=word;
    }

}
