package com.example.t_robop.matomemo;

import java.util.ArrayList;

/**
 * Created by taiga on 2017/07/12.
 */

//まとめ画面に表示するデータ用class
public class MatomeObject {

    String memo;
    ArrayList<String> marckWords;

    public String getMemo(){
        return this.memo;
    }
    public void setMemo(String memo){
        this.memo=memo;
    }

    public ArrayList<String> getMarckWords(){
        return this.marckWords;
    }
    public void setMarckWords(ArrayList<String> marckWords){
        this.marckWords=marckWords;
    }

}
