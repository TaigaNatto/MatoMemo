package com.example.t_robop.matomemo;

/**
 * Created by daipc on 17/08/27.
 */

public class CardMatomeData {
    private String mMatomeTitle;
    private String mMatomeStartDate;
    private String mMatomeEndDate;
    private String mMatomeTagName;

    public CardMatomeData(String matomeTitle, String matomeStartDate, String matomeEndDate, String matomeTagName) {
        this.mMatomeTitle = matomeTitle;
        this.mMatomeStartDate = matomeStartDate;
        this.mMatomeEndDate = matomeEndDate;
        this.mMatomeTagName = matomeTagName;
    }

    public String getMatomeTitle(){
        return mMatomeTitle;
    }

    public String getMatomeStartDate(){
        return mMatomeStartDate;
    }

    public String getMatomeEndData(){
        return mMatomeEndDate;
    }

    public String getMatomeTagName(){
        return mMatomeTagName;
    }
}
