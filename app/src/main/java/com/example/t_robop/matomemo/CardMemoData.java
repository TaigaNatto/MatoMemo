package com.example.t_robop.matomemo;

/**
 * Created by daipc on 17/08/26.
 */

public class CardMemoData {
    private String mMemoText;
    private String mMemoTime;
    private String mMemoDate;

    public CardMemoData(String memoText, String memoTime, String memoDate){
        this.mMemoText = memoText;
        this.mMemoTime = memoTime;
        this.mMemoDate = memoDate;
    }

    public String getMemoText(){
        return mMemoText;
    }

    public String getMemoTime(){
        return mMemoTime;
    }

    public String getMemoDate(){
        return mMemoDate;
    }
}
