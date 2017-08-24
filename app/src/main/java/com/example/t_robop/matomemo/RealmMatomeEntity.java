package com.example.t_robop.matomemo;

import android.os.storage.StorageManager;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by taiga on 2017/07/12.
 */

public class RealmMatomeEntity extends RealmObject {

    //id
    private int id;
    //まとめの名前
    private String matomeName;
    //まとめる日付　無ければ-1とかで
    private int startDate=-1;
    private int endDate=-1;
    //教科名
    private String folder;
    private int folderId=-1;
    //まとめ設定で指定された単語リスト(単語一覧参照先のid)
    public RealmList<MatomeWord> words;
    //まとめ設定でしていされた

    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id=id;
    }

    public String getMatomeName(){
        return this.matomeName;
    }
    public void setMatomeName(String matomeName){
        this.matomeName=matomeName;
    }

    public String getFolder(){
        return this.folder;
    }
    public void setFolder(String folder){
        this.folder=folder;
    }

    public int getStartDate(){
        return this.startDate;
    }
    public void setStartDate(int startDate){
        this.startDate=startDate;
    }

    public int getEndDate(){
        return this.endDate;
    }
    public void setEndDate(int endDate){
        this.endDate=endDate;
    }

    public void setFolderId(int folderId){
        this.folderId=folderId;
    }
    public int getFolderId(){
        return this.folderId;
    }

    public RealmList<MatomeWord> getWords(){
        return this.words;
    }
    public void setWords(RealmList<MatomeWord> words){
        this.words=words;
    }

}
